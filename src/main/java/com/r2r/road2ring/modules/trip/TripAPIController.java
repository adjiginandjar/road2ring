package com.r2r.road2ring.modules.trip;

import static com.r2r.road2ring.modules.common.Static.API;
import static com.r2r.road2ring.modules.common.Static.M_API;
import static com.r2r.road2ring.modules.common.Static.TRIP;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.PublishedStatus;
import com.r2r.road2ring.modules.common.ResponseCode;
import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.common.ResponseView;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.common.UploadService;
import com.r2r.road2ring.modules.itinerary.Itinerary;
import com.r2r.road2ring.modules.itinerary.ItineraryService;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = API + TRIP)
public class TripAPIController {

  TripService tripService;

  UploadService uploadService;

  ItineraryService itineraryService;

  TripPriceMotorService tripPriceMotorService;

  @Autowired
  public void setTripPriceMotorService(
      TripPriceMotorService tripPriceMotorService) {
    this.tripPriceMotorService = tripPriceMotorService;
  }

  @Autowired
  public void setItineraryService(ItineraryService itineraryService) {
    this.itineraryService = itineraryService;
  }

  @Autowired
  public void setTripService(TripService tripService) {
    this.tripService = tripService;
  }

  @Autowired
  public void setUploadService(UploadService uploadService){
    this.uploadService = uploadService;
  }

  @RequestMapping(value = "/datatable", method = RequestMethod.GET)
  @JsonView(ResponseView.DetailedTrip.class)
  public DataTablesOutput<Trip> datatable(@Valid DataTablesInput input,
      HttpServletRequest request) {

    return tripService.getDatatableContents(input);
//    return tripService.getAllTrip();
  }

  @RequestMapping(value = "/data", method = RequestMethod.GET)
  @JsonView(ResponseView.DetailedTrip.class)
  public List<Trip> datatable(
      HttpServletRequest request) {

    return tripService.getAllTrip();
  }

  @RequestMapping(value = "/{tripPriceId}/trip-price-motor/datatable", method = RequestMethod.GET)
  @JsonView(ResponseView.DetailedTripPriceMotor.class)
  public List<TripPriceMotor> tripPriceMotorDatatable(@PathVariable("tripPriceId") Integer id,
      HttpServletRequest request) {

    return tripPriceMotorService.getDatatable(id);
  }

  @RequestMapping(value = "/{tripId}/itinerary/data", method = RequestMethod.GET)
//  @JsonView(ResponseView.DetailedTrip.class)
  public List<TripItineraryDataView> datatableItinerary(@PathVariable("tripId") int id,
      HttpServletRequest request) {
    return tripService.getTripItineraryGroup(id);
  }

  @RequestMapping(value = "/{tripId}/itinerary/{group}", method = RequestMethod.GET)
  @JsonView(ResponseView.DetailedTrip.class)
  public List<Itinerary> itineraryByGroup(@PathVariable("tripId") int tripId, @PathVariable("group") int group,
      HttpServletRequest request) {
    List<Itinerary> itineraryList = itineraryService.getItineraryByGroupAndTrip(group, tripId);
    return itineraryList;
  }


  @RequestMapping(value = "/{tripId}/price-list/data", method = RequestMethod.GET)
  @JsonView(ResponseView.DetailedTrip.class)
  public List<TripPrice> datatablePriceList(@PathVariable("tripId") int id,
      HttpServletRequest request) {
    return tripService.getTripPriceList(id);
  }

  @PostMapping("/upload_trip/{typeImage}")
  public ResponseMessage uploadPhoto(@PathVariable String typeImage,@RequestParam("file") MultipartFile file) {
    ResponseMessage responseMessage = new ResponseMessage();
    try {
      if (!file.isEmpty()) {
        responseMessage.setCode(ResponseCode.SUCCESS.getCode());
        responseMessage.setObject(uploadService.uploadImagePicture(file, typeImage));
      }
    }catch (IOException e){
      responseMessage.setMessage(e.getMessage());
      responseMessage.setCode(ResponseCode.INTERNAL_SERVER_ERROR.getCode());
    }catch (FileSizeLimitExceededException e){
      responseMessage.setMessage(e.getMessage());
      responseMessage.setCode(ResponseCode.INTERNAL_SERVER_ERROR.getCode());
    }
    return responseMessage;
  }

  @PostMapping("/{tripId}/price-list/{tripPriceId}/bike/save")
  public ResponseMessage saveTripPriceMotor(@PathVariable("tripId") int tripId,
                              @PathVariable("tripPriceId") int tripPriceId,
                              @RequestParam Integer[] id, @RequestParam Integer[] price,
                              @RequestParam Integer[] stock){

    ResponseMessage responseMessage = new ResponseMessage();
    int failure = 0;
    for(int i = 0; i<id.length; i++){
      if(!tripPriceMotorService.ifExists(id[i], tripPriceId)) {
        tripPriceMotorService.saveTripPriceMotors(id[i], tripPriceId, stock[i], price[i]);
      }else{
        failure+=1;
      }
    }
    if(failure > 0){
      responseMessage.setMessage(failure+" data anda tidak berhasil disimpan karena list ini sudah memiliki motor yang anda masukkan.");
    }else{
      responseMessage.setMessage("sukses");
    }

    return responseMessage;

  }

  @PostMapping("/change-status/{tripId}/{statusId}")
  public ResponseMessage changeStatusTrip(@PathVariable("tripId") int tripId,
      @PathVariable("statusId") TripPublishedStatus statusId, Principal principal){
    ResponseMessage responseMessage = new ResponseMessage();
    try{
      tripService.changeTripStatus(statusId,tripId);
    } catch (Road2RingException e){
      responseMessage.setCode(e.getCode());
      responseMessage.setMessage(e.getMessage());
    }

    return responseMessage;
  }



  @PostMapping("/itinerary/change-status/{id}/{statusId}")
  public ResponseMessage changeStatus(@PathVariable("id") int id,
      @PathVariable("statusId") PublishedStatus statusId, Principal principal){
    ResponseMessage responseMessage = new ResponseMessage();
    try{
      itineraryService.changeStatus(statusId,id);
    } catch (Road2RingException e){
      responseMessage.setCode(e.getCode());
      responseMessage.setMessage(e.getMessage());
    }

    return responseMessage;
  }
}
