package com.r2r.road2ring.modules.user;

import static com.r2r.road2ring.modules.common.Static.API;
import static com.r2r.road2ring.modules.common.Static.M_API;
import static com.r2r.road2ring.modules.common.Static.USER;

import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.common.UploadService;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = {M_API + USER, API + USER})
@CrossOrigin
public class UserAPIController {

  UserService userService;

  UploadService uploadService;

  UserViewService userViewService;

  @Autowired
  UserRequestRoleService userRequestRoleService;

  @Autowired
  public void setUserService(UserService userService){
    this.userService = userService;
  }

  @Autowired
  public void setUploadService(UploadService uploadService){
    this.uploadService = uploadService;
  }

  @Autowired
  public void setUserViewService(UserViewService userViewService){
    this.userViewService = userViewService;
  }

  @RequestMapping(value = "/auth/basic", method = RequestMethod.POST)
  public ResponseMessage login(
      @RequestHeader(value = "X-User-Agent", required = false) String userAgent,
      @ModelAttribute User user, HttpServletRequest request,
      HttpServletResponse httpResponse) {

    ResponseMessage responseMessage = new ResponseMessage();
    try {
      responseMessage.setObject(userService.login(user, userAgent));
      httpResponse.setStatus(HttpStatus.OK.value());
    } catch (Exception e) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      responseMessage.setCode(800);
      responseMessage.setMessage("Email atau Password anda salah");
    }

    return responseMessage;

  }

  @RequestMapping(value = "/auth/social", method = RequestMethod.POST)
  public ResponseMessage loginSocial(
      @RequestHeader(value = "X-User-Agent", required = false) String userAgent,
      @ModelAttribute User user, HttpServletRequest request,
      HttpServletResponse httpResponse) {

    ResponseMessage responseMessage = new ResponseMessage();
    try {
      responseMessage.setObject(userService.loginSocial(user.getSocialToken(),userAgent));
      httpResponse.setStatus(HttpStatus.OK.value());
    } catch (Exception e) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      responseMessage.setCode(800);
      responseMessage.setMessage("Email atau Password anda salah");
    }

    return responseMessage;

  }

  @RequestMapping(value = "/registration", method = RequestMethod.POST)
  public ResponseMessage registerRefresh(
      @RequestHeader(value = "X-User-Agent", required = false) String userAgent,
      @ModelAttribute(value = "user") User user, HttpServletRequest request,
      HttpServletResponse httpStatus) {
    ResponseMessage response = new ResponseMessage();
    try {
      if (user.getEmail() != null) {
        userService.register(user, user);
        httpStatus.setStatus(HttpStatus.OK.value());
      } else {
        httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setCode(703);
        response.setMessage("Missing email or username parameter");
      }
    } catch (DataIntegrityViolationException e) {
      httpStatus.setStatus(HttpStatus.CONFLICT.value());
      response.setCode(703);
      response.setMessage("Email or username duplicate");
    }catch (Road2RingException e) {
      httpStatus.setStatus(HttpStatus.CONFLICT.value());
      response.setCode(e.getCode());
      response.setMessage(e.getMessage());
    }

    return response;
  }

  @GetMapping(value = "/profile")
  public ResponseMessage getProfileUser(Principal principal,
      HttpServletResponse httpStatus){
    ResponseMessage response = new ResponseMessage();
    if(principal != null ){
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
      User user = userService.findUserByEmail(currentConsumer.getUsername());
      response.setCode(200);
      response.setObject(userViewService.bindUserViewDetail(user));
      httpStatus.setStatus(HttpStatus.OK.value());
    }else {
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setCode(703);
      response.setMessage("Please login first");
    }
    return response;
  }

  @PostMapping(value = "/profile")
  public ResponseMessage saveProfileUser(@ModelAttribute (value = "user") User user,
  Principal principal, HttpServletResponse httpStatus,
  @RequestParam(value = "useridentityPicture", required = false) MultipartFile userIdentitiy,
  @RequestParam(value = "driverlicensePicture", required = false) MultipartFile driverLicense,
  @RequestParam(value = "userPicture", required = false) MultipartFile pictureUser)
      throws IOException, FileSizeLimitExceededException {
    ResponseMessage responseMessage = new ResponseMessage();
    if(principal != null ){
      String userIdentityUrl;
      String driverLicenseUrl;
      String pictureUserUrl;
      if(userIdentitiy != null) {
        userIdentityUrl = uploadService.uploadImagePicture(userIdentitiy, "jpeg");
        user.setUserIdentityPicture(userIdentityUrl);
      }
      if(driverLicense != null){
        driverLicenseUrl = uploadService.uploadImagePicture(driverLicense, "jpeg");
        user.setDriverLicensePicture(driverLicenseUrl);
      }
      if(pictureUser != null){
        pictureUserUrl = uploadService.uploadImagePicture(pictureUser, "jpeg");
        user.setPicture(pictureUserUrl);
      }
      responseMessage.setCode(200);
      responseMessage.setObject(userViewService.bindUserViewDetail(userService.saveUserProfile(user)));
      httpStatus.setStatus(HttpStatus.OK.value());
    } else {
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
      responseMessage.setCode(703);
      responseMessage.setMessage("Please login first");
    }
    return responseMessage;
  }

  @RequestMapping(value = "/verification/code", method = RequestMethod.POST)
  public ResponseMessage verificationEmail(@ModelAttribute User user,
      HttpServletResponse httpStatus) {
    ResponseMessage response = new ResponseMessage();
    try {
      response.setMessage("Yey, Your account has been verified");
      userService.verificationEmail(user);
      httpStatus.setStatus(HttpStatus.OK.value());
    } catch (Road2RingException e) {
      response.setCode(e.getCode());
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
      e.printStackTrace();
    } catch (Exception e) {
      response.setCode(800);
      e.printStackTrace();
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }

    return response;
  }

  @PostMapping(value = "/forgot-password")
  public ResponseMessage forgotPassword(@ModelAttribute User user,
      HttpServletResponse httpStatus){
    ResponseMessage response  = new ResponseMessage();
    try{
     userService.forgotPassword(user);
    }catch (Road2RingException e) {
      response.setMessage(e.getMessage());
      response.setCode(e.getCode());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }catch (Exception e){
      response.setCode(800);
      e.printStackTrace();
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    return response;
  }

  @PostMapping(value = "/reset-password")
  public ResponseMessage resetPassword(@ModelAttribute User user,
      HttpServletResponse httpStatus){
    ResponseMessage response  = new ResponseMessage();
    try{
      userService.resetPassword(user);
    }catch (Road2RingException e){
      response.setCode(800);
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }catch (Exception e){
      response.setCode(800);
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    return response;
  }

  @RequestMapping(value = "/captain/helper", method = RequestMethod.GET)
  public ResponseMessage helperPublished(@RequestParam("keyword") String keyword) {

    ResponseMessage responseMessage = new ResponseMessage();

    List<User> user = userService.getRcAutocomplete(keyword);
    List<UserViewDetail> userViewDetailList = new ArrayList<>();

    for(User u : user){
      userViewDetailList.add(userViewService.bindUserViewDetail(u));
    }

    responseMessage.setObject(userViewDetailList);

    return responseMessage;

  }

  /**
   * RC Request
   */

  @PostMapping(value="/request-rc")
  public ResponseMessage requestRc(Principal principal,
      HttpServletResponse httpStatus){
    ResponseMessage response = new ResponseMessage();
    try{
      if(principal != null ){
        Authentication auth = (Authentication) principal;
        UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
        User user = userService.findUserByEmail(currentConsumer.getUsername());

        response.setObject(userRequestRoleService.save(user));
      }
    }catch (Road2RingException e){
      response.setCode(800);
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }catch (Exception e){
      response.setCode(800);
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    return response;

  }

  @PostMapping(value="/approve-rc")
  public ResponseMessage approveRoadCaptain(@ModelAttribute UserRequestRole userRequestRole,
      HttpServletResponse httpStatus){
    ResponseMessage response = new ResponseMessage();
    try{
      userRequestRoleService.update(userRequestRole);
    }catch (Exception e){
      response.setCode(800);
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    return response;
  }

  @PostMapping(value="/remove-rc")
  public ResponseMessage removeRoadCaptain(@ModelAttribute User user,
      HttpServletResponse httpStatus){
    ResponseMessage response = new ResponseMessage();
    try{
      userService.update(user);
    }catch (Exception e){
      response.setCode(800);
      response.setMessage(e.getMessage());
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    return response;
  }

  @RequestMapping(value = "/request-rc/data", method = RequestMethod.GET)
  public List<UserRequestRoleView> datatable(
      HttpServletRequest request) {

    return userRequestRoleService.bindUserRequestRole(userRequestRoleService.getAllRequest());
  }

  @RequestMapping(value = "/rc/data", method = RequestMethod.GET)
  public List<UserViewDetail> dataRole(
      HttpServletRequest request) {

    List<User> user = userService.rcList();
    List<UserViewDetail> userViewDetailList = new ArrayList<>();

    for(User u : user){
      userViewDetailList.add(userViewService.bindUserViewDetail(u));
    }

    return userViewDetailList;
  }

}
