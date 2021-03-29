package com.r2r.road2ring.modules.midtrans;

import com.midtrans.service.MidtransCoreApi;
import com.r2r.road2ring.modules.transaction.Transaction;
import com.r2r.road2ring.modules.transaction.TransactionDetail;
import com.r2r.road2ring.modules.trip.TripPrice;
import com.r2r.road2ring.modules.user.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MidtransService {


  @Value("${midtrans.server-key}")
  String SERVER_KEY;

  @Value("${midtrans.client-key}")
  String CLIENT_KEY;

  @Value("${midtrans.production-mode}")
  Boolean IS_PRODUCTION;

  @Value("${midtrans.callbacks-url}")
  String CALLBACKS;



  public String checkoutSnapApi(List<String> listPay) throws MidtransError {

    MidtransSnapApi snapApi = new ConfigFactory(new Config(
        SERVER_KEY,CLIENT_KEY
        ,IS_PRODUCTION)).getSnapApi();


    String clientKey = snapApi.apiConfig().getCLIENT_KEY();

    // New Map Object for JSON raw request body
    Map<String, Object> requestBody = new HashMap<>();

    // Add enablePayment from @RequestParam to dataMockup
    List<String> paymentList = new ArrayList<>();
    if (listPay != null) {
      paymentList.addAll(listPay);
    }

    // PutAll data mockUp to requestBody
    requestBody.putAll(this.buildRequestBody());

    return  snapApi.createTransactionToken(requestBody);
  }

  public String checkoutTrip(List<TransactionDetail> accessories, TransactionDetail motor,
      Transaction transaction, TripPrice tripPrice, User user) throws MidtransError{

    MidtransSnapApi snapApi = new ConfigFactory(new Config(
        SERVER_KEY,CLIENT_KEY
        ,IS_PRODUCTION)).getSnapApi();

    MidtransCoreApi coreApi = new ConfigFactory(new Config(
        SERVER_KEY,CLIENT_KEY
        ,IS_PRODUCTION)).getCoreApi();

    Map<String, Object> requestBody = new LinkedHashMap<>();

    Map<String,String> transactionDetails = this.buildTransactionDetails(transaction);
    List<Map<String, String>> itemDetails = this.buildItemDetails(motor,accessories,tripPrice,transaction);
    Map<String,Object> customerDetails = this.buildCustomerDetails(user);
    List<String> listedPayment = this.buildListPayment();
    Map<String,String> bcaVA = this.buildBCAVirtualAccount();
    Map<String,String> briVA = this.buildBRIVirtualAccount();
    Map<String,String> creditCard = this.buildCreditCard();
    Map<String,String> expiry = this.buildExpire();
    Map<String,String> callbacks = this.buildCallbacks(transaction.getId());

    requestBody.put("transaction_details", transactionDetails);
    requestBody.put("item_details", itemDetails);
    requestBody.put("customer_details", customerDetails);
    requestBody.put("enabled_payments", listedPayment);
//    requestBody.put("credit_card", creditCard);
    requestBody.put("bca_va", bcaVA);
    requestBody.put("bri_va", briVA);
    requestBody.put("expiry", expiry);
    requestBody.put("callbacks", callbacks);

    return snapApi.createTransactionToken(requestBody);
  }

  private Map<String, String> buildCallbacks(Integer transactionId) {
    Map<String, String> callbacks = new HashMap<>();


    callbacks.put("finish",CALLBACKS+transactionId);

    return callbacks;

  }

  private Map<String, String> buildExpire() {
    Map<String, String> expiry = new HashMap<>();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

    expiry.put("start_time",sdf.format(new Date()));
    expiry.put("unit", "hour");
    expiry.put("duration", "2");

    return expiry;
  }

  private Map<String, String> buildCreditCard() {
    Map<String, String> creditCard = new HashMap<>();

    creditCard.put("secure", "true");

    return creditCard;
  }

  private Map<String, String> buildBCAVirtualAccount() {
    Map<String, String> bcaVirtualAccount = new HashMap<>();

    bcaVirtualAccount.put("va_number",this.generateRandomVACode(11));
    bcaVirtualAccount.put("sub_company_code","39465");

    return bcaVirtualAccount;
  }
  private Map<String, String> buildBRIVirtualAccount() {
    Map<String, String> briVirtualAccount = new HashMap<>();

    briVirtualAccount.put("va_number",this.generateRandomVACode(11));
    return briVirtualAccount;
  }

  private List<String> buildListPayment() {
    List<String> listPayment = new ArrayList<>();
//    listPayment.add("credit_card");
//    listPayment.add("bca_va");
    listPayment.add("bri_va");
    listPayment.add("bank_transfer");
    listPayment.add("gopay");

    return listPayment;
  }

  private Map<String, String> buildTransactionDetails(Transaction transaction) {
    Map<String, String> transactionDetails = new HashMap<>();
    transactionDetails.put("order_id", transaction.getCode());
    transactionDetails.put("gross_amount", transaction.getPrice().toString());

    return transactionDetails;
  }

  private Map<String,Object> buildCustomerDetails(User user){
    Map<String, Object> custDetail = new HashMap<>();
    custDetail.put("first_name", user.getFullName());
    custDetail.put("email", user.getEmail());
    custDetail.put("phone", user.getPhoneNumber());

    return custDetail;
  }

  private List<Map<String, String>> buildItemDetails(TransactionDetail motor,
      List<TransactionDetail> accessories,TripPrice tripPrice,Transaction transaction) {
    List<Map<String, String>> itemDetails = new ArrayList<>();

    itemDetails.add(this.buildTripItem(transaction,tripPrice));

//    if(transaction.getPillion()){
//      itemDetails.addAll(this.buildPiliionItem(tripPrice));
//    }
//    if(transaction.getSingleRoom()){
//      itemDetails.add(this.buildSingleRoomItem(tripPrice));
//    }

    if(motor != null){
      itemDetails.add(this.buildItem(motor));
    }
    if(accessories != null) {
      for (TransactionDetail accesorry : accessories
      ) {
        itemDetails.add(this.buildItem(accesorry));
      }
    }

    return itemDetails;
  }

  private Map<String, String> buildTripItem(Transaction transaction, TripPrice tripPrice) {
    Map<String, String> item = new HashMap<>();

    item.put("name",tripPrice.getTrip().getTitle());
    item.put("brand","Road2Ring");
    item.put("category","Trip");
    item.put("quantity","1");
    item.put("price",tripPrice.getPrice().toString());

    return item;
  }

  private List<Map<String, String>> buildPiliionItem(TripPrice tripPrice) {
    List<Map<String, String>> items = new ArrayList<>();
    Map<String, String> item = new HashMap<>();

    item.put("name","Additional Passenger Food");
    item.put("brand","Road2Ring");
    item.put("category","Pillion");
    item.put("quantity","1");
    item.put("price",tripPrice.getTripPriceDetail().getFoodPrice().toString());

    items.add(item);

    item.put("name","Additional Passenger Hotel");
    item.put("brand","Road2Ring");
    item.put("category","Pillion");
    item.put("quantity","1");
    item.put("price",tripPrice.getTripPriceDetail().getHotelPrice().toString());

    items.add(item);

    return items;
  }
  private Map<String, String> buildSingleRoomItem(TripPrice tripPrice) {
    Map<String, String> item = new HashMap<>();

    item.put("name","Single Hotel Room");
    item.put("brand","Road2Ring");
    item.put("category","Single Hotel");
    item.put("quantity","1");
    item.put("price",tripPrice.getTripPriceDetail().getHotelPrice().toString());

    return item;
  }

  private Map<String, String> buildItem (TransactionDetail detailItem){
    Map<String, String> item = new HashMap<>();

    item.put("name",detailItem.getTitle());
    item.put("brand",detailItem.getBrand());
    item.put("category",detailItem.getType());
    item.put("quantity",detailItem.getQuantity().toString());
    item.put("price",detailItem.getPrice().toString() );

    return item;

  }

  private Map<String,Object> buildRequestBody(){
    UUID idRand = UUID.randomUUID();
    Map<String, Object> params = new HashMap<>();

    Map<String, String> transactionDetails = new HashMap<>();
    transactionDetails.put("order_id", idRand.toString());
    transactionDetails.put("gross_amount", "265000");


    Map<String, String> creditCard = new HashMap<>();
    creditCard.put("secure", "true");


    params.put("transaction_details", transactionDetails);
    params.put("credit_card", creditCard);

    return params;
  }

  private String generateRandomVACode(int length) {
    String key = RandomStringUtils
        .random(length, "1205914478272463760852021448462".toCharArray());

    return key;
  }



}
