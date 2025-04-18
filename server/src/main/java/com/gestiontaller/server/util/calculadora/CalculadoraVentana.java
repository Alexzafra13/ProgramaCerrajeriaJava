package com.gestiontaller.server.util.calculadora;

import com.gestiontaller.server.dto.calculo.CorteDTO;
import com.gestiontaller.server.dto.calculo.MaterialAdicionalDTO;
import com.gestiontaller.server.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.server.model.TipoCristal;
import com.gestiontaller.server.model.presupuesto.TipoPresupuesto;
import com.gestiontaller.server.model.serie.PerfilSerie;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.model.serie.TipoPerfil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CalculadoraVentana {

    /**
     * Método principal de cálculo que redirige al método específico para ALUPROM-21
     * Mantiene la interfaz compatible con el código existente pero se centra solo en ALUPROM-21
     */
    public ResultadoCalculoDTO calcularVentana(
            Integer ancho,
            Integer alto,
            Integer numeroHojas,
            Boolean incluyePersiana,
            Integer alturaCajon,
            Boolean altoEsTotal,
            SerieBase serie,
            List<PerfilSerie> perfiles) {

        // Ignoramos el parámetro numeroHojas y usamos siempre 2 hojas para ALUPROM-21
        return calcularVentanaCorredoraAluprom21(
                ancho,
                alto,
                incluyePersiana,
                alturaCajon,
                altoEsTotal,
                TipoCristal.SIMPLE, // Por defecto
                serie,
                perfiles
        );
    }

    /**
     * Calcula los cortes para la ventana corredera específica de la serie ALUPROM-21
     * Esta serie siempre tiene 2 hojas y utiliza unas reglas específicas de descuento.
     */
    public ResultadoCalculoDTO calcularVentanaCorredoraAluprom21(
            Integer ancho,
            Integer alto,
            Boolean incluyePersiana,
            Integer alturaCajon,
            Boolean altoEsTotal,
            TipoCristal tipoCristal,
            SerieBase serie,
            List<PerfilSerie> perfiles) {

        ResultadoCalculoDTO resultado = new ResultadoCalculoDTO();
        List<CorteDTO> cortes = new ArrayList<>();
        List<MaterialAdicionalDTO> materiales = new ArrayList<>();

        // Establecemos que es una ventana de 2 hojas siempre para ALUPROM-21
        int numeroHojas = 2;

        // Calcular dimensiones considerando altura de persiana
        int alturaVentana = alto;
        int alturaTotal = alto;

        if (incluyePersiana && alturaCajon != null && alturaCajon > 0) {
            if (altoEsTotal) {
                alturaVentana = alto - alturaCajon;
                alturaTotal = alto;
            } else {
                alturaVentana = alto;
                alturaTotal = alto + alturaCajon;
            }
        }

        // Agrupar perfiles por código para facilitar su acceso
        Map<String, PerfilSerie> perfilesPorCodigo = perfiles.stream()
                .collect(Collectors.toMap(PerfilSerie::getCodigo, p -> p, (p1, p2) -> p1));

        // Obtener perfiles específicos
        PerfilSerie perfilMarcoLateral = obtenerPerfil(perfilesPorCodigo, "ALUPROM21-ML", TipoPerfil.MARCO, perfiles);
        PerfilSerie perfilMarcoSuperior = obtenerPerfil(perfilesPorCodigo, "ALUPROM21-MS", TipoPerfil.MARCO, perfiles);
        PerfilSerie perfilMarcoInferior = obtenerPerfil(perfilesPorCodigo, "ALUPROM21-MI", TipoPerfil.MARCO, perfiles);
        PerfilSerie perfilHojaLateral = obtenerPerfil(perfilesPorCodigo, "ALUPROM21-HL", TipoPerfil.HOJA, perfiles);
        PerfilSerie perfilHojaCentral = obtenerPerfil(perfilesPorCodigo, "ALUPROM21-HC", TipoPerfil.HOJA, perfiles);
        PerfilSerie perfilHojaRuleta = obtenerPerfil(perfilesPorCodigo, "ALUPROM21-HR", TipoPerfil.HOJA, perfiles);

        // Aplicar los descuentos específicos para ALUPROM-21

        // Marco lateral (altura sin descuento)
        int largoMarcoLateral = alturaTotal;
        cortes.add(crearCorte(perfilMarcoLateral, largoMarcoLateral, 2, "Marco lateral", 0));

        // Marco superior e inferior (ancho - 4.1 cm)
        int largoMarcoHorizontal = ancho - 41; // 4.1 cm = 41 mm
        cortes.add(crearCorte(perfilMarcoSuperior, largoMarcoHorizontal, 1, "Marco superior", 41));
        cortes.add(crearCorte(perfilMarcoInferior, largoMarcoHorizontal, 1, "Marco inferior", 41));

        // Hoja lateral (altura - 5.3 cm)
        int largoHojaLateral = alturaVentana - 53; // 5.3 cm = 53 mm
        cortes.add(crearCorte(perfilHojaLateral, largoHojaLateral, 2, "Hoja lateral", 53));

        // Hoja central (altura - 5.3 cm)
        int largoHojaCentral = alturaVentana - 53; // 5.3 cm = 53 mm
        cortes.add(crearCorte(perfilHojaCentral, largoHojaCentral, 2, "Hoja central", 53));

        // Hoja ruleta (ancho de hoja = ancho total / 2 - 2 cm)
        int anchoHoja = (ancho / 2) - 20; // 2 cm = 20 mm
        cortes.add(crearCorte(perfilHojaRuleta, anchoHoja, 4, "Hoja ruleta", 20));

        // Calcular materiales adicionales (herrajes, burletes, etc.)
        calcularMaterialesAdicionalesCorredora(materiales, ancho, alturaVentana, numeroHojas);

        // Añadir materiales específicos según el tipo de cristal
        agregarMaterialesPorTipoCristal(materiales, tipoCristal, ancho, alturaVentana);

        // Configurar resultado
        resultado.setTipoPresupuesto(TipoPresupuesto.VENTANA_CORREDERA);
        resultado.setAncho(ancho);
        resultado.setAlto(alturaTotal);
        resultado.setAnchoVentana(ancho);
        resultado.setAltoVentana(alturaVentana);
        resultado.setNumeroHojas(numeroHojas);
        resultado.setIncluyePersiana(incluyePersiana);
        resultado.setAlturaCajon(incluyePersiana ? alturaCajon : null);
        resultado.setCortes(cortes);
        resultado.setMaterialesAdicionales(materiales);
        resultado.setTipoCristal(tipoCristal);

        // Calcular precio total
        double precioTotal = calcularPrecioTotal(cortes, materiales);
        resultado.setPrecioTotal(precioTotal);

        // Añadir resumen e información sobre el tipo de cristal
        String descripcionPersiana = incluyePersiana ? " con persiana" : "";
        String tipoCristalDesc = tipoCristal == TipoCristal.SIMPLE ?
                "vidrio simple" : "cristal climalit doble";

        resultado.setResumen(String.format("Ventana Corredera ALUPROM-21 %dx%dmm%s con %s",
                ancho, alturaTotal, descripcionPersiana, tipoCristalDesc));

        return resultado;
    }

    /**
     * Método auxiliar para obtener un perfil por código o tipo
     */
    private PerfilSerie obtenerPerfil(Map<String, PerfilSerie> perfilesPorCodigo, String codigo, TipoPerfil tipo, List<PerfilSerie> todosPerfiles) {
        // Primero intentamos por código específico
        PerfilSerie perfil = perfilesPorCodigo.get(codigo);

        // Si no lo encontramos, buscamos uno genérico del mismo tipo
        if (perfil == null) {
            perfil = todosPerfiles.stream()
                    .filter(p -> p.getTipoPerfil() == tipo)
                    .findFirst()
                    .orElse(null);

            // Si aún no encontramos, creamos un perfil "dummy" para que no falle el cálculo
            if (perfil == null) {
                PerfilSerie perfilDummy = new PerfilSerie();
                perfilDummy.setId(1L); // ID ficticio
                perfilDummy.setCodigo(codigo);
                perfilDummy.setNombre("Perfil " + tipo.name());
                perfilDummy.setTipoPerfil(tipo);
                perfilDummy.setPesoMetro(1.0);
                perfilDummy.setPrecioMetro(15.0);
                return perfilDummy;
            }
        }

        return perfil;
    }

    /**
     * Crea un DTO para un corte específico
     */
    private CorteDTO crearCorte(PerfilSerie perfil, int longitud, int cantidad, String descripcion, int descuentoAplicado) {
        CorteDTO corte = new CorteDTO();
        corte.setPerfilId(perfil.getId());
        corte.setCodigoPerfil(perfil.getCodigo());
        corte.setNombrePerfil(perfil.getNombre());
        corte.setTipoPerfil(perfil.getTipoPerfil());
        corte.setLongitud(longitud);
        corte.setCantidad(cantidad);
        corte.setDescripcion(descripcion);
        corte.setDescuentoAplicado(descuentoAplicado);
        return corte;
    }

    /**
     * Método para calcular materiales adicionales para ventanas correderas
     */
    private void calcularMaterialesAdicionalesCorredora(
            List<MaterialAdicionalDTO> materiales,
            int ancho,
            int alto,
            int numeroHojas) {

        // Rodamientos (un juego por hoja)
        MaterialAdicionalDTO rodamientos = new MaterialAdicionalDTO();
        rodamientos.setDescripcion("Juego de rodamientos");
        rodamientos.setCantidad(numeroHojas);
        rodamientos.setPrecioUnitario(8.50); // Precio por juego
        rodamientos.setPrecioTotal(rodamientos.getCantidad() * rodamientos.getPrecioUnitario());
        materiales.add(rodamientos);

        // Felpa para el perímetro de las hojas
        double metrosFelpa = ((ancho / numeroHojas) * 2 + alto * 2) * numeroHojas / 1000.0;
        MaterialAdicionalDTO felpa = new MaterialAdicionalDTO();
        felpa.setDescripcion("Felpa (metros)");
        felpa.setCantidad((int)Math.ceil(metrosFelpa));
        felpa.setPrecioUnitario(0.80); // Precio por metro
        felpa.setPrecioTotal(felpa.getCantidad() * felpa.getPrecioUnitario());
        materiales.add(felpa);

        // Cierre (uno por ventana en general)
        MaterialAdicionalDTO cierre = new MaterialAdicionalDTO();
        cierre.setDescripcion("Cierre ventana corredera");
        cierre.setCantidad(1);
        cierre.setPrecioUnitario(12.30);
        cierre.setPrecioTotal(cierre.getCantidad() * cierre.getPrecioUnitario());
        materiales.add(cierre);

        // Tornillos (aproximadamente 20 por hoja)
        MaterialAdicionalDTO tornillos = new MaterialAdicionalDTO();
        tornillos.setDescripcion("Tornillos autorroscantes 3.5x16mm");
        tornillos.setCantidad(20 * numeroHojas);
        tornillos.setPrecioUnitario(0.03);
        tornillos.setPrecioTotal(tornillos.getCantidad() * tornillos.getPrecioUnitario());
        materiales.add(tornillos);

        // Si hay más de una hoja, añadir tope
        if (numeroHojas > 1) {
            MaterialAdicionalDTO tope = new MaterialAdicionalDTO();
            tope.setDescripcion("Tope cruce hojas");
            tope.setCantidad(numeroHojas - 1);
            tope.setPrecioUnitario(3.20);
            tope.setPrecioTotal(tope.getCantidad() * tope.getPrecioUnitario());
            materiales.add(tope);
        }
    }

    /**
     * Añade materiales específicos según el tipo de cristal seleccionado
     */
    private void agregarMaterialesPorTipoCristal(List<MaterialAdicionalDTO> materiales,
                                                 TipoCristal tipoCristal,
                                                 int ancho,
                                                 int alto) {
        // Calcular metros cuadrados de cristal
        double metrosCuadrados = (ancho / 1000.0) * (alto / 1000.0);
        metrosCuadrados = Math.ceil(metrosCuadrados * 10) / 10.0; // Redondear a 1 decimal

        MaterialAdicionalDTO cristal = new MaterialAdicionalDTO();

        if (tipoCristal == TipoCristal.SIMPLE) {
            cristal.setDescripcion("Vidrio simple 4mm");
            cristal.setCantidad((int)Math.ceil(metrosCuadrados));
            cristal.setPrecioUnitario(15.0); // Precio por m²
        } else {
            cristal.setDescripcion("Vidrio doble climalit 4-12-4");
            cristal.setCantidad((int)Math.ceil(metrosCuadrados));
            cristal.setPrecioUnitario(30.0); // Precio por m²
        }

        cristal.setPrecioTotal(cristal.getCantidad() * cristal.getPrecioUnitario());
        materiales.add(cristal);

        // Si es doble cristal, añadir juntas adicionales
        if (tipoCristal == TipoCristal.CLIMALIT) {
            MaterialAdicionalDTO juntaClimalit = new MaterialAdicionalDTO();
            juntaClimalit.setDescripcion("Junta especial climalit (metros)");
            juntaClimalit.setCantidad((int)Math.ceil((ancho * 2 + alto * 2) / 1000.0));
            juntaClimalit.setPrecioUnitario(1.2);
            juntaClimalit.setPrecioTotal(juntaClimalit.getCantidad() * juntaClimalit.getPrecioUnitario());
            materiales.add(juntaClimalit);
        }
    }

    /**
     * Método para calcular el precio total de la ventana
     */
    private double calcularPrecioTotal(List<CorteDTO> cortes, List<MaterialAdicionalDTO> materiales) {
        // Calcular el costo de los cortes (metros de perfil * precio/metro)
        double costoPerfiles = cortes.stream()
                .mapToDouble(c -> (c.getLongitud() / 1000.0) * c.getCantidad() * 20.0) // 20.0 es un precio por metro ficticio
                .sum();

        // Calcular el costo de los materiales adicionales
        double costoMateriales = materiales.stream()
                .mapToDouble(m -> m.getPrecioTotal() != null ? m.getPrecioTotal() : 0.0)
                .sum();

        // Sumar ambos costos
        return costoPerfiles + costoMateriales;
    }
}