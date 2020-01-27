package com.r2r.road2ring.modules.roadcaptain;

import com.r2r.road2ring.modules.common.PublishedStatus;
import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.user.UserService;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/captain")
public class RoadCaptainAPIController {

  RoadCaptainService roadCaptainService;

  @Autowired
  UserService userService;

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

  @RequestMapping(value = "/helper", method = RequestMethod.GET)
  public ResponseMessage helperPublished(@RequestParam("keyword") String keyword) {

    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(roadCaptainService.getAutoCompleteCaptain(keyword));

    return responseMessage;

  }

  @PostMapping("/change-status/{id}/{statusId}")
  public ResponseMessage changeStatusTrip(@PathVariable("id") int id,
      @PathVariable("statusId") PublishedStatus statusId, Principal principal){
    ResponseMessage responseMessage = new ResponseMessage();
    try{
      roadCaptainService.changeStatus(statusId,id);
    } catch (Road2RingException e){
      responseMessage.setCode(e.getCode());
      responseMessage.setMessage(e.getMessage());
    }

    return responseMessage;
  }
}
