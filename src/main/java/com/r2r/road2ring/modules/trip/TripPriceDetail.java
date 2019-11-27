package com.r2r.road2ring.modules.trip;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "trip_price_detail")
public class TripPriceDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trip_price_detail_id")
  private Integer id;
  @Column(name = "trip_price_detail_fuel_price")
  private Integer fuelPrice; //total distance/ 20 * harga pertamax
  @Column(name = "trip_price_detail_hotel_price")
  private Integer hotelPrice; //harga hotel *  brerapa lama
  @Column(name = "trip_price_detail_road_captain_price")
  private Integer roadCaptainPrice; // fee rc / minimum orang
  @Column(name = "trip_price_detail_towing_price")
  private Integer towingPrice; //fee towing / min oranng
  @Column(name = "trip_price_detail_food_price")
  private Integer foodPrice; //fee makan * total hari
  @Column(name = "trip_price_detail_company_commission")
  private Integer companyCommission;
}
