package com.r2r.road2ring.modules.midtrans;


import static com.r2r.road2ring.modules.common.Static.M_API;
import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;

import com.r2r.road2ring.modules.common.ResponseMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = M_API)
public class MidtransMAPIController {


  @Autowired
  MidtransService midtransService;

  @RequestMapping(value = "snap/check-out", method = RequestMethod.POST)
  public ResponseMessage checkout(@RequestParam(value = "enablePay", required = false) List<String> listPay) throws MidtransError {
    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setObject(midtransService.checkoutSnapApi(listPay));

    return responseMessage;

  }

  @PostMapping(value = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
  private ResponseEntity<String> handleNotification(@RequestBody Map<String, Object> response) {
    String notifResponse = null;
    if (!(response.isEmpty())) {
      String orderId = (String) response.get("order_id");
      String transactionStatus = (String) response.get("transaction_status");
      String fraudStatus = (String) response.get("fraud_status");

      notifResponse = "Transaction notification received. Order ID: "+orderId+". Transaction status: "+transactionStatus+". Fraud status: "+fraudStatus;
      System.out.println(notifResponse);

      if (fraudStatus.equals("capture")) {
        if (fraudStatus.equals("challenge")) {
          // TODO set transaction status on your database to 'challenge'
          System.out.println("MidtransMAPIController.handleNotification Challenge");
        } else if (fraudStatus.equals("accept")){
          // TODO set transaction status on your database to 'success'
          System.out.println("MidtransMAPIController.handleNotification Success");
        }
      } else if (transactionStatus.equals("cancel") || transactionStatus.equals("deny") || transactionStatus.equals("expire")) {
        // TODO set transaction status on your database to 'failure'
        System.out.println("MidtransMAPIController.handleNotification Fail");
      } else if (transactionStatus.equals("pending")) {
        // TODO set transaction status on your database to 'pending' / waiting payment
        System.out.println("MidtransMAPIController.handleNotification Pending");
      }
    }
    return new ResponseEntity<>(notifResponse, HttpStatus.OK);
  }


}
