package com.r2r.road2ring.modules.accessorycategory;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.PublishedStatus;
import com.r2r.road2ring.modules.common.ResponseView;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "accessory_category")
@Data
public class AccessoryCategory implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "accessory_category_id")
  @JsonView(ResponseView.LimitedCategory.class)
  private Integer id;

  @Column(name = "accessory_category_name")
  @JsonView(ResponseView.DefaultCategory.class)
  private String title;

  @Column(name = "accessory_category_image")
  @JsonView(ResponseView.DetailedCategory.class)
  private String image;

  @Column(name = "accessory_category_status")
  @JsonView(ResponseView.DefaultCategory.class)
  private PublishedStatus status;


}
