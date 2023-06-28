/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.abfl.saml.AttributeSet;
import com.abfl.saml.IdPConfig;
import com.abfl.saml.SAMLClient;
import com.abfl.saml.SAMLInit;
import com.abfl.saml.SAMLUtils;
import com.abfl.saml.SPConfig;
import com.econtract.esign.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class AdidService {
    
    @Autowired
    LoggerService loggerService;
    
    public String getUrl() throws Exception{
        SAMLClient client=null;
        SAMLInit.initialize();
    

        IdPConfig idpConfig = new IdPConfig(FileUtil.getFileFromResource("adid/idpmeta.xml"));
        SPConfig spConfig = new SPConfig(FileUtil.getFileFromResource("adid/spmeta.xml"));
	client = new SAMLClient(spConfig, idpConfig);
	
	String requestId = SAMLUtils.generateRequestId();
	String authrequest = client.generateAuthnRequest(requestId);
	String url = client.getIdPConfig().getLoginUrl() +"?SAMLRequest=" + URLEncoder.encode(authrequest, "UTF-8");
	return url;
    }
    
    public String renderAdidResponse(String response){
        loggerService.logApi("adid response: " + response);
        try{
            SAMLInit.initialize();
            IdPConfig idpConfig = new IdPConfig(FileUtil.getFileFromResource("adid/idpmeta.xml"));
            SPConfig spConfig = new SPConfig(FileUtil.getFileFromResource("adid/spmeta.xml"));
            
            SAMLClient client = new SAMLClient(spConfig, idpConfig);
            AttributeSet aset = client.validateResponse(response);
            String user = aset.getNameId();
            
            loggerService.logApi("adid user: " + user);
            return user;
        }catch(Exception ex){
            loggerService.logApi("adid error: " + ex.getMessage());
        }
        return "";
    }
}
