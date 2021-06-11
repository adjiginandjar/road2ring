package com.r2r.road2ring.modules.transaction;

import com.r2r.road2ring.modules.common.ResponseMessage;
import com.r2r.road2ring.modules.transactionlog.TransactionLog;
import com.r2r.road2ring.modules.transactionlog.TransactionLogService;
import com.r2r.road2ring.modules.trip.Trip;
import com.r2r.road2ring.modules.trip.TripPrice;
import com.r2r.road2ring.modules.trip.TripPriceService;
import com.r2r.road2ring.modules.trip.TripPriceStatus;
import com.r2r.road2ring.modules.trip.TripService;
import com.r2r.road2ring.modules.user.User;
import com.r2r.road2ring.modules.user.UserService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/transaction")
public class TransactionController {

  TransactionService transactionService;

  TransactionLogService transactionLogService;

  TransactionDetailService transactionDetailService;

  TripService tripService;

  UserService userService;

  @Autowired
  TripPriceService tripPriceService;

  @Autowired
  public void setTransactionService(
      TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @Autowired
  public void setTransactionLogService(
      TransactionLogService transactionLogService) {
    this.transactionLogService = transactionLogService;
  }

  @Autowired
  public void setTransactionDetailService(
      TransactionDetailService transactionDetailService) {
    this.transactionDetailService = transactionDetailService;
  }

  @Autowired
  public void setTripService(
      TripService tripService) {
    this.tripService = tripService;
  }

  @Autowired
  public void setUserService(UserService userService){
    this.userService = userService;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String index(Model model) {
    ResponseMessage response = new ResponseMessage();
    model.addAttribute("response", response);
    return "admin/page/transaction";
  }

  @RequestMapping(value = "/edit", method = RequestMethod.GET)
  public String edit(Model model, @RequestParam int id) {
    ResponseMessage response = new ResponseMessage();
    TransactionConfirmationDetailView transaction = transactionService.getTransactionDetailView(id);
    List<TransactionLog> log = transactionLogService.getAllLogbyTrxID(id);
    List<TransactionDetail> detail = transactionDetailService.getAllDetailByTrxId(id);
    response.setObject(transaction);
    model.addAttribute("response", response);
    model.addAttribute("log", log);
    model.addAttribute("detail", detail);
    return "admin/forms/transaction";
  }

  @RequestMapping(value = "/rtms/list", method = RequestMethod.GET)
  public String list(Model model,@RequestParam(value = "page",required = false)Integer page,
      Principal principal) {

    if(page == null){
      page = 0;
    }else{
      page = page-1;
    }

    Authentication auth = (Authentication) principal;
    User user = userService.findUserByEmail(auth.getName());
    ResponseMessage response = new ResponseMessage();

    List<TripPrice> trips = tripPriceService.getListUpcommingTripByUser(user);
    response.setObject(trips);
    model.addAttribute("response", response);
    model.addAttribute("tripStatus", "waiting");
    return "rtms/page/transaction";
  }

  @RequestMapping(value = "/list/complete", method = RequestMethod.GET)
  public String listComplete(Model model,@RequestParam(value = "page",required = false)Integer page,
      Principal principal) {

    if(page == null){
      page = 0;
    }else{
      page = page-1;
    }

    Authentication auth = (Authentication) principal;
    User user = userService.findUserByEmail(auth.getName());
    ResponseMessage response = new ResponseMessage();

    List<TripPrice> trips = tripPriceService.getListScheduleTriExcludeWaitingByUser(user);

    response.setObject(trips);
    model.addAttribute("response", response);
    model.addAttribute("tripStatus", "complete");

    return "rtms/page/transaction";
  }

  @RequestMapping(value = "/detail", method = RequestMethod.GET)
  public String transactionDetail(Model model,
      @RequestParam(value = "id",required = false)Integer tripPriceId, Principal principal) {

    Authentication auth = (Authentication) principal;
    User user = userService.findUserByEmail(auth.getName());
    ResponseMessage response = new ResponseMessage();
    Integer totalPrice;

    TripPrice tripPrice = tripPriceService.getOneTripPrice(tripPriceId);
    List<Transaction> transactions  = transactionService.getTransactionByTripPrice(tripPrice);
    totalPrice = transactions.stream()
        .mapToInt(x -> x.getPrice()).sum();

    response.setObject(transactions);
    model.addAttribute("response", response);
    model.addAttribute("tripPrice", tripPrice);
    model.addAttribute("totalPrice", totalPrice);
    return "rtms/page/transactionDetail";
  }

  @RequestMapping(value="/cancel", method = RequestMethod.GET)
  public String cancelTransaction(Model model,
      @RequestParam(value="id",required = false) Integer tripPriceId, Principal principal){

    Authentication auth = (Authentication) principal;
    User user = userService.findUserByEmail(auth.getName());
    ResponseMessage response = new ResponseMessage();
    Integer totalPrice;

    TripPrice tripPrice = tripPriceService.changeStatusTripPrice(tripPriceId, TripPriceStatus.CANCEL);

    return "redirect:/transaction/rtms/list";
  }

}
