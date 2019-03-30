package com.r2r.road2ring.modules.roadcaptain;

import java.util.List;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadCaptainRepository extends DataTablesRepository<RoadCaptain,Integer> {
  List<RoadCaptain> findAll();
  List<RoadCaptain> findTop5ByNameIgnoreCaseContainingOrderByNameAsc(String name);
}
