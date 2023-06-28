/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.ResetPassword;
import com.econtract.esign.model.User;
import com.econtract.esign.repository.ResetPasswordRepository;
import com.econtract.esign.repository.UserRepository;
import com.econtract.esign.util.PasswordUtil;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class ResetPasswordService {

    @Autowired
    UserService userService;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    ResetPasswordRepository resetPasswordRepository;
    
    @Value("${resetPassword.ttl}")
    Integer resetPasswordTtl;
    
    
    public String validateResetPasswordAttemps(String userName, Integer userId){
        Optional<ResetPassword> resetPasswordO = resetPasswordRepository.findFirstByUserNameAndIsValid(userName, 1);

        if(!resetPasswordO.isPresent()){
            return "";
        }

        ResetPassword resetPassword = resetPasswordO.get();

        if(!resetPassword.getUserId().equals(userId)){
            return "";
        }

        if(resetPassword.getIsValid() != 1){
            return "";
        }

        long cd = new Date().getTime();
        long ttl = resetPassword.getCreatedAt().getTime() + (resetPassword.getTtl() * 1000);
        if( cd >  ttl){
            //invalid
            resetPassword.setIsValid(0);
            resetPasswordRepository.save(resetPassword);
            return "";
        }

        if(resetPassword.getAttemp() > 3){
            throw new ApiException("You can not send reset mail more than 3 times in "+ (resetPasswordTtl/60) +" min. Please try after "+ (resetPasswordTtl/60) +" min");
        }


        resetPassword.setAttemp(resetPassword.getAttemp() + 1);
        resetPasswordRepository.save(resetPassword);

        return resetPassword.getToken();
    }

    public void saveResetPassword(String userName, String token, Integer userId, HttpServletRequest request){
        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setUserId(userId);
        resetPassword.setIp(request.getRemoteAddr());
        resetPassword.setUserName(userName);
        resetPassword.setToken(token);
        resetPassword.setTtl(resetPasswordTtl);
        resetPassword.setIsValid(1);
        resetPassword.setAttemp(1);
        resetPassword.setCreatedAt(new Date());

        resetPasswordRepository.save(resetPassword);
    }

    public ResetPassword validateResetPasswordToken(String token){
        ResetPassword resetPassword = new ResetPassword();
        ResetPassword resetPasswordBlank = new ResetPassword();
        Optional<ResetPassword> resetPasswordO = resetPasswordRepository.findFirstByToken(token);

        if(!resetPasswordO.isPresent()){
            return resetPasswordBlank;
        }

        resetPassword = resetPasswordO.get();

        if(resetPassword.getIsValid() != 1){
            return resetPasswordBlank;
        }

//        if(resetPassword.getUserType().equalsIgnoreCase(UserType.student.toString())){
//            return resetPasswordBlank;
//        }

        long cd = new Date().getTime();
        long ttl = resetPassword.getCreatedAt().getTime() + (resetPassword.getTtl() * 1000);
        if( cd >  ttl){
            //invalid
            resetPassword.setIsValid(0);
            resetPasswordRepository.save(resetPassword);
            return resetPasswordBlank;
        }

        return resetPassword;
    }

    public Boolean changePassword(ResetPassword resetPassword, String password){

        Optional<User> userO = userRepository.findById(resetPassword.getUserId());
        if(!userO.isPresent()){
            return false;
        }

        User user = userO.get();
        user.setPassword(PasswordUtil.hash(password));
        userRepository.save(user);

        resetPassword.setIsValid(2);
        resetPasswordRepository.save(resetPassword);
        return true;
    }
}
