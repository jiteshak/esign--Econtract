/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.model.DocumentCategory;
import com.econtract.esign.model.DocumentCategoryState;
import com.econtract.esign.model.State;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.repository.DocumentCategoryRepository;
import com.econtract.esign.repository.DocumentCategoryStateRepository;
import com.econtract.esign.security.AclService;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author TS
 */
@RestController
@RequestMapping("/document/")
public class DocumentController {
    
    @Autowired
    DocumentCategoryRepository documentCategoryRepository;
    
    @Autowired
    DocumentCategoryStateRepository documentCategoryStateRepository;
    
    @Autowired
    AclService aclService;
    
    @GetMapping("list")
    public List<DocumentCategory> list(HttpServletRequest request, @RequestParam int entity){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.DOCUMENT, PermissionType.USER));
        
        if(entity == 0){
            return documentCategoryRepository.findAll();
        }
        
        return documentCategoryRepository.findByEntityId(entity);
    }
    
    @GetMapping("/stamp/list")
    public List<DocumentCategoryState> listStamp(HttpServletRequest request, @RequestParam int entity){ 
        aclService.verifyAccess(request, Arrays.asList(PermissionType.DOCUMENT));
        
        if(entity == 0){
            return documentCategoryStateRepository.findAll();
        }
        
        return documentCategoryStateRepository.findByEntityId(entity);
    }
}
