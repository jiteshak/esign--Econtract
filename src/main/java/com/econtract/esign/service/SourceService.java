/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.Source;
import com.econtract.esign.repository.SourceRepository;
import com.econtract.esign.util.Encryptor;
import com.econtract.esign.util.PasswordUtil;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author TS
 */
@Service
public class SourceService {
 
    @Autowired
    SourceRepository sourceRepository;
    
    public SourceService(){
    }
    
    public List<Source> getAll(){
        List<Source> sl =  sourceRepository.findByIsActive(1);
        return sl;
    }
    
    public Source getById(int id){
        Optional<Source> sO =  sourceRepository.findById(id);
        if(!sO.isPresent()){
            throw new ApiException("invalid id");
        }
        
        return sO.get();
    }
    
    
    public Source getByAppId(String appId){
        Optional<Source> sO =  sourceRepository.findFirstByAppId(appId);
        if(!sO.isPresent()){
            throw new ApiException("invalid app id passed");
        }
        
        return sO.get();
    }
    
    public String getAESKey(){
        String key = "";
        try {
            key = Encryptor.getAESKey();
        } catch (Exception ex) {
            
        }   
        return key;
    }
    
    public String getToken(String appId, String key, String data){
        String token = "";
        
        
        try {
            String sessionKey = Encryptor.getAESKey();
            String encryptedSessionKey = Encryptor.encryptAES(sessionKey, key);

            if(data.isEmpty()){
                token = encryptedSessionKey;
                String encryptedHash = Encryptor.encryptAES(appId, sessionKey);
                token = encryptedHash + "||" + encryptedSessionKey;
            }else{
                String hash2 = PasswordUtil.hash2(data);
                String encryptedHash = Encryptor.encryptAES(hash2, sessionKey);
                String hash3 = Encryptor.decryptAES(encryptedHash, sessionKey);
                token = encryptedHash + "||" + encryptedSessionKey;
            }
        } catch (Exception ex) {
           System.out.println(ex);
        }
        
        
        return token;
    }
}
