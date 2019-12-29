package com.r2r.road2ring.modules.trip;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.ResponseView;
import com.r2r.road2ring.modules.motor.Motor;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "trip_price_motor")
@Data
public class TripPriceMotor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trip_price_motor_id")
  @JsonView(ResponseView.LimitedTripPriceMotor.class)
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER , optional = true)
  @JoinColumn(name = "trip_price_motor_motor_id" )
  @JsonView(ResponseView.LimitedTripPriceMotor.class)
  private Motor bike;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_price_motor_trip_price_id" )
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private TripPrice tripPrice;

  @Column(name = "trip_price_motor_price")
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private Integer price;

  @Column(name = "trip_price_motor_stock")
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private Integer stock;

  @Column(name = "trip_price_motor_stock_avail", columnDefinition = "integer default 0")
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private Integer stockReserved;

  @CreationTimestamp
  @Column(name="trip_price_motor_created_at")
  @JsonView(ResponseView.DetailedTripPriceMotor.class)
  private Date created;

  @UpdateTimestamp
  @Column(name="trip_price_motor_updated_at")
  @JsonView(ResponseView.DetailedTripPriceMotor.class)
  private Date updated;
}
