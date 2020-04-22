package com.r2r.road2ring.modules.motor;

import com.r2r.road2ring.modules.common.PublishedStatus;
import com.r2r.road2ring.modules.common.Road2RingException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

@Service
public class MotorService {

  MotorRepository motorRepository;

  @Autowired
  public void setMotorRepository(MotorRepository motorRepository){
    this.motorRepository = motorRepository;
  }

  public List<Motor> getAllMotorDatatable(){
    return motorRepository.findAll();
  }
  public List<Motor> getAutocompletPublishedMotorByTitle(String keyword){
    return motorRepository.findTop5ByStatusAndTitleIgnoreCaseContainingOrderByTitleAsc(
        PublishedStatus.PUBLISHED,keyword);
  }

  public List<Motor> getAllMotor(){
    return motorRepository.findAllByStatus(PublishedStatus.PUBLISHED);
  }

  public Motor saveMotor(Motor motor){
    Motor saved = new Motor();
    if(motor.getId() != 0 && motor.getId() != null){
      saved = motorRepository.findOne(motor.getId());
    }else{
      saved.setStatus(PublishedStatus.UNPUBLISHED);
    }
    saved.setBrand(motor.getBrand());
    saved.setCapacity(motor.getCapacity());
    saved.setDiscount(motor.getDiscount());
    saved.setDescription(motor.getDescription());
    saved.setPrice(motor.getPrice());
    saved.setTitle(motor.getTitle());
    saved.setBrand(motor.getBrand());
    saved.setPicture(motor.getPicture());

    return motorRepository.save(saved);
  }

  public Motor getMotoyrById(int id){
    return motorRepository.findOne(id);
  }

  public Motor getMotorByTilte(String title){
    return motorRepository.findOneByTitle(title);
  }


  public List<Motor> getListMotor(Integer page, Integer limit){
    Pageable pageable = new PageRequest(page, limit);
    List<Motor> result = motorRepository.findAllByOrderByIdDesc(pageable);
    return result;
  }

  public void changeStatus(PublishedStatus statusId, int id) throws Road2RingException {
    Motor save = motorRepository.findOne(id);
    save.setStatus(statusId);
    motorRepository.save(save);
  }
}
