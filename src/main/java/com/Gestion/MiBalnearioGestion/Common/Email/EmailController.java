package com.Gestion.MiBalnearioGestion.Common.Email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final JavaMailSender mailSender;

    @RequestMapping("/send-email")
    public ResponseEntity<String> sendEmail (){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo("balnearioapiutn@gmail.com");
        mailMessage.setSubject("Bienvenido al balnearioapiutn");
        mailMessage.setText("Bienvenido al balnearioapiutn");

        mailSender.send(mailMessage);
        return ResponseEntity.ok("Email enviado!");
    }

}
