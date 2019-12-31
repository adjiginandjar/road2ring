package com.r2r.road2ring.modules.testimonial;

import java.util.Date;
import lombok.Data;

@Data
public class TestimonialDetailView extends TestimonialView {
  private int duration;
  private int distance;
  private String description;
  private String coverLandscape;
  private String tags;
  private String article;
  private Boolean isVideo;
  private String captainName;
  private String captainPicture;
  private String iconCover;
  private Date startTripDate;
  private Date endTripDate;
  private Integer albumId;
}
