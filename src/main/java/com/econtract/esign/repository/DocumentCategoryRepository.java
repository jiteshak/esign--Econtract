/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.DocumentCategory;
import com.econtract.esign.model.State;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface DocumentCategoryRepository  extends JpaRepository<DocumentCategory, Integer> {
    List<DocumentCategory> findByEntityId(int entityId);
    Optional<DocumentCategory> findFirstByName(String name);
    Optional<DocumentCategory> findFirstByCode(int code);
}
