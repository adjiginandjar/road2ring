package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.TripFacility.TripFacilityService;
import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.config.SystemConfig;
import com.r2r.road2ring.modules.config.SystemConfigService;
import com.r2r.road2ring.modules.facility.Facility;
import com.r2r.road2ring.modules.facility.FacilityService;
import com.r2r.road2ring.modules.hotel.Hotel;
import com.r2r.road2ring.modules.itinerary.Itinerary;
import com.r2r.road2ring.modules.itinerary.ItineraryService;
import com.r2r.road2ring.modules.motor.Motor;
import com.r2r.road2ring.modules.motor.MotorService;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/trip")
public class TripController {

  TripService tripService;

  ItineraryService itineraryService;

  TripFacilityService tripFacilityService;

  FacilityService facilityService;

  MotorService motorService;

  @Autowired
  TripPriceMotorService tripPriceMotorService;

  @Autowired
  SystemConfigService systemConfigService;

  @Autowired
  public void setFacilityService(FacilityService facility) {
    this.facilityService = facility;
  }

  @Autowired
  public void setTripService(TripService tripService) {
    this.tripService = tripService;
  }

  @Autowired
  public void setItineraryService(ItineraryService itineraryService) {
    this.itineraryService = itineraryService;
  }

  @Autowired
  public void setMotorService(MotorService motorService) {
    this.motorService = motorService;
  }

  @Autowired
  public void setTripFacilityService(TripFacilityService tripFacility) {
    this.tripFacilityService = tripFacility;
  }

  @RequestMapping(value = "/rtms", method = RequestMethod.GET)
  public String indexRtms(Model model) {
    ResponseMessage response = new ResponseMessage();
    model.addAttribute("response", response);
    return "rtms/page/trip";
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String index(Model model) {
    ResponseMessage response = new ResponseMessage();
    model.addAttribute("response", response);
    return "admin/page/trip";
  }

  @RequestMapping(value = "/add", method = RequestMethod.GET)
  public String add(Model model) {
    ResponseMessage response = new ResponseMessage();
    Trip trip = new Trip();
    response.setObject(trip);

    List<Facility> facilityList = facilityService.getAllFacilityPublished();

    model.addAttribute("response", response);
    model.addAttribute("facilities", facilityList);
    return "admin/forms/trip";
  }

  @RequestMapping(value = "/edit", method = RequestMethod.GET)
  public String edit(Model model, @RequestParam int id, HttpServletRequest request) {
    ResponseMessage response = new ResponseMessage();

    String baseUrl = request.getRequestURL().toString()
        .replace(request.getRequestURI().substring(1), request.getContextPath());

    Trip trip = tripService.getTripById(id);
    response.setObject(trip);

    List<Facility> facilityList = facilityService.getAllFacilityPublished();

    model.addAttribute("response", response);
    model.addAttribute("facilities", facilityList);
    model.addAttribute("baseUrl", baseUrl);
    return "admin/forms/trip";
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(@ModelAttribute Trip trip, Model model, Principal principal) {
    ResponseMessage response = new ResponseMessage();
    if(trip.getDeletedHotel()!= null) {
      for (Iterator<Hotel> iter = trip.getDeletedHotel().listIterator();
           iter.hasNext(); ) {
        Hotel deleted = iter.next();
        if (deleted.getId() == 0) {
          iter.remove();
        }
      }
    }

    if(trip.getDeletedHotel()!= null) {
      for (Iterator<Hotel> iter = trip.getHotels().listIterator();
           iter.hasNext(); ) {
        Hotel deleted = iter.next();
        if (deleted.getId() == null) {
          iter.remove();
        }
      }
    }
    response.setObject(tripService.saveTrip(trip));
    model.addAttribute("response", response);

    return "redirect:/trip";
  }

  @RequestMapping(value = "/{tripId}/itinerary/delete/{day}")
  public String deleteTripItinerary(@PathVariable("tripId") int id,@PathVariable("day") int day, @ModelAttribute Trip trip){
    itineraryService.deleteItinerary(id, day);
    return "redirect:/trip/"+id+"/itinerary";
  }

  @RequestMapping(value = "/{tripId}/itinerary/save")
  public String saveTripItinerary(@PathVariable("tripId") int id, @ModelAttribute Trip trip, Model model, Principal principal){
    Trip theTrip = tripService.getTripById(id);
    theTrip.setItineraries(trip.getItineraries());
    theTrip.setDeletedItinerary(trip.getDeletedItinerary());

    if(theTrip.getDeletedItinerary()!= null) {
      for (Iterator<Itinerary> iter = theTrip.getDeletedItinerary().listIterator();
          iter.hasNext(); ) {
        Itinerary deleted = iter.next();
        if (deleted.getId() == 0) {
          iter.remove();
        }
      }
    }

    if(theTrip.getItineraries()!= null) {
      for (Iterator<Itinerary> iter = theTrip.getItineraries().listIterator();
          iter.hasNext(); ) {
        Itinerary deleted = iter.next();
        if (deleted.getId() == null) {
          iter.remove();
        }
      }
    }

    itineraryService.saveListOfItinerary(theTrip.getItineraries(), theTrip);
    return "redirect:/trip/"+id+"/itinerary";
  }

  @RequestMapping(value = "/{tripId}/itinerary")
  public String viewTripItinerary(@PathVariable("tripId") int id, Model model) {
    Trip trip = tripService.getTripById(id);
    ResponseMessage response = new ResponseMessage();
    response.setObject(trip);
    model.addAttribute("response", response);
    return "admin/page/trip-itinerary";
  }

  @RequestMapping(value = "/{tripId}/itinerary/add")
  public String addTripItinerary(@PathVariable("tripId") int id, Model model) {
    Trip trip = tripService.getTripById(id);
    List<Itinerary> itineraryList = new ArrayList<>();
    ResponseMessage response = new ResponseMessage();
    response.setObject(itineraryList);
    model.addAttribute("response", response);
    model.addAttribute("action", "/trip/"+id+"/itinerary/save");
    model.addAttribute("trip", trip);
    return "admin/forms/trip-itinerary";
  }

  @RequestMapping(value = "/{tripId}/itinerary/edit")
  public String editTripItinerary(@PathVariable("tripId") int tripId, @RequestParam int id, @ModelAttribute Itinerary itinerary, Model model, HttpServletRequest request) {
    List<Itinerary> itineraryList = itineraryService.getItineraryByGroupAndTrip(id, tripId);
    Trip trip = tripService.getTripById(tripId);
    ResponseMessage response = new ResponseMessage();

    String baseUrl = request.getRequestURL().toString()
        .replace(request.getRequestURI().substring(1), request.getContextPath());

    response.setObject(itineraryList);
    model.addAttribute("response", response);
    model.addAttribute("action", "/trip/"+tripId+"/itinerary/save");
    model.addAttribute("trip", trip);
    model.addAttribute("baseUrl", baseUrl);
    return "admin/forms/trip-itinerary";
  }

  @RequestMapping(value = "/{tripId}/facility")
  public String editTripFacility(@PathVariable("tripId") int id, @ModelAttribute Itinerary itinerary, Model model) {
    ResponseMessage response = new ResponseMessage();
    model.addAttribute("response", response);
    return "admin/page/trip-facility";
  }

  @RequestMapping(value = "/{tripId}/facility/add")
  public String addTripFacility(@PathVariable("tripId") int id, @ModelAttribute Facility itinerary, Model model) {
    Trip trip = tripService.getTripById(id);
    ResponseMessage response = new ResponseMessage();
    response.setObject(trip);
    model.addAttribute("action", "/trip/"+id+"/facility/save");
    model.addAttribute("response", response);
    return "admin/forms/trip-facility";
  }

  @RequestMapping(value = "/{tripId}/price-list")
  public String viewTripPriceList(@PathVariable("tripId") int id, @ModelAttribute TripPrice tripPrice, Model model) {
    ResponseMessage response = new ResponseMessage();
    model.addAttribute("response", response);
    return "admin/page/price-list";
  }

  @RequestMapping(value = "/{tripId}/price-list/add")
  public String addTripPriceList(@PathVariable("tripId") int id, @ModelAttribute TripPrice tripPrice, Model model) {
    TripPrice price = new TripPrice();
    TripPriceDetail detail = new TripPriceDetail();

    ResponseMessage response = new ResponseMessage();
    response.setObject(price);
    model.addAttribute("response", response);
    model.addAttribute("tripPriceDetail", detail);
    model.addAttribute("action", "/trip/"+id+"/price-list/save");
    model.addAttribute("tripId", id);
    model.addAttribute("companyCommissionFee",systemConfigService.getSystemConfig().getCompanyFee());
    return "admin/forms/price-list";
  }

  @RequestMapping(value = "/{tripId}/price-list/save")
  public String saveTripPriceList(@PathVariable("tripId") int id, @ModelAttribute TripPrice tripPrice, Model model) {
    ResponseMessage response = new ResponseMessage();
    model.addAttribute("response", response);
    Trip trip = tripService.getTripById(id);
    Calendar cal = Calendar.getInstance();
    cal.setTime(tripPrice.getStartTrip());
    cal.add(Calendar.DAY_OF_MONTH, trip.getDuration());
    Date newDate = cal.getTime();
    tripPrice.setFinishTrip(newDate);

    System.out.println("tripPrice = " + tripPrice);

    tripService.saveTripPrice(id, tripPrice);
    return "redirect:/trip/"+id+"/price-list";
  }

  @RequestMapping(value = "/{tripId}/price-list/edit")
  public String editTripPrice(@PathVariable("tripId") int tripId, @RequestParam int id, @ModelAttribute Itinerary itinerary, Model model) {
    TripPrice tripPrice = tripService.getTripPriceById(id);
    TripPriceDetail detail = new TripPriceDetail();
    if(tripPrice.getTripPriceDetail() !=null){
      detail = tripPrice.getTripPriceDetail();
    }
    ResponseMessage response = new ResponseMessage();
    response.setObject(tripPrice);
    model.addAttribute("response", response);
    model.addAttribute("tripPriceDetail", detail);
    model.addAttribute("companyCommissionFee",detail.getCompanyCommission());
    model.addAttribute("action", "/trip/"+tripId+"/price-list/save");
    return "admin/forms/price-list";
  }



  @RequestMapping(value = "/{tripId}/price-list/{tripPriceId}/bike",method = RequestMethod.GET)
  public String listBikeTripPrice(@PathVariable("tripPriceId") int tripPriceId,
      @PathVariable("tripId") int tripId, Model model) {

    ResponseMessage response = new ResponseMessage();
    response.setObject(tripPriceMotorService.getDatatable(tripPriceId));
    model.addAttribute("response", response);
    model.addAttribute("tripId", tripId);
    model.addAttribute("priceId", tripPriceId);
    return "admin/page/trip-bike";
  }
  @RequestMapping(value = "/{tripId}/price-list/{tripPriceId}/bike/add",method = RequestMethod.GET)
  public String addBikeTripPrice(@PathVariable("tripId") int tripId,
      @PathVariable("tripPriceId") int tripPriceId, Model model) {

    ResponseMessage response = new ResponseMessage();
    TripPriceMotor tripPriceMotor = new TripPriceMotor();
    Motor motor = new Motor();
    tripPriceMotor.setBike(motor);
    response.setObject(tripPriceMotor);
    model.addAttribute("response", response);
    model.addAttribute("action", "/trip/"+tripId+"/price-list/"+tripPriceId+"/bike/save");
    model.addAttribute("tripPriceId", tripPriceId);
    return "admin/forms/bike";
  }

  @RequestMapping(value = "/{tripId}/price-list/{tripPriceId}/bike/edit",method = RequestMethod.GET)
  public String addBikeTripPrice(@PathVariable("tripId") int tripId,
      @PathVariable("tripPriceId") int tripPriceId, Model model, @RequestParam int id) {

    ResponseMessage response = new ResponseMessage();
    TripPriceMotor tripPriceMotor = tripPriceMotorService.getOneTripPriceMotor(id);
    Motor motor = motorService.getMotoyrById(tripPriceMotor.getBike().getId());
    tripPriceMotor.setBike(motor);
    response.setObject(tripPriceMotor);
    model.addAttribute("response", response);
    model.addAttribute("action", "/trip/"+tripId+"/price-list/"+tripPriceId+"/bike/save");
    model.addAttribute("tripPriceId", tripPriceId);
    return "admin/forms/bike";
  }

  @RequestMapping(value = "/{tripId}/price-list/{tripPriceId}/bike/save",method = RequestMethod.POST)
  public String saveBikeTripPrice(@ModelAttribute TripPriceMotor tripPriceMotor, @PathVariable("tripId") int tripId,
      @PathVariable("tripPriceId") int tripPriceId, Model model) {

    if(!tripPriceMotorService.ifExist(tripPriceMotor, tripPriceId)) {
      tripPriceMotorService.saveTripPriceMotor(tripPriceMotor, tripPriceId);
      return "redirect:/trip/" + tripId + "/price-list/" + tripPriceId + "/bike";
    }
    else{
      if(tripPriceMotor.getId() != null && tripPriceMotor.getId() != 0)
        return "redirect:/trip/"+ tripId +"/price-list/"+tripPriceId+"/bike/edit?id="+tripPriceMotor.getId()+"&exists=true";
      else
        return "redirect:/trip/"+ tripId +"/price-list/"+tripPriceId+"/bike/add?exists=true";
    }

  }

  @RequestMapping(value = "/rtms/form",method = RequestMethod.GET)
  public String addTripTitleBody(Model model) {

    ResponseMessage response = new ResponseMessage();
    return "rtms/form/tripTitleBody";
  }
}
