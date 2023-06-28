/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Log4j2
@Service
public class LoggerService {
    
    
    public void logEsignRequest(String msg){
        this.log.trace(msg);
    }
    
    
    public void logApi(String msg){
        this.log.debug(msg);
    }
    
    
    
    public void logStampPaper(String msg){
        this.log.log(Level.WARN,msg);
    }
}
