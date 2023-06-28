/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.EsignRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */

@Repository
public interface EsignRequestRepository extends JpaRepository<EsignRequest, Integer>, JpaSpecificationExecutor<EsignRequest> {
    
    //page=0&size=3&sort=createdAt,desc
    Optional<EsignRequest> findByToken(String token);
    List<EsignRequest> findByIsActive(int isActive);
    List<EsignRequest> findByIdIn(List<Integer> ids);
    
    @Override
    Page<EsignRequest> findAll(Specification<EsignRequest> spec, Pageable pageable);
    Page<EsignRequest> findByIsActive(int isActive, Pageable pageable);
    Page<EsignRequest> findByIsActive(int isActive, Specification<EsignRequest> spec,Pageable pageable);
    
    @Query("SELECT DISTINCT state FROM EsignRequest ORDER BY state")
    List<String> findDistinctStates();
    
    Optional<EsignRequest> findFirstByReferenceNumber1(String referenceNumber1);
    Optional<EsignRequest> findFirstByReferenceNumber2(String referenceNumber2);
    Optional<EsignRequest> findFirstByReferenceNumber1OrReferenceNumber2(String referenceNumber1, String referenceNumber2);
    Optional<EsignRequest> findFirstByReferenceNumber1AndReferenceNumber2AndModuleIdAndDocumentCategoryId(String referenceNumber1, String referenceNumber2, int moduleId, int documentCategoryId);
    Optional<EsignRequest> findFirstByReferenceNumber1AndModuleIdAndDocumentCategoryIdAndStatusNot(String referenceNumber1, int moduleId, int documentCategoryId,int status);
    
    List<EsignRequest> findByStateAndStatusAndIsActive(String state, int status, int isActive);
}
