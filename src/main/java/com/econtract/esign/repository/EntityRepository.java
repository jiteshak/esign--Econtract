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
import com.econtract.esign.model.EEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EntityRepository extends JpaRepository<EEntity, Integer> {
    List<EEntity> findByIsActive(int isActive);
    Optional<EEntity> findFirstByNameAndIsActive(String name, int isActive);
    //page=0&size=3&sort=createdAt,desc
    Page<EEntity> findByIsActive(int isActive, Pageable pageable);
}
