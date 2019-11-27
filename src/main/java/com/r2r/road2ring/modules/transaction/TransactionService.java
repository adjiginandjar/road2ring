package com.r2r.road2ring.modules.transaction;

import com.midtrans.httpclient.error.MidtransError;
import com.r2r.road2ring.modules.common.InvalidOrderException;
import com.r2r.road2ring.modules.common.PaymentStatus;
import com.r2r.road2ring.modules.common.R2rTools;
import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.common.TripStatus;
import com.r2r.road2ring.modules.confirmation.Confirmation;
import com.r2r.road2ring.modules.confirmation.ConfirmationService;
import com.r2r.road2ring.modules.consumer.Consumer;
import com.r2r.road2ring.modules.mail.MailClient;
import com.r2r.road2ring.modules.midtrans.MidtransService;
import com.r2r.road2ring.modules.transactionlog.TransactionCreator;
import com.r2r.road2ring.modules.transactionlog.TransactionLog;
import com.r2r.road2ring.modules.transactionlog.TransactionLogService;
import com.r2r.road2ring.modules.trip.TripPrice;
import com.r2r.road2ring.modules.trip.TripPriceRepository;
import com.r2r.road2ring.modules.trip.TripPriceService;
import com.r2r.road2ring.modules.trip.TripPriceStatus;
import com.r2r.road2ring.modules.trip.TripRepository;
import com.r2r.road2ring.modules.trip.TripService;
import com.r2r.road2ring.modules.user.User;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  TransactionRepository transactionRepository;

  R2rTools r2rTools;

  TripPriceService tripPriceService;

  TransactionDetailService transactionDetailService;

  TransactionViewService transactionViewService;

  TransactionDetailRepository transactionDetailRepository;

  TransactionLogService transactionLogService;

  TripRepository tripRepository;

  ConfirmationService confirmationService;

  MailClient mailClient;

  TripPriceRepository tripPriceRepository;

  @Autowired
  TripService tripService;

  @Autowired
  MidtransService midtransService;

  @Autowired
  public void setTransactionRepository(TransactionRepository transactionRepository){
    this.transactionRepository = transactionRepository;
  }

  @Autowired
  public void setR2rTools(R2rTools r2rTools){
    this.r2rTools = r2rTools;
  }

  @Autowired
  public void setTripPriceService(TripPriceService tripPriceService){
    this.tripPriceService = tripPriceService;
  }

  @Autowired
  public void setTransactionDetailService(TransactionDetailService transactionDetailService){
    this.transactionDetailService = transactionDetailService;
  }

  @Autowired
  public void setTransactionViewService(TransactionViewService transactionViewService){
    this.transactionViewService = transactionViewService;
  }

  @Autowired
  public void setTransactionDetailRepository(TransactionDetailRepository transactionDetailRepository){
    this.transactionDetailRepository = transactionDetailRepository;
  }

  @Autowired
  public void setTransactionLogService(TransactionLogService transactionLogService){
    this.transactionLogService = transactionLogService;
  }

  @Autowired
  public void setTripRepository(TripRepository tripRepository){
    this.tripRepository = tripRepository;
  }

  @Autowired
  public void setConfirmationService(
      ConfirmationService confirmationService) {
    this.confirmationService = confirmationService;
  }

  @Autowired
  public void setMailClient(MailClient mailClient){
    this.mailClient = mailClient;
  }

  @Autowired
  public void setTripPriceRepository(TripPriceRepository tripPriceRepository){
    this.tripPriceRepository = tripPriceRepository;
  }

  @Transactional
  public TransactionCreateView createTransaction(Transaction transaction, User user)
      throws Road2RingException, MidtransError {

    Transaction result = new Transaction();
    TripPrice tripPrice = null;
    Date created  = new Date();
    String midtransToken ="";

    Calendar cal = Calendar.getInstance();
    cal.setTime(created);
    cal.add(Calendar.HOUR_OF_DAY, 1);
    Date newDate = cal.getTime();

    Timestamp code = new Timestamp(created.getTime());

    Date startDate = new Date(transaction.getStartTimestamp());

    Integer tripPersonPaid = tripPriceService.getTripPrice(transaction.getTrip().getId(), startDate).getPersonPaid();
    Integer tripMaxRider = tripRepository.findOne(transaction.getTrip().getId()).getMaxRider();

    if(tripPersonPaid < tripMaxRider) {
      result.setUser(user);
      result.setPaymentStatus(PaymentStatus.WAITING);
      result.setTrip(transaction.getTrip());
      result.setCreated(created);
      result.setExpiredPaymentDate(newDate);
      result.setTripStatus(TripStatus.READY);
      result.setNotes(transaction.getNotes());
      result.setPrice(transaction.getPrice());
      result.setStartDate(startDate);
      result.setCode(
          r2rTools.generateRandomCode(2) + code.getTime() + r2rTools.generateRandomCode(3));
      result.setUpdated(created);
      result.setCreatedBy(user.getEmail());
      result.setUpdatedBy(user.getEmail());
      result.setTransactionCreator(TransactionCreator.USER);

      if(transaction.getBringOwnHelm() == null){
        transaction.setBringOwnHelm(false);
      }
      if(transaction.getBringOwnMotor() == null){
        transaction.setBringOwnMotor(false);
      }
      if (transactionRepository.save(result) != null) {
        tripPrice = tripPriceService.addPersonTripPrice(transaction.getTrip().getId(), result.getStartDate());
        Transaction transactionSaved = transactionRepository.findOneByCode(result.getCode());
        TransactionDetail detailMotor = transactionDetailService.saveMotor(transaction.getMotor(),
            transactionSaved, transaction.getBringOwnMotor());
        List<TransactionDetail> detailAccessories = transactionDetailService
            .saveListTransactionalAccessory(transaction.getAccessories(), transactionSaved,
                transaction.getBringOwnHelm());

        midtransToken = midtransService.checkoutTrip(detailAccessories,detailMotor,transactionSaved,tripPrice,user);
        this.createTransactionLogByUser(transactionSaved);
      }

      TransactionCreateView view = new TransactionCreateView();
      view.setLastPayment(newDate);
      view.setTotalPrice(result.getPrice());
      view.setTransactionCodeId(result.getCode());
      view.setMidtransToken(midtransToken);

      /*CREATE EMAIL DATA INVOICE*/

      int index = result.getUser().getEmail().indexOf('@');
      String username = result.getUser().getEmail().substring(0,index);

      mailClient.sendCheckoutEmail(result.getUser().getEmail(),username,result,
          transaction.getMotor(), transaction.getAccessories(), tripPrice,
          transaction.getBringOwnHelm(), transaction.getBringOwnMotor());
      return view;
    } else {
      throw new Road2RingException("CAN NOT CREATE TRANSACTION, ALREADY FULL", 705);
    }

  }

  @Async
  private void createTransactionLogByUser(Transaction transaction){
    TransactionLog saved = new TransactionLog();
    saved.setUsername(transaction.getUser().getEmail());
    saved.setAction("CREATED TRANSACTION BY USER");
    saved.setTransactionId(transaction.getId());
    saved.setCreator(TransactionCreator.USER);
    transactionLogService.setTransactionLog(saved);
  }

  @Async
  private void createTransactionLogBookedByUser(Transaction transaction){
    TransactionLog saved = new TransactionLog();
    saved.setUsername(transaction.getUser().getEmail());
    saved.setAction("TRANSACTION BOOKED BY USER");
    saved.setTransactionId(transaction.getId());
    saved.setCreator(TransactionCreator.USER);
    transactionLogService.setTransactionLog(saved);
  }

  @Async
  private void createTransactionLogBySystem(Transaction transaction){
    TransactionLog saved = new TransactionLog();
    saved.setUsername(transaction.getUser().getEmail());
    saved.setAction("CHANGE STATUS TRANSACTION TO FAILED");
    saved.setTransactionId(transaction.getId());
    saved.setCreator(TransactionCreator.SYSTEM);
    transactionLogService.setTransactionLog(saved);
  }

  @Async
  private void acceptTransactionPayment(Transaction transaction, Consumer consumer){
    TransactionLog saved = new TransactionLog();
    saved.setUsername(consumer.getEmail());
    saved.setTransactionId(transaction.getId());
    saved.setCreator(TransactionCreator.ADMIN);
    saved.setAction("ACCEPT PAYMENT BY ADMIN");
    transactionLogService.setTransactionLog(saved);
  }

  @Async
  private void cancelTransactionPayment(Transaction transaction, Consumer consumer){
    TransactionLog saved = new TransactionLog();
    saved.setUsername(consumer.getEmail());
    saved.setTransactionId(transaction.getId());
    saved.setCreator(TransactionCreator.ADMIN);
    saved.setAction("CANCEL PAYMENT BY ADMIN");
    transactionLogService.setTransactionLog(saved);
  }

  @Async
  private void failedTransactionPayment(Transaction transaction, Consumer consumer){
    TransactionLog saved = new TransactionLog();
    saved.setUsername(consumer.getEmail());
    saved.setTransactionId(transaction.getId());
    saved.setCreator(TransactionCreator.ADMIN);
    saved.setAction("FAILED PAYMENT BY ADMIN");
    transactionLogService.setTransactionLog(saved);
  }

  @Transactional
  public void acceptPayment(Transaction transaction, Consumer consumer){
    Transaction saved  = transactionRepository.findOneByCode(transaction.getCode());
    saved.setPaymentStatus(PaymentStatus.PAID);
    saved.setCompletePaymentDate(new Date());
    saved.setUpdatedBy(consumer.getEmail());
    saved.setUpdated(new Date());
    saved.setTransactionCreator(TransactionCreator.ADMIN);
    this.acceptTransactionPayment(saved, consumer);

    int index = saved.getUser().getEmail().indexOf('@');
    String username = saved.getUser().getEmail().substring(0,index);
    try {
      /*change email recipient*/
      mailClient.sendPaidEmail(saved.getUser().getEmail(),username,
          this.getRidersNeeded(saved, saved.getStartDate()));
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    transactionRepository.save(saved);

    /*Check All Paid User*/
    this.checkPaidUser(saved);
  }

  @Transactional
  public void checkPaidUser(Transaction transaction){
    int maxRider = transaction.getTrip().getMaxRider();
    TripPrice tripPrice = tripPriceService.getTripPrice(transaction.getTrip().getId(),transaction.getStartDate());
    List<Transaction> transactions = this.findPaidTransaction(transaction.getStartDate());
    if(maxRider == transactions.size()){
      tripPrice.setStatus(TripPriceStatus.COMPLETE);
      tripPriceRepository.save(tripPrice);

      /*SENT EMAIL*/
      for(Transaction result : transactions){
        mailClient.sentEmailCompleteTrip(result.getUser().getEmail(),
            result.getTrip().getMeetingPoint());
      }
    }
  }

  @Transactional
  public void cancelPayment(Transaction transaction, Consumer consumer){
    Transaction saved  = transactionRepository.findOneByCode(transaction.getCode());
    saved.setPaymentStatus(PaymentStatus.CANCEL);
    saved.setCompletePaymentDate(new Date());
    saved.setUpdated(new Date());
    saved.setUpdatedBy(consumer.getEmail());
    saved.setTransactionCreator(TransactionCreator.ADMIN);
    this.cancelTransactionPayment(saved, consumer);
    transactionRepository.save(saved);
  }

  @Transactional
  public void failedPayment(Transaction transaction, Consumer consumer){
    Transaction saved  = transactionRepository.findOneByCode(transaction.getCode());
    saved.setPaymentStatus(PaymentStatus.FAILED);
    saved.setCompletePaymentDate(new Date());
    saved.setUpdated(new Date());
    saved.setUpdatedBy(consumer.getEmail());
    saved.setTransactionCreator(TransactionCreator.ADMIN);
    transactionRepository.save(saved);
    tripPriceService.minPersonTripPrice(transaction.getTrip().getId(), transaction.getStartDate());
    this.failedTransactionPayment(saved, consumer);
  }


  public void changeStatusLatePayment(){
    List<Transaction> transactions = transactionRepository.
        findAllByPaymentStatusAndExpiredPaymentDateLessThan(PaymentStatus.WAITING, new Date());
    for(Transaction transaction : transactions){
      transaction.setPaymentStatus(PaymentStatus.FAILED);
      transaction.setUpdatedBy(TransactionCreator.SYSTEM.name());
      transaction.setTransactionCreator(TransactionCreator.SYSTEM);
      transactionRepository.save(transaction);
      tripPriceService.minPersonTripPrice(transaction.getTrip().getId(), transaction.getStartDate());
      this.createTransactionLogBySystem(transaction);
    }
  }

  public List<TransactionView> getAllMyTransaction(User user, int pageId, int limit)
      throws Road2RingException {
    Pageable pageable = new PageRequest(pageId, limit);
    List<Transaction> transactions = transactionRepository.findAllByUserIdOrderByCreatedDesc(user.getId(),pageable);
    List<TransactionView> result;
    if(transactions.size() != 0) {
      result = transactionViewService.bindListTransactionView(transactions);
    } else {
      throw new Road2RingException("you have 0 transaction yet", 200);
    }
    return result;
  }

  public TransactionalDetailView getMyTransactionDetail(User user, int transactionId){
    TransactionalDetailView result;
    Transaction transaction = transactionRepository.findOneByIdAndUserId(transactionId, user.getId());
    List<TransactionDetail> transactionDetails = transactionDetailRepository.findAllByTransactionIdOrderByIdDesc(transactionId);
    result = transactionViewService.bindTransactionDetail(transaction,transactionDetails);
    return result;
  }

  public TransactionalDetailView getTransactionDetailByCode(String transactionCodeId){
    TransactionalDetailView result;
    Transaction transaction = transactionRepository.findOneByCode(transactionCodeId);
    List<TransactionDetail> transactionDetails = transactionDetailRepository.findAllByTransactionIdOrderByIdDesc(transaction.getId());
    result = transactionViewService.bindTransactionDetail(transaction,transactionDetails);
    return result;
  }

  public List<Transaction> getAllTranscation(){
    return transactionRepository.findAll();
  }

  public Transaction getTransactionById(int id){
    return transactionRepository.findOne(id);
  }

  public TransactionConfirmationDetailView getTransactionDetailView(int id){
    Transaction trx = this.getTransactionById(id);
    Confirmation confirmation = confirmationService.getByTransactionCode(trx.getCode());
    TransactionConfirmationDetailView saved = new TransactionConfirmationDetailView();
    //transcation info
    saved.setTrxId(trx.getId());
    saved.setCode(trx.getCode());
    saved.setCreated(trx.getCreated());
    saved.setUpdated(trx.getUpdated());
    saved.setCreatedBy(trx.getCreatedBy());
    saved.setUpdatedBy(trx.getUpdatedBy());
    saved.setPaymentStatus(trx.getPaymentStatus());
    saved.setTripStatus(trx.getTripStatus());
    saved.setPrice(trx.getPrice());
    saved.setUser(trx.getUser());
    saved.setStartDate(trx.getStartDate());
    saved.setCompletePaymentDate(trx.getCompletePaymentDate());
    saved.setExpiredPaymentDate(trx.getExpiredPaymentDate());
    saved.setNotes(trx.getNotes());
    saved.setTrip(trx.getTrip());

    //confirmation info
    if(confirmation != null) {
      saved.setConfirmationId(confirmation.getId());
      saved.setPicture(confirmation.getPicture());
      saved.setAccountNumber(confirmation.getAccountNumber());
      saved.setBank(confirmation.getBank());
      saved.setCodeTrx(confirmation.getCodeTransaction());
      saved.setConfirmed(true);
    }else{
      saved.setConfirmed(false);
    }

    return saved;
  }

  /*Change Status Payment by Admin*/
  public void changeStatusPayment(Consumer consumer,PaymentStatus statusId, String transactionCode)
      throws Road2RingException {
    Transaction saved  = transactionRepository.findOneByCode(transactionCode);
    if(statusId == PaymentStatus.PAID){
      this.acceptPayment(saved,consumer);
    } else if(statusId == PaymentStatus.CANCEL){
      this.cancelPayment(saved, consumer);
    } else if(statusId == PaymentStatus.FAILED){
      this.failedPayment(saved,consumer);
    } else {
      throw new Road2RingException("cannot set value", 900);
    }
  }

  public Transaction getTransactionPdf(String transactionCodeId){
    return transactionRepository.findOneByCode(transactionCodeId);
  }

  public List<TransactionDetail> getListTransactionDetailPdf(int transactionId){
    return transactionDetailRepository.findAllByTransactionIdOrderByIdDesc(transactionId);
  }

  private int getRidersNeeded(Transaction transaction, Date startDate){
    TripPrice tripPrice = tripPriceService.getTripPrice(transaction.getTrip().getId(),startDate);
    return (transaction.getTrip().getMaxRider() - tripPrice.getPersonPaid());
  }

  @Transactional
  public Transaction changeStatusPayment(String transactionCode){
    Transaction result = transactionRepository.findOneByCode(transactionCode);
    result.setPaymentStatus(PaymentStatus.BOOKED);
    transactionRepository.save(result);
    this.createTransactionLogBookedByUser(result);
    return result;
  }

  public List<Transaction> findPaidTransaction(Date startDate){
    List<Transaction> result = transactionRepository.findAllByPaymentStatusAndStartDate(PaymentStatus.PAID, startDate);
    return result;
  }

  public List<TransactionView> getDummyData(Integer page, Integer limit) throws Road2RingException {
    List<TransactionView> result = transactionViewService.createDummyMyTransaction(
        tripService.findTripPageablePage(page,limit).getContent());
    if(result.size() == 0){
      throw new Road2RingException("you have 0 transaction yet", 200);
    }
    return result;
  }
}
