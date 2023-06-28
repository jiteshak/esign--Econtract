/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.model.Module;
import com.econtract.esign.model.constant.ModuleType;
import com.econtract.esign.model.dto.ModuleDto;
import com.econtract.esign.repository.ModuleRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class ModuleService {
    
    @Autowired 
    ModuleRepository moduleRepository;
    
    
    public List<ModuleDto> getProduct(List<Integer> entityIds){
        List<ModuleDto> mrl = new ArrayList<>();
        
        List<Module> ml =  moduleRepository.findByIsActiveAndTypeAndEntityIdIn(1, ModuleType.PRODUCT, entityIds);
        
        ml.forEach(m -> {
            mrl.add(new ModuleDto(m.getId(), m.getName(), m.getEntityId(), m.getEntity().getName()));
        });
        
        return mrl;
    }
    
}
