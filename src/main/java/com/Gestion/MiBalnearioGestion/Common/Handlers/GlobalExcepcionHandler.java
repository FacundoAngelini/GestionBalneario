package com.Gestion.MiBalnearioGestion.Common.Handlers;


import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExcepcionHandler {

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(EntidadNoEncontradaException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(404,"El usuario no fue encontrado : "+ ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
                                            Exception ex,
                                            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(500, "Error interno del servidor");
        error.setPath(request.getRequestURI());
        return ResponseEntity.internalServerError().body(error);
    }

}
