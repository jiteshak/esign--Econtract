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
import com.econtract.esign.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>  {
    Optional<User> findFirstByUserName(String userName);
    Optional<User> findFirstById(Integer id);
    List<User> findByIsActive(int isActive);
    List<User> findByIsActiveOrderByModifiedDateDesc(int isActive);
}
