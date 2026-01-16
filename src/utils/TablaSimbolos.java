package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase TablaSimbolos para gestionar tokens por scope
 */
public class TablaSimbolos {
    private String nombreScope;
    private Map<String, TokenInfo> simbolos;
    
    public TablaSimbolos(String nombreScope) {
        this.nombreScope = nombreScope;
        this.simbolos = new HashMap<>();
    }
    
    // Agregar un simbolo
    public void agregar(String nombre, String tipo, int linea, int columna) {
        simbolos.put(nombre, new TokenInfo(nombre, tipo, linea, columna));
    }
    
    // Verificar si existe un simbolo
    public boolean existe(String nombre) {
        return simbolos.containsKey(nombre);
    }
    
    // Obtener informacion de un simbolo
    public TokenInfo obtener(String nombre) {
        return simbolos.get(nombre);
    }
    
    // Obtener nombre del scope
    public String getNombreScope() {
        return nombreScope;
    }
    
    // Imprimir tabla
    public void imprimir() {
        System.out.println("\n--- Tabla de Simbolos: " + nombreScope + " ---");
        System.out.printf("%-20s %-15s %-10s %-10s\n", "NOMBRE", "TIPO", "LINEA", "COLUMNA");
        System.out.println("---------------------------------------------------------------");
        
        for (TokenInfo info : simbolos.values()) {
            System.out.printf("%-20s %-15s %-10d %-10d\n", 
                info.nombre, info.tipo, info.linea, info.columna);
        }
        
        System.out.println("---------------------------------------------------------------");
        System.out.println("Total de simbolos: " + simbolos.size() + "\n");
    }
    
    // Clase interna para informacion de tokens
    public static class TokenInfo {
        public String nombre;
        public String tipo;
        public int linea;
        public int columna;
        
        public TokenInfo(String nombre, String tipo, int linea, int columna) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.linea = linea;
            this.columna = columna;
        }
        
        @Override
        public String toString() {
            return "TokenInfo{" +
                   "nombre='" + nombre + '\'' +
                   ", tipo='" + tipo + '\'' +
                   ", linea=" + linea +
                   ", columna=" + columna +
                   '}';
        }
    }
}