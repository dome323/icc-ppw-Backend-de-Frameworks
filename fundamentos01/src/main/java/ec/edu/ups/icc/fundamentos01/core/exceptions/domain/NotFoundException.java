package ec.edu.ups.icc.fundamentos01.core.exceptions.domain;

import org.springframework.http.HttpStatus;

import ec.edu.ups.icc.fundamentos01.core.exceptions.base.ApplicationException;

/*
 * Se utiliza cuando un recurso no existe
 * o está eliminado lógicamente.
 */
public class NotFoundException
        extends ApplicationException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}