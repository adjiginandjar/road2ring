package com.r2r.road2ring.modules.hotel;

import com.r2r.road2ring.modules.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    HotelRepository hotelRepository;

    @Autowired
    public void setHotelRepository(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public void saveListHotel(List<Hotel> hotels, Trip trip, List<Hotel> deleted){

        Hotel hotel;
        if (deleted != null && deleted.size() != 0) {
            remove(deleted);
        }
        for(Hotel htl : hotels){
            hotel = new Hotel();
            if(htl.getId() != null && htl.getId() != 0){
                hotel = hotelRepository.findOne(htl.getId());
            }
            hotel.setAlamat(htl.getAlamat());
            hotel.setNama(htl.getNama());
            hotel.setPicture(htl.getPicture());
            hotel.setStar(htl.getStar());
            hotel.setTrip(trip);
            hotelRepository.save(hotel);
        }


    }

    public void remove(List<Hotel> deleted){
        for (Hotel htl : deleted) {
            hotelRepository.delete(htl.getId());
        }
    }
}
