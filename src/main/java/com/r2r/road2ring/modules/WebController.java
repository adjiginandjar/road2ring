package com.r2r.road2ring.modules;


import com.r2r.road2ring.modules.consumer.Consumer;
import com.r2r.road2ring.modules.user.User;
import com.r2r.road2ring.modules.user.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

  @Autowired
  UserService userService;

  @GetMapping("")
  public String start(Principal principal){
    Authentication auth = (Authentication) principal;
    User user = userService.findUserByEmail(auth.getName());
    if(user.getRole().getName().equals("ROLE_ROAD_CAPTAIN"))
      return "redirect:/trip/rtms";

    return "admin/page/trip";
  }

  @GetMapping("/login")
  public String login(Model model){
    Consumer consumer = new Consumer();

    //return "admin/page/login";
    return "rtms/form/login";
  }
}
