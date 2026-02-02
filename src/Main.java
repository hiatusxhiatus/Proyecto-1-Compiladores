import lexer.Lexer;
import parser.Parser;
import semantico.AnalizadorSemantico;
import java_cup.runtime.Symbol;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("uso: java Main <archivo_entrada>");
            System.exit(1);
        }

        String inputFile = args[0];

        try {
            FileReader reader = new FileReader(inputFile);
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(lexer, new java_cup.runtime.DefaultSymbolFactory());
            
            System.out.println("==============================================");
            System.out.println("  compilador - proyecto 3");
            System.out.println("  archivo: " + inputFile);
            System.out.println("==============================================\n");
            
            System.out.println("[inicio] analisis lexico y sintactico...\n");
            parser.parse();
            
            if (parser.arbolSintactico != null) {
                parser.arbolSintactico.imprimirArbol();
            } else {
                System.out.println("[error] no se pudo construir arbol sintactico");
                System.exit(1);
            }
            
            parser.imprimirTablas();
            
            // analisis semantico
            AnalizadorSemantico analizador = new AnalizadorSemantico(parser.tablasSimbolos);
            boolean semanticoOk = analizador.analizar(parser.arbolSintactico);
            
            if (!semanticoOk) {
                System.err.println("\n[fallo] errores semanticos encontrados");
                System.exit(1);
            }
            
            System.out.println("\n[exito] analisis completado sin errores");
            
        } catch (FileNotFoundException e) {
            System.err.println("[error] archivo no encontrado: " + inputFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("[error] durante analisis: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}