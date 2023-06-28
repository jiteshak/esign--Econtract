package com.econtract.esign.service;

import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.Task;
import com.econtract.esign.model.Template;
import com.econtract.esign.model.constant.EsignRequestEmail;
import com.econtract.esign.model.constant.TemplateType;
import com.econtract.esign.util.CommunicationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resetPassword.link}")
    private String resetPasswordLink;
    
    
    @Value("${base.app.url.customer}")
    private String customerUrl;

    @Autowired
    CommunicationUtil communicationUtil;
    
    @Autowired
    TemplateService templateService;

    public EmailService() {
    }

    public void sendForgotPasswordMail(String to, String userName, String token){
        String subject = "EC : Forgot Password?";
        String htmlBody = "Hi "+userName+",<br/>" +
                "<br/>" +
                "We received a request to reset your account password.<br/>" +
                "<br/>" +
                "<a href='"+resetPasswordLink.replaceAll("token", token)+"'>Click here</a> to change your password.<br/>" +
                "<br/>" +
                "Didn't request this change?<br/>" +
                "If you didn't request a new password, let us know.<br/>" +
                "<br/>" +
                "Regards,<br/>" +
                "EC Team";
        communicationUtil.sendMail(to, subject, "", htmlBody);

    }
    
    public void sendNewUserMail(String to, String name, String userName, String password){
        String subject = "EC : Welcom to ABFL";
        String htmlBody = "Hi "+name+",<br/>" +
                "<br/>" +
                "The entire team of ABFL is thrilled to welcome you on board. We hope you'll do some amazing works here!<br/>" +
                "Please find your username and passwrod below:<br/>" +
                "Username: "+ userName +"<br/>" +
                "Password: "+ password +"<br/>" +
                "<br/>" +
                "If you didn't request for this, let us know.<br/>" +
                "<br/>" +
                "Regards,<br/>" +
                "EC Team<br/><br/>";
        communicationUtil.sendMail(to, subject, "", htmlBody);

    }
    
    @Async
    public void sendAgreement(EsignRequestSignee ers){
        String url = customerUrl.replace("{token}", ers.getToken());
        url = communicationUtil.shortUrl(url);
        
        String text = "Thank you for choosing Aditya Birla Capital for your financial needs! We are happy to invite you to the online loan agreement for your recently availed loan facility. The same can also be signed digitally from the comfort of your house, at your convenience. Please click on the link below to access the agreement. Kindly note the link would be valid only for 7 days.";
        if(ers.getSequence() > 1){
            text = "Thank you for choosing Aditya Birla Capital for your financial needs! We are happy to invite you to the online loan agreement as a co-applicant for recently availed loan facility. The same can also be signed digitally from the comfort of your house, at your convenience. Please click on the link below to access the agreement. Kindly note the link would be valid only for 7 days.";
        }
        
        String htmlBody = EsignRequestEmail.text;
        htmlBody = htmlBody.replace("{{content}}", text);
        
        
        //get template
        int templateType = TemplateType.AGMNT_MAIL_APPLICANT;
        if(ers.getSequence() > 1){
            templateType = TemplateType.AGMNT_MAIL_COAPPLICANT;
        }
        Template t = templateService.getTemplate(ers.getEsignRequest().getModuleId(), ers.getEsignRequest().getClientType(), ers.getEsignRequest().getDocumentCategoryId(), templateType);
        if(t.getId() != null){
            htmlBody = t.getDocument();
        }
        
        String subject = "Sign Your Document";
        htmlBody = htmlBody.replace("{{applicantName}}", ers.getApplicantName());
        htmlBody = htmlBody.replace("{{tokenUrl}}", url);
        communicationUtil.sendMail(ers.getApplicantEmail(), subject, "", htmlBody);
    }
    
    public void sendTaskFailMail(Task task, String linkId,String to, String userName){
        String linkType = task.getLinkTypeString();
        
        
        String subject = "EC : Task Failed: " + linkId;
        String htmlBody = "Hi "+userName+",<br/>" +
                "<br/>" +
                "Please find below error details.<br/>" +
                "<br/>" +
                "Task Id: "+ task.getId() +"<br/>" +
                linkType + " Id :"+ linkId +"<br/>" +
                "Failed message: "+ task.getMessage() +"<br/>" +
                "<br/>" +
                "<br/>" +
                "Regards,<br/>" +
                "EC Team<br/><br/>";
        communicationUtil.sendMail(to, subject, "", htmlBody);

    }
    
    public void sendCustomerOtp(EsignRequestSignee ers, String otp){
       
        String htmlBody = "Dear user,<br/>"+"<br/>"+otp+" is your one time password to sign a document with the document ID: {{referenceNumber1}}. This OTP is valid for 15 mins only<."+"<br/>" +
                "<br/>" +
                "Regards,<br/>" +
                "EC Team<br/><br/>";
        htmlBody = htmlBody.replace("{{referenceNumber1}}", ers.getEsignRequest().getReferenceNumber1());
        String subject = "Verification otp";
        
      
        
        communicationUtil.sendMail(ers.getApplicantEmail(), subject, "", htmlBody);
    
    }

}
