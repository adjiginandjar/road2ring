package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.TripFacility.TripFacility;
import com.r2r.road2ring.modules.itinerary.ItineraryService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

@Service
public class TripService {

  TripRepository tripRepository;

  ItineraryService itineraryService;

  @Autowired
  public void setTripRepository(TripRepository tripRepository){
    this.tripRepository = tripRepository;
  }

  @Autowired
  public void setItineraryService(ItineraryService itineraryService){
    this.itineraryService = itineraryService;
  }

  public Trip saveTrip(Trip trip){
    Trip saved = new Trip();
    saved.setDescription(trip.getDescription());
    saved.setDistance(trip.getDistance());
    saved.setMaxRider(trip.getMaxRider());
    saved.setTerrain(trip.getTerrain());
    saved.setDuration(trip.getDuration());
    saved.setCreated(new Date());
    saved.setMeetingPoint(trip.getMeetingPoint());
    saved.setUpdated(new Date());
    saved.setTag(trip.getTag());
    saved.setTitle(trip.getTitle());
    saved.setLocation(trip.getLocation());
    saved = tripRepository.save(saved);
//    if( saved != null){
//      itineraryService.saveListOfItinerary(trip.getItineraries(),saved, trip.getGroupTitle());
//    }
    return saved;
  }

  public List<Trip> getAllTrip(){
    return tripRepository.findAll();
  }

  public DataTablesOutput<Trip> getDatatableContents(DataTablesInput input) {
    DataTablesOutput<Trip> trip = tripRepository.findAll(input);
    return trip;
  }

  public List<TripFacility> getTripFacility(int tripId){
    Trip trips = tripRepository.findOne(tripId);
    TripFacility tripFacility;
    List<TripFacility> result = new ArrayList<TripFacility>();
    result = trips.getTripFacilities();
    return result;
  }

  public List<Itinerary> getTripItinerary(int tripId){
    Trip trips = tripRepository.findOne(tripId);
    TripFacility tripFacility;
    List<Itinerary> result = new ArrayList<Itinerary>();
    result = trips.getItineraries();
    return result;
  }
}
