/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.EsignRequestSignee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface EsignRequestSigneeRepository extends JpaRepository<EsignRequestSignee, Integer> {
    
    Optional<EsignRequestSignee> findByToken(String token);
    List<EsignRequestSignee> findByEsignRequestId(int esignRequestId);
}
