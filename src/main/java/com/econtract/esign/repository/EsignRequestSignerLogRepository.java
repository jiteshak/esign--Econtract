/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.EsignRequestSignerLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface EsignRequestSignerLogRepository extends JpaRepository<EsignRequestSignerLog, Integer>  {
    List<EsignRequestSignerLog> findByEsignRequestId(int esignRequestId);
    Optional<EsignRequestSignerLog> findFirstByEsignRequestIdOrderByIdDesc(int esignRequestId);
    Optional<EsignRequestSignerLog> findFirstByEsignRequestIdAndSignatoryIdAndTypeOrderByIdDesc(int esignRequestId, int signatoryId, int type);
}
