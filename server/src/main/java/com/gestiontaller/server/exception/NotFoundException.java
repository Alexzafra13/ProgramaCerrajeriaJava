package com.gestiontaller.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String mensaje) {
        super(mensaje);
    }

    public NotFoundException(String entidad, Long id) {
        super(String.format("%s no encontrado con ID: %d", entidad, id));
    }

    public NotFoundException(String entidad, String campo, String valor) {
        super(String.format("%s no encontrado con %s: %s", entidad, campo, valor));
    }
}