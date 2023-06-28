/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignProcess;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignerLog;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.EsignRequestStatus;
import com.econtract.esign.model.constant.LinkType;
import com.econtract.esign.model.dto.EmudraDto;
import com.econtract.esign.model.dto.EmudraRequestDto;
import com.econtract.esign.model.dto.EmudraServerDto;
import com.econtract.esign.util.CommonUtil;
import com.econtract.esign.util.FileUtil;
import com.econtract.esign.util.JsonUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class EmudraService {

    @Value("${nsdl.base.path}")
    String basePath;

    @Value("${emudra.certificate.path}")
    String certificatePath;

    @Value("${emudra.auth.token}")
    String authtoken;

    @Value("${emudra.private.key}")
    String publicKey;

    @Value("${emudra.signature.type}")
    Integer signatureType;

    @Value("${emudra.response.url}")
    String responseUrl;
    
    @Value("${emudra.response.back.url}")
    String backUrl;
    
    @Autowired
    LoggerService loggerService;
    
    @Autowired
    EsignRequestService esignRequestService;
    
    @Autowired
    FileStorageService fileStorageService;
    
    @Autowired
    EsignProcessService esignProcessService;

    String JCE_PROVIDER = "BC";

    public EmudraRequestDto getSignedRequest(EsignRequest er, EsignRequestSignerLog ersl, User user) {
        loggerService.logApi("emudra sign er: " + er.toString());
        loggerService.logApi("emudra sign user: " + user.toString());
        EmudraRequestDto emr = new EmudraRequestDto();
        try {
            loggerService.logApi("building request");
            String sresponseUrl = responseUrl.replace("{transactionId}", ersl.getId().toString());
            String sbackUrl = backUrl.replace("{token}", er.getToken());
            String pdfReadServerPath = basePath + er.getFile();
            String signCoordinates = "";//do sign base checking
            
            List<EsignRequestSignerLog> logs =  esignRequestService.getEsignRequestSignerLogs(er.getId());
            int sign = 0;
            for(int i = 0; i < logs.size(); i++ ){
                EsignRequestSignerLog c = logs.get(i);
                if(c.getStatus() == 1 && c.getType() == LinkType.USER){
                    sign++;
                }
            }
            loggerService.logApi("logs with signed status =" + sign);
            sign += 1; //going for next sign
            sign = sign * 4;
            
            int line = 1;
            while(sign > 4){
                sign -= 4;
                line++;
            }
            
            if(line == 1){
                line = 2;
            }else if(line == 2){
                line = 1;
            }
//            line = 2;
//            sign = 4;
            
//            int x = 20 + (142 * (sign - 1));
//            int y = 30 + ((line - 1) * 80);
            int x = 20 + (135 * (sign - 1));
            int y = 46 + ((line - 1) * 46);
            
            //CustomizeCoordinates=left,top,width,height
            signCoordinates = x + ","+ y + ",546," + (12 + ((line - 1) * 42));
            if(line == 2){
                signCoordinates = "423,201,550,103";
            }else if(line == 1){
                signCoordinates = "425,102,552,4";
            }
//            signCoordinates = "417,140,537,80";//shared by emudra
//            signCoordinates = "142,50," + x + ","+ y + "";
            
            //condition base
            //check in process header
            //this will always true because in this agreement we have 
            //two people one is customer and another on is signatory
            boolean isCosign = true;

            EmudraDto em = new EmudraDto();
            em.setFiletype("PDF");
            em.setFile(FileUtil.getBase64(pdfReadServerPath));
            em.setReferenceNumber(er.getReferenceNumber1() + " - " + ersl.getId().toString());
            em.setName(user.getName());
            em.setAuthtoken(authtoken);
            em.setSignatureType(signatureType);
            em.setSignerID(user.getId().toString());
            em.setIsCosign(isCosign);
            em.setSelectPage("ALL");
            em.setSignaturePosition("Customize");
            em.setCustomizeCoordinates(signCoordinates);
            em.setPreviewRequired(false);
            em.setEnableuploadsignature(false);
            em.setEnablefontsignature(false);
            em.setEnableDrawSignature(false);
            em.setEnableeSignaturePad(false);
            em.setStoretodb(false);
            em.setEnableViewDocumentLink(false);
            em.setSUrl(sresponseUrl);
            em.setFurl(sresponseUrl);
            em.setCurl(sresponseUrl);
            em.setIsGSTN(false);
            em.setIsGSTN3B(false);

            byte[] sessionKey = GenerateSessionKey();
            String json = JsonUtil.toString(em);
            byte[] hash = GenerateSha256Hash(json.getBytes());
            loggerService.logApi("emudra request json " + json);

            String encryptedData = CommonUtil.toBase64(EncryptUsingSessionKey(sessionKey, json.getBytes()));
            String encryptedHash = CommonUtil.toBase64(EncryptUsingSessionKey(sessionKey, hash));
            String encryptedSessionKey = CommonUtil.toBase64(EncryptUsingPublicKey(sessionKey));

            emr.setParameter1(encryptedSessionKey);
            emr.setParameter2(encryptedData);
            emr.setParameter3(encryptedHash);
            
//            String tid = CommonUtil.toBase64(sessionKey);
//            if(tid.length() > 20){
//                tid = tid.substring(0, 20);
//            }
            ersl.setTransactionId(CommonUtil.toBase64(sessionKey));
            esignRequestService.saveSignLog(ersl);
        } catch (Exception ex) {
            loggerService.logApi("emudra R error =" + ex.getMessage());
        }

        return emr;
    }
    
    public void response(EmudraServerDto esr, EsignRequest er, EsignRequestSignerLog ersl){
        loggerService.logApi("emudra success ersl =" + ersl.toString());
        
        try{
            byte[] file = decrypt(esr.getReturnvalue().getBytes(), ersl.getTransactionId().getBytes());
            String fileName = er.getFile().replace(".pdf", "_emudra.pdf");
            fileStorageService.store(file, basePath, fileName);
            
            ersl.setStatus(1);
            esignRequestService.saveSignLog(ersl);
            
            List<EsignRequestSignerLog> logs =  esignRequestService.getEsignRequestSignerLogs(er.getId());
            int sign = 0;
            for(int i = 0; i < logs.size(); i++ ){
                EsignRequestSignerLog c = logs.get(i);
                if(c.getStatus() == 1 && c.getType() == LinkType.USER){
                    sign++;
                }
            }
            loggerService.logApi("emudra signed logs =" + sign);
            
            if(er.getNumberOfOfficeSign() == sign || er.getNumberOfOfficeSign() == 0){
                er.setStatus(EsignRequestStatus.SINGED_BY_BUSINESS_SIGNATOR);
            }
            
            er.setFile(fileName);
            esignRequestService.save(er);
        }catch(Exception ex){
            loggerService.logApi("emudra error =" + ex.getMessage());
            throw new ApiException(ex.getMessage());
        }
    }

    public byte[] GenerateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyGenerator kgen = KeyGenerator.getInstance("AES", JCE_PROVIDER);
        kgen.init(256);
        SecretKey key = kgen.generateKey();
        byte[] symmKey = key.getEncoded();

        return symmKey;

    }

    public byte[] EncryptUsingSessionKey(byte[] skey, byte[] data) throws InvalidCipherTextException {

        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine(), new PKCS7Padding());
        cipher.init(true, new KeyParameter(skey));
        //
        int outputSize = cipher.getOutputSize(data.length);
        byte[] tempOP = new byte[outputSize];
        int processLen = cipher.processBytes(data, 0, data.length, tempOP, 0);
        int outputLen = cipher.doFinal(tempOP, processLen);
        byte[] result = new byte[processLen + outputLen];
        System.arraycopy(tempOP, 0, result, 0, result.length);

        return result;
    }

    public byte[] GenerateSha256Hash(byte[] message) {
        String algorithm = "SHA-256";
        String SECURITY_PROVIDER = "BC";

        byte[] hash = null;

        MessageDigest digest;

        try {

            digest = MessageDigest.getInstance(algorithm, SECURITY_PROVIDER);
            digest.reset();
            hash = digest.digest(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hash;
    }

    public byte[] EncryptUsingPublicKey(byte[] data) throws IOException, GeneralSecurityException, Exception {
        // encrypt the session key with the public key
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PublicKey publicKey = null;
        byte[] bytevalue = FileUtil.readBytesFromFile(certificatePath);
        //String certificatedata = "";
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        //byte[] bytevalue = Base64.decode(certificatedata);
        InputStream streamvalue = new ByteArrayInputStream(bytevalue);
        java.security.cert.Certificate certificate = certificateFactory.generateCertificate(streamvalue);
        publicKey = certificate.getPublicKey();
        Cipher pkCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        pkCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encSessionKey = pkCipher.doFinal(data);
//         System.out.println("ENC SESSION KEY : "+ encSessionKey.length);
        return encSessionKey;
    }

    public static byte[] decrypt(byte[] text, byte[] key) throws Exception {

        byte[] dec_Key = org.apache.commons.codec.binary.Base64.decodeBase64(key);
        SecretKey dec_Key_original = new SecretKeySpec(dec_Key, 0, dec_Key.length, "AES");
        byte[] decodedValue = org.apache.commons.codec.binary.Base64.decodeBase64(text);
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, dec_Key_original);
        byte[] bytePlainText = aesCipher.doFinal(decodedValue);

        return bytePlainText;

    }
}
