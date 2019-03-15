package com.r2r.road2ring.modules.trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripFacilityRepository extends JpaRepository<TripFacility, Integer> {

}
