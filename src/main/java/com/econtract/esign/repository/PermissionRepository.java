/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.Permission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findByIdIn(List<Integer> id);
    List<Permission> findByIsActive(int isActive);
}
