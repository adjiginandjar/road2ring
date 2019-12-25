package com.r2r.road2ring.modules.hotel;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.ResponseView;
import com.r2r.road2ring.modules.trip.Trip;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="hotel")
@Data
public class Hotel implements Serializable {
    private static final long serialVersionUID = 8512311425439756664L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Integer id;

    @Column(name = "hotel_name")
    private String nama;

    @Column(name = "hotel_address")
    private String alamat;

    @Column(name = "hotel_pciture")
    private String picture;

    @Column(name = "hotel_bintang")
    private Integer Star;

    @ManyToOne(fetch = FetchType.LAZY , optional = true)
    @JoinColumn(name = "hotel_trip_id" , nullable = true)
    private Trip trip;

}
