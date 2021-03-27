package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.motor.Motor;
import com.r2r.road2ring.modules.motor.MotorService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TripPriceMotorService {

  @Autowired
  TripPriceMotorRepository tripPriceMotorRepository;

  @Autowired
  MotorService motorService;

  TripPriceService tripPriceService;

  @Autowired
  public void setTripPriceService(TripPriceService tripPriceService) {
    this.tripPriceService = tripPriceService;
  }

  public List<TripPriceMotor> getDatatable(Integer tripPriceID){
    return tripPriceMotorRepository.findAllByTripPriceId(tripPriceID);
  }

  public List<Motor> getTripPriceMotor(Integer tripPriceMotorId){
    List<Motor> result = new ArrayList<Motor>();
    List<TripPriceMotor> listTripPriceMotor = tripPriceMotorRepository
        .findAllByTripPriceIdAndStockGreaterThan(tripPriceMotorId,0);
    for (TripPriceMotor item :
        listTripPriceMotor) {
      item.getBike().setId(item.getId());
      item.getBike().setAvailableStock(item.getStock());
      item.getBike().setStocks(item.getStockReserved());
      item.getBike().setPrice(item.getPrice());
      item.getBike().setPicture(item.getBike().getPicture());
      result.add(item.getBike());
    }

    return result;
  }

  public TripPriceMotor getOneTripPriceMotor(Integer id){
    return tripPriceMotorRepository.findOne(id);
  }

  public TripPriceMotor getOneTripPriceMotorByMotorIdAndPriceId(Integer motorId,Integer tripPriceId){
    return tripPriceMotorRepository.findAllByBikeIdAndTripPriceId(motorId,tripPriceId).get(0);
  }


  public List<TripPriceMotor> saveList(List<TripPriceMotor> listTripPriceMotor){
    List<TripPriceMotor> result = new ArrayList<TripPriceMotor>();
    for (TripPriceMotor item :
        listTripPriceMotor) {
      result.add(save(item));
    }
    return result;

  }

  public TripPriceMotor reverseStock(Integer idTripPriceMotor){
    TripPriceMotor result = tripPriceMotorRepository.findOne(idTripPriceMotor);
    result.setStock(result.getStock() + 1);
    return save(result);
  }

  public TripPriceMotor substractStock(Integer idTripPriceMotor ) throws Road2RingException {
    TripPriceMotor result = tripPriceMotorRepository.findOne(idTripPriceMotor);
    if(result.getStock() - 1 >= 0) {
      result.setStock(result.getStock() - 1);
      return save(result);
    }
    else
      throw new Road2RingException("Stock Motor not Available",500);
  }

  public TripPriceMotor save(TripPriceMotor saved){

    return tripPriceMotorRepository.save(saved);
  }

  public TripPriceMotor saveTripPriceMotor(TripPriceMotor motor, Integer tripPriceId){
    TripPriceMotor saved = new TripPriceMotor();
    TripPrice tripPrice = tripPriceService.getOneTripPrice(tripPriceId);

    if(motor.getId() != null && motor.getId() != 0){
      saved = tripPriceMotorRepository.findOne(motor.getId());
    }
    saved.setTripPrice(tripPrice);
    saved.setStock(motor.getStock());
    saved.setBike(motor.getBike());
    saved.setPrice(motor.getPrice());

    return tripPriceMotorRepository.save(saved);
  }

  @Transactional
  public TripPriceMotor saveTripPriceMotorV2(TripPriceMotor tripPriceMotor, Integer tripPriceId){
    TripPriceMotor saved = new TripPriceMotor();
    Motor motor = new Motor();
    if(tripPriceMotor.getId() != 0){
      saved = tripPriceMotorRepository.findOne(tripPriceMotor.getId());
    }

    motor = motorService.getMotoyrById(tripPriceMotor.getBike().getId());
    TripPrice tripPrice = tripPriceService.getOneTripPrice(tripPriceId);

    saved.setTripPrice(tripPrice);
    saved.setStock(tripPriceMotor.getStock());
    saved.setBike(motor);
    saved.setPrice(tripPriceMotor.getPrice());

    return tripPriceMotorRepository.save(saved);
  }

  @Transactional
  public TripPriceMotor saveTripPriceMotors(Integer motorId, Integer tripPriceId, Integer stock, Integer price){
    TripPriceMotor saved = new TripPriceMotor();
    Motor motor = new Motor();
    motor = motorService.getMotoyrById(motorId);
    TripPrice tripPrice = tripPriceService.getOneTripPrice(tripPriceId);

    saved.setTripPrice(tripPrice);
    saved.setStock(stock);
    saved.setBike(motor);
    saved.setPrice(price);

    return tripPriceMotorRepository.save(saved);
  }

  @Transactional
  public List<TripPriceMotor> saveListTripPriceMotors(TripPrice tripPrice){
    List<TripPriceMotor> result = new ArrayList<>();
    List<TripPriceMotor> deletedTripPriceMotorList = tripPrice.getDeletedTripPriceMotorList();

    if (deletedTripPriceMotorList != null && deletedTripPriceMotorList.size() != 0) {
      for (TripPriceMotor deletedTripPriceMotor : deletedTripPriceMotorList) {
        removeTab(deletedTripPriceMotor.getId());
      }
    }
    for (TripPriceMotor saved : tripPrice.getTripPriceMotorList()) {
      result.add(saveTripPriceMotorV2(saved, tripPrice.getId()));
    }

    return result;
  }

  private void removeTab(Integer id) {
    tripPriceMotorRepository.delete(id);
  }

  public boolean ifExist(TripPriceMotor tripPriceMotor, Integer tripPriceId){
    if(tripPriceMotor.getId() != null && tripPriceMotor.getId() != 0){
      TripPriceMotor current = getOneTripPriceMotor(tripPriceMotor.getId());
      if(tripPriceMotor.getBike().getId() == current.getBike().getId()){
        return false;
      }else{
        return tripPriceMotorRepository.findAllByBikeIdAndTripPriceId(tripPriceMotor.getBike().getId(), tripPriceId).size() != 0;
      }
    }else{
      return tripPriceMotorRepository.findAllByBikeIdAndTripPriceId(tripPriceMotor.getBike().getId(), tripPriceId).size() != 0;
    }
  }

  public boolean ifExists(Integer motorId, Integer tripPriceId){
    return tripPriceMotorRepository.findAllByBikeIdAndTripPriceId(motorId, tripPriceId).size() != 0;
  }

}
