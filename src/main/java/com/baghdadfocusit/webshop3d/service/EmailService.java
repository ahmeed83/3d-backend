package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Order;
import com.baghdadfocusit.webshop3d.entities.Product;
import com.baghdadfocusit.webshop3d.model.contactus.ContactUsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Value("${app.email-contactus}")
    private String contactUsEmail;
    
    @Value("${app.email-employee}")
    private String emailEmployee;

    @Value("${app.email-order}")
    private String orderEmail;

    private final JavaMailSender javaMailSender;

    public EmailService(final JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    void sendEmailToAdminWithOrder(final Order order, final Set<Product> products) throws MessagingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        if (order.getEmail() == null) {
            helper.setTo(orderEmail + "," + emailEmployee);
        } else {
            helper.setTo(InternetAddress.parse(orderEmail + "," + emailEmployee + "," + order.getEmail()));
        }
        helper.setSubject("3D Order id: " + order.getOrderTrackId());
        List<Product> productList = products.stream()
                .map(prod -> Product.builder().name(prod.getName()).price(prod.getPrice()).build())
                .collect(Collectors.toList());

        StringBuilder productSB = new StringBuilder();
        productList.forEach(product -> productSB.append("[ ")
                .append(product.getName())
                .append(": ")
                .append(product.getPrice())
                .append(" ] "));
        String emailContent = "<html>\n"
                + "<body>\n<h3>طلبية البضاعة </h3>\n<p>Customer name: " + order.getName()
                + " </p>\n<p>District: " + order.getDistrict()
                + " </p>\n<p>Additional District: " + order.getDistrict2()
                + " </p>\n<p>Mobile Number: " + order.getMobileNumber()
                + " </p>\n<p>Email: " + order.getEmail()
                + " </p>\n<p>Company Name: " + order.getCompanyName()
                + " </p>\n<p>City: " + order.getCity()
                + " </p>\n<p>Products: " + productSB
                + " </p>\n<p>Total price: " + order.getTotalAmount()
                + " </p>\n<p>Customer note: " + order.getNotes()
                + " </p>\n</body>\n</html>";
        helper.setText(emailContent, true);
        javaMailSender.send(msg);
    }

    @Async
    void sendEmailToAdminFromContactUsForm(final ContactUsRequest contactUsRequest) throws MessagingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        helper.setTo(InternetAddress.parse(contactUsEmail
                                                   + ","
                                                   + emailEmployee
                                                   + "," 
                                                   + contactUsRequest.getSenderEmail()));
        helper.setSubject("Contact us from: " + contactUsRequest.getSenderName());
        String emailContent = "<html>\n<body>\n"
                        + "\n<h3>Customer name: " + contactUsRequest.getSenderName()
                        + "</h3>\n<h3>Customer email: " + contactUsRequest.getSenderEmail() 
                        + "</h3>\n<h3>Customer mobile number: " + contactUsRequest.getSenderMobile()
                        + "</h3>\n<p>Message: " + contactUsRequest.getMessageContent() 
                        + " </p>\n</body>\n</html>";
        helper.setText(emailContent, true);
        javaMailSender.send(msg);
    }
}
