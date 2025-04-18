package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.server.dto.calculo.CalculoVentanaDTO;
import com.gestiontaller.server.dto.calculo.ParametrosCalculoDTO;
import com.gestiontaller.server.dto.calculo.ResultadoCalculoDTO;

public interface CalculoVentanaService {

    // Calcular una ventana a partir de parámetros básicos
    ResultadoCalculoDTO calcularVentana(ParametrosCalculoDTO parametros);

    // Guardar un cálculo de ventana completo
    CalculoVentanaDTO guardarCalculo(CalculoVentanaDTO calculoDTO);

    // Obtener un cálculo por su ID
    CalculoVentanaDTO obtenerCalculo(Long id);

    // Calcular para una línea de presupuesto
    ResultadoCalculoDTO calcularParaLineaPresupuesto(Long lineaPresupuestoId);

    // Optimizar cortes para varios cálculos (trabajo completo)
    ResultadoCalculoDTO optimizarCortesParaTrabajo(Long trabajoId);
}