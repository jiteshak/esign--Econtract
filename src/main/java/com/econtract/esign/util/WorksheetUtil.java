/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.util;

import java.util.Date;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author TS
 */
public class WorksheetUtil {
   public static int getNumberCell(XSSFRow row,int cellIndex){
       int v = 0;
       try{
           v = (int) row.getCell(cellIndex).getNumericCellValue();
       }catch(Exception ex){}
       return v;
   } 
   
   public static String getStringCell(XSSFRow row,int cellIndex){
       String v = "";
       try{
           v = row.getCell(cellIndex).getStringCellValue();
       }catch(Exception ex){}
       return v;
   } 
   
   
   public static String getRawCell(XSSFRow row,int cellIndex){
       String v = "";
       try{
           v = row.getCell(cellIndex).getRawValue();
           if(v.length() == 9){
                v = v + "0";
           }
       }catch(Exception ex){}
       return v;
   } 
   
   public static Date getDateCell(XSSFRow row,int cellIndex){
       Date v = null;
       try{
           v =  row.getCell(cellIndex).getDateCellValue();
       }catch(Exception ex){}
       return v;
   } 
   
   public static boolean getBooleanCell(XSSFRow row,int cellIndex){
       boolean v = false;
       try{
           v =  row.getCell(cellIndex).getBooleanCellValue();
       }catch(Exception ex){}
       return v;
   } 
}
