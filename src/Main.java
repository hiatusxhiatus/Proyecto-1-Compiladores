import lexer.Lexer;
import parser.Parser;
import java_cup.runtime.Symbol;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java Main <archivo_entrada>");
            System.exit(1);
        }

        String inputFile = args[0];

        try {
            // Crear el lexer y parser
            FileReader reader = new FileReader(inputFile);
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(lexer, new java_cup.runtime.DefaultSymbolFactory());
            
            System.out.println("==============================================");
            System.out.println("  COMPILADOR - PROYECTO 2");
            System.out.println("  Archivo: " + inputFile);
            System.out.println("==============================================\n");
            
            // Parsear
            System.out.println("[INICIO] Analizando archivo...\n");
            parser.parse();
            
            // Mostrar arbol sintactico
            if (parser.arbolSintactico != null) {
                parser.arbolSintactico.imprimirArbol();
            } else {
                System.out.println("[ERROR] No se pudo construir el arbol sintactico");
            }
            
            // Mostrar tablas de simbolos
            parser.imprimirTablas();
            
            System.out.println("\n[EXITO] Analisis completado sin errores");
            
        } catch (FileNotFoundException e) {
            System.err.println("[ERROR] Archivo no encontrado: " + inputFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("[ERROR] Durante el analisis: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}