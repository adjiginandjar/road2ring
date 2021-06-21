package com.r2r.road2ring.modules.user;

import static com.r2r.road2ring.modules.common.Static.USER;

import com.r2r.road2ring.modules.common.R2rTools;
import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.trip.Trip;
import java.security.Principal;
import java.util.Map;
import org.codehaus.jackson.node.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
  R2rTools r2rTools;

  @Autowired
  public void setUserRequestRoleService(
      UserRequestRoleService userRequestRoleService) {
    this.userRequestRoleService = userRequestRoleService;
  }

  @RequestMapping(value = "/list-all", method = RequestMethod.GET)
  public String listUser (Model model){
    return "admin/page/userList";
  }

  @RequestMapping(value = "/request-role", method = RequestMethod.GET)
  public String requestRCList (Model model){
    return "admin/page/userRequestRole";
  }

  @RequestMapping(value = "/captain-list", method = RequestMethod.GET)
  public String captainList (Model model){
    return "admin/page/roadCaptain";
  }

  @RequestMapping(value = "/profile/rtms", method = RequestMethod.GET)
  public String editProfile (Model model, Principal principal) {

     Authentication auth = (Authentication) principal;
     User user = userService.findUserByEmail(auth.getName());

     ResponseMessage response = new ResponseMessage();
     response.setObject(user);

     model.addAttribute("response", response);
    return "rtms/form/editProfile";
  }
  @RequestMapping(value = "/profile/rtms/submit", method = RequestMethod.POST)
  public String saveProfile (@ModelAttribute User user ,Model model, Principal principal) {

    Authentication auth = (Authentication) principal;
    User userLogin = userService.findUserByEmail(auth.getName());

    user.setEmail(userLogin.getEmail());

    userService.saveUserProfileCMS(user);

    return "redirect:/user/profile/rtms";
  }

  @RequestMapping(value = "/profile/rtms/edit-password", method = RequestMethod.GET)
  public String changePassword (Model model,Principal principal){

    Authentication auth = (Authentication) principal;
    User userLogin = userService.findUserByEmail(auth.getName());
    ResponseMessage response = new ResponseMessage();
    response.setObject(userLogin);

    model.addAttribute("response", response);

    return "rtms/form/changePassword";
  }

  @RequestMapping(value = "/profile/rtms/edit-password", method = RequestMethod.POST)
  public String changePasswordSubmit (@ModelAttribute User user,
      Model model,Principal principal){


    Authentication auth = (Authentication) principal;
    User userLogin = userService.findUserByEmail(auth.getName());

    if(r2rTools.comparePassword(userLogin.getPassword(),user.getOldPassword())) {
      userService.updatePassword(user);
    }else{
      return "redirect:/user/profile/rtms/edit-password?error=oldPassword-not-match";
    }

    return "redirect:/user/profile/rtms";
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
