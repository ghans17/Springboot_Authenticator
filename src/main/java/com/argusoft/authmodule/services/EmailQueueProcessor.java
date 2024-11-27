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
                e.printStackTrace();
            }
        }
    }


    private void sendEmailMessage(EmailQueue email, String transactionId) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email.getToEmail());
        helper.setSubject(email.getSubject());


        // Construct the email body with HTML and CSS styling
        String htmlBody = """
    <html>
        <head>
            <style>
                /* General styling for the email body */
                body {
                    font-family: 'Arial', sans-serif;
                    line-height: 1.8;
                    background-color: #f9f9f9;
                    color: #333333;
                    margin: 0;
                    padding: 20px;
                }
    
                /* Container to center the email and add padding */
                .email-container {
                    max-width: 600px;
                    margin: 0 auto;
                    background-color: #ffffff;
                    border: 1px solid #dddddd;
                    border-radius: 8px;
                    box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
                    overflow: hidden;
                }

                /* Header section */
                .header {
                    background-color: #007BFF;
                    color: #ffffff;
                    padding: 15px 20px;
                    text-align: center;
                    font-size: 20px;
                    font-weight: bold;
                }

                /* Main content section */
                .content {
                    padding: 20px;
                }

                .content p {
                    font-size: 16px;
                    color: #555555;
                    margin-bottom: 15px;
                }

                /* Footer section */
                .footer {
                    background-color: #f1f1f1;
                    padding: 10px 20px;
                    text-align: center;
                    font-size: 12px;
                    color: #888888;
                }
                .footer {
                    border-top: 1px solid #dddddd;
                    margin-top: 20px;
                }

                /*For Links inside the email */
                a {
                    color: #007BFF;
                    text-decoration: none;
                }

                a:hover {
                    text-decoration: underline;
                }
            </style>
        </head>
        <body>
            <div class="email-container">
                <!-- Email Header -->
                <div class="header">
                    Important Notification
                </div>

                <!-- Email Content -->
                <div class="content">
                    <p>%s</p>
                </div>

                <!-- Email Footer -->
                <div class="footer">
                    This is an automated email. Please do not reply.<br />
                    <a href="https://abc.com/unsubscribe">Unsubscribe</a>
                </div>
            </div>
        </body>
    </html>
    """.formatted(email.getBody());

        helper.setText(email.getBody(), htmlBody);

        //Add cc,bcc if any
        if (email.getCc() != null && !email.getCc().isEmpty()) {
            helper.setCc(email.getCc().split(","));
        }
        if (email.getBcc() != null && !email.getBcc().isEmpty()) {
            helper.setBcc(email.getBcc().split(","));
        }

        // Add attachments (if any)
        if (email.getAttachments() != null && !email.getAttachments().isEmpty()) {
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
        emailHistory.setFromEmail("abc@gmail.com"); //actual mail of the config file
        emailHistory.setCc(email.getCc());
        emailHistory.setBcc(email.getBcc());
        emailHistory.setSubject(email.getSubject());
        emailHistory.setBody(email.getBody());
        emailHistory.setAttachments(email.getAttachments());
        emailHistory.setSentAt(LocalDateTime.now());

        emailHistoryRepository.save(emailHistory);
    }
}



