package com.r2r.road2ring.modules.transaction;

import com.r2r.road2ring.modules.common.PaymentStatus;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends DataTablesRepository<Transaction, Integer> {
  List<Transaction> findAll();
  Transaction findOneByCode(String code);
  List<Transaction> findAllByPaymentStatusAndExpiredPaymentDateLessThan(PaymentStatus paymentStatus, Date paydate);
  List<Transaction> findAllByUserIdOrderByCreatedDesc(Integer userId, Pageable pageable);
  Transaction findOneByIdAndUserId(int id, int userId);
  List<Transaction> findAllByPaymentStatusAndStartDate(PaymentStatus paymentStatus, Date startDate);

  List<Transaction> findAllByStartDateAndTripId(Date startDate,Integer tripId);
}
