/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.Branch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    Optional<Branch> findFirstByName(String name);
    List<Branch> findByIdIn(List<Integer> ids);
    List<Branch> findByEntityIdIn(List<Integer> ids);
    List<Branch> findByEntityId(Integer entityId);
}
