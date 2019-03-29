package com.r2r.road2ring.modules.facility;

import com.r2r.road2ring.modules.common.ResponseMessage;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/facility")
public class FacilityController {

  FacilityService facilityService;

  @Autowired
  public void setFacilityService(FacilityService facilityService){
    this.facilityService = facilityService;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String index(Model model) {
    ResponseMessage response = new ResponseMessage();
    model.addAttribute("response", response);
    return "admin/page/facility";
  }

  @RequestMapping(value = "/add", method = RequestMethod.GET)
  public String add(Model model) {
    ResponseMessage response = new ResponseMessage();
    Facility facility = new Facility();
    response.setObject(facility);
    model.addAttribute("response", response);
    return "admin/forms/facility";
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(@ModelAttribute Facility facility, Model model, Principal principal) {
    ResponseMessage response = new ResponseMessage();
    response.setObject(facilityService.saveFacility(facility));
    model.addAttribute("response", response);
    return "redirect:/home";
  }

}