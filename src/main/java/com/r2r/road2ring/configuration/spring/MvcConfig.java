package com.r2r.road2ring.configuration.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

//  @Override
//  public void addViewControllers(ViewControllerRegistry registry){
//    registry.addViewController("/si/login").setViewName("/si/login");
//  }


  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    super.addResourceHandlers(registry);
    registry.addResourceHandler("src/main/resources/static/img/assets/").addResourceLocations("file:src/main/resources/static/img/assets/");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*");
  }

}