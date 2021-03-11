package com.r2r.road2ring.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Order(5)
public class WebRoad2RoadSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  UserDetailsService customConsumerDetailsService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http
        .headers()
        .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy","script-src 'self'"))
        .and()
        .antMatcher("/**").authorizeRequests()
        .antMatchers("/user/email-verification").permitAll()
          .antMatchers("/assets/**").permitAll()
          .antMatchers("/css/**").permitAll()
          .antMatchers("/script/**").permitAll()
          .antMatchers("/bootstrap/**").permitAll()
          .antMatchers("/dist/**").permitAll()
          .antMatchers("/plugins/**").permitAll()
          .antMatchers("/img/**").permitAll()
          .antMatchers("/rtms/assets/**").permitAll()
          .antMatchers("/rtms/css/**").permitAll()
          .antMatchers("/rtms/js/**").permitAll()
          .antMatchers("/**").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_ROAD_CAPTAIN")
          .and()

        .formLogin()
          .loginPage("/login")
          .defaultSuccessUrl("/trip")
          .permitAll()
          .and()
        .logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .permitAll();
  }

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth.userDetailsService(customConsumerDetailsService).passwordEncoder(new BCryptPasswordEncoder());
  }
}
