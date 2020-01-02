package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.TripFacility.TripFacility;
import com.r2r.road2ring.modules.hotel.HotelDataView;
import java.util.List;
import lombok.Data;

@Data
public class TripViewDetail extends TripView {
  private Integer distance;
  private String terrain;
  private Integer maxRider;
  private String iconCover;
  private String map;
  private String description;
  private String imageRoadCaptain;
  private String roadCaptainName;
  private String roadCaptainDescription;
  private String facilityNotIncluded;
  private List<TripViewFacility> facilities;
  private String meetingPoint;
  private List<TripViewItinerary> itineraries;
  private List<HotelDataView> hotels;
}
