/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignProcess;
import com.econtract.esign.model.StampPaper;
import com.econtract.esign.model.Template;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.security.AclService;
import com.econtract.esign.model.dto.EsignProcessResponse;
import com.econtract.esign.service.EsignProcessService;
import com.econtract.esign.service.StampPaperService;
import com.econtract.esign.service.TemplateService;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author TS
 */
@RestController
@RequestMapping("/esign/process/")
public class EsignProcessController {
    
    @Autowired
    EsignProcessService esignProcessService;
    
    @Autowired
    StampPaperService stampPaperService;
    
    @Autowired
    AclService aclService;
    
    @GetMapping("details")
    public EsignProcessResponse details(HttpServletRequest request,@RequestParam int esignRequestId, @RequestParam int sourceId,@RequestParam int clientType,@RequestParam int moduleId,@RequestParam int documentCategoryId){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.PROCESS, PermissionType.ESIGNREQUEST));
       
    
        EsignProcess p = esignProcessService.getProcess(moduleId, documentCategoryId, clientType, sourceId);
        if(p.getId() == null){
           new ApiException("Template not configured");
        }
        
        List<StampPaper> spl = stampPaperService.getAllocatedStampPaper(esignRequestId);
        return new EsignProcessResponse(p, spl);
    }
}
