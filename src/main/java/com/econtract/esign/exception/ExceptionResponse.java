package com.econtract.esign.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class ExceptionResponse {
    private Date timestamp;
    private int status;
    private String errorCode;
    private String error;
    private String message;

    @JsonIgnore
    private String details;

    public ExceptionResponse(Date timestamp, int status, String error, String errorCode,String message, String details) {
        super();
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
