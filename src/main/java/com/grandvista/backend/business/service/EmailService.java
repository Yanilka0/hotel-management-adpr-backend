package com.grandvista.backend.business.service;

import com.grandvista.backend.config.ConfigLoader;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private final String username;
    private final String password;
    private final Properties props;

    public EmailService() {
        this.username = ConfigLoader.getProperty("spring.mail.username");
        this.password = ConfigLoader.getProperty("spring.mail.password");

        this.props = new Properties();
        props.put("mail.smtp.auth", ConfigLoader.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable",
                ConfigLoader.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.host", ConfigLoader.getProperty("spring.mail.host", "smtp.gmail.com"));
        props.put("mail.smtp.port", ConfigLoader.getProperty("spring.mail.port", "587"));
    }

    public void sendEmail(String to, String subject, String body) {
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
