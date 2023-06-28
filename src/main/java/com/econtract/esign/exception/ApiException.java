package com.econtract.esign.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ApiException extends RuntimeException {

    String errorCode;
    
    public ApiException(String exception) {
        super(exception);
    }
    
    public ApiException(String errorCode,String exception) {
        super(exception);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode(){
        return this.errorCode;
    }
}
