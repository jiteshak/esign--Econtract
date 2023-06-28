package com.econtract.esign.security;

import com.econtract.esign.exception.ApiException;
import com.econtract.esign.exception.UnauthorizedException;
import com.econtract.esign.model.OtpLog;
import com.econtract.esign.model.constant.LinkType;
import com.econtract.esign.repository.OtpLogRepository;
import com.econtract.esign.util.Encryptor;
import com.econtract.esign.util.JsonUtil;
import com.econtract.esign.util.PasswordUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class TokenService {

    @Value("${security.jwt.authUrl}")
    private String authUrl;

    @Value("${security.jwt.header}")
    private String header;

    @Value("${security.jwt.prefix}")
    private String prefix;

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private Long expiration;
    
    @Value("${security.client}")
    private String clientId;

    @Autowired
    OtpLogRepository otpLogRepository;

    public TokenService() {
    }

    public String generateJwt(Token token){

        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expiration * 1000);
        final LocalDateTime expirationDate2 = LocalDateTime.now().plusSeconds(expiration);
        
        int loginLogId = createLoginLog(token.getUserId(), token.getUserName(), expirationDate2);

        return Jwts.builder()
                .setSubject(token.getSub() + "")
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .claim("email", token.getEmail())
                .claim("userType", token.getUserType())
                .claim("userName", token.getUserName())
                .claim("id", token.getUserId())
                .claim("loginLogId", loginLogId)
//                .claim("roles", token.getRoles())
//                .claim("branches", token.getBranches())
                 .claim("group", token.getGroup())
                .claim("acl", token.getAcl())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

    }
    
    public int createLoginLog(int userId, String userName, LocalDateTime expirationDate){
        
        OtpLog o = new OtpLog();
        o.setLinkId(userId);
        o.setLinkType(LinkType.USER);
        o.setContact("");
        o.setOtp("");
        o.setDescription("Login - Entity User");
        o.setEndDate(expirationDate);
        o.setClosed(0);
        o.setModifiedBy(userId);
        o.setModifiedDate(LocalDateTime.now());
        o = otpLogRepository.save(o);
        
        return o.getId();
    }
    
    public OtpLog getLoginLog(int id){
        
        Optional<OtpLog> ool = otpLogRepository.findById(id);
        
        if(!ool.isPresent()){
            throw new UnauthorizedException("unauthorized");
        }
        
        return ool.get();
    }
    
    public void closeLoginLog(int id){
        
        OtpLog ol = getLoginLog(id);
        ol.setClosed(1);
        otpLogRepository.save(ol);
    }

    public Token verifyJwt(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        

        Token tokenBody = new Token();
        tokenBody.setSub(claims.getSubject());
        tokenBody.setEmail((String) claims.get("email"));
        tokenBody.setUserId((int) claims.get("id"));
        tokenBody.setLoginLogId((int) claims.get("loginLogId"));
        tokenBody.setUserName((String) claims.get("userName"));
        tokenBody.setUserType(claims.get("userType").toString());
        tokenBody.setAcl(claims.get("acl").toString());
        if(claims.get("group") != null){
            tokenBody.setGroup(claims.get("group").toString());
        }
//        tokenBody.setRoles((String) claims.get("roles"));
//        tokenBody.setBranches((String) claims.get("branches"));


        OtpLog ol = getLoginLog(tokenBody.getLoginLogId());
        if(ol.getClosed() == 1){
            throw new UnauthorizedException("unauthorized");
        }
                
        return tokenBody;
    }

    public Token verifyJwt(HttpServletRequest request){
        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header = request.getHeader(getHeader());
        // 2. validate the header and check the prefix
        if(header == null || !header.startsWith(getPrefix())) {
            throw new ApiException("invalid header");
        }

        // 3. Get the token
        String token = header.replace(getPrefix(), "");

        return verifyJwt(token);
    }
    
    
    public boolean verifyApiToken(String appId, String secret, String data, String token) {
        //token 
        String[] part = token.trim().split("\\|\\|");
        if (part.length != 2) {
            return false;
        }
        String encryptedHash = null;
        String encryptedSessionKey = null;

        encryptedHash = part[0];
        encryptedSessionKey = part[1];

        try {

            String sessionKey = Encryptor.decryptAES(encryptedSessionKey, secret);
//            if (data != null) {
//                String hash = PasswordUtil.hash2(data);
//                String clientHash = Encryptor.decryptAES(encryptedHash, sessionKey);
//                if (clientHash == null) {
//                    return false;
//                }
//                if (!hash.equalsIgnoreCase(clientHash)) {
//                    return false;
//                }
//            }else{
                //check app id
                String clientAppId = Encryptor.decryptAES(encryptedHash, sessionKey);
                
                if(clientAppId.equalsIgnoreCase(appId)){
                    return true;
                }
                
                return false;
//            }

        } catch (Exception ex) {
            return false;
        }

    }

    public String getToken(HttpServletRequest request){
        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header = request.getHeader(getHeader());
        // 2. validate the header and check the prefix
        if(header == null || !header.startsWith(getPrefix())) {
            throw new ApiException("invalid header");
        }

        // 3. Get the token
        String token = header.replace(getPrefix(), "");

        return token;
    }

    public String generateOpaque(String userId){
        SecureRandom random = new SecureRandom();
        long longToken1 = Math.abs( random.nextLong() );
        long longToken2 = Math.abs( random.nextLong() );
        String randomString = Long.toString( longToken1, 30 ) + Long.toString( longToken2, 30 ) + userId;
        return randomString;
    }

    
    public String getClientId(HttpServletRequest request){
        String header = request.getHeader(getClientId());
        return header;
    }

    public String getPostData(HttpServletRequest request){
        Map<String, String[]> data = request.getParameterMap();
        Map<String, String> data2 = new HashMap<String, String>();
        
        for (Map.Entry<String,String[]> entry : data.entrySet()){
            String key = entry.getKey();
            String[] value = entry.getValue();
            data2.put(key, value[0]);
        }
        
        if(data2.size() == 0){
            return null;
        }
        
        String jsonData = JsonUtil.toString(data2);
        String method = request.getMethod();
        return jsonData;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSecret() {
        return secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public String getClientId() {
        return clientId;
    }
}
