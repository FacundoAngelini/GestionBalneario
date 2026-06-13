package com.Gestion.MiBalnearioGestion.Common.Handlers;


import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.RecursoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.ReservaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.RecursoOcupadoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExcepcionHandler {

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(EntidadNoEncontradaException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(404,"La entidad no fue encontrada : "+ ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
                                            Exception ex,
                                            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(500, "Error interno del servidor" +ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(EntidadExistenteException.class)
    public ResponseEntity<ErrorResponse> handleUserFound(EntidadExistenteException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(409,"La entidad fue encontrada : "+ ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(409).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handExceptionValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(400, "Argumento no valido" + ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(400, "Error" + ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handMessageException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(400, "error de mensaje " + ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(400, "Los datos enviados son invalidos o estan incompletos" + ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(403, "No tiene los permisos suficientes" + ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorResponse> handleMailException(MailException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(500, "Error al enviar el mail" + ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(ReservaException.class)
    public ResponseEntity<ErrorResponse> ReservaExceptionHandler(ReservaException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(500, "Error en la reserva" + ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(RecursoException.class)
    public ResponseEntity<ErrorResponse> RecursoExceptionHandler(RecursoException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(500, "Error en el recurso" + ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class, DisabledException.class,
            LockedException.class})
    public ResponseEntity<ErrorResponse> handleBadCredentials(Exception ex, HttpServletRequest request
    ) {
        String mensajeError = "Usuario o contraseña incorrecta";

        if (ex instanceof org.springframework.security.authentication.DisabledException) {
            mensajeError = "Esta cuenta ha sido deshabilitada. Contacte al administrador.";
        }
        else if (ex instanceof org.springframework.security.authentication.LockedException) {
            mensajeError = "Esta cuenta se encuentra bloqueada.";
        }

        ErrorResponse error = new ErrorResponse(401, mensajeError);
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(401).body(error);
    }

    @ExceptionHandler(com.mercadopago.exceptions.MPApiException.class)
    public ResponseEntity<ErrorResponse> handleMPApiException(com.mercadopago.exceptions.MPApiException ex, HttpServletRequest request) {
        int status = ex.getStatusCode();
        String detalleError = ex.getApiResponse() != null ? ex.getApiResponse().getContent() : ex.getMessage();
        ErrorResponse error = new ErrorResponse(status, "Error en la API de Mercado Pago: " + detalleError);
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(com.mercadopago.exceptions.MPException.class)
    public ResponseEntity<ErrorResponse> handleMPException(com.mercadopago.exceptions.MPException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(500, "Error de comunicación con el SDK de Mercado Pago: " + ex.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(RecursoOcupadoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoOcupado(RecursoOcupadoException ex, HttpServletRequest  request) {
        ErrorResponse errorResponse= new ErrorResponse(400, "Error de recurso ocupado" + ex.getMessage());
       errorResponse.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse) ;
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(400, ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(400).body(error);
    }
}
