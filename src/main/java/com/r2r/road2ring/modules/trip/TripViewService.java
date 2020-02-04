package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.TripFacility.TripFacility;
import com.r2r.road2ring.modules.TripFacility.TripFacilityService;
import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.hotel.HotelService;
import com.r2r.road2ring.modules.itinerary.Itinerary;
import com.r2r.road2ring.modules.itinerary.ItineraryService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class TripViewService {

  TripService tripService;

  ItineraryService itineraryService;

  TripPriceRepository tripPriceRepository;

  TripFacilityService tripFacilityService;

  @Autowired
  HotelService hotelService;

  @Autowired
  public void setTripService(TripService tripService){
    this.tripService = tripService;
  }

  @Autowired
  public void setItineraryService(ItineraryService itineraryService){
    this.itineraryService = itineraryService;
  }

  @Autowired
  public void setTripPriceRepository(TripPriceRepository tripPriceRepository) {
    this.tripPriceRepository = tripPriceRepository;
  }

  @Autowired
  public void setTripFacilityService(TripFacilityService tripFacilityService){
    this.tripFacilityService = tripFacilityService;
  }

//  public List<TripView> getListTripView(Integer page, Integer limit){
//    List<TripView> result = new ArrayList<>();
//    Page<Trip> trips = tripService.findTripPageablePage(page,limit);
//    for(Trip trip : trips){
//      result.add(this.getTripView(trip));
//    }
//    return result;
//  }

  public ResponseMessage getListTripView(Integer page, Integer limit){
    ResponseMessage responseMessage = new ResponseMessage();
    List<TripView> result = new ArrayList<>();
    TripView item = new TripView();
    Page<Trip> trips = tripService.findTripPageablePage(page,limit);
    for(Trip trip : trips){
      item = this.getTripView(trip);
      if(item.getTripPrice() > 0) {
        result.add(item);
      }
    }
    responseMessage.setObject(result);
    responseMessage.setTotalPage(trips.getTotalPages());
    return responseMessage;
  }

  public List<TripView> bindListTripView(List<Trip> trips){
    List<TripView> tripViews = new ArrayList<>();
    TripView item = new TripView();
    for(Trip trip : trips){
      item = this.getTripView(trip);
      if(item.getTripPrice() >0)
        tripViews.add(item);
    }
    return tripViews;
  }

  public TripView getTripView(Trip trip){
    TripView tripView = new TripView();
    tripView.setId(trip.getId());
    tripView.setTitle(trip.getTitle());
    tripView.setDuration(trip.getDuration());
    tripView.setLocation(trip.getLocation());
    // TODO: CHANGE FAKER
    tripView.setCoverLandscape(trip.getCoverLandscape());
    tripView.setIconPublisher(trip.getIconPublisher());
    tripView.setCoverPortrait(trip.getCoverPotrait());
    tripView.setIconCover(trip.getIconCover());
    // TODO: CHANGE ID TRIP
    tripView.setTripPrice(this.bindTripPrice(trip.getId()));
    return tripView;
  }

  private int bindTripPrice(int tripId){
    int tripPrice;
    try {
      tripPrice = tripPriceRepository.
          findTop1ByTripIdAndStartTripGreaterThanOrderByPriceAsc(tripId, new Date()).getPrice();
    } catch (NullPointerException e){
      return 0;
    }
    return tripPrice;
  }

  public TripViewDetail getDetailTripView(Integer tripId){
    Trip trip = tripService.getTripById(tripId);

    TripViewDetail tripViewDetail = new TripViewDetail();
    tripViewDetail.setId(trip.getId());
    tripViewDetail.setTitle(trip.getTitle());
    tripViewDetail.setDuration(trip.getDuration());
    tripViewDetail.setCoverLandscape(trip.getCoverLandscape());
    tripViewDetail.setIconPublisher(trip.getIconPublisher());
    tripViewDetail.setTripPrice(tripPriceRepository.
        findTop1ByTripIdAndStartTripGreaterThanOrderByPriceAsc(tripId, new Date()).getPrice());
    tripViewDetail.setCoverPortrait(trip.getCoverPotrait());
    tripViewDetail.setMap(trip.getMap());
    tripViewDetail.setDescription(trip.getDescription());
    tripViewDetail.setDistance(trip.getDistance());
    tripViewDetail.setIconCover(trip.getIconCover());
    tripViewDetail.setMaxRider(trip.getMaxRider());
    tripViewDetail.setTerrain(trip.getTerrain());
    tripViewDetail.setLocation(trip.getLocation());
    tripViewDetail.setMeetingPoint(trip.getMeetingPoint());
    tripViewDetail.setRoadCaptainDescription(trip.getRoadCaptain().getDescription());
    tripViewDetail.setRoadCaptainName(trip.getRoadCaptain().getFullName());
    tripViewDetail.setImageRoadCaptain(trip.getRoadCaptain().getPicture());
    tripViewDetail.setFacilityNotIncluded(trip.getFacilityNot());
    tripViewDetail.setFacilities(getTripFacilityView(trip.getId()));
    tripViewDetail.setItineraries(this.getListItineraryTrip(trip.getId()));
    tripViewDetail.setHotels(hotelService.getDetailTripHotel(trip.getId()));
    return tripViewDetail;
  }

  private List<TripViewFacility> getTripFacilityView(Integer tripId){
    List<TripViewFacility> tripViewFacilities = new ArrayList<>();
    List<TripFacility> tripFacilities = tripFacilityService.getTripFacilityOnTrip(tripId);
    for(TripFacility tripFacility : tripFacilities){
      TripViewFacility saved = new TripViewFacility();
      saved.setPicture(tripFacility.getFacilityImage());
      saved.setTitle(tripFacility.getFacilityName());
      tripViewFacilities.add(saved);
    }
    return tripViewFacilities;
  }

  private TripViewItinerary getItineraryTrip(Integer tripId, Integer groupId){
    TripViewItinerary tripViewItinerary = new TripViewItinerary();
    List<Itinerary> itineraries = itineraryService.getItineraryTripByGroup(groupId,tripId);

    List<ItineraryDetail> itineraryDetails = new ArrayList<>();

    for(Itinerary itinerary : itineraries){
      ItineraryDetail itineraryDetail = new ItineraryDetail();
      itineraryDetail.setTitle(itinerary.getTitle());
      itineraryDetail.setDescription(itinerary.getDescription());
      itineraryDetail.setImageItinerary(itinerary.getImageUrl());
      itineraryDetails.add(itineraryDetail);
      tripViewItinerary.setGroupTitle(itinerary.getGroupTitle());
    }

    tripViewItinerary.setDetails(itineraryDetails);

    return tripViewItinerary;
  }

  private List<TripViewItinerary> getListItineraryTrip(Integer tripId){
    List<TripViewItinerary> tripViewItineraries = new ArrayList<>();
    List<Integer> groupIdTrip = itineraryService.getItineraryGroupInTrip(tripId);
    for(Integer groupdId : groupIdTrip){
      tripViewItineraries.add(this.getItineraryTrip(tripId,groupdId));
    }
    return tripViewItineraries;
  }
}
