package com.econtract.esign.repository;

import com.econtract.esign.model.ResetPassword;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Integer> {

    Optional<ResetPassword> findFirstByUserNameAndIsValid(String userName, int isValid);

    Optional<ResetPassword> findFirstByToken(String token);

    Optional<ResetPassword> findFirstByUserName(String userName);
}
