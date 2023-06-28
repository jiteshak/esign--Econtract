/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.EsignRequestService;
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
@RequestMapping("/state/")
public class StateController {
    
    @Autowired
    EsignRequestService esignRequestService;
    
    
    @Autowired
    AclService aclService;
    
    @GetMapping("list")
    public List<String> list(HttpServletRequest request, @RequestParam int entity){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST, PermissionType.STAMPPAPER));
        
        return esignRequestService.getDistinctStates();
    }
}
