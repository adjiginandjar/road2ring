package com.r2r.road2ring.modules.TripMotor;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripMotorRepository extends JpaRepository<TripMotor,Integer> {

  List<TripMotor> findAllByTripPriceIdIn(List<Integer> tripPriceIds);
  List<TripMotor> findAllByTripPriceIdInAndStockGreaterThan(List<Integer> tripPriceIds,Integer stock);
  List<TripMotor> findAllByTripPriceIdAndStockGreaterThan(Integer tripId,Integer stock);

}
