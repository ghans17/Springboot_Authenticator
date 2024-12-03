package com.authModule.authmodule.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;


    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String toPhoneNumber, String message) {
        Message.creator(
                new com.twilio.type.PhoneNumber(toPhoneNumber),  // To
                new com.twilio.type.PhoneNumber(fromPhoneNumber), // From
                message                                            // Message
        ).create();
    }
}

