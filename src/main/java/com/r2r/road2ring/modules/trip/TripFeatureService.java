package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.common.PublishedStatus;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

@Service
public class TripFeatureService {

  @Autowired
  TripFeatureRepository tripFeatureRepository;

  public TripFeature save(TripFeature tripFeature){
    TripFeature saved = new TripFeature();
    if(tripFeature.getId() != null && tripFeature.getId() != 0){
      saved = tripFeatureRepository.findOne(tripFeature.getId());
    }else{
      saved.setPublishStatus(PublishedStatus.UNPUBLISHED);
    }

    saved.setTitle(tripFeature.getTitle());
    saved.setIcon(tripFeature.getIcon());
    saved.setLink(tripFeature.getLink());
    saved.setCover(tripFeature.getCover());

    return tripFeatureRepository.save(saved);
  }

  public List<TripFeature> getTripFeature(){
    return tripFeatureRepository.findAllByPublishStatusOrderByUpdatedDesc(PublishedStatus.PUBLISHED);
  }

  public TripFeature updatePublishStatus(TripFeature updated){
    updated = tripFeatureRepository.findOne(updated.getId());
    if(updated.getPublishStatus().equals(PublishedStatus.PUBLISHED)){
      updated.setPublishStatus(PublishedStatus.UNPUBLISHED);
    }else{
      updated.setPublishStatus(PublishedStatus.PUBLISHED);
    }
    return save(updated);
  }

  public List<TripFeature> getDatatableContents() {
    return tripFeatureRepository.findAll();
  }

  public TripFeature getTripFeatureById(int id) {
    return tripFeatureRepository.findOne(id);
  }
}
