cd lib
curl -L https://github.com/jflex-de/jflex/releases/download/v1.8.2/jflex-full-1.8.2.jar -o jflex-full-1.8.2.jar
cd ..

# 2. Generar Lexer
java -jar lib/jflex-full-1.8.2.jar -d src/lexer src/lexer/lexer.flex

# 3. Generar Parser
java -jar lib/java-cup-11b.jar -parser Parser -symbols sym -destdir src/parser src/parser/parser.cup

# 4. Compilar todo
javac -cp "lib/*:src" src/lexer/Lexer.java -d src
javac -cp "lib/*:src" src/parser/Parser.java src/parser/sym.java -d src
javac -cp "lib/*:src" src/utils/*.java -d src
javac -cp "lib/*:src" src/Main.java -d src

# 5. Ejecutar
java -cp "lib/*:src" Main test/prueba_final.txt