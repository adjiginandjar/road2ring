package com.r2r.road2ring.modules.accessorycategory;

import static com.r2r.road2ring.modules.common.Static.M_API;

import com.r2r.road2ring.modules.common.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = M_API)
public class AccessoryCategoryMAPIController {
  AccessoryCategoryService accessoryCategoryService;

  @Autowired
  AccessorySubCategoryService accessorySubCategoryService;

  @Autowired
  public void setAccessoryCategoryService(AccessoryCategoryService accessoryCategoryService){
    this.accessoryCategoryService = accessoryCategoryService;
  }

  @GetMapping("/accessory-categories")
  public ResponseMessage getAccessoryCatagory(){
    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(accessoryCategoryService.getAccessoryCategoriesIdNotContaionHelm());
    return responseMessage;
  }

  @GetMapping("/accessory-categories/{accessoryCategoryId}/accessory-sub-categories")
  public ResponseMessage getAccessorySubCatagory(
      @PathVariable("accessoryCategoryId") Integer accessoryCategoryId){
    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(accessorySubCategoryService.getSubCategoryByCategory(accessoryCategoryId));
    return responseMessage;
  }
}
