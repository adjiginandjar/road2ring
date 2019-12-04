package com.r2r.road2ring.modules.trip;

import static com.r2r.road2ring.modules.common.Static.M_API;
import static com.r2r.road2ring.modules.common.Static.TRIP;
import static com.r2r.road2ring.modules.common.Static.TRIPS;

import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.common.Road2RingException;
import java.security.Principal;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
public class TripMAPIController {

  TripViewService tripViewService;

  TripPriceService tripPriceService;

  TripService tripService;

  @Autowired
  public void setTripViewService(TripViewService tripViewService) {
    this.tripViewService = tripViewService;
  }

  @Autowired
  public void setTripPriceService(TripPriceService tripPriceService){
    this.tripPriceService = tripPriceService;
  }

  @Autowired
  public void setTripService(TripService tripService){
    this.tripService = tripService;
  }

  @GetMapping( TRIPS + "/{page}/{limit}")
  public ResponseMessage getAllTrip(
      @PathVariable(value = "page") Integer page,
      @PathVariable(value = "limit") Integer limit,
      Principal principal) {

    if (principal != null) {
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
    }

    ResponseMessage responseMessage = tripViewService.getListTripView(page, limit);
    return responseMessage;
  }

  @GetMapping(TRIP + "/{tripId}")
  public ResponseMessage getAllTrip(
      @PathVariable("tripId") Integer tripId,
      Principal principal) {

    if (principal != null) {
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
    }

    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(tripViewService.getDetailTripView(tripId));
    return responseMessage;
  }

  @GetMapping(TRIP + "/{tripId}/price")
  public ResponseMessage getAllTripPrice(
      @PathVariable("tripId") Integer tripId,
      Principal principal) {

    if (principal != null) {
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
    }

    ResponseMessage responseMessage = new ResponseMessage();
    try {
      responseMessage.setObject(tripPriceService.bindListTripPriceView(tripId));
    } catch (Road2RingException e) {
      responseMessage.setMessage(e.getMessage());
      responseMessage.setCode(e.getCode());
    }
    return responseMessage;
  }

  @GetMapping(TRIP + "/related")
  public ResponseMessage getRelatedTrip(
      @RequestParam(value = "tag") List<String> listTags,
      @RequestParam(value = "id") int tripId){
    ResponseMessage responseMessage = new ResponseMessage();
    String contentTags = StringUtils.join(listTags, ",");

    List<Trip> trips = tripService.getListTripByTag(contentTags+",", tripId);
    responseMessage.setObject(tripViewService.bindListTripView(trips));
    return responseMessage;
  }

}
