package com.r2r.road2ring.modules.trip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripPriceDetailService {

  @Autowired
  TripPriceDetailRepository tripPriceDetailRepository;


  public TripPriceDetail save(TripPriceDetail tripPriceDetail){
    TripPriceDetail saved = tripPriceDetailRepository.save(tripPriceDetail);
    return saved;
  }

  public TripPriceDetail getDetailPriceById(Integer tripPriceDetailId){
    TripPriceDetail result = tripPriceDetailRepository.findOneById(tripPriceDetailId);
    return result;
  }


}
