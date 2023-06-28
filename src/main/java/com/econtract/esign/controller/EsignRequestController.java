/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignProcess;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.EsignRequestSignerLog;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.EsignRequestStatus;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.model.constant.SignOption;
import com.econtract.esign.model.dto.EsignRequestBulkInitiateDto;
import com.econtract.esign.model.dto.CommonDto;
import com.econtract.esign.model.dto.EmudraDto;
import com.econtract.esign.model.dto.EmudraRequestDto;
import com.econtract.esign.model.dto.EmudraServerDto;
import com.econtract.esign.model.dto.EsignRequestDetailDto;
import com.econtract.esign.model.dto.EsignRequestDto;
import com.econtract.esign.repository.EsignRequestRepository;
import com.econtract.esign.repository.specification.EsignRequestSpecification;
import com.econtract.esign.repository.specification.SearchCriteria;
import com.econtract.esign.security.Token;
import com.econtract.esign.security.TokenService;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.CommunicationService;
import com.econtract.esign.service.EmudraService;
import com.econtract.esign.service.EsignProcessService;
import com.econtract.esign.service.EsignRequestService;
import com.econtract.esign.service.EsignRequestUploadService;
import com.econtract.esign.service.FileStorageService;
import com.econtract.esign.service.LoggerService;
import com.econtract.esign.service.StampPaperService;
import com.econtract.esign.service.StampPaperUploadService;
import com.econtract.esign.service.TaskService;
import com.econtract.esign.util.CommonUtil;
import com.econtract.esign.util.FileUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * @author TS
 */
@RestController
@RequestMapping("/esign/")
public class EsignRequestController {

    @Value("${nsdl.base.path}")
    String basePath;

    @Value("${emudra.response.back.url}")
    String emudraBackUrl;

    @Autowired
    EsignRequestRepository esignRequestRepository;

    @Autowired
    EsignRequestService esignRequestService;

    @Autowired
    TokenService tokenService;

    @Autowired
    StampPaperService stampPaperService;

    @Autowired
    StampPaperUploadService stampPaperUploadService;

    @Autowired
    EsignRequestUploadService esignRequestUploadService;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    TaskService taskService;

    @Autowired
    LoggerService loggerService;

    @Autowired
    AclService aclService;

    @Autowired
    EmudraService emudraService;

    @Autowired
    EsignProcessService esignProcessService;

    @Autowired
    CommunicationService communicationService;

    @GetMapping("list")
    public Page<EsignRequestDto> list(HttpServletRequest request, Pageable pageable, @RequestParam(required = false) int moduleId, @RequestParam(required = false) int documentCategoryId, @RequestParam(required = false) int status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTill,
            @RequestParam(required = false) String state, @RequestParam(required = false) String referenceNumber) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));

        //list basis user
        //currently returning full
        //pagination needs to be implemented
//        return esignRequestRepository.findByIsActive(1);
//        return esignRequestRepository.findByIsActive(1, ers, pageable);
        EsignRequestSpecification a = new EsignRequestSpecification(new SearchCriteria("isActive", ":", "1"));
        Specification<EsignRequest> spec = Specification.where(a);

        if (referenceNumber != null && !referenceNumber.isEmpty()) {
            spec = spec.and(new EsignRequestSpecification(new SearchCriteria("referenceNumber1", ":", referenceNumber)));
        }
        if (state != null && !state.isEmpty()) {
            spec = spec.and(new EsignRequestSpecification(new SearchCriteria("state", ":", state)));
        }
        if (moduleId > 0) {
            spec = spec.and(new EsignRequestSpecification(new SearchCriteria("moduleId", ":", moduleId)));
        }
        if (documentCategoryId > 0) {
            spec = spec.and(new EsignRequestSpecification(new SearchCriteria("documentCategoryId", ":", documentCategoryId)));
        }
        if (status > 0) {
            spec = spec.and(new EsignRequestSpecification(new SearchCriteria("status", ":", status)));
        }

        //TODO: date filter showing error needs to resolve
        if (dateFrom != null) {
            spec = spec.and(new EsignRequestSpecification(new SearchCriteria("date", "<", dateFrom)));
        }
        if (dateTill != null) {
            spec = spec.and(new EsignRequestSpecification(new SearchCriteria("date", ">", dateTill)));
        }

        spec = aclService.esignRequest(spec);

        Page<EsignRequest> er = esignRequestRepository.findAll(spec, pageable);
        return EsignRequestDto.toPageDto(er);
    }

    @GetMapping("details/{token}")
    EsignRequestDetailDto details(HttpServletRequest request, @PathVariable("token") String token) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));

        EsignRequestDetailDto err = esignRequestService.getEsignRequestByToken(token);
        return err;
    }

    @GetMapping("agreement/{id}")
    ResponseEntity<byte[]> agreement(HttpServletRequest request, @PathVariable("id") int id, @RequestParam("token") String token, @RequestParam(required = false) String download) {
        aclService.verifyAccess(token, Arrays.asList(PermissionType.ESIGNREQUEST));

        EsignRequest er = esignRequestService.getEsignRequestById(id);

        //set on download status
        if (er.getIsAgreementDownloaded() != 1) {
            esignRequestService.downloadAgreement(er, aclService.getUserId());
        }

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

    @PostMapping("sign/{id}")
    CommonDto sign(HttpServletRequest request, @PathVariable("id") int id, @RequestParam MultipartFile file) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));
        Token t = aclService.getToken();

        if (!file.getOriginalFilename().toLowerCase().contains(".pdf")) {
            throw new ApiException("Agreement should be in pdf format.");
        }
        EsignRequest er = esignRequestService.getEsignRequestById(id);
        EsignProcess p = esignProcessService.getProcess(er.getModuleId(), er.getDocumentCategoryId(), er.getClientType(), er.getSourceId());

        String match = p.getProcessSettings().getOfficeGroup();
        String match1 = t.getGroup();

        String[] groupArray = match.split(",", 3);
        String[] userGroupArray = match1.split(",", 3);

        List<String> firstList = new ArrayList<String>(Arrays.asList(groupArray));
        ArrayList<String> secondList = new ArrayList<String>(Arrays.asList(userGroupArray));
        boolean notInGroup = firstList.stream().filter(secondList::contains).collect(Collectors.toList()).isEmpty();

        if (notInGroup == false) {
            esignRequestService.setSignatorySigned(id, file, t.getUserId(), commonUtil.getRemoteAddr(request));
            communicationService.sendEsignUpdateToSource(er);
            return new CommonDto("Agreement signed successfully");
        }
        throw new ApiException("You are not allowed to sign this document.");
    }

    @PostMapping("sign/emudra/request/{id}")
    EmudraRequestDto EmudraSignRequest(HttpServletRequest request, @PathVariable("id") int id) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));
        Token t = aclService.getToken();

        EsignRequest er = esignRequestService.getEsignRequestById(id);

        if (!esignRequestService.canSignatorySign(er)) {
            throw new ApiException("You are not allowed to sign at this stage.");
        }

        EsignProcess p = esignProcessService.getProcess(er.getModuleId(), er.getDocumentCategoryId(), er.getClientType(), er.getSourceId());

        String match = p.getProcessSettings().getOfficeGroup();
        String match1 = t.getGroup();

        if (match.isEmpty()) {
            throw new ApiException("Agreement group not set in config.");
        }
        if (match1.isEmpty()) {
            throw new ApiException("No group assigned to user.");
        }

        String[] groupArray = match.split(",", 3);
        String[] userGroupArray = match1.split(",", 3);

        List<String> firstList = new ArrayList<String>(Arrays.asList(groupArray));
        ArrayList<String> secondList = new ArrayList<String>(Arrays.asList(userGroupArray));
        boolean notInGroup = firstList.stream().filter(secondList::contains).collect(Collectors.toList()).isEmpty();

        if (notInGroup == false) {
            User user = aclService.getUser();
            EsignRequestSignerLog ersl = esignRequestService.createSignatoryEsignLog(id, user, er.getFile(), commonUtil.getRemoteAddr(request));
            return emudraService.getSignedRequest(er, ersl, user);
        }

        throw new ApiException("You are not allowed to sign this document.");
    }

    @PostMapping("sign/emudra/response/{transactionId}")
    String EmudraSignResponse(HttpServletRequest request, HttpServletResponse response, @PathVariable("transactionId") Integer tid,
            @RequestParam("ErrorMessage") String ErrorMessage, @RequestParam("ReturnStatus") String ReturnStatus,
            @RequestParam("Returnvalue") String Returnvalue, @RequestParam("Transactionnumber") String Transactionnumber,
            @RequestParam("Referencenumber") String Referencenumber) {

        loggerService.logApi("emudra id =" + tid + " ErrorMessage:" + ErrorMessage + "  ReturnStatus:" + ReturnStatus + " Referencenumber:" + Referencenumber + " Transactionnumber:" + Transactionnumber);

        EmudraServerDto esr = new EmudraServerDto();
        esr.setErrorMessage(ErrorMessage);
        esr.setReturnStatus(ReturnStatus);
        esr.setReturnvalue(Returnvalue);
        esr.setTransactionnumber(Transactionnumber);
        esr.setReferencenumber(Referencenumber);

        EsignRequestSignerLog ersl = esignRequestService.getEsignRequestSignerLog(tid);
        EsignRequest er = esignRequestService.getEsignRequestById(ersl.getEsignRequestId());

        String msg = esr.getErrorMessage();
        ersl.setResponseThirdParty(msg);
        if (esr.getReturnStatus().equalsIgnoreCase("Success")) {
            emudraService.response(esr, er, ersl);
        } else {
            esignRequestService.saveSignLog(ersl);
        }

        if (er.getOfficeSignFirst() == 1 && er.getStatus() == EsignRequestStatus.SINGED_BY_BUSINESS_SIGNATOR) {
            List<EsignRequestSignee> ersls = esignRequestService.getEsignRequestSigneeById(er.getId());
            esignRequestService.sendAgreementSMSEmail(er, ersls);
        }

        communicationService.sendEsignUpdateToSource(er);
        response.setHeader("Location", emudraBackUrl.replace("{token}", er.getToken()) + "?msg=" + msg);
        response.setStatus(302);
        return "";
    }

    @PostMapping("initiate/{id}")
    CommonDto initiate(HttpServletRequest request, @PathVariable("id") int id,
            @PathVariable(name = "agreement", required = false) MultipartFile agreement,
            @PathVariable(name = "stampPapers", required = false) MultipartFile[] stampPapers) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));
        loggerService.logEsignRequest("Recieved request to initiate: " + id);
        loggerService.logEsignRequest("Agreement File provided: " + agreement);
        loggerService.logEsignRequest("Stamp File provided: " + stampPapers.length);

        if (agreement != null && !agreement.getOriginalFilename().contains(".pdf")) {
            throw new ApiException("Only accepts in pdf format.");
        }

        EsignRequest er = esignRequestService.getEsignRequestById(id);
        List<EsignRequestSignee> ersl = esignRequestService.getEsignRequestSigneeById(er.getId());

        if (!esignRequestService.canInitiate(er)) {
            throw new ApiException("You can not initiate this agreement.");
        }

        if (agreement != null) {
            String agreementName = er.getToken() + "_agreement.pdf";
            fileStorageService.store(agreement, basePath, agreementName);
            er.setFile(agreementName);
            er = esignRequestRepository.save(er);
        }

        if (er.getIsStampAttached() == 1) {
            boolean status = esignRequestService.savePreAttachedStampPapers(er, stampPapers);
            if (!status) {
                throw new ApiException("Stamp Paper required");
            }
        }

        //upload agreement
        String msg = esignRequestService.initiateAgreement(er, ersl, aclService.getUserId());

        if (!msg.isEmpty()) {
            throw new ApiException(msg);
        }

        //send mail for signing to user if customer is first to sign
        if (er.getOfficeSignFirst() == 0) {
            esignRequestService.sendAgreementSMSEmail(er, ersl);
        }

        communicationService.sendEsignUpdateToSource(er);
        return new CommonDto("upload completed");
    }

    @PostMapping("initiate/bulk")
    CommonDto initiateBulk(HttpServletRequest request, @RequestBody EsignRequestBulkInitiateDto erbir) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));
        List<Integer> ids = erbir.getIds();

        //create task for this
        List<EsignRequest> erl = esignRequestService.getEsignRequests(ids);

        if (ids.size() == 0) {
            throw new ApiException("Please select agreement to initiate");
        }

        if (erl.size() != ids.size()) {
            throw new ApiException("Invalid agreement added to list");
        }

        //validate status
        erl.forEach(er -> {
            if (!esignRequestService.canInitiate(er)) {
                throw new ApiException("Can not initiate: " + er.getReferenceNumber1());
            }
        });

        //create funciton in esign service for task creation
        taskService.addEsginRequestInitiationTask(erl, aclService.getUserId());

        //update status when task is created
        erl = esignRequestService.taskCreated(erl, aclService.getUserId());

        return new CommonDto("Initiation is in progress");
    }

    @PostMapping("upload")
    CommonDto upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));

        if (!file.getOriginalFilename().toLowerCase().contains(".xlsx")) {
            throw new ApiException("Agreement details should be in xlsx format.");
        }

        esignRequestUploadService.save(file, aclService.getUser());

        return new CommonDto("upload completed");
    }

    @GetMapping("cancel/{id}")
    CommonDto cancel(HttpServletRequest request, @PathVariable("id") int id) {
        Token t = tokenService.verifyJwt(request);
        EsignRequest er = esignRequestService.getEsignRequestById(id);
        esignRequestService.cancelAgreement(id, er, t.getUserId());
        communicationService.sendEsignUpdateToSource(er);
        return new CommonDto("Agreement cancelled");
    }

    @PostMapping("upload/agreement/{id}")
    CommonDto uploadAgreement(HttpServletRequest request, @PathVariable("id") int id,
            @RequestParam("agreement") MultipartFile agreement,
            @PathVariable(name = "stampPapers", required = false) MultipartFile[] stampPapers) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));

        if (!agreement.getOriginalFilename().toLowerCase().contains(".pdf")) {
            throw new ApiException("Agreement should be in pdf format.");
        }

        EsignRequest er = esignRequestService.getEsignRequestById(id);

        if (!esignRequestService.canInitiate(er)) {
            throw new ApiException("You can not upload agreement at this state");
        }

        boolean status = esignRequestService.saveAgreement(er, agreement, aclService.getUserId());
        if (!status) {
            throw new ApiException("Failed to upload agreement");
        }

        if (er.getIsStampAttached() == 1) {
            status = esignRequestService.savePreAttachedStampPapers(er, stampPapers);
            if (!status) {
                throw new ApiException("Stamp Paper required");
            }
        }

        //no mail in a case of just upload
//        if(er.getOfficeSignFirst() == 1){
//            List<EsignRequestSignee> ersl = esignRequestService.getEsignRequestSigneeById(er.getId());
//            esignRequestService.sendAgreementSMSEmail(er, ersl);
//        }
        return new CommonDto("upload completed");
    }

    @GetMapping("resendLink/{id}")
    public CommonDto resendLink(HttpServletRequest request, @PathVariable("id") int id) {
        aclService.verifyAccess(request, Arrays.asList(PermissionType.ESIGNREQUEST));

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
}
