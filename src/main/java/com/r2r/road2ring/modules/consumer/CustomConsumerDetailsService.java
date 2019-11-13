package com.r2r.road2ring.modules.consumer;

import com.r2r.road2ring.modules.role.Role;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.r2r.road2ring.modules.user.User;
import com.r2r.road2ring.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Qualifier("customConsumerDetailsService")
public class CustomConsumerDetailsService implements UserDetailsService {

  private UserRepository userRepository;

  @Autowired
  public void setUserRepository(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  @Transactional(readOnly=true)
  @Override
  public UserDetails loadUserByUsername(final String email)
      throws UsernameNotFoundException {
    /*Consumer in SI*/
    User user = userRepository.findOneByEmailIgnoreCase(email);
    List<GrantedAuthority> authorities = buildUserAuthority(user.getRole());

    if(user.getRole().getId() == 2){
      throw new UsernameNotFoundException("user not found");
    }
    return buildUserForAuthentication(user, authorities) ;

  }

  public ConsumerDetail loadUserByConsumer(User consumer)
      throws UsernameNotFoundException {


    List<GrantedAuthority> authorities = buildUserAuthority(consumer.getRole());
    return buildUserForAuthentication(consumer, authorities) ;

  }

  private ConsumerDetail buildUserForAuthentication(User consumer,
      List<GrantedAuthority> authorities) {
    return new ConsumerDetail(consumer.getEmail(), consumer.getPassword(), authorities, consumer);
  }

  private List<GrantedAuthority> buildUserAuthority(Role userRole) {

    Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

    // Build user's authorities
    setAuths.add(new SimpleGrantedAuthority(userRole.getName()));

    return new ArrayList<GrantedAuthority>(setAuths);
  }
}
