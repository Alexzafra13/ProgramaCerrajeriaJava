package com.gestiontaller.server.model.serie;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "serie_hierro")
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
public class SerieHierro extends SerieBase {

    @Column(nullable = false)
    private double espesor;

    private boolean galvanizado;

    private String tipoAcabado;

}