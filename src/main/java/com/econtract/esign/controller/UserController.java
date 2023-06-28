/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.model.dto.CommonDto;
import com.econtract.esign.model.dto.UserDto;
import com.econtract.esign.security.AclService;
import com.econtract.esign.security.TokenService;
import com.econtract.esign.service.BranchService;
import com.econtract.esign.service.CommunicationService;
import com.econtract.esign.service.UserLinkService;
import com.econtract.esign.service.UserService;
import com.econtract.esign.util.PasswordUtil;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author TS
 */
@RestController
@RequestMapping("/user/")
public class UserController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    UserLinkService userLinkService;
    
    @Autowired
    BranchService branchService;
    
    @Autowired
    CommunicationService communicationService;
    
    @Autowired
    TokenService tokenService;
    
    @Autowired
    AclService aclService;
    
    @GetMapping("list")
    public List<UserDto> list(HttpServletRequest request){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.USER));
        
        return UserDto.toDtoList(userService.list());
    }
    
    @PostMapping("createUpdate")
    public UserDto add(HttpServletRequest request, @RequestBody UserDto ud){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.USER));
        
        if(!UserDto.isValid(ud)){
            throw new ApiException(ud.getError());
        }
        
        
        
        boolean isNewUser = ud.getId() == 0;
        String password = "";
        
        User u = UserDto.toModel(ud);
        if(userService.isUserPresent(u)){
            throw new ApiException("User already exists");
        }
        if(isNewUser){
            password = PasswordUtil.generateOtp() + "12";
            String hashPassword = PasswordUtil.hash(password);
            hashPassword = PasswordUtil.hash2(hashPassword);
            u.setPassword(hashPassword);
            u.setIsActive(1);
            u.setChangePassword(1);
        }else{
            u = userService.getUserByUserId(ud.getId());
            u.setExternalId(ud.getExternalId());
            u.setUserName(ud.getUserName());
            u.setFirstName(ud.getFirstName());
            u.setMiddleName(ud.getMiddleName());
            u.setLastName(ud.getLastName());
            u.setEmail(ud.getEmail());
            u.setContact(ud.getContact());
            u.setGender(ud.getGender());
            u.setSourceId(ud.getSourceId());
            u.setStampPaperSourceId(ud.getStampPaperSourceId());
            u.setGroup(ud.getGroup());
        }
        
        if(ud.getSourceId() == 0){
            u.setSourceId(null);
        }
        
        if(ud.getStampPaperSourceId() == 0){
            u.setStampPaperSourceId(null);
        }
        
        u.setModifiedBy(aclService.getUserId());
        u.setModifiedDate(LocalDateTime.now());
        u = userService.save(u);
        
        if(ud.getRoleIds() != null){
            //get roles and update roles
            userService.resolveRoles(u, ud.getRoleIds(), aclService.getUserId());
        }
        if(ud.getBranchIds() != null){
            userLinkService.resolveBranches(u, ud.getBranchIds(), aclService.getUserId());
        }
        
        
        if(ud.getModuleIds()!= null){
            userLinkService.resolveModules(u, ud.getModuleIds(), aclService.getUserId());
        }
        

        if(isNewUser){
            communicationService.sendNewUserMail(u.getEmail(), u.getFirstName(), u.getUserName(), password);
        }
        
        return ud;
    } 
    
    @GetMapping("delete/{id}")
    public CommonDto deleteUser(HttpServletRequest request, @PathVariable("id") int id){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.USER));
        
        User u = userService.getUserByUserId(id);
        u.setIsActive(0);
        userService.save(u);
        
        
        return new CommonDto("User removed");
    }
}
