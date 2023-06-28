/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.config;

import com.econtract.esign.security.Token;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author TS
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {
    
    @Bean
    public AuditorAware<Integer> auditorProvider() {

        /*
          if you are using spring security, you can get the currently logged username with following code segment.
          SecurityContextHolder.getContext().getAuthentication().getName()
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object oauth2 = SecurityContextHolder.getContext().getAuthentication().getDetails();
//            val loggedInUserId = oauth2.token.claims["sub"].toString();
//            return Optional.of(loggedInUserId);
        }
        return () -> Optional.ofNullable(0);
    }
    

    @Bean
    public Integer getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return ((Token) authentication.getPrincipal()).getUserId();
    }

}
