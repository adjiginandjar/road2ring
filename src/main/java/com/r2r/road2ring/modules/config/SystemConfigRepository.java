package com.r2r.road2ring.modules.config;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigRepository extends DataTablesRepository<SystemConfig,Integer> {



}
