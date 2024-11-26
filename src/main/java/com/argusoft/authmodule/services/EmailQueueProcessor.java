package com.argusoft.authmodule.services;


import com.argusoft.authmodule.entities.EmailHistory;
import com.argusoft.authmodule.entities.EmailQueue;
import com.argusoft.authmodule.repositories.EmailHistoryRepository;
import com.argusoft.authmodule.repositories.EmailQueueRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmailQueueProcessor {

    @Autowired
    private EmailQueueRepository emailQueueRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailHistoryRepository emailHistoryRepository;

    // Runs every 5 seconds to process unprocessed emails
    @Scheduled(fixedDelay = 5000)
    public void processEmailQueue() {
        System.out.println("for debugging");
        List<EmailQueue> emailsToProcess = emailQueueRepository.findByIsProcessedFalse();

        for (EmailQueue email : emailsToProcess) {
            try {
                // Generate a transaction ID
                String transactionId = UUID.randomUUID().toString();

                // Send the email
                sendEmailMessage(email, transactionId);

                // Save email history
                saveEmailHistory(email, transactionId);

                // Mark email as processed after sending
                email.setProcessed(true);
                emailQueueRepository.save(email);

            } catch (Exception e) {
                // Log error if sending failed
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }
    }


    private void sendEmailMessage(EmailQueue email, String transactionId) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email.getToEmail());
        helper.setSubject(email.getSubject());

        helper.setText(email.getBody(), true); // true for HTML content

        // Add CC and BCC (if any)
        if (email.getCc() != null) {
            helper.setCc(email.getCc().split(","));
        }
        if (email.getBcc() != null) {
            helper.setBcc(email.getBcc().split(","));
        }

        // Add attachments (if any)
        if (email.getAttachments() != null) {
            for (String filePath : email.getAttachments().split(",")) {
                FileSystemResource file = new FileSystemResource(new File(filePath));
                helper.addAttachment(file.getFilename(), file);
            }
        }

        mailSender.send(mimeMessage); // Send email
    }



    private void saveEmailHistory(EmailQueue email, String transactionId) {
        EmailHistory emailHistory = new EmailHistory();
        emailHistory.setTransactionId(transactionId);
        emailHistory.setToEmail(email.getToEmail());
        emailHistory.setFromEmail("email@domain.com"); //actual mail of the config file
        emailHistory.setCc(email.getCc());
        emailHistory.setBcc(email.getBcc());
        emailHistory.setSubject(email.getSubject());
        emailHistory.setBody(email.getBody());
        emailHistory.setAttachments(email.getAttachments());
        emailHistory.setSentAt(LocalDateTime.now());

        emailHistoryRepository.save(emailHistory);
    }
}






//String Emailcontent=createcontent(email.getBody());
//private String createcontent(String body) {
//        return """
//                <html>
//                </html>
//                """;
//    }