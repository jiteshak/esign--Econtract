/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.model.EEntity;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.repository.EntityRepository;
import com.econtract.esign.security.AclService;
import com.econtract.esign.security.Token;
import com.econtract.esign.security.TokenService;
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
@RequestMapping("/entity/")
public class EntityController {
    
    @Autowired
    TokenService tokenService;
    
    @Autowired
    EntityRepository entityRepository;
    
    @Autowired
    AclService aclService;
    
    @GetMapping("list")
    public List<EEntity> list(HttpServletRequest request){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ENTITY, PermissionType.USER));
        
    
        return entityRepository.findByIsActive(1);
    }
    
}
