/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.Branch;
import com.econtract.esign.model.DocumentCategory;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.EsignRequestStampPaperLink;
import com.econtract.esign.model.Module;
import com.econtract.esign.model.Source;
import com.econtract.esign.model.StampPaper;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.ApplicationStatus;
import com.econtract.esign.model.constant.ClientType;
import com.econtract.esign.model.constant.SignOption;
import com.econtract.esign.model.constant.StampPaperAllocation;
import com.econtract.esign.model.dto.EsignRequestApplicantFormDto;
import com.econtract.esign.model.dto.EsignRequestFormDto;
import com.econtract.esign.model.dto.EsignRequestUploadDto;
import com.econtract.esign.model.dto.StampPaperDto;
import com.econtract.esign.repository.BranchRepository;
import com.econtract.esign.repository.DocumentCategoryRepository;
import com.econtract.esign.repository.EsignRequestRepository;
import com.econtract.esign.repository.EsignRequestSigneeRepository;
import com.econtract.esign.repository.EsignRequestStampPaperLinkRepository;
import com.econtract.esign.repository.StampPaperRepository;
import com.econtract.esign.repository.ModuleRepository;
import com.econtract.esign.repository.SourceRepository;
import com.econtract.esign.util.PasswordUtil;
import com.econtract.esign.util.WorksheetUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author TS
 */
@Service
public class EsignRequestUploadService {
    
    @Autowired
    DocumentCategoryRepository documentCategoryRepository;
    
    @Autowired
    EsignRequestSigneeRepository esignRequestSigneeRepository;
    
    @Autowired
    EsignRequestRepository esignRequestRepository;
    
    @Autowired
    ModuleRepository moduleRepository;
    
    @Autowired
    SourceRepository sourceRepository;
    
    @Autowired
    BranchRepository branchRepository;
    
    @Autowired
    EsignRequestService esignRequestService;
    
    @Autowired
    StampPaperUploadService stampPaperUploadService;
    
    
    @Autowired
    StampPaperRepository stampPaperRepository;
    
    @Autowired
    EsignRequestStampPaperLinkRepository esignRequestStampPaperLinkRepository;
    
    
    @Autowired
    DocumentCategoryStateService documentCategoryStateService;
    
    List<String> fields = new ArrayList<String>();

    
    DocumentCategory dc = null;
    Module m = null;
    Source src = null;
    Branch br = null;
            
    public EsignRequestUploadService() {
        fields.add("Document Ref No");
        fields.add("Reference ID");
        fields.add("Document Category");
        fields.add("Client Type");
        fields.add("Product");
        fields.add("Agreement Date");
        fields.add("Consideration Amount");
        fields.add("Location");
        fields.add("Branch");
        fields.add("Applicant Status");
        fields.add("Firm Name");
        fields.add("Firm Email");
        fields.add("Firm Address");
        fields.add("Firm Telephone");
        fields.add("Firm Mobile");
        fields.add("Applicant1 Name");
        fields.add("Applicant1 Email");
        fields.add("Applicant1 Mobile");
        fields.add("Applicant1 Telephone");
        fields.add("Applicant1 Address");
        fields.add("Applicant1 Sign Option");
        fields.add("Applicant2 Name");
        fields.add("Applicant2 Email");
        fields.add("Applicant2 Mobile");
        fields.add("Applicant2 Telephone");
        fields.add("Applicant2 Sign Option");
        fields.add("Applicant3 Name");
        fields.add("Applicant3 Email");
        fields.add("Applicant3 Mobile");
        fields.add("Applicant3 Telephone");
        fields.add("Applicant3 Sign Option");
        fields.add("Applicant4 Name");
        fields.add("Applicant4 Email");
        fields.add("Applicant4 Mobile");
        fields.add("Applicant4 Telephone");
        fields.add("Applicant4 Sign Option");
        fields.add("Applicant5 Name");
        fields.add("Applicant5 Email");
        fields.add("Applicant5 Mobile");
        fields.add("Applicant5 Telephone");
        fields.add("Applicant5 Sign Option");
        fields.add("Applicant6 Name");
        fields.add("Applicant6 Email");
        fields.add("Applicant6 Mobile");
        fields.add("Applicant6 Telephone");
        fields.add("Applicant6 Sign Option");
        fields.add("Agreement Sign Option");
        fields.add("Source System");
//        fields.add("Auto Allocate Stamp Paper");
        fields.add("Is Stamp Paper Attached");
    }
    
    
    
    public boolean validateHeader(XSSFRow row){
        
        String field  = "";
        for(int i = 0; i < fields.size(); i++){
            field = row.getCell(i).getStringCellValue();
            if(!field.equalsIgnoreCase(fields.get(i))){
                return false;
            }
        }
        
        return true;
    }
    
    public List<EsignRequestFormDto> convertSheetToEsignRequestRequest(XSSFSheet erWorksheet, XSSFSheet sWorksheet, User createdBy){
        List<EsignRequestFormDto> errl = new ArrayList<>();
        XSSFRow row = erWorksheet.getRow(0);
        
        //stamp paper
        List<StampPaperDto> lspr = new ArrayList<StampPaperDto>();
        for(int j=1; j < sWorksheet.getPhysicalNumberOfRows(); j++) {
            XSSFRow srow = sWorksheet.getRow(j);
            if(WorksheetUtil.getStringCell(srow, 0) == null){
                continue;
            }
            if(WorksheetUtil.getStringCell(srow, 0).isEmpty()){
                continue;
            }
            
            StampPaperDto spr = new StampPaperDto();
            spr.setEsignRequestDocumentRefNo(WorksheetUtil.getStringCell(srow, 0));
            spr.setEsignRequestDocumentCategory(WorksheetUtil.getNumberCell(srow, 1));
            spr.setReferenceNo(WorksheetUtil.getStringCell(srow, 2));
            spr.setValue(WorksheetUtil.getNumberCell(srow, 3));
            spr.setState(WorksheetUtil.getStringCell(srow, 4));
            spr.setProcurementDate(WorksheetUtil.getDateCell(srow, 5));
            spr.setExpiryDate(WorksheetUtil.getDateCell(srow, 6));
            spr.setEntity(createdBy.getStampPaperSource().getName());
            spr.setSourceId(createdBy.getStampPaperSource().getId());
            lspr.add(spr);
        }
        
        for(int i=2;i<erWorksheet.getPhysicalNumberOfRows() ;i++) {
            row = erWorksheet.getRow(i);
            if(row == null){
                continue;
            }
            if(row.getCell(0) == null){
                continue;
            }
            
            EsignRequestFormDto err = new EsignRequestFormDto();
            err.setDocumentRefNo(WorksheetUtil.getStringCell(row, 0));
            err.setReferenceId(WorksheetUtil.getStringCell(row, 1));
            err.setDocumentCategory(WorksheetUtil.getNumberCell(row, 2));
            err.setClientType(WorksheetUtil.getNumberCell(row, 3));
            err.setProduct(WorksheetUtil.getStringCell(row, 4));
            err.setAgreementDate(WorksheetUtil.getDateCell(row, 5));
            err.setSanctionAmount(WorksheetUtil.getNumberCell(row, 6));
            err.setLocation(WorksheetUtil.getStringCell(row, 7));
            err.setBranch(WorksheetUtil.getStringCell(row, 8));
            err.setApplicationStatus(WorksheetUtil.getNumberCell(row, 9));
            err.setFirmName(WorksheetUtil.getStringCell(row, 10));
            err.setFirmEmail(WorksheetUtil.getStringCell(row, 11));
            err.setFirmAddress(WorksheetUtil.getStringCell(row, 12));
            err.setFirmTelephone(WorksheetUtil.getStringCell(row, 13));
            err.setFirmMobile(WorksheetUtil.getRawCell(row, 14));
            err.setApplicantName(WorksheetUtil.getStringCell(row, 15));
            err.setApplicantEmail(WorksheetUtil.getStringCell(row, 16));
            err.setApplicantMobile(WorksheetUtil.getRawCell(row, 17));
            err.setApplicantTelephone(WorksheetUtil.getStringCell(row, 18));
            err.setApplicantAddress(WorksheetUtil.getStringCell(row, 19));
            
            err.setApplicantSignOption(WorksheetUtil.getNumberCell(row, 20));
            
            //applicant 2
            if((WorksheetUtil.getStringCell(row, 21) != null && !WorksheetUtil.getStringCell(row, 21).isEmpty()) || 
               (WorksheetUtil.getStringCell(row, 22) != null && !WorksheetUtil.getStringCell(row, 22).isEmpty()) || 
               (WorksheetUtil.getRawCell(row, 23) != null && !WorksheetUtil.getRawCell(row, 23).isEmpty())){
                EsignRequestApplicantFormDto erar2 = new EsignRequestApplicantFormDto();
                erar2.setName(WorksheetUtil.getStringCell(row, 21));
                erar2.setEmail(WorksheetUtil.getStringCell(row, 22));
                erar2.setMobile(WorksheetUtil.getRawCell(row, 23));
                erar2.setTelephone(WorksheetUtil.getStringCell(row, 24));
                erar2.setAddress("");
                erar2.setSignOption(WorksheetUtil.getNumberCell(row, 25));
                err.addCoApplicant(erar2);
            }
            
            
            //applicant 3
            if((WorksheetUtil.getStringCell(row, 26) != null && !WorksheetUtil.getStringCell(row, 26).isEmpty()) || 
               (WorksheetUtil.getStringCell(row, 27) != null && !WorksheetUtil.getStringCell(row, 27).isEmpty()) || 
               (WorksheetUtil.getRawCell(row, 28) != null && !WorksheetUtil.getRawCell(row, 28).isEmpty())){
                EsignRequestApplicantFormDto erar3 = new EsignRequestApplicantFormDto();
                erar3.setName(WorksheetUtil.getStringCell(row, 26));
                erar3.setEmail(WorksheetUtil.getStringCell(row, 27));
                erar3.setMobile(WorksheetUtil.getRawCell(row, 28));
                erar3.setTelephone(WorksheetUtil.getStringCell(row, 29));
                erar3.setAddress("");
                erar3.setSignOption(WorksheetUtil.getNumberCell(row, 30));
                err.addCoApplicant(erar3);
            }
            
            //applicant 4
            if((WorksheetUtil.getStringCell(row, 31) != null && !WorksheetUtil.getStringCell(row, 31).isEmpty()) || 
               (WorksheetUtil.getStringCell(row, 32) != null && !WorksheetUtil.getStringCell(row, 32).isEmpty()) || 
               (WorksheetUtil.getRawCell(row, 33) != null && !WorksheetUtil.getRawCell(row, 33).isEmpty())){
                EsignRequestApplicantFormDto erar4 = new EsignRequestApplicantFormDto();
                erar4.setName(WorksheetUtil.getStringCell(row, 31));
                erar4.setEmail(WorksheetUtil.getStringCell(row, 32));
                erar4.setMobile(WorksheetUtil.getRawCell(row, 33));
                erar4.setTelephone(WorksheetUtil.getStringCell(row, 34));
                erar4.setAddress("");
                erar4.setSignOption(WorksheetUtil.getNumberCell(row, 35));
                err.addCoApplicant(erar4);
            }
            
            
            //applicant 5
            if((WorksheetUtil.getStringCell(row, 36) != null && !WorksheetUtil.getStringCell(row, 36).isEmpty()) || 
               (WorksheetUtil.getStringCell(row, 37) != null && !WorksheetUtil.getStringCell(row, 37).isEmpty()) || 
               (WorksheetUtil.getRawCell(row, 38) != null && !WorksheetUtil.getRawCell(row, 38).isEmpty())){
                EsignRequestApplicantFormDto erar5 = new EsignRequestApplicantFormDto();
                erar5.setName(WorksheetUtil.getStringCell(row, 36));
                erar5.setEmail(WorksheetUtil.getStringCell(row, 37));
                erar5.setMobile(WorksheetUtil.getRawCell(row, 38));
                erar5.setTelephone(WorksheetUtil.getStringCell(row, 39));
                erar5.setAddress("");
                erar5.setSignOption(WorksheetUtil.getNumberCell(row, 40));
                err.addCoApplicant(erar5);
            }
            
            
            
            //applicant 6
            if((WorksheetUtil.getStringCell(row, 41) != null && !WorksheetUtil.getStringCell(row, 41).isEmpty()) || 
               (WorksheetUtil.getStringCell(row, 42) != null && !WorksheetUtil.getStringCell(row, 42).isEmpty()) || 
               (WorksheetUtil.getRawCell(row, 43) != null && !WorksheetUtil.getRawCell(row, 43).isEmpty())){
                EsignRequestApplicantFormDto erar6 = new EsignRequestApplicantFormDto();
                erar6.setName(WorksheetUtil.getStringCell(row, 41));
                erar6.setEmail(WorksheetUtil.getStringCell(row, 42));
                erar6.setMobile(WorksheetUtil.getRawCell(row, 43));
                erar6.setTelephone(WorksheetUtil.getStringCell(row, 44));
                erar6.setAddress("");
                erar6.setSignOption(WorksheetUtil.getNumberCell(row, 45));
                err.addCoApplicant(erar6);
            }
            
            
            err.setSignOption(WorksheetUtil.getNumberCell(row, 46));
            err.setSource(WorksheetUtil.getStringCell(row, 47)); 
            
            err.setIsAgreementDownloaded(0);
//            err.setAutoAllocateStampPaper(WorksheetUtil.getNumberCell(row, 48));
            
            //is attached
            err.setIsStampPaperAttached(0);
            if(WorksheetUtil.getBooleanCell(row, 48)){
                err.setIsStampPaperAttached(1);
            }
            
            //add stamp paper
            lspr.forEach(spr -> {
                if(spr.getEsignRequestDocumentRefNo().equalsIgnoreCase(err.getDocumentRefNo()) && spr.getEsignRequestDocumentCategory() == err.getDocumentCategory()){
                    err.addStampPaper(spr);
                }
            });
            
            
            errl.add(err);
        }
        
        return errl;
    }
    
    public String validateEsignRequestRequest(EsignRequestFormDto err){
        String ep = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern p = Pattern.compile("(0/91)?[1-9][0-9]{9}");
        Pattern e = Pattern.compile(ep);
        String np = "^[a-zA-Z\\s'\\-]+";
        
       if(err.getDocumentRefNo() == null){
           throw new ApiException("EC002", "Missing Document ref Number");
       }
       if(err.getDocumentRefNo().isEmpty()){
           throw new ApiException("EC002", "Missing Document ref Number");
       }
       if(err.getReferenceId()== null){
           throw new ApiException("EC003", "Missing Reference ID");
       }
       if(err.getReferenceId().isEmpty()){
           throw new ApiException("EC003", "Missing Reference ID");
       }
       if(err.getDocumentCategory() == 0){
           throw new ApiException("EC004", "Missing or Invalid Document Category");
       }
       if(err.getClientType() == 0){
           throw new ApiException("EC005", "Missing or Invalid Client Type");
       }
       if(err.getProduct()== null){
           throw new ApiException("EC006", "Missing or Invalid Product");
       }
       if(err.getProduct().isEmpty()){
           throw new ApiException("EC006", "Missing or Invalid Product");
       }
       if(err.getAgreementDate() == null){
           throw new ApiException("EC007", "Missing or Invalid Agreement Date, it cannot be in the past");
       }
       if(err.getSanctionAmount() == 0){
           throw new ApiException("EC008", "Missing or Invalid Consideration Amount");
       }
       if(err.getLocation() == null){
           throw new ApiException("EC009", "Missing or Invalid Location");
       }
       if(err.getLocation().isEmpty()){
           throw new ApiException("EC009", "Missing or Invalid Location");
       }
       if(err.getBranch() == null){
           throw new ApiException("EC021", "Missing or Invalid Branch");
       }
       if(err.getBranch().isEmpty()){
           throw new ApiException("EC021", "Missing or Invalid Branch");
       }
       if(err.getApplicationStatus() == 0 ){
           throw new ApiException("EC010", "Missing or Invalid Application Status");
       }
       
       if(ApplicationStatus.INDIVIDUAL != err.getApplicationStatus()){
           if(err.getFirmName() == null){
                throw new ApiException("EC011", "For the given application status, Missing or Invalid Firm Name");
           }
           if(err.getFirmEmail()== null){
                throw new ApiException("EC012", "For the given application status, Missing or Invalid Firm Email Address");
           }
           if(err.getFirmMobile()== null){
                throw new ApiException("EC015", "For the given application status, Missing or Invalid Firm Mobile Number, it should be 10 digits");
           }
       }
       
       if(err.getApplicantName() == null ){
           throw new ApiException("EC016", "Missing Applicant Name");
       }
       if(err.getApplicantName().isEmpty()){
           throw new ApiException("EC016", "Missing Applicant Name");
       }
       if(err.getApplicantEmail() == null ){
           throw new ApiException("EC018", "Missing or Invalid Applicant Email Address");
       }
       if(err.getApplicantEmail().isEmpty() ){
           throw new ApiException("EC018", "Missing or Invalid Applicant Email Address");
       }
       if(err.getApplicantMobile() == null ){
           throw new ApiException("EC019", "Missing or Invalid Applicant Mobile Number, it should be 10 digits");
       }
       if(err.getApplicantMobile().isEmpty() ){
           throw new ApiException("EC019", "Missing or Invalid Applicant Mobile Number, it should be 10 digits");
       }
//       if(err.getApplicantTelephone()== null ){
//           return "Application 1 Telephone is required";
//       }
//       if(err.getApplicant1Telephone().isEmpty() ){
//           return "Application 1 Telephone is required";
//       }
       if(err.getApplicantAddress()== null ){
           throw new ApiException("EC017", "Missing Applicant Address");
       }
       if(err.getApplicantAddress().isEmpty() ){
           throw new ApiException("EC017", "Missing Applicant Address");
       }
       
       //other validation
       if(!ClientType.isValid(err.getClientType())){
           throw new ApiException("EC005", "Missing or Invalid Client Type");
       }
       if(!ApplicationStatus.isValid(err.getApplicationStatus())){
           throw new ApiException("EC010", "Missing or Invalid Application Status");
       }
       if(!SignOption.isValid(err.getSignOption())){
           throw new ApiException("EC036", "Invalid sign option");
       }
       
       Matcher m = null;
       if(ApplicationStatus.INDIVIDUAL != err.getApplicationStatus()){
            m = p.matcher(err.getFirmMobile());
            if(!(m.find() && m.group().equals(err.getFirmMobile()))){
                throw new ApiException("EC015", "For the given application status, Missing or Invalid Firm Mobile Number, it should be 10 digits");
            }
            m = e.matcher(err.getFirmEmail());
            if(!(m.find() && m.group().equals(err.getFirmEmail()))){
                throw new ApiException("EC018", "Missing or Invalid Applicant Email Address");
            }
       }
       
       //applicant 1 validation
       if(!err.getApplicantName().matches(np)){
           throw new ApiException("EC016", "Missing Applicant Name");
       }
       m = p.matcher(err.getApplicantMobile());
       if(!(m.find() && m.group().equals(err.getApplicantMobile()))){
           throw new ApiException("EC019", "Missing or Invalid Applicant Mobile Number, it should be 10 digits");
       }
       m = e.matcher(err.getApplicantEmail());
       if(!(m.find() && m.group().equals(err.getApplicantEmail()))){
           throw new ApiException("EC018", "Missing or Invalid Applicant Email Address");
       }
       
       if(!SignOption.isValid(err.getApplicantSignOption())){
           throw new ApiException("EC036", "Invalid applicant sign option");
       }
       
       
       //applicant 2-6 validation
       for(int i = 0; i < err.getCoApplicants().size();i++){
           EsignRequestApplicantFormDto erar = err.getCoApplicants().get(i);
           
            
            if(erar.getName() == null){
                throw new ApiException("EC020", "Missing or Invalid Co-Applicant "+ (i+1) +" Data - Missing Name");
            }
            if(erar.getEmail()== null){
                throw new ApiException("EC020", "Missing or Invalid Co-Applicant "+ (i+1) +" Data - Missing or Invalid Email Address");
            }
            if(erar.getMobile()== null){
                throw new ApiException("EC020", "Missing or Invalid Co-Applicant "+ (i+1) +" Data - Missing or Invalid Mobile Number, it should be 10 digits");
            }


            if(!erar.getName().matches(np)){
                throw new ApiException("EC020", "Missing or Invalid Co-Applicant "+ (i+1) +" Data - Missing Name");
            }
            m = p.matcher(erar.getMobile());
            if(!(m.find() && m.group().equals(erar.getMobile()))){
                throw new ApiException("EC020", "Missing or Invalid Co-Applicant "+ (i+1) +" Data - Missing or Invalid Mobile Number, it should be 10 digits");
            }
            m = e.matcher(erar.getEmail());
            if(!(m.find() && m.group().equals(erar.getEmail()))){
                throw new ApiException("EC020", "Missing or Invalid Co-Applicant "+ (i+1) +" Data - Missing or Invalid Email Address");
            }
       
            if(!SignOption.isValid(err.getApplicantSignOption())){
                throw new ApiException("EC020", "Missing or Invalid Co-Applicant "+ (i+1) +" Data - Invalid Sign option");
            }
       }
       
       if(err.getAutoAllocateStampPaper() == StampPaperAllocation.YES && err.getIsStampPaperAttached() == 1){
           throw new ApiException("EC100", "Please check keys [autoAllocateStampPaper, isStampPaperAttached]");
       }
       
       if(err.getIsStampPaperAttached() == 1 && err.getStampPapers().size() == 0){
           throw new ApiException("EC100", "Stamp Paper details required if already attached");
       }
       
       if(err.getIsStampPaperAttached() != 1 && err.getStampPapers().size() > 0){
           throw new ApiException("EC100", "Stamp Paper details not required");
       }
       
       
       for(int i = 0; i < err.getStampPapers().size();i++){
           try{
                stampPaperUploadService.validateStampPaper(err.getStampPapers().get(i));
           }
           catch(Exception ex){
                return err.getStampPapers().get(i).getReferenceNo() + ": " + ex.getMessage();
           }
       }
       
       
       return ""; 
    }
    
     public EsignRequestFormDto updateRequiredIds(EsignRequestFormDto err){
        if(err.getDocumentCategoryId() == 0){
            int dcName = err.getDocumentCategory();
            if(dc == null || (dc != null && dcName != dc.getCode())){
                Optional<DocumentCategory> dcO = documentCategoryRepository.findFirstByCode(dcName);
                if(!dcO.isPresent()){
                    throw new ApiException("EC004", "Document Category is invalid");
                }
                dc = dcO.get();
            }
            err.setDocumentCategoryId(dc.getId());
        }
        
        if(err.getProductId() == 0){
            String mName = err.getProduct();
            if(m == null || (m != null && !mName.equals(m.getName()))){
                Optional<Module> mO = moduleRepository.findFirstByName(mName);
                if(!mO.isPresent()){
                    throw new ApiException("EC006", "Product is invalid");
                }
                m = mO.get();
            }
            err.setProductId(m.getId());
            err.setEntityId(m.getEntityId());
            err.setEntity(m.getEntity().getName());
        }
        
        if(err.getBranchId() == 0){
            String bName = err.getBranch();
            if(br == null || (br != null && !bName.equals(br.getName()))){
                Optional<Branch> bO = branchRepository.findFirstByName(bName);
                if(!bO.isPresent()){
                    throw new ApiException("EC021", "Branch is invalid");
                }
                br = bO.get();
            }
            err.setBranchId(br.getId());
        }
        
        if(err.getSourceId() == 0){
            String srcName = null;
            if(err.getSource() != null){
                srcName = err.getSource();
                if(srcName.equalsIgnoreCase("")){
                    srcName = null;
                }
            }
            if((src == null && srcName != null) || (src != null && srcName != null && !srcName.equals(src.getName()))){
                Optional<Source> srcO = sourceRepository.findFirstByName(srcName);
                if(!srcO.isPresent()){
                    throw new ApiException("EC100", "Source is invalid");
                }
                src = srcO.get();
            }
            if(src == null){
                List<Source> srcl = sourceRepository.findAll();
                for (Source s : srcl) {
                    if(s.getName().equalsIgnoreCase("Internal Upload")){
                        src = s;
                    }
                }
                if(src == null){
                    throw new ApiException("EC100", "'Internal Upload' source is not configured");
                }
            }
            err.setSourceId(src.getId());
        }
    
        return err;
    }
    
    public boolean isUniqueInCurrentUpload(EsignRequestFormDto err, List<String> uniqueId){
        String key = err.getDocumentRefNo() + err.getProduct() + err.getDocumentCategory();
        if(uniqueId.contains(key)){
            return false;
        }
        
        uniqueId.add(key);
        return true;
    }
    
    
    public EsignRequest createEsignRequest(EsignRequestFormDto err ,int createdBy){
        EsignRequest er = new EsignRequest();
        er.setReferenceNumber1(err.getDocumentRefNo());
        er.setReferenceNumber2(err.getReferenceId());
        er.setModuleId(err.getProductId());
        er.setDocumentCategoryId(err.getDocumentCategoryId());
        er.setBranchId(err.getBranchId());
        er.setDate(err.getAgreementDate());
        er.setSanctionAmount(err.getSanctionAmount());
        er.setLocation(err.getLocation());
        er.setState(err.getLocation());
        er.setApplicantName(err.getApplicantName());
        er.setApplicantContact(err.getApplicantMobile());
        er.setApplicantEmail(err.getApplicantEmail());
        er.setApplicantTelephone(err.getApplicantTelephone());
        er.setApplicantAddress(err.getApplicantAddress());
        er.setApplicantSignOption(err.getApplicantSignOption());
        er.setApplicantionStatus(err.getApplicationStatus());
        er.setSignOption(err.getSignOption());
        er.setIsSignatorySignRequired(1);//signatory option should be set basis on process
        er.setStatus(err.getStatus());
        er.setToken(PasswordUtil.generateOpaque(er.getReferenceNumber1()));
        er.setClientType(err.getClientType());
//        er.setFile(er.getToken()+".pdf");
        er.setIsActive(1);
        er.setModifiedBy(createdBy);
        
        er.setIsAgreementDownloaded(err.getIsAgreementDownloaded());
        er.setIsStampAttached(err.getIsStampPaperAttached());
        er.setAutoAllocateStampPaper(err.getAutoAllocateStampPaper());
        er.setSourceId(err.getSourceId());
        
        
        
        if(ApplicationStatus.INDIVIDUAL != err.getApplicationStatus()){
            er.setApplicantName(err.getFirmName());
            er.setApplicantEmail(err.getFirmEmail());
            er.setApplicantAddress(err.getFirmAddress());
            er.setApplicantTelephone(err.getFirmTelephone());
            er.setApplicantContact(err.getFirmMobile());
        }
        
        return er;
    }
    
    public List<EsignRequestSignee> createSigners(EsignRequest er, EsignRequestFormDto err){
        List<EsignRequestSignee> ersl = new ArrayList<>();
        
        //first applicant
        EsignRequestSignee ers1 = new EsignRequestSignee();
        ers1.setEsignRequestId(er.getId());
        ers1.setApplicantName(err.getApplicantName());
        ers1.setApplicantContact(err.getApplicantMobile());
        ers1.setApplicantTelephone(err.getApplicantTelephone());
        ers1.setApplicantEmail(err.getApplicantEmail());
        ers1.setApplicantAddress(err.getApplicantAddress());
        ers1.setSignOption(err.getApplicantSignOption());
        ers1.setSequence(1);
        ers1.setStatus(0);
        ers1.setModifiedBy(er.getModifiedBy());
        ersl.add(ers1);
        
        
        //2-6 applicant
        for(int i = 0; i < err.getCoApplicants().size(); i++){
            EsignRequestApplicantFormDto erar = err.getCoApplicants().get(i);
            EsignRequestSignee ers2 = new EsignRequestSignee();
            ers2.setEsignRequestId(er.getId());
            ers2.setApplicantName(erar.getName());
            ers2.setApplicantContact(erar.getMobile());
            ers2.setApplicantTelephone(erar.getTelephone());
            ers2.setApplicantEmail(erar.getEmail());
            ers2.setApplicantAddress(erar.getAddress());
            ers2.setSignOption(erar.getSignOption());
            ers2.setSequence(i + 2);
            ers2.setStatus(0);
            ers2.setModifiedBy(er.getModifiedBy());
            ersl.add(ers2);
        }
       
        return ersl;
    }
    
    
    
    public List<StampPaper> createStampPapers(List<StampPaperDto> sprl, int entityId, int sourceId, int createdBy){
        List<StampPaper> spl = new ArrayList<>();
        
        for(int i = 0; i < sprl.size(); i++){
            StampPaperDto spr = sprl.get(i);
            
            StampPaper sp = new StampPaper();
            sp.setReferenceNo(spr.getReferenceNo());
            sp.setEntityId(entityId);
            sp.setSourceId(spr.getSourceId());
            sp.setValue(spr.getValue());
            sp.setState(spr.getState());
            sp.setProcurementDate(spr.getProcurementDate());
            sp.setExpiryDate(spr.getExpiryDate());
            sp.setStatus(1);
            sp.setIsActive(1);
            sp.setModifiedBy(createdBy);
            
            
            if(spr.getFile() != null){
                String file = stampPaperUploadService.storeFile(sp, spr.getFile());
                sp.setFile1(file);
            }
            
            spl.add(sp);
        }
        
        return spl;
    }
    
    public List<EsignRequestStampPaperLink> createEsignRequestStampPapers(List<StampPaperDto> sprl, int createdBy){
        List<EsignRequestStampPaperLink> spl = new ArrayList<>();
        
        for(int i = 0; i < sprl.size(); i++){
            StampPaperDto spr = sprl.get(i);
            EsignRequestStampPaperLink sp = new EsignRequestStampPaperLink();
            sp.setStampPaperReferenceNo(spr.getReferenceNo());
            sp.setStampPaperValue(spr.getValue());
            sp.setStatus(1);
            sp.setModifiedBy(createdBy);
            spl.add(sp);
        }
        
        return spl;
    }
    
    public EsignRequestUploadDto processEsignRequestRequest(EsignRequestFormDto err, int createdBy, List<String> uniqueId){
        try{
            validateEsignRequestRequest(err);
        } catch(ApiException ex){
            throw new ApiException(ex.getErrorCode(), ex.getMessage());
        }


        if(!isUniqueInCurrentUpload(err, uniqueId)){
            throw new ApiException("EC001", "Combination of [doc ref + doc cat + product] already available in list");
        }

        err = updateRequiredIds(err);
        
        if(esignRequestService.isReferenceNoExist(err.getDocumentRefNo(), err.getReferenceId(), err.getProductId(), err.getDocumentCategoryId())){
            throw new ApiException("EC001", "Agreement with the given Document Reference Number, Product and Category exists in the system.");
        }
        

        EsignRequestUploadDto eru = new EsignRequestUploadDto();
        eru.setEsignRequest(createEsignRequest(err ,createdBy));
        
        if(err.getStampPapers().size() > 0){
            EsignRequest er = eru.getEsignRequest();
            int amount = documentCategoryStateService.getStampPaperAmount(er.getState(), er.getModuleId(), er.getDocumentCategoryId(), er.getSanctionAmount().intValue());
           
            int stampAmount = 0;
            for(int i = 0; i < err.getStampPapers().size(); i++){
                stampAmount += err.getStampPapers().get(i).getValue();
            }
            
            if(stampAmount != amount){
                throw new ApiException("EC028", "Stamp value for " + er.getReferenceNumber1() + " is not valid. it should be " + amount);
            }
        }
        
        eru.setEsignRequestSignees(createSigners(eru.getEsignRequest() ,err));
        eru.setStampPapers(createStampPapers(err.getStampPapers(), err.getEntityId(), err.getEntityId(), createdBy));
        eru.setStampPaperLinks(createEsignRequestStampPapers(err.getStampPapers(), createdBy));
        return eru;
    }
    
    public void saveAllEsignRequest(List<EsignRequestUploadDto> erul){
        for(int k = 0; k < erul.size(); k++){
            EsignRequest e = erul.get(k).getEsignRequest();
            e = esignRequestRepository.save(e);
            erul.get(k).setEsignRequest(e);


            List<EsignRequestSignee> erusl = erul.get(k).getEsignRequestSignees();
            for(int l = 0; l < erusl.size(); l++){
                EsignRequestSignee ers = erusl.get(l);
                ers.setEsignRequestId(e.getId());
                ers.setToken(PasswordUtil.generateOpaque(e.getId().toString()));
                ers = esignRequestSigneeRepository.save(ers);
                erusl.set(l, ers);
            }
            erul.get(k).setEsignRequestSignees(erusl);
            
            
            List<StampPaper> spl = erul.get(k).getStampPapers();
            List<EsignRequestStampPaperLink> erspll = erul.get(k).getStampPaperLinks();
            for(int l = 0; l < spl.size(); l++){
                StampPaper sp = spl.get(l);
                EsignRequestStampPaperLink erspl = erspll.get(l);
                sp = stampPaperRepository.save(sp);
                
                erspl.setEsignRequestId(e.getId());
                erspl.setStampPaperId(sp.getId());
                erspl = esignRequestStampPaperLinkRepository.save(erspl);
                
                
                spl.set(l, sp);
                erspll.set(l, erspl);
            }
            erul.get(k).setStampPapers(spl);
            erul.get(k).setStampPaperLinks(erspll);
            
        }
        
    }
    
    public void save(MultipartFile file, User createdBy){
        int lastRow = 0;
        try{
            List<String> uniqueId = new ArrayList<>();
            
            List<EsignRequestUploadDto> erul = new ArrayList<EsignRequestUploadDto>();
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet erWorksheet = workbook.getSheetAt(0);
            XSSFSheet sWorksheet = workbook.getSheetAt(1);
            
            XSSFRow row = erWorksheet.getRow(0);
            if(!validateHeader(row)){
                throw new ApiException("Wrong template uploaded");
            }
            
            if(createdBy.getStampPaperSource() == null){
                throw new ApiException("Stamp source not mapped with user");
            }
            
            //convert to request obj
            List<EsignRequestFormDto> errl = convertSheetToEsignRequestRequest(erWorksheet, sWorksheet, createdBy);
            
            for(int i=0;i<errl.size();i++) {
                lastRow = i + 1;
                EsignRequestUploadDto eru = processEsignRequestRequest(errl.get(i), createdBy.getId(), uniqueId);
                erul.add(eru);
            }
            
            saveAllEsignRequest(erul);
            
        }catch(ApiException ex){
            throw new ApiException(ex.getErrorCode(), "Error on line "+ lastRow + ": " + ex.getMessage());
        }catch(Exception ex){
            throw new ApiException("Error on line "+ lastRow + ": " + ex.getMessage());
        }
    }
    
    public List<EsignRequestUploadDto> save(EsignRequestFormDto err, int createdBy){
        List<String> uniqueId = new ArrayList<>();
        List<EsignRequestUploadDto> erul = new ArrayList<>();
        try{
            
            EsignRequestUploadDto eru = processEsignRequestRequest(err, createdBy, uniqueId);
            erul.add(eru);

            saveAllEsignRequest(erul);
        }catch(ApiException ex){
            throw new ApiException(ex.getErrorCode(), ex.getMessage());
        }
        
        return erul;
    }
    
}
