/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.model.EsignProcess;
import com.econtract.esign.repository.EsignProcessRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class EsignProcessService {
    
    @Autowired
    EsignProcessRepository esignProcessRepository;
    
    public EsignProcess getProcess(int moduleId, int documentCategoryId,int clientType ,int sourceId){
        
        Optional<EsignProcess> Oep = esignProcessRepository.findFirstByModuleIdAndDocumentCategoryIdAndClientTypeAndSourceIdAndIsActive(moduleId, documentCategoryId, clientType, sourceId, 1);
        EsignProcess ep = new EsignProcess();
        if(Oep.isPresent()){
            ep = Oep.get();
        }
        
        return ep;
    }
    
}
