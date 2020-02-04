package com.r2r.road2ring.modules.testimonial;

import com.fasterxml.jackson.annotation.JsonView;
import com.r2r.road2ring.modules.common.PublishedStatus;
import com.r2r.road2ring.modules.common.ResponseView;
import com.r2r.road2ring.modules.user.User;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "testimonial")
@Data
public class Testimonial implements Serializable {

  private static final long serialVersionUID = -7657630545857549758L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "testimonial_id")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private Integer id;

  @Column(name = "testimonial_title")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private String title;

  @Column(name = "testimonial_is_video")
  private Boolean isVideo;

  @Column(name = "testimonial_cover_landscape")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private String coverLandscape;

  @Column(name = "testimonial_cover_potrait")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private String coverPotrait;

  @Column(name = "testimonial_icon_cover")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private String iconCover;

  @Column(name = "testimonial_trip_date")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private Date tripDate;

  @Column(name = "testimonial_description")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private String description;

  @Column(name = "testimonial_tag")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private String tag;

  @Column(name = "testimonial_article")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private String article;

  @ManyToOne(fetch = FetchType.LAZY , optional = true)
  @JoinColumn(name = "testimonial_trip_road_captain" , nullable = true)
  @JsonView(ResponseView.DefaultTestimonial.class)
  private User roadCaptain;

  @Column(name = "testimonial_trip_distance")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private Integer distance;

  @Column(name = "testimonial_trip_duration")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private Integer duration;

  @Column(name = "testimonial_status")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private PublishedStatus publishedStatus;

  @Column(name = "testimonial_album_id")
  @JsonView(ResponseView.LimitedTestimonial.class)
  private Integer albumId;
}
