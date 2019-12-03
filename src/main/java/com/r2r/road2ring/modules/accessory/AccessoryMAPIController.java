package com.r2r.road2ring.modules.accessory;

import static com.r2r.road2ring.modules.common.Static.ACCESSORIES;
import static com.r2r.road2ring.modules.common.Static.M_API;

import com.r2r.road2ring.modules.common.ResponseMessage;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = M_API)
public class AccessoryMAPIController {

  AccessoryViewService accessoryViewService;

  @Autowired
  public void setAccessoryViewService(AccessoryViewService accessoryViewService){
    this.accessoryViewService = accessoryViewService;
  }

//  @GetMapping( ACCESSORIES + "/helm")
//  public ResponseMessage getAllHelmet(Principal principal) {
//    ResponseMessage responseMessage = new ResponseMessage();
//    responseMessage.setObject(accessoryViewService.getListAccessories(2));
//    return responseMessage;
//  }

  @GetMapping (ACCESSORIES + "/{titleAccessoryCategory}")
  public ResponseMessage getAccessoryByCategory(Principal principal,
      @PathVariable("titleAccessoryCategory") String title){
    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(accessoryViewService.getListAccessoriesByCategoryName(title));
    return responseMessage;
  }

  @GetMapping (ACCESSORIES + "/detail")
  public ResponseMessage getDetailAccessory(Principal principal,
      @RequestParam("accessoryId") Integer accessoryId){
    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(accessoryViewService.getAccessoryById(accessoryId));
    return responseMessage;
  }


  @GetMapping (ACCESSORIES + "/by-sub-category")
  public ResponseMessage getAccessoryByCategory(Principal principal,
      @RequestParam("subCategoryId") Integer subcategoryId,
  @RequestParam("categoryId") Integer categoryId){
    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(accessoryViewService.getListAccessoriesBySubCategory(subcategoryId,categoryId));
    return responseMessage;
  }

}
