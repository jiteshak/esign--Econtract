/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.service.Main;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * this controller was here for testing purpose now it is not in used
 * @author TS
 */
@Controller
@RequestMapping("/nsdl/")
public class NsdlController {
    
    @GetMapping("request")
    public String index(ModelMap model){
    
        String requestXml = Main.init();
        
        
        
        model.addAttribute("requestXml", requestXml);
        return "nsdl-request";
    }
    
    
    @PostMapping("response")
    public String response(@RequestParam String msg){
    
        Main.response(msg);
        
        
        
        return "nsdl-response";
    }
}
