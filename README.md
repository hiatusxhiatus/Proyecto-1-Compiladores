# Analizador Léxico - Proyecto 1
## Compiladores e Intérpretes - Verano 2025/2026
### Estudiante: Daniel Zeas Brown


### Descripción
Analizador léxico (scanner) para un lenguaje imperativo diseñado para configuración de chips en sistemas empotrados.

### Requisitos
- Java JDK 8 o superior
- JFlex (incluido en lib/)
- CUP (incluido en lib/)

### Estructura del proyecto
```
Proyecto-1-Compiladores/
├── lib/                    # Librerías JFlex y CUP
├── src/
│   ├── lexer/
│   │   ├── lexer.flex     # Especificación del analizador léxico
│   │   └── Lexer.java     # Generado por JFlex
│   ├── parser/
│   │   ├── parser.cup     # Especificación para generar tokens
│   │   └── sym.java       # Tokens generados por CUP
│   └── Main.java          # Programa principal
├── test/                   # Archivos de prueba
└── output/                 # Resultados del análisis
```

### Compilación

#### Paso 1: Generar archivos con CUP
```bash
java -jar lib/java-cup-11b.jar -destdir src/parser -parser Parser src/parser/parser.cup
```

#### Paso 2: Generar lexer con JFlex
```bash
jflex -d src/lexer src/lexer/lexer.flex
```

#### Paso 3: Compilar proyecto Java

**macOS/Linux:**
```bash
javac -cp "lib/*:." src/Main.java src/lexer/Lexer.java src/parser/*.java
```

**Windows:**
```bash
javac -cp "lib/*;." src/Main.java src/lexer/Lexer.java src/parser/*.java
```

### Ejecución

**macOS/Linux:**
```bash
java -cp "lib/*:src:." Main <archivo_entrada> <archivo_salida>
```

**Windows:**
```bash
java -cp "lib/*;src;." Main <archivo_entrada> <archivo_salida>
```

### Ejemplos de uso
```bash
# Analizar prueba básica
java -cp "lib/*:src:." Main test/prueba1.txt output/resultado1.txt

# Analizar programa con estructuras de control
java -cp "lib/*:src:." Main test/prueba2.txt output/resultado2.txt

# Analizar programa con errores
java -cp "lib/*:src:." Main test/prueba6.txt output/resultado6.txt
```

### Archivos de prueba incluidos

- **prueba1.txt**: Variables y operaciones básicas
- **prueba2.txt**: Estructuras de control (decide, loop)
- **prueba3.txt**: Funciones y arreglos
- **prueba4.txt**: Operadores complejos
- **prueba5.txt**: Comentarios y strings
- **prueba6.txt**: Manejo de errores léxicos

### Tokens reconocidos

#### Palabras reservadas
world, local, decide, of, else, end, loop, exit, when, for, return, break, show, get, gift, navidad, coal, endl

#### Tipos de datos
int, float, boolean, char, string

#### Operadores aritméticos
+, -, *, /, //, %, ^, ++, --

#### Operadores relacionales
<, <=, >, >=, ==, !=

#### Operadores lógicos
@ (AND), ~ (OR), Σ (NOT)

#### Símbolos especiales
¿ ? (paréntesis), ¡ ! (llaves), [ ] (corchetes), ->, =, ,

#### Literales
Enteros, flotantes, strings, caracteres, booleanos (true/false)

### Manejo de errores

El analizador implementa recuperación en modo pánico:
- Detecta caracteres no reconocidos
- Reporta línea y columna del error
- Continúa el análisis después del error

### Autores
[Tu nombre aquí]
[Nombre de tu compañero]

### Fecha
Diciembre 2025