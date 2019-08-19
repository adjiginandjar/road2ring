package com.r2r.road2ring.modules.TripMotor;

import static com.r2r.road2ring.modules.common.Static.MOTORS;
import static com.r2r.road2ring.modules.common.Static.M_API;

import com.r2r.road2ring.modules.common.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = M_API + MOTORS)
@CrossOrigin
public class TripMotorAPIController {

  @Autowired
  TripMotorService tripMotorService;

  @GetMapping("/by-trip-price-id")
  public ResponseMessage getMotorByTripPriceId(@RequestParam("tripPriceId") int tripPriceId){

    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(tripMotorService.getAllMotorbyTripPriceId(tripPriceId));
    return  responseMessage;

  }

  @GetMapping("/by-trip-id")
  public ResponseMessage getMotorByTripId(@RequestParam("tripId") int tripId){

    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(tripMotorService.getAllMotorbyTripId(tripId));
    return  responseMessage;

  }

}
