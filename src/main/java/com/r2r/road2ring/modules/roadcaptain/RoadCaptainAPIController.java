package com.r2r.road2ring.modules.roadcaptain;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/captain")
public class RoadCaptainAPIController {

  RoadCaptainService roadCaptainService;

  @Autowired
  public void setMotorService(RoadCaptainService roadCaptainService) {
    this.roadCaptainService = roadCaptainService;
  }

  @RequestMapping(value = "/data", method = RequestMethod.GET)
//  @JsonView(ResponseView..class)
  public List<RoadCaptain> datatable(
      HttpServletRequest request) {

    return roadCaptainService.getAllCaptain();
  }

}