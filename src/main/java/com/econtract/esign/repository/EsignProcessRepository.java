/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.EsignProcess;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface EsignProcessRepository extends JpaRepository<EsignProcess, Integer>{
    Optional<EsignProcess> findFirstByModuleIdAndDocumentCategoryIdAndClientTypeAndSourceIdAndIsActive(Integer moduleId, Integer documentCategoryId, Integer clientType, Integer sourceId, Integer isActive);
}
