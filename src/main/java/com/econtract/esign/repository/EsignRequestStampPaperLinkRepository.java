/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.EsignRequestStampPaperLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author TS
 */
@Repository
public interface EsignRequestStampPaperLinkRepository  extends JpaRepository<EsignRequestStampPaperLink, Integer> {
    Optional<EsignRequestStampPaperLink> findFirstByEsignRequestIdAndStampPaperId(int esignRequestId, int stampPaperId);
    Optional<EsignRequestStampPaperLink> findFirstByEsignRequestIdAndStampPaperIdAndStatus(int esignRequestId, int stampPaperId, int status);
    List<EsignRequestStampPaperLink> findByEsignRequestId(int esignRequestId);
}
