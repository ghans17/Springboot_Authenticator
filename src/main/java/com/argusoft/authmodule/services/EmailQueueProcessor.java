package com.argusoft.authmodule.services;


import com.argusoft.authmodule.entities.EmailQueue;
import com.argusoft.authmodule.repositories.EmailQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailQueueProcessor {

    @Autowired
    private EmailQueueRepository emailQueueRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Runs every 5 seconds to process unprocessed emails
    @Scheduled(fixedDelay = 5000)
    public void processEmailQueue() {
        System.out.println("for debugging");
        List<EmailQueue> emailsToProcess = emailQueueRepository.findByIsProcessedFalse();

        for (EmailQueue email : emailsToProcess) {
            try {
                System.out.println("for debugging");
                sendEmailMessage(email);

                // Mark email as processed after sending
                email.setProcessed(true);
                emailQueueRepository.save(email);

            } catch (Exception e) {
                // Log error if sending failed
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }
    }

    private void sendEmailMessage(EmailQueue email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getToEmail());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());

        mailSender.send(message); // Send email
    }
}
