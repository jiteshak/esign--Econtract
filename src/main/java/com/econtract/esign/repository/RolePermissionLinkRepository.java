/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.Permission;
import com.econtract.esign.model.RolePermissionLink;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface RolePermissionLinkRepository extends  JpaRepository<RolePermissionLink, Integer> {
    List<RolePermissionLink> findByRoleId(int roleId);
    List<RolePermissionLink> findByPermissionId(int permissionId);
}
