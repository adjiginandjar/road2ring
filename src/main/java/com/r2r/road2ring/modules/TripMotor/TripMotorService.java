package com.r2r.road2ring.modules.TripMotor;

import com.r2r.road2ring.modules.trip.TripPrice;
import com.r2r.road2ring.modules.trip.TripPriceService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripMotorService {

  @Autowired
  TripMotorRepository tripMotorRepository;

  @Autowired
  TripPriceService tripPriceService;

  /**
   *
   * Web
   */

  public List<TripMotor> getAllMotorbyTripPriceId(Integer tripId){
    List<TripMotor> result =  tripMotorRepository
        .findAllByTripPriceIdAndStockGreaterThan(tripId,0);

    return result;
  }
  public List<TripMotor> getAllMotorbyTripId(Integer tripId){
    List<Integer> tripPriceIds = tripPriceService.getAllTripPriceByTripId(tripId).stream()
        .map(TripPrice::getId).collect(Collectors.toList());
    List<TripMotor> result =  tripMotorRepository
        .findAllByTripPriceIdInAndStockGreaterThan(tripPriceIds,0);

    return result;
  }

  /**
   * SI
   */

  public TripMotor save(TripMotor tripMotor){
    return tripMotorRepository.save(tripMotor);
  }

  public TripMotor reduceStock(Integer tripMotorId){
    TripMotor saved ;
    saved =  tripMotorRepository.findOne(tripMotorId);
    if(saved != null) {
      if (saved.getStock() - 1 >= 0) {
        saved.setStock(saved.getStock() - 1);
        tripMotorRepository.save(saved);
      }
    }

    return saved;
  }

}
