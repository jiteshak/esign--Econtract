/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author TS
 */
@Configuration
public class RestTemplateConfig{
 
	@Bean
	public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    		      TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
 
    		      SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    		.loadTrustMaterial(null, acceptingTrustStrategy)
                    		.build();
 
    		      SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
 
    		      CloseableHttpClient httpClient = HttpClients.custom()
                    		.setSSLSocketFactory(csf)
                    		.build();
 
    		      HttpComponentsClientHttpRequestFactory requestFactory =
                    		new HttpComponentsClientHttpRequestFactory();
 
    		requestFactory.setHttpClient(httpClient);
    		RestTemplate restTemplate = new RestTemplate(requestFactory);
   		return restTemplate;
 	}
}