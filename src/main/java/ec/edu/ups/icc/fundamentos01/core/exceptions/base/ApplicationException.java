package ec.edu.ups.icc.fundamentos01.core.exceptions.base;

import org.springframework.http.HttpStatus;

/*
 * Excepción base de la aplicación.
 *
 * Todas las excepciones propias del sistema
 * deben heredar de esta clase.
 */
public abstract class ApplicationException
        extends RuntimeException {

    private final HttpStatus status;

    protected ApplicationException(
            HttpStatus status,
            String message) {

        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}