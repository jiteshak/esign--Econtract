package com.econtract.esign.controller;

import com.econtract.esign.model.ResetPassword;
import com.econtract.esign.model.User;
import com.econtract.esign.service.ResetPasswordService;
import com.econtract.esign.service.UserService;
import com.econtract.esign.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResetPasswordController {

    @Autowired
    UserService userService;
    
    @Autowired
    ResetPasswordService resetPasswordService;


    @GetMapping("resetPassword/{token}")
    public String resetPassword(ModelMap model, @PathVariable("token") String token){
        int showLogin = 0;
        int showThankYou = 0;
        int showError = 0;

        //validate token
        ResetPassword resetPassword = resetPasswordService.validateResetPasswordToken(token);

        if(resetPassword.getUserId() != null && resetPassword.getUserId() > 0){
            showLogin = 1;
        }else{
            showError = 1;
        }


        //send to reset page
        model.addAttribute("token", token);
        model.addAttribute("showLogin", showLogin);
        model.addAttribute("showThankYou", showThankYou);
        model.addAttribute("showError", showError);
        return "resetPassword";
    }

    @RequestMapping(value="verifyResetPassword", method = RequestMethod.POST)
    public String veriyPassword(ModelMap model
            , @RequestParam String password, @RequestParam String token){
        int showLogin = 0;
        int showThankYou = 0;
        int showError = 0;


        if(password == null|| password.isEmpty()){
//            return "redirect:../resetPassword/"+token;
        }

        //validate token
        ResetPassword resetPassword = resetPasswordService.validateResetPasswordToken(token);

        if(resetPassword.getUserId() == null || resetPassword.getUserId() > 0){
//            return "redirect:../resetPassword/"+token;
        }else{
            
            boolean changed = resetPasswordService.changePassword(resetPassword, password);
            if(changed){
                showThankYou = 1;
            }

        }

        if(showThankYou == 0){
            showError = 1;
        }


        //send to reset page
        model.addAttribute("token", token);
        model.addAttribute("showLogin", showLogin);
        model.addAttribute("showThankYou", showThankYou);
        model.addAttribute("showError", showError);
        return "resetPassword";
    }

}
