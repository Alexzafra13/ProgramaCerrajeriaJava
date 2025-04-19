package com.gestiontaller.server.exception;

public class SerieNotFoundException extends NotFoundException {

    public SerieNotFoundException(Long id) {
        super("Serie", id);
    }

    public SerieNotFoundException(String codigo) {
        super("Serie", "código", codigo);
    }
}