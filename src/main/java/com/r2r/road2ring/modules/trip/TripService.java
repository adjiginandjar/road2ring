package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.TripFacility.TripFacility;
import com.r2r.road2ring.modules.TripFacility.TripFacilityService;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.hotel.HotelService;
import com.r2r.road2ring.modules.itinerary.Itinerary;
import com.r2r.road2ring.modules.itinerary.ItineraryService;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class TripService {

  TripRepository tripRepository;

  ItineraryService itineraryService;

  TripFacilityService tripFacilityService;

  TripPriceRepository tripPriceRepository;

  HotelService hotelService;

  @Autowired
  public void setTripRepository(TripRepository tripRepository){
    this.tripRepository = tripRepository;
  }

  @Autowired
  public void setItineraryService(ItineraryService itineraryService){
    this.itineraryService = itineraryService;
  }

  @Autowired
  public void setHotelService(HotelService hotelService) {
    this.hotelService = hotelService;
  }

  @Autowired
  public void setTripFacilityService(TripFacilityService tripFacilityService){
    this.tripFacilityService = tripFacilityService;
  }

  @Autowired
  public void setTripPriceRepository(
      TripPriceRepository tripPriceRepository) {
    this.tripPriceRepository = tripPriceRepository;
  }

  public Trip saveTrip(Trip trip){
    Trip saved = new Trip();
    Boolean checkNewTrip = false;
    if(trip.getId() != null && trip.getId() != 0) {
      checkNewTrip = true;
      saved = tripRepository.findOne(trip.getId());
    } else {
      saved.setCreated(new Date());
      saved.setPublishedStatus(TripPublishedStatus.UNPUBLISHED);
    }
    saved.setDescription(trip.getDescription());
    saved.setDistance(trip.getDistance());
    saved.setMaxRider(trip.getMaxRider());
    saved.setIconCover(trip.getIconCover());
    saved.setIconPublisher(trip.getIconPublisher());
    saved.setTerrain(trip.getTerrain());
    saved.setDuration(trip.getDuration());
    saved.setCoverLandscape(trip.getCoverLandscape());
    saved.setCoverPotrait(trip.getCoverPotrait());
    saved.setMap(trip.getMap());


    if(trip.getRoadCaptain() != null && trip.getRoadCaptain().getId() != 0) {
      saved.setRoadCaptain(trip.getRoadCaptain());
    }
    saved.setMeetingPoint(trip.getMeetingPoint());
    saved.setUpdated(new Date());
    saved.setTag(trip.getTag());
    saved.setFacilityNot(trip.getFacilityNot());
    saved.setTitle(trip.getTitle());
    saved.setLocation(trip.getLocation());
    saved = tripRepository.save(saved);

    if( saved != null){
      Trip tripSaved;
      if(checkNewTrip != false){
        tripSaved = tripRepository.findOne(trip.getId());
      }else {
        tripSaved = tripRepository.findByCreated(saved.getCreated());
      }
      tripFacilityService.saveListOfTripFacilityByInteger(trip.getFacilityInclude(),tripSaved);
      hotelService.saveListHotel(trip.getHotels(), tripSaved, trip.getDeletedHotel());

    }
    return saved;
  }

  public List<Trip> getAllTrip(){
    return tripRepository.findAll();
  }

  public DataTablesOutput<Trip> getDatatableContents(DataTablesInput input) {
    DataTablesOutput<Trip> trip = tripRepository.findAll(input);
    return trip;
  }

  public Trip getTripById(int id){
    Trip trip = tripRepository.findOne(id);
    trip.setFacilityInclude(this.getTripFacilityInTrip(trip.getTripFacilities()));
    return trip;
  }

  public List<Itinerary> getTripItinerary(int tripId){
    Trip trips = tripRepository.findOne(tripId);
    List<Itinerary> result = trips.getItineraries();
    return result;
  }

  public List<TripPrice> getTripPriceList(int tripId){
    Trip trips = tripRepository.findOne(tripId);
    List<TripPrice> result = trips.getTripPrices();
    return result;
  }

  private List<Integer> getTripFacilityInTrip(List<TripFacility> tripFacilities){
    List<Integer> result = new ArrayList<>();
    for(TripFacility tripFacility : tripFacilities){
      result.add(tripFacility.getFacilityId());
    }
    return result;
  }

//  public List<Trip> findTripPageable(Integer page, Integer limit) {
//    Pageable pageable = new PageRequest(page, limit);
//    List<Trip> result = tripRepository.findAllByOrderByIdAsc(pageable);
//    return result;
//  }

  public Page<Trip> findTripPageablePage(Integer page, Integer limit) {
    Pageable pageable = new PageRequest(page, limit);
    Page<Trip> result = tripRepository.findAllByPublishedStatusOrderByIdDesc(pageable, TripPublishedStatus.PUBLISHED);
    return result;
  }

  public TripPrice saveTripPrice(int tripId, TripPrice tripPrice){
    TripPrice saved = new TripPrice();

    if(tripPrice.getId() != null && tripPrice.getId() != 0){
      saved = tripPriceRepository.findOne(tripPrice.getId());
    } else {
      saved.setPersonPaid(0);
    }

    saved.setDiscount(tripPrice.getDiscount());
    saved.setFinishTrip(tripPrice.getFinishTrip());
    saved.setStartTrip(tripPrice.getStartTrip());
    saved.setStatus(tripPrice.getStatus());
    saved.setPrice(tripPrice.getPrice());
    saved.setTrip(tripRepository.findOne(tripId));

    return tripPriceRepository.save(saved);
  }

  public TripPrice getTripPriceById(int tripPricId){
    return tripPriceRepository.findOne(tripPricId);
  }

  public List<TripItineraryDataView> getTripItineraryGroup(int tripId){
    List<Object[]> a = tripRepository.groupByItineraryGroup(tripId);
    TripItineraryDataView tripItineraryDataView;
    List<TripItineraryDataView> list = new ArrayList<>();
    int index = 1;
    for(Object[] s : a){
      tripItineraryDataView = new TripItineraryDataView();
      tripItineraryDataView.setId(index);
      tripItineraryDataView.setCountEvent(((BigInteger)s[0]).intValue());
      tripItineraryDataView.setGroupEvent((int)s[1]);
      tripItineraryDataView.setGroupTitleEvent((String)s[2]);
      list.add(tripItineraryDataView);
      index++;
    }
    return list;
  }

  public List<Trip> getListTripByTag(String tag, int tripId){
    List<Trip> trips = new ArrayList<>();
    List<String> tags = new ArrayList<>();
    for(String resultTag  : tag.split(",")){
      tags.add(resultTag);
    }
    tag = tag.replaceAll(",", ",)|(");
    tag = "(" + tag.substring(0, tag.length() - 2);

    trips.addAll(tripRepository.findAllByTagInOrderByIdDesc(tripId,tag));
    return trips;
  }

  /*Change status trip*/
  public void changeTripStatus(TripPublishedStatus statusId, Integer tripId)
      throws Road2RingException {
    Trip saved  = tripRepository.findOne(tripId);
    if(statusId == TripPublishedStatus.PUBLISHED){
      saved.setPublishedStatus(TripPublishedStatus.PUBLISHED);
      tripRepository.save(saved);
    } else if(statusId == TripPublishedStatus.UNPUBLISHED){
      saved.setPublishedStatus(TripPublishedStatus.UNPUBLISHED);
      tripRepository.save(saved);
    } else if(statusId == TripPublishedStatus.EDITED){
      saved.setPublishedStatus(TripPublishedStatus.EDITED);
      tripRepository.save(saved);
    } else {
      throw new Road2RingException("cannot set value", 900);
    }
  }
}
