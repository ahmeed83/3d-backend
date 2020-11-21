package com.baghdadfocusit.webshop3d.model.contactus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactUsRequest {

    @NotNull
    private String senderName;
    @NotNull
    private String senderEmail;
    @NotNull
    private String senderMobile;
    @NotNull
    private String messageContent;
}
