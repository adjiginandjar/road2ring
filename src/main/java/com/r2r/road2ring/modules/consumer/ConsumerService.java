package com.r2r.road2ring.modules.consumer;

import com.r2r.road2ring.modules.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

  private ConsumerRepository consumerRepository;

  @Autowired
  public void setConsumerRepository(ConsumerRepository consumerRepository){
    this.consumerRepository = consumerRepository;
  }

//  public Consumer saveUser(Consumer user){
//    Consumer saved = new Consumer();
//    Role role = new Role();
//    role.setId(user.getRole().getId());
//    saved.setRole(role);
//    saved.setEmail(user.getEmail());
//    saved.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
//    saved.setUsername(user.getUsername());
//
//    return consumerRepository.save(saved);
//  }

  public Consumer getUserByEmail(String email){
    return consumerRepository.findByEmail(email);
  }
}
