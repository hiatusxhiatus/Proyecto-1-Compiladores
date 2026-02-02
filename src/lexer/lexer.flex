package lexer;

import java_cup.runtime.*;
import parser.sym;

%%

%class Lexer
%public
%cup
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
%}

// expresiones regulares basicas
WhiteSpace = [ \t\r\n]+
LineComment = \|[^\n]*
MultiComment = "Ñ"[^"Ñ"]*"Ñ"
Digit = [0-9]
Letter = [a-zA-Z]
Integer = {Digit}+
Float = {Digit}+\.{Digit}+
Identifier = ({Letter}|_)({Letter}|{Digit}|_)*
StringLiteral = \"[^\"]*\"
CharLiteral = \'[^\']\'

%%

// palabras reservadas
"world"         { return symbol(sym.WORLD, yytext()); }
"local"         { return symbol(sym.LOCAL, yytext()); }
"decide"        { return symbol(sym.DECIDE, yytext()); }
"of"            { return symbol(sym.OF, yytext()); }
"else"          { return symbol(sym.ELSE, yytext()); }
"end"           { return symbol(sym.END, yytext()); }
"loop"          { return symbol(sym.LOOP, yytext()); }
"exit"          { return symbol(sym.EXIT, yytext()); }
"when"          { return symbol(sym.WHEN, yytext()); }
"for"           { return symbol(sym.FOR, yytext()); }
"return"        { return symbol(sym.RETURN, yytext()); }
"break"         { return symbol(sym.BREAK, yytext()); }
"show"          { return symbol(sym.SHOW, yytext()); }
"get"           { return symbol(sym.GET, yytext()); }
"gift"          { return symbol(sym.GIFT, yytext()); }
"navidad"       { return symbol(sym.NAVIDAD, yytext()); }
"coal"          { return symbol(sym.COAL, yytext()); }
"endl"          { return symbol(sym.ENDL, yytext()); }

// tipos
"int"           { return symbol(sym.INT, yytext()); }
"float"         { return symbol(sym.FLOAT, yytext()); }
"bool"          { return symbol(sym.BOOL, yytext()); }
"char"          { return symbol(sym.CHAR, yytext()); }
"string"        { return symbol(sym.STRING, yytext()); }

// operadores aritmeticos
"+"             { return symbol(sym.MAS, yytext()); }
"-"             { return symbol(sym.MENOS, yytext()); }
"*"             { return symbol(sym.MULT, yytext()); }
"//"            { return symbol(sym.DIV_ENTERA, yytext()); }
"/"             { return symbol(sym.DIV, yytext()); }
"%"             { return symbol(sym.MODULO, yytext()); }
"^"             { return symbol(sym.POTENCIA, yytext()); }
"++"            { return symbol(sym.INCREMENTO, yytext()); }
"--"            { return symbol(sym.DECREMENTO, yytext()); }

// operadores relacionales
"<"             { return symbol(sym.MENOR, yytext()); }
"<="            { return symbol(sym.MENOR_IGUAL, yytext()); }
">"             { return symbol(sym.MAYOR, yytext()); }
">="            { return symbol(sym.MAYOR_IGUAL, yytext()); }
"=="            { return symbol(sym.IGUAL_IGUAL, yytext()); }
"!="            { return symbol(sym.DIFERENTE, yytext()); }

// Operadores logicos
"@"             { return symbol(sym.AND, yytext()); }
"~"             { return symbol(sym.OR, yytext()); }
"Σ"             { return symbol(sym.NOT, yytext()); }

// Simbolos especiales
"¿"             { return symbol(sym.PAREN_IZQ, yytext()); }
"?"             { return symbol(sym.PAREN_DER, yytext()); }
"¡"             { return symbol(sym.LLAVE_IZQ, yytext()); }
"!"             { return symbol(sym.LLAVE_DER, yytext()); }
"["             { return symbol(sym.CORCHETE_IZQ, yytext()); }
"]"             { return symbol(sym.CORCHETE_DER, yytext()); }
"->"            { return symbol(sym.FLECHA, yytext()); }
","             { return symbol(sym.COMA, yytext()); }
"="             { return symbol(sym.ASIGNACION, yytext()); }

// literales
{Integer}       { return symbol(sym.INT_LIT, Integer.parseInt(yytext())); }
{Float}         { return symbol(sym.FLOAT_LIT, Double.parseDouble(yytext())); }
{StringLiteral} { 
    String str = yytext();
    str = str.substring(1, str.length()-1);
    return symbol(sym.STRING_LIT, str); 
}
{CharLiteral}   { 
    String ch = yytext();
    ch = ch.substring(1, ch.length()-1);
    return symbol(sym.CHAR_LIT, ch); 
}
"true"|"false"  { return symbol(sym.BOOL_LIT, yytext()); }

// identificadores
{Identifier}    { return symbol(sym.ID, yytext()); }

// ignorar
{WhiteSpace}    { /* no hacer nada */ }
{LineComment}   { /* ignorar comentarios */ }
{MultiComment}  { /* ignorar comentarios multilinea */ }

// errores
.               { 
    System.err.println("[ERROR LEXICO] Caracter no reconocido '" + yytext() + 
                       "' en linea " + (yyline+1) + ", columna " + (yycolumn+1));
}