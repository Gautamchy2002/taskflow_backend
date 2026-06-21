//package com.taskmanager.task.task.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender javaMailSender;
//    @Async
//    public void sendEmail(String to, String subject, String body) {
//        try{
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(body);
//
//            javaMailSender.send(message);
//            System.out.println("Email sent successfully to : " + to);
//        } catch (Exception e) {
//            System.out.println("Email sending failed : " + e.getMessage());
//            e.printStackTrace();
//        }
//
//    }
//}



package com.taskmanager.task.task.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final WebClient webClient;

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${BREVO_SENDER_EMAIL}")
    private String senderEmail;

    @Value("${BREVO_SENDER_NAME:TaskFlow}")
    private String senderName;

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of(
                            "name", senderName,
                            "email", senderEmail
                    ),
                    "to", List.of(
                            Map.of("email", to)
                    ),
                    "subject", subject,
                    "textContent", body
            );

//            webClient.post()
//                    .uri("/smtp/email")
//                    .header("api-key", brevoApiKey)
//                    .header("Content-Type", "application/json")
//                    .bodyValue(payload)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .doOnSuccess(response ->
//                            System.out.println("Email sent successfully to: " + to)
//                    )
//                    .doOnError(error ->
//                            System.out.println("Email sending failed: " + error.getMessage())
//                    )
//                    .subscribe();
            String responseBody = webClient.post()
                    .uri("/smtp/email")
                    .header("api-key", brevoApiKey)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .exchangeToMono(response ->
                            response.bodyToMono(String.class)
                                    .map(responseText -> {
                                        System.out.println("Brevo status: " + response.statusCode());
                                        System.out.println("Brevo response: " + responseText);
                                        return responseText;
                                    })
                    )
                    .block();

            System.out.println("Brevo final response: " + responseBody);

        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }
    }
}
