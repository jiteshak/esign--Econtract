/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.constant;

import lombok.Getter;
import lombok.Setter;
/**
 *
 * @author TS
 */
@Getter
@Setter
public class EsignProcessSettings {
    public static String TRUE = "TRUE";
    public static String FALSE = "FALSE";
    public static String SYSTEM = "SYSTEM";
    public static String EXTERNAL = "EXTERNAL";
    
    
    //SYSTEM/EXTERNAL/FALSE/TRUE
    private Integer allocateStampPaper = 0;
    private String agreement = "";
    private Integer stampPaperAnnexure = 0;
    private Integer numberOfOfficeSign = 0;
    private Integer officeSignFirst = 0;
    private String officeGroup = "";
}
