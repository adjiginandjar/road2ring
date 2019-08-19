package com.r2r.road2ring.modules.motor;

import static com.r2r.road2ring.modules.common.Static.MOTORS;
import static com.r2r.road2ring.modules.common.Static.M_API;

import com.r2r.road2ring.modules.common.ResponseMessage;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = M_API)
public class MotorMAPIController {

  MotorService motorService;

  @Autowired
  public void setMotorService(MotorService motorService){
    this.motorService = motorService;
  }

  @GetMapping(MOTORS)
  public ResponseMessage getAllMotor(
      Principal principal) {

    if (principal != null) {
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
    }

    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(motorService.getAllMotor());
    return responseMessage;
  }

}
