/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EEntity;
import com.econtract.esign.model.Role;
import com.econtract.esign.model.User;
import com.econtract.esign.model.UserRoleLink;
import com.econtract.esign.repository.EntityRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class EntityService {
    
    @Autowired
    EntityRepository entityRepository;
    
    public EEntity getByName(String name){
        Optional<EEntity> eO = entityRepository.findFirstByNameAndIsActive(name, 1);
        if(!eO.isPresent()){
            throw new ApiException("Entity not found : " + name);
        }
        
        return eO.get();
    }
    
}
