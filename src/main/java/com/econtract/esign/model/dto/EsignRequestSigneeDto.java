/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.EsignRequestSignee;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
public class EsignRequestSigneeDto {
    
    private Integer id;
    private Integer esignRequestId;
    private String applicantName;
    private String applicantContact;
    private String applicantTelephone;
    private String applicantEmail;
    private String applicantAddress;
    private String token;
    private Integer sequence;
    private Integer status = 0;//unsigned = 0, signed = 1
    private Integer signOption = 0;
    private LocalDateTime modifiedDate;
    
    
    
    
    public static EsignRequestSigneeDto toDto(EsignRequestSignee ers){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        EsignRequestSigneeDto erd = mm.map(ers, EsignRequestSigneeDto.class);
        
        return erd;
    }
    
    public static List<EsignRequestSigneeDto> toDtoList(List<EsignRequestSignee> rs){
        return rs.stream().map(r -> EsignRequestSigneeDto.toDto(r)).collect(Collectors.toList());
    }
    
}
