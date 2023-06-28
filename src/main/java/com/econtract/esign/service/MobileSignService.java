/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.repository.EsignRequestRepository;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream$AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class MobileSignService {
    
    @Value("${nsdl.base.path}")
    String basePath;
    
    @Autowired
    EsignRequestRepository esignRequestRepository;
    
    @Autowired
    LoggerService loggerService;
    
    public boolean getSignOnDocument(EsignRequestSignee ers, EsignRequest er, String createdByIp) {
        loggerService.logApi("mobile sign user: " + ers.toString());

        try { 
            int seq = ers.getSequence();
            int sign = seq;
            int line = 1;
            while(sign > 3){
                sign -= 3;
                line++;
            }
            
            if(line == 1){
                line = 2;
            }else if(line == 2){
                line = 1;
            }
            
            int x = 20; 
            int y = 30 + ((line - 1) * 50);
            int z = 142;
            String personName = ers.getApplicantName();
            String fileName = er.getFile();
            
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy");
            ZonedDateTime now = ZonedDateTime.now();
            String CurrentDate = dtf.format(now);

            String sPDFPath = basePath + fileName;
            //Loading an existing document 
            PDDocument doc = PDDocument.load(new File(sPDFPath));
            int noOfPages = doc.getNumberOfPages();
            for (int i = 0; i < noOfPages; i++) {
                //Creating a PDF Document 
                PDPage page = doc.getPage(i);

                PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream$AppendMode.APPEND, true, true);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ITALIC, 7);
                contentStream.newLineAtOffset(x, y);
                contentStream.newLineAtOffset(z * (sign - 1), 0);
                contentStream.showText("e-Sign by xTendContract:");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ITALIC, 7);
                contentStream.newLineAtOffset(x, y - 8);
                contentStream.newLineAtOffset(z * (sign - 1), 0);
                contentStream.showText("Name:"+personName);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ITALIC, 7);
                contentStream.newLineAtOffset(x, y - 16);
                contentStream.newLineAtOffset(z * (sign - 1), 0);
                contentStream.showText("Date:" + CurrentDate);
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ITALIC, 7);
                contentStream.newLineAtOffset(x, y - 24);
                contentStream.newLineAtOffset(z * (sign - 1), 0);
                contentStream.showText("Through Device ip:" + createdByIp);
                contentStream.endText();

//                //Print Adity Birla Person name
//                contentStream.beginText();
//                contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
//                contentStream.newLineAtOffset(x, y - 75);
//                for (int j = 0; j < 2; j++) {
//                    if (j > 0) {
//                        contentStream.newLineAtOffset(z, 0);
//                    }
//                    contentStream.showText("Aditya birla " + (j + 1));
//                }
//                contentStream.endText();
//
//                //Begin the Content stream 
//                contentStream.beginText();
//                contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
//                contentStream.newLineAtOffset(x, y - 90);
//                for (int j = 0; j < 2; j++) {
//                    if (j > 0) {
//                        contentStream.newLineAtOffset(z, 0);
//                    }
//                    contentStream.showText(CurrentDate);
//                }
//                contentStream.endText();
//
//                //Begin the Content stream 
//                contentStream.beginText();
//                contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
//                contentStream.newLineAtOffset(x, y - 105);
//                for (int j = 0; j < 2; j++) {
//                    if (j > 0) {
//                        contentStream.newLineAtOffset(z, 0);
//                    }
//                    contentStream.showText("Digitally signed");
//                }
//                contentStream.endText();
                //Closing the content stream 
                contentStream.close();

            }
            //Saving the document  
            fileName = fileName.replace(".pdf", "_mobile.pdf");
            doc.save(new File(basePath + fileName)); //fileName

            //Closing the document  
            doc.close();
            
            er.setFile(fileName);
            
            er = esignRequestRepository.save(er);
        } catch (Exception ex) {
            loggerService.logApi("mobile sign error: " + ex.getMessage());
            return false;
        }

        return true;
    }
    
}
