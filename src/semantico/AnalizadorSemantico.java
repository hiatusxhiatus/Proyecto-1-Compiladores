package semantico;

import utils.Nodo;
import utils.TablaSimbolos;
import java.util.*;

public class AnalizadorSemantico {
    private List<TablaSimbolos> tablasSimbolos;
    private TablaSimbolos tablaActual;
    private List<String> errores;
    private Map<String, FuncionInfo> tablaFunciones;
    
    public AnalizadorSemantico(List<TablaSimbolos> tablas) {
        this.tablasSimbolos = tablas;
        this.errores = new ArrayList<>();
        this.tablaFunciones = new HashMap<>();
    }
    
    public boolean analizar(Nodo raiz) {
        System.out.println("\n[inicio] analisis semantico");
        
        // construir tabla de funciones primero
        construirTablaFunciones(raiz);
        
        // validar todo el arbol
        validarNodo(raiz);
        
        // reportar errores
        if (!errores.isEmpty()) {
            System.out.println("\n[errores semanticos encontrados]");
            for (String error : errores) {
                System.err.println(error);
            }
            return false;
        }
        
        System.out.println("[exito] analisis semantico completado\n");
        return true;
    }
    
    private void construirTablaFunciones(Nodo raiz) {
        if (raiz == null) return;
        
        // buscar nodos de tipo gift (funciones)
        if (raiz.getTipo().equals("GIFT")) {
            List<Nodo> hijos = raiz.getHijos();
            if (hijos.size() >= 3) {
                String tipoRetorno = hijos.get(1).getLexema(); // tipo
                String nombreFunc = hijos.get(2).getLexema();  // id
                
                List<String> tiposParams = new ArrayList<>();
                Nodo params = hijos.get(3); // parametros
                
                if (params.tieneHijos()) {
                    for (Nodo param : params.getHijos()) {
                        if (param.tieneHijos() && param.getHijos().size() >= 1) {
                            tiposParams.add(param.getHijos().get(0).getLexema());
                        }
                    }
                }
                
                tablaFunciones.put(nombreFunc, 
                    new FuncionInfo(nombreFunc, tipoRetorno, tiposParams));
            }
        }
        
        // recursivo para todos los hijos
        for (Nodo hijo : raiz.getHijos()) {
            construirTablaFunciones(hijo);
        }
    }
    
    private String validarNodo(Nodo nodo) {
        if (nodo == null) return "void";
        
        String tipo = nodo.getTipo();
        
        switch (tipo) {
            case "OP_SUMA":
            case "OP_RESTA":
            case "OP_MULT":
            case "OP_DIV":
            case "OP_DIV_ENTERA":
            case "OP_POTENCIA":
                return validarOperacionAritmetica(nodo);
                
            case "OP_MAYOR":
            case "OP_MENOR_IGUAL":
            case "OP_IGUAL":
            case "OP_DIFERENTE":
                return validarOperacionRelacional(nodo);
                
            case "OP_AND":
                return validarOperacionLogica(nodo);
                
            case "OP_INCREMENTO":
            case "OP_NEGATIVO":
                return validarOperacionUnaria(nodo);
                
            case "ASIGNACION":
                validarAsignacion(nodo);
                return "void";
                
            case "LLAMADA_FUNC":
                return validarLlamadaFuncion(nodo);
                
            case "DECIDE":
                validarDecide(nodo);
                return "void";
                
            case "FOR":
                validarFor(nodo);
                return "void";
                
            case "return":
                validarReturn(nodo);
                return "void";
                
            case "INT_LIT":
                return "int";
            case "FLOAT_LIT":
                return "float";
            case "BOOL_LIT":
                return "bool";
            case "STRING_LIT":
                return "string";
                
            case "ID":
                return validarIdentificador(nodo);
                
            case "ARRAY_ACCESS":
                return validarAccesoArreglo(nodo);
                
            default:
                // recursivo para hijos
                for (Nodo hijo : nodo.getHijos()) {
                    validarNodo(hijo);
                }
                return "void";
        }
    }
    
    private String validarOperacionAritmetica(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 2) {
            errores.add("[error] operacion aritmetica requiere 2 operandos");
            return "error";
        }
        
        String tipo1 = validarNodo(hijos.get(0));
        String tipo2 = validarNodo(hijos.get(1));
        
        if (tipo1.equals("error") || tipo2.equals("error")) {
            return "error";
        }
        
        // tipado fuerte: deben ser iguales
        if (!tipo1.equals(tipo2)) {
            errores.add(String.format(
                "[error] operacion aritmetica entre tipos incompatibles: %s y %s",
                tipo1, tipo2));
            return "error";
        }
        
        // solo int o float permitidos
        if (!tipo1.equals("int") && !tipo1.equals("float")) {
            errores.add(String.format(
                "[error] operacion aritmetica no soportada para tipo: %s", tipo1));
            return "error";
        }
        
        // division entera solo para int
        if (nodo.getTipo().equals("OP_DIV_ENTERA") && !tipo1.equals("int")) {
            errores.add("[error] division entera solo para tipo int");
            return "error";
        }
        
        // potencia solo para int
        if (nodo.getTipo().equals("OP_POTENCIA") && !tipo1.equals("int")) {
            errores.add("[error] potencia solo para tipo int");
            return "error";
        }
        
        return tipo1;
    }
    
    private String validarOperacionRelacional(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 2) {
            errores.add("[error] operacion relacional requiere 2 operandos");
            return "error";
        }
        
        String tipo1 = validarNodo(hijos.get(0));
        String tipo2 = validarNodo(hijos.get(1));
        
        if (tipo1.equals("error") || tipo2.equals("error")) {
            return "error";
        }
        
        // igual y diferente permiten bool tambien
        if (nodo.getTipo().equals("OP_IGUAL") || nodo.getTipo().equals("OP_DIFERENTE")) {
            if (!tipo1.equals(tipo2)) {
                errores.add(String.format(
                    "[error] comparacion entre tipos incompatibles: %s y %s",
                    tipo1, tipo2));
                return "error";
            }
            return "bool";
        }
        
        // mayor, menor_igual solo int o float
        if (!tipo1.equals(tipo2)) {
            errores.add(String.format(
                "[error] comparacion entre tipos incompatibles: %s y %s",
                tipo1, tipo2));
            return "error";
        }
        
        if (!tipo1.equals("int") && !tipo1.equals("float")) {
            errores.add(String.format(
                "[error] comparacion no soportada para tipo: %s", tipo1));
            return "error";
        }
        
        return "bool";
    }
    
    private String validarOperacionLogica(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 2) {
            errores.add("[error] operacion logica requiere 2 operandos");
            return "error";
        }
        
        String tipo1 = validarNodo(hijos.get(0));
        String tipo2 = validarNodo(hijos.get(1));
        
        if (!tipo1.equals("bool") || !tipo2.equals("bool")) {
            errores.add(String.format(
                "[error] operacion logica requiere operandos bool, encontrados: %s y %s",
                tipo1, tipo2));
            return "error";
        }
        
        return "bool";
    }
    
    private String validarOperacionUnaria(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.isEmpty()) {
            errores.add("[error] operacion unaria requiere operando");
            return "error";
        }
        
        String tipoOperando = validarNodo(hijos.get(0));
        
        if (nodo.getTipo().equals("OP_INCREMENTO")) {
            if (!tipoOperando.equals("int") && !tipoOperando.equals("float")) {
                errores.add("[error] incremento solo para int o float");
                return "error";
            }
            return tipoOperando;
        }
        
        if (nodo.getTipo().equals("OP_NEGATIVO")) {
            if (!tipoOperando.equals("int") && !tipoOperando.equals("float")) {
                errores.add("[error] negativo solo para int o float");
                return "error";
            }
            return tipoOperando;
        }
        
        return tipoOperando;
    }
    
    private void validarAsignacion(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 3) return;
        
        String nombreVar = hijos.get(0).getLexema();
        String tipoExpr = validarNodo(hijos.get(2));
        
        // buscar variable en tablas
        String tipoVar = buscarTipoVariable(nombreVar);
        
        if (tipoVar == null) {
            errores.add(String.format(
                "[error] variable '%s' no declarada", nombreVar));
            return;
        }
        
        if (!tipoVar.equals(tipoExpr)) {
            errores.add(String.format(
                "[error] asignacion de tipo %s a variable tipo %s",
                tipoExpr, tipoVar));
        }
    }
    
    private String validarLlamadaFuncion(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.isEmpty()) return "error";
        
        String nombreFunc = hijos.get(0).getLexema();
        FuncionInfo func = tablaFunciones.get(nombreFunc);
        
        if (func == null) {
            errores.add(String.format(
                "[error] funcion '%s' no declarada", nombreFunc));
            return "error";
        }
        
        // validar argumentos
        List<String> tiposArgs = new ArrayList<>();
        if (hijos.size() > 1 && hijos.get(1).tieneHijos()) {
            for (Nodo arg : hijos.get(1).getHijos()) {
                tiposArgs.add(validarNodo(arg));
            }
        }
        
        if (tiposArgs.size() != func.tiposParametros.size()) {
            errores.add(String.format(
                "[error] funcion '%s' espera %d parametros, recibio %d",
                nombreFunc, func.tiposParametros.size(), tiposArgs.size()));
            return func.tipoRetorno;
        }
        
        for (int i = 0; i < tiposArgs.size(); i++) {
            if (!tiposArgs.get(i).equals(func.tiposParametros.get(i))) {
                errores.add(String.format(
                    "[error] parametro %d de '%s': esperado %s, recibido %s",
                    i+1, nombreFunc, func.tiposParametros.get(i), tiposArgs.get(i)));
            }
        }
        
        return func.tipoRetorno;
    }
    
    private void validarDecide(Nodo nodo) {
        for (Nodo hijo : nodo.getHijos()) {
            if (hijo.getTipo().equals("CASO")) {
                List<Nodo> hijosCase = hijo.getHijos();
                if (!hijosCase.isEmpty()) {
                    String tipoCondicion = validarNodo(hijosCase.get(0));
                    if (!tipoCondicion.equals("bool")) {
                        errores.add(String.format(
                            "[error] condicion en decide debe ser bool, encontrado: %s",
                            tipoCondicion));
                    }
                }
            }
            validarNodo(hijo);
        }
    }
    
    private void validarFor(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 3) return;
        
        // validar condicion (debe ser bool)
        String tipoCondicion = validarNodo(hijos.get(1));
        if (!tipoCondicion.equals("bool")) {
            errores.add(String.format(
                "[error] condicion en for debe ser bool, encontrado: %s",
                tipoCondicion));
        }
        
        // validar incremento (debe ser aritmetica)
        String tipoIncremento = validarNodo(hijos.get(2));
        if (!tipoIncremento.equals("int") && !tipoIncremento.equals("float")) {
            errores.add(String.format(
                "[error] incremento en for debe ser aritmetico, encontrado: %s",
                tipoIncremento));
        }
    }
    
    private void validarReturn(Nodo nodo) {
        // buscar funcion contenedora y validar tipo
        // simplificado: solo validar que la expresion sea valida
        if (nodo.tieneHijos()) {
            validarNodo(nodo.getHijos().get(0));
        }
    }
    
    private String validarIdentificador(Nodo nodo) {
        String nombre = nodo.getLexema();
        String tipo = buscarTipoVariable(nombre);
        
        if (tipo == null) {
            errores.add(String.format(
                "[error] variable '%s' no declarada", nombre));
            return "error";
        }
        
        return tipo;
    }
    
    private String validarAccesoArreglo(Nodo nodo) {
        List<Nodo> hijos = nodo.getHijos();
        if (hijos.size() < 3) return "error";
        
        String nombreArr = hijos.get(0).getLexema();
        String tipoIndice1 = validarNodo(hijos.get(1));
        String tipoIndice2 = validarNodo(hijos.get(2));
        
        if (!tipoIndice1.equals("int")) {
            errores.add("[error] indice de arreglo debe ser int");
        }
        if (!tipoIndice2.equals("int")) {
            errores.add("[error] indice de arreglo debe ser int");
        }
        
        String tipoArr = buscarTipoVariable(nombreArr);
        if (tipoArr == null) {
            errores.add(String.format(
                "[error] arreglo '%s' no declarado", nombreArr));
            return "error";
        }
        
        // extraer tipo base (quitar [][])
        return tipoArr.replace("[][]", "");
    }
    
    private String buscarTipoVariable(String nombre) {
        for (TablaSimbolos tabla : tablasSimbolos) {
            if (tabla.existe(nombre)) {
                return tabla.obtener(nombre).tipo;
            }
        }
        return null;
    }
    
    public List<String> getErrores() {
        return errores;
    }
    
    // clase interna para info de funciones
    private static class FuncionInfo {
        String nombre;
        String tipoRetorno;
        List<String> tiposParametros;
        
        FuncionInfo(String nombre, String tipoRetorno, List<String> tiposParametros) {
            this.nombre = nombre;
            this.tipoRetorno = tipoRetorno;
            this.tiposParametros = tiposParametros;
        }
    }
}