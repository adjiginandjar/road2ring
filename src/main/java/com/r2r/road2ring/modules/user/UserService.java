package com.r2r.road2ring.modules.user;

import static com.r2r.road2ring.modules.common.Static.ROLE_ID;

import com.r2r.road2ring.modules.common.R2rTools;
import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.common.Static;
import com.r2r.road2ring.modules.consumer.Consumer;
import com.r2r.road2ring.modules.mail.MailClient;
import com.r2r.road2ring.modules.role.Role;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

  Environment environment;

  UserRepository userRepository;

  R2rTools r2rTools;

  MailClient mailClient;

  @Autowired
  public void setEnvironment(Environment environment){
    this.environment = environment;
  }

  @Autowired
  public void setUserRepository(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  @Autowired
  public void setR2rTools(R2rTools r2rTools){
    this.r2rTools = r2rTools;
  }

  @Autowired
  public void setMailClient(MailClient mailClient){
    this.mailClient = mailClient;
  }

  public AuthToken login(User user, String userAgent) {
    AuthToken authToken = new AuthToken();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<AuthToken> response;

    String base64 = this.createBasicAuthForApplication(userAgent);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + base64);

    String email = user.getEmail();
    String password = user.getPassword();

    /********************* REQUEST CENTRAL BLOCK *******************************************/
    // Sent credential to central (Look at endpoint reset username in consumer controller, it has
    // correlation)

    // if you get central_id, then pass credential to internal oauth

    // otherwise reject
    /**************************** END BLOCK ************************************************/



    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("username", email);
    map.add("password", password);
    map.add("grant_type", "password");
    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<MultiValueMap<String, String>>(map, headers);


    response =
        restTemplate.exchange("http://localhost:"+ environment.getProperty("server.port") + "/oauth/token", HttpMethod.POST, request, AuthToken.class);

    authToken = response.getBody();

    return authToken;
  }

  private String createBasicAuthForApplication(String userAgent) {

    String plainCreds = "", base64Creds = "";
    byte[] plainCredsBytes, base64CredsBytes;

    if (userAgent != null) {
      if (userAgent.startsWith("Android"))
        plainCreds = String.format("%s:%s", environment.getProperty("r2r.android.client.id"),
            environment.getProperty("r2r.android.client.secret"));
      else if (userAgent.startsWith("IOS"))
        plainCreds = String.format("%s:%s", environment.getProperty("r2r.ios.client.id"),
            environment.getProperty("r2r.ios.client.secret"));
      else if (userAgent.startsWith("Web"))
        plainCreds = String.format("%s:%s", environment.getProperty("r2r.web.client.id"),
            environment.getProperty("r2r.web.client.secret"));
    } else {
      plainCreds = String.format("%s:%s", environment.getProperty("r2r.android.client.id"),
          environment.getProperty("r2r.android.client.secret"));
    }
    plainCredsBytes = plainCreds.getBytes();
    base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    base64Creds = new String(base64CredsBytes);

    return base64Creds;
  }

  @Transactional(readOnly = false)
  public User register(User user, User userPicture)
      throws Road2RingException {

    if(!isEmailValid(user.getEmail())){
      throw new Road2RingException("Email invalid", 703);
    }
    if(!isEmailAvailable(user)){
      throw new Road2RingException("Email telah terdaftar.", 708);
    }

    User saved = new User();
    Role role;
    if(user.getRole() == null) {
      role = new Role();
      role.setId(ROLE_ID);
    }else{
      role = user.getRole();
    }

    saved.setRole(role);
    saved.setBirthday( user.getUserBirthday() == null || user.getUserBirthday() == 0 ? null : new Date(user.getUserBirthday()));
    saved.setPassword(r2rTools.hashingPassword(user.getPassword()));

    saved.setEmail(user.getEmail());
    saved.setRegisterDate(new Date());
    saved.setVerificationCode(this.generateVerificationCode());
    saved.setActivation(user.getActivation() == null ? Static.IS_NOT_ACTIVE : Static.IS_ACTIVE);

    saved = userRepository.save(saved);

    /*ADD SERVICE EMAIL VERIFICATION*/
    int index = saved.getEmail().indexOf('@');
    String username = saved.getEmail().substring(0,index);

    //mailClient.sendRegistrationEmail(saved.getEmail(),username,saved.getVerificationCode());

    return saved;
  }

  public String generateVerificationCode(){
    Boolean breaker = true;
    String code;
    do{
      code = r2rTools.generateRandomCode(12);
      User checkVerificationCode = userRepository.findOneByVerificationCode(code);
      if(checkVerificationCode == null)
        breaker = false;
    }while(breaker);

    return code;
  }

  public boolean isEmailValid(String email){
    boolean result = false;
    Pattern pattern = Pattern.compile("(?:[a-zA-Z]+[a-zA-Z0-9._-]+(?:\\.[a-zA-Z0-9._-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    if(!email.isEmpty() && email != null && pattern.matcher(email).matches()){
      result= true;
    }
    return result;
  }

  public boolean isEmailAvailable(User user) {
    return userRepository.findTopOneByEmailIgnoreCase(user.getEmail()) == null;
  }

  public User findUserByEmail(String email){
    return userRepository.findOneByEmailIgnoreCase(email);
  }

  public User saveUserProfile(User user){
    User saved = userRepository.findOneByEmailIgnoreCase(user.getEmail());
    saved.setBirthday(user.getUserBirthday() != 0 ? new Date(user.getUserBirthday()) : saved.getBirthday());
    saved.setDriverLicensePicture(user.getDriverLicensePicture() != null ?
        user.getDriverLicensePicture() : saved.getDriverLicensePicture());
    saved.setDriverLicenseNumber(user.getDriverLicenseNumber() != null ? user.getDriverLicenseNumber() :
        saved.getDriverLicenseNumber());
    saved.setFullName(user.getFullName() != null ? user.getFullName() : saved.getFullName());
    saved.setUserIdentity(user.getUserIdentity() != null ? user.getUserIdentity() :
        saved.getUserIdentity());
    saved.setUserIdentitiyNumber(user.getUserIdentitiyNumber() != null ? user.getUserIdentitiyNumber() :
        saved.getUserIdentitiyNumber());
    saved.setUserIdentityPicture(user.getUserIdentityPicture() != null ?
        user.getUserIdentityPicture() : saved.getUserIdentityPicture());
    saved.setPicture(user.getPicture() != null ?
        user.getPicture() : saved.getPicture());

    saved.setBloodType(user.getBloodType());
    saved.setPhoneNumber(user.getPhoneNumber());
    return userRepository.save(saved);
  }

  public User verificationEmail(User user) throws Exception {
    User saved = userRepository.findOneByVerificationCode(user.getVerificationCode());
    if (saved == null) {
      throw new Road2RingException("Invalid verification code", 800);
    }
    if (saved.getActivation().equals(Static.IS_ACTIVE)) {
      throw new Road2RingException("already verified", 703);
    }
    saved.setActivation(Static.IS_ACTIVE);
    saved.setVerificationCode(null);
    return userRepository.save(saved);
  }

  public User forgotPassword(User user) throws Road2RingException {
    user = userRepository.findOneByEmailIgnoreCase(user.getEmail());

    if (user != null) {
      if(user.getVerificationCodePasswordLastSend() == null) {
        user.setVerificationCodePassword(r2rTools.generateRandomCode(12));
        user.setVerificationCodePasswordLastSend(new Date());
        userRepository.save(user);

        /*SEND EMAIL*/
        int index = user.getEmail().indexOf('@');
        String username = user.getEmail().substring(0,index);

        mailClient.sendForgotPassword(user.getEmail(),username,
            user.getVerificationCodePassword());
      }

    } else {
      throw new Road2RingException("email not found", 702);
    }
    return user;
  }

  public User resetPassword(User user) throws Exception {
    User saved = userRepository
        .findOneByVerificationCodePassword(user.getVerificationCodePassword());

    if (saved == null) {
      throw new Road2RingException("Tautan setel ulang kata sandi sudah tidak berlaku lagi.", 800);
    }
    saved.setPassword(r2rTools.hashingPassword(user.getPassword()));
    saved.setVerificationCodePassword(null);
    saved.setVerificationCodePasswordLastSend(null);
    return userRepository.save(saved);
  }

  public User changeRole(User user, int roleId) {
    User result = userRepository.findOne(user.getId());
    Role role = new Role();
    role.setId(roleId);
    result.setRole(role);

    return userRepository.save(result);

  }

  public List<User> rcList(){
    return userRepository.findAllByRoleId(3);
  }

  public User update(User user) {
    user = userRepository.findOne(user.getId());
    return this.changeRole(user, 2);
  }
}
