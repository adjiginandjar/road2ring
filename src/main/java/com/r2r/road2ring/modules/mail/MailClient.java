package com.r2r.road2ring.modules.mail;


import com.r2r.road2ring.modules.accessory.Accessory;
import com.r2r.road2ring.modules.accessory.AccessoryRepository;
import com.r2r.road2ring.modules.transaction.Transaction;
import com.r2r.road2ring.modules.transaction.TransactionRepository;
import com.r2r.road2ring.modules.trip.TripPrice;
import com.r2r.road2ring.modules.trip.TripPriceMotor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailClient {

  private JavaMailSender mailSender;

  AccessoryRepository accessoryRepository;

  TransactionRepository transactionRepository;

  @Autowired
  private TemplateEngine templateEngine;

  @Autowired
  public Environment env;

  @Autowired
  public MailClient(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Autowired
  public void setAccessoryRepository(AccessoryRepository accessoryRepository){
    this.accessoryRepository = accessoryRepository;
  }

  @Autowired
  public void setTransactionRepository(TransactionRepository transactionRepository){
    this.transactionRepository = transactionRepository;
  }

  public void prepareAndSend(String recipient, String messages) throws MessagingException {
    //TODO implement
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    Map model = new HashMap();
    model.put("message", "Naik motor si besi Tua");

    Context context = new Context();
    context.setVariables(model);
    String html = templateEngine.process("admin/email/checkout", context);

    helper.setFrom("admin@road2ring.com");
    helper.setText(html, true);
    helper.setSubject("Sample mail subject");
    helper.setTo("paulmartensimanjuntak19@gmail.com");

//    mailSender.send(message);

    try {
      mailSender.send(message);
    } catch (MailException e) {
      // runtime exception; compiler will not force you to handle it
    }
  }

  public void sendPaidEmail(String recipient, String consumerName, int ridersNeeded) throws MessagingException {
    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
      message.setTo(recipient);
      message.setFrom(new InternetAddress("admin@road2ring.com"));
      message.setSubject("Paid Transaction Road2Ring");

      Map model = new HashMap();
      model.put("consumerName", consumerName);
      model.put("ridersNeeded", ridersNeeded);

      Context context = new Context();
      context.setVariables(model);
      String html = templateEngine.process("admin/email/paid", context);

      message.setText(html, true);
    };
    mailSender.send(messagePreparator);
  }

  public void sendEmail(String recipient, String consumerName, int ridersNeeded) {

    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
      message.setTo(recipient);
      message.setFrom(new InternetAddress("admin@road2ring.com"));
      message.setSubject("Paid Transaction Road2Ring");

      Map model = new HashMap();
      model.put("consumerName", consumerName);
      model.put("ridersNeeded", ridersNeeded);

      Context context = new Context();
      context.setVariables(model);
      String html = templateEngine.process("admin/email/paid", context);

      message.setText(html, true);

//      if (attachmentPath != null) {
//        message.addAttachment(attachmentName, attachmentPath);
//      }
    };

    mailSender.send(messagePreparator);

//    if (attachmentPath != null) {
//      attachmentPath.delete();
//    }
  }

  public void sendCheckoutEmail(String recipient, String consumerName, Transaction transaction,
      TripPriceMotor motor, List<Accessory> accessories, TripPrice tripPrice,
      boolean bringOwnHelm, boolean bringOwnMotor) {

    Transaction resultTransaction = transactionRepository.findOne(transaction.getTrip().getId());

    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
      message.setTo(recipient);
      message.setFrom(new InternetAddress("admin@road2ring.com"));

      message.setSubject("Congratulation for your transaction");
      Map model = new HashMap();
      model.put("username", consumerName);
      model.put("image",  resultTransaction.getTrip().getCoverLandscape());
      model.put("iconCover", resultTransaction.getTrip().getIconCover());
      model.put("startDate", tripPrice.getStartTrip());
      model.put("endDate", tripPrice.getFinishTrip());
      model.put("meetingPoint", resultTransaction.getTrip().getMeetingPoint());
      if(!bringOwnMotor) {
        model.put("motorName", motor.getBike().getTitle());
        model.put("motorImage", motor.getBike().getPicture());
      } else if(bringOwnMotor){
        model.put("motorName", "Bring Own Motor");
        model.put("motorImage", "");
      }
      model.put("expiredDate", transaction.getExpiredPaymentDate());
      model.put("tripTitle", resultTransaction.getTrip().getTitle());
      model.put("tripPrice", tripPrice.getPrice());
      model.put("invoiceId", transaction.getCode().toUpperCase());
      model.put("expiredPaymentDate", transaction.getExpiredPaymentDate());

//      if(!bringOwnHelm) {
//        for (int i = 0; i < accessories.size(); i++) {
//          Accessory accessory = accessoryRepository.findOne(accessories.get(i).getId());
//          if (accessory.getAccessoryCategory().getTitle().toLowerCase().equalsIgnoreCase("helm")) {
//            model.put("accossoriesTitle", accessory.getTitle());
//            model.put("accossoriesImage",  accessory.getPicture());
//            model.put("accessoriesSize", accessories.get(0).getSize());
//          }
//        }
//      } else if(bringOwnHelm){
//        model.put("accossoriesTitle", "Bring Own Helm");
//        model.put("accossoriesImage", "");
//        model.put("accessoriesSize", "-");
//      }
      Context context = new Context();
      context.setVariables(model);
      String html = templateEngine.process("admin/email/checkout", context);

      message.setText(html, true);
//      if (attachmentPath != null) {
//      if (attachmentPath != null) {
//      if (attachmentPath != null) {
//        message.addAttachment(attachmentName, attachmentPath);
//      }
    };
    mailSender.send(messagePreparator);
//    if (attachmentPath != null) {
//      attachmentPath.delete();
//    }
  }

  public void sendRegistrationEmail(String recipient, String consumerName, String codeVerify) {

    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
      message.setTo(recipient);
      message.setFrom(new InternetAddress("admin@road2ring.com"));
      message.setSubject("Welcome to RANSTOURING");

      Map model = new HashMap();
      model.put("consumerName", consumerName);

      /*CODE VERIFYNYA HARUS DIUBAH LINK*/
      /*CHANGE LINK FOR REAL LINK SOON*/
      model.put("codeVerify",env.getProperty("application.base-url")
          +"verification/"+codeVerify);

      Context context = new Context();
      context.setVariables(model);
      String html = templateEngine.process("admin/email/register", context);

      message.setText(html, true);
    };

    mailSender.send(messagePreparator);
  }

  public void sendForgotPassword(String recipient, String consumerName, String codeResetPassword) {

    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
      message.setTo(recipient);
      message.setFrom(new InternetAddress("admin@road2ring.com"));
      message.setSubject("Reset Password On Road2Ring");

      Map model = new HashMap();
      model.put("consumerName", consumerName);

      /*CODE VERIFYNYA HARUS DIUBAH LINK*/
      model.put("codeResetPassword", env.getProperty("application.base-url")
          +"reset-password?code="+codeResetPassword);

      Context context = new Context();
      context.setVariables(model);
      String html = templateEngine.process("admin/email/forgot-password", context);

      message.setText(html, true);
    };

    mailSender.send(messagePreparator);
  }

  public void sentEmailCompleteTrip(String recipient, String location){
    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
      message.setTo(recipient);
      message.setFrom(new InternetAddress("admin@road2ring.com"));
      message.setSubject("Be Ready For Your Touring");

      Map model = new HashMap();
      model.put("location", location);

      Context context = new Context();
      context.setVariables(model);
      String html = templateEngine.process("admin/email/complete-trip", context);

      message.setText(html, true);
    };

    mailSender.send(messagePreparator);
  }
}
