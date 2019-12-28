package com.r2r.road2ring.modules.trip;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.TripFacility.TripFacility;
import com.r2r.road2ring.modules.common.Language;
import com.r2r.road2ring.modules.common.ResponseView;
import com.r2r.road2ring.modules.itinerary.Itinerary;
import com.r2r.road2ring.modules.roadcaptain.RoadCaptain;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "trip")
@Data
public class Trip implements Serializable {

  private static final long serialVersionUID = 8796958319368475125L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trip_id")
  @JsonView(ResponseView.LimitedTrip.class)
  private Integer id;

  @Column(name = "trip_title")
  @JsonView(ResponseView.LimitedTrip.class)
  private String title;

  @Column(name = "trip_meeting_point")
  @JsonView(ResponseView.LimitedTrip.class)
  private String meetingPoint;

  @Column(name = "trip_description")
  @JsonView(ResponseView.LimitedTrip.class)
  private String description;

  @Column(name = "trip_location")
  @JsonView(ResponseView.DefaultTrip.class)
  private String location;

  @Column(name = "trip_icon_cover")
  @JsonView(ResponseView.DefaultTrip.class)
  private String iconCover;

  @Column(name = "trip_icon_publisher")
  @JsonView(ResponseView.DefaultTrip.class)
  private String iconPublisher;

  @Column(name = "trip_cover_landscape")
  @JsonView(ResponseView.DefaultTrip.class)
  private String coverLandscape;

  @Column(name = "trip_cover_potrait")
  @JsonView(ResponseView.DefaultTrip.class)
  private String coverPotrait;

  @Column(name = "trip_duration")
  @JsonView(ResponseView.DefaultTrip.class)
  private Integer duration;

  @Column(name = "trip_distance")
  @JsonView(ResponseView.DefaultTrip.class)
  private Integer distance;

  @Column(name = "trip_terrain")
  @JsonView(ResponseView.DefaultTrip.class)
  private String terrain;

  @Column(name = "trip_max_rider")
  @JsonView(ResponseView.DefaultTrip.class)
  private Integer maxRider;

  @Column(name = "trip_tag")
  @JsonView(ResponseView.DefaultTrip.class)
  private String tag;

  @Column(name = "trip_map")
  @JsonView(ResponseView.DefaultTrip.class)
  private String map;

  @Column(name = "trip_created")
  @JsonView(ResponseView.DefaultTrip.class)
  private Date created;

  @Column(name = "trip_updated")
  @JsonView(ResponseView.DefaultTrip.class)
  private Date updated;

  @Column(name = "trip_published_status")
  @JsonView(ResponseView.DefaultTrip.class)
  private TripPublishedStatus publishedStatus;

  @Column(name = "trip_language")
  @JsonView(ResponseView.DefaultTrip.class)
  private Language language;

  @Column(name = "trip_related_trip_id")
  @JsonView(ResponseView.DefaultTrip.class)
  private Integer relatedTrip;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "trip_road_captain" , nullable = true)
  @JsonView(ResponseView.DetailedTrip.class)
  private RoadCaptain roadCaptain;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip")
  @JsonView(ResponseView.DetailedTrip.class)
  private List<TripFacility> tripFacilities;

  @Column(name = "trip_facility_not_included")
  @JsonView(ResponseView.DefaultTrip.class)
  private String facilityNot;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip")
  @JsonView(ResponseView.DetailedTrip.class)
  private List<Itinerary> itineraries;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip")
  @JsonView(ResponseView.DetailedTrip.class)
  private List<TripPrice> tripPrices;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip")
  @JsonView(ResponseView.DetailedTrip.class)
  private List<RequestTrip> requestTrips;

  @Transient
  private List<Integer> facilityInclude;

  @Transient
  public List<Itinerary> deletedItinerary;

  @Transient
  private Double midtransFee;

  @Transient
  private Integer virtualAccountFee;

}
