/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.controller;

import com.econtract.esign.model.Task;
import com.econtract.esign.model.constant.PermissionType;
import com.econtract.esign.model.dto.CommonDto;
import com.econtract.esign.repository.specification.SearchCriteria;
import com.econtract.esign.repository.specification.TaskSpecification;
import com.econtract.esign.security.AclService;
import com.econtract.esign.service.TaskService;
import com.econtract.esign.util.FileUtil;
import java.util.Arrays;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author TS
 */
@RestController
@RequestMapping("/task/")
public class TaskController {
    
    @Autowired
    AclService aclService;
    
    @Autowired
    TaskService taskService;
    
    
    @GetMapping("list")
    public Page<Task> list(HttpServletRequest request, Pageable pageable, @RequestParam(required = false) int id,@RequestParam(required = false) int status){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.TASK));
        
        
        Specification<Task> spec = aclService.task();
        
        if(status > 0){
            spec = spec.and(new TaskSpecification(new SearchCriteria("status",":", status)));
        }
        
        
        if(id > 0){
            spec = spec.and(new TaskSpecification(new SearchCriteria("id",":", id)));
        }
        
        return taskService.list(spec, pageable);
    }
    
    
    @PostMapping("reset/{id}")
    public CommonDto reset(HttpServletRequest request, @PathVariable("id") int id){
        aclService.verifyAccess(request, Arrays.asList(PermissionType.TASK));
        
        taskService.moveToNew(id);
        
        return new CommonDto("Status updated to new");
    }
    
    @GetMapping("file/{id}")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request, @PathVariable("id") int id, @RequestParam("token") String token){
        aclService.verifyAccess(token, Arrays.asList(PermissionType.TASK));
        
        Task t = taskService.getTask(id);
        
        String fullPath = taskService.getFullFilePath(t, aclService.getCurrentRole().getSourceId());
        byte[] contents = FileUtil.readBytesFromFile(fullPath);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        String filename = t.getFile();
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }
}
