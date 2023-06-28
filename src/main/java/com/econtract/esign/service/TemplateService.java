/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.model.Template;
import com.econtract.esign.repository.TemplateRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class TemplateService {
    
    @Autowired
    TemplateRepository templateRepository;
    
    public Template getTemplate(int moduleId, int clientType, int documentCategoryId, int type){
        Optional<Template> Ot = templateRepository.findFirstByModuleIdAndClientTypeAndDocumentCategoryIdAndType(moduleId, clientType, documentCategoryId, type);
        Template t = new Template();
        if(Ot.isPresent()){
            t = Ot.get();
        }
        return t;
    }
}
