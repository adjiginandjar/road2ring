package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.common.Road2RingException;
import com.r2r.road2ring.modules.user.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TripPriceService {

  TripPriceRepository tripPriceRepository;

  @Autowired
  public void setTripPriceRepository(TripPriceRepository tripPriceRepository){
    this.tripPriceRepository = tripPriceRepository;
  }

  public TripPriceView bindTripPriceView(TripPrice tripPrice){
    TripPriceView tripPriceView = new TripPriceView();
    tripPriceView.setId(tripPrice.getId());
    tripPriceView.setPersonPaid(tripPrice.getPersonPaid());
    tripPriceView.setMaxRiders(tripPrice.getTrip().getMaxRider());
    tripPriceView.setStartTrip(tripPrice.getStartTrip());
    tripPriceView.setFinishTrip(tripPrice.getFinishTrip());
    tripPriceView.setDiscount(tripPrice.getDiscount());
    tripPriceView.setPrice(tripPrice.getPrice());
    return tripPriceView;
  }

  public List<TripPriceView> bindListTripPriceView(Integer tripId) throws Road2RingException {
    List<TripPriceView> tripPriceViews = new ArrayList<>();
    List<TripPrice> tripPrices = tripPriceRepository.
        findAllByTripIdAndStatusAndStartTripGreaterThanOrderByStartTripAsc(
            tripId, TripPriceStatus.WAITING, new Date());
    if(tripPrices.size() != 0){
      for(TripPrice tripPrice : tripPrices){
        if(tripPrice.getPersonPaid() < tripPrice.getTrip().getMaxRider())
          tripPriceViews.add(this.bindTripPriceView(tripPrice));
      }
    } else {
      throw new Road2RingException("not found", 200);
    }
    return tripPriceViews;
  }

  public TripPrice addPersonTripPrice(Integer tripId, Date startDate){
    TripPrice saved = tripPriceRepository.findOneByTripIdAndStartTrip(tripId,startDate);
    saved.setPersonPaid(saved.getPersonPaid()+1);
    tripPriceRepository.save(saved);
    return saved;
  }

  public TripPrice minPersonTripPrice(Integer tripId, Date startDate){
    TripPrice saved = tripPriceRepository.findOneByTripIdAndStartTrip(tripId,startDate);
    saved.setPersonPaid(saved.getPersonPaid()-1);
    return tripPriceRepository.save(saved);
  }

  public TripPrice getTripPrice(Integer tripId, Date startDate){
    return tripPriceRepository.findOneByTripIdAndStartTrip(tripId,startDate);
  }

  public TripPrice getOneTripPrice(Integer tripPriceId){
    return tripPriceRepository.findOne(tripPriceId);
  }

  public TripPrice deleteTripPrice(Integer tripPriceId){

    return this.changeStatusTripPrice(tripPriceId,TripPriceStatus.DELETE);

  }

  public TripPrice changeStatusTripPrice(Integer tripPriceId,TripPriceStatus status){
    TripPrice tripPrice = tripPriceRepository.findOne(tripPriceId);
    tripPrice.setStatus(status);

    tripPriceRepository.save(tripPrice);

    return tripPrice;

  }

  public void checkExpiredTripPrice(){
    Date now = new Date();
    Calendar nextDay = Calendar.getInstance();
    nextDay.setTime(now);
    nextDay.set(Calendar.HOUR_OF_DAY,0);
    nextDay.set(Calendar.MINUTE,0);
    nextDay.set(Calendar.SECOND,0);

    nextDay.add(Calendar.DATE,1);

    List<TripPrice> tripPrices = tripPriceRepository
        .findAllByStartTripLessThanEqualAndStatus(nextDay.getTime(),TripPriceStatus.WAITING);

    for (TripPrice tripPrice :
        tripPrices) {
      if(tripPrice.getPersonPaid() == 0) {
        changeStatusTripPrice(tripPrice.getId(), TripPriceStatus.EXPIRED);
      }else{
        changeStatusTripPrice(tripPrice.getId(),TripPriceStatus.COMPLETE);
      }
    }
  }


  public List<TripPrice> getListUpcommingTripByUser(User roadCaptain){
    List<TripPrice> tripPrices = tripPriceRepository.
        findAllByTripRoadCaptainIdAndTripPublishedStatusAndStartTripGreaterThanAndStatus(
            roadCaptain.getId(),TripPublishedStatus.PUBLISHED,new Date(),TripPriceStatus.WAITING);

    return tripPrices;
  }

  public List<TripPrice> getListScheduleTriExcludeWaitingByUser(User roadCaptain){
    List<TripPrice> tripPrices = tripPriceRepository.
        findAllByTripRoadCaptainIdAndTripPublishedStatusAndAndStatusIsNot(
            roadCaptain.getId(),TripPublishedStatus.PUBLISHED,TripPriceStatus.WAITING);

    return tripPrices;
  }
}
