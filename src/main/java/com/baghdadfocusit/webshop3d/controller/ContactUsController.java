package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.model.contactus.ContactUsRequest;
import com.baghdadfocusit.webshop3d.service.ContactUsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ContactUs controller.
 */
@RestController
@RequestMapping("resources/contact-us")
public class ContactUsController {

    private final ContactUsService contactUsService;

    public ContactUsController(ContactUsService contactUsService) {
        this.contactUsService = contactUsService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> senContactUsEmail(@RequestBody ContactUsRequest contactUsRequest) {
        contactUsService.senContactUsEmail(contactUsRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}