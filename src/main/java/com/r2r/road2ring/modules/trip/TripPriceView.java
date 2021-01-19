package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.common.BaseView;
import java.util.Date;
import lombok.Data;

@Data
public class TripPriceView{
  private Integer id;
  private Date startTrip;
  private Date finishTrip;
  private Integer personPaid;
  private Integer maxRiders;
  private Integer price;
  private Integer discount;
}
