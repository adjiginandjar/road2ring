package com.r2r.road2ring.modules.transaction;

import com.r2r.road2ring.modules.trip.Trip;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "transaction_detail")
@Data
public class TransactionDetail implements Serializable {

  private static final long serialVersionUID = 2940256787450830772L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "transaction_detail_id")
  private Integer id;

  @Column(name = "transaction_detail_title")
  private String title;

  @Column(name = "transaction_detail_picture")
  private String picture;

  @Column(name = "transaction_detail_discount")
  private Integer discount;

  @Column(name = "transaction_detail_price")
  private Integer price;

  @Column(name = "transaction_detail_size")
  private String size;

  @Column(name = "transaction_detail_type")
  private String type;

  @Column(name = "transaction_detail_quantity")
  private Integer quantity;

  @Transient
  private String brand;

  @Transient
  private String description;

  @Transient
  private Integer capacity;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "transaction_detail_transaction_id" , nullable = true)
  private Transaction transaction;
}
