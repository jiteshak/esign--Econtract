/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.OtpLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface OtpLogRepository extends JpaRepository<OtpLog, Integer>  {
    List<OtpLog> findByModifiedDateGreaterThanEqual(LocalDateTime modifiedDate);
    List<OtpLog> findByLinkIdAndModifiedDateGreaterThanEqual(Integer linkId, LocalDateTime modifiedDate);
    Optional<OtpLog> findFirstByLinkIdOrderByIdDesc(Integer linkId);
}
