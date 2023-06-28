package com.econtract.esign.service;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author TS
 */
import com.nsdl.esign.preverifiedNo.controller.EsignApplication;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
//    public static void main(String args[]){
//        System.out.println("Signature signing started");
//        
//        Main.init();
//    }
    
    public  static String init() {
        try { 
            String basePath = "D:\\projects\\econtract\\nsdltest\\";
//            String basePath = "/var/www/html/nsdltest/";
            
            String ekycId = "";
            String pdfReadServerPath = basePath + "test-doc.pdf";
            String aspId = "ASPXVPLUAT002271";
            String authMode = "1";
            String responseUrl = "http://13.229.236.211/nsdltest/nsdlresponse.php";
            String p12CertificatePath = basePath + "DS Xtend Value Private Limited Docusigner Certificate.pfx";
            String p12CertiPwd = "pass1word";
            String tickImagePath = basePath + "tick.png";
            String alias = "le-019b7593-82f7-4abc-9d9d-734de2529f71";
            String nameToShowOnSignatureStamp = "Suhail";
            String locationToShowOnSignatureStamp = "";
            String reasonForSign="Test";
            String pdfPassword="";
            String txn="";
            ArrayList<Integer> listOfPageNumbers = new ArrayList<Integer>();
            listOfPageNumbers.add(1);
            listOfPageNumbers.add(2);
            ArrayList<Integer> listOfxCoordinates = new ArrayList<Integer>();
            listOfxCoordinates.add(100);
            listOfxCoordinates.add(100);
            ArrayList<Integer> listOfyCoordinates = new ArrayList<Integer>();
            listOfyCoordinates.add(100);
            listOfyCoordinates.add(100);
            ArrayList<Integer> listOfSignatureHeights = new ArrayList<Integer>();
            listOfSignatureHeights.add(50);
            listOfSignatureHeights.add(50);
            ArrayList<Integer> listOfSignatureWidths = new ArrayList<Integer>();
            listOfSignatureWidths.add(150);
            listOfSignatureWidths.add(150);
            
            String documentHash = Main.getHash(pdfReadServerPath);
            String respSignatureType = null;
            
            
            //EsignApplication App = new EsignApplication();
            //String requestXml = App.getEsignRequestXml(ekycId, pdfReadServerPath, aspId, authMode, responseUrl, p12CertificatePath, p12CertiPwd, tickImagePath, 10, alias, nameToShowOnSignatureStamp, locationToShowOnSignatureStamp, reasonForSign, pdfPassword, txn, listOfPageNumbers, listOfxCoordinates, listOfyCoordinates, listOfSignatureHeights, listOfSignatureWidths);
            //String signedRequestXml = App.generateEsignRequestXmlUsingHash(ekycId, documentHash, aspId, authMode, responseUrl, p12CertificatePath, p12CertiPwd, alias, txn, respSignatureType);
            
            System.out.println("###################################################");
            System.out.println("request xml");
            //System.out.println(requestXml);
            System.out.println("###################################################");
            System.out.println("signed request xml");
            //System.out.println(signedRequestXml);
            System.out.println("###################################################");
            String signedRequestXml = "";
            return signedRequestXml;
            
//            String res = Main.nsdlAPI(signedRequestXml);
//            System.out.println("nsdl response");
//            System.out.println(res);
//            System.out.println("###################################################");
            
        } catch (Exception ex) {
            System.out.println("###################################################");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("###################################################");
        }
        
        return "";
    }
    
    public static void response(String eSignResp){
        try{
            String basePath = "D:\\projects\\econtract\\nsdltest\\";
//            String basePath = "/var/www/html/nsdltest/";
            
            String outputFinalPdfPath = basePath;
            String ekycId = "";
            String pdfReadServerPath = basePath + "test-doc.pdf";
            String aspId = "ASPXVPLUAT002271";
            String authMode = "1";
            String responseUrl = "http://13.229.236.211/nsdltest/nsdlresponse.php";
            String p12CertificatePath = basePath + "DS Xtend Value Private Limited Docusigner Certificate.pfx";
            String p12CertiPwd = "pass1word";
            String tickImagePath = basePath + "tick.png";
            String alias = "le-019b7593-82f7-4abc-9d9d-734de2529f71";
            String nameToShowOnSignatureStamp = "Suhail";
            String locationToShowOnSignatureStamp = "";
            String reasonForSign="Test";
            String pdfPassword="";
            String txn="";
            ArrayList<Integer> listOfPageNumbers = new ArrayList<Integer>();
            listOfPageNumbers.add(1);
            listOfPageNumbers.add(2);
            ArrayList<Integer> listOfxCoordinates = new ArrayList<Integer>();
            listOfxCoordinates.add(100);
            listOfxCoordinates.add(100);
            ArrayList<Integer> listOfyCoordinates = new ArrayList<Integer>();
            listOfyCoordinates.add(100);
            listOfyCoordinates.add(100);
            ArrayList<Integer> listOfSignatureHeights = new ArrayList<Integer>();
            listOfSignatureHeights.add(50);
            listOfSignatureHeights.add(50);
            ArrayList<Integer> listOfSignatureWidths = new ArrayList<Integer>();
            listOfSignatureWidths.add(150);
            listOfSignatureWidths.add(150);
            
            
            //EsignApplication App = new EsignApplication();
            //App.getSignOnDocument(eSignResp, pdfReadServerPath, tickImagePath, 0, nameToShowOnSignatureStamp, locationToShowOnSignatureStamp, reasonForSign, pdfPassword, outputFinalPdfPath, listOfPageNumbers, listOfxCoordinates, listOfyCoordinates, listOfSignatureHeights, listOfSignatureWidths);
        }catch (Exception ex){
            System.out.println("###################################################");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("###################################################");
        }
    }
    
    public static String getHash(String documentName){
        StringBuffer hexString = new StringBuffer();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = md.digest(documentName.getBytes(StandardCharsets.UTF_8));
        
        
        for (int i = 0; i < encodedhash.length; i++) {
        String hex = Integer.toHexString(0xff & encodedhash[i]);
        if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hexString.toString();
    }
    
    
    public void getP12() throws KeyStoreException, IOException{
        try {
            KeyStore p12 = KeyStore.getInstance("pkcs12");
            p12.load(new FileInputStream("DS Xtend Value Private Limited Docusigner Certificate.pfx"), "pass1word".toCharArray());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static String nsdlAPI(String requestXml){
        String nsdl = "https://pregw.esign.egov-nsdl.com/nsdl-esp/authenticate/esign-doc/";
        
        try{
            URL url = new URL(nsdl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type", "multipart/form-data");
            con.setRequestProperty( "charset", "utf-8");
            con.setDoOutput( true );
            con.setUseCaches( false );
            
            Map<String, String> parameters = new HashMap<>();
            parameters.put("msg", requestXml);
            
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            
            BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            out.flush();
            out.close();
            return content.toString();
            
            
        }catch(Exception ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }
}
