package com.r2r.road2ring.modules;


import com.r2r.road2ring.modules.consumer.Consumer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

  @GetMapping("")
  public String start(){
    return "admin/page/trip";
  }

  @GetMapping("/login")
  public String login(Model model){
    Consumer consumer = new Consumer();

    //return "admin/page/login";
    return "rtms/form/login";
  }
}
