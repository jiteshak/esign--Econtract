/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

/**
 *
 * @author TS
 */
import com.econtract.esign.model.StampPaper;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface StampPaperRepository extends JpaRepository<StampPaper, Integer>, JpaSpecificationExecutor<StampPaper> {
    @Override
    Page<StampPaper> findAll(Specification<StampPaper> spec, Pageable pageable);
    List<StampPaper> findByStateAndIsActive(String state, int isActive);
    List<StampPaper> findByStateAndIsActiveAndExpiryDateGreaterThanEqual(String state, int isActive, Date expiryDate);
    List<StampPaper> findFirst10ByStateAndIsActiveAndExpiryDateGreaterThanEqualAndStatusAndValueOrderByExpiryDateAsc(String state, int isActive, Date expiryDate, int status, int value);
    Boolean existsByReferenceNoAndStateAndIsActive(String referenceNo,String state, int isActive);
}
