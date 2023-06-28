/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.model.Branch;
import com.econtract.esign.model.dto.BranchDto;
import com.econtract.esign.repository.BranchRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class BranchService {
    
    @Autowired
    BranchRepository branchRepository;
    
    
    public List<BranchDto> getBrancheByEnitities(List<Integer> entityIds){
        List<BranchDto> brl = new ArrayList<>();
        List<Branch> bl = branchRepository.findByEntityIdIn(entityIds);
        
        bl.forEach(b -> {
            brl.add(new BranchDto(b.getId(), b.getName(), b.getEntityId(), b.getEntity().getName()));
        });
        
        return brl;
    }
    
    public List<Branch> getBrancheByEnitityId(Integer entityId){
        List<Branch> bl = branchRepository.findByEntityId(entityId);
        return bl;
    }
    
}
