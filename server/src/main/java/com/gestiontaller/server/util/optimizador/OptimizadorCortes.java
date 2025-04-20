package com.gestiontaller.server.util.optimizador;

import com.gestiontaller.common.dto.inventario.RetalDTO;
import com.gestiontaller.server.service.interfaces.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase encargada de optimizar los cortes de perfiles para minimizar
 * el desperdicio de material y aprovechar retales existentes.
 */
@Component
public class OptimizadorCortes {

    private final InventarioService inventarioService;

    // Longitud estándar de las barras en mm
    private static final int LONGITUD_BARRA_ESTANDAR = 6000;

    // Margen mínimo para considerar un retal como aprovechable en mm
    private static final int MARGEN_MINIMO_RETAL = 300;

    @Autowired
    public OptimizadorCortes(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    /**
     * Optimiza los cortes para un resultado de cálculo de ventana
     */
    public ResultadoCalculoDTO optimizarCortes(ResultadoCalculoDTO resultadoCalculo) {
        // Agrupar los cortes por tipo de perfil y ordenarlos por longitud (descendente)
        Map<Long, List<CorteDTO>> cortesPorPerfil = resultadoCalculo.getCortes().stream()
                .collect(Collectors.groupingBy(CorteDTO::getPerfilId));

        // Para cada grupo de cortes del mismo perfil, optimizar
        cortesPorPerfil.forEach((perfilId, cortes) -> {
            // Ordenar cortes de mayor a menor longitud
            cortes.sort(Comparator.comparing(CorteDTO::getLongitud).reversed());

            // Buscar retales disponibles para este perfil
            List<RetalDTO> retalesDisponibles = buscarRetalesDisponibles(perfilId);

            // Realizar la optimización
            optimizarCortesPorPerfil(cortes, retalesDisponibles);
        });

        return resultadoCalculo;
    }

    /**
     * Optimiza los cortes para múltiples ventanas (como para un trabajo completo)
     */
    public List<ResultadoCalculoDTO> optimizarCortesMultiplesVentanas(List<ResultadoCalculoDTO> resultadosCalculos) {
        // Recolectar todos los cortes de todas las ventanas, agrupados por perfil
        Map<Long, List<CorteDTO>> todosLosCortesAgrupados = new HashMap<>();

        // Recolectar cortes de todas las ventanas
        for (ResultadoCalculoDTO resultado : resultadosCalculos) {
            for (CorteDTO corte : resultado.getCortes()) {
                todosLosCortesAgrupados
                        .computeIfAbsent(corte.getPerfilId(), k -> new ArrayList<>())
                        .add(corte);
            }
        }

        // Optimizar cada grupo de cortes del mismo perfil
        todosLosCortesAgrupados.forEach((perfilId, cortes) -> {
            // Ordenar cortes de mayor a menor longitud
            cortes.sort(Comparator.comparing(CorteDTO::getLongitud).reversed());

            // Buscar retales disponibles para este perfil
            List<RetalDTO> retalesDisponibles = buscarRetalesDisponibles(perfilId);

            // Realizar la optimización
            optimizarCortesPorPerfil(cortes, retalesDisponibles);
        });

        return resultadosCalculos;
    }

    /**
     * Busca retales disponibles para un tipo de perfil concreto
     */
    private List<RetalDTO> buscarRetalesDisponibles(Long perfilId) {
        try {
            return inventarioService.buscarRetalesDisponiblesParaCorte(perfilId, MARGEN_MINIMO_RETAL);
        } catch (Exception e) {
            // En caso de error, devolver lista vacía
            return new ArrayList<>();
        }
    }

    /**
     * Optimiza los cortes para un perfil específico usando el algoritmo de Bin Packing
     */
    private void optimizarCortesPorPerfil(List<CorteDTO> cortes, List<RetalDTO> retalesDisponibles) {
        // Implementación simplificada del algoritmo First Fit Decreasing Bin Packing

        // Crear barras de trabajo (bins)
        List<Barra> barras = new ArrayList<>();

        // Convertir los retales disponibles a barras
        for (RetalDTO retal : retalesDisponibles) {
            barras.add(new Barra(retal.getId(), retal.getLongitud()));
        }

        // Añadir barras completas según sea necesario
        barras.add(new Barra(0L, LONGITUD_BARRA_ESTANDAR));

        // Para cada corte, encontrar la mejor barra donde encaje
        for (CorteDTO corte : cortes) {
            // Consideramos cada corte según su cantidad
            for (int i = 0; i < corte.getCantidad(); i++) {
                boolean asignado = false;

                // Buscar la primera barra donde encaje el corte
                for (Barra barra : barras) {
                    if (barra.espacioDisponible >= corte.getLongitud()) {
                        // Asignar el corte a esta barra
                        barra.asignarCorte(corte);
                        asignado = true;
                        break;
                    }
                }

                // Si no se pudo asignar a ninguna barra existente, crear una nueva
                if (!asignado) {
                    Barra nuevaBarra = new Barra(0L, LONGITUD_BARRA_ESTANDAR);
                    nuevaBarra.asignarCorte(corte);
                    barras.add(nuevaBarra);
                }
            }
        }

        // En una implementación completa, aquí actualizaríamos los CorteDTO
        // con la información de optimización (barra asignada, posición, etc.)
    }

    /**
     * Clase interna para representar una barra de material
     */
    private static class Barra {
        private final Long id; // 0 para barras nuevas, id del retal para retales
        private final int longitudTotal;
        private int espacioDisponible;
        private final List<CorteAsignado> cortesAsignados = new ArrayList<>();

        public Barra(Long id, int longitudTotal) {
            this.id = id;
            this.longitudTotal = longitudTotal;
            this.espacioDisponible = longitudTotal;
        }

        public void asignarCorte(CorteDTO corte) {
            if (corte.getLongitud() <= espacioDisponible) {
                int posicion = longitudTotal - espacioDisponible;
                cortesAsignados.add(new CorteAsignado(corte, posicion));
                espacioDisponible -= corte.getLongitud();
            }
        }
    }

    /**
     * Clase interna para representar un corte asignado a una barra
     */
    private static class CorteAsignado {
        private final CorteDTO corte;
        private final int posicion; // Posición en mm desde el inicio de la barra

        public CorteAsignado(CorteDTO corte, int posicion) {
            this.corte = corte;
            this.posicion = posicion;
        }
    }
}