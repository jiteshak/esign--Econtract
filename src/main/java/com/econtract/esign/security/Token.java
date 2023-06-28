package com.econtract.esign.security;

import com.econtract.esign.model.dto.AclRoleDto;
import com.econtract.esign.util.JsonUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Token {
    //userId
    private String sub;
    private String email;
    private String userType;
    private int userId;
    private int loginLogId;
    private String userName;
    private String acl;
    private String group;


    public Token(String sub, String userType,String email,String group) {
        this.sub = sub;
        this.email = email;
        this.userType = userType;
        this.group= group;
    }
    
    public List<AclRoleDto> getAclParsed(){
        List<AclRoleDto> rr = new ArrayList<>();
        
        try{
           rr = (List<AclRoleDto>) JsonUtil.toObject(this.acl, AclRoleDto.class);
        }catch(Exception ex){}
        
        return rr;
    }
    
    public String getGroup(){
        if(this.group == null){
            return "";
        }
        
        return this.group;
    }
}
