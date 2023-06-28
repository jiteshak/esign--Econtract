/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.OtpLog;
import com.econtract.esign.model.Template;
import com.econtract.esign.model.constant.LinkType;
import com.econtract.esign.model.constant.TemplateType;
import com.econtract.esign.repository.OtpLogRepository;
import com.econtract.esign.util.CommunicationUtil;
import com.econtract.esign.util.PasswordUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class SmsService {
    
    @Autowired
    CommunicationUtil communicationUtil;
    
    
    @Autowired
    TemplateService templateService;
    
    @Value("${base.app.url.customer}")
    private String customerUrl;
    
    
    public String sendCustomerOtp(EsignRequestSignee ers, OtpLog o){
        
        String text = o.getOtp() + " is your one time password to sign a document with the document ID: {{referenceNumber1}}. This OTP is valid for 15 mins only. Regards, ABFL";
        text = text.replace("{{referenceNumber1}}", ers.getEsignRequest().getReferenceNumber1());

        
        
        communicationUtil.sendSms(o.getContact(), text);
        return text;
    }
    
    @Async
    public Boolean sendAgreement(EsignRequestSignee ers){
        if(ers.getApplicantContact().isEmpty()){
            return false;
        }
        String url = customerUrl.replace("{token}", ers.getToken());
        url = communicationUtil.shortUrl(url);
        
        //default msg if no template configured
        String message = "Dear Customer, you are invited to view your digital loan agreement contract with Aditya Birla Finance and e-signing of the same, online. Please click on the link here to access the agreement {{tokenUrl}} . Kindly note the link would be valid only for 7 days. Regards, ABFL";
        if(ers.getSequence() > 1){
            message = "Dear Customer, you are invited to view digital loan agreement contract as a co-applicant with Aditya Birla Finance and e-signing of the same, online. Please click on the link here to access the agreement {{tokenUrl}} . Kindly note the link would be valid only for 7 days. Regards, ABFL";
        }
        
        //get template
        int templateType = TemplateType.AGMNT_SMS_APPLICANT;
        if(ers.getSequence() > 1){
            templateType = TemplateType.AGMNT_SMS_COAPPLICANT;
        }
        Template t = templateService.getTemplate(ers.getEsignRequest().getModuleId(), ers.getEsignRequest().getClientType(), ers.getEsignRequest().getDocumentCategoryId(), templateType);
        if(t.getId() != null){
            message = t.getDocument();
        }
        
        message = message.replace("{{applicantName}}", ers.getApplicantName());
        message = message.replace("{{referenceNumber1}}", ers.getEsignRequest().getReferenceNumber1());
        message = message.replace("{{tokenUrl}}", url);
        communicationUtil.sendSms(ers.getApplicantContact(), message);
        
        return true;
    }
}
