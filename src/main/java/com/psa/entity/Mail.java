package com.psa.entity;

import java.util.Date;
import java.util.List;

public class Mail {
    private String mailFrom;
 
    private String mailTo;
 
    private String mailSubject;
 
    private String mailContent;
 
    private String contentType;
 
    public Mail() {
        contentType = "text/html";
    }
 
    public String getContentType() {
        return contentType;
    }
 
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
 
    public String getMailFrom() {
        return mailFrom;
    }
 
    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }
 
    public String getMailSubject() {
        return mailSubject;
    }
 
    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }
 
    public String getMailTo() {
        return mailTo;
    }
 
    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }
 
    public Date getMailSendDate() {
        return new Date();
    }
 
    public String getMailContent() {
        return mailContent;
    }
 
    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public static String formatAlerts (List <Alert> vesselAlerts) {
        String mailContent = "<ul>";
        for (Alert alert: vesselAlerts) {
            mailContent += "<li>" + alert.toString() + "</li>";
        }
        mailContent += "</ul>";
        return mailContent;
    }

}
