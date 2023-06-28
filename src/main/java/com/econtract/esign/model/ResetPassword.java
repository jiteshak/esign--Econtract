package com.econtract.esign.model;


import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reset_password")
public class ResetPassword {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    
    private Integer userId;
    private String ip;
    private String deviceType;
    private String userName;

    private String token;
    private Integer ttl;
    private Integer isValid;
    private Integer attemp;

    @CreatedDate
    private Date createdAt;

    
}
