/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EEntity;
import com.econtract.esign.model.Module;
import com.econtract.esign.model.constant.ModuleType;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.model.dto.CommonDto;
import com.econtract.esign.repository.ModuleRepository;
import com.econtract.esign.security.AclService;
import com.econtract.esign.security.Token;
import com.econtract.esign.security.TokenService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author TS
 */
@RestController
@RequestMapping("/product/")
public class ProductController {
    
    @Autowired
    ModuleRepository moduleRepository;
    
    @Autowired
    TokenService tokenService;
    
    @Autowired
    AclService aclService;
    
    @PostMapping("bulkUpload")
    public String bulkUpload(@RequestBody String s){
        // TODO: db not shared
    
        return "products uploaded";
    }
    
    
        
    @GetMapping("list")
    public List<Module> list(HttpServletRequest request, @RequestParam() int type, @RequestParam() int entity, @RequestParam(required = false) int parentId){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.MODULE, PermissionType.USER, PermissionType.ESIGNREQUEST));
        
        if(!(ModuleType.LOB == type || ModuleType.PRODUCT == type)){
            throw new ApiException("invalid type");
        }
        
        if(entity == 0){
            if(parentId == 0 || type == ModuleType.LOB){
                return moduleRepository.findByIsActiveAndType(1, type);
            }
            if(type == ModuleType.PRODUCT){
                return moduleRepository.findByIsActiveAndTypeAndParentId(1, type, parentId);
            }
        }
    
        if(type == ModuleType.LOB || (type == ModuleType.PRODUCT && !(parentId > 0))){
            return moduleRepository.findByIsActiveAndTypeAndEntityId(1, type, entity);
        }
        
        
        return moduleRepository.findByIsActiveAndTypeAndEntityIdAndParentId(1, type, entity, parentId);
    }
    
    @PostMapping("createUpdate")
    public CommonDto create(HttpServletRequest request,@RequestBody Module m){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.MODULE, PermissionType.USER));
        
        m.setIsActive(1);
        m.setModifiedBy(aclService.getUserId());
        m.setModifiedDate(LocalDateTime.now());
        moduleRepository.save(m);
        
        return new CommonDto("Successfully Done");
    }
}
