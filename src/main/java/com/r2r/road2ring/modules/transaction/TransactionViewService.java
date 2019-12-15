package com.r2r.road2ring.modules.transaction;

import com.r2r.road2ring.modules.common.PaymentStatus;
import com.r2r.road2ring.modules.common.TripStatus;
import com.r2r.road2ring.modules.motor.Motor;
import com.r2r.road2ring.modules.motor.MotorRepository;
import com.r2r.road2ring.modules.trip.Trip;
import com.r2r.road2ring.modules.trip.TripPriceRepository;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionViewService {

  TripPriceRepository tripPriceRepository;

  MotorRepository motorRepository;

  @Autowired
  public void setTripPriceRepository(TripPriceRepository tripPriceRepository){
    this.tripPriceRepository = tripPriceRepository;
  }

  @Autowired
  public void setMotorRepository(MotorRepository motorRepository){
    this.motorRepository = motorRepository;
  }

  public List<TransactionView> bindListTransactionView(List<Transaction> transactions){
    List<TransactionView> transactionViews = new ArrayList<>();
    for(Transaction transaction : transactions){
      transactionViews.add(this.bindTransactionView(transaction));
    }
    return transactionViews;
  }


  public TransactionView bindTransactionView(Transaction transaction){
    TransactionView result = new TransactionView();
    result.setId(transaction.getId());
    result.setTitle(transaction.getTrip().getTitle());
    result.setDuration(transaction.getTrip().getDuration());
    result.setCoverLandscape(transaction.getTrip().getCoverLandscape());
    result.setCoverPortrait(transaction.getTrip().getCoverPotrait());
    result.setIconCover(transaction.getTrip().getIconCover());
    result.setIconPublisher(transaction.getTrip().getIconPublisher());
    result.setLocation(transaction.getTrip().getLocation());
    result.setTripStatus(transaction.getTripStatus());
    result.setPaymentStatus(transaction.getPaymentStatus());
    result.setStartDate(transaction.getStartDate());
    result.setCode(transaction.getCode());
    result.setTripPrice(tripPriceRepository.findOneByTripIdAndStartTrip(transaction.getTrip().getId(),
        transaction.getStartDate()).getPrice());
    if(result.getPaymentStatus().equals(PaymentStatus.WAITING)){
      result.setExpiredDate(transaction.getExpiredPaymentDate());
    }

    return result;
  }

  public TransactionalDetailView bindTransactionDetail(Transaction transaction, List<TransactionDetail> transactionDetails){
    TransactionalDetailView result = new TransactionalDetailView();
    result.setId(transaction.getId());
    result.setTitle(transaction.getTrip().getTitle());
    result.setTripStatus(transaction.getTripStatus());
    result.setDuration(transaction.getTrip().getDuration());
    result.setStartDate(transaction.getStartDate());
    result.setFinishDate(this.getFinishTripDuration(transaction.getStartDate(),transaction.getTrip().getDuration()));
    result.setPrice(transaction.getPrice());
    result.setCode(transaction.getCode());
    result.setCreated(transaction.getCreated());
    result.setPaymentStatus(transaction.getPaymentStatus());
    result.setNotes(transaction.getNotes());
    result.setCoverLandscape(transaction.getTrip().getCoverLandscape());
    result.setCoverPortrait(transaction.getTrip().getCoverPotrait());
    result.setIconCover(transaction.getTrip().getIconCover());
    result.setIconPublisher(transaction.getTrip().getIconPublisher());
    result.setLocation(transaction.getTrip().getLocation());
    result.setMeetingPoint(transaction.getTrip().getMeetingPoint());
    result.setAccessories(this.getAccessories(transactionDetails));
    result.setMotor(this.bindMotor(transactionDetails));
    result.setTripPrice(tripPriceRepository.findOneByTripIdAndStartTrip(transaction.getTrip().getId(),
        transaction.getStartDate()).getPrice());
    if(result.getPaymentStatus().equals(PaymentStatus.WAITING)){
      result.setExpiredDate(transaction.getExpiredPaymentDate());
    }

    return result;
  }

  private Date getFinishTripDuration(Date startDate, int duration){
    Calendar cal = Calendar.getInstance();
    cal.setTime(startDate);
    cal.add(Calendar.DAY_OF_MONTH, duration);
    Date newDate = cal.getTime();

    return newDate;
  }

  private List<TransactionDetailAccessoryView> getAccessories(List<TransactionDetail> transactionDetails){
    List<TransactionDetailAccessoryView> result = new ArrayList<>();
    for(TransactionDetail transaction : transactionDetails){
      if(!transaction.getType().equalsIgnoreCase("motor")){
        result.add(this.bindAccessoriesDetailTransaction(transaction));
      }
    }
    return result;
  }

  private Motor bindMotor(List<TransactionDetail> transactionDetails){
    Motor motor = new Motor();
    for(TransactionDetail transaction : transactionDetails){
      if(transaction.getType().equalsIgnoreCase("motor")){
        if(!transaction.getTitle().equalsIgnoreCase("bring own motor")) {
          motor = motorRepository.findOneByTitle(transaction.getTitle());
        }else{
          motor.setId(transaction.getId());
          motor.setTitle("Bring Own Motor");
        }
      }
    }
    return motor;
  }

  private TransactionDetailAccessoryView bindAccessoriesDetailTransaction(TransactionDetail transactionDetail){
    TransactionDetailAccessoryView result = new TransactionDetailAccessoryView();
    result.setId(transactionDetail.getId());
    result.setType(transactionDetail.getType());
    result.setTitle(transactionDetail.getTitle());
    result.setDescription(transactionDetail.getDescription());
    result.setDiscount(transactionDetail.getDiscount());
    result.setPrice(transactionDetail.getPrice());
    result.setSize(transactionDetail.getSize());
    result.setPicture(transactionDetail.getPicture());
    return result;
  }


}
