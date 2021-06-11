package com.r2r.road2ring.modules.transaction;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, Integer> {
  List<TransactionDetail> findAllByTransactionIdOrderByIdDesc(int transactionId);

  TransactionDetail findOneByTransactionIdAndTypeIgnoreCase(int transactionId,String type);
}
