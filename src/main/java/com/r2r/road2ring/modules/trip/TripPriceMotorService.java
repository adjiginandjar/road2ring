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
//    List<TripPriceMotor> listTripPriceMotor = tripPriceMotorRepository
//        .findAllByTripPriceIdAndStockGreaterThan(tripPriceMotorId,0);
//    for (TripPriceMotor item :
//        listTripPriceMotor) {
//      result.add(item.getBike());
//    }

    result = motorService.getAllMotor();

    return result;
  }

  public TripPriceMotor getOneTripPriceMotor(Integer id){
    return tripPriceMotorRepository.findOne(id);
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

}
