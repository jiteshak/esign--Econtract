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
import com.econtract.esign.model.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByIdIn(List<Integer> id);
    List<Role> findByIsActiveAndEntityId(int isActive, int entityId);
    List<Role> findByIsActive(int isActive);
    List<Role> findByIsActiveOrderByModifiedDateDesc(int isActive);
}
