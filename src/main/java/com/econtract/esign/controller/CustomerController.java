/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;

import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.constant.EsignRequestStatus;
import com.econtract.esign.model.constant.SignOption;
import com.econtract.esign.model.dto.CommonDto;
import com.econtract.esign.model.dto.EsignRequestDetailDto;
import com.econtract.esign.model.dto.EsignRequestSigneeDto;
import com.econtract.esign.service.CommunicationService;
import com.econtract.esign.service.EsignRequestService;
import com.econtract.esign.service.MobileSignService;
import com.econtract.esign.service.NsdlService;
import com.econtract.esign.util.CommonUtil;
import com.econtract.esign.util.FileUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@RequestMapping("/customer/esign/{token}/")
public class CustomerController {

    @Value("${nsdl.base.path}")
    String basePath;

    @Value("${nsdl.response.back.url}")
    String nsdlBackUrl;

    @Autowired
    EsignRequestService esignRequestService;

    @Autowired
    CommunicationService communicationService;

    @Autowired
    NsdlService nsdlService;
    
    @Autowired
    MobileSignService mobileSignService;
    
    @Autowired
    CommonUtil commonUtil;

    String index() {

        return "";
    }

    @GetMapping("sendOtp")
    CommonDto sendOtp(@PathVariable("token") String token) {
        EsignRequestSignee ers = esignRequestService.getEsignRequestSigneeByToken(token);
        if(ers.getEsignRequest().getStatus() == EsignRequestStatus.NEW || ers.getStatus() == EsignRequestStatus.CANCELLED){
            throw new ApiException("Invalid url");
        }
        if(ers.getStatus() == 1){
            //throw new ApiException("Please contact admin. you can not proceed");
        }
        
         communicationService.sendCustomerOtp(ers);
         return new CommonDto("OTP sent to your registered mobile number and email");
    }

    @PostMapping("verifyOtp")
    CommonDto verifyOtp(@PathVariable("token") String token, @RequestBody Map<String, String> payload) {
        String otp = payload.get("otp");
        EsignRequestSignee ers = esignRequestService.getEsignRequestSigneeByToken(token);
        communicationService.verifyCustomerOtp(ers, otp);
        return new CommonDto("Verified");
    }

    @GetMapping("details")
    EsignRequestDetailDto details(@PathVariable("token") String token) {
        String otp = token.substring(token.length() - 6, token.length());
        token = token.replace(otp, "");
        
        EsignRequestDetailDto err = esignRequestService.getEsignRequest(token);
        if(err.getEsignRequest().getStatus() == EsignRequestStatus.NEW || err.getEsignRequest().getStatus() == EsignRequestStatus.CANCELLED){
            throw new ApiException("Invalid url");
        }
        for (int i = 0; i < err.getEsignRequestSignees().size(); i++) {
            if (err.getEsignRequestSignees().get(i).getToken().equals(token)) {
                err.setCurrentUser(err.getEsignRequestSignees().get(i).getId());
            }
        }
        
        
        
        EsignRequestSigneeDto ers = null;
        for(int i = 0; i<err.getEsignRequestSignees().size();i++) {
            if(err.getCurrentUser() == err.getEsignRequestSignees().get(i).getId()){
                ers = err.getEsignRequestSignees().get(i);
            }
        }
        
        boolean verified = communicationService.verifiedCustomerOtp(ers.getId(), otp);
        if(!verified){
            throw new ApiException("invalid url");
        }
        
        
        
        return err;
    }

    @GetMapping("request")
    CommonDto esignRequest(HttpServletRequest request,@PathVariable("token") String token) {
        String otp = token.substring(token.length() - 6, token.length());
        token = token.replace(otp, "");
        
        EsignRequestSignee ers = esignRequestService.getEsignRequestSigneeByToken(token);
        EsignRequest er = esignRequestService.getEsignRequest(ers);
        if(!esignRequestService.canCustomerSign(er, ers)){
            throw new ApiException("Please contact admin. you can not proceed");
        }
        boolean verified = communicationService.verifiedCustomerOtp(ers.getId(), otp);
        if(!verified){
            throw new ApiException("invalid url");
        }
        
        if(!SignOption.isAadhaar(ers.getSignOption())){
            throw new ApiException("You are not authorized to sign with this method.");
        }
        
        String req = nsdlService.getSignedRequest(er, ers);
        if(req.isEmpty()){
            throw new ApiException("Please contact admin. you can not proceed");
        }
        
        esignRequestService.createCustomerEsignLog(ers, er.getFile(), commonUtil.getRemoteAddr(request));
        
        return new CommonDto("", req);
    }

    @PostMapping("response")
    String esignResponse(HttpServletRequest request, HttpServletResponse response,@PathVariable("token") String token, @RequestParam("msg") String msg) {
        if (!msg.contains("status=\"1\"")) {
            System.err.println("nsdl:response:false -- " + msg);
            response.setHeader("Location", nsdlBackUrl.replace("{token}", token));
            response.setStatus(302);
            return "Invalid url";
        }
        //TODO: hard validatin
        //is this response from nsdl
        //check adhaar is of intendent person

        EsignRequestSignee ers = esignRequestService.getEsignRequestSigneeByToken(token);
        EsignRequest er = esignRequestService.getEsignRequest(ers);
        if(!esignRequestService.canCustomerSign(er, ers)){ 
            throw new ApiException("Please contact admin. you can not proceed");
        }
        
        boolean done = nsdlService.getSignedResponse(msg, er, ers);
        if (done) {
            esignRequestService.completeCustomerEsignLog(er, ers, msg, commonUtil.getRemoteAddr(request));
            communicationService.sendEsignUpdateToSource(er);
            response.setHeader("Location", nsdlBackUrl.replace("{token}", token));
            response.setStatus(302);
            return "";
        } else {
            response.setHeader("Location", nsdlBackUrl.replace("{token}", token));
            response.setStatus(302);
            return "Invalid response";
        }

    }

    @GetMapping("agreement")
    ResponseEntity<byte[]> agreement(@PathVariable("token") String token) {
        String otp = token.substring(token.length() - 6, token.length());
        token = token.replace(otp, "");
        
        EsignRequestSignee ers = esignRequestService.getEsignRequestSigneeByToken(token);
        EsignRequest er = esignRequestService.getEsignRequest(ers);
        if(er.getStatus() == EsignRequestStatus.NEW){
            throw new ApiException("Please contact admin. you can not proceed");
        }
        boolean verified = communicationService.verifiedCustomerOtp(ers.getId(), otp);
        if(!verified){
            throw new ApiException("invalid url");
        }

        //basePath + 
        String fullPath = basePath + er.getFile();
        byte[] contents = FileUtil.readBytesFromFile(fullPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = er.getFile();
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("no-cache, no-store, must-revalidate, max-age=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }

    @PostMapping("mobileSign")
    CommonDto mobileSign(HttpServletRequest request, @PathVariable("token") String token) {
        String otp = token.substring(token.length() - 6, token.length());
        token = token.replace(otp, "");
        
        EsignRequestSignee ers = esignRequestService.getEsignRequestSigneeByToken(token);
        EsignRequest er = esignRequestService.getEsignRequest(ers);
        if(!esignRequestService.canCustomerSign(er, ers)){
            throw new ApiException("Please contact admin. you can not proceed");
        }
        boolean verified = communicationService.verifiedCustomerOtp(ers.getId(), otp);
        if(!verified){
            throw new ApiException("invalid otp or expired");
        }
        
        if(!SignOption.isMobile(ers.getSignOption())){
            throw new ApiException("You are not authorized to sign with this method.");
        }
        
        boolean done = mobileSignService.getSignOnDocument(ers, er, commonUtil.getRemoteAddr(request));
        if(!done){
            throw new ApiException("invalid otp or expired");
        }
        esignRequestService.completeCustomerEsignLogByMobile(er, ers, commonUtil.getRemoteAddr(request));
        communicationService.sendEsignUpdateToSource(er);
        
        return new CommonDto("Sign through otp done");
    }

}
