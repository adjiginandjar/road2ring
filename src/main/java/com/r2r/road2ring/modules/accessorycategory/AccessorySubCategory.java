package com.r2r.road2ring.modules.accessorycategory;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.PublishedStatus;
import com.r2r.road2ring.modules.common.ResponseView;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "accessory_sub_category")
@Data
public class AccessorySubCategory {

  public AccessorySubCategory() {
  }

  public AccessorySubCategory(Integer id, String title, String image,
      PublishedStatus status) {
    this.id = id;
    this.title = title;
    this.image = image;
    this.status = status;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "accessory_sub_category_id")
  @JsonView(ResponseView.LimitedSubCategory.class)
  private Integer id;

  @Column(name = "accessory_sub_category_name")
  @JsonView(ResponseView.DefaultSubCategory.class)
  private String title;

  @Column(name = "accessory_sub_category_image")
  @JsonView(ResponseView.DefaultSubCategory.class)
  private String image;

  @Column(name = "accessory_sub_category_status")
  @JsonView(ResponseView.DefaultSubCategory.class)
  private PublishedStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "accessory_sub_category_category_id", nullable = false)
  @JsonView(ResponseView.DetailedSubCategory.class)
  private AccessoryCategory accessoryCategory;
}
