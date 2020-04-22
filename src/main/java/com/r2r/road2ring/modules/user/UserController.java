package com.r2r.road2ring.modules.user;

import static com.r2r.road2ring.modules.common.Static.USER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = USER)
public class UserController {

  UserRequestRoleService userRequestRoleService;

  @Autowired
  UserService userService;

  @Autowired
  public void setUserRequestRoleService(
      UserRequestRoleService userRequestRoleService) {
    this.userRequestRoleService = userRequestRoleService;
  }

  @RequestMapping(value = "/request-role", method = RequestMethod.GET)
  public String requestRCList (Model model){
    return "admin/page/userRequestRole";
  }

  @RequestMapping(value = "/captain-list", method = RequestMethod.GET)
  public String captainList (Model model){
    return "admin/page/roadCaptain";
  }

  @RequestMapping(value = "/email-verification", method = RequestMethod.GET)
  public String emailVerification (@RequestParam("verificationCode") String verificationCode,
      Model model){
    String status = "success";
    try {
      userService.verificationEmail(verificationCode);

    } catch (Exception e) {
      status = "failed";
    }
    return "redirect:https://road2ring.com?verificationStatus="+status;
  }
}
