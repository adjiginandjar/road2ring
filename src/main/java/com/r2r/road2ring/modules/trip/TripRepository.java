package com.r2r.road2ring.modules.trip;

import com.r2r.road2ring.modules.itinerary.Itinerary;
import com.r2r.road2ring.modules.user.User;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends DataTablesRepository<Trip,Integer> {
  List<Trip> findAll();
  List<Trip> findAllByRoadCaptain(User roadCaptain);
  Trip findByCreated(Date date);

  @Query(value = "select count(itinerary_id) as countEvent, itinerary_group as groupEvent, itinerary_group_title as groupTitleEvent "
      + "from itinerary where itinerary_trip_id = :tripId "
      + "group by groupEvent, groupTitleEvent order by groupEvent asc",
      nativeQuery = true)
  List<Object[]> groupByItineraryGroup(@Param("tripId") int tripId);

  /*Trip Pageable*/
//  List<Trip>findAllByOrderByIdAsc(Pageable pageable);

//  Page<Trip> findAllByOrderByIdAsc(Pageable pageable);

  Page<Trip> findByPublishedStatusAndTripPricesStartTripGreaterThanAndTripPricesStatusOrderByIdDesc(Pageable pageable,
      TripPublishedStatus tripPublishedStatus,Date startDate,TripPriceStatus tripPriceStatus);

  @Query(value="select "
      + "trip.* "
      + "from trip "
      + "left outer join trip_price tripprices on trip.trip_id=tripprices.trip_price_trip_id "
      + "where trip.trip_published_status=0 "
      + "and tripprices.trip_price_start_trip> now() "
      + "and tripprices.trip_price_status= 0 "
      + "group by trip.trip_id "
      + "order by trip.trip_id desc "
      + "limit :limit "
      + "offset :offset ",nativeQuery = true)
  List<Trip> findAllPublishTripAndTripPriceStatusIsWaiting(@Param("limit")int limit,@Param("offset")int offset);


  @Query(value=  "select count(total) from ( "
      + "select "
      + "trip.* "
      + "from trip "
      + "left outer join trip_price tripprices on trip.trip_id=tripprices.trip_price_trip_id "
      + "where trip.trip_published_status=0 "
      + "and tripprices.trip_price_start_trip> now() "
      + "and tripprices.trip_price_status= 0 "
      + "group by trip.trip_id ) total",nativeQuery = true)
  int countTotalPublishTrip();

  @Query(value = "SELECT * " +
      "FROM trip " +
      "WHERE trip_tag ~ :tags " +
      "AND trip_id != :id " +
      "ORDER BY trip_id ASC " +
      "lIMIT 10", nativeQuery = true)
  List<Trip> findAllByTagInOrderByIdDesc(
      @Param("id")int id,
      @Param("tags")String tags);
}
