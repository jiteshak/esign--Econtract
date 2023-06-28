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
public class EsignRequestDetailsDto2 {
    
    
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
    private String file;
    private String token;
    private Integer clientType;
    
    private List<EsignRequestApplicantFormDto> signees;
    
    private BranchDto branch;
    
    private SourceDto source;
    
    private ModuleDto module;
    
    private DocumentCategoryDto documentCategory;
    
    
    public static EsignRequestDetailsDto2 toDto(EsignRequest er){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        EsignRequestDetailsDto2 erd = mm.map(er, EsignRequestDetailsDto2.class);
        
        return erd;
    }
    
    public static List<EsignRequestDetailsDto2> toDtoList(List<EsignRequest> rs){
        return rs.stream().map(r -> EsignRequestDetailsDto2.toDto(r)).collect(Collectors.toList());
    }
    
    public static Page<EsignRequestDetailsDto2> toPageDto(Page<EsignRequest> entities){
        Page<EsignRequestDetailsDto2> dtoPage = entities.map((t) -> {
            EsignRequestDetailsDto2 erd = EsignRequestDetailsDto2.toDto(t);
            return erd;
        });
        
        return dtoPage;
    }
}
