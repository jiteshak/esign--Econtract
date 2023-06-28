package com.econtract.esign.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.econtract.esign.exception.ApiException;

import java.io.IOException;
import java.util.List;

public class JsonUtil {

    public static String toString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new ApiException(e.toString());
        }
    }

    public static Object toObject(String json, Class obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            try{
                return mapper.readValue(json, obj);
            }catch(Exception ex){
                return  mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, obj));
            }
        } catch (JsonProcessingException e) {
            
            throw new ApiException(e.toString());
        } catch (IOException e) {
            throw new ApiException(e.toString());
        }
    }

}
