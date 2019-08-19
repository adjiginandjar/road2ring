package com.r2r.road2ring.modules.TripMotor;

import com.r2r.road2ring.modules.motor.Motor;
import com.r2r.road2ring.modules.trip.Trip;
import com.r2r.road2ring.modules.trip.TripPrice;
import java.util.Date;
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

@Entity
@Table(name="trip_motor")
@Data
public class TripMotor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="trip_motor_id")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_motor_trip_price_id", nullable = false)
  private TripPrice tripPrice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_motor_motor_id", nullable = false)
  private Motor motor;

  @Column(name="trip_motor_stock")
  private Integer stock;

  @Column(name="trip_motor_price")
  private Integer price;

  @CreationTimestamp
  @Column(name="user_request_rc_created_at")
  private Date created;
  @UpdateTimestamp
  @Column(name="user_request_rc_updated_at")
  private Date updated;



}
