package com.authModule.authmodule.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.authModule.authmodule.entities.EmailQueue;
import com.authModule.authmodule.entities.EmailTemplate;
import com.authModule.authmodule.repositories.EmailQueueRepository;
import com.authModule.authmodule.repositories.EmailTemplateRepository;

import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private EmailQueueRepository emailQueueRepository;


    public void queueEmail(String to, String templateName, Map<String, String> placeholders) {
        // Retrieve the template
        EmailTemplate template = emailTemplateRepository.findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));

        // Populate template with placeholders
        String subject = template.getSubject();
        String body = template.getBody();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            body = body.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        String cc=template.getCc();
        String bcc=template.getBcc();
        String attachments=template.getAttachments();
        queueEmailMessage(to,cc,bcc, subject, body,attachments);

    }


    //saving in email queue
    public void queueEmailMessage(String to, String cc, String bcc, String subject, String body, String attachments) {
        EmailQueue emailQueue = new EmailQueue();
        emailQueue.setToEmail(to);
        emailQueue.setCc(cc);
        emailQueue.setBcc(bcc);
        emailQueue.setSubject(subject);
        emailQueue.setBody(body);
        emailQueue.setAttachments(attachments);

        emailQueueRepository.save(emailQueue);
    }

}
