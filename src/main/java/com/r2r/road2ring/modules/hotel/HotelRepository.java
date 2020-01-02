package com.r2r.road2ring.modules.hotel;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {


  List<Hotel> findAllByTripId(Integer tripId);
}
