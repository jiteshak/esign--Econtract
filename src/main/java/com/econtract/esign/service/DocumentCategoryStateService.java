/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.model.DocumentCategoryState;
import com.econtract.esign.repository.DocumentCategoryStateRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class DocumentCategoryStateService {
    
    @Autowired
    DocumentCategoryStateRepository documentCategoryStateRepository;
    
    public int getStampPaperAmount(String state,int moduleId ,int documentCategoryId, int agreementAmount){
        int a = 0;
        List<DocumentCategoryState> ldcsDefault = new ArrayList<>();
        List<DocumentCategoryState> ldcs = documentCategoryStateRepository.findByModuleIdAndDocumentCategoryIdAndState(moduleId, documentCategoryId, state);
        
        //check if range define then check basis on range else amount can be first index
        for(int i=0;i<ldcs.size();i++){
            DocumentCategoryState dcs = ldcs.get(i);
            if(dcs.getMinAmount() > 0 && dcs.getMaxAmount() > 0){
                if(dcs.getMinAmount() <= agreementAmount && dcs.getMaxAmount() >= agreementAmount){
                    a = dcs.getStampValue();
                }
            }else{
                ldcsDefault.add(dcs);
            }
        }
        
        if(a == 0 && ldcsDefault.size() > 0){
            a = ldcsDefault.get(0).getStampValue();
        }
        
        return a;
    }
}
