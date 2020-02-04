package com.r2r.road2ring.modules.testimonial;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TestimonialViewService {

  public List<TestimonialView> bindListTestimonials(List<Testimonial> testimonials){
    List<TestimonialView> result = new ArrayList<>();
    for(Testimonial testimonial : testimonials){
      result.add(this.bindTestimonial(testimonial));
    }
    return result;
  }

  public TestimonialView bindTestimonial(Testimonial testimonial){
    TestimonialView result = new TestimonialView();

    result.setCoverPotrait(testimonial.getCoverPotrait());
    result.setCoverLandscape(testimonial.getCoverLandscape());
    result.setId(testimonial.getId());
    result.setTitle(testimonial.getTitle());
    return result;
  }

  public TestimonialDetailView bindDetailTestimonial(Testimonial testimonial){
    TestimonialDetailView result = new TestimonialDetailView();

    Calendar cal = Calendar.getInstance();
    cal.setTime(testimonial.getTripDate());
    cal.add(Calendar.DATE, testimonial.getDuration());
    Date endTripDate = cal.getTime();

    result.setCoverLandscape(testimonial.getCoverLandscape());
    result.setDescription(testimonial.getDescription());
    result.setDistance(testimonial.getDistance());
    result.setDuration(testimonial.getDuration());
    result.setCoverPotrait(testimonial.getCoverPotrait());
    result.setIconCover(testimonial.getIconCover());
    result.setId(testimonial.getId());
    result.setTitle(testimonial.getTitle());
    result.setStartTripDate(testimonial.getTripDate());
    result.setEndTripDate(endTripDate);
    result.setTags(testimonial.getTag());
    result.setArticle(testimonial.getArticle());
    result.setIsVideo(testimonial.getIsVideo());
    result.setCaptainName(testimonial.getRoadCaptain().getFullName());
    result.setCaptainPicture(testimonial.getRoadCaptain().getPicture());
    result.setAlbumId(testimonial.getAlbumId());
    return result;
  }

}
