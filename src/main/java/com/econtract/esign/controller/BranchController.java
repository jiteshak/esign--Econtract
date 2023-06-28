/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.model.dto.BranchDto;
import com.econtract.esign.model.dto.SourceDto;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.BranchService;
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
@RequestMapping("/branch/")
public class BranchController {
    
    @Autowired
    AclService aclService;
    
    @Autowired
    BranchService branchService;
    
    
    @GetMapping("list")
    public List<BranchDto> list(HttpServletRequest request, @RequestParam int entityId){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.BRANCH, PermissionType.USER));
        
        return BranchDto.toDtoList(branchService.getBrancheByEnitityId(entityId));
    }
    
}
