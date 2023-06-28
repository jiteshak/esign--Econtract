/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository;

import com.econtract.esign.model.UserLink;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author zaidk
 */

@Repository
public interface UserLinkRepository extends JpaRepository<UserLink, Integer>{
     List<UserLink> findByUserIdAndLinkType(int userId, int linkType);
}
