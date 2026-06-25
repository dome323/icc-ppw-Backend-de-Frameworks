package ec.edu.ups.icc.fundamentos01.core.exceptions.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ec.edu.ups.icc.fundamentos01.core.exceptions.base.ApplicationException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

/*
 * Manejador global de errores.
 *
 * Captura las excepciones producidas en los
 * controladores y servicios de la aplicación.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Maneja NotFoundException,
     * ConflictException, BadRequestException
     * y cualquier ApplicationException.
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse>
            handleApplicationException(
                    ApplicationException exception,
                    HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                exception.getStatus(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    /*
     * Maneja errores producidos por @Valid.
     */
    @ExceptionHandler(
            MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
            handleValidationException(
                    MethodArgumentNotValidException exception,
                    HttpServletRequest request) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Datos de entrada inválidos",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /*
     * Maneja cualquier error inesperado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
            handleUnexpectedException(
                    Exception exception,
                    HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}