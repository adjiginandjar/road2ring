package com.r2r.road2ring.modules.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService {


  @Autowired
  SystemConfigRepository systemConfigRepository;


  public SystemConfig save(SystemConfig systemConfig){
    SystemConfig result = new SystemConfig();
    if(systemConfig.getId() != null){
      result = systemConfigRepository.findOne(systemConfig.getId());

      result.setBankVAFee(systemConfig.getBankVAFee());
      result.setCompanyFee(systemConfig.getCompanyFee());
      result.setMidtransFee(systemConfig.getMidtransFee());

      result = systemConfigRepository.save(result);
    }else{
      result = systemConfigRepository.save(systemConfig);
    }

    return result;

  }

  public SystemConfig getSystemConfig(){
    SystemConfig result = systemConfigRepository.findOne(1);
    if(result == null){
      result = new SystemConfig();
    }
    return result;
  }
}
