/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.DocumentCategory;
import com.econtract.esign.model.EsignRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Converter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
public class EsignRequestDto {
    
    
    private Integer id;
    private String referenceNumber1;
    private String referenceNumber2;
    
    private Date date;
    private Double sanctionAmount;
    private String location;
    private String state;
    
    private String applicantName;
    private String applicantContact;
    private String applicantTelephone;
    private String applicantEmail;
    private String applicantAddress;
    
    
    private Integer applicantSignOption = 0;
    
    
    private Integer applicantionStatus;
    private Integer status;
    private Integer signOption = 2;
    private Integer isSignatorySignRequired = 1;
    private Integer officeSignFirst = 0;
    private Integer numberOfOfficeSign = 1;
    private Integer isCustomerSignRequired = 1;
    private Integer isStampAttached = 0;
    private String file;
    private String token;
    private Integer clientType;
    
    private Integer branchId;
    
    private Integer sourceId;
    
    private Integer moduleId;
    
    private Integer documentCategoryId;
    
    
    public static EsignRequestDto toDto(EsignRequest er){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        EsignRequestDto erd = mm.map(er, EsignRequestDto.class);
        
        return erd;
    }
    
    public static List<EsignRequestDto> toDtoList(List<EsignRequest> rs){
        return rs.stream().map(r -> EsignRequestDto.toDto(r)).collect(Collectors.toList());
    }
    
    public static Page<EsignRequestDto> toPageDto(Page<EsignRequest> entities){
        Page<EsignRequestDto> dtoPage = entities.map((t) -> {
            EsignRequestDto erd = EsignRequestDto.toDto(t);
            return erd;
        });
        
        return dtoPage;
    }
}
