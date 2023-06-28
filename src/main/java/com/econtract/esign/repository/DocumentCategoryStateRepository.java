/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.DocumentCategory;
import com.econtract.esign.model.DocumentCategoryState;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author TS
 */
@Repository
public interface DocumentCategoryStateRepository extends JpaRepository<DocumentCategoryState, Integer> {
    List<DocumentCategoryState> findByEntityId(int entityId);
    List<DocumentCategoryState> findByModuleIdAndDocumentCategoryIdAndState(int moduleId ,int documentCategoryId, String state);
}
