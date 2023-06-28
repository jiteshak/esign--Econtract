/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
public class StampPaperDto {
   
    private String esignRequestDocumentRefNo;
    private int esignRequestDocumentCategory;
    private String entity;
    private Integer entityId;
    private String referenceNo;
    private Integer value;
    private String state;
    private String file1;
    private Integer sourceId;
    private Date procurementDate;
    private Date expiryDate;
    
    private MultipartFile file;
}
