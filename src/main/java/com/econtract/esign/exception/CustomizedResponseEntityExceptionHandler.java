package com.econtract.esign.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request){
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.hashCode(), "", "EC500", ex.getMessage(), request.getDescription(false));

        return new  ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), 400, "EC400", "Bad Request","Validation Failed",
                ex.getBindingResult().toString());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public final ResponseEntity<Object> handleMethodArgumentNotValid(ApiException ex, WebRequest request){
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), 400, "Bad Request", ex.getErrorCode(), ex.getMessage(), request.getDescription(false));

        return new  ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
    
    
    @ExceptionHandler(PermissionException.class)
    public final ResponseEntity<Object> handleMethodArgumentNotValid(PermissionException ex, WebRequest request){
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), 403, "Bad Request", "EC403",ex.getMessage(), request.getDescription(false));

        return new  ResponseEntity(exceptionResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<Object> handleAnauthorized(ApiException ex, WebRequest request){
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), 401, "Unauthorized Exception", "EC401",ex.getMessage(), request.getDescription(false));

        return new  ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
