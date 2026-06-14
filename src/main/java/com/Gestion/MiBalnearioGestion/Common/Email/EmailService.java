package com.Gestion.MiBalnearioGestion.Common.Email;

import com.Gestion.MiBalnearioGestion.Auth.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.RecursoMapper;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmpleadosRepositorio empleadosRepositorio;

    public void BienvenidaCliente (ClienteRequest cliente){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(cliente.getEmail());
        mailMessage.setSubject("Bienvenido al Balneario " + cliente.getNombre());
        mailMessage.setText("Te damos la bienvenida y confirmamos la creación de tu cuenta!");
        mailSender.send(mailMessage);
    }

    public void BienvenidaClienteRegistro (NewAccountRequest cliente){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(cliente.getEmail());
        mailMessage.setSubject("Bienvenido al Balneario " + cliente.getNombre());
        mailMessage.setText("Te damos la bienvenida y confirmamos la creación de tu cuenta!");
        mailSender.send(mailMessage);
    }

    public void BienvenidaEmpleado (EmpleadoDTO empleadoDTO){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(empleadoDTO.getEmail());
        mailMessage.setSubject("Bienvenido al Equipo del Balneario " + empleadoDTO.getNombre());
        mailMessage.setText("Te damos la bienvenida y confirmamos la creación de tu cuenta!");
        mailSender.send(mailMessage);
    }

    public void cancelacionReserva(ReservaEntity reserva){

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(reserva.getCliente().getEmail());
        mailMessage.setSubject("Reserva en Balneario de " + reserva.getCliente().getNombre());
        mailMessage.setText("Hola! " + reserva.getCliente().getNombre() + " detalles de la reserva a cancelar: "
                +"\n" + "Codigo de Reserva: " + reserva.getPublicId()
                +"\n" + "Estado: " + reserva.getEstadoReserva().toString()
                +"\n" + "Monto total: " +reserva.getMontoTotal()
                +"\n" + "Duracion: Empieza el  " + reserva.getFechaInicio() + " y termina el " + reserva.getFechaFin()
                +"\n" + "Por: " +reserva.getRecursos().stream().map(RecursoEntity::getNombre).toList().toString()
                +"\n" + " Esta reserva fue CANCELADA correctamente");
        mailSender.send(mailMessage);
    }

    public void confirmacionReserva(ReservaEntity reserva, String urlMercadoPago) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(reserva.getCliente().getEmail());
        mailMessage.setSubject("Reserva Pendiente - Balneario");

        mailMessage.setText("¡Hola, " + reserva.getCliente().getNombre() + "!\n\n"
                + "Tu reserva se ha generado correctamente en nuestro sistema y se encuentra en estado PENDIENTE.\n"
                + "IMPORTANTE: Tenés un límite de 15 minutos para completar el pago; de lo contrario, la reserva será cancelada automáticamente para liberar los recursos.\n\n"
                + "=========================================\n"
                + "          DETALLES DE LA RESERVA         \n"
                + "=========================================\n"
                + "Código de Reserva: " + reserva.getPublicId() + "\n"
                + "Período: Desde el " + reserva.getFechaInicio() + " hasta el " + reserva.getFechaFin() + "\n"
                + "Monto Total a Abonado: $" + reserva.getMontoTotal() + "\n"
                + "Recursos seleccionados: " + reserva.getRecursos().stream().map(RecursoEntity::getNombre).toList().toString() + "\n\n"
                + "=========================================\n"
                + "          ENLACE DE PAGO (MERCADO PAGO)   \n"
                + "=========================================\n"
                + "Hacé clic en el siguiente enlace para abonar de forma segura:\n" + urlMercadoPago + "\n\n"
                + "Muchas gracias por elegirnos.");

        mailSender.send(mailMessage);
    }


    public void confirmacionPagoReserva(PagoReservaEntity pagoReserva, TicketEntity ticket) {
        ReservaEntity reserva = pagoReserva.getReserva();
        var cliente = reserva.getCliente();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(cliente.getEmail());
        mailMessage.setSubject("¡Pago Confirmado! Tu reserva en Balneario está Lista");

        mailMessage.setText("¡Hola, " + cliente.getNombre() + "! Confirmamos que recibimos tu pago correctamente.\n\n"
                + "=========================================\n"
                + "           DETALLES DEL TICKET           \n"
                + "=========================================\n"
                + "ID de Pago/Ticket: " + ticket.getId() + "\n"
                + "Fecha de Pago: " + ticket.getFechaTicket() + "\n"
                + "Monto Abonado: $" + ticket.getTotal() + "\n\n"
                + "=========================================\n"
                + "         ESTADO DE TU RESERVA            \n"
                + "=========================================\n"
                + "Código de Reserva: " + reserva.getPublicId() + "\n"
                + "Estado Actual: " + reserva.getEstadoReserva().toString() + " (PAGADA)\n"
                + "Período: Desde el " + reserva.getFechaInicio() + " hasta el " + reserva.getFechaFin() + "\n"
                + "Recursos reservados: " + reserva.getRecursos().stream().map(RecursoEntity::getNombre).toList().toString() + "\n\n"
                + "¡Te esperamos para disfrutar del verano con todo listo!");

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
