package com.r2r.road2ring.modules.user;

import com.google.common.base.Splitter;
import com.r2r.road2ring.modules.role.Role;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("r2rUserDetailsService")
@Transactional(readOnly = true)
public class r2rUserDetailsService implements UserDetailsService {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  UserRepository userRepository;

  @Autowired
  public void setUserRepository(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
    int i = 0;
    String via = "", userId = "";
    com.r2r.road2ring.modules.user.User consumer;
    List<GrantedAuthority> authorities;
    UserDetails user;

    System.out.println("username = " + username);

    Iterable<String> result = Splitter.on("+via+").trimResults().omitEmptyStrings().split(username);
    for (String s : result) {
      if (i == 0) {
        userId = s;
      } else {
        via = s;
      }
      i++;
    }

    try {

      consumer = userRepository.findOneByEmailIgnoreCase(userId);

      if(consumer.getRole().getId() == 1){
        return null;
      }

      authorities = buildUserAuthority(consumer.getRole());

      if(consumer.getSocialProvider() !=null){
        user = new User(consumer.getEmail(), consumer.getSocialPassword(), authorities);
      }else{
        user = new User(consumer.getEmail(), consumer.getPassword(), authorities);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new UsernameNotFoundException("Error in retrieving user");
    }

    return user;
  }

  private List<GrantedAuthority> buildUserAuthority(Role userRole) {

    Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

    // Build user's authorities
    setAuths.add(new SimpleGrantedAuthority(userRole.getName()));

    return new ArrayList<GrantedAuthority>(setAuths);
  }
}
