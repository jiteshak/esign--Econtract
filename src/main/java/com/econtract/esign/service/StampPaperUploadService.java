/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EEntity;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestStampPaperLink;
import com.econtract.esign.model.Source;
import com.econtract.esign.model.StampPaper;
import com.econtract.esign.model.constant.StateList;
import com.econtract.esign.model.dto.StampPaperDto;
import com.econtract.esign.repository.EsignRequestStampPaperLinkRepository;
import com.econtract.esign.repository.StampPaperRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author TS
 */
@Service
public class StampPaperUploadService {
    
    
    @Autowired
    StampPaperRepository stampPaperRepository;
    
    @Autowired
    EsignRequestStampPaperLinkRepository esignRequestStampPaperLinkRepository;
    
    @Autowired
    FileStorageService fileStorageService;
    
    @Autowired
    EntityService entityService;
    
    @Autowired
    SourceService sourceService;
    
    @Autowired
    LoggerService loggerService;
    
    @Value("${base.stamp.paper.path}")
    String basePath;
    
    List<String> fields = new ArrayList<String>();

    public StampPaperUploadService() {
        fields.add("Reference Number");
        fields.add("Value");
        fields.add("State");
        fields.add("Procurement Date");
        fields.add("Expiry Date");
        fields.add("File Name");
//        fields.add("Entity");
    }
    
    public boolean validateFolderFileCount(int rowCount){
        //TODO: get folder count
        return true;
    }
    
    public boolean validateFileExist(MultipartFile file, int sourceId){
        String path = fileStorageService.joinPath(basePath + sourceId, file.getOriginalFilename());
        
        if(fileStorageService.isExist(path)){
            return false;
        }
        return true;
    }
    
    public String validateFile(MultipartFile file, int sourceId){
        
        try{
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            
            if(!validateFileExist(file, sourceId)){
                throw new ApiException("File with this name already exists");
            }
            
            XSSFRow row = worksheet.getRow(0);
            if(!validateHeader(row)){
                throw new ApiException("Wrong stamp paper template uploaded");
            }
            
            if(!validateFolderFileCount(worksheet.getPhysicalNumberOfRows())){
                throw new ApiException("Stamp papers in file is more than the files in stamp folder");
            }
            
        }catch(Exception ex){
            return  ex.getMessage();
        }
        
        
        return "";
    }
    
    public String moveFileFromSourceToStampDir(Source s, String state,String fileName, String newFileName){
        //Size of the PDF file should not be more than 5 MB.
        String sourcePath = s.getPath() + File.separator + "in" + File.separator + state;
        fileStorageService.move(sourcePath,basePath,fileName, newFileName);
        
        return fileName;
    }
    
    public String moveFileFromSourceToErrorDir(Source s, String state,String fileName){
        //Size of the PDF file should not be more than 5 MB.
        String sourcePath = s.getPath() + File.separator + "in" + File.separator + state;
        String errorPath = s.getPath() + File.separator + "error" + File.separator + state;
        fileStorageService.move(sourcePath,errorPath,fileName);
        
        return fileName;
    }
    
    public String validateStampPaper(StampPaper sp){
        if(sp.getReferenceNo() == null){
            throw new ApiException("invalid reference number");
        }
        
        if(sp.getReferenceNo().trim().length() == 0){
            throw new ApiException("invalid reference number");
        }
        
        if(!StateList.isValid(sp.getState())){
            throw new ApiException("State codes should be valid as per xtendContract app and not empty");
        }
        
        if(sp.getFile1() == null || (sp.getFile1() != null &&!(sp.getFile1().endsWith(".pdf") || sp.getFile1().endsWith(".jpg")))){
            throw new ApiException("Invalid file");
        }
        
        if((sp.getFile2() != null &&!(sp.getFile2().endsWith(".pdf") || sp.getFile2().endsWith(".jpg")))){
            throw new ApiException("Invalid file 2");
        }
        
        if(!(sp.getValue() > 0)){
            throw new ApiException("The value should be not null or empty and > 0");
        }
        
        if(sp.getProcurementDate() == null){
            throw new ApiException("Procurement Date should not be empty");
        }
        
        if(sp.getExpiryDate()== null){
            throw new ApiException("Expiry Date should not be empty");
        }
        
        Date cd = new Date();
        if(sp.getExpiryDate().before(cd) || sp.getExpiryDate().before(sp.getProcurementDate())){
            throw new ApiException("Expiry Date should be > than today and > than procurement date.");
        }
        
        if(stampPaperRepository.existsByReferenceNoAndStateAndIsActive(sp.getReferenceNo(), sp.getState(), 1)){
            return "duplicate stamp reference number";
        }
        
        return "";
    }
    
    public String validateStampPaper(StampPaperDto sp){
        if(sp.getReferenceNo() == null){
            throw new ApiException("invalid reference number");
        }
        
        if(sp.getReferenceNo().trim().length() == 0){
            throw new ApiException("invalid reference number");
        }
        
        if(!StateList.isValid(sp.getState())){
            throw new ApiException("State codes should be valid as per xtendContract app and not empty");
        }
        
        if(!(sp.getValue() > 0)){
            throw new ApiException("The value should be not null or empty and > 0");
        }
        
        if(sp.getProcurementDate() == null){
            throw new ApiException("Procurement Date should not be empty");
        }
        
        if(sp.getExpiryDate()== null){
            throw new ApiException("Expiry Date should not be empty");
        }
        
        Date cd = new Date();
        if(sp.getExpiryDate().before(cd) || sp.getExpiryDate().before(sp.getProcurementDate())){
            throw new ApiException("Expiry Date should be > than today and > than procurement date.");
        }
        
        if(stampPaperRepository.existsByReferenceNoAndStateAndIsActive(sp.getReferenceNo(), sp.getState(), 1)){
            return "duplicate stamp reference number";
        }
        
        return "";
    }
    
    public String store(MultipartFile file, int sourceId){
        return fileStorageService.store(file, basePath + sourceId, file.getOriginalFilename());
    }
    
    public String upload(int userId, int sourceId, String file){
        int totalProcess = 0;
        int completeProcess = 0;
        String msg = "";
        String path = fileStorageService.joinPath(basePath + sourceId, file);
        Source s = sourceService.getById(sourceId);
        List<StampPaper> spl = new ArrayList<StampPaper>();
        List<String> states = Arrays.asList();
        
        try{
            FileInputStream fi = fileStorageService.getInputStream(path);
            EEntity e = null;
            
            XSSFWorkbook workbook = new XSSFWorkbook(fi);
            XSSFSheet worksheet = workbook.getSheetAt(0);
            
            XSSFRow row = worksheet.getRow(0);
            if(!validateHeader(row)){
                return "Wrong stamp paper template uploaded";
            }
            
            loggerService.logStampPaper("Started reading stamp paper excel rows");
            for(int i=1;i<worksheet.getPhysicalNumberOfRows();i++) {
                StampPaper sp = null;
                row = worksheet.getRow(i);
                if(row == null){
                    continue;
                }
                if(row.getCell(0) == null){
                    continue;
                }
                
                try{
                    totalProcess++;
                    
                    if(row.getCell(7) == null){
                        row.createCell(7);
                    }
                    
                    
                    
                    sp = new StampPaper();
                    Date pd = row.getCell(3).getDateCellValue();
                    Date ed = row.getCell(4).getDateCellValue();

                    sp.setReferenceNo(row.getCell(0).getStringCellValue());
                    sp.setValue((int) row.getCell(1).getNumericCellValue());
                    sp.setState(row.getCell(2).getStringCellValue());
                    sp.setReferenceNo(row.getCell(0).getStringCellValue());
                    sp.setFile1(row.getCell(5).getStringCellValue());
                    sp.setProcurementDate(pd);
                    sp.setExpiryDate(ed);
                    sp.setIsActive(1);
                    sp.setSourceId(sourceId);
                    sp.setStatus(0);
                    sp.setModifiedBy(userId);
                    sp.setModifiedDate(LocalDateTime.now());
                    
                    
                    //read row
                    String eName = row.getCell(6).getStringCellValue();
                    if(e == null || (e != null && (eName != e.getName()))){
                        e = entityService.getByName(eName);
                    }
                    
                    sp.setEntityId(e.getId());
                    //validate
                    String err = validateStampPaper(sp);
                    if(!err.isEmpty()){
                        throw new ApiException(err);
                    }
                    
                    String[] tf = sp.getFile1().split("\\.");
                    String newFileName =  sp.getState() + sp.getReferenceNo() + "_front." + tf[tf.length - 1];
                    moveFileFromSourceToStampDir(s, sp.getState(), sp.getFile1(), newFileName);
                    sp.setFile1(newFileName);
                    if(sp.getFile2() != null){
                        tf = sp.getFile1().split("\\.");
                        newFileName =  sp.getState() + sp.getReferenceNo() + "_back." + tf[tf.length - 1];
                        moveFileFromSourceToStampDir(s, sp.getState(), sp.getFile2(), newFileName);
                        sp.setFile2(newFileName);
                    }
                    
                    //save
                    sp = stampPaperRepository.save(sp);
                    
                    row.getCell(7).setCellValue("Done");
                    spl.add(sp);
                    
                    if(!states.contains(sp.getState())){
                        states.add(sp.getState());
                    }
                    
                    completeProcess++;
                }catch(Exception ex){
                    row.getCell(7).setCellValue(ex.getMessage());
                    
                    try{
                        if(sp != null){
                            if(sp.getFile1() != null && !sp.getFile1().isEmpty()){
                                moveFileFromSourceToErrorDir(s, sp.getState(), sp.getFile1());
                            }

                            if(sp.getFile2() != null && !sp.getFile2().isEmpty()){
                                moveFileFromSourceToErrorDir(s, sp.getState(), sp.getFile2());
                            }
                        }
                    }catch(Exception x){}
                }
                
            }
            
            FileOutputStream fo = fileStorageService.getOuputStream(path);
            workbook.write(fo);
            fo.close();
            fi.close();
            msg = completeProcess + " out of "+ totalProcess +" stamp papers are uploaded |||" + String.join(",", states);
        }catch(Exception ex){
            throw  new ApiException(msg);
        }
        
        
        return msg;
    }
    
    public String upload(int userId, int sourceId, StampPaperDto spr){
        try{
            StampPaper sp = new StampPaper();

            sp.setReferenceNo(spr.getReferenceNo());
            sp.setValue(spr.getValue());
            sp.setState(spr.getState());
            sp.setFile1(spr.getFile1());
            sp.setProcurementDate(spr.getProcurementDate());
            sp.setExpiryDate(spr.getExpiryDate());
            sp.setIsActive(1);
            sp.setSourceId(sourceId);
            sp.setStatus(0);
            sp.setModifiedBy(userId);
            sp.setModifiedDate(LocalDateTime.now());


            //read row
            sp.setEntityId(spr.getEntityId());
            if(spr.getEntityId() == 0){
                EEntity e = entityService.getByName(spr.getEntity());
                sp.setEntityId(e.getId());
            }
            
            
            //validate
            String err = validateStampPaper(sp);
            if(!err.isEmpty()){
                throw new ApiException(err);
            }

            //save
            sp = stampPaperRepository.save(sp);

        }catch(Exception ex){
            return "Error : " + ex.getMessage();
        }
        
        return "";
    }
    
    public String storeFile(StampPaperDto sp, MultipartFile stamp){
        String[] tf = stamp.getOriginalFilename().split("\\.");
        String newFileName =  sp.getState() + sp.getReferenceNo() + "_front." + tf[tf.length - 1];
        
        fileStorageService.store(stamp, basePath, newFileName);
        return newFileName;
    }
    
    public String storeFile(StampPaper sp, MultipartFile stamp){
        String[] tf = stamp.getOriginalFilename().split("\\.");
        String newFileName =  sp.getState() + sp.getReferenceNo() + "_front." + tf[tf.length - 1];
        
        fileStorageService.store(stamp, basePath, newFileName);
        return newFileName;
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
    
    public int validateRow(XSSFRow row){
        int i = 0;
        try{
            String field;
            
            //field should be string if not number or date
            List<Integer> strings = new ArrayList<Integer>();
            
            List<Integer> numbers = new ArrayList<Integer>();
            numbers.add(1);
            List<Integer> dates = new ArrayList<Integer>();
            dates.add(3);
            dates.add(4);
             
            //makesure field is not emply
            for(i = 0; i < 5; i++){
                if(numbers.contains(i)){
                    if(!(row.getCell(i).getNumericCellValue() > 0)){
                        return i;
                    }
                }else if(dates.contains(i)){
                    if(!row.getCell(i).getDateCellValue().after(new Date())){
//                        return i; not required to return it is just to validate date
                    }
                }else{
                    field = row.getCell(i).getStringCellValue();
                    if(field.isEmpty()){
                        return i;
                    }
                }
            }
        
        }catch(Exception ex){
            return i;
        }
        
        return -1;
    }
}
