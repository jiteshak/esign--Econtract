/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.StampPaper;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.model.dto.StampPaperDto;
import com.econtract.esign.model.dto.CommonDto;
import com.econtract.esign.repository.specification.SearchCriteria;
import com.econtract.esign.repository.specification.StampPaperSpecification;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.StampPaperService;
import com.econtract.esign.service.StampPaperUploadService;
import com.econtract.esign.service.TaskService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author TS
 */
@RestController
@RequestMapping("/stamp-paper/")
public class StampPaperController {
    
    @Autowired
    StampPaperService stampPaperService;
    
    @Autowired
    StampPaperUploadService stampPaperUploadService;
    
    @Autowired
    TaskService taskService;
    
    @Autowired
    AclService aclService;
    
    @PostMapping("add")
    public StampPaper add(HttpServletRequest request, @RequestBody StampPaper sp){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.STAMPPAPER));
        // TODO: who can add sp
        
        sp = stampPaperService.save(sp);
        return sp;
    }
    
    @GetMapping("list")
    public Page<StampPaper> list(HttpServletRequest request, Pageable pageable, 
            @RequestParam(required = false) String referenceNo, @RequestParam(required = false) int value,
            @RequestParam(required = false) String state, @RequestParam(required = false) int status){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.STAMPPAPER));
        
        Specification<StampPaper> spec = Specification.where(new StampPaperSpecification(new SearchCriteria("isActive", ":", "1")));
        
        if(referenceNo != null && !referenceNo.isEmpty()){
            spec = spec.and(new StampPaperSpecification(new SearchCriteria("referenceNo", ":", referenceNo)));
        }
        
        if(value > 0){
            spec = spec.and(new StampPaperSpecification(new SearchCriteria("value", ":", value)));
        }
        
//        if(status > 0){
            spec = spec.and(new StampPaperSpecification(new SearchCriteria("status", ":", status)));
//        }
        
        if(state != null && !state.isEmpty()){
            spec = spec.and(new StampPaperSpecification(new SearchCriteria("state", ":", state)));
        }
        
        spec = aclService.stampPaper(spec);
        return stampPaperService.list(spec,pageable);
    }
    
    @PostMapping("upload")
    public CommonDto upload(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.STAMPPAPER));
        
        if(!file.getOriginalFilename().toLowerCase().contains(".xlsx")){
            throw new ApiException("File should be in xlsx format.");
        }
        
        
        String msg = stampPaperUploadService.validateFile(file, aclService.getCurrentRole().getSourceId());
        if(!msg.isEmpty()){
            throw new ApiException(msg);
        }
        
        String path = stampPaperUploadService.store(file, aclService.getCurrentRole().getSourceId());
        
        //create job
        taskService.addStampPaperUploadTask(path, aclService.getCurrentRole().getSourceId(), aclService.getToken().getUserId());
        
        return new CommonDto("Stamp Paper will be fetched in sometime. You can visit task section to see progress");
    }
    
    @PostMapping("upload/single")
    public CommonDto uploadSingle(
            HttpServletRequest request, 
            @RequestParam("stamp") MultipartFile stamp, 
            @RequestParam("referenceNo") String referenceNo, @RequestParam("entityId") Integer entityId, 
            @RequestParam("value") Integer value, @RequestParam("state") String state, 
            @RequestParam("procurementDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date procurementDate, @RequestParam("expiryDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date expiryDate
    ){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.STAMPPAPER));
        
        StampPaperDto spr = new StampPaperDto();
        spr.setReferenceNo(referenceNo);
        spr.setEntityId(entityId);
        spr.setValue(value);
        spr.setState(state);
        spr.setExpiryDate(expiryDate);
        spr.setProcurementDate(procurementDate);
        
        
        if(!stamp.getOriginalFilename().toLowerCase().contains(".pdf")){
            throw new ApiException("Stamp Paper should be in pdf format.");
        }
        
        //validate
        String msg = stampPaperUploadService.validateStampPaper(spr);
        if(!msg.isEmpty()){
           throw new ApiException(msg);
        }
        
        String fileName = stampPaperUploadService.storeFile(spr, stamp);
        spr.setFile1(fileName);
        msg = stampPaperUploadService.upload(aclService.getCurrentRole().getUserId(), aclService.getCurrentRole().getSourceId(), spr);
        if(!msg.isEmpty()){
            throw new ApiException(msg);
        }
        
        return new CommonDto("Stamp Paper added");
    }
    
    
    @GetMapping("delete/{id}")
    public CommonDto delete(HttpServletRequest request, @PathVariable("id") int id){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.STAMPPAPER));
        
        stampPaperService.delete(id);
        return new CommonDto("Stamp Paper deleted");
    }
}
