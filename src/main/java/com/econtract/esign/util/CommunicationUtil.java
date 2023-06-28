package com.econtract.esign.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.econtract.esign.exception.ApiException;
import com.econtract.esign.model.dto.ShortenUrlResponseDto;
import com.econtract.esign.service.LoggerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

@Service
public class CommunicationUtil {


    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;

    @Value("${amazon.aws.bucketName}")
    private String amazonAWSBucketName;
    
    @Value("${sms.type}")
    private String smsType;
    
    @Value("${sms.url}")
    private String smsurl;
    
    @Value("${sms.soap.url}")
    private String smsSoapUrl;
    
    
    @Value("${sms.soap.username}")
    private String smsSoapUsername;
    
    
    @Value("${sms.soap.password}")
    private String smsSoapPassword;
    
    
    @Value("${mail.type}")
    private String mailType;
    
    @Value("${mail.url}")
    private String mailurl;
    
    @Value("${mail.apikey}")
    private String mailApiKey;
    
    @Value("${spring.mail.username}")
    private String mailFrom;
    
    @Autowired
    private JavaMailSender javaMailSender;

    
    @Value("${shorten.url}")
    private String shortenUrl;
    
    @Value("${shorten.user}")
    private String shortenUser;
    
    @Value("${shorten.password}")
    private String shortenPassword;
    
    @Value("${shorten.expiryDay}")
    private String shortenExpiryDay;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private LoggerService loggerService;
    
    public CommunicationUtil() {
    }

    public AWSCredentials getAWSCredentials(){
        return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
    }
    
    @Async
    public void sendSms(String phoneNumber, String message){
        try{
            if(smsType.equalsIgnoreCase("rest")){
                sendSmsRest(phoneNumber, message);
            }else if(smsType.equalsIgnoreCase("soap")){
                sendSmsSoap(phoneNumber, message);
            }else if(smsType.equalsIgnoreCase("aws")){
                sendSmsAws(phoneNumber, message);
            }
            
        }catch(Exception ex){
            System.out.println("SMS sending error:: " + ex.getMessage());
        }
    }
    
    @Async
    public void sendMail(String to, String subject, String textBody, String htmlBody){
        try{
            if(mailType.equalsIgnoreCase("smtp")){
                sendMailSmtp(to, subject, textBody, htmlBody);
            }else if(mailType.equalsIgnoreCase("rest")){
                sendMailRest(to, subject, textBody, htmlBody);
            }else if(mailType.equalsIgnoreCase("aws")){
                sendMailAws(to, subject, textBody, htmlBody);
            }
        }catch(Exception ex){
            System.out.println("Mail sending error:: " + ex.getMessage());
        }
    }
    
    public void sendSmsSoap(String phoneNumber, String message){
        try{
            
            String url = smsSoapUrl;
            String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                            "  <soap12:Body>\n" +
                            "    <SendSMS xmlns=\"https://www.birlasunlife.com/webservice\">\n" +
                            "      <UserID>"+smsSoapUsername+"</UserID>\n" +
                            "      <Password>"+smsSoapPassword+"</Password>\n" +
                            "      <MobileNo>"+phoneNumber+"</MobileNo>\n" +
                            "      <SMSText>"+message+"</SMSText>\n" +
                            "    </SendSMS>\n" +
                            "  </soap12:Body>\n" +
                            "</soap12:Envelope>";
            
            
            loggerService.logApi("sms: " + url);
            loggerService.logApi("sms body: " + body);
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/soap+xml"));
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            String result = rt.postForObject(url, entity, String.class);
            loggerService.logApi("sms success: " + result);
        }catch(Exception ex){
            loggerService.logApi("sms error: " + ex.getMessage());
        }
    }
    
    public void sendSmsRest(String phoneNumber, String message){
        try{
            
            String url = smsurl.replace("{message}", message);
            url = url.replace("{phone}", phoneNumber);
            
            
            loggerService.logApi("sms: " + url);
            RestTemplate rt = new RestTemplate();
            String result = rt.getForObject(url, String.class);
            loggerService.logApi("sms success: " + result);
        }catch(Exception ex){
            loggerService.logApi("sms error: " + ex.getMessage());
        }
    }

    public void sendSmsAws(String phoneNumber, String message){
        try{
            AmazonSNSClient snsClient = new AmazonSNSClient(getAWSCredentials());
            snsClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
            Map<String, MessageAttributeValue> smsAttributes =
                    new HashMap<String, MessageAttributeValue>();
            smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                    .withStringValue("EVEINFO") //The sender ID shown on the device.
                    .withDataType("String"));
//        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
//                .withStringValue("Promotional") //Sets the type to promotional.
//                .withDataType("String"));

            PublishResult result = snsClient.publish(new PublishRequest()
                    .withMessage(message)
                    .withPhoneNumber(phoneNumber)
                    .withMessageAttributes(smsAttributes));
            System.out.println("SMS sent to" + phoneNumber);
        }catch (Exception ex){
            System.out.println("The SMS was not sent to "+phoneNumber+". Error message: "
                    + ex.getMessage());
        }
    }

    public void sendMailAws(String to, String subject, String textBody, String htmlBody){
        try {
            Body body =  null;
            if(textBody !=null && !textBody.isEmpty()){
                body = new Body()
                        .withText(new Content()
                                .withCharset("UTF-8").withData(textBody));
            }else if(htmlBody != null && !htmlBody.isEmpty()){
                body = new Body()
                        .withHtml(new Content()
                                .withCharset("UTF-8").withData(htmlBody));
            }else{
                throw new Exception();
            }


            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            .withCredentials(new AWSCredentialsProvider() {
                                @Override
                                public AWSCredentials getCredentials() {
                                    return getAWSCredentials();
                                }

                                @Override
                                public void refresh() {

                                }
                            })
                            // Amazon SES.
                            .withRegion(Regions.EU_WEST_1).build();
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(to))
                    .withMessage(new Message()
                            .withBody(body)
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(subject)))
                    .withSource("evemobileapplication@gmail.com")
                    // Comment or remove the next line if you are not using a
                    // configuration set
                    //.withConfigurationSetName(CONFIGSET)
                    ;
            client.sendEmail(request);
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: "
                    + ex.getMessage());
        }
    }

    public void sendMailSmtp(String to, String subject, String textBody, String htmlBody){
        try{
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg);

            helper.setTo(to);
            helper.setFrom(mailFrom);
            
            helper.setSubject(subject);
            if(!textBody.isEmpty()){
                helper.setText(textBody);
            }
            if(!htmlBody.isEmpty()){
                helper.setText(htmlBody, true);
            }

            loggerService.logApi("mail smtp: [to=" + to + ", subject=" + subject + "]");
            javaMailSender.send(msg);
        }catch(Exception ex){
            loggerService.logApi("mail error: " + ex.getMessage());
        }
    }

    public void sendMailRest(String to, String subject, String textBody, String htmlBody){
        try{
            String body = htmlBody;
            if(!textBody.isEmpty()){
                body = textBody;
            }
            body = body.replace('"', '\'').replaceAll("\n", "").replaceAll("\t", "");
            
            String url = mailurl;
            
            String requestJson =  "{\"personalizations\":[{"
                    + "\"recipient\":\""+to+"\"}],"
                    + "\"from\":{\"fromEmail\":\" info@fl.adityabirlacapital.org\",\"fromName\":\"Aditya Birla Capital\"},"
                    + "\"subject\":\""+subject+"\",\"content\":\""+ body +"\"}";
            
            loggerService.logApi("mail rest: " + requestJson);
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("api_key", mailApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(requestJson, headers);
            String result = rt.postForObject(url, request,String.class);
            loggerService.logApi("mail success: " + result);
            
        }catch(Exception ex){
            loggerService.logApi("mail error: " + ex.getMessage());
        }
    }
    
    public String shortUrl(String longUrl){
        String shorted = longUrl;
        try{
            String url = shortenUrl;
            RestTemplate rt = new RestTemplate();
            rt = restTemplate;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestJson =  "{\n" +
            "	\"objRequest\": {\n" +
            "		\"UserId\": \""+shortenUser+"\",\n" +
            "		\"Password\" : \""+shortenPassword+"\",\n" +
            "		\"LongURL\" : \""+longUrl+"\",\n" +
            "		\"ExpiryDay\" : "+shortenExpiryDay+"\n" +
            "	}\n" +
            "}";
            HttpEntity<String> request = new HttpEntity<String>(requestJson, headers);
            ShortenUrlResponseDto result = rt.postForObject(url, request,ShortenUrlResponseDto.class);
            if(result.getReturnCode().equalsIgnoreCase("1")){
                shorted = result.getUrlReturned();
            }else{
                System.out.println("short url error:: " + result.getReturMessage());
            }
        }catch(Exception ex){
            System.out.println("short url error:: " + ex.getMessage());
        }
        
        return shorted;
    }

    
    public String uploadFile(MultipartFile multipartFile){
        try{
            if(multipartFile == null || multipartFile.isEmpty()){
                return "";
            }

            String fileName = new Date().getTime() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");
            AmazonS3Client client = new AmazonS3Client(getAWSCredentials()).withRegion(Regions.EU_WEST_1);
            File file = convertMultiPartToFile(multipartFile);
            client.putObject(new PutObjectRequest(amazonAWSBucketName, fileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return fileName;
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            return "";
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile(file.getOriginalFilename(), "");
        System.out.println("AbsolutePath:" + convFile.getAbsolutePath());
        System.out.println("Path:" + convFile.getPath());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
