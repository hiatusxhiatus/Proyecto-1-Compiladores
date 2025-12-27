## Repositorio

Este proyecto está disponible en GitHub: https://github.com/TU_USUARIO/Proyecto-1-Compiladores

---

# Proyecto 1 - Analizador Léxico
Curso: Compiladores e Intérpretes (Verano 2025/2026)
Daniel Zeas Brown

---

Analizador léxico para un lenguaje de programación imperativo orientado a configuración de chips.

## ¿Qué hace este proyecto?

Lee un archivo de código fuente y lo divide en tokens. Genera un archivo de salida con todos los tokens encontrados, incluyendo su posición en el código.

## Requisitos

- Java 8 o superior
- Las librerías ya están incluidas en la carpeta `lib/`

## Compilar el proyecto

Hay que ejecutar estos comandos en orden:
```bash
# 1. Generar el archivo de tokens
java -jar lib/java-cup-11b.jar -destdir src/parser -parser Parser src/parser/parser.cup

# 2. Generar el analizador léxico
jflex -d src/lexer src/lexer/lexer.flex

# 3. Compilar todo
# Compilación para macOS y Linux:
javac -cp "lib/*:." src/Main.java src/lexer/Lexer.java src/parser/*.java

# Compilación para Windows:
javac -cp "lib/*;." src/Main.java src/lexer/Lexer.java src/parser/*.java
```

## Ejecutar
```bash
# macOS y Linux
java -cp "lib/*:src:." Main <archivo_entrada> <archivo_salida>

# Windows
java -cp "lib/*;src;." Main <archivo_entrada> <archivo_salida>
```

**Ejemplo:**
```bash
java -cp "lib/*:src:." Main test/prueba1.txt output/resultado1.txt
java -cp "lib/*:src:." Main test/prueba2.txt output/resultado2.txt
java -cp "lib/*:src:." Main test/prueba3.txt output/resultado3.txt
java -cp "lib/*:src:." Main test/prueba4.txt output/resultado4.txt
java -cp "lib/*:src:." Main test/prueba5.txt output/resultado5.txt
java -cp "lib/*:src:." Main test/prueba6.txt output/resultado6.txt
```

## Pruebas incluidas

En la carpeta `test/` hay 6 archivos de prueba:

- `prueba1.txt` - Variables y operaciones básicas
- `prueba2.txt` - Estructuras de control (decide, loop)
- `prueba3.txt` - Funciones y arreglos
- `prueba4.txt` - Operadores y expresiones complejas
- `prueba5.txt` - Comentarios y cadenas de texto
- `prueba6.txt` - Errores léxicos (para probar el manejo de errores)

## Estructura de carpetas
```
Proyecto-1-Compiladores/
├── lib/                    (librerías de JFlex y CUP)
├── src/
│   ├── lexer/             (especificación y código del lexer)
│   ├── parser/            (definición de tokens)
│   └── Main.java          (programa principal)
├── test/                   (archivos de prueba)
└── output/                 (resultados del análisis)
```

## Características del lenguaje

El analizador reconoce:

- Palabras clave: world, local, decide, loop, for, etc.
- Tipos: int, float, boolean, char, string
- Operadores: +, -, *, /, //, %, ^, ++, --
- Comparaciones: <, <=, >, >=, ==, !=
- Lógicos: @ (and), ~ (or), Σ (not)
- Paréntesis especiales: ¿ y ? en lugar de ( y )
- Llaves especiales: ¡ y ! en lugar de { y }
- Comentarios: | (una línea) y є...э (multilínea)

## Manejo de errores

Cuando encuentra un carácter que no reconoce, lo reporta en el archivo de salida y continúa analizando el resto del código.