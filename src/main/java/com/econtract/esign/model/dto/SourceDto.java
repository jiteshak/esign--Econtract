/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.Source;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

/**
 *
 * @author TS
 */

@Getter
@Setter
@NoArgsConstructor
public class SourceDto {
    private Integer id;
    private String name;
    
    public static SourceDto toDto(Source s){
        ModelMapper mm = new ModelMapper();
        SourceDto sd = mm.map(s, SourceDto.class);
        return sd;
    }
    
    
    public static List<SourceDto> toDtoList(List<Source> sd){
        return sd.stream().map(s -> SourceDto.toDto(s)).collect(Collectors.toList());
    }
}
