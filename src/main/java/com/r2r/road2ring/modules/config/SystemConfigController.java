package com.r2r.road2ring.modules.config;

import com.r2r.road2ring.modules.common.ResponseMessage;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/config")
public class SystemConfigController {

  @Autowired
  SystemConfigService systemConfigService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String index(Model model) {
    ResponseMessage response = new ResponseMessage();
    response.setObject(systemConfigService.getSystemConfig());
    model.addAttribute("response", response);
    return "admin/forms/config";
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(@ModelAttribute SystemConfig systemConfig, Model model, Principal principal) {
    ResponseMessage response = new ResponseMessage();
    response.setObject(systemConfigService.save(systemConfig));
    model.addAttribute("response", response);
    return "redirect:/config";
  }

}
