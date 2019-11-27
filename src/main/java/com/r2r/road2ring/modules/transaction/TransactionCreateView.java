package com.r2r.road2ring.modules.transaction;

import java.util.Date;
import lombok.Data;

@Data
public class TransactionCreateView {
  private Integer totalPrice;
  private Date lastPayment;
  private String transactionCodeId;
  private String midtransToken;
}
