package com.r2r.road2ring.modules.accessory;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.accessorycategory.AccessoryCategory;
import com.r2r.road2ring.modules.accessorycategory.AccessorySubCategory;
import com.r2r.road2ring.modules.common.PublishedStatus;
import com.r2r.road2ring.modules.common.ResponseView;
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
@Table(name = "accessory")
@Data
public class Accessory implements Serializable {

  private static final long serialVersionUID = -7526773254128628085L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "accessory_id")
  @JsonView(ResponseView.LimitedAccessory.class)
  private Integer id;

  @Column(name = "accessory_title")
  @JsonView(ResponseView.DefaultAccessory.class)
  private String title;

  @Column(name = "accessory_price")
  @JsonView(ResponseView.DefaultAccessory.class)
  private Integer price;

  @Column(name = "accessory_discount")
  @JsonView(ResponseView.DefaultAccessory.class)
  private Integer discount;

  @Column(name = "accessory_stocks")
  @JsonView(ResponseView.DefaultAccessory.class)
  private Integer stock;

  @Column(name = "accessory_picture")
  @JsonView(ResponseView.DefaultAccessory.class)
  private String picture;

  @Column(name = "accessory_description")
  @JsonView(ResponseView.DefaultAccessory.class)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "accessory_accessory_category" , nullable = true)
  @JsonView(ResponseView.DetailedAccessory.class)
  private AccessoryCategory accessoryCategory;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "accessory_accessory_sub_category" , nullable = true)
  @JsonView(ResponseView.DetailedAccessory.class)
  private AccessorySubCategory accessorySubCategory;

  @Column(name = "accessory_status")
  @JsonView(ResponseView.DefaultAccessory.class)
  private PublishedStatus status;

  @Transient
  private String size;

  @Transient
  private Integer quantity;
}
