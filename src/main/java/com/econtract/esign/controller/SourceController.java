/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.constant.EsignRequestStatus;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.model.dto.EsignRequestFormDto;
import com.econtract.esign.model.dto.EsignRequestUploadDto;
import com.econtract.esign.model.dto.BranchDto;
import com.econtract.esign.model.dto.CommonDto;
import com.econtract.esign.model.dto.EsignRequestApplicantFormDto;
import com.econtract.esign.model.dto.EsignRequestResponseDto;
import com.econtract.esign.model.dto.EsignRequestStatusDto;
import com.econtract.esign.model.dto.ModuleDto;
import com.econtract.esign.model.dto.SourceDto;
import com.econtract.esign.model.dto.StampPaperDto;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.BranchService;
import com.econtract.esign.service.EsignRequestService;
import com.econtract.esign.service.EsignRequestUploadService;
import com.econtract.esign.service.FileStorageService;
import com.econtract.esign.service.LoggerService;
import com.econtract.esign.service.ModuleService;
import com.econtract.esign.service.SourceService;
import com.econtract.esign.util.FileUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author TS
 * 
 * This controller is for managing third party api
 * it will have code in header to verify
 */
@RestController
@RequestMapping("/source/")
public class SourceController {
    
    @Value("${nsdl.base.path}")
    String basePath;
    
    @Value("${base.app.url.source.agreement}")
    String sourceAgreementUrl;
    
    @Autowired
    AclService aclService;
    
    @Autowired
    SourceService sourceService;
    
    @Autowired
    EsignRequestUploadService esignRequestUploadService;
    
    @Autowired
    EsignRequestService esignRequestService;
    
    @Autowired
    ModuleService moduleService;
    
    @Autowired
    BranchService branchService;
    
    @Autowired
    FileStorageService fileStorageService;
    

    @Autowired
    LoggerService loggerService;
    
    @GetMapping("aes-key")
    public String getAESKey(){
        return sourceService.getAESKey();
    }
    
    @GetMapping("token")
    public String getToken(@RequestParam String appId, @RequestParam String key, @RequestParam String data){
        return sourceService.getToken(appId, key, data);
    }
    
    
    
    @GetMapping("list")
    public List<SourceDto> list(HttpServletRequest request){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.SOURCE, PermissionType.USER));
        
        return SourceDto.toDtoList(sourceService.getAll());
    }
    
    @PostMapping("esign/create")
    public EsignRequestResponseDto esignCreate(
            HttpServletRequest request,
            @RequestParam(required = false) String documentRefNo,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false, defaultValue = "0") Integer documentCategory,
            @RequestParam(required = false, defaultValue = "0") Integer clientType,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date agreementDate,
            @RequestParam(required = false, defaultValue = "0") double sanctionAmount,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false, defaultValue = "0") Integer applicationStatus,
            @RequestParam(required = false) String firmName,
            @RequestParam(required = false) String firmEmail,
            @RequestParam(required = false) String firmAddress,
            @RequestParam(required = false) String firmTelephone,
            @RequestParam(required = false) String firmMobile,
            @RequestParam(required = false) String applicantName,
            @RequestParam(required = false) String applicantEmail,
            @RequestParam(required = false) String applicantMobile,
            @RequestParam(required = false) String applicantTelephone,
            @RequestParam(required = false) String applicantAddress,
            @RequestParam(required = false, defaultValue = "0") Integer applicantSignOption,
            @RequestParam(required = false) String[] coApplicantName,
            @RequestParam(required = false) String[] coApplicantEmail,
            @RequestParam(required = false) String[] coApplicantMobile,
            @RequestParam(required = false) String[] coApplicantTelephone,
            @RequestParam(required = false) String[] coApplicantAddress,
            @RequestParam(required = false, defaultValue = "0") Integer[] coApplicantSignoption,
            @RequestParam(required = false, defaultValue = "0") Integer signOption,
            @RequestParam(required = false, defaultValue = "0") Integer autoAllocateStampPaper,
            @RequestParam(required = false, defaultValue = "0") Integer isStampPaperAttached,
            @RequestParam(required = false) String[] stampEntity,
            @RequestParam(required = false) String[] stampReferenceNo,
            @RequestParam(required = false, defaultValue = "0") Integer[] stampValue,
            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date[] stampProcurementDate,
            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date[] stampExpiryDate,
            @RequestParam(required = false) String[] stampState,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) MultipartFile[] stampFiles,
            @RequestParam(required = false, defaultValue = "0") Integer initiateEsignProcess
        ){
        aclService.verifySourceAccess(request);
        
        EsignRequestFormDto err = new EsignRequestFormDto();
        err.setDocumentRefNo(documentRefNo);
        err.setReferenceId(referenceId);
        err.setEntity(entity);
        err.setDocumentCategory(documentCategory);
        err.setClientType(clientType);
        err.setProduct(product);
        err.setAgreementDate(agreementDate);
        err.setSanctionAmount(sanctionAmount);
        err.setLocation(location);
        err.setBranch(branch);
        err.setApplicationStatus(applicationStatus);
        
        err.setSignOption(signOption);
        err.setSource(aclService.getCurrentRole().getSource().getName());
        err.setSourceId(aclService.getCurrentRole().getSource().getId());
        err.setIsAgreementDownloaded(0);
        err.setAutoAllocateStampPaper(autoAllocateStampPaper);
        err.setIsStampPaperAttached(isStampPaperAttached);
        
        err.setFirmName(firmName);
        err.setFirmEmail(firmEmail);
        err.setFirmMobile(firmMobile);
        err.setFirmTelephone(firmTelephone);
        err.setFirmAddress(firmAddress);
        
        err.setApplicantName(applicantName);
        err.setApplicantEmail(applicantEmail);
        err.setApplicantMobile(applicantMobile);
        err.setApplicantTelephone(applicantTelephone);
        err.setApplicantAddress(applicantAddress);
        err.setApplicantSignOption(applicantSignOption);
        
        if(coApplicantName != null){
            for(int i =0; i < coApplicantName.length; i++){
                EsignRequestApplicantFormDto eraf = new EsignRequestApplicantFormDto();
                eraf.setName(coApplicantName[i]);
                eraf.setEmail(coApplicantEmail != null && coApplicantEmail.length > i ? coApplicantEmail[i] : "");
                eraf.setMobile(coApplicantMobile != null && coApplicantMobile.length > i ? coApplicantMobile[i] : "");
                eraf.setTelephone(coApplicantTelephone != null && coApplicantTelephone.length > i ? coApplicantTelephone[i] : "");
                eraf.setAddress(coApplicantAddress != null && coApplicantAddress.length > i ? coApplicantAddress[i] : "");
                eraf.setSignOption(coApplicantSignoption != null && coApplicantSignoption.length > i ? coApplicantSignoption[i] : 0);

                err.addCoApplicant(eraf);
            }
        }
        
        if(stampReferenceNo != null){
            for(int i =0; i < stampReferenceNo.length; i++){
                StampPaperDto spd = new StampPaperDto();
                spd.setReferenceNo(stampReferenceNo[i]);
                spd.setEsignRequestDocumentRefNo(err.getDocumentRefNo());
                spd.setEsignRequestDocumentCategory(err.getDocumentCategory());
                spd.setProcurementDate(stampProcurementDate != null && stampProcurementDate.length > i ? stampProcurementDate[i] : null);
                spd.setExpiryDate(stampExpiryDate != null && stampExpiryDate.length > i ? stampExpiryDate[i] : null);
                spd.setState(stampState != null && stampState.length > i ? stampState[i] : null);
                spd.setValue(stampValue != null && stampValue.length > i ? stampValue[i] : null);
                spd.setEntity(err.getEntity());
                spd.setSourceId(err.getSourceId());
                spd.setFile(stampFiles != null && stampFiles.length > i ? stampFiles[i] : null);

                if(spd.getFile() != null && !spd.getFile().getOriginalFilename().toLowerCase().contains(".pdf")){
                    throw new ApiException("EC032", "Stamp File "+ spd.getReferenceNo() +": should be in pdf format.");
                }
                
                if(initiateEsignProcess == 1 && spd.getFile() == null){
                    throw new ApiException("EC032", "Stamp "+ spd.getReferenceNo() +": Missing Stamp Paper Scanned PDF");
                }

                err.addStampPaper(spd);
            }
        }
        
        
        if(initiateEsignProcess == 1 && file == null){
            throw new ApiException("EC035","Missing Agreement Scanned PDF");
        }
        
        if(file!= null && !file.getOriginalFilename().toLowerCase().contains(".pdf")){
            throw new ApiException("EC035","Missing Agreement Scanned PDF");
        }
        
        
        if(!EsignRequestStatus.isBeforeInitiate(err.getStatus()) && file == null){
            throw new ApiException("EC035","Missing Agreement Scanned PDF");
        }
        
        for(int i =0; i< aclService.getCurrentRole().getSourceEntities().size(); i++){
            if(aclService.getCurrentRole().getSourceEntities().get(i).getName().equalsIgnoreCase(err.getEntity())){
                err.setEntityId(aclService.getCurrentRole().getSourceEntities().get(i).getId());
            }
        }
        
        if(!(err.getEntityId() > 0)){
            throw new ApiException("EC024", "Missing or Invalid Entity");
        }
        
        
        
        List<EsignRequestUploadDto> erul = esignRequestUploadService.save(err, 0);
        
        EsignRequest er = erul.get(0).getEsignRequest();
        if(file != null){
            String agreementName = er.getToken() + "_agreement.pdf";
            fileStorageService.store(file, basePath, agreementName);
            er.setFile(agreementName);
            er = esignRequestService.save(er);
        }
        
        if(initiateEsignProcess == 1){
            er.setStatus(EsignRequestStatus.WAITING_FOR_INITIATE);
        }
        
        return EsignRequestResponseDto.toDto(er);
    }
    
    
    @GetMapping("esign/{id}/status")
    public EsignRequestStatusDto esignStatus(HttpServletRequest request, @PathVariable(value = "id") Integer id){
        aclService.verifySourceAccess(request);
        EsignRequestStatusDto ers = new EsignRequestStatusDto();
        
        EsignRequest er = null;
        try{
            er = esignRequestService.getEsignRequestById(id);
            aclService.isSourceEntityAllowed(er.getModule().getEntityId());
            ers =  EsignRequestStatusDto.toDto(er, sourceAgreementUrl);
        }catch(Exception e){
            throw new ApiException("10001");
        }
        
        
        return ers;
    }
    
    @GetMapping("esign/{id}/agreement")
    ResponseEntity<byte[]> agreement(HttpServletRequest request, @PathVariable("id") int id, @RequestParam("token") String token, @RequestParam("appId") String appId) {
        aclService.verifySourceAccess(appId, token);

        EsignRequest er = esignRequestService.getEsignRequestById(id);
        aclService.isSourceEntityAllowed(er.getModule().getEntityId());
        

        //basePath + 
        String fullPath = basePath + er.getFile();
        byte[] contents = FileUtil.readBytesFromFile(fullPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = er.getFile();
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }
    
    @GetMapping("esign/{id}/cancel")
    CommonDto cancel(HttpServletRequest request, @PathVariable(value = "id") Integer id){
        aclService.verifySourceAccess(request);
        
        
        EsignRequest er = esignRequestService.getEsignRequestById(id);
        aclService.isSourceEntityAllowed(er.getModule().getEntityId());
        
        esignRequestService.cancelAgreement(id, er,0);
        return new CommonDto("Agreement cancelled");
    }
    
    
    @GetMapping("esign/{id}/resendLink")
    public CommonDto resendLink(HttpServletRequest request, @PathVariable("id") int id) {
        aclService.verifySourceAccess(request);

        loggerService.logEsignRequest("Link re sent by " + aclService.getToken().getUserId() + "::" + aclService.getToken().getUserName() + " for " + id);

        EsignRequest er = esignRequestService.getEsignRequestById(id);

        //removed this to allow send link to customer
        if (er.getModifiedDate().isAfter(LocalDateTime.now().minusMinutes(15))) {
//            throw new ApiException("You need to wait for 15min to regenerate link.");
        }

        er.setModifiedDate(LocalDateTime.now());
        esignRequestService.save(er);

        esignRequestService.sendAgreementSMSEmail(er, null);
        return new CommonDto("sent link to customer on his registered email and mobile number");
    }
    
    @GetMapping("product/list")
    public List<ModuleDto> productList(HttpServletRequest request, @RequestParam(required = false) String entity){
        aclService.verifySourceAccess(request);
        
        
        List<ModuleDto> mrl = moduleService.getProduct(aclService.getCurrentRole().getSourceEntityIds());
        
        //full list
        if(entity == null){
            return mrl;
        }
        
        List<ModuleDto> mrl2 = new ArrayList<>();
        mrl.forEach(m -> {
            if(m.getEntityName().equalsIgnoreCase(entity)){
                mrl2.add(m);
            }
        });
        
        return mrl2;
    }
    
    
    @GetMapping("branch/list")
    public List<BranchDto> branchList(HttpServletRequest request, @RequestParam(required = false) String entity){
        aclService.verifySourceAccess(request);
        List<BranchDto> brl = branchService.getBrancheByEnitities(aclService.getCurrentRole().getSourceEntityIds());
        
        //full list
        if(entity == null){
            return brl;
        }
        
        List<BranchDto> brl2 = new ArrayList<>();
        brl.forEach(b -> {
            if(b.getEntityName().equalsIgnoreCase(entity)){
                brl2.add(b);
            }
        });
        
        return brl2;
    }
}
