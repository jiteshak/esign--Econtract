/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.OtpLog;
import com.econtract.esign.model.Source;
import com.econtract.esign.model.Task;
import com.econtract.esign.model.constant.LinkType;
import com.econtract.esign.model.dto.EsignRequestStatusDto;
import com.econtract.esign.repository.OtpLogRepository;
import com.econtract.esign.util.JsonUtil;
import com.econtract.esign.util.PasswordUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author TS
 */
@Service
public class CommunicationService {
    
    
    @Value("${base.app.url.source.agreement}")
    String sourceAgreementUrl;
    
    @Autowired
    OtpLogRepository otpLogRepository;
    
    @Autowired
    SmsService smsService;
    
    @Autowired
    EmailService emailService;
    
    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    LoggerService loggerService;
    
    public void sendCustomerOtp(EsignRequestSignee ers){
    //check attempts
        List<OtpLog> ols = otpLogRepository.findByLinkIdAndModifiedDateGreaterThanEqual(ers.getId(),LocalDateTime.now().minusMinutes(15));
        if(ols.size() >= 15){
            throw new ApiException("You Exceeded maximum tries. Kindly try again after 15 min");
        }
        
        //create new entry
        OtpLog o = new OtpLog();
        o.setLinkId(ers.getId());
        o.setLinkType(LinkType.APPLICANT);
        o.setContact(ers.getApplicantContact());
        o.setOtp(PasswordUtil.generateOtp());
        o.setDescription(o.getOtp());
        o.setEndDate(LocalDateTime.now().plusMinutes(15));
        o.setClosed(0);
        o.setModifiedBy(ers.getEsignRequestId());
        o.setModifiedDate(LocalDateTime.now());
        
        String text = smsService.sendCustomerOtp(ers, o);
        emailService.sendCustomerOtp(ers, o.getOtp());
        
        o.setDescription(text);
        otpLogRepository.save(o);
    }
    
    
    
    public void verifyCustomerOtp(EsignRequestSignee ers, String otp){
        Optional<OtpLog> olsO = otpLogRepository.findFirstByLinkIdOrderByIdDesc(ers.getId());
       //LocalDateTime.now().minusMinutes(15)
       if(!olsO.isPresent()){
           throw new ApiException("OTP invalid or expired");
       }
       OtpLog ols = olsO.get();
       
       if(ols.getAttempt() >= 5){
           throw new ApiException("You exceeded your maximum tries");
       }
       
       //add attempts
       ols.setAttempt(ols.getAttempt() + 1);
       otpLogRepository.save(ols);
       
       if(ols.getModifiedDate().isBefore(LocalDateTime.now().minusMinutes(15)) || ols.getClosed() == 1){
           throw new ApiException("OTP invalid or expired");
       }
       if(!otp.equals(ols.getOtp())){
           throw new ApiException("OTP invalid");
       }
       
       
       ols.setClosed(1);
       otpLogRepository.save(ols);
    }
    
    
    public boolean verifiedCustomerOtp(Integer EsignRequestSigneeId, String otp){
        Optional<OtpLog> olsO = otpLogRepository.findFirstByLinkIdOrderByIdDesc(EsignRequestSigneeId);
       //LocalDateTime.now().minusMinutes(15)
       if(!olsO.isPresent()){
           return false;
       }
       OtpLog ols = olsO.get();
       if(ols.getModifiedDate().isBefore(LocalDateTime.now().minusMinutes(15))){
//           return false;
       }
       if(!otp.equals(ols.getOtp())){
           return false;
       }
       
       
       if(ols.getClosed() == 1){
           return true;
       }
       
       return false;
    }
    
    public void sendAgreement(EsignRequestSignee ers){
        emailService.sendAgreement(ers);
        smsService.sendAgreement(ers);
    }
    
    public void sendForgotPasswordMail(String to, String userName, String token){
        emailService.sendForgotPasswordMail(to, userName, token);
    }
    
    public void sendNewUserMail(String to, String name, String userName, String password){
        emailService.sendNewUserMail(to, name, userName, password);
    }
    
    
    public void sendTaskFailMail(Task task, String linkId,String to, String userName){
        emailService.sendTaskFailMail(task, linkId, to, userName);
    }
    
    public void sendEsignUpdateToSource(EsignRequest er) {
        EsignRequestStatusDto ers =  EsignRequestStatusDto.toDto(er, sourceAgreementUrl);
        Source s = er.getSource();

        String url = s.getWebhook();
        String requestJson = JsonUtil.toString(ers);
        String result = null;
        String token = "";

        try {

            if (url != null && !url.isEmpty()) {
                RestTemplate rt = restTemplate;
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentType(new MediaType("Authorization", "Bearer " + token));
                HttpEntity<String> request = new HttpEntity<String>(requestJson, headers);
                result = rt.postForObject(url, request, String.class);

            }

        } catch (Exception ex) {
            result = ex.getMessage();
        }

        if (url != null && !url.isEmpty()) {
            loggerService.logApi("sourceId ="+ s.getId() +" url= " + url + " \n data = " + requestJson + " \n result = " + result);
        }
    }
}
