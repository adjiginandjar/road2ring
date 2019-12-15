package com.r2r.road2ring.modules.accessorycategory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class AccessorySubCategoryView {

  private Integer id;
  private String title;
  private String image;

}
