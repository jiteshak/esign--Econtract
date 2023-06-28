/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.model.Branch;
import com.econtract.esign.model.Module;
import com.econtract.esign.model.UserLink;
import com.econtract.esign.model.User;
import com.econtract.esign.model.constant.UserLinkType;
import com.econtract.esign.repository.UserLinkRepository;
import com.econtract.esign.repository.BranchRepository;
import com.econtract.esign.repository.ModuleRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author zaidk
 */
@Service
public class UserLinkService {

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    UserLinkRepository userLinkRepository;

    @Autowired
    ModuleRepository moduleRepository;

    public List<Branch> getBranches(User u) {
        List<Branch> bl = new ArrayList<Branch>();
        List<UserLink> urls = userLinkRepository.findByUserIdAndLinkType(u.getId(), UserLinkType.BRANCH_LINK);
        if (urls.size() == 0) {
            return bl;
        }
        List<Integer> bid = new ArrayList<Integer>();
        urls.forEach(cnsm -> {
            bid.add(cnsm.getLinkId());
        });

        bl = branchRepository.findByIdIn(bid);
        return bl;
    }

    public String getBrancheIds(User u) {
        String ids = "";
        List<UserLink> bls = userLinkRepository.findByUserIdAndLinkType(u.getId(), UserLinkType.BRANCH_LINK);
        if (bls.size() == 0) {
            return "";
        }
        List<Integer> bid = new ArrayList<Integer>();
        for (int i = 0; i < bls.size(); i++) {
            if (!ids.isEmpty()) {
                ids += ",";
            }
            ids += bls.get(i).getLinkId().toString();
        }

        return ids;
    }

    public List<Integer> getBrancheIdList(User u) {
        List<UserLink> bls = userLinkRepository.findByUserIdAndLinkType(u.getId(), UserLinkType.BRANCH_LINK);
        List<Integer> bid = new ArrayList<Integer>();
        for (int i = 0; i < bls.size(); i++) {
            bid.add(bls.get(i).getLinkId());
        }

        return bid;
    }

    public void createBranchUserLink(Integer userId, List<Integer> branchIds, Integer createdBy) {
        List<UserLink> bl = new ArrayList<>();
        for (int i = 0; i < branchIds.size(); i++) {
            UserLink b = new UserLink();
            b.setUserId(userId);
            b.setLinkId(branchIds.get(i));
            b.setLinkType(UserLinkType.BRANCH_LINK);
            b.setModifiedBy(createdBy);
            b.setModifiedDate(LocalDateTime.now());

            bl.add(b);
        }

        userLinkRepository.saveAll(bl);
    }

    public List<UserLink> getBrancheLinkList(User u) {
        List<UserLink> bls = userLinkRepository.findByUserIdAndLinkType(u.getId(), UserLinkType.BRANCH_LINK);
        return bls;
    }

    public void resolveBranches(User user, List<Integer> branches, int createdBy) {
        List<Integer> db = new ArrayList<Integer>();//delete
        List<Integer> nb = new ArrayList<Integer>();//new 
        List<Integer> ab = new ArrayList<Integer>();//avaialbe
        List<UserLink> list = this.getBrancheLinkList(user);
        for (int i = 0; i < list.size(); i++) {
            ab.add(list.get(i).getLinkId());
            if (!branches.contains(list.get(i).getLinkId())) {
                userLinkRepository.delete(list.get(i));
            }
        }
        for (int i = 0; i < branches.size(); i++) {
            if (!ab.contains(branches.get(i))) {
                nb.add(branches.get(i));
            }
        }
        if (nb.size() > 0) {
            this.createBranchUserLink(user.getId(), nb, createdBy);
        }
    }

    public void createModuleUserLink(Integer userId, List<Integer> moduleIds, int createdBy) {
        List<UserLink> ml = new ArrayList<>();

        for (int i = 0; i < moduleIds.size(); i++) {
            UserLink m = new UserLink();
            m.setUserId(userId);
            m.setLinkId(moduleIds.get(i));
            m.setLinkType(UserLinkType.PRODUCT_LINK);
            m.setModifiedBy(createdBy);
            m.setModifiedDate(LocalDateTime.now());

            ml.add(m);
        }

        userLinkRepository.saveAll(ml);
    }

    public List<UserLink> getModuleLinks(User u) {
        List<UserLink> mrls = userLinkRepository.findByUserIdAndLinkType(u.getId(), UserLinkType.PRODUCT_LINK);
        return mrls;
    }

    public void resolveModules(User user, List<Integer> modules, int createdBy) {
        List<Integer> dp = new ArrayList<>();//delete
        List<Integer> np = new ArrayList<>();//new 
        List<Integer> ap = new ArrayList<>();//avaialbe
        List<UserLink> rpll = this.getModuleLinks(user);
        for (int i = 0; i < rpll.size(); i++) {
            ap.add(rpll.get(i).getLinkId());
            if (!modules.contains(rpll.get(i).getLinkId())) {
                userLinkRepository.delete(rpll.get(i));
            }
        }
        for (int i = 0; i < modules.size(); i++) {
            if (!ap.contains(modules.get(i))) {
                np.add(modules.get(i));
            }
        }
        if (np.size() > 0) {
            this.createModuleUserLink(user.getId(), np, createdBy);
        }
    }

    public List<Module> getModules(User u) {
        List<Module> ml = new ArrayList<Module>();
        List<UserLink> urls = userLinkRepository.findByUserIdAndLinkType(u.getId(), UserLinkType.PRODUCT_LINK);
        if (urls.size() == 0) {
            return ml;
        }
        List<Integer> mid = new ArrayList<Integer>();
        urls.forEach(cnsm -> {
            mid.add(cnsm.getLinkId());
        });

        ml = moduleRepository.findByIdIn(mid);
        return ml;
    }

}
