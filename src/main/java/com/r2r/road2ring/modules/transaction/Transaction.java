package com.r2r.road2ring.modules.transaction;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.accessory.Accessory;
import com.r2r.road2ring.modules.common.PaymentStatus;
import com.r2r.road2ring.modules.common.ResponseView;
import com.r2r.road2ring.modules.common.TripStatus;
import com.r2r.road2ring.modules.motor.Motor;
import com.r2r.road2ring.modules.transactionlog.TransactionCreator;
import com.r2r.road2ring.modules.trip.Trip;
import com.r2r.road2ring.modules.user.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "transaction")
@Data
public class Transaction implements Serializable {

  private static final long serialVersionUID = 6639851039826587565L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "transaction_id")
  @JsonView(ResponseView.LimitedTransaction.class)
  private Integer id;

  @Column(name = "transaction_created")
  @JsonView(ResponseView.DefaultTransaction.class)
  private Date created;

  @Column(name = "transaction_updated")
  @JsonView(ResponseView.DefaultTransaction.class)
  private Date updated;

  @Column(name = "transaction_updated_by")
  @JsonView(ResponseView.DefaultTransaction.class)
  private String updatedBy;

  @Column(name = "transaction_created_by")
  @JsonView(ResponseView.DefaultTransaction.class)
  private String createdBy;

  @Column(name = "transaction_expired_payment")
  @JsonView(ResponseView.DefaultTransaction.class)
  private Date expiredPaymentDate;

  @Column(name = "transaction_complete_payment")
  @JsonView(ResponseView.DefaultTransaction.class)
  private Date completePaymentDate;

  @Column(name = "transaction_notes")
  @JsonView(ResponseView.DefaultTransaction.class)
  private String notes;

  @Column(name = "transaction_payment_status")
  @Enumerated(EnumType.ORDINAL)
  @JsonView(ResponseView.DefaultTransaction.class)
  private PaymentStatus paymentStatus;

  @Column(name = "transaction_trip_status")
  @Enumerated(EnumType.ORDINAL)
  @JsonView(ResponseView.DefaultTransaction.class)
  private TripStatus tripStatus;

  @Column(name = "transaction_code")
  @JsonView(ResponseView.DefaultTransaction.class)
  private String code;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "transaction_trip_id" , nullable = true)
  @JsonView(ResponseView.DetailedTransaction.class)
  private Trip trip;

  @Column(name = "transaction_start_date")
  @JsonView(ResponseView.DefaultTransaction.class)
  @DateTimeFormat(iso= ISO.DATE)
  private Date startDate;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "transaction_user_id" , nullable = true)
  @JsonView(ResponseView.DetailedTransaction.class)
  private User user;

  @Column(name = "transaction_price")
  @JsonView(ResponseView.DefaultTransaction.class)
  private Integer price;

  @Column(name = "transaction_is_cancelled")
  private Boolean isCancelled;

  @Column(name = "transaction_creator")
  private TransactionCreator transactionCreator;

  @Column(name = "transaction_single_room")
  private Boolean singleRoom;

  @Column(name = "transaction_pillion")
  private Boolean pillion;

  @Transient
  private Motor motor;

  @Transient
  private List<Accessory> accessories;

  @Transient
  private Long startTimestamp;

  @Transient
  private Date endDate;

  @Transient
  private Boolean bringOwnMotor;

  @Transient
  private Boolean bringOwnHelm;

}
