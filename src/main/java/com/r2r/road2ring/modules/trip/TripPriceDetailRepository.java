package com.r2r.road2ring.modules.trip;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripPriceDetailRepository extends DataTablesRepository<TripPriceDetail,Integer> {

  TripPriceDetail findOneById(Integer id);

}
