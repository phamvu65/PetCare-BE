package vn.vuxnye.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailService {

    @Value("${spring.sendgrid.from-email}")
    private String from;

    @Value("${spring.sendgrid.templateId}")
    private String templateId;

    @Value("${spring.sendgrid.verificationLink}")
    private String verificationLink;

    private final SendGrid sendGrid;

    /**
     * Send email by SendGrid
     * @param to
     * @param subject
     * @param text
     */

    public void send(String to, String subject, String text)  {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(fromEmail, subject,toEmail, content);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                log.info("Email sent successfully");
            }else  {
                log.error("Email sent failed");
            }
        } catch (IOException e) {
            log.error("Error occured while sending email, error:{}", e.getMessage());
        }
    }

    /**
     * Email verification by SendGrid
     * @param to
     * @param name
     */
    public void emailValidation(String to, String name) throws IOException {
        log.info("Email verification started");

        Email fromEmail = new Email(from,"Vũ Ca");
        Email toEmail = new Email(to);

        String subject = "Xác thực tài khoản";

        String secretCode = String.format("?secretCode=%s", UUID.randomUUID());

        //TODO generate secretCode and save to database

        //Định nghĩa template
        Map<String, String> map = new HashMap<>();
        map.put("name",name);
        map.put("verificationLink", verificationLink + secretCode);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);


        // Add to dynamic data
        map.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            log.info("SendGrid response: {}", response.getBody());
            log.info("Status: {}", response.getStatusCode());
            if(response.getStatusCode()== HttpStatus.ACCEPTED.value()){
                log.info("verification sent successfully");
            }
            else{
                log.error("verification sent failed");
            }

    }
}
