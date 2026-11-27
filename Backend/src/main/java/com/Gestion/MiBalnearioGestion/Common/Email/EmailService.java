
package com.Gestion.MiBalnearioGestion.Common.Email;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoLugarEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String mailFrom;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @FunctionalInterface
    private interface AccionEmail {
        void ejecutar() throws Exception;
    }

    // =========================================================
    // MÉTODOS COMUNES
    // =========================================================

    private CompletableFuture<Void> ejecutarEmail(
            String destinatario,
            String tipo,
            AccionEmail accion
    ) {
        try {
            accion.ejecutar();

            log.info("Email {} enviado a: {}", tipo, destinatario);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error(
                    "Error al enviar email {} a: {}",
                    tipo,
                    destinatario,
                    e
            );

            return CompletableFuture.failedFuture(e);
        }
    }


    private void enviarEmail(
            String destinatario,
            String asunto,
            String html,
            String textoPlano
    ) throws Exception {

        MimeMessage mensaje = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                mensaje,
                true,
                StandardCharsets.UTF_8.name()
        );

        helper.setFrom(mailFrom);
        helper.setTo(destinatario);
        helper.setSubject(asunto);

        // Envía una alternativa de texto plano y otra HTML.
        helper.setText(textoPlano, html);

        mailSender.send(mensaje);
    }


    private void enviarPlantilla(
            String destinatario,
            String asunto,
            String preheader,
            String titulo,
            String subtitulo,
            String saludo,
            String introduccion,
            List<EmailHtml.Dato> datos,
            String textoBoton,
            String urlBoton,
            String advertencia,
            String textoPlano
    ) throws Exception {

        String html = EmailHtml.crear(
                preheader,
                titulo,
                subtitulo,
                saludo,
                introduccion,
                datos,
                textoBoton,
                urlBoton,
                advertencia
        );

        enviarEmail(
                destinatario,
                asunto,
                html,
                textoPlano
        );
    }

    private String recursosDeReserva(ReservaEntity reserva) {
        return reserva.getRecursos()
                .stream()
                .map(RecursoEntity::getNombre)
                .collect(Collectors.joining(", "));
    }

    private String detallePedido(PedidoLugarEntity pedido) {
        return pedido.getDetallePedidos()
                .stream()
                .map(d -> d.getProducto().getNombre()
                        + " x" + d.getCantidad()
                        + " — $" + d.getPrecio())
                .collect(Collectors.joining("\n"));
    }

    private double totalPedido(PedidoLugarEntity pedido) {
        return pedido.getDetallePedidos()
                .stream()
                .mapToDouble(DetallePedidoEntity::getPrecio)
                .sum();
    }

    private List<EmailHtml.Dato> datosPedido(PedidoLugarEntity pedido) {
        List<EmailHtml.Dato> datos = new ArrayList<>();

        datos.add(new EmailHtml.Dato(
                "Código de pedido",
                String.valueOf(pedido.getPublicId())
        ));

        datos.add(new EmailHtml.Dato(
                "Fecha",
                String.valueOf(pedido.getFechaPedido())
        ));

        datos.add(new EmailHtml.Dato(
                "Tipo de pedido",
                String.valueOf(pedido.getTipoPedido())
        ));

        datos.add(new EmailHtml.Dato(
                "Productos",
                detallePedido(pedido)
        ));

        return datos;
    }

    // =========================================================
    // BIENVENIDA
    // =========================================================

    @Async("emailExecutor")
    public CompletableFuture<Void> BienvenidaCliente(ClienteRequest cliente) {

        return ejecutarEmail(
                cliente.getEmail(),
                "de bienvenida a cliente",
                () -> enviarPlantilla(
                        cliente.getEmail(),
                        "Te damos la bienvenida a ARENA",
                        "Tu cuenta ya está creada.",
                        "¡Bienvenido a ARENA!",
                        "Tu experiencia en el balneario empieza acá.",
                        "¡Hola, " + cliente.getNombre() + "!",
                        "Tu cuenta fue creada correctamente. Ya podés ingresar al sistema y comenzar a disfrutar de nuestros servicios.",
                        List.of(
                                new EmailHtml.Dato(
                                        "Nombre",
                                        cliente.getNombre()
                                ),
                                new EmailHtml.Dato(
                                        "Correo electrónico",
                                        cliente.getEmail()
                                )
                        ),
                        null,
                        null,
                        null,
                        "¡Hola, " + cliente.getNombre() + "!\n\n"
                                + "Te damos la bienvenida a ARENA. "
                                + "Tu cuenta fue creada correctamente.\n\n"
                                + "Gracias por elegirnos."
                )
        );
    }

    @Async("emailExecutor")
    public CompletableFuture<Void> BienvenidaClienteRegistro(NewAccountRequest cliente) {

        return ejecutarEmail(
                cliente.getEmail(),
                "de bienvenida a cliente registrado",
                () -> enviarPlantilla(
                        cliente.getEmail(),
                        "Te damos la bienvenida a ARENA",
                        "Tu cuenta ya está creada.",
                        "¡Bienvenido a ARENA!",
                        "Tu experiencia en el balneario empieza acá.",
                        "¡Hola, " + cliente.getNombre() + "!",
                        "Tu registro se completó correctamente. Ya podés ingresar al sistema y consultar nuestros servicios.",
                        List.of(
                                new EmailHtml.Dato(
                                        "Nombre",
                                        cliente.getNombre()
                                ),
                                new EmailHtml.Dato(
                                        "Correo electrónico",
                                        cliente.getEmail()
                                )
                        ),
                        null,
                        null,
                        null,
                        "¡Hola, " + cliente.getNombre() + "!\n\n"
                                + "Tu registro en ARENA se completó correctamente.\n\n"
                                + "Gracias por elegirnos."
                )
        );
    }

    @Async("emailExecutor")
    public CompletableFuture<Void> BienvenidaEmpleado(EmpleadoDTO empleadoDTO) {

        return ejecutarEmail(
                empleadoDTO.getEmail(),
                "de bienvenida a empleado",
                () -> enviarPlantilla(
                        empleadoDTO.getEmail(),
                        "Bienvenido al equipo de ARENA",
                        "Te damos la bienvenida al equipo.",
                        "¡Bienvenido al equipo!",
                        "Tu cuenta de empleado ya está disponible.",
                        "¡Hola, " + empleadoDTO.getNombre() + "!",
                        "Tu cuenta fue creada correctamente. Ya podés ingresar al sistema con las credenciales correspondientes a tu rol.",
                        List.of(
                                new EmailHtml.Dato(
                                        "Nombre",
                                        empleadoDTO.getNombre()
                                ),
                                new EmailHtml.Dato(
                                        "Correo electrónico",
                                        empleadoDTO.getEmail()
                                )
                        ),
                        null,
                        null,
                        null,
                        "¡Hola, " + empleadoDTO.getNombre() + "!\n\n"
                                + "Te damos la bienvenida al equipo de ARENA.\n"
                                + "Tu cuenta fue creada correctamente."
                )
        );
    }

    // =========================================================
    // RESERVAS
    // =========================================================

    @Async("emailExecutor")
    public CompletableFuture<Void> cancelacionReserva(ReservaEntity reserva) {

        var cliente = reserva.getCliente();

        return ejecutarEmail(
                cliente.getEmail(),
                "de cancelación de reserva",
                () -> enviarPlantilla(
                        cliente.getEmail(),
                        "Actualización de tu reserva",
                        "Te informamos sobre el estado de tu reserva.",
                        "Tu reserva fue cancelada",
                        "La operación se registró correctamente.",
                        "Hola, " + cliente.getNombre() + ".",
                        "Te confirmamos que la reserva indicada a continuación fue cancelada.",
                        List.of(
                                new EmailHtml.Dato(
                                        "Código de reserva",
                                        String.valueOf(reserva.getPublicId())
                                ),
                                new EmailHtml.Dato(
                                        "Estado",
                                        String.valueOf(reserva.getEstadoReserva())
                                ),
                                new EmailHtml.Dato(
                                        "Monto",
                                        "$" + reserva.getMontoTotal()
                                ),
                                new EmailHtml.Dato(
                                        "Desde",
                                        String.valueOf(reserva.getFechaInicio())
                                ),
                                new EmailHtml.Dato(
                                        "Hasta",
                                        String.valueOf(reserva.getFechaFin())
                                ),
                                new EmailHtml.Dato(
                                        "Recursos",
                                        recursosDeReserva(reserva)
                                )
                        ),
                        null,
                        null,
                        null,
                        "Hola, " + cliente.getNombre() + ".\n\n"
                                + "Tu reserva fue cancelada.\n\n"
                                + "Código: " + reserva.getPublicId() + "\n"
                                + "Estado: " + reserva.getEstadoReserva() + "\n"
                                + "Monto: $" + reserva.getMontoTotal() + "\n"
                                + "Desde: " + reserva.getFechaInicio() + "\n"
                                + "Hasta: " + reserva.getFechaFin() + "\n"
                                + "Recursos: " + recursosDeReserva(reserva)
                )
        );
    }

    @Async("emailExecutor")
    public CompletableFuture<Void> confirmacionReserva(
            ReservaEntity reserva,
            String urlMercadoPago
    ) {
        var cliente = reserva.getCliente();

        return ejecutarEmail(
                cliente.getEmail(),
                "de reserva pendiente",
                () -> enviarPlantilla(
                        cliente.getEmail(),
                        "Completá el pago de tu reserva",
                        "Tu reserva fue generada correctamente.",
                        "Tu reserva está pendiente",
                        "Solo falta completar el pago para confirmar tu lugar.",
                        "¡Hola, " + cliente.getNombre() + "!",
                        "Generamos tu reserva correctamente. Revisá los datos y completá el pago mediante Mercado Pago.",
                        List.of(
                                new EmailHtml.Dato(
                                        "Código de reserva",
                                        String.valueOf(reserva.getPublicId())
                                ),
                                new EmailHtml.Dato(
                                        "Fecha de inicio",
                                        String.valueOf(reserva.getFechaInicio())
                                ),
                                new EmailHtml.Dato(
                                        "Fecha de finalización",
                                        String.valueOf(reserva.getFechaFin())
                                ),
                                new EmailHtml.Dato(
                                        "Recursos",
                                        recursosDeReserva(reserva)
                                ),
                                new EmailHtml.Dato(
                                        "Total a pagar",
                                        "$" + reserva.getMontoTotal()
                                )
                        ),
                        "PAGAR CON MERCADO PAGO",
                        urlMercadoPago,
                        "Importante: tenés 15 minutos para completar el pago. Si no se acredita dentro de ese plazo, la reserva será cancelada automáticamente.",
                        "¡Hola, " + cliente.getNombre() + "!\n\n"
                                + "Tu reserva fue generada correctamente y está pendiente de pago.\n\n"
                                + "Código: " + reserva.getPublicId() + "\n"
                                + "Desde: " + reserva.getFechaInicio() + "\n"
                                + "Hasta: " + reserva.getFechaFin() + "\n"
                                + "Recursos: " + recursosDeReserva(reserva) + "\n"
                                + "Total: $" + reserva.getMontoTotal() + "\n\n"
                                + "Completá el pago en este enlace:\n"
                                + urlMercadoPago + "\n\n"
                                + "Tenés 15 minutos para pagar."
                )
        );
    }

    @Async("emailExecutor")
    public CompletableFuture<Void> confirmacionPagoReserva(
            PagoReservaEntity pagoReserva,
            TicketEntity ticket
    ) {
        ReservaEntity reserva = pagoReserva.getReserva();
        var cliente = reserva.getCliente();

        return ejecutarEmail(
                cliente.getEmail(),
                "de pago de reserva",
                () -> enviarPlantilla(
                        cliente.getEmail(),
                        "Pago confirmado: tu reserva está lista",
                        "Recibimos tu pago correctamente.",
                        "¡Tu reserva está confirmada!",
                        "El pago fue registrado correctamente.",
                        "¡Hola, " + cliente.getNombre() + "!",
                        "Ya está todo listo. Te compartimos los datos de tu comprobante y de tu reserva.",
                        List.of(
                                new EmailHtml.Dato(
                                        "Número de ticket",
                                        String.valueOf(ticket.getId())
                                ),
                                new EmailHtml.Dato(
                                        "Fecha de pago",
                                        String.valueOf(ticket.getFechaTicket())
                                ),
                                new EmailHtml.Dato(
                                        "Monto abonado",
                                        "$" + ticket.getTotal()
                                ),
                                new EmailHtml.Dato(
                                        "Código de reserva",
                                        String.valueOf(reserva.getPublicId())
                                ),
                                new EmailHtml.Dato(
                                        "Estado",
                                        String.valueOf(reserva.getEstadoReserva())
                                ),
                                new EmailHtml.Dato(
                                        "Período",
                                        reserva.getFechaInicio() + " al " + reserva.getFechaFin()
                                ),
                                new EmailHtml.Dato(
                                        "Recursos",
                                        recursosDeReserva(reserva)
                                )
                        ),
                        null,
                        null,
                        "¡Listo! Tu pago fue confirmado. Te esperamos para disfrutar del verano.",
                        "¡Hola, " + cliente.getNombre() + "!\n\n"
                                + "Confirmamos que recibimos tu pago.\n\n"
                                + "Ticket: " + ticket.getId() + "\n"
                                + "Fecha de pago: " + ticket.getFechaTicket() + "\n"
                                + "Monto: $" + ticket.getTotal() + "\n"
                                + "Reserva: " + reserva.getPublicId() + "\n"
                                + "Estado: " + reserva.getEstadoReserva() + "\n"
                                + "Período: " + reserva.getFechaInicio()
                                + " al " + reserva.getFechaFin() + "\n"
                                + "Recursos: " + recursosDeReserva(reserva)
                )
        );
    }

    // =========================================================
    // PEDIDOS
    // =========================================================

    @Async("emailExecutor")
    public CompletableFuture<Void> enviarLinkPagoPedido(
            PedidoLugarEntity pedido,
            PagoEntity pago,
            String linkPago
    ) {
        ClienteEntity cliente = pedido.getCliente();

        List<EmailHtml.Dato> datos = datosPedido(pedido);

        datos.add(new EmailHtml.Dato(
                "Total a pagar",
                "$" + totalPedido(pedido)
        ));

        return ejecutarEmail(
                cliente.getEmail(),
                "de enlace de pago de pedido",
                () -> enviarPlantilla(
                        cliente.getEmail(),
                        "Tu pedido está listo para pagar",
                        "Recibimos tu pedido correctamente.",
                        "Tu pedido está pendiente de pago",
                        "Completá el pago para que podamos prepararlo.",
                        "¡Hola, " + cliente.getNombre() + "!",
                        "Recibimos tu pedido. Te dejamos el detalle y el enlace para realizar el pago.",
                        datos,
                        "PAGAR CON MERCADO PAGO",
                        linkPago,
                        "Tenés 15 minutos para completar el pago. Si no se acredita dentro de ese plazo, el pedido será cancelado automáticamente.",
                        "¡Hola, " + cliente.getNombre() + "!\n\n"
                                + "Recibimos tu pedido.\n\n"
                                + "Código: " + pedido.getPublicId() + "\n"
                                + "Fecha: " + pedido.getFechaPedido() + "\n"
                                + "Tipo: " + pedido.getTipoPedido() + "\n\n"
                                + "Productos:\n" + detallePedido(pedido) + "\n\n"
                                + "Total: $" + totalPedido(pedido) + "\n\n"
                                + "Enlace de pago: " + linkPago + "\n\n"
                                + "Tenés 15 minutos para completar el pago."
                )
        );
    }

    @Async("emailExecutor")
    public CompletableFuture<Void> confirmacionPagoPedido(
            PedidoLugarEntity pedido,
            TicketEntity ticket
    ) {
        ClienteEntity cliente = pedido.getCliente();

        List<EmailHtml.Dato> datos = datosPedido(pedido);

        datos.add(new EmailHtml.Dato(
                "Total abonado",
                "$" + ticket.getTotal()
        ));

        datos.add(new EmailHtml.Dato(
                "Número de ticket",
                String.valueOf(ticket.getPublicId())
        ));

        datos.add(new EmailHtml.Dato(
                "Fecha de pago",
                String.valueOf(ticket.getFechaTicket())
        ));

        return ejecutarEmail(
                cliente.getEmail(),
                "de confirmación de pago de pedido",
                () -> enviarPlantilla(
                        cliente.getEmail(),
                        "Pago confirmado: tu pedido está en preparación",
                        "Recibimos tu pago correctamente.",
                        "¡Tu pedido está confirmado!",
                        "El pago fue registrado y tu pedido ya puede prepararse.",
                        "¡Hola, " + cliente.getNombre() + "!",
                        "Confirmamos que recibimos tu pago. Te compartimos el detalle del pedido y los datos del comprobante.",
                        datos,
                        null,
                        null,
                        "En breve un repartidor te llevará tu pedido. ¡Gracias por elegir ARENA!",
                        "¡Hola, " + cliente.getNombre() + "!\n\n"
                                + "Confirmamos que recibimos tu pago.\n\n"
                                + "Código: " + pedido.getPublicId() + "\n"
                                + "Fecha: " + pedido.getFechaPedido() + "\n"
                                + "Tipo: " + pedido.getTipoPedido() + "\n"
                                + "Productos:\n" + detallePedido(pedido) + "\n\n"
                                + "Total abonado: $" + ticket.getTotal() + "\n"
                                + "Ticket: " + ticket.getPublicId() + "\n"
                                + "Fecha de pago: " + ticket.getFechaTicket() + "\n\n"
                                + "En breve un repartidor te llevará tu pedido."
                )
        );
    }

    // =========================================================
    // RECUPERACIÓN DE CONTRASEÑA
    // =========================================================

    @Async("emailExecutor")
    public CompletableFuture<Void> enviarResetContrasenia(
            String email,
            String nombreUsuario,
            String token
    ) {
        String baseUrl = frontendUrl.replaceAll("/+$", "");

        String link = baseUrl
                + "/reset-password?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        return ejecutarEmail(
                email,
                "de recuperación de contraseña",
                () -> enviarPlantilla(
                        email,
                        "Restablecé tu contraseña de ARENA",
                        "Recibimos una solicitud para cambiar tu contraseña.",
                        "Recuperación de contraseña",
                        "Volvé a acceder a tu cuenta de forma segura.",
                        "Hola, " + nombreUsuario + ".",
                        "Recibimos una solicitud para restablecer la contraseña de tu cuenta.",
                        List.of(
                                new EmailHtml.Dato(
                                        "Cuenta",
                                        email
                                ),
                                new EmailHtml.Dato(
                                        "Validez del enlace",
                                        "30 minutos"
                                )
                        ),
                        "RESTABLECER CONTRASEÑA",
                        link,
                        "Si no solicitaste este cambio, podés ignorar este correo. Tu contraseña actual seguirá siendo válida.",
                        "Hola, " + nombreUsuario + ".\n\n"
                                + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                                + "Ingresá en el siguiente enlace para crear una nueva contraseña:\n"
                                + link + "\n\n"
                                + "El enlace es válido durante 30 minutos.\n"
                                + "Si no solicitaste este cambio, ignorá este mensaje."
                )
        );
    }
}
