package com.r2r.road2ring.modules.trip;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.ResponseView;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
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
import lombok.Data;

@Entity
@Table(name = "trip_price")
@Data
public class TripPrice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trip_price_id")
  @JsonView(ResponseView.LimitedTripPrice.class)
  private Integer id;

  @Column(name = "trip_price_start_trip")
  @JsonView(ResponseView.DefaultTripPrice.class)
  private Date startTrip;

  @Column(name = "trip_price_finish_trip")
  @JsonView(ResponseView.DefaultTripPrice.class)
  private Date finishTrip;

  @Column(name = "trip_price_person_paid")
  @JsonView(ResponseView.DefaultTripPrice.class)
  private Integer personPaid;

  @Column(name = "trip_price_price")
  @JsonView(ResponseView.DefaultTripPrice.class)
  private Integer price;

  @Column(name = "trip_price_status")
  @JsonView(ResponseView.DefaultTripPrice.class)
  private TripPriceStatus status;

  @Column(name = "trip_price_discount")
  @JsonView(ResponseView.DefaultTripPrice.class)
  private Integer discount;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "trip_price_trip_id" , nullable = true)
  @JsonView(ResponseView.DetailedTripPrice.class)
  private Trip trip;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "tripPrice")
  @JsonView(ResponseView.DetailedTripPrice.class)
  private List<TripPriceMotor> tripPriceMotorList;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "trip_price_trip_price_detail_id" , nullable = true)
  @JsonView(ResponseView.DetailedTripPrice.class)
  private TripPriceDetail tripPriceDetail;
}
