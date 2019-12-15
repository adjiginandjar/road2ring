package com.r2r.road2ring.modules.transaction;

import com.r2r.road2ring.modules.accessory.Accessory;
import com.r2r.road2ring.modules.accessory.AccessoryRepository;
import com.r2r.road2ring.modules.accessory.AccessoryService;
import com.r2r.road2ring.modules.common.InvalidOrderException;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.motor.Motor;
import com.r2r.road2ring.modules.motor.MotorRepository;
import com.r2r.road2ring.modules.trip.TripPriceMotor;
import com.r2r.road2ring.modules.trip.TripPriceMotorService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionDetailService {

  AccessoryRepository accessoryRepository;

  TransactionDetailRepository transactionDetailRepository;

  MotorRepository motorRepository;

  @Autowired
  TripPriceMotorService tripPriceMotorService;

  @Autowired
  AccessoryService accessoryService;

  @Autowired
  public void setTransactionDetailRepository(TransactionDetailRepository transactionDetailRepository){
    this.transactionDetailRepository = transactionDetailRepository;
  }

  @Autowired
  public void setAccessoryRepository(AccessoryRepository accessoryRepository){
    this.accessoryRepository = accessoryRepository;
  }

  @Autowired
  public void setMotorRepository(MotorRepository motorRepository){
    this.motorRepository = motorRepository;
  }

  public TransactionDetail saveTransactionDetailAccessory(Accessory accessory,
      Transaction transaction,Integer quantity, boolean bringOwnHelm)
      throws Road2RingException {
    TransactionDetail transactionDetail = new TransactionDetail();
    if(!bringOwnHelm) {
      accessoryService.substractStock(accessory.getId(),quantity);
      transactionDetail.setTransaction(transaction);
      transactionDetail.setTitle(accessory.getTitle());
      transactionDetail.setType(accessory.getAccessorySubCategory().getTitle());
      transactionDetail.setPrice(accessory.getPrice());
      transactionDetail.setDiscount(accessory.getDiscount());
      transactionDetail.setPicture(accessory.getPicture());
      transactionDetail.setSize(accessory.getSize());
      transactionDetail.setQuantity(quantity);
      return transactionDetailRepository.save(transactionDetail);
    } else {
      transactionDetail.setTransaction(transaction);
      transactionDetail.setTitle("Bring Own Helm");
      transactionDetail.setType("Helm");
      return transactionDetailRepository.save(transactionDetail);
    }
  }

  public List<TransactionDetail> saveListTransactionalAccessory(List<Accessory> accessoryList, Transaction transaction, boolean bringOwnHelm)
      throws Road2RingException {

    List<TransactionDetail> result = new ArrayList<>();
    if(!bringOwnHelm) {
      for (Accessory accessory : accessoryList) {
        Accessory saved = accessoryRepository.findOne(accessory.getId());
        saved.setSize(accessory.getSize());
        result.add(saveTransactionDetailAccessory(saved, transaction,accessory.getQuantity(), bringOwnHelm));
      }
    } else {
      Accessory saved = new Accessory();
      saveTransactionDetailAccessory(saved, transaction,0, bringOwnHelm);
    }
    return result;
  }

  public TransactionDetail saveMotor(Motor motor, Transaction transaction, boolean bringOwn)
      throws Road2RingException {
    TransactionDetail transactionDetail = new TransactionDetail();
    if(bringOwn != true) {
      TripPriceMotor saved = tripPriceMotorService.substractStock(motor.getId());
      transactionDetail.setTransaction(transaction);
      transactionDetail.setTitle(saved.getBike().getTitle());
      transactionDetail.setType("Motor");
      transactionDetail.setPrice(saved.getPrice());
      transactionDetail.setDiscount(saved.getBike().getDiscount());
      transactionDetail.setPicture(saved.getBike().getPicture());
      transactionDetail.setCapacity(saved.getBike().getCapacity());
      transactionDetail.setBrand(saved.getBike().getBrand());
      transactionDetail.setQuantity(1);
      return transactionDetailRepository.save(transactionDetail);
    } else {
      transactionDetail.setTransaction(transaction);
      transactionDetail.setTitle("Bring Own Motor");
      transactionDetail.setType("motor");
      return null;
    }
  }

  public List<TransactionDetail> getAllDetailByTrxId(int id) {
    return transactionDetailRepository.findAllByTransactionIdOrderByIdDesc(id);
  }

}
