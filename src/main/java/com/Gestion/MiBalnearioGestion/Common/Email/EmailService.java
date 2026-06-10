package com.Gestion.MiBalnearioGestion.Common.Email;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void BienvenidaCliente (ClienteRequest cliente){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(cliente.getEmail());
        mailMessage.setSubject("Bienvenido al Balneario " + cliente.getNombre());
        mailMessage.setText("Te damos la bienvenida y confirmamos la creación de tu cuenta!");
        mailSender.send(mailMessage);
    }

    /**
     * Con Empleado Request eventualmente

    public void BienvenidaEmpleado (EmpleadoRequest empleado){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(empleado.getEmail());
        mailMessage.setSubject("Bienvenido al Equipo " + empleado.getNombre());
        mailMessage.setText("Te damos la bienvenida y confirmamos la creación de tu cuenta!);
        mailSender.send(mailMessage);
    }*/
}
