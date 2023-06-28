/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.service;

import com.econtract.esign.exception.ApiException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author TS
 */
@Service
public class FileStorageService {
    
    
    private Path fileStorageLocation;
    
    public FileStorageService() {

    }
    
    public boolean isExist(String file){
        try{
            File f = new File(file);
            if(f.isFile() && f.exists()){
                return true;
            }
        }catch(Exception ex){
        }
        
        
        return false;
    }
    
    public String joinPath(String path1, String path2){
        try{
            File f = new File(path1, path2);
            return f.toString();
        }catch(Exception ex){
        }
        
        
        return "";
    }
    
    public String store(MultipartFile file, String path, String fileName){
        this.fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();
        
        fileName = StringUtils.cleanPath(fileName);
        
        try {
            //check path exist
            if(!this.fileStorageLocation.toFile().exists()){
                this.fileStorageLocation.toFile().mkdirs();
            }
            
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new ApiException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new ApiException("Could not store file " + fileName + ". Please try again!");
        }
    }
    
    
    public String store(byte[] bytes, String path, String fileName){ 
        this.fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();
        fileName = StringUtils.cleanPath(fileName);
        
        try { 
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            File file = new File(targetLocation.toString());
            OutputStream os = new FileOutputStream(file);
            
            os.write(bytes);
            os.close();
        } catch (Exception e) { 
            throw new ApiException("Could not store file " + fileName + ". Please try again!");
        } 
       return fileName;
    } 
  
    
    public File read(String path){
        return new File(path);
    }
    
    public FileOutputStream getOuputStream(String path) throws FileNotFoundException{
        return new FileOutputStream(path);
    }
    
    
    public FileInputStream getInputStream(String path) throws FileNotFoundException{
        return new FileInputStream(path);
    }
    
    public void move(String source, String destination, String fileName, String newFileName){
        if(newFileName.isEmpty()){
            newFileName = fileName;
        }
        
        File sf = new File(source + File.separator + fileName);
        
        if(!sf.exists()){
            throw new ApiException("not found path:" + source + " file:" + fileName);
        }
        
        File df = new File(destination + File.separator + newFileName);
        
        
        File d = new File(destination);
        if(!d.exists()){
            d.mkdirs();
        }
        
        sf.renameTo(df);
        if(sf.exists()){
            sf.delete();
        }
    }
    
    public void move(String source, String destination, String fileName){
        move(source, destination, fileName, "");
    }
}
