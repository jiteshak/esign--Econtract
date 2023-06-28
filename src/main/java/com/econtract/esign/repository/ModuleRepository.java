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
import com.econtract.esign.model.Module;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer>  {
    Optional<Module> findFirstByName(String name);
    List<Module> findByIdIn(List<Integer> ids);
    List<Module> findByIsActiveAndType(int isActive, int type);
    List<Module> findByIsActiveAndTypeAndParentId(int isActive, int type, int parentId);
    List<Module> findByTypeAndEntityId(int type,int entityId);
    List<Module> findByIsActiveAndTypeAndEntityIdAndParentId(int isActive,int type,int entityId, int parentId);
    List<Module> findByIsActiveAndTypeAndEntityId(int isActive,int entityId, int parentId);
    List<Module> findByIsActiveAndTypeAndEntityIdIn(int isActive, int type, List<Integer> entityId);
}
