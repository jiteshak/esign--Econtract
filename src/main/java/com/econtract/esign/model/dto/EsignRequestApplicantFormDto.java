/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.EsignRequestSignee;
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
public class EsignRequestApplicantFormDto {
    
    private String name;
    private String email;
    private String mobile;
    private String telephone;
    private String address;
    private Integer signOption;
    
}
