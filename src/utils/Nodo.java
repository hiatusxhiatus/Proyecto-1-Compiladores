package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Nodo para construccion del arbol sintactico
 * Basado en la estrategia del profesor
 */
public class Nodo {
    private String lexema;
    private String tipo;
    private List<Nodo> hijos;
    
    // Constructor basico
    public Nodo(String lexema, String tipo) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.hijos = new ArrayList<>();
    }
    
    // Constructor con hijos
    public Nodo(String lexema, String tipo, List<Nodo> hijos) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.hijos = hijos != null ? hijos : new ArrayList<>();
    }
    
    // Getters
    public String getLexema() {
        return lexema;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public List<Nodo> getHijos() {
        return hijos;
    }
    
    // Setters
    public void setLexema(String lexema) {
        this.lexema = lexema;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public void setHijos(List<Nodo> hijos) {
        this.hijos = hijos;
    }
    
    // Agregar un hijo
    public void agregarHijo(Nodo hijo) {
        if (hijo != null) {
            this.hijos.add(hijo);
        }
    }
    
    // Verificar si tiene hijos
    public boolean tieneHijos() {
        return !hijos.isEmpty();
    }
    
    // Imprimir arbol (version basica como la del profesor)
    public void imprimirArbol(String indentacion, boolean esUltimo) {
        System.out.print(indentacion);
        
        if (esUltimo) {
            System.out.print("└─ ");
            indentacion += "   ";
        } else {
            System.out.print("├─ ");
            indentacion += "│  ";
        }
        
        System.out.println(tipo + ": " + lexema);
        
        int cantHijos = hijos.size();
        for (int i = 0; i < cantHijos; i++) {
            hijos.get(i).imprimirArbol(indentacion, i == cantHijos - 1);
        }
    }
    
    // Sobrecarga para llamada inicial
    public void imprimirArbol() {
        System.out.println("\n=== ARBOL SINTACTICO ===");
        System.out.println(tipo + ": " + lexema);
        int cantHijos = hijos.size();
        for (int i = 0; i < cantHijos; i++) {
            hijos.get(i).imprimirArbol("", i == cantHijos - 1);
        }
        System.out.println("========================\n");
    }
    
    @Override
    public String toString() {
        return "Nodo{" + 
               "tipo='" + tipo + '\'' + 
               ", lexema='" + lexema + '\'' + 
               ", hijos=" + hijos.size() + 
               '}';
    }
}