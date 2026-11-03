package com.Gestion.MiBalnearioGestion.Common.Handlers;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.*;
import com.Gestion.MiBalnearioGestion.Recursos.Exception.RecursoException;
import com.Gestion.MiBalnearioGestion.Recursos.Exception.RecursoOcupadoException;
import com.Gestion.MiBalnearioGestion.Reservas.Exception.ReservaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaNoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * Convierte las excepciones en respuestas JSON { status, message, path, timestamp }.
 * Regla: el mensaje que ve el usuario nunca trae detalles internos (SQL, clases, pilas de error);
 * esos detalles quedan en el log del servidor.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExcepcionHandler {

    private ResponseEntity<ErrorResponse> respuesta(int estado, String mensaje, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(estado, mensaje);
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(estado).body(error);
    }

    // ---- No controlados: mensaje genérico, detalle solo en el log ----

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return respuesta(500, "Ocurrió un error interno. Intentá de nuevo más tarde.", request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        return respuesta(400, ex.getMessage(), request);
    }

    // ---- Datos de entrada ----

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respuesta(400, "Datos inválidos: " + detalle, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleViolacion(ConstraintViolationException ex, HttpServletRequest request) {
        String detalle = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining("; "));
        return respuesta(400, "Datos inválidos: " + detalle, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleCuerpoIlegible(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Cuerpo ilegible en {}: {}", request.getRequestURI(), ex.getMessage());
        return respuesta(400, "El cuerpo de la solicitud no es válido o tiene un valor que no corresponde", request);
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleDatosIncompletos(Exception ex, HttpServletRequest request) {
        log.warn("Datos inválidos en {}: {}", request.getRequestURI(), ex.getMessage());
        return respuesta(400, "Los datos enviados son inválidos o están incompletos", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTipoParametro(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return respuesta(400, "El parámetro '" + ex.getName() + "' tiene un valor inválido", request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleFaltaParametro(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return respuesta(400, "Falta el parámetro '" + ex.getParameterName() + "'", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleRutaInexistente(NoResourceFoundException ex, HttpServletRequest request) {
        return respuesta(400, "URL mal escrita", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMetodo(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return respuesta(405, "Método no permitido para esta ruta", request);
    }

    // ---- Reglas de negocio (los mensajes son intencionales) ----

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrada(EntidadNoEncontradaException ex, HttpServletRequest request) {
        return respuesta(404, "La entidad no fue encontrada: " + ex.getMessage(), request);
    }

    @ExceptionHandler(EntidadExistenteException.class)
    public ResponseEntity<ErrorResponse> handleExistente(EntidadExistenteException ex, HttpServletRequest request) {
        return respuesta(409, "La entidad ya existe: " + ex.getMessage(), request);
    }

    @ExceptionHandler(DatosInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleDatosInvalidos(DatosInvalidoException ex, HttpServletRequest request) {
        return respuesta(400, "Los datos son inválidos: " + ex.getMessage(), request);
    }

    @ExceptionHandler(ReservaException.class)
    public ResponseEntity<ErrorResponse> handleReserva(ReservaException ex, HttpServletRequest request) {
        return respuesta(400, "Error en la reserva: " + ex.getMessage(), request);
    }

    @ExceptionHandler(RecursoException.class)
    public ResponseEntity<ErrorResponse> handleRecurso(RecursoException ex, HttpServletRequest request) {
        return respuesta(400, "Error en el recurso: " + ex.getMessage(), request);
    }

    @ExceptionHandler(RecursoOcupadoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoOcupado(RecursoOcupadoException ex, HttpServletRequest request) {
        return respuesta(409, "Recurso ocupado: " + ex.getMessage(), request);
    }

    @ExceptionHandler(ProductoException.class)
    public ResponseEntity<ErrorResponse> handleProducto(ProductoException ex, HttpServletRequest request) {
        return respuesta(422, "Producto no disponible: " + ex.getMessage(), request);
    }

    @ExceptionHandler(AccionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleAccionInvalida(AccionInvalidaException ex, HttpServletRequest request) {
        return respuesta(422, "No se puede realizar esta acción: " + ex.getMessage(), request);
    }

    // ---- Duplicados y conflictos: 409 (antes respondían 403) ----

    @ExceptionHandler(CuentaEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCuentaExistente(CuentaEncontradaException ex, HttpServletRequest request) {
        return respuesta(409, "Esta cuenta ya está asociada: " + ex.getMessage(), request);
    }

    @ExceptionHandler(CuentaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCuentaNoEncontrada(CuentaNoEncontradaException ex, HttpServletRequest request) {
        return respuesta(404, "Esta cuenta no se encuentra: " + ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violación de integridad en {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return respuesta(409, "No se pudo completar la operación: ya existe un registro con esos datos o el dato está en uso", request);
    }

    // ---- Seguridad ----

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class, DisabledException.class, LockedException.class})
    public ResponseEntity<ErrorResponse> handleCredenciales(Exception ex, HttpServletRequest request) {
        String mensaje = "Usuario o contraseña incorrecta";
        if (ex instanceof DisabledException) mensaje = "Esta cuenta ha sido deshabilitada. Contacte al administrador.";
        else if (ex instanceof LockedException) mensaje = "Esta cuenta se encuentra bloqueada.";
        return respuesta(401, mensaje, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAutenticacion(AuthenticationException ex, HttpServletRequest request) {
        return respuesta(401, "Error de autenticación", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccesoDenegado(AccessDeniedException ex, HttpServletRequest request) {
        return respuesta(403, "No tiene los permisos suficientes", request);
    }

    // ---- Servicios externos ----

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorResponse> handleMail(MailException ex, HttpServletRequest request) {
        log.error("Error al enviar un correo", ex);
        return respuesta(403, "No se pudo enviar el correo", request);
    }

    @ExceptionHandler(com.mercadopago.exceptions.MPApiException.class)
    public ResponseEntity<ErrorResponse> handleMPApi(com.mercadopago.exceptions.MPApiException ex, HttpServletRequest request) {
        log.error("Error de la API de Mercado Pago: {}", ex.getApiResponse() != null ? ex.getApiResponse().getContent() : ex.getMessage());
        return respuesta(502, "No se pudo procesar el pago con Mercado Pago", request);
    }

    @ExceptionHandler(com.mercadopago.exceptions.MPException.class)
    public ResponseEntity<ErrorResponse> handleMP(com.mercadopago.exceptions.MPException ex, HttpServletRequest request) {
        log.error("Error de comunicación con Mercado Pago", ex);
        return respuesta(502, "No se pudo comunicar con Mercado Pago", request);
    }
}