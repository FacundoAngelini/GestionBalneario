package com.Gestion.MiBalnearioGestion.Common.Email;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoLugarEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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
    public void enviarLinkPagoPedido(PedidoLugarEntity pedido, PagoEntity pago, String linkPago) {
        ClienteEntity cliente = pedido.getCliente();

        // Arma el detalle de productos
        String detalle = pedido.getDetallePedidos().stream()
                .map(d -> "  - " + d.getProducto().getNombre()
                        + " x" + d.getCantidad()
                        + "  →  $" + d.getPrecio())
                .collect(Collectors.joining("\n"));

        double total = pedido.getDetallePedidos().stream()
                .mapToDouble(DetallePedidoEntity::getPrecio)
                .sum();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("balnearioapiutn@gmail.com");
        mail.setTo(cliente.getEmail());
        mail.setSubject("Tu pedido en el Balneario está listo para pagar 🏖️");

        mail.setText(
                "¡Hola, " + cliente.getNombre() + "!\n\n"
                        + "Recibimos tu pedido correctamente. Para que lo preparemos, completá el pago "
                        + "usando el siguiente enlace antes de que expire.\n\n"
                        + "=========================================\n"
                        + "           DETALLE DEL PEDIDO            \n"
                        + "=========================================\n"
                        + "Código de Pedido : " + pedido.getPublicId() + "\n"
                        + "Fecha            : " + pedido.getFechaPedido() + "\n"
                        + "Tipo             : " + pedido.getTipoPedido() + "\n\n"
                        + "Productos:\n"
                        + detalle + "\n\n"
                        + "─────────────────────────────────────────\n"
                        + "TOTAL A PAGAR    : $" + total + "\n"
                        + "─────────────────────────────────────────\n\n"
                        + "=========================================\n"
                        + "          ENLACE DE PAGO (MERCADO PAGO)  \n"
                        + "=========================================\n"
                        + linkPago + "\n\n"
                        + "IMPORTANTE: Tenés 15 minutos para completar el pago o el pedido será cancelado automáticamente.\n\n"
                        + "¡Muchas gracias por elegirnos!"
        );

        mailSender.send(mail);
    }


    public void confirmacionPagoPedido(PedidoLugarEntity pedido, TicketEntity ticket) {
        ClienteEntity cliente = pedido.getCliente();

        String detalle = pedido.getDetallePedidos().stream()
                .map(d -> "  - " + d.getProducto().getNombre()
                        + " x" + d.getCantidad()
                        + "  →  $" + d.getPrecio())
                .collect(Collectors.joining("\n"));

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("balnearioapiutn@gmail.com");
        mail.setTo(cliente.getEmail());
        mail.setSubject("¡Pago confirmado! Tu pedido está en preparación 🍽️");

        mail.setText(
                "¡Hola, " + cliente.getNombre() + "!\n\n"
                        + "Confirmamos que recibimos tu pago y tu pedido ya está en preparación.\n\n"
                        + "=========================================\n"
                        + "           DETALLE DEL PEDIDO            \n"
                        + "=========================================\n"
                        + "Código de Pedido : " + pedido.getPublicId() + "\n"
                        + "Fecha            : " + pedido.getFechaPedido() + "\n"
                        + "Tipo             : " + pedido.getTipoPedido() + "\n\n"
                        + "Productos:\n"
                        + detalle + "\n\n"
                        + "─────────────────────────────────────────\n"
                        + "TOTAL ABONADO    : $" + ticket.getTotal() + "\n"
                        + "─────────────────────────────────────────\n\n"
                        + "=========================================\n"
                        + "            COMPROBANTE DE PAGO          \n"
                        + "=========================================\n"
                        + "ID Ticket        : " + ticket.getPublicId() + "\n"
                        + "Fecha de Pago    : " + ticket.getFechaTicket() + "\n\n"
                        + "En breve un repartidor te llevará tu pedido. ¡Gracias por elegirnos! 🏖️"
        );

        mailSender.send(mail);
    }

    // esto para el dia de mañana que tengamos el front
    public void enviarResetContrasenia(String email, String nombreUsuario, String token) {
        String link = "https://tu-frontend.com/reset-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("Recuperación de contraseña — Balneario");
        mensaje.setText(
                "Hola " + nombreUsuario + ",\n\n" +
                        "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                        "Hacé clic en el siguiente enlace para crear una nueva (válido por 30 minutos):\n" +
                        link + "\n\n" +
                        "Si no solicitaste esto, ignorá este mensaje.\n\n" +
                        "Balneario — Sistema de Gestión"
        );

        mailSender.send(mensaje);
    }
}
