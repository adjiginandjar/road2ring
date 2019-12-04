package com.r2r.road2ring.modules.accessorycategory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.r2r.road2ring.modules.accessory.AccessoryView;
import com.r2r.road2ring.modules.common.BaseView;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class AccessoryCategoryView extends BaseView {

  String categoryName;
  Integer categoryId;
  List<AccessorySubCategoryView> subCategories;
  List<AccessoryView> accessories;
}
