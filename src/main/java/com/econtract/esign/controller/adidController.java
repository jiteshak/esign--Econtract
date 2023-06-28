/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.model.Role;
import com.econtract.esign.model.User;
import com.econtract.esign.model.dto.LoginRequestDto;
import com.econtract.esign.model.dto.LoginResponseDto;
import com.econtract.esign.model.dto.UserDto;
import com.econtract.esign.security.Token;
import com.econtract.esign.security.TokenService;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.AdidService;
import com.econtract.esign.service.BranchService;
import com.econtract.esign.service.UserService;
import com.econtract.esign.util.CommonUtil;
import com.econtract.esign.util.JsonUtil;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author TS
 */
@Controller
@RequestMapping("/auth/adid/")
public class adidController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    TokenService tokenService;
    
    @Autowired
    BranchService branchService;
    
    @Autowired
    AdidService adidService;
    
    @Autowired
    AclService aclService;
    
    @Value("${base.app.url.login}")
    String appLoginUrl;
    
    @GetMapping("/init")
    public String initAdid() throws Exception{
        String url = adidService.getUrl();
        return "redirect:" + url;
    }
        
    @PostMapping("/login")
    public String adidLogin(HttpServletResponse httpServletResponse, @RequestParam String SAMLResponse){
        //ad logic
        String adid = adidService.renderAdidResponse(SAMLResponse);
        if(adid.isEmpty()){
            return "redirect:" + appLoginUrl + "?msg=invalid attempt";
        }
        
        
        LoginRequestDto loginRequest = new LoginRequestDto(adid);
        User user;
        try{
            user = userService.authenticate(loginRequest, false);
        }catch(Exception ex){
            return "redirect:" + appLoginUrl + "?msg="+ex.getMessage();
        }
        
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
//        tokenBody.setRoles(user.getRoleIds());
//        tokenBody.setBranches(branchService.getBrancheIds(user));
        tokenBody.setAcl(aclService.getAclString(user));
        String token = tokenService.generateJwt(tokenBody);
        
        
        LoginResponseDto loginResponse = new LoginResponseDto();
        loginResponse.setToken(token);
        loginResponse.setUser(UserDto.toDto(user));
        loginResponse.setAcl(aclService.getAcl(user));

        user.setLastLogin(LocalDateTime.now());
        userService.save(user);

        return "redirect:" + appLoginUrl  + "?adidres="+ CommonUtil.toBase64(JsonUtil.toString(loginResponse));
    }
    
}
