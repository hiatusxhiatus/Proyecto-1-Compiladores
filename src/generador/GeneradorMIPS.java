package generador;

import utils.Nodo;
import utils.TablaSimbolos;
import java.util.*;
import java.io.*;

public class GeneradorMIPS {
    private StringBuilder data;
    private StringBuilder text;
    private int contadorTemporales;
    private int contadorEtiquetas;
    private Map<String, Integer> offsetsVariables;
    private int offsetActual;
    private List<String> stringsLiterales;
    
    public GeneradorMIPS() {
        this.data = new StringBuilder();
        this.text = new StringBuilder();
        this.contadorTemporales = 0;
        this.contadorEtiquetas = 0;
        this.offsetsVariables = new HashMap<>();
        this.offsetActual = 0;
        this.stringsLiterales = new ArrayList<>();
    }
    
    public void generar(Nodo raiz, String archivoSalida) {
        System.out.println("\n[inicio] generacion codigo mips");
        
        inicializarData();
        generarText(raiz);
        
        escribirArchivo(archivoSalida);
        System.out.println("[exito] codigo mips generado: " + archivoSalida + "\n");
    }
    
    private void inicializarData() {
        data.append(".data\n");
        data.append("newline: .asciiz \"\\n\"\n");
    }
    
    private void generarText(Nodo raiz) {
        text.append("\n.text\n");
        text.append(".globl main\n\n");
        
        if (raiz != null) {
            for (Nodo hijo : raiz.getHijos()) {
                procesarNodo(hijo);
            }
        }
    }
    
    private void procesarNodo(Nodo nodo) {
        if (nodo == null) return;
        
        String tipo = nodo.getTipo();
        
        if (tipo.equals("MAIN")) {
            generarMain(nodo);
        } else if (tipo.equals("GIFT")) {
            generarFuncion(nodo);
        } else {
            for (Nodo hijo : nodo.getHijos()) {
                procesarNodo(hijo);
            }
        }
    }
    
    private void generarMain(Nodo nodo) {
        text.append("main:\n");
        text.append("    # prologo main\n");
        text.append("    sub $sp, $sp, 32\n");
        text.append("    sw $ra, 0($sp)\n\n");
        
        offsetActual = -4;
        offsetsVariables.clear();
        
        List<Nodo> hijos = nodo.getHijos();
        for (Nodo hijo : hijos) {
            if (hijo.getTipo().equals("BLOQUE")) {
                generarBloque(hijo);
            }
        }
        
        text.append("\n    # epilogo main\n");
        text.append("    lw $ra, 0($sp)\n");
        text.append("    add $sp, $sp, 32\n");
        text.append("    li $v0, 10\n");
        text.append("    syscall\n\n");
    }
    
    private void generarFuncion(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 3) return;
        
        String nombreFunc = hijos.get(2).getLexema();
        
        text.append(nombreFunc + ":\n");
        text.append("    # prologo funcion\n");
        text.append("    sub $sp, $sp, 32\n");
        text.append("    sw $ra, 0($sp)\n\n");
        
        offsetActual = -4;
        offsetsVariables.clear();
        
        for (Nodo hijo : hijos) {
            if (hijo.getTipo().equals("BLOQUE")) {
                generarBloque(hijo);
            }
        }
        
        text.append("\n    # epilogo funcion\n");
        text.append("    lw $ra, 0($sp)\n");
        text.append("    add $sp, $sp, 32\n");
        text.append("    jr $ra\n\n");
    }
    
    private void generarBloque(Nodo nodo) {
        for (Nodo hijo : nodo.getHijos()) {
            if (hijo.getTipo().equals("SENTENCIAS")) {
                for (Nodo sentencia : hijo.getHijos()) {
                    generarSentencia(sentencia);
                }
            }
        }
    }
    
    private void generarSentencia(Nodo nodo) {
        String tipo = nodo.getTipo();
        
        switch (tipo) {
            case "LOCAL":
            case "decl_local":
            case "decl_local_asign":
                generarDeclaracionLocal(nodo);
                break;
            case "ASIGNACION":
            case "asignacion":
                generarAsignacion(nodo);
                break;
            case "SHOW":
            case "show":
                generarShow(nodo);
                break;
            case "GET":
            case "get":
                generarGet(nodo);
                break;
            case "RETURN":
            case "return":
                generarReturn(nodo);
                break;
            case "DECIDE":
            case "decide_of":
            case "decide_of_else":
                generarDecide(nodo);
                break;
            case "FOR":
            case "for":
                generarFor(nodo);
                break;
        }
    }
    
    private void generarDeclaracionLocal(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 2) return;
        
        String tipoVar = hijos.get(0).getLexema();
        Nodo listaIds = hijos.get(1);
        
        if (listaIds.tieneHijos()) {
            for (Nodo idNode : listaIds.getHijos()) {
                String nombreVar = idNode.getLexema();
                offsetsVariables.put(nombreVar, offsetActual);
                offsetActual -= 4;
            }
        }
        
        if (nodo.getTipo().equals("decl_local_asign") && hijos.size() >= 4) {
            Nodo expr = hijos.get(3);
            String reg = generarExpresion(expr, tipoVar);
            
            String nombreVar = listaIds.getHijos().get(0).getLexema();
            int offset = offsetsVariables.get(nombreVar);
            
            if (tipoVar.equals("float")) {
                text.append("    s.s " + reg + ", " + offset + "($sp)\n");
            } else {
                text.append("    sw " + reg + ", " + offset + "($sp)\n");
            }
        }
    }
    
    private void generarAsignacion(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 3) return;
        
        String nombreVar = hijos.get(0).getLexema();
        Nodo expr = hijos.get(2);
        
        String tipoVar = inferirTipo(expr);
        String reg = generarExpresion(expr, tipoVar);
        
        Integer offset = offsetsVariables.get(nombreVar);
        if (offset != null) {
            if (tipoVar.equals("float")) {
                text.append("    s.s " + reg + ", " + offset + "($sp)\n");
            } else {
                text.append("    sw " + reg + ", " + offset + "($sp)\n");
            }
        }
    }
    
    private String generarExpresion(Nodo nodo, String tipoEsperado) {
        String tipo = nodo.getTipo();
        
        switch (tipo) {
            case "INT_LIT":
                String regInt = "$t" + (contadorTemporales % 8);
                contadorTemporales++;
                text.append("    li " + regInt + ", " + nodo.getLexema() + "\n");
                return regInt;
                
            case "FLOAT_LIT":
                String regFloat = "$f" + (contadorTemporales % 32);
                contadorTemporales++;
                text.append("    li.s " + regFloat + ", " + nodo.getLexema() + "\n");
                return regFloat;
                
            case "BOOL_LIT":
                String regBool = "$t" + (contadorTemporales % 8);
                contadorTemporales++;
                int valor = nodo.getLexema().equals("true") ? 1 : 0;
                text.append("    li " + regBool + ", " + valor + "\n");
                return regBool;
                
            case "ID":
                return cargarVariable(nodo.getLexema(), tipoEsperado);
                
            case "OP_SUMA":
            case "OP_RESTA":
            case "OP_MULT":
            case "OP_DIV":
            case "OP_DIV_ENTERA":
                return generarOperacionBinaria(nodo, tipoEsperado);
                
            case "OP_MAYOR":
            case "OP_MENOR_IGUAL":
            case "OP_IGUAL":
            case "OP_DIFERENTE":
                return generarComparacion(nodo, tipoEsperado);
                
            case "OP_NEGATIVO":
                return generarNegativo(nodo, tipoEsperado);
                
            case "OP_INCREMENTO":
                return generarIncremento(nodo, tipoEsperado);
                
            default:
                String regDef = "$t" + (contadorTemporales % 8);
                contadorTemporales++;
                text.append("    li " + regDef + ", 0\n");
                return regDef;
        }
    }
    
    private String cargarVariable(String nombre, String tipo) {
        Integer offset = offsetsVariables.get(nombre);
        
        if (tipo.equals("float")) {
            String reg = "$f" + (contadorTemporales % 32);
            contadorTemporales++;
            if (offset != null) {
                text.append("    l.s " + reg + ", " + offset + "($sp)\n");
            } else {
                text.append("    li.s " + reg + ", 0.0\n");
            }
            return reg;
        } else {
            String reg = "$t" + (contadorTemporales % 8);
            contadorTemporales++;
            if (offset != null) {
                text.append("    lw " + reg + ", " + offset + "($sp)\n");
            } else {
                text.append("    li " + reg + ", 0\n");
            }
            return reg;
        }
    }
    
    private String generarOperacionBinaria(Nodo nodo, String tipo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 2) {
            String reg = "$t0";
            text.append("    li " + reg + ", 0\n");
            return reg;
        }
        
        String reg1 = generarExpresion(hijos.get(0), tipo);
        String reg2 = generarExpresion(hijos.get(1), tipo);
        
        String tipoOp = nodo.getTipo();
        
        if (tipo.equals("float")) {
            String regRes = "$f" + (contadorTemporales % 32);
            contadorTemporales++;
            
            switch (tipoOp) {
                case "OP_SUMA":
                    text.append("    add.s " + regRes + ", " + reg1 + ", " + reg2 + "\n");
                    break;
                case "OP_RESTA":
                    text.append("    sub.s " + regRes + ", " + reg1 + ", " + reg2 + "\n");
                    break;
                case "OP_MULT":
                    text.append("    mul.s " + regRes + ", " + reg1 + ", " + reg2 + "\n");
                    break;
                case "OP_DIV":
                    text.append("    div.s " + regRes + ", " + reg1 + ", " + reg2 + "\n");
                    break;
            }
            return regRes;
        } else {
            String regRes = "$t" + (contadorTemporales % 8);
            contadorTemporales++;
            
            switch (tipoOp) {
                case "OP_SUMA":
                    text.append("    add " + regRes + ", " + reg1 + ", " + reg2 + "\n");
                    break;
                case "OP_RESTA":
                    text.append("    sub " + regRes + ", " + reg1 + ", " + reg2 + "\n");
                    break;
                case "OP_MULT":
                    text.append("    mult " + reg1 + ", " + reg2 + "\n");
                    text.append("    mflo " + regRes + "\n");
                    break;
                case "OP_DIV_ENTERA":
                    text.append("    div " + reg1 + ", " + reg2 + "\n");
                    text.append("    mflo " + regRes + "\n");
                    break;
            }
            return regRes;
        }
    }
    
    private String generarComparacion(Nodo nodo, String tipo) {
        List<Nodo> hijos = nodo.getHijos();
        String tipo1 = inferirTipo(hijos.get(0));
        
        String reg1 = generarExpresion(hijos.get(0), tipo1);
        String reg2 = generarExpresion(hijos.get(1), tipo1);
        
        String regRes = "$t" + (contadorTemporales % 8);
        contadorTemporales++;
        
        String tipoOp = nodo.getTipo();
        
        if (tipo1.equals("float")) {
            switch (tipoOp) {
                case "OP_MAYOR":
                    text.append("    c.lt.s " + reg2 + ", " + reg1 + "\n");
                    text.append("    bc1t L" + contadorEtiquetas + "\n");
                    text.append("    li " + regRes + ", 0\n");
                    text.append("    j L" + (contadorEtiquetas+1) + "\n");
                    text.append("L" + contadorEtiquetas + ":\n");
                    text.append("    li " + regRes + ", 1\n");
                    text.append("L" + (contadorEtiquetas+1) + ":\n");
                    contadorEtiquetas += 2;
                    break;
                case "OP_MENOR_IGUAL":
                    text.append("    c.le.s " + reg1 + ", " + reg2 + "\n");
                    text.append("    bc1t L" + contadorEtiquetas + "\n");
                    text.append("    li " + regRes + ", 0\n");
                    text.append("    j L" + (contadorEtiquetas+1) + "\n");
                    text.append("L" + contadorEtiquetas + ":\n");
                    text.append("    li " + regRes + ", 1\n");
                    text.append("L" + (contadorEtiquetas+1) + ":\n");
                    contadorEtiquetas += 2;
                    break;
                case "OP_IGUAL":
                    text.append("    c.eq.s " + reg1 + ", " + reg2 + "\n");
                    text.append("    bc1t L" + contadorEtiquetas + "\n");
                    text.append("    li " + regRes + ", 0\n");
                    text.append("    j L" + (contadorEtiquetas+1) + "\n");
                    text.append("L" + contadorEtiquetas + ":\n");
                    text.append("    li " + regRes + ", 1\n");
                    text.append("L" + (contadorEtiquetas+1) + ":\n");
                    contadorEtiquetas += 2;
                    break;
                case "OP_DIFERENTE":
                    text.append("    c.eq.s " + reg1 + ", " + reg2 + "\n");
                    text.append("    bc1f L" + contadorEtiquetas + "\n");
                    text.append("    li " + regRes + ", 0\n");
                    text.append("    j L" + (contadorEtiquetas+1) + "\n");
                    text.append("L" + contadorEtiquetas + ":\n");
                    text.append("    li " + regRes + ", 1\n");
                    text.append("L" + (contadorEtiquetas+1) + ":\n");
                    contadorEtiquetas += 2;
                    break;
            }
        } else {
            switch (tipoOp) {
                case "OP_MAYOR":
                    text.append("    slt " + regRes + ", " + reg2 + ", " + reg1 + "\n");
                    break;
                case "OP_MENOR_IGUAL":
                    text.append("    slt " + regRes + ", " + reg2 + ", " + reg1 + "\n");
                    text.append("    xori " + regRes + ", " + regRes + ", 1\n");
                    break;
                case "OP_IGUAL":
                    text.append("    seq " + regRes + ", " + reg1 + ", " + reg2 + "\n");
                    break;
                case "OP_DIFERENTE":
                    text.append("    sne " + regRes + ", " + reg1 + ", " + reg2 + "\n");
                    break;
            }
        }
        
        return regRes;
    }
    
    private String generarNegativo(Nodo nodo, String tipo) {
        List<Nodo> hijos = nodo.getHijos();
        String reg = generarExpresion(hijos.get(0), tipo);
        
        if (tipo.equals("float")) {
            String regRes = "$f" + (contadorTemporales % 32);
            contadorTemporales++;
            text.append("    neg.s " + regRes + ", " + reg + "\n");
            return regRes;
        } else {
            String regRes = "$t" + (contadorTemporales % 8);
            contadorTemporales++;
            text.append("    neg " + regRes + ", " + reg + "\n");
            return regRes;
        }
    }
    
    private String generarIncremento(Nodo nodo, String tipo) {
        List<Nodo> hijos = nodo.getHijos();
        String nombreVar = hijos.get(0).getLexema();
        
        String reg = cargarVariable(nombreVar, tipo);
        String regRes = "$t" + (contadorTemporales % 8);
        contadorTemporales++;
        
        text.append("    addi " + regRes + ", " + reg + ", 1\n");
        
        Integer offset = offsetsVariables.get(nombreVar);
        if (offset != null) {
            text.append("    sw " + regRes + ", " + offset + "($sp)\n");
        }
        
        return regRes;
    }
    
    private void generarShow(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.isEmpty()) return;
        
        Nodo args = hijos.get(0);
        if (!args.tieneHijos()) return;
        
        for (Nodo arg : args.getHijos()) {
            String tipo = arg.getTipo();
            
            if (tipo.equals("STRING_LIT")) {
                String label = "str" + stringsLiterales.size();
                String contenido = arg.getLexema().replace("\"", "");
                data.append(label + ": .asciiz " + arg.getLexema() + "\n");
                stringsLiterales.add(label);
                
                text.append("    la $a0, " + label + "\n");
                text.append("    li $v0, 4\n");
                text.append("    syscall\n");
            } else {
                String tipoExpr = inferirTipo(arg);
                String reg = generarExpresion(arg, tipoExpr);
                
                if (tipoExpr.equals("float")) {
                    text.append("    mov.s $f12, " + reg + "\n");
                    text.append("    li $v0, 2\n");
                    text.append("    syscall\n");
                } else if (tipoExpr.equals("bool")) {
                    text.append("    move $a0, " + reg + "\n");
                    text.append("    li $v0, 1\n");
                    text.append("    syscall\n");
                } else {
                    text.append("    move $a0, " + reg + "\n");
                    text.append("    li $v0, 1\n");
                    text.append("    syscall\n");
                }
            }
            
            text.append("    la $a0, newline\n");
            text.append("    li $v0, 4\n");
            text.append("    syscall\n");
        }
    }
    
    private void generarGet(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.isEmpty()) return;
        
        String nombreVar = hijos.get(0).getLexema();
        Integer offset = offsetsVariables.get(nombreVar);
        
        text.append("    li $v0, 5\n");
        text.append("    syscall\n");
        
        if (offset != null) {
            text.append("    sw $v0, " + offset + "($sp)\n");
        }
    }
    
    private void generarReturn(Nodo nodo) {
        if (nodo.tieneHijos()) {
            String tipo = inferirTipo(nodo.getHijos().get(0));
            String reg = generarExpresion(nodo.getHijos().get(0), tipo);
            
            if (tipo.equals("float")) {
                text.append("    mov.s $f0, " + reg + "\n");
            } else {
                text.append("    move $v0, " + reg + "\n");
            }
        }
    }
    
    private void generarDecide(Nodo nodo) {
        int etiqDecide = contadorEtiquetas++;
        int etiqEnd = contadorEtiquetas++;
        
        List<Nodo> casos = new ArrayList<>();
        Nodo bloqueElse = null;
        
        for (Nodo hijo : nodo.getHijos()) {
            if (hijo.getTipo().equals("CASOS") || hijo.getTipo().equals("casos")) {
                casos.addAll(hijo.getHijos());
            } else if (hijo.getTipo().equals("ELSE") || hijo.getTipo().equals("else")) {
                if (hijo.tieneHijos()) {
                    bloqueElse = hijo.getHijos().get(0);
                }
            }
        }
        
        int casoNum = 0;
        for (Nodo caso : casos) {
            List<Nodo> hijosCaso = caso.getHijos();
            if (hijosCaso.size() < 2) continue;
            
            int etiqCaso = contadorEtiquetas++;
            int etiqSiguiente = contadorEtiquetas++;
            
            String regCond = generarExpresion(hijosCaso.get(0), "bool");
            text.append("    beqz " + regCond + ", L" + etiqSiguiente + "\n");
            
            text.append("L" + etiqCaso + ":\n");
            generarBloque(hijosCaso.get(1));
            text.append("    j L" + etiqEnd + "\n");
            
            text.append("L" + etiqSiguiente + ":\n");
            casoNum++;
        }
        
        if (bloqueElse != null) {
            generarBloque(bloqueElse);
        }
        
        text.append("L" + etiqEnd + ":\n");
    }
    
    private void generarFor(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 4) return;
        
        int etiqCond = contadorEtiquetas++;
        int etiqBloque = contadorEtiquetas++;
        int etiqInc = contadorEtiquetas++;
        int etiqEnd = contadorEtiquetas++;
        
        generarSentencia(hijos.get(0));
        
        text.append("L" + etiqCond + ":\n");
        String regCond = generarExpresion(hijos.get(1), "bool");
        text.append("    beqz " + regCond + ", L" + etiqEnd + "\n");
        
        text.append("L" + etiqBloque + ":\n");
        generarBloque(hijos.get(3));
        
        text.append("L" + etiqInc + ":\n");
        generarExpresion(hijos.get(2), "int");
        text.append("    j L" + etiqCond + "\n");
        
        text.append("L" + etiqEnd + ":\n");
    }
    
    private String inferirTipo(Nodo nodo) {
        String tipo = nodo.getTipo();
        
        switch (tipo) {
            case "INT_LIT": return "int";
            case "FLOAT_LIT": return "float";
            case "BOOL_LIT": return "bool";
            case "STRING_LIT": return "string";
            default: return "int";
        }
    }
    
    private void escribirArchivo(String nombreArchivo) {
        try {
            PrintWriter writer = new PrintWriter(nombreArchivo, "UTF-8");
            writer.println(data.toString());
            writer.println(text.toString());
            writer.close();
        } catch (Exception e) {
            System.err.println("[error] al escribir archivo: " + e.getMessage());
        }
    }
}