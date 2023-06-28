/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.repository.EsignRequestRepository;
import com.econtract.esign.repository.EsignRequestSigneeRepository;
import com.econtract.esign.util.PasswordUtil;
import com.nsdl.esign.preverifiedNo.controller.EsignApplication;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class NsdlService {

    @Value("${nsdl.base.path}")
    String basePath;

    @Value("${nsdl.aspId}")
    String aspId;

    @Value("${nsdl.authMode}")
    String authMode;

    @Value("${nsdl.response.url}")
    String responseUrl;
    
    @Value("${nsdl.certificate.p12}")
    String certificate;
    
    @Value("${nsdl.certificate.p12.password}")
    String certificatePassword;
    
    @Value("${nsdl.tick.img}")
    String tickImage;
    
    @Value("${nsdl.alias}")
    String alias;
    
    @Autowired
    EsignRequestRepository esignRequestRepository;
    
    @Autowired
    EsignRequestSigneeRepository esignRequestSigneeRepository;
    
    @Autowired
    LoggerService loggerService;
    
    


    private static String getFileChecksum(String path) throws IOException, NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");

        File file = new File(path);
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            md.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = md.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
//            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }

        //return complete hash
        return sb.toString();
    }
    
    public String getSignedRequest(EsignRequest er, EsignRequestSignee ers) {
        loggerService.logApi("nsdl request user: " + ers.toString());
        try {
            ers.setTransactionId(PasswordUtil.generateOpaque(ers.getId().toString()));
            String file = er.getFile();
            
            String ekycId = "";
            String pdfReadServerPath = basePath + file;
//        String aspId = "ASPXVPLUAT002271";
//        String authMode = "1";
//        String responseUrl = "http://13.229.236.211/nsdltest/nsdlresponse.php";
            String responseUrlP = responseUrl.replace("{token}", ers.getToken());
            String p12CertificatePath = basePath + certificate;
            String p12CertiPwd = certificatePassword;
            String tickImagePath = basePath + tickImage;
//            String alias = alias;
            String nameToShowOnSignatureStamp = ers.getApplicantName();
            String locationToShowOnSignatureStamp = "";
            String reasonForSign = "";
            String pdfPassword = "";
            String txn = ers.getTransactionId();
            
            //sign on all page
            ArrayList<Integer> listOfPageNumbers = new ArrayList<Integer>();
            ArrayList<Integer> listOfxCoordinates = new ArrayList<Integer>();
            ArrayList<Integer> listOfyCoordinates = new ArrayList<Integer>();
            ArrayList<Integer> listOfSignatureHeights = new ArrayList<Integer>();
            ArrayList<Integer> listOfSignatureWidths = new ArrayList<Integer>();
            int pages = getPdfPageCount(pdfReadServerPath);
             //signature place logic
            //kept x is fixed by assuming it will be 570 for all agreement document
            //if variable comes for certain document then dynamic width needs to be taken
            int s = ers.getSequence();
            
            //switch sign position
            switch (s) {
                case 1:
                    s = 4;
                    break;
                case 2:
                    s = 5;
                    break;
                case 3:
                    s = 6;
                    break;
                case 4:
                    s = 1;
                    break;
                case 5:
                    s = 2;
                    break;
                case 6:
                    s = 3;
                    break;
                default:
                    break;
            }
            
            //570 is total width. actual width is 594 just have side padding kept 570
            //making 4*4 grid so divide it by 4 
            //logic is based on 3 customer signature in one line 
            //so will add one grid space in last
            int x = 426 - (s * 142);
            int y = 13;
            while(x < 0){
                y += 50;
                s -= 3;
                x = 426 - (s * 142);
            }
            x += 152;
            
            //as per standard width of 594 we handled this logic
            //to make it more dynamic in width we will get width of first page
            int pageWidth = (int) getPdfPageWidth(pdfReadServerPath);
            x += (pageWidth - 594);
            
            
            for(int p = 1; p <= pages;p++){
                listOfPageNumbers.add(p);
                
               
                listOfxCoordinates.add(x);
                listOfyCoordinates.add(y);
                listOfSignatureHeights.add(25);
                listOfSignatureWidths.add(142);
            }
//            int xCoordinate = x;
//            int yCoordinate = y;
//            int signatureHeights = 25;
//            int signatureWidths = 142;
            

            EsignApplication App = new EsignApplication();
            String requestXml = App.getEsignRequestXml(ekycId, pdfReadServerPath, aspId, authMode, responseUrlP, p12CertificatePath, p12CertiPwd, tickImagePath, 10, alias, nameToShowOnSignatureStamp, locationToShowOnSignatureStamp, reasonForSign, pdfPassword, txn, listOfPageNumbers, listOfxCoordinates, listOfyCoordinates, listOfSignatureHeights, listOfSignatureWidths);
            
            loggerService.logApi("nsdl request: " + requestXml);
            if(requestXml.contains("Error")){
                return "";
            }

//            String documentHash = getFileChecksum(basePath + file);
//            String respSignatureType = null;
//            String signedRequestXml = App.generateEsignRequestXmlUsingHash(ekycId, documentHash, aspId, authMode, responseUrlP, p12CertificatePath, p12CertiPwd, alias, txn, respSignatureType);
//            if(signedRequestXml.contains("Error")){
//                return "";
//            }
            String signedRequestXml = requestXml;
            //test-doc_encryptTemp - first function
            //test-doc_encryptTempSigned - second function
            //test-doc_signedFinal - 
//            er.setFile(file);
//            file = file.replace(".pdf", "_encryptTempSigned.pdf");
//            esignRequestRepository.save(er);
            esignRequestSigneeRepository.save(ers);
//            System.out.println("###################################################");
//            System.out.println("request xml");
//            System.out.println(requestXml);
//            System.out.println("###################################################");
//            System.out.println("signed request xml");
//            System.out.println(signedRequestXml);
//            System.out.println("###################################################");
            return signedRequestXml;

        } catch (Exception ex) {
            loggerService.logApi("nsdl request error: " + ex.getMessage());
        }
        return "";
    }

    public Boolean getSignedResponse(String eSignResp, EsignRequest er, EsignRequestSignee ers) {
        loggerService.logApi("nsdl response user: " + ers.toString());
        loggerService.logApi("nsdl response: " + eSignResp);
        try {
//            String basePath = "D:\\projects\\econtract\\nsdltest\\";
//            String basePath = "/var/www/html/nsdltest/";

            String ekycId = "";
            String outputFinalPdfPath = basePath;
            String pdfReadServerPath = basePath + er.getFile();
//            String aspId = "ASPXVPLUAT002271";
//            String authMode = "1";
//            String responseUrl = "http://13.229.236.211/nsdltest/nsdlresponse.php";
            String p12CertificatePath = basePath + certificate;
            String p12CertiPwd = certificatePassword;
            String tickImagePath = basePath + tickImage;
//            String alias = "le-019b7593-82f7-4abc-9d9d-734de2529f71";
            String nameToShowOnSignatureStamp = ers.getApplicantName();
            String locationToShowOnSignatureStamp = "";
            String reasonForSign = "";
            String pdfPassword = "";
            String txn = ers.getTransactionId();
            ArrayList<Integer> listOfPageNumbers = new ArrayList<Integer>();
            ArrayList<Integer> listOfxCoordinates = new ArrayList<Integer>();
            ArrayList<Integer> listOfyCoordinates = new ArrayList<Integer>();
            ArrayList<Integer> listOfSignatureHeights = new ArrayList<Integer>();
            ArrayList<Integer> listOfSignatureWidths = new ArrayList<Integer>();
            int pages = getPdfPageCount(pdfReadServerPath);
             //signature place logic
            //kept x is fixed by assuming it will be 570 for all agreement document
            //if variable comes for certain document then dynamic width needs to be taken
            int s = ers.getSequence();
            
            //switch sign position
            switch (s) {
                case 1:
                    s = 4;
                    break;
                case 2:
                    s = 5;
                    break;
                case 3:
                    s = 6;
                    break;
                case 4:
                    s = 1;
                    break;
                case 5:
                    s = 2;
                    break;
                case 6:
                    s = 3;
                    break;
                default:
                    break;
            }
            
            //570 is total width
            //making 4*4 grid so divide it by 4 
            //logic is based on 3 customer signature in one line 
            //so will add one grid space in last
            int x = 426 - (s * 142);
            int y = 13;
            while(x < 0){
                y += 50;
                s -= 3;
                x = 426 - (s * 142);
            }
            x += 152;
            
            //as per standard width of 594 we handled this logic
            //to make it more dynamic in width we will get width of first page
            int pageWidth = (int) getPdfPageWidth(pdfReadServerPath);
            x += (pageWidth - 594);
            
            
            for(int p = 1; p <= pages;p++){
                listOfPageNumbers.add(p);
                
               
                listOfxCoordinates.add(x);
                listOfyCoordinates.add(y);
                listOfSignatureHeights.add(25);
                listOfSignatureWidths.add(142);
            }
//            int xCoordinate = x;
//            int yCoordinate = y;
//            int signatureHeights = 25;
//            int signatureWidths = 142;
            

            EsignApplication App = new EsignApplication();
            App.getSignOnDocument(eSignResp, pdfReadServerPath, tickImagePath, 10, nameToShowOnSignatureStamp, locationToShowOnSignatureStamp, reasonForSign, pdfPassword, outputFinalPdfPath, listOfPageNumbers, listOfxCoordinates, listOfyCoordinates, listOfSignatureHeights, listOfSignatureWidths);
            String file = er.getFile().replace(".pdf", "_signedFinal.pdf");
            File f = new File(basePath + file);
            if(f.exists()){
                er.setFile(file);
                esignRequestRepository.save(er);
                return true;
            }
            return false;
            
        } catch (Exception ex) {
            loggerService.logApi("nsdl response error: " + ex.getMessage());
        }
        
        return false;
    }
    
    
    public int getPdfPageCount(String sPDFPath) throws IOException{
        PDDocument doc = PDDocument.load(new File(sPDFPath));
        int p = doc.getNumberOfPages();
        doc.close();
        return p;
    }
    
    public float getPdfPageWidth(String sPDFPath) throws IOException{
        PDDocument doc = PDDocument.load(new File(sPDFPath));
        float w = doc.getPage(0).getMediaBox().getWidth();
        doc.close();
        return w;
    }

}
