package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.common.Road2RingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

  public void minPersonTripPrice(Integer tripId, Date startDate){
    TripPrice saved = tripPriceRepository.findOneByTripIdAndStartTrip(tripId,startDate);
    saved.setPersonPaid(saved.getPersonPaid()-1);
    tripPriceRepository.save(saved);
  }

  public TripPrice getTripPrice(Integer tripId, Date startDate){
    return tripPriceRepository.findOneByTripIdAndStartTrip(tripId,startDate);
  }

  public List<TripPrice> getAllTripPriceByTripId(Integer tripId){
    List<TripPrice> result = tripPriceRepository
        .findAllByTripIdAndStatusAndStartTripGreaterThanOrderByStartTripAsc(
            tripId, TripPriceStatus.WAITING, new Date());

    return result;
  }
}
