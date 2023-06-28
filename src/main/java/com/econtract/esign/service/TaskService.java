/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.Task;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.TaskLinkType;
import com.econtract.esign.model.constant.TaskStatus;
import com.econtract.esign.repository.TaskRepository;
import com.econtract.esign.repository.specification.SearchCriteria;
import com.econtract.esign.repository.specification.TaskSpecification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author TS
 */
@Service
public class TaskService {
    
    @Autowired
    TaskRepository taskRepository;
    
    @Autowired
    UserService userService;
    
    
    @Autowired
    FileStorageService fileStorageService;
    
    
    @Value("${base.stamp.paper.path}")
    String baseStampPath;
    
    public void TaskService(){
    }
    
    public Task create(int linkId, int linkType, String file, int createdBy){
    
        Task t = new Task();
        
        if(file != null){
            t.setFile(file);
        }
        
        t.setLinkId(linkId);
        t.setLinkType(linkType);
        t.setStatus(TaskStatus.NEW);
        t.setModifiedBy(createdBy);
        t.setModifiedDate(LocalDateTime.now());
        
        t = taskRepository.save(t);
        
        return t;
    }
    
    public List<Task> addEsginRequestInitiationTask(List<EsignRequest> erl, int createdBy){
        List<Task> tl = new ArrayList<>();
        erl.forEach(er -> {
            Task t = this.create(er.getId(), TaskLinkType.ESIGN_REQUEST_ID, null, createdBy);
            tl.add(t);
        });
        return tl;
    }
    
    
    public List<Task> getEsginRequestInitiationNewTask(){
        return taskRepository.findByLinkTypeAndStatus(TaskLinkType.ESIGN_REQUEST_ID, TaskStatus.NEW);
    }
    
    
    public List<Task> getStampPaperUploadNewTask(){
        return taskRepository.findByLinkTypeAndStatus(TaskLinkType.SOURCE_ID, TaskStatus.NEW);
    }
    
    public List<Task> setTaskToInProgress(List<Task> tl){
        for(int i = 0; i < tl.size(); i++){
            tl.get(i).setStatus(TaskStatus.IN_PROGRESS);
        }
        
        tl = taskRepository.saveAll(tl);
        return tl;
    }
    
    public Task setTaskToCompleted(Task t){
        t.setStatus(TaskStatus.COMPLETED);
        t = taskRepository.save(t);
        return t;
    }
    
    
    public Task setTaskToFailed(Task t, String msg){
        t.setStatus(TaskStatus.FAILED);
        t.setMessage(msg);
        t = taskRepository.save(t);
        return t;
    }

    public User getTaskOwnerEmail(Task t) {
        return userService.getUserByUserId(t.getModifiedBy());
    }
    
    public Task addStampPaperUploadTask(String file, int sourceId,int createdBy){
        //TODO: source id required
        Task t = this.create(sourceId, TaskLinkType.SOURCE_ID, file, createdBy);
        return t;
    }
    
    
    public Page<Task> list(Specification<Task> spec,Pageable pageable){
        return taskRepository.findAll(spec, pageable);
    }
    
    public Task getTask(int id){
        Optional<Task> tO = taskRepository.findById(id);
        if(!tO.isPresent()){
            throw new ApiException("Invalid task id");
        }
        
        return tO.get();
    }
    
    public void moveToNew(int id){
        Task t = getTask(id);
        if(t.getStatus() != TaskStatus.IN_PROGRESS){
            throw new ApiException("Only in progress can be moved to new");
        }
        t.setStatus(TaskStatus.NEW);
        taskRepository.save(t);
    }
    
    public String getFullFilePath(Task t, int sourceId){
        String path = "";
        
        if(t.getLinkType() == TaskLinkType.SOURCE_ID){
            path = fileStorageService.joinPath(baseStampPath + sourceId, t.getFile());
        }
        
        return path;
    }
}
