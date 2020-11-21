package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.model.contactus.ContactUsRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
@RequiredArgsConstructor
public class ContactUsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsService.class);
    private final EmailService emailService;
    
    public void senContactUsEmail(final ContactUsRequest contactUsService) {
        try {
            emailService.sendEmailToAdminFromContactUsForm(contactUsService);
        } catch (MessagingException e) {
            LOGGER.error("Email Contact us form failed to be sent", e);
        }
    }
}
