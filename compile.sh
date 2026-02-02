#!/bin/bash

echo "=== COMPILANDO PROYECTO 2 CORREGIDO ==="

# Limpiar archivos previos
echo "[1/4] Limpiando archivos antiguos..."
rm -f src/lexer/Lexer.java
rm -f src/parser/Parser.java src/parser/sym.java
rm -f src/**/*.class

# Generar Lexer
echo "[2/4] Generando Lexer con JFlex..."
java -jar lib/java-cup-11b.jar -destdir src/lexer src/lexer/lexer.flex

# Generar Parser
echo "[3/4] Generando Parser con CUP..."
java -jar lib/java-cup-11b.jar -parser Parser -symbols sym -destdir src/parser src/parser/parser.cup

# Compilar todo
echo "[4/4] Compilando clases Java..."
javac -cp "lib/*:src" -d src src/lexer/Lexer.java
javac -cp "lib/*:src" -d src src/parser/Parser.java src/parser/sym.java
javac -cp "lib/*:src" -d src src/utils/*.java
javac -cp "lib/*:src" -d src src/Main.java

echo ""
echo "=== COMPILACION EXITOSA ==="
echo ""
echo "Para ejecutar:"
echo "  java -cp lib/*:src Main test/archivo_prueba.txt"
echo ""