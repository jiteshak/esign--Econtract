/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.security;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.exception.PermissionException;
import com.econtract.esign.model.Branch;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.Module;
import com.econtract.esign.model.Role;
import com.econtract.esign.model.Source;
import com.econtract.esign.model.StampPaper;
import com.econtract.esign.model.Task;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.TaskLinkType;
import com.econtract.esign.model.dto.BranchDto;
import com.econtract.esign.model.dto.EntityDto;
import com.econtract.esign.model.dto.ModuleDto;
import com.econtract.esign.model.dto.AclRoleDto;
import com.econtract.esign.model.dto.PermissionDto;
import com.econtract.esign.repository.specification.EsignRequestSpecification;
import com.econtract.esign.repository.specification.SearchCriteria;
import com.econtract.esign.repository.specification.StampPaperSpecification;
import com.econtract.esign.repository.specification.TaskSpecification;
import com.econtract.esign.security.Token;
import com.econtract.esign.security.TokenService;
import com.econtract.esign.service.UserLinkService;
import com.econtract.esign.service.SourceService;
import com.econtract.esign.service.UserService;
import com.econtract.esign.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class AclService {
    
    @Autowired
    UserLinkService userLinkService;
    
    @Autowired
    TokenService tokenService;
    
    @Autowired
    SourceService sourceService;
    
    
    @Autowired
    UserService userService;
    
    Token token;
    
    HttpServletRequest request;
    
    AclRoleDto currentRole;
    
    Integer currentRoleIndex = 0;
    
    
    public void setRequest(HttpServletRequest request){
        this.request = request;
    }
    
    public void verifyAccess(HttpServletRequest request, List<String> modules){
        this.request = request;
        this.token = tokenService.verifyJwt(this.request);
        this.setCurrentRole();
        
        List<PermissionDto> pd =  this.getCurrentRole().getPermissions().stream().filter(p -> modules.contains(p.getCode())).collect(Collectors.toList());
        if(pd.isEmpty() && modules.size() > 0){
            throw new PermissionException("you don't have access to " + modules.toString() + " module(s)");
        }
    }
    
    public void verifyAccess(String t, List<String> modules){
        this.token = tokenService.verifyJwt(t);
        this.setCurrentRole();
        
        List<PermissionDto> pd =  this.getCurrentRole().getPermissions().stream().filter(p -> modules.contains(p.getCode())).collect(Collectors.toList());
        if(pd.isEmpty()){
            throw new PermissionException("you don't have access to " + modules.toString() + " module(s)");
        }
    }
    
    public void verifySourceAccess(HttpServletRequest request){
        this.request = request;
        
        String appId = tokenService.getClientId(request);
//        String data = tokenService.getPostData(request);
        String data = "";
        String token = tokenService.getToken(request);
        this.setSourceAcl(appId);
        
        
        boolean access = tokenService.verifyApiToken(appId, this.getCurrentRole().getSource().getSecretKey() ,data, token);
        
        if(!access){
         throw new ApiException("Invalid token provided");
        }
    }
    
    public void verifySourceAccess(String appId, String token){
        this.request = request;
        
        String data = null;
        this.setSourceAcl(appId);
        
        
        boolean access = tokenService.verifyApiToken(appId, this.getCurrentRole().getSource().getSecretKey() ,data, token);
        
        if(!access){
         throw new ApiException("Invalid token provided");
        }
    }
    
    public void isSourceEntityAllowed(Integer entityId){
        
        if(!this.getCurrentRole().getSourceEntityIds().contains(entityId)){
         throw new ApiException("not in your entity");
        }
    }
    
    public Token getToken(){
        return this.token;
    }
    
    public User getUser(){
        return userService.getUserByUserId(this.token.getUserId());
    }
    
    public Integer getUserId(){
        return this.token.getUserId();
    }
    
    public AclRoleDto getCurrentRole(){
        return this.currentRole;
    }
    
    
    private void setCurrentRole(){
        List<AclRoleDto> rrl = this.token.getAclParsed();
        if(rrl.size() == 0){
           throw new ApiException("No role assigned");
        }
        
        //write logic for multi role here
        //check header if not set use 0
        this.currentRoleIndex = 0;
        this.currentRole = rrl.get(this.currentRoleIndex);
    }
    
    public List<AclRoleDto> getAcl(User user){
        List<AclRoleDto> acl = new ArrayList<>();
        if(user.getRoles() == null){
            return acl;
        }
         List<Branch> branches = userLinkService.getBranches(user);
        List<Module> modules = userLinkService.getModules(user);
        for(int i = 0; i < user.getRoles().size(); i++){
            Role r = user.getRoles().get(i);
            if(r.getIsActive() != 1){
                continue;
            }
            
            
            AclRoleDto rr = AclRoleDto.toDto(r);
            rr.setUserId(user.getId());
            rr.setSourceId(user.getSourceId());
            
            
            List<BranchDto> brl = new ArrayList<>();
            branches.forEach(b -> {
                if(b.getEntityId() != r.getEntityId()){
                    return;
                }
                
                BranchDto br = new BranchDto();
                br.setId(b.getId());
                br.setName(b.getName());
                brl.add(br);
            });
            
             List<ModuleDto> mrl = new ArrayList<>();
            modules.forEach(m -> {
                if(m.getEntityId() != r.getEntityId()){
                    return;
                }
                
                ModuleDto mr = new ModuleDto();
                mr.setId(m.getId());
                mr.setName(m.getName());
                mrl.add(mr);
            });
            
            List<EntityDto> erl = new ArrayList<>();
            user.getSource().getEntities().forEach(e -> {
                EntityDto er = new EntityDto();
                er.setId(e.getId());
                er.setName(e.getName());
                erl.add(er);
            });
            
            rr.setModules(mrl);
            rr.setBranches(brl);
            rr.setSourceEntities(erl);
            acl.add(rr);
        }
        
        
        
        return acl;
    }
    
    public void setSourceAcl(String appId){
        Source s = sourceService.getByAppId(appId);
        if(s.getIsActive() != 1){
            throw new ApiException("Source is not active");
        }
        
//        List<AclRoleDto> rr = this.getAcl(s.getUser());
        List<AclRoleDto> rr = new ArrayList<AclRoleDto>();
        AclRoleDto ard = new AclRoleDto();
        List<EntityDto> erl = new ArrayList<>();
        List<Integer> erlId = new ArrayList<>();
        for(int e = 0; e < s.getEntities().size(); e++){
            erl.add(new EntityDto(s.getEntities().get(e).getId(), s.getEntities().get(e).getName()));
        }
        ard.setSourceEntities(erl);
        ard.setSource(s);
        
        this.currentRoleIndex = 0;
        this.currentRole = ard;
        
        
        if(this.currentRole == null){
            throw new ApiException("No role found");
        }
        if(this.currentRole.getSourceEntities().size() == 0){
            throw new ApiException("No access to entity");
        }
    }
    
    public String getAclString(User user){
        return JsonUtil.toString(this.getAcl(user));
    }

    public Specification<EsignRequest> esignRequest(Specification<EsignRequest> spec) {
        List<Integer> bids = new ArrayList<>();
        this.currentRole.getBranches().forEach(b -> {
            bids.add(b.getId());
        });
        spec = spec.and(new EsignRequestSpecification(new SearchCriteria("branchId", "in", bids)));
        
        List<Integer> mids = new ArrayList<>();
        this.currentRole.getModules().forEach(m -> {
            mids.add(m.getId());
        });
        spec = spec.and(new EsignRequestSpecification(new SearchCriteria("moduleId", "in", mids)));
        
        return spec;
    }

    public Specification<StampPaper> stampPaper(Specification<StampPaper> spec) {
        spec = spec.and(new StampPaperSpecification(new SearchCriteria("sourceId", ":", this.currentRole.getSourceId())));
        return spec;
    }

    public Specification<Task> task() {
        Specification<Task> specSrc = null;
        
        //if has access to source
        if(this.currentRole.getSourceId() > 0){
            specSrc = Specification.where(new TaskSpecification(new SearchCriteria("linkId", ":", this.currentRole.getSourceId())));
            specSrc.and(new TaskSpecification(new SearchCriteria("linkType", ":", TaskLinkType.SOURCE_ID)));
        }
        
        //own created task
        Specification<Task> specUser = Specification.where(new TaskSpecification(new SearchCriteria("modifiedBy", ":", this.token.getUserId())));
        
        Specification<Task> specAcl = Specification.where(specUser);
        if(specSrc != null){
            specAcl.or(specSrc);
        }
        
        Specification<Task> spec = Specification.where(specAcl);
        return spec;
    }
}
