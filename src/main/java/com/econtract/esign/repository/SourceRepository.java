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
import com.econtract.esign.model.Source;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SourceRepository extends JpaRepository<Source, Integer>  {
    Optional<Source> findFirstByName(String name);
    Optional<Source> findFirstByAppId(String appId);
    List<Source> findByIsActive(Integer isActive);
}
