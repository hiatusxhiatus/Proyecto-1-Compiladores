# Proyecto 2 - Analizador Sintáctico
Curso: Compiladores e Intérpretes (Verano 2025/2026)  
Daniel Zeas Brown - 2023147474

---

Analizador sintáctico completo que construye árbol sintáctico abstracto y tablas de símbolos para un lenguaje de programación imperativo.

## ¿Qué hace este proyecto?

Lee un archivo de código fuente y:
1. **Analiza la sintaxis** usando un parser generado con CUP
2. **Construye un árbol sintáctico** con la estructura del programa
3. **Genera tablas de símbolos** por cada scope (main y funciones)
4. **Recupera errores sintácticos** y continúa el análisis

## Repositorio

Este proyecto está disponible en GitHub: https://github.com/hiatusxhiatus/Proyecto-Compiladores

## Requisitos

- Java 8 o superior
- Las librerías ya están incluidas en la carpeta `lib/`

## Compilar el proyecto

Ejecutar estos comandos en orden:
```bash
# 1. Generar el parser desde la gramática BNF
java -jar lib/java-cup-11b.jar -destdir src/parser -parser Parser src/parser/parser.cup

# 2. Generar el analizador léxico (si es necesario)
jflex -d src/lexer src/lexer/lexer.flex

# 3. Compilar todo
# macOS y Linux:
javac -cp "lib/*:." src/utils/*.java
javac -cp "lib/*:." src/lexer/Lexer.java
javac -cp "lib/*:." src/parser/*.java
javac -cp "lib/*:src:." src/Main.java

# Windows:
javac -cp "lib/*;." src/utils/*.java
javac -cp "lib/*;." src/lexer/Lexer.java
javac -cp "lib/*;." src/parser/*.java
javac -cp "lib/*;src;." src/Main.java
```

## Ejecutar
```bash
# macOS y Linux:
java -cp "lib/*:src:." Main <archivo_entrada>

# Windows:
java -cp "lib/*;src;." Main <archivo_entrada>
```

**Ejemplos:**
```bash
java -cp "lib/*:src:." Main test/prueba1.txt
java -cp "lib/*:src:." Main test/prueba2.txt
java -cp "lib/*:src:." Main test/prueba3.txt
java -cp "lib/*:src:." Main test/prueba4.txt
java -cp "lib/*:src:." Main test/prueba5.txt
```

## Salida del programa

El programa muestra:
1. **Árbol sintáctico** completo con estructura jerárquica
2. **Tablas de símbolos** para cada scope (main y funciones)
3. **Errores sintácticos** con línea y columna (si los hay)

**Ejemplo de salida:**
```
=== ARBOL SINTACTICO ===
PROGRAMA: programa
└─ MAIN: navidad
   ├─ TIPO: coal
   ├─ NOMBRE: navidad
   └─ BLOQUE: bloque
      └─ SENTENCIAS: sentencias
         ├─ LOCAL: decl_local_asign
         │  ├─ TIPO: int
         │  └─ ID: contador

========================================
      TABLAS DE SIMBOLOS
========================================

--- Tabla de Simbolos: navidad ---
NOMBRE               TIPO            LINEA      COLUMNA   
---------------------------------------------------------------
contador             int             0          0         
precio               float           0          0         
---------------------------------------------------------------
Total de simbolos: 2
```

## Pruebas incluidas

En la carpeta `test/` hay 6 archivos de prueba:

- ✅ `prueba1.txt` - Variables y operaciones básicas (3 símbolos)
- ✅ `prueba2.txt` - Estructuras de control decide/loop (2 símbolos)
- ✅ `prueba3.txt` - Funciones con parámetros (2 tablas, 3 símbolos)
- ✅ `prueba4.txt` - Operadores complejos (6 símbolos)
- ✅ `prueba5.txt` - Strings y chars (1 símbolo)
- ⚠️ `prueba6.txt` - Errores léxicos (recuperación exitosa)

## Estructura de carpetas
```
Proyecto-Compiladores/
├── lib/                         (JFlex y CUP)
├── src/
│   ├── utils/
│   │   ├── Nodo.java           (Nodos del árbol sintáctico)
│   │   └── TablaSimbolos.java  (Gestión de símbolos por scope)
│   ├── lexer/
│   │   ├── Lexer.java
│   │   └── lexer.flex          (Especificación léxica)
│   ├── parser/
│   │   ├── Parser.java         (Parser generado)
│   │   ├── parser.cup          (Gramática BNF completa)
│   │   └── sym.java            (Símbolos del parser)
│   └── Main.java               (Programa principal)
├── test/                        (Archivos de prueba)
├── Gramatica BNF.txt           (Especificación completa)
├── info.txt                    (Información del proyecto)
└── README.md
```

## Características técnicas

### Parser
- **58 terminales** y **42 no terminales**
- **113 producciones** gramaticales
- **253 estados únicos**
- **0 conflictos** shift/reduce o reduce/reduce

### Árbol Sintáctico
- Construcción completa del AST
- Representación jerárquica visual
- Todos los nodos tipados

### Tablas de Símbolos
- **Scope automático** para main (navidad)
- **Scope automático** para cada función (gift)
- Almacena: nombre, tipo, línea, columna
- Detección de símbolos duplicados

### Recuperación de Errores
- Modo pánico con recuperación
- Mensajes descriptivos con línea/columna
- Continúa análisis tras errores
- Producciones `error` estratégicas

## Características del lenguaje

El parser reconoce la sintaxis completa del lenguaje:

- **Declaraciones globales**: `world tipo id endl`
- **Funciones**: `gift tipo id ¿params? bloque`
- **Main**: `coal navidad ¿? bloque`
- **Estructuras de control**: `decide of`, `loop`, `for`
- **Arreglos bidimensionales**: `tipo id[n][m]`
- **Expresiones**: aritméticas, lógicas, relacionales
- **I/O**: `show¿args?`, `get¿id?`

### Operadores
- Aritméticos: +, -, *, /, //, %, ^
- Unarios: ++, --, -(negativo), Σ(not)
- Relacionales: <, <=, >, >=, ==, !=
- Lógicos: @ (and), ~ (or), Σ (not)

### Delimitadores especiales
- Paréntesis: ¿ y ?
- Llaves: ¡ y !
- Corchetes: [ y ]

## Manejo de errores

Cuando encuentra un error sintáctico:
1. **Reporta** la línea y columna exacta
2. **Muestra** el token problemático
3. **Recupera** en modo pánico
4. **Continúa** analizando el resto del código

Ejemplo:
```
[ERROR SINTACTICO] Linea 10, Columna 14
Token inesperado: --
Recuperando del error...
```
