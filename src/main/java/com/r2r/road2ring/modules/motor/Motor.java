package com.r2r.road2ring.modules.motor;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.PublishedStatus;
import com.r2r.road2ring.modules.common.ResponseView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "motor")
@Data
public class Motor implements Serializable {

  private static final long serialVersionUID = -151788449453504012L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "motor_id")
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private Integer id;

  @Column(name = "motor_title")
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private String title;

  @Column(name = "motor_capacity")
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private Integer capacity;

  @Column(name = "motor_brand")
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private String brand;

  @Column(name = "motor_price")
  private Integer price;

  @Column(name = "motor_discount")
  private Integer discount;

  @Column(name = "motor_description")
  @JsonView(ResponseView.DetailedTripPriceMotor.class)
  private String description;

  @Column(name = "motor_picture")
  @JsonView(ResponseView.DefaultTripPriceMotor.class)
  private String picture;

  @Column(name = "motor_status")
  private PublishedStatus status;

  @Transient
  private Integer availableStock;

  @Transient
  private Integer stocks;
}
