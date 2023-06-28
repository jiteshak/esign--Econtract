/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestStampPaperLink;
import com.econtract.esign.model.StampPaper;
import com.econtract.esign.model.constant.StampPaperValue;
import com.econtract.esign.repository.EsignRequestStampPaperLinkRepository;
import com.econtract.esign.repository.StampPaperRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * @author TS
 */
@Service
public class StampPaperService {

    @Autowired
    StampPaperRepository stampPaperRepository;

    @Autowired
    EsignRequestStampPaperLinkRepository esignRequestStampPaperLinkRepository;

    public StampPaper save(StampPaper sp) {
        return stampPaperRepository.save(sp);
    }

    public StampPaper getStampPaper(int id) {
        Optional<StampPaper> spO = stampPaperRepository.findById(id);
        if (!spO.isPresent()) {
            throw new ApiException("Stamp Paper not available");
        }

        StampPaper sp = spO.get();
        return sp;
    }

    public Page<StampPaper> list(Specification<StampPaper> spec, Pageable pageable) {
        return stampPaperRepository.findAll(spec, pageable);
    }

    public void delete(int id) {
        StampPaper sp = this.getStampPaper(id);
        if (sp.getStatus() != 0) {
            throw new ApiException("You can not delete consumed Stamp Paper");
        }

        sp.setIsActive(0);
        stampPaperRepository.save(sp);
    }

    public Map<Integer, Integer> getPaperValues(int amount) {
        int remainingAmount = amount;
        List<Integer> papers = StampPaperValue.getList();
        Map<Integer, Integer> paperValues = new HashMap<>();

        while (remainingAmount != 0) {
            Integer a = 0;
            for (int i = 0; i < papers.size(); i++) {
                int p = papers.get(i);
                if (remainingAmount >= p) {
                    a = p;
                    break;
                }
            }
            ;
            if (a == 0) {
                break;
            }

            //add to paper requirement count
            int c = 1;
            if (paperValues.containsKey(a)) {
                c = paperValues.get(a) + 1;
            }
            paperValues.put(a, c);
            remainingAmount -= a;
        }
        return paperValues;
    }

    public List<StampPaper> getStampPapers(Map<Integer, Integer> paperValues, String state) {
        List<StampPaper> sp = new ArrayList<StampPaper>();
        Date expiryDate = new Date();
        boolean againCheck = true;
        while (againCheck) {
            againCheck = false;
            sp = new ArrayList<StampPaper>();

            try {
                for (Map.Entry<Integer, Integer> pv : paperValues.entrySet()) {
                    List<StampPaper> sp10 = stampPaperRepository.findFirst10ByStateAndIsActiveAndExpiryDateGreaterThanEqualAndStatusAndValueOrderByExpiryDateAsc(state, 1, expiryDate, 0, pv.getKey());

                    int nv = StampPaperValue.getNextLowValue(pv.getKey());

                    int lv = pv.getValue(); //loop vallue
                    for (int j = 0; j < lv; j++) {
                        if ((sp10.size() - 1) < j && pv.getKey() != 1) {
                            paperValues.put(pv.getKey(), pv.getValue() - 1);

                            //check key exist
                            int tnvv = pv.getKey() / nv;
                            int nvv = tnvv;
                            if (paperValues.containsKey(nv)) {
                                nvv += paperValues.get(nv);
                            }
                            paperValues.put(nv, nvv);

                            //if stamp is of 50 then we will get only 2 of 20 
                            //we need to get remainig 10 as another stamp 
                            if ((tnvv * nv) != pv.getKey()) {
                                int rv = Math.abs(pv.getKey() - (tnvv * nv));
                                int rvv = 1;
                                if (paperValues.containsKey(rv)) {
                                    rvv += paperValues.get(rv);
                                }

                                //if 2 paper of 10 can make 20 will make it 
                                //since above we save nvv we can override
                                if (nv <= (rv * rvv)) {
                                    tnvv = (rv * rvv) / nv;

                                    if ((tnvv * nv) != (rv * rvv)) {
                                        rv = Math.abs((tnvv * nv) - (rv * rvv));
                                        rvv = 1;
                                    } else {
                                        rvv = 0;
                                    }

                                    nvv += tnvv;
                                    paperValues.put(nv, nvv);

                                }

                                paperValues.remove(rv);
                                if (rvv != 0) {
                                    paperValues.put(rv, rvv);
                                }
                            }

                            //remove key if 0
                            if (pv.getValue() == 0) {
                                paperValues.remove(pv.getKey());
                            }
                            againCheck = true;
                            continue;
                        }
                        sp.add(sp10.get(j));
                    }
                }
            } catch (Exception ex) {
                sp = new ArrayList<StampPaper>();
            }
        }
        return sp;
    }

    public List<StampPaper> updateStatus(int esignRequestId, List<StampPaper> lsp, int status) {
        List<EsignRequestStampPaperLink> lerspl = new ArrayList<>();
        for (int i = 0; i < lsp.size(); i++) {
            EsignRequestStampPaperLink erspl = null;
            lsp.get(i).setStatus(status);

            //we can query db n check for data regardless of status
            //but it will unneccessary increase db hit
            //so did it directly on status as per currently flow der will be no records if status is 1 
            //n zero then it will have record
            if (status == 0) {
                try {
                    Optional<EsignRequestStampPaperLink> Oerspl = esignRequestStampPaperLinkRepository.findFirstByEsignRequestIdAndStampPaperIdAndStatus(esignRequestId, lsp.get(i).getId(), 1);
                    erspl = Oerspl.get();
                    erspl.setStatus(status);
                    lerspl.add(erspl);
                } catch (Exception e) {
                }
            } else {
                erspl = new EsignRequestStampPaperLink();
                erspl.setEsignRequestId(esignRequestId);
                erspl.setStampPaperId(lsp.get(i).getId());
                erspl.setStampPaperReferenceNo(lsp.get(i).getReferenceNo());
                erspl.setStampPaperValue(lsp.get(i).getValue());

                erspl.setStatus(status);
                lerspl.add(erspl);
            }

        }
        lsp = stampPaperRepository.saveAll(lsp);
        esignRequestStampPaperLinkRepository.saveAll(lerspl);
        return lsp;
    }

    public void deallocateStampPaper(int esignRequestId, List<StampPaper> lsp) {
        lsp = updateStatus(esignRequestId, lsp, 0);
    }

    public void deallocateStampPaper(int esignRequestId, int isStampPreAttached) {
        List<EsignRequestStampPaperLink> lspl = esignRequestStampPaperLinkRepository.findByEsignRequestId(esignRequestId);
        for (int i = 0; i < lspl.size(); i++) {
            if (lspl.get(i).getStatus() == 1) {
                Optional<StampPaper> spO = stampPaperRepository.findById(lspl.get(i).getStampPaperId());
                if (spO.isPresent()) {
                    StampPaper sp = spO.get();
                    sp.setStatus(0);

                    //cancels user stamp paper when canceling the agreement
                    if (isStampPreAttached == 1) {
                        sp.setIsActive(0);
                    }

                    stampPaperRepository.save(sp);
                }
            }
            lspl.get(i).setStatus(0);
        }

        esignRequestStampPaperLinkRepository.saveAll(lspl);
    }

    public List<StampPaper> allocateStampPaper(int esignRequestId, String state, int amount) {
        Map<Integer, Integer> paperValues = getPaperValues(amount);
        List<StampPaper> lsp = getStampPapers(paperValues, state);

        int total = 0;
        for (int i = 0; i < lsp.size(); i++) {
            total += lsp.get(i).getValue();
        }

        //if not got exact amount than don't allocate any one
        if (total != amount) {
            lsp = new ArrayList<>();
        }

        lsp = updateStatus(esignRequestId, lsp, 1);
        return lsp;
    }

    public List<StampPaper> getAllocatedStampPaper(int esignRequestId) {
        List<EsignRequestStampPaperLink> lspl = esignRequestStampPaperLinkRepository.findByEsignRequestId(esignRequestId);
        List<StampPaper> spl = new ArrayList<>();
        for (int i = 0; i < lspl.size(); i++) {
            if (lspl.get(i).getStatus() == 1) {
                Optional<StampPaper> spO = stampPaperRepository.findById(lspl.get(i).getStampPaperId());
                if (spO.isPresent()) {
                    StampPaper sp = spO.get();
                    spl.add(sp);
                }
            }
        }

        return spl;
    }
}
