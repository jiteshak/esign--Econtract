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
import com.econtract.esign.model.UserRoleLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRoleLinkRepository extends JpaRepository<UserRoleLink, Integer>  {
    List<UserRoleLink> findByUserId(int userId);
    List<UserRoleLink> findByRoleId(int roleId);
    long deleteByUserIdAndRoleIdIn(int userId, List<Integer> roleId);
}
