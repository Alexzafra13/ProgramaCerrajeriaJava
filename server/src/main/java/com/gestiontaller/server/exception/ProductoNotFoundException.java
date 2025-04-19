package com.gestiontaller.server.exception;

public class ProductoNotFoundException extends NotFoundException {

    public ProductoNotFoundException(Long id) {
        super("Producto", id);
    }

    public ProductoNotFoundException(String codigo) {
        super("Producto", "código", codigo);
    }
}