package com.Gestion.MiBalnearioGestion.Common.Email;

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

    public void BienvenidaEmpleado (EmpleadoDTO empleadoDTO){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(empleadoDTO.getEmail());
        mailMessage.setSubject("Bienvenido al Equipo del Balneario " + empleadoDTO.getNombre());
        mailMessage.setText("Te damos la bienvenida y confirmamos la creación de tu cuenta!");
        mailSender.send(mailMessage);
    }

    public void confirmacionReserva(ReservaEntity reserva){

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(reserva.getCliente().getEmail());
        mailMessage.setSubject("Reserva en Balneario de " + reserva.getCliente().getNombre());
        mailMessage.setText("Hola! " + reserva.getCliente().getNombre() + " te acercamos los detalles de tu reserva: "
                +"\n" + "Codigo de Reserva: " + reserva.getPublicId()
                +"\n" + "Estado: " + reserva.getEstadoReserva().toString()
                +"\n" + "Monto total: " +reserva.getMontoTotal()
                +"\n" + "Duracion: Empieza el  " + reserva.getFechaInicio() + " y termina el " + reserva.getFechaFin()
                +"\n" + "Por: " +reserva.getRecursos().stream().map(RecursoEntity::getNombre).toList().toString());
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

    public void confirmacionPagoReserva(PagoReservaEntity pagoReserva, TicketEntity ticket){

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("balnearioapiutn@gmail.com");
        mailMessage.setTo(pagoReserva.getReserva().getCliente().getEmail());
        mailMessage.setSubject("Pago de Reserva en Balneario de " + pagoReserva.getReserva().getCliente().getNombre());
        mailMessage.setText("Hola! " + pagoReserva.getReserva().getCliente().getNombre() + " te acercamos el ticket de tu pago: "
                +"\n" + "Codigo de Reserva: " + pagoReserva.getReserva().getPublicId()
                +"\n" + "Reserva: " + pagoReserva.getReserva().getEstadoReserva().toString()
                +"\n" + "De: " + pagoReserva.getReserva().getCliente().getNombre()
                +"\n" + "Fecha: " + ticket.getFechaTicket()
                +"\n" + "Total: " + ticket.getTotal());
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
