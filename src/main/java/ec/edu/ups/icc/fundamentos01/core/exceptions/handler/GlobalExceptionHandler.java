package ec.edu.ups.icc.fundamentos01.core.exceptions.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ec.edu.ups.icc.fundamentos01.core.exceptions.base.ApplicationException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Maneja excepciones de la aplicación:
     * 400, 404 y 409.
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse>
            handleApplicationException(
                    ApplicationException exception,
                    HttpServletRequest request
            ) {

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
     * Maneja validaciones de @RequestBody.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
            handleMethodArgumentNotValidException(
                    MethodArgumentNotValidException exception,
                    HttpServletRequest request
            ) {

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
     * Maneja validaciones de query parameters
     * recibidos con @ModelAttribute.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse>
            handleBindException(
                    BindException exception,
                    HttpServletRequest request
            ) {

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
                "Parámetros de consulta inválidos",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /*
     * Maneja parámetros inválidos como page=-1 o size=0.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse>
            handleIllegalArgumentException(
                    IllegalArgumentException exception,
                    HttpServletRequest request
            ) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Parámetros de paginación inválidos",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Maneja errores de autorización con @PreAuthorize.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse>
            handleAuthorizationDeniedException(
                    AuthorizationDeniedException exception,
                    HttpServletRequest request
            ) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "No tienes permisos para acceder a este recurso",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    /*
     * Maneja errores de acceso denegado.
     *
     * Se usa cuando:
     * - Un usuario autenticado no tiene permiso.
     * - Un usuario intenta modificar o eliminar un producto ajeno.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse>
            handleAccessDeniedException(
                    AccessDeniedException exception,
                    HttpServletRequest request
            ) {

        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            message = "Acceso denegado";
        }

        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    /*
     * Maneja errores de autenticación.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse>
            handleAuthenticationException(
                    AuthenticationException exception,
                    HttpServletRequest request
            ) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas o sesión expirada",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /*
     * Maneja errores inesperados.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
            handleUnexpectedException(
                    Exception exception,
                    HttpServletRequest request
            ) {

        /*
         * Temporalmente permite ver el error verdadero
         * en la consola de Spring Boot.
         */
        exception.printStackTrace();

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