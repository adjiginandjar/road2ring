package com.r2r.road2ring.modules.common;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

public class ResponseView {

  /**
   * @apiDefine Base
   * @apiSuccess {Integer} code
   * @apiSuccess {String} message
   */
  public interface Base extends DataTablesOutput.View{}

  /*TRIP JSON*/
  public interface LimitedTrip extends Base{}
  public interface DefaultTrip extends LimitedTrip{}
  public interface DetailedTrip extends DefaultTrip,DefaultItinerary, DefaultTripFacility, DefaultTripPrice{}

  /*FACILITY JSON*/
  public interface LimitedTripFacility extends Base{}
  public interface DefaultTripFacility extends LimitedTripFacility{}
  public interface DetailedTripFacility extends DefaultTripFacility{}

  /*CONSUMER JSON*/
  public interface LimitedConsumer extends Base{}
  public interface DefaultConsumer extends LimitedConsumer{}
  public interface DetailedConsumer extends DefaultConsumer{}

  /*ITINERARY JSON*/
  public interface LimitedItinerary extends Base{}
  public interface DefaultItinerary extends LimitedItinerary{}
  public interface DetailedItinerary extends DefaultItinerary{}

  /*ROAD CAPTAIN JSON*/
  public interface LimitedRoadCaptain extends Base{}
  public interface DefaultRoadCaptain extends LimitedRoadCaptain{}
  public interface DetailedRoadCaptain extends DefaultRoadCaptain{}

  /*TRIP PRICE JSON*/
  public interface LimitedTripPrice extends Base{}
  public interface DefaultTripPrice extends LimitedTripPrice{}
  public interface DetailedTripPrice extends DefaultTripPrice{}

  public interface LimitedAccessory extends Base{}
  public interface DefaultAccessory extends LimitedAccessory{}
  public interface DetailedAccessory extends DefaultAccessory{}

  public interface LimitedTransaction extends Base{}
  public interface DefaultTransaction extends LimitedTransaction{}
  public interface DetailedTransaction extends DefaultTransaction, DefaultTrip{}

  public interface LimitedTestimonial extends Base{}
  public interface DefaultTestimonial extends LimitedTestimonial, DetailedTrip{}

  public interface LimitedRequestTrip extends Base{}
  public interface DefaultRequestTrip extends LimitedRequestTrip, DefaultTrip{}

  public interface LimitedAlbum extends Base{}
  public interface DefaultAlbum extends LimitedAlbum{}
  public interface DetailedAlbum extends DefaultAlbum, DefaultMedia{}

  public interface LimitedMedia extends Base{}
  public interface DefaultMedia extends LimitedMedia{}
  public interface DetailedMedia extends DefaultMedia{}

  public interface LimitedSubCategory extends Base {}
  public interface DefaultSubCategory extends LimitedSubCategory, DefaultCategory {}
  public interface DetailedSubCategory extends DefaultSubCategory {}

  public interface LimitedCategory extends Base {}
  public interface DefaultCategory extends LimitedCategory {}
  public interface DetailedCategory extends DefaultCategory {}

  public interface LimitedTripPriceMotor extends Base{}
  public interface DefaultTripPriceMotor extends LimitedTripPriceMotor{}
  public interface DetailedTripPriceMotor extends DefaultTripPriceMotor{}


}
