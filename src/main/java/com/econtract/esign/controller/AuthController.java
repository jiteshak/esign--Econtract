/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.Role;
import com.econtract.esign.model.User;
import com.econtract.esign.model.dto.LoginRequestDto;
import com.econtract.esign.model.dto.LoginResponseDto;
import com.econtract.esign.model.dto.UserDto;
import com.econtract.esign.security.Token;
import com.econtract.esign.security.TokenService;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.BranchService;
import com.econtract.esign.service.CommunicationService;
import com.econtract.esign.service.ResetPasswordService;
import com.econtract.esign.service.UserService;
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
@RequestMapping("/auth/")
public class AuthController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    TokenService tokenService;
    
    @Autowired
    CommunicationService communicationService;
    
    @Autowired
    ResetPasswordService resetPasswordService;
    
    @Autowired
    BranchService branchService;
    
    @Autowired
    AclService aclService;
    
    
    @RequestMapping("/")
    String index(){
        return "";
    }
    
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest){
        User user = userService.authenticate(loginRequest, true);
        
        List<Role> rl = userService.getRoles(user);
        String rn = "";
        if(rl.size() > 0){
            rn = rl.get(0).getName();
        }
        
        Token tokenBody = new Token();
        tokenBody.setUserId(user.getId());
        tokenBody.setUserName(user.getUserName());
        tokenBody.setUserType(rn);
        tokenBody.setEmail(user.getUserName());
        tokenBody.setGroup(user.getGroup());
        tokenBody.setAcl(aclService.getAclString(user));
        String token = tokenService.generateJwt(tokenBody);
        
        
        LoginResponseDto loginResponse = new LoginResponseDto();
        loginResponse.setToken(token);
        loginResponse.setUser(UserDto.toDto(user));
        loginResponse.setAcl(aclService.getAcl(user));
        
        
        user.setLastLogin(LocalDateTime.now());
        userService.save(user);
        return loginResponse;
    }

    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        aclService.verifyAccess(request, Arrays.asList());
        tokenService.closeLoginLog(aclService.getToken().getLoginLogId());
        
        return "OK";
    }
    
    @PostMapping("forgotPassword")
    public String forgotPassword(@RequestParam String userName, HttpServletRequest request){
        User user = userService.getUserByUserName(userName);
        if(user.getIsActive() != 1){
            throw new ApiException("Invalid User");
        }


        String token = resetPasswordService.validateResetPasswordAttemps(userName, user.getId());
        if(token.isEmpty()){
            token = tokenService.generateOpaque(user.getId() + user.getUserName());

            resetPasswordService.saveResetPassword(userName, token, user.getId(), request);
        }
        communicationService.sendForgotPasswordMail(user.getEmail(), user.getFirstName(), token);
        
        
        return "Mail send successfully";
    }
    
    
    
}
