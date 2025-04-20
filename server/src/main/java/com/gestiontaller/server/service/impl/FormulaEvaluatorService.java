package com.gestiontaller.server.service.impl;

import org.springframework.stereotype.Service;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

/**
 * Servicio para evaluar fórmulas y expresiones matemáticas
 * Trabaja con valores en centímetros para cálculos de ventanas
 */
@Service
public class FormulaEvaluatorService {

    private final ScriptEngine jsEngine;

    public FormulaEvaluatorService() {
        this.jsEngine = new ScriptEngineManager().getEngineByName("javascript");
    }

    /**
     * Evalúa una fórmula JavaScript con las variables proporcionadas
     * Las variables y el resultado se interpretan como valores en centímetros
     *
     * @param formula La fórmula a evaluar
     * @param variables Mapa de variables a usar en la evaluación (en centímetros)
     * @return El resultado como Double (en centímetros)
     */
    public Double evaluarFormula(String formula, Map<String, Object> variables) {
        try {
            if (formula == null || formula.trim().isEmpty()) {
                return null;
            }

            Bindings bindings = jsEngine.createBindings();
            variables.forEach(bindings::put);

            Object result = jsEngine.eval(formula, bindings);
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            } else if (result instanceof String) {
                return Double.parseDouble((String) result);
            } else {
                throw new RuntimeException("El resultado de la fórmula no es un número: " + result);
            }
        } catch (ScriptException e) {
            throw new RuntimeException("Error al evaluar fórmula: " + formula + "\nError: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("No se pudo convertir el resultado a número: " + e.getMessage(), e);
        }
    }

    /**
     * Evalúa una fórmula JavaScript con las variables proporcionadas
     * Las variables se interpretan como centímetros, y el resultado se redondea a un entero
     *
     * @param formula La fórmula a evaluar
     * @param variables Mapa de variables a usar en la evaluación (en centímetros)
     * @return El resultado como Integer
     */
    public Integer evaluarFormulaEntero(String formula, Map<String, Object> variables) {
        Double resultado = evaluarFormula(formula, variables);
        return resultado != null ? (int) Math.round(resultado) : null;
    }

    /**
     * Evalúa una condición JavaScript (expresión booleana)
     *
     * @param condicion La condición a evaluar
     * @param variables Mapa de variables a usar en la evaluación
     * @return El resultado como Boolean
     */
    public Boolean evaluarCondicion(String condicion, Map<String, Object> variables) {
        try {
            if (condicion == null || condicion.trim().isEmpty()) {
                return true;
            }

            Bindings bindings = jsEngine.createBindings();
            variables.forEach(bindings::put);

            Object result = jsEngine.eval(condicion, bindings);
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else {
                return Boolean.valueOf(result.toString());
            }
        } catch (ScriptException e) {
            throw new RuntimeException("Error al evaluar condición: " + condicion + "\nError: " + e.getMessage(), e);
        }
    }
}