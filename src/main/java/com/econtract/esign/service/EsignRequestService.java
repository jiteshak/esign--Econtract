/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignProcess;
import com.econtract.esign.model.constant.EsignProcessSettings;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.EsignRequestSignerLog;
import com.econtract.esign.model.StampPaper;
import com.econtract.esign.model.Template;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.EsignMethod;
import com.econtract.esign.model.constant.EsignRequestStatus;
import com.econtract.esign.model.constant.EsignType;
import com.econtract.esign.model.constant.LinkType;
import com.econtract.esign.model.constant.SignOption;
import com.econtract.esign.model.constant.StampPaperAllocation;
import com.econtract.esign.model.constant.TemplateType;
import com.econtract.esign.model.dto.EsignRequestDetailDto;
import com.econtract.esign.model.dto.EsignRequestFormDto;
import com.econtract.esign.repository.EsignRequestRepository;
import com.econtract.esign.repository.EsignRequestSigneeRepository;
import com.econtract.esign.repository.EsignRequestSignerLogRepository;
import com.econtract.esign.util.PasswordUtil;
import com.econtract.esign.util.PdfUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream$AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author TS
 */
@Service
public class EsignRequestService {
    
    @Autowired
    EsignRequestSigneeRepository esignRequestSigneeRepository;
    
    @Autowired
    EsignRequestRepository esignRequestRepository;
    
    @Autowired
    EsignRequestSignerLogRepository esignRequestSignerLogRepository;
    
    @Autowired
    EsignProcessService esignProcessService;
    
    @Autowired
    StampPaperService stampPaperService;
    
    
    @Autowired
    StampPaperUploadService stampPaperUploadService;
    
    @Autowired
    TemplateService templateService;
    
    @Autowired
    DocumentCategoryStateService documentCategoryStateService;
    
    
    @Autowired
    LoggerService loggerService;
    
    @Autowired
    CommunicationService communicationService;
    
    @Value("${nsdl.base.path}")
    String basePath;
    
    @Value("${base.stamp.paper.path}")
    String baseStampPaperPath;
    
    @Value("${base.template.path}")
    String baseTemplatePath;
    
    @Value("${base.wkhtmltopdf.path}")
    String wkhtmltopdf;
    
    public EsignRequest save(EsignRequest er){
        return esignRequestRepository.save(er);
    }
    
    public EsignRequestSignee getEsignRequestSigneeByToken(String token){
        Optional<EsignRequestSignee> esO =  esignRequestSigneeRepository.findByToken(token);
        if(!esO.isPresent()){
            throw new ApiException("Invalid token");
        }
        
        return esO.get();
    }
    
    public List<EsignRequestSignee> getEsignRequestSigneeById(int id){
        List<EsignRequestSignee> ersl =  esignRequestSigneeRepository.findByEsignRequestId(id);
        return ersl;
    }
    
    public EsignRequest getEsignRequest(EsignRequestSignee ers){
        Optional<EsignRequest> erO = esignRequestRepository.findById(ers.getEsignRequestId());
        if(!erO.isPresent()){
            throw new ApiException("Invalid token");
        }
        return erO.get();
    }
    
    public EsignRequest getEsignRequestById(int id){
        Optional<EsignRequest> erO = esignRequestRepository.findById(id);
        if(!erO.isPresent()){
            throw new ApiException("Invalid");
        }
        return erO.get();
    }
    
    public EsignRequestDetailDto getEsignRequest(String token){
        EsignRequestDetailDto err = new EsignRequestDetailDto();
        
        //find signer by token
        Optional<EsignRequestSignee> esO =  esignRequestSigneeRepository.findByToken(token);
        if(!esO.isPresent()){
            throw new ApiException("Invalid token");
        }
        EsignRequestSignee ers = esO.get();
        
        Optional<EsignRequest> erO = esignRequestRepository.findById(ers.getEsignRequestId());
        if(!erO.isPresent()){
            throw new ApiException("Invalid token");
        }
        EsignRequest er = erO.get();
        
        err.setEsignRequest(er, er.getSignees());
//        err.setEsignRequestSignees(esignRequestSigneeRepository.findByEsignRequestId(err.getEsignRequest().getId()));
        err.setEsignRequestSignerLogs(esignRequestSignerLogRepository.findByEsignRequestId(err.getEsignRequest().getId()));
        
        return err;
    }
    
    public EsignRequestDetailDto getEsignRequest(int id){
        EsignRequestDetailDto err = new EsignRequestDetailDto();
        
        
        Optional<EsignRequest> erO = esignRequestRepository.findById(id);
        if(!erO.isPresent()){
            throw new ApiException("Invalid token");
        }
        EsignRequest er = erO.get();
        
        err.setEsignRequest(er, er.getSignees());
//        err.setEsignRequestSignees(esignRequestSigneeRepository.findByEsignRequestId(err.getEsignRequest().getId()));
        err.setEsignRequestSignerLogs(esignRequestSignerLogRepository.findByEsignRequestId(err.getEsignRequest().getId()));
        
        return err;
    }
    
    public EsignRequestDetailDto getEsignRequestByToken(String token){
        EsignRequestDetailDto err = new EsignRequestDetailDto();
        
        
        Optional<EsignRequest> erO = esignRequestRepository.findByToken(token);
        if(!erO.isPresent()){
            throw new ApiException("Invalid token");
        }
        
        EsignRequest er = erO.get();
        
        err.setEsignRequest(er, er.getSignees());
//        err.setEsignRequestSignees(esignRequestSigneeRepository.findByEsignRequestId(err.getEsignRequest().getId()));
        err.setEsignRequestSignerLogs(esignRequestSignerLogRepository.findByEsignRequestId(err.getEsignRequest().getId()));
        
        return err;
    }
    
    public void createCustomerEsignLog(EsignRequestSignee ers, String file, String createdByIp){
        EsignRequestSignerLog ersl = new EsignRequestSignerLog();
        ersl.setSignatoryId(ers.getId());
        ersl.setEsignRequestId(ers.getEsignRequestId());
        ersl.setApplicantName(ers.getApplicantName());
        ersl.setApplicantContact(ers.getApplicantContact());
        ersl.setType(LinkType.APPLICANT);
        ersl.setSignType(EsignType.AADHAR);
        ersl.setStatus(0);
        ersl.setFile(file);
        ersl.setTransactionId(ers.getTransactionId());
        ersl.setSigner(EsignMethod.ADHAAR);
        ersl.setIp(createdByIp);
        ersl.setSignDate(LocalDateTime.now());
        ersl = esignRequestSignerLogRepository.save(ersl);
    }
    
    public void completeCustomerEsignLog(EsignRequest er, EsignRequestSignee ers, String msg, String createdByIp){
        Optional<EsignRequestSignerLog> erslO = esignRequestSignerLogRepository.findFirstByEsignRequestIdAndSignatoryIdAndTypeOrderByIdDesc(er.getId(), ers.getId(), LinkType.APPLICANT);
        EsignRequestSignerLog ersl = erslO.get();
        ersl.setStatus(1);
        ersl.setResponseThirdParty(msg);
        ersl.setIp(createdByIp);
        ersl.setFile(er.getFile());
        ersl.setSignDate(LocalDateTime.now());
        ersl = esignRequestSignerLogRepository.save(ersl);
        
        ers.setIp(createdByIp);
        ers.setStatus(1);
        esignRequestSigneeRepository.save(ers);
        
        //check all is completed then mark all signed
        int isAllDone = 1;
        List<EsignRequestSignee> erslist = esignRequestSigneeRepository.findByEsignRequestId(er.getId());
        for(int i = 0; i < erslist.size(); i++){
            if(erslist.get(i).getStatus() == 0){
                isAllDone = 0;
            }
        }
        
        if(isAllDone == 1){
            er.setStatus(EsignRequestStatus.SINGED_BY_CUSTOMER);
            esignRequestRepository.save(er);
        }
    }
    
    
    public EsignRequestSignerLog createSignatoryEsignLog(int esignRequestId,User user, String file, String createdByIp){
        EsignRequestSignerLog ersl = new EsignRequestSignerLog();
        ersl.setSignatoryId(user.getId());
        ersl.setEsignRequestId(esignRequestId);
        ersl.setApplicantName(user.getName());
        ersl.setApplicantContact(user.getContact().toString());
        ersl.setType(LinkType.USER);
        ersl.setSignType(EsignType.EMUDRA);
        ersl.setStatus(0);
        ersl.setFile(file);
        ersl.setTransactionId(PasswordUtil.generateOpaque(user.getId().toString()));
        ersl.setSigner(EsignMethod.EMUDRA);
        ersl.setIp(createdByIp);
        ersl.setSignDate(LocalDateTime.now());
        ersl = esignRequestSignerLogRepository.save(ersl);
        
        return ersl;
    }
    
    public void completeSignatoryEsignLog(EsignRequest er, User user, String msg, String createdByIp){
        Optional<EsignRequestSignerLog> erslO = esignRequestSignerLogRepository.findFirstByEsignRequestIdAndSignatoryIdAndTypeOrderByIdDesc(er.getId(), user.getId(), LinkType.USER);
        EsignRequestSignerLog ersl = erslO.get();
        ersl.setStatus(1);
        ersl.setResponseThirdParty(msg);
        ersl.setIp(createdByIp);
        ersl.setFile(er.getFile());
        ersl.setSignDate(LocalDateTime.now());
        ersl = esignRequestSignerLogRepository.save(ersl);
        
        
        //check sign log
        //check process
        //update er status
        
        //check all is completed then mark all signed
        int isAllDone = 1;
        List<EsignRequestSignee> erslist = esignRequestSigneeRepository.findByEsignRequestId(er.getId());
        for(int i = 0; i < erslist.size(); i++){
            if(erslist.get(i).getStatus() == 0){
                isAllDone = 0;
            }
        }
        
        if(isAllDone == 1){
            er.setStatus(EsignRequestStatus.SINGED_BY_CUSTOMER);
            esignRequestRepository.save(er);
        }
    }
    
     
    public void completeCustomerEsignLogByMobile(EsignRequest er, EsignRequestSignee ers, String createdByIp){
        
        EsignRequestSignerLog ersl = new EsignRequestSignerLog();
        ersl.setResponseThirdParty("");
        ersl.setSignatoryId(ers.getId());
        ersl.setEsignRequestId(ers.getEsignRequestId());
        ersl.setApplicantName(ers.getApplicantName());
        ersl.setApplicantContact(ers.getApplicantContact());
        ersl.setType(LinkType.APPLICANT);
        ersl.setSignType(EsignType.OTP);
        ersl.setStatus(1);
        ersl.setFile(er.getFile());
        ersl.setTransactionId(ers.getToken());
        ersl.setSigner(EsignMethod.OTP);
        ersl.setIp(createdByIp);
        ersl.setSignDate(LocalDateTime.now());
        ersl = esignRequestSignerLogRepository.save(ersl);
        
        ers.setTransactionId(ers.getToken());
        ers.setIp(createdByIp);
        ers.setStatus(1);
        esignRequestSigneeRepository.save(ers);
        
        //check all is completed then mark all signed
        int isAllDone = 1;
        List<EsignRequestSignee> erslist = esignRequestSigneeRepository.findByEsignRequestId(er.getId());
        for(int i = 0; i < erslist.size(); i++){
            if(erslist.get(i).getStatus() == 0){
                isAllDone = 0;
            }
        }
        
        if(isAllDone == 1){
            er.setStatus(EsignRequestStatus.SINGED_BY_CUSTOMER);
            esignRequestRepository.save(er);
        }
    }
    
    public void setSignatorySigned(int id, MultipartFile agreement, int updatedBy, String createdByIp){
        EsignRequest er = this.getEsignRequestById(id);
        
        try{
            //store file
            String file = er.getFile().replace(".pdf", "_abfl.pdf");
            byte[] bytes = agreement.getBytes();
            Path path = Paths.get(basePath + file);
            Files.write(path, bytes);
            
            //update file and status
            er.setFile(file);
            er.setStatus(EsignRequestStatus.SINGED_BY_BUSINESS_SIGNATOR);
            er.setModifiedBy(updatedBy);
            esignRequestRepository.save(er);
            
            
            createCompleteSignatoryEsignLog(er , updatedBy, createdByIp);
        }catch(Exception ex){
            throw new ApiException(ex.getMessage());
        }
        
    }
    
    public void createCompleteSignatoryEsignLog(EsignRequest er, int createdBy, String createdByIp){
        EsignRequestSignerLog ersl = new EsignRequestSignerLog();
        ersl.setSignatoryId(createdBy);
        ersl.setEsignRequestId(er.getId());
        ersl.setApplicantName("NA");
        ersl.setApplicantContact("NA");
        ersl.setType(LinkType.USER);
        ersl.setSignType(EsignType.DSC);
        ersl.setStatus(1);
        ersl.setFile(er.getFile());
        ersl.setTransactionId(PasswordUtil.generateOpaque(er.getId().toString()));
        ersl.setSigner(EsignMethod.DSC);
        ersl.setIp(createdByIp); 
        ersl.setSignDate(LocalDateTime.now());
        ersl.setUserId(createdBy);
        ersl = esignRequestSignerLogRepository.save(ersl);
    }
    
    
    public boolean saveAgreement(EsignRequest er, MultipartFile agreement, int updatedBy){
        try{
            //store file
            String file = er.getToken() + PasswordUtil.generateOtp() + "_agreement.pdf";
            byte[] bytes = agreement.getBytes();
            Path path = Paths.get(basePath + file);
            Files.write(path, bytes);
            
            //update file and status
            er.setFile(file);
            er.setStatus(EsignRequestStatus.AGREEMENT_UPLOADED);
            er.setModifiedBy(updatedBy);
            er = esignRequestRepository.save(er);
        }catch(Exception ex){
            return false;
        }
        
        return true;
    }
    
    
    public boolean savePreAttachedStampPapers(EsignRequest er, MultipartFile[] stampPapers){
        List<StampPaper> spl = stampPaperService.getAllocatedStampPaper(er.getId());
        
        
        if(stampPapers.length == 0){
            for(int i = 0; i < spl.size(); i++){
                if(spl.get(i).getFile1().isEmpty()){
                    return false;
                }
            }
            
            return true;
        }
        
        if(spl.size() != stampPapers.length){
            return false;
        }
        
        for(int i = 0; i < spl.size(); i++){
            if(!stampPapers[i].getOriginalFilename().toLowerCase().contains(".pdf")){
                return false;
            }
            
            StampPaper sp = spl.get(i);
            String file = stampPaperUploadService.storeFile(sp, stampPapers[i]);
            sp.setFile1(file);
            stampPaperService.save(sp);
        }
        
        
        return true;
    }
    
    public boolean canInitiate(EsignRequest er){
        if(er.getStatus() != EsignRequestStatus.NEW && er.getStatus() != EsignRequestStatus.AGREEMENT_UPLOADED && er.getStatus() != EsignRequestStatus.INITIATIATION_FAILED){
            return false;
        }
        
        return true;
    }
    
    public boolean canCustomerSign(EsignRequest er, EsignRequestSignee ers){
        if (er.getIsCustomerSignRequired() == 1 && er.getOfficeSignFirst() == 0 && er.getStatus() == EsignRequestStatus.INITIATED && ers.getStatus() == 0){
            return true;
        }
        
        if (er.getIsCustomerSignRequired() == 1 && er.getOfficeSignFirst() == 1 && er.getStatus() == EsignRequestStatus.SINGED_BY_BUSINESS_SIGNATOR && ers.getStatus() == 0){
            return true;
        }
        
        return false;
    }
    
    public boolean canSignatorySign(EsignRequest er){
        if (er.getIsSignatorySignRequired()== 1 && er.getOfficeSignFirst() == 1 && er.getStatus() == EsignRequestStatus.INITIATED){
            return true;
        }
        
        if (er.getIsSignatorySignRequired() == 1 && er.getOfficeSignFirst() == 0 && er.getStatus() == EsignRequestStatus.SINGED_BY_CUSTOMER){
            return true;
        }
        
        return false;
    }
    
    public String initiateAgreement(EsignRequest er, List<EsignRequestSignee> ersl, int updatedBy){
        List<StampPaper> lsp = new ArrayList<StampPaper>();
        Template agT = new Template();
        Template annexIT = new Template();
        
        int src = 0;
        if(er.getSourceId() !=  null){
            src = er.getSourceId();
        }
        loggerService.logEsignRequest("source set to " + src);
        
        EsignProcess ep = esignProcessService.getProcess(er.getModuleId(), er.getDocumentCategoryId(), er.getClientType(), src);
        if(ep.getId() == null){
            return "Process not found";
        }
        loggerService.logEsignRequest(ep.toString());
        
        //if default then change with option
        if(er.getSignOption() == null){
            er.setSignOption(ep.getSignOption());
            loggerService.logEsignRequest("set option to " + ep.getSignOption());
        }else if(er.getSignOption() == SignOption.SYSTEM_DEFAULT){
            er.setSignOption(ep.getSignOption());
            loggerService.logEsignRequest("set option from default to " + ep.getSignOption());
        }
        
        for(int i = 0; i < ersl.size(); i++){
            if(ersl.get(i).getSignOption() == SignOption.SYSTEM_DEFAULT){
                ersl.get(i).setSignOption(er.getSignOption());
            }
        }
        this.saveAllSignees(ersl);
        
        
        //update configured settings to econ_txn
        er.setIsSignatorySignRequired(ep.getIsSignatorySignRequired());
        er.setIsCustomerSignRequired(ep.getIsCuctomerSignRequired());
        er.setOfficeSignFirst(ep.getProcessSettings().getOfficeSignFirst());
        er.setNumberOfOfficeSign(ep.getProcessSettings().getNumberOfOfficeSign());
        
        
        //set stamp paper
        if(er.getAutoAllocateStampPaper() != StampPaperAllocation.SYSTEM_DEFAULT){
           if(er.getAutoAllocateStampPaper() == StampPaperAllocation.YES){
            ep.getProcessSettings().setAllocateStampPaper(1);
           }else{
            ep.getProcessSettings().setAllocateStampPaper(0);
           }
        }
        
        if(er.getIsStampAttached() == 1){
            ep.getProcessSettings().setAllocateStampPaper(0);
        }
        
        //get template header
        agT = templateService.getTemplate(er.getModuleId(), er.getClientType(), er.getDocumentCategoryId(), TemplateType.AGMNT);
        if(agT.getId() == null && ep.getProcessSettings().getAgreement().equalsIgnoreCase(EsignProcessSettings.SYSTEM)){
            return "Template agreement is not configured";
        }
        annexIT = templateService.getTemplate(er.getModuleId(), er.getClientType(), er.getDocumentCategoryId(), TemplateType.ANNEXI);
        if(annexIT.getId() == null && ep.getProcessSettings().getAgreement().equalsIgnoreCase(EsignProcessSettings.SYSTEM)){
            return "Template annex I is not configured";
        }
        if(ep.getProcessSettings().getAgreement().equalsIgnoreCase(EsignProcessSettings.EXTERNAL)){
            if(er.getFile()== null){
                return "Agreement required";
            }
        }
        
        if(agT != null){
            loggerService.logEsignRequest("Agreement Template: " + agT.toString());
        }
        if(annexIT != null){
            loggerService.logEsignRequest("annexIT Template " + annexIT.toString());
        }
        
        
        //get stamp paper if in option
        if(ep.getProcessSettings().getAllocateStampPaper() == 1){
            int amount = documentCategoryStateService.getStampPaperAmount(er.getState(), er.getModuleId(),er.getDocumentCategoryId(), er.getSanctionAmount().intValue());
            loggerService.logEsignRequest("For ["+ er.getSanctionAmount().intValue() +"] Stamp paper Value is " + amount);
            if(amount == 0){
                return "Stamp Paper value not available";
            }
            lsp = stampPaperService.allocateStampPaper(er.getId(), er.getState(), amount);
            if(lsp.isEmpty()){  
                return "Stamp Paper not available";
            }
            
            lsp.forEach(sp -> {
                loggerService.logEsignRequest("Stamp paper ref: " + sp.toString());
            });
        }
        
        if(er.getIsStampAttached() == 1){
            lsp = stampPaperService.getAllocatedStampPaper(er.getId());
            lsp.forEach(sp -> {
                loggerService.logEsignRequest("Stamp paper ref: " + sp.toString());
            });
        }
        
        
        loggerService.logEsignRequest("Agreement stiching logic");
        //generate document
        boolean completed = prepareAgreement(ep, er, lsp, agT, annexIT);
        if(!completed && er.getIsStampAttached() != 1){
            //unload stamp papers
            stampPaperService.deallocateStampPaper(er.getId(), lsp);
            return "failed generate agreement";
        }
        
        er.setStatus(EsignRequestStatus.INITIATED);
        esignRequestRepository.save(er);
        
        loggerService.logEsignRequest("Agreement Completed Initiate");
        
        //send email or sms
//        this.sendAgreementSMSEmail(er);
        return "";
    }
    
    public boolean prepareAgreement(EsignProcess ep, EsignRequest er, List<StampPaper> lsp, Template agT, Template annexIT){
        try{
            String file = er.getToken() + ".pdf";

            PDDocument document = new PDDocument();
            List<PDDocument> spDL = new ArrayList<>();
            PDDocument sp1 = null;
            PDDocument sp2 = null;

            if(ep.getProcessSettings().getAllocateStampPaper() == 1 || er.getIsStampAttached() == 1){
                loggerService.logEsignRequest("Agreement stiching stamp paper");
                //add stamp paper image
                for(int i = 0; i < lsp.size(); i++){
                    
                    StampPaper sp = lsp.get(i);
                    loggerService.logEsignRequest("Agreement stiching stamp paper : " + sp.getReferenceNo());
                    
                    //if image or pdf is uploaded
                    if(lsp.get(i).getFile1().contains(".pdf")){
                        sp1 = PDDocument.load(PdfUtil.cleanFile(baseStampPaperPath + lsp.get(i).getFile1()));
                        for(int j = 0; j < sp1.getNumberOfPages();j++){
                            if(j == 0){
                                document.addPage(this.addPartydetails(sp1.getPage(j) , document, er, sp));
                            }else{
                                document.addPage(sp1.getPage(j));
                            }
                        }
                    }else if(lsp.get(i).getFile1() != null){
                        PDPage spp1 = PdfUtil.getImagePage(baseStampPaperPath + lsp.get(i).getFile1(), document);
                        spp1 = this.addPartydetails(spp1 , document, er, sp);
                        document.addPage(spp1);
                    }
                    
                    if(lsp.get(i).getFile2() == null){
                        //no code //only file2 can be null
                    }else  if(lsp.get(i).getFile2().contains(".pdf")){
                        sp2 = PDDocument.load(PdfUtil.cleanFile(baseStampPaperPath + lsp.get(i).getFile1()));
                        for(int j = 0; j < sp2.getNumberOfPages();j++){
                            document.addPage(sp2.getPage(j));
                        }
                    }else if(lsp.get(i).getFile2() != null){
                        document.addPage(PdfUtil.getImagePage(baseStampPaperPath + lsp.get(i).getFile2(), document));
                    }
                    
                    //collect open documents
                    spDL.add(sp1);
                    spDL.add(sp2);
                }
            }

            PDDocument tnc = null;
            if(ep.getProcessSettings().getAgreement().equalsIgnoreCase(EsignProcessSettings.SYSTEM)){
                loggerService.logEsignRequest("Agreement stiching system agreement : " + baseTemplatePath + agT.getDocument());
                
                //add template of tnc
                tnc = PDDocument.load(new File(baseTemplatePath + agT.getDocument()));
                for(int i = 0; i < tnc.getNumberOfPages();i++){
                    document.addPage(tnc.getPage(i));
                }
            }else if(ep.getProcessSettings().getAgreement().equalsIgnoreCase(EsignProcessSettings.EXTERNAL)){
                loggerService.logEsignRequest("Agreement stiching external agreement : " + basePath + er.getFile());
                
                //add template of tnc
                tnc = PDDocument.load(new File(basePath + er.getFile()));
                for(int i = 0; i < tnc.getNumberOfPages();i++){
                    document.addPage(tnc.getPage(i));
                }
            }


            PDDocument annexpdf = null;
            if(ep.getProcessSettings().getAgreement().equalsIgnoreCase(EsignProcessSettings.SYSTEM)){
                loggerService.logEsignRequest("Agreement stiching system annexI");
                //set html template 
                String annexfile = PdfUtil.generatePdfFromHTML(prepareAnnexureI(annexIT.getDocument(), er), baseTemplatePath, wkhtmltopdf);
                annexpdf = PDDocument.load(new File(baseTemplatePath + annexfile));
                for(int i = 0; i < annexpdf.getNumberOfPages();i++){
                    document.addPage(annexpdf.getPage(i));
                }
            }
            
            PDDocument sdpdf = null;
            if(ep.getProcessSettings().getStampPaperAnnexure() == 1){
                loggerService.logEsignRequest("Agreement stiching system sp annex");
                String sdfile = PdfUtil.generatePdfFromHTML(prepareStampDutyHtml(lsp), baseTemplatePath, wkhtmltopdf);
                sdpdf = PDDocument.load(new File(baseTemplatePath + sdfile));
                for(int i = 0; i < sdpdf.getNumberOfPages();i++){
                    document.addPage(sdpdf.getPage(i));
                }
            }


            document.save(basePath  + file);
            document.close();
            loggerService.logEsignRequest("Agreement stiched");
            
            //close all stamps
            for(int s = 0; s < spDL.size(); s++){
                if(spDL.get(s) != null){
                    spDL.get(s).close();
                }
            }
            
            if(!ep.getProcessSettings().getAgreement().equalsIgnoreCase(EsignProcessSettings.FALSE)){
                //remove file once done
                tnc.close();
                if(annexpdf != null){
                    annexpdf.close();
                    File f= new File(baseTemplatePath + annexpdf);
                    f.delete();
                }
            }
            
            if(ep.getProcessSettings().getStampPaperAnnexure() == 1){
                //remove file once done
                sdpdf.close();
                File f2= new File(baseTemplatePath + sdpdf);
                f2.delete();
            }
            loggerService.logEsignRequest("Agreement document cleaned");
        
            
            
            er.setFile(file);
            esignRequestRepository.save(er);
            return true;
        }catch(Exception ex){
             loggerService.logEsignRequest("Agreement stiching error:" + ex.getMessage());
            return false;
        }
    }
    
    public String prepareAnnexureI(String html, EsignRequest er){
        
        //location details
        html = html.replace("{{state}}", er.getState());
        
        //party two
        html = html.replace("{{name}}", er.getApplicantName());
        html = html.replace("{{email}}", er.getApplicantEmail());
        html = html.replace("{{telephone}}", er.getApplicantTelephone());
        html = html.replace("{{mobile}}", er.getApplicantContact());
        html = html.replace("{{address}}", er.getApplicantAddress());
        
        return html;
    }
    
    public String prepareStampDutyHtml(List<StampPaper> lsp){
       String sphtml = "";
       int total = 0;
       for(int i = 0; i < lsp.size(); i++){
            StampPaper sp = lsp.get(i);
            sphtml += "<tr>\n" +
                        "<td style=\"width: 291px;\">"+sp.getReferenceNo()+"</td>\n" +
                        "<td style=\"width: 289px;\">"+sp.getValue()+"</td>\n" +
                        "</tr>\n" ;
            total += sp.getValue();
        }
       
       String html = "<p style=\"text-align: center;\">&nbsp;</p>\n" +
                        "<p style=\"text-align: center;\">&nbsp;</p>\n" +
                        "<p style=\"text-align: center;\">Stamp Duty Payment Details</p>\n" +
                        "<p style=\"text-align: center;\">&nbsp;</p>\n" +
                        "<table style=\"width: 576px; margin-left: auto; margin-right: auto;\" border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n" +
                        "<tbody>\n" +
                        "<tr>\n" +
                        "<td style=\"width: 291px;\">Stamp Reference Number</td>\n" +
                        "<td style=\"width: 289px;\">Stamp Duty Amount (INR)</td>\n" +
                        "</tr>\n" + sphtml +
                        "<tr>\n" +
                        "<td style=\"width: 291px;\">Total Amount Paid</td>\n" +
                        "<td style=\"width: 289px;\">"+ total +"</td>\n" +
                        "</tr>\n" +
                        "</tbody>\n" +
                        "</table>\n" +
                        "<p>&nbsp;</p>";
       
       
       return html;
    }
    
    public void cancelAgreement(int id, EsignRequest er, int updatedBy){
        if(er == null){
            er = getEsignRequestById(id);
        }
        
        if(er.getStatus() == EsignRequestStatus.COMPLETED){
            throw new ApiException("You can not cancel completed agreement");
        }
        if(er.getStatus() == EsignRequestStatus.CANCELLED){
            throw new ApiException("Already cancelled");
        }
        
        List<Integer> st = new ArrayList<>();
        st.add(EsignRequestStatus.NEW);
        st.add(EsignRequestStatus.INITIATED);
        st.add(EsignRequestStatus.SINGED_BY_CUSTOMER);
        st.add(EsignRequestStatus.SINGED_BY_BUSINESS_SIGNATOR);
        if(!st.contains(er.getStatus())){
            throw new ApiException("You can not cancel this agreement");
        }
        
        er.setStatus(EsignRequestStatus.CANCELLED);
        er.setModifiedBy(updatedBy);
        
        stampPaperService.deallocateStampPaper(er.getId(), er.getIsStampAttached());
        
        er = esignRequestRepository.save(er);
        
    }
    
    public void downloadAgreement(EsignRequest er, int updatedBy){
        
        er.setIsAgreementDownloaded(1);
        er.setModifiedBy(updatedBy);
        er = esignRequestRepository.save(er);
    }
    
    public List<String> getDistinctStates(){
        return esignRequestRepository.findDistinctStates();
    }
    
    
    public boolean isDocumentReferenceNoExist(String referenceNumber1){
        Optional<EsignRequest> erO = esignRequestRepository.findFirstByReferenceNumber1(referenceNumber1);
        
        if(erO.isPresent()){
            return true;
        }
        return false;
    }
    
    public boolean isReferenceIdExist(String referenceNumber2){
        Optional<EsignRequest> erO = esignRequestRepository.findFirstByReferenceNumber2(referenceNumber2);
        
        if(erO.isPresent()){
            return true;
        }
        return false;
    }
    
    
    public boolean isReferenceNoExist(String referenceNumber1, String referenceNumber2, int moduleId, int documentCategoryId){
        Optional<EsignRequest> erO = esignRequestRepository.findFirstByReferenceNumber1AndModuleIdAndDocumentCategoryIdAndStatusNot(referenceNumber1, moduleId, documentCategoryId, EsignRequestStatus.CANCELLED);
        
        if(!erO.isPresent()){
            return false;
        }
//        EsignRequest er = erO.get();
//        if(er.getReferenceNumber1().equalsIgnoreCase(referenceNumber1)){
//            return 1;
//        }
//        if(er.getReferenceNumber1().equalsIgnoreCase(referenceNumber2)){
//            return 2;
//        }
        
        return true;
    }
    
    
    public PDPage addPartydetails(PDPage page, PDDocument doc, EsignRequest er, StampPaper sp) throws IOException{
        int x = 100;
        int y = 170;
        
        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream$AppendMode.APPEND, true, true);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 9);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText("1st Party : Aditya Birla Finance Limited");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 9);
        contentStream.newLineAtOffset(x, y - 11);
        contentStream.showText("2nd Party : " + er.getApplicantName());
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 9);
        contentStream.newLineAtOffset(x, y - 22);
        contentStream.showText("Consideration Amount : INR " + er.getSanctionAmount().toString());
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 9);
        contentStream.newLineAtOffset(x, y - 33);
        contentStream.showText("Stamp Reference No. : " + sp.getReferenceNo());
        contentStream.endText();
        
//        contentStream.beginText();
//        contentStream.setFont(PDType1Font.TIMES_ITALIC, 9);
//        contentStream.newLineAtOffset(x, y - 44);
//        contentStream.showText("Loan Account No. :");
//        contentStream.endText();
        
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 9);
        contentStream.newLineAtOffset(x, y - 44);
        contentStream.showText("Document Reference No. : " + er.getReferenceNumber1());
        contentStream.endText();
        
        contentStream.close();
        
        
        return page;
    }
    
    
    public List<EsignRequest> getEsignRequests(List<Integer> ids){
        return esignRequestRepository.findByIdIn(ids);
    }
    
    public List<EsignRequest> taskCreated(List<EsignRequest> erl, int updatedBy){
        for(int i = 0; i < erl.size(); i++){
            erl.get(i).setStatus(EsignRequestStatus.WAITING_FOR_INITIATE);
            erl.get(i).setModifiedBy(updatedBy);
        }
        erl = esignRequestRepository.saveAll(erl);
        return erl;
    }
    
    /**
     *
     * @param er
     */
    @Async
    public void sendAgreementSMSEmail(EsignRequest er, List<EsignRequestSignee> ersl){
        //email
//        List<EsignRequestSignee> ersl = this.getEsignRequestSigneeById(er.getId());
        
        if(ersl == null){
            ersl = this.getEsignRequestSigneeById(er.getId());
        }
        ersl.forEach(ers -> {
            if(ers.getStatus() != 1){
                communicationService.sendAgreement(ers);
            }
        });
    }
    
    public EsignRequest setStatusToInitiationFailed(EsignRequest er, String msg){
        er.setStatus(EsignRequestStatus.INITIATIATION_FAILED);
        if(msg.equalsIgnoreCase("Stamp Paper not available")){
            er.setStatus(EsignRequestStatus.AWAITING_STAMP_PAPER);
        }
        esignRequestRepository.save(er);
        return er;
    }
    
    public List<EsignRequestSignerLog> getEsignRequestSignerLogs(Integer id){
        return esignRequestSignerLogRepository.findByEsignRequestId(id);
    }
    
    public EsignRequestSignerLog getEsignRequestSignerLog(Integer id){
        Optional<EsignRequestSignerLog> o =  esignRequestSignerLogRepository.findById(id);
        if(!o.isPresent()){
            throw new ApiException("Invalid transaction");
        }
        
        return o.get();
    }
    
    
    public void saveAllSignees(List<EsignRequestSignee> ersl){
        esignRequestSigneeRepository.saveAll(ersl);
    }
    
    public void saveSignLog(EsignRequestSignerLog ersl){
        esignRequestSignerLogRepository.save(ersl);
    }
    
    public void stampAvailable(String state){
    
        List<EsignRequest> erl = esignRequestRepository.findByStateAndStatusAndIsActive(state, EsignRequestStatus.AWAITING_STAMP_PAPER, 1);
        
        erl.forEach(er -> {
            er.setStatus(EsignRequestStatus.WAITING_FOR_INITIATE);
        });
        
        esignRequestRepository.saveAll(erl);
    }
}
