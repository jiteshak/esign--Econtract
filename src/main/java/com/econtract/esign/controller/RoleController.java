/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.Role;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.model.dto.CommonDto;
import com.econtract.esign.model.dto.PermissionDto;
import com.econtract.esign.model.dto.RoleDto;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.ModuleService;
import com.econtract.esign.service.UserLinkService;
import com.econtract.esign.service.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/role/")
public class RoleController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    ModuleService moduleService;
    
    @Autowired
    UserLinkService userLinkService;
    
    @Autowired
    AclService aclService;
    
    @GetMapping("list")
    public List<RoleDto> list(HttpServletRequest request, @RequestParam int entityId, @RequestParam int root){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.USER));
        
        if(root == 1){
            return RoleDto.toDtoRootList(userService.getRolesByEntity(entityId));
        }else{
            if(entityId == 0){
                return RoleDto.toDtoList(userService.getRoles());
            }else{
                return RoleDto.toDtoList(userService.getRolesByEntity(entityId));
            }
        }
    }
    
    
    @GetMapping("permission/list")
    public List<PermissionDto> listPermission(HttpServletRequest request){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.USER));
        
        return PermissionDto.toDtoList(userService.listPermission());
    }
    
    
    
    @PostMapping("createUpdate")
    public RoleDto add(HttpServletRequest request, @RequestBody RoleDto rd){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.USER));
        
        if(!RoleDto.isValid(rd)){
            throw new ApiException(rd.getError());
        }
        
        
        
        Role r = null;
        if(rd.getId() == null || rd.getId() == 0){
            r = RoleDto.toModel(rd);
        }else{
            r = userService.getRoleById(rd.getId());
            r.setName(rd.getName());
            r.setEntityId(rd.getEntityId());
        }
        
        r.setIsActive(1);
        r.setModifiedBy(aclService.getUserId());
        r.setModifiedDate(LocalDateTime.now());
        r = userService.save(r);
       
        
        
        //resolve permissions
        userService.resolvePermissions(r, rd.getPermissionIds(), aclService.getUserId());
        
        
        return rd;
    } 
    
    
    
    
    
    @GetMapping("delete/{id}")
    public CommonDto deleteRole(HttpServletRequest request, @PathVariable("id") int id){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.USER));
        
        userService.deleteRole(id);
        
        return new CommonDto("Role removed");
    }
    
}
