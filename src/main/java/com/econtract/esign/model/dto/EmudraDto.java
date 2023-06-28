/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
public class EmudraDto {
    
    //PDF, XML, DATA
    private String Filetype;
    private String File;
    private String ReferenceNumber;
    private String Name;
    private String Authtoken;
    private Integer SignatureType;
    private String SignerID;
    
    @JsonProperty("IsCosign")
    private boolean IsCosign;
    private String SelectPage;
    private String PageNumber;
    private String PagelevelCoordinates;
    private String SignaturePosition;
    private String CustomizeCoordinates;
    private boolean PreviewRequired;
    private boolean Enableuploadsignature;
    private boolean Enablefontsignature;
    private boolean EnableDrawSignature;
    private boolean EnableeSignaturePad;
    private boolean Storetodb;
    private boolean EnableViewDocumentLink;
    private String SUrl;
    private String Furl;
    private String Curl;
    private boolean IsGSTN;
    private boolean IsGSTN3B;
}
