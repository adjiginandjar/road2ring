package com.r2r.road2ring.modules.config;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.ResponseView;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "system_config")
@Data
public class SystemConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "system_config_id")
  private Integer id;

  @Column(name = "system_config_company_fee")
  private Double companyFee;

  @Column(name = "system_config_midtrans_fee")
  private Double midtransFee;

  @Column(name = "system_config_bank_va_fee")
  private Integer bankVAFee;

  @CreationTimestamp
  @Column(name="system_config_created_at")
  @JsonView(ResponseView.DetailedTripPriceMotor.class)
  private Date created;

  @UpdateTimestamp
  @Column(name="system_config_updated_at")
  @JsonView(ResponseView.DetailedTripPriceMotor.class)
  private Date updated;

}
