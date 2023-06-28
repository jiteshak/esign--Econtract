package com.econtract.esign.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity    // Enable security config. This annotation denotes config for spring security.
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    TokenService tokenService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        
        http
                .csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // handle an authorized attempts
                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                // Add a filter to validate the tokens with every request
                .addFilterAfter(new JwtTokenAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                // authorization requests config
                .authorizeRequests()
                // allow all who are accessing "auth" service
                .antMatchers(tokenService.getAuthUrl()).permitAll()
                // must be an admin if trying to access admin area (authentication is also required here)
                //.antMatchers("/gallery" + "/admin/**").hasRole("ADMIN")
                .antMatchers(
                        "/**/customer/esign/**",
                        "/esign/agreement/**",
                        "/esign/agreement/**",
                        "/task/file/**",
                        "/source/**", //this route will verify through source header
                        "/**/nsdl/**",
                        "/**/sign/emudra/response/**"
                )
                .permitAll()
                // Any other request must be authenticated
                .anyRequest().authenticated();
        
    }
}
