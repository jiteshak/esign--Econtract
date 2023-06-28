/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;


import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.Permission;
import com.econtract.esign.model.Role;
import com.econtract.esign.model.RolePermissionLink;
import com.econtract.esign.model.User;
import com.econtract.esign.model.UserRoleLink;
import com.econtract.esign.model.dto.LoginRequestDto;
import com.econtract.esign.repository.PermissionRepository;
import com.econtract.esign.repository.RolePermissionLinkRepository;
import com.econtract.esign.repository.RoleRepository;
import com.econtract.esign.repository.UserRepository;
import com.econtract.esign.repository.UserRoleLinkRepository;
import com.econtract.esign.util.PasswordUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class UserService {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    UserRoleLinkRepository roleLinkRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    PermissionRepository permissionRepository;
    
    @Autowired
    RolePermissionLinkRepository rolePermissionLinkRepository;
    
    public User save(User user){
        return userRepository.save(user);
    }
    
    public Role save(Role role){
        return roleRepository.save(role);
    }
    
    public User authenticate(LoginRequestDto loginRequest, boolean validatePassword){
        Optional<User> userO = userRepository.findFirstByUserName(loginRequest.getUsername());
        if(!userO.isPresent()){
            throw new ApiException("Incorrect Email Address or Password");
        }
        
        User user = userO.get();
        if(user.getIsActive() != 1){
            throw new ApiException("User account is not active");
        }
        
        LocalDateTime lastLoginTime = user.getLastLogin();
        if(user.getLoginTry() != null && user.getLoginTry().isBefore(LocalDateTime.now().minusMinutes(15))){
            user.setLoginAttempt(0);
        }
        if(user.getLoginAttempt() > 5){
            throw new ApiException("You have exceeded your maximum tries. Please try after 15min");
        }
        
        user.setLoginTry(LocalDateTime.now());
        user.setLoginAttempt(user.getLoginAttempt() + 1);
        user = userRepository.save(user);
        
        if(loginRequest.getPassword() != null && !PasswordUtil.hash2(loginRequest.getPassword()).equalsIgnoreCase(user.getPassword()) && validatePassword){
            throw new ApiException("Incorrect Email Address or Password");
        }
        
        
        user.setLoginAttempt(0);
        user.setLastLogin(LocalDateTime.now());
        user = userRepository.save(user);
        
        user.setLastLogin(lastLoginTime);
        return user;
    }
    
    public User getUserByUserName(String userName){
        Optional<User> userO = userRepository.findFirstByUserName(userName);
        if(!userO.isPresent()){
            throw new ApiException("Invalid User");
        }
        
        User user = userO.get();
        
        return user;
    }
    
    public Boolean isUserPresent(String userName){
        Optional<User> userO = userRepository.findFirstByUserName(userName);
        return userO.isPresent();
    }
    
    public Boolean isUserPresent(User user){
        Optional<User> userO = userRepository.findFirstByUserName(user.getUserName());
        if(!userO.isPresent()){
            return false;
        }
        User u = userO.get();
        if(u.getId().equals(user.getId())){
            return false;
        }
        
        return userO.isPresent();
    }
    
    
    public User getUserByUserId(Integer userId){
        Optional<User> userO = userRepository.findById(userId);
        if(!userO.isPresent()){
            throw new ApiException("Invalid User");
        }
        
        User user = userO.get();
        
        return user;
    }
    
    public List<Role> getRoles(User u){
        List<Role> rl = new ArrayList<Role>();
        List<UserRoleLink> urls = roleLinkRepository.findByUserId(u.getId());
        if(urls.size() == 0){
            return rl;
        }
        List<Integer> rid = new ArrayList<Integer>();
        urls.forEach(cnsmr -> {
            rid.add(cnsmr.getRoleId());
        });
        
        rl = roleRepository.findByIdIn(rid);
        return rl;
    }
    
    public boolean deleteRole(Integer id){
        Optional<Role> rO = roleRepository.findById(id);
        if(!rO.isPresent()){
            return false;
        }
        
        Role r = rO.get();
        r.setIsActive(0);
        roleRepository.save(r);
        
        //remove from role link
        List<UserRoleLink> urll = roleLinkRepository.findByRoleId(r.getId());
        urll.forEach(url -> {
            roleLinkRepository.delete(url);
        });
        
        return true;
    }
    
    public List<UserRoleLink> getRoleLinks(User u){
        List<UserRoleLink> urls = roleLinkRepository.findByUserId(u.getId());
        return urls;
    }
    
    public List<Role> getRolesByEntity(int id){
        return roleRepository.findByIsActiveAndEntityId(1, id);
    }
    
    public Role getRoleById(int id){
        Optional<Role> rO = roleRepository.findById(id);
        return rO.get();
    }
    
    public List<Role> getRoles(){
        return roleRepository.findByIsActiveOrderByModifiedDateDesc(1);
    }
    
    public boolean deleteRoles(int user, List<Integer> roles){
        roleLinkRepository.deleteByUserIdAndRoleIdIn(user, roles);
        return true;
    }
    public boolean createRoles(int user, List<Integer> roles, int createdBy){
        for(int i =0; i < roles.size(); i++){
            UserRoleLink url = new UserRoleLink();
            url.setRoleId(roles.get(i));
            url.setUserId(user);
            url.setModifiedBy(createdBy);
            url.setModifiedDate(LocalDateTime.now());
            roleLinkRepository.save(url);
        }
        return true;
    }
    
    public void resolveRoles(User user, List<Integer> roles, int createdBy){
        List<Integer> dr = new ArrayList<>();//delete
        List<Integer> nr = new ArrayList<>();//new 
        List<Integer> ar = new ArrayList<>();//avaialbe
        List<UserRoleLink> rl = this.getRoleLinks(user);
        for(int i =0; i < rl.size(); i++){
            ar.add(rl.get(i).getRoleId());
            if(!roles.contains(rl.get(i).getRoleId())){
                roleLinkRepository.delete(rl.get(i));
            }
        }
        for(int i =0; i < roles.size(); i++){
            if(!ar.contains(roles.get(i))){
                nr.add(roles.get(i));
            }
        }
        if(nr.size() > 0){
            this.createRoles(user.getId(), nr, createdBy);
        }
    }
    
    
    public List<RolePermissionLink> getPermissionLinks(Role r){
        List<RolePermissionLink> rpls = rolePermissionLinkRepository.findByRoleId(r.getId());
        return rpls;
    }
    
    public boolean createPermissions(int roleId, List<Integer> permissions, int createdBy){
        for(int i =0; i < permissions.size(); i++){
            RolePermissionLink rpl = new RolePermissionLink();
            rpl.setPermissionId(permissions.get(i));
            rpl.setRoleId(roleId);
            rpl.setModifiedBy(createdBy);
            rpl.setModifiedDate(LocalDateTime.now());
            rolePermissionLinkRepository.save(rpl);
        }
        return true;
    }
    
    public void resolvePermissions(Role role, List<Integer> permissions, int createdBy){
        List<Integer> dp = new ArrayList<>();//delete
        List<Integer> np = new ArrayList<>();//new 
        List<Integer> ap = new ArrayList<>();//avaialbe
        List<RolePermissionLink> rpll = this.getPermissionLinks(role);
        for(int i =0; i < rpll.size(); i++){
            ap.add(rpll.get(i).getPermissionId());
            if(!permissions.contains(rpll.get(i).getPermissionId())){
                rolePermissionLinkRepository.delete(rpll.get(i));
            }
        }
        for(int i =0; i < permissions.size(); i++){
            if(!ap.contains(permissions.get(i))){
                np.add(permissions.get(i));
            }
        }
        if(np.size() > 0){
            this.createPermissions(role.getId(), np, createdBy);
        }
    }
    
    
    public List<User> list(){
        return userRepository.findByIsActiveOrderByModifiedDateDesc(1);
    }
    
    public List<Permission> listPermission(){
        return permissionRepository.findByIsActive(1);
    }
    
}
