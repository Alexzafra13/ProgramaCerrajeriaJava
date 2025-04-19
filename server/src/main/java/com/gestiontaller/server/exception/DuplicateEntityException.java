package com.gestiontaller.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String mensaje) {
        super(mensaje);
    }

    public DuplicateEntityException(String entidad, String campo, String valor) {
        super(String.format("Ya existe un/a %s con %s: %s", entidad, campo, valor));
    }
}