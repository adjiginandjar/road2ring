package com.r2r.road2ring.modules.transaction;

import static com.r2r.road2ring.modules.common.Static.M_API;

import com.midtrans.httpclient.error.MidtransError;
import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.user.User;
import com.r2r.road2ring.modules.user.UserService;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = M_API)
@CrossOrigin
public class TransactionMAPIController {

  TransactionService transactionService;

  UserService userService;

  @Autowired
  public void setTransactionService(TransactionService transactionService){
    this.transactionService = transactionService;
  }

  @Autowired
  public void setUserService(UserService userService){
    this.userService = userService;
  }


  @PostMapping("/transaction/create")
  public ResponseMessage createTransaction(
      @ModelAttribute Transaction transaction,Principal principal,
      HttpServletResponse httpStatus){
    ResponseMessage responseMessage = new ResponseMessage();
    if (principal != null) {
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
      User user = userService.findUserByEmail(currentConsumer.getUsername());
      try {
        responseMessage.setObject(transactionService.createTransaction(transaction, user));
        responseMessage.setCode(200);
        httpStatus.setStatus(HttpStatus.OK.value());
        responseMessage.setMessage("Transaction created");
      } catch (Road2RingException e){
        responseMessage.setCode(e.getCode());
        responseMessage.setMessage(e.getMessage());
        httpStatus.setStatus(HttpStatus.FORBIDDEN.value());
      }catch(MidtransError e){
        responseMessage.setCode(555);
        responseMessage.setMessage(e.getMessage());
        httpStatus.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      }
    } else {
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
      responseMessage.setCode(703);
      responseMessage.setMessage("Please login first");
    }
    return responseMessage;
  }

  @GetMapping("/transaction/get-my-transaction/{pageId}/{limit}")
  public ResponseMessage getAllMyTransaction(
      @PathVariable(value = "pageId") Integer pageId,
      @PathVariable(value = "limit") Integer limit ,Principal principal,
      HttpServletResponse httpStatus){
    ResponseMessage responseMessage = new ResponseMessage();
    if (principal != null) {
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
      User user = userService.findUserByEmail(currentConsumer.getUsername());
      try {
        //dummy
//        responseMessage.setObject(transactionService.getAllMyTransaction(user,pageId,limit));
        responseMessage.setObject(transactionService.getDummyData(pageId,limit));
        responseMessage.setCode(200);
      } catch (Road2RingException e) {
        responseMessage.setMessage(e.getMessage());
        responseMessage.setCode(e.getCode());
      }
      httpStatus.setStatus(HttpStatus.OK.value());
    } else {
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
      responseMessage.setCode(703);
      responseMessage.setMessage("Please login first");
    }
    return responseMessage;
  }


  @GetMapping("/transaction/detail/{transactionId}")
  public ResponseMessage getDetailTransaction(
      @PathVariable (value = "transactionId") int transactionId,
      Principal principal, HttpServletResponse httpStatus){
    ResponseMessage responseMessage = new ResponseMessage();
    if(principal != null){
      Authentication auth = (Authentication) principal;
      UserDetails currentConsumer = (UserDetails) auth.getPrincipal();
      User user = userService.findUserByEmail(currentConsumer.getUsername());
      responseMessage.setCode(200);
      responseMessage.setObject(transactionService.getMyTransactionDetail(user,transactionId));
      httpStatus.setStatus(HttpStatus.OK.value());
    } else {
      httpStatus.setStatus(HttpStatus.BAD_REQUEST.value());
      responseMessage.setCode(703);
      responseMessage.setMessage("Please login first");
    }
    return responseMessage;
  }
}
