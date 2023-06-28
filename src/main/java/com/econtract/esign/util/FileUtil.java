/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 *
 * @author TS
 */
@Service
public class FileUtil {
    
    public static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }

    
    public static File getFileFromResource(String path) throws IOException{
        return new ClassPathResource(path).getFile();
    }
    
    public static String getBase64(String path){
        String base64 = DatatypeConverter.printBase64Binary(FileUtil.readBytesFromFile(path));
        return base64;
    }
}
