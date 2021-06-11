package com.r2r.road2ring.component;

import com.r2r.road2ring.modules.trip.TripPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledComponent {

  @Autowired
  TripPriceService tripPriceService;


  @Scheduled(cron = "0 0 21 * * *")
//  @Scheduled(cron = "* * * * * *")
  public void scheduleTaskUsingCronExpression() {
    tripPriceService.checkExpiredTripPrice();
  }

}
