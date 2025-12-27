import lexer.Lexer;
import parser.sym;
import java_cup.runtime.Symbol;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Uso: java Main <archivo_entrada> <archivo_salida>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // crear el lexer
            FileReader reader = new FileReader(inputFile);
            Lexer lexer = new Lexer(reader);
            
            // abrir archivo de salida
            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
            writer.println("ANALISIS LEXICO");
            writer.println("Archivo: " + inputFile);
            writer.println("================\n");
            writer.printf("%-15s %-20s %-10s %-10s\n", "TOKEN", "LEXEMA", "LINEA", "COLUMNA");
            writer.println("-----------------------------------------------------------");
            
            // procesar tokens
            Symbol token;
            int tokenCount = 0;
            boolean hayErrores = false;
            
            while (true) {
                try {
                    token = lexer.next_token();
                    
                    if (token.sym == sym.EOF) {
                        break;
                    }
                    
                    tokenCount++;
                    String tokenName = getTokenName(token.sym);
                    String lexema = token.value != null ? token.value.toString() : "";
                    
                    writer.printf("%-15s %-20s %-10d %-10d\n", 
                        tokenName, lexema, token.left, token.right);
                        
                } catch (Error e) {
                    hayErrores = true;
                    writer.println("\n[ERROR] " + e.getMessage());
                    System.err.println("Error lexico: " + e.getMessage());
                    // continuar con el siguiente token
                }
            }
            
            writer.println("\n-----------------------------------------------------------");
            writer.println("Total de tokens: " + tokenCount);

            if (hayErrores) {
                writer.println("\nSe encontraron errores lexicos durante el analisis.");
            }

            writer.close();
            reader.close();
            
            System.out.println("Analisis completado. Resultados en: " + outputFile);
            
        } catch (FileNotFoundException e) {
            System.err.println("Error: archivo no encontrado - " + inputFile);
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
        }
    }
    
    // convierte el numero de token a nombre legible
    private static String getTokenName(int tokenType) {
        switch(tokenType) {
            case sym.WORLD: return "WORLD";
            case sym.LOCAL: return "LOCAL";
            case sym.DECIDE: return "DECIDE";
            case sym.OF: return "OF";
            case sym.ELSE: return "ELSE";
            case sym.END: return "END";
            case sym.LOOP: return "LOOP";
            case sym.EXIT: return "EXIT";
            case sym.WHEN: return "WHEN";
            case sym.FOR: return "FOR";
            case sym.RETURN: return "RETURN";
            case sym.BREAK: return "BREAK";
            case sym.SHOW: return "SHOW";
            case sym.GET: return "GET";
            case sym.GIFT: return "GIFT";
            case sym.NAVIDAD: return "NAVIDAD";
            case sym.COAL: return "COAL";
            case sym.ENDL: return "ENDL";
            case sym.INT: return "INT";
            case sym.FLOAT: return "FLOAT";
            case sym.BOOLEAN: return "BOOLEAN";
            case sym.CHAR: return "CHAR";
            case sym.STRING: return "STRING";
            case sym.MAS: return "MAS";
            case sym.MENOS: return "MENOS";
            case sym.MULT: return "MULT";
            case sym.DIV: return "DIV";
            case sym.DIV_ENTERA: return "DIV_ENTERA";
            case sym.MODULO: return "MODULO";
            case sym.POTENCIA: return "POTENCIA";
            case sym.INCREMENTO: return "INCREMENTO";
            case sym.DECREMENTO: return "DECREMENTO";
            case sym.MENOR: return "MENOR";
            case sym.MENOR_IGUAL: return "MENOR_IGUAL";
            case sym.MAYOR: return "MAYOR";
            case sym.MAYOR_IGUAL: return "MAYOR_IGUAL";
            case sym.IGUAL_IGUAL: return "IGUAL_IGUAL";
            case sym.DIFERENTE: return "DIFERENTE";
            case sym.AND: return "AND";
            case sym.OR: return "OR";
            case sym.NOT: return "NOT";
            case sym.PAREN_IZQ: return "PAREN_IZQ";
            case sym.PAREN_DER: return "PAREN_DER";
            case sym.LLAVE_IZQ: return "LLAVE_IZQ";
            case sym.LLAVE_DER: return "LLAVE_DER";
            case sym.CORCHETE_IZQ: return "CORCHETE_IZQ";
            case sym.CORCHETE_DER: return "CORCHETE_DER";
            case sym.FLECHA: return "FLECHA";
            case sym.COMA: return "COMA";
            case sym.ASIGNACION: return "ASIGNACION";
            case sym.INT_LIT: return "INT_LIT";
            case sym.FLOAT_LIT: return "FLOAT_LIT";
            case sym.STRING_LIT: return "STRING_LIT";
            case sym.CHAR_LIT: return "CHAR_LIT";
            case sym.BOOL_LIT: return "BOOL_LIT";
            case sym.ID: return "ID";
            default: return "DESCONOCIDO";
        }
    }
}