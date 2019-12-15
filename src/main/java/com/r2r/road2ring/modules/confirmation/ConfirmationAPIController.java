package com.r2r.road2ring.modules.confirmation;

import static com.r2r.road2ring.modules.common.Static.CONFIRMATION;
import static com.r2r.road2ring.modules.common.Static.M_API;

import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.common.UploadService;
import com.r2r.road2ring.modules.user.User;
import com.r2r.road2ring.modules.user.UserService;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = M_API)
public class ConfirmationAPIController {

  UserService userService;

  ConfirmationService confirmationService;

  UploadService uploadService;

  @Autowired
  public void setUserService(UserService userService){
    this.userService = userService;
  }

  @Autowired
  public void setConfirmationService(ConfirmationService confirmationService){
    this.confirmationService = confirmationService;
  }

  @Autowired
  public void setUploadService(UploadService uploadService){
    this.uploadService = uploadService;
  }

  @PostMapping(CONFIRMATION)
  public ResponseMessage saveConfirmation(
      @ModelAttribute Confirmation confirmation,
      @RequestParam(value = "file", required = false) MultipartFile file,
      Principal principal, HttpServletResponse httpStatus)
      throws IOException, FileSizeLimitExceededException {
    ResponseMessage responseMessage = new ResponseMessage();
    if (principal != null) {
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
      User user = userService.findUserByEmail(currentConsumer.getUsername());
      String picture;
      if(file != null) {
        picture = uploadService.uploadImagePicture(file, "jpeg");
        confirmation.setPicture(picture);
      }
      try {
        responseMessage.setObject(confirmationService.saveConfirmation(confirmation));
      } catch (Road2RingException e) {
        e.printStackTrace();
        responseMessage.setCode(e.getCode());
        responseMessage.setMessage(e.getMessage());
      }
    }
    return responseMessage;
  }

}
