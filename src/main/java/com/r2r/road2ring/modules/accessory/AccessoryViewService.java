package com.r2r.road2ring.modules.accessory;

import com.r2r.road2ring.modules.accessorycategory.AccessoryCategory;
import com.r2r.road2ring.modules.accessorycategory.AccessoryCategoryService;
import com.r2r.road2ring.modules.accessorycategory.AccessoryCategoryView;
import com.r2r.road2ring.modules.accessorycategory.AccessorySubCategory;
import com.r2r.road2ring.modules.accessorycategory.AccessorySubCategoryService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessoryViewService {

  AccessoryService accessoryService;

  AccessoryCategoryService accessoryCategoryService;

  @Autowired
  AccessorySubCategoryService accessorySubCategoryService;

  @Autowired
  public void setAccessoryService(AccessoryService accessoryService){
    this.accessoryService = accessoryService;
  }

  @Autowired
  public void setAccessoryCategoryService(AccessoryCategoryService accessoryCategoryService){
    this.accessoryCategoryService = accessoryCategoryService;
  }

  public AccessoryView getAccessoryById(Integer accessoryId){
    Accessory result = accessoryService.getAccessoryById(accessoryId);
    return this.bindAccessoryView(result);
  }

  public AccessoryView bindAccessoryView(Accessory accessory){
    AccessoryView accessoryView = new AccessoryView();
    accessoryView.setId(accessory.getId());
    accessoryView.setTitle(accessory.getTitle());
    accessoryView.setDescription(accessory.getDescription());
    accessoryView.setDiscount(accessory.getDiscount());
    accessoryView.setPicture(accessory.getPicture());
    accessoryView.setPrice(accessory.getPrice());
    return accessoryView;
  }

  public List<AccessoryView> bindListAccessoryView(List<Accessory> accessories){
    List<AccessoryView> accessoryViews = new ArrayList<>();
    for(Accessory accessory : accessories){
      accessoryViews.add(this.bindAccessoryView(accessory));
    }
    return accessoryViews;
  }

  public List<AccessoryView> getListAccessories(Integer id){
    List<Accessory> accessories = accessoryService.getAllAccessoryByCategory(id);
    List<AccessoryView> result = this.bindListAccessoryView(accessories);
    return result;
  }

  public List<AccessoryView> getListAccessoriesByCategoryName(String title){
    Integer accessoryCategoryId = accessoryCategoryService.getAccessoryCategoryByTitle(title).getId();
    List<Accessory> accessories = accessoryService.getAllAccessoryByCategory(accessoryCategoryId);
    List<AccessoryView> result = this.bindListAccessoryView(accessories);
    return result;
  }

  public AccessoryCategoryView getListAccessoriesBySubCategory(Integer accessorySubCategoryId,Integer categoryId){
    AccessoryCategoryView result = new AccessoryCategoryView();
    List<Accessory> accessories = new ArrayList<Accessory>();
    if(accessorySubCategoryId != 0) {
      AccessorySubCategory subCategory = accessorySubCategoryService
          .getAccessorySubCategroryById(accessorySubCategoryId);
      accessories = accessoryService
          .getAllAccessoryBySubCategory(accessorySubCategoryId);
      result.setCategoryName(subCategory.getTitle());
    }else{
      accessories = accessoryService.getAllAccessoryByCategory(categoryId);
    }

    result.setAccessories(this.bindListAccessoryView(accessories));
    return result;
  }



}
