package com.gestiontaller.server.exception;

public class ConfiguracionNotFoundException extends NotFoundException {

    public ConfiguracionNotFoundException(Long id) {
        super("Configuración", id);
    }

    public ConfiguracionNotFoundException(Long serieId, Integer numHojas) {
        super(String.format("Configuración no encontrada para serie ID %d con %d hojas", serieId, numHojas));
    }
}