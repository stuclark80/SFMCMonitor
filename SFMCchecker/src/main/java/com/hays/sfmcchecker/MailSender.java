/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hays.sfmcchecker;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailSender {

    private static final Logger logger = LogManager.getLogger(MailSender.class);

    private static Config config = new Config();
    private static final String to = config.getProperty("RECIPIENT_TO");
    private static final String cc = config.getProperty("RECIPIENT_CC");
    private static final String from = config.getProperty("RECIPIENT_FROM");
    private static final String SMTP_RELAY = config.getProperty("SMTP_RELAY");
    private static final String SMTP_PORT = config.getProperty("SMTP_PORT");
    private static final String SUBJECT_REGION = config.getProperty("SUBJECT_REGION");

    public static void sendMail(String html, String date, String type) {

        logger.info("**STARTING MAILER**");

        logger.info("MAIL FROM: "+from);
        logger.info("MAIL TO: "+to);
        logger.info("MAIL CC: "+cc);


        // Mention the Recipient's email address
        //String from = "filemonitor@sfm-hays.com";

        // Mention the Sender's email address
       // String to = "stuart.clark-davies@hays.com";
       // String cc = "prashant.gangwar@hays.com,Elliot.Green@hays.com";

        

       
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", SMTP_RELAY);
        properties.put("mail.smtp.port", SMTP_PORT);
        
        logger.info("USING: "+SMTP_RELAY+" - PORT: "+SMTP_PORT);

        // Get the Session object and pass username and password
        Session session = Session.getInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            //message.addRecipient(Message.RecipientType.CC, new InternetAddress("prashant.gangwar@hays.com"));
            //message.addRecipient(Message.RecipientType.CC, new InternetAddress("Elliot.Green@hays.com"));
            if (cc.length() > 1) {
                message.addRecipients(Message.RecipientType.CC,InternetAddress.parse(cc));
            }
            // Set Subject: header field
            message.setSubject("SFMC "+type+" MONITOR: "+date+" - "+ SUBJECT_REGION);
            message.setContent(html, "text/html; charset=utf-8");

            // Now set the actual message
            //message.setText(html);
            logger.info("SENDING MAIL ---> ");
            //System.out.println("sending...");
            // Send message
            Transport.send(message);
            logger.info("MAIL SENT");
        } catch (MessagingException mex) {
            //mex.printStackTrace();
            
            logger.error("{}", mex);

        }

    }

}
