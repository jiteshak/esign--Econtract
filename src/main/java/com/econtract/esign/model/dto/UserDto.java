/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDto  implements Serializable{
    private Integer id;
    
    private Integer sourceId;
    private String sourceName;
    
    private Integer stampPaperSourceId;
    
    private List<EntityDto> sourceEntities = new ArrayList<>();
    
    
    private Integer InternalId;
    private String externalId;
    
    private String userName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Long contact;
    private String gender;
    private Date dob;
    private LocalDateTime lastLogin;
    
    private String group;
    
    private List<Integer> moduleIds = new ArrayList<>();
    private List<RoleDto> roles = new ArrayList<>();
    
    private List<BranchDto> branches = new ArrayList<>();
    
    private Integer modifiedBy;
    private LocalDateTime modifiedDate;
    
    //for form
    private List<Integer> roleIds;
    private List<Integer> branchIds;
    
    private List<ModuleDto> modules;
    
    
    //send error
    private String error;
    
    public static Boolean isValid(UserDto ud){
        String ep = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern p = Pattern.compile("(0/91)?[1-9][0-9]{9}");
        Pattern e = Pattern.compile(ep);
        Matcher m = null;
        
        
        if(ud.getExternalId() == null){
            ud.setError("Employee Id is required");
        }else if(ud.getExternalId().isEmpty()){
            ud.setError("Employee Id is required");
        }else if(ud.getUserName() == null){
            ud.setError("Username is required");
        }else if(ud.getUserName().isEmpty()){
            ud.setError("Username is required");
        }else if(ud.getFirstName() == null){
            ud.setError("First name is required");
        }else if(ud.getFirstName().isEmpty()){
            ud.setError("First name is required");
        }else if(ud.getEmail() == null){
            ud.setError("Email is required");
        }else if(ud.getEmail().isEmpty()){
            ud.setError("Email is required");
        }else if(ud.getContact()== null){
            ud.setError("Mobile is required");
        }else if(ud.getGender() != null && !("M".equals(ud.getGender()) || "F".equals(ud.getGender()) || "O".equals(ud.getGender()))){
            ud.setError("Gender is invalid");
        }else if(ud.getRoleIds() == null){
            ud.setError("Role is required");
        }else if(ud.getRoleIds().isEmpty()){
            ud.setError("Role is required");
        }
        
        
        m = p.matcher(ud.getContact().toString());
        if(!(m.find() && m.group().equals(ud.getContact().toString()))){
            ud.setError("Mobile is not valid");
        }
        m = e.matcher(ud.getEmail());
        if(!(m.find() && m.group().equals(ud.getEmail()))){
            ud.setError("Email is not valid");
        }
        
        return ud.getError() == null;
    }
    
//    public List<Integer> getBranchIds(){
//        List<Integer> el = new ArrayList<>();
//        
//        this.branches.forEach(e -> {
//            el.add(e.getId());
//        });
//        
//        return el;
//    }
    
    
    public List<Integer> getSourceEntityIds(){
        List<Integer> el = new ArrayList<>();
        
        this.sourceEntities.forEach(e -> {
            el.add(e.getId());
        });
        
        return el;
    }
    
    static PropertyMap<UserDto, User> skipSourceFieldsMap = new PropertyMap<UserDto, User>() {
      protected void configure() {
         skip().setSourceId(null);
     }
    };
    
    public static UserDto toDto(User u){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto ud = mm.map(u, UserDto.class);
        
        if(u.getSource() != null){
            ud.setSourceName(u.getSource().getName());
            ud.setSourceEntities(EntityDto.toDtoList(u.getSource().getEntities()));
        }
        
        return ud;
    }
    
    
    public static List<UserDto> toDtoList(List<User> es){
        return es.stream().map(u -> UserDto.toDto(u)).collect(Collectors.toList());
    }
    
    
    
    public static User toModel(UserDto ud){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mm.getConfiguration().setDeepCopyEnabled(false);
        User um = mm.map(ud, User.class);
        
        
        return um;
    }
    
    
}
