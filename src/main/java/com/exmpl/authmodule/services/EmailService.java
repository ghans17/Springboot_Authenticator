package com.exmpl.authmodule.services;

import com.exmpl.authmodule.entities.EmailTemplate;
import com.exmpl.authmodule.repositories.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    // Assuming an SMTP mail sender is configured
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String templateName, Map<String, String> placeholders) {
        // Retrieve the template
        EmailTemplate template = emailTemplateRepository.findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));

        // Populate template with placeholders
        String subject = template.getSubject();
        String body = template.getBody();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            body = body.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        // Send the email
        sendEmailMessage(to, subject, body);
    }

    private void sendEmailMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
