package com.r2r.road2ring.modules.headline;

import static com.r2r.road2ring.modules.common.Static.HEADLINE;
import static com.r2r.road2ring.modules.common.Static.M_API;

import com.r2r.road2ring.modules.common.ResponseMessage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = M_API + HEADLINE)
public class HeadlineMAPIController {

  HeadlineService headlineService;

  @Autowired
  public void setHeadlineService(HeadlineService headlineService){
    this.headlineService = headlineService;
  }

  @GetMapping("")
  public ResponseMessage getHeadline(){
    ResponseMessage responseMessage = new ResponseMessage();
    List<Headline> result = headlineService.getAllHeadline();
    responseMessage.setObject(result);
    return responseMessage;
  }

}
