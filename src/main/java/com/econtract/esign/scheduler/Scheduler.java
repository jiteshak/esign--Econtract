/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.scheduler;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.StampPaper;
import com.econtract.esign.model.Task;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.EsignRequestStatus;
import com.econtract.esign.service.CommunicationService;
import com.econtract.esign.service.EsignRequestService;
import com.econtract.esign.service.LoggerService;
import com.econtract.esign.service.StampPaperService;
import com.econtract.esign.service.StampPaperUploadService;
import com.econtract.esign.service.TaskService;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author TS
 */
@Component
@Log4j2
public class Scheduler {
    
    @Autowired
    TaskService taskService;
    
    @Autowired
    EsignRequestService esignRequestService;
    
    @Autowired
    StampPaperService stampPaperService;
    
    @Autowired
    StampPaperUploadService stampPaperUploadService;
    
    @Autowired
    CommunicationService communicationService;
    
    @Autowired
    LoggerService loggerService;
    
    @Scheduled(cron = "0 0/15 * * * ?")
    public void test(){
      loggerService.logEsignRequest("Schduler running...");
    }
    
    @Scheduled(cron = "0 0/15 * * * ?")
    public void initiateEsignRequest(){
        
        //getting all new task
        List<Task> tl = taskService.getEsginRequestInitiationNewTask();
        loggerService.logEsignRequest("Found Task To initiate: " + tl.size());
        
        
        //set task in progress
        tl = taskService.setTaskToInProgress(tl);
        
        
        //proces initiation one by one
        for(int i =0; i < tl.size(); i++){
            Task t = tl.get(i);
            EsignRequest er = esignRequestService.getEsignRequestById(t.getLinkId());
            List<EsignRequestSignee> ersl = esignRequestService.getEsignRequestSigneeById(er.getId());
            
            loggerService.logEsignRequest("Started Task : " + t.getId());
            
            loggerService.logEsignRequest(er.toString());
            try{
                if(EsignRequestStatus.WAITING_FOR_INITIATE != er.getStatus()){
                    throw new ApiException("You can not initiate this agreement.");
                }
                loggerService.logEsignRequest("initiation process started...");
                
                String msg = esignRequestService.initiateAgreement(er, ersl, t.getModifiedBy());
                if(!msg.isEmpty()){
                    throw new ApiException(msg);
                }
                
                
                //send mail for signing to user if customer is first to sign
                if(er.getOfficeSignFirst() == 0){
                    loggerService.logEsignRequest("sending customer email...");
                    esignRequestService.sendAgreementSMSEmail(er, ersl);
                }
                
                taskService.setTaskToCompleted(t);
                communicationService.sendEsignUpdateToSource(er);
            }catch(Exception ex){
                //send email to user
                t = taskService.setTaskToFailed(t, ex.getMessage());
                esignRequestService.setStatusToInitiationFailed(er, ex.getMessage());
                User user = taskService.getTaskOwnerEmail(t);
                communicationService.sendTaskFailMail(t, er.getReferenceNumber1(),user.getEmail(), user.getFirstName());
                
                
                loggerService.logEsignRequest("Received Error: " + t.getMessage());
            }
            
            //space to get error properly readable
            loggerService.logEsignRequest("\n\n\n");
        }
    }
    
    
    @Scheduled(cron = "0 0/10 * * * ?")
    public void stampPaperUpload(){
        
        //getting all new task
        List<Task> tl = taskService.getStampPaperUploadNewTask();
        loggerService.logEsignRequest("Found Task To initiate: " + tl.size());
        
        
        //set task in progress
        tl = taskService.setTaskToInProgress(tl);
        
        
        //proces initiation one by one
        for(int i =0; i < tl.size(); i++){
            Task t = tl.get(i);
            loggerService.logStampPaper("Started Task : " + t.getId());
            
            try{
                
                String res = stampPaperUploadService.upload(t.getModifiedBy() ,t.getLinkId(), t.getFile());
                String[] r = res.split("\\|\\|\\|");
                String msg = r[0];
                
                t.setMessage(msg);
                taskService.setTaskToCompleted(t);
                
                //set 
                if(r.length > 1){
                    List<String> states = Arrays.asList(r[1].split(","));
                    states.forEach(s -> {
                        loggerService.logEsignRequest("Reinitiating esign request for  : " + s);
                        esignRequestService.stampAvailable(msg);
                    });
                    
                }
            }catch(Exception ex){
                //send email to user
                t = taskService.setTaskToFailed(t, ex.getMessage());
                User user = taskService.getTaskOwnerEmail(t);
                communicationService.sendTaskFailMail(t, t.getLinkId().toString(),user.getEmail(), user.getFirstName());
                
                
                loggerService.logStampPaper("Received Error: " + t.getMessage());
            }
            
            //space to get error properly readable
            loggerService.logStampPaper("\n\n\n");
        }
    }
}
