/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladorl3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.valueOf;

/**
 *
 * @author tarci
 */
public class Lexico {
    private char[] conteudo;
    private int indiceConteudo;
    
    public Lexico(String caminhoCodigoFonte){
        try {
            String conteudoStr;
            conteudoStr = new String(Files.readAllBytes(Paths.get(caminhoCodigoFonte)));
            this.conteudo = conteudoStr.toCharArray();
            this.indiceConteudo = 0;                        
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
    
    //Retorna próximo char
    private char nextChar(){
        return this.conteudo[this.indiceConteudo++];
    }
    
    //Verifica existe próximo char ou chegou ao final do código fonte
    private boolean hasNextChar(){
        return indiceConteudo < this.conteudo.length;
    }
    
    //Retrocede o índice que aponta para o "char da vez" em uma unidade
    private void back(){
        this.indiceConteudo--;
    }
    
    //Identificar se char é letra minúscula    
    private boolean isLetra(char c){
        return (c >= 'a') && (c <= 'z');
    }
    
    //Identificar se char é dígito
    private boolean isDigito(char c){
        return (c >= '0') && (c <= '9');
    }

    //Método retorna próximo token válido ou retorna mensagem de erro.
    public Token nextToken() {
        Token token = null;
        char c;
        int estado = 0;
    
        StringBuffer lexema = new StringBuffer();
        while (this.hasNextChar()) {
          c = this.nextChar();
          switch (estado) {
            case 0:
              if (c == ' ' || c == '\t' || c == '\n' || c == '\r') { // caracteres de espaço em branco ASCII tradicionais
                estado = 0;
              } else if (c == 'i') {
                lexema.append(c);
                estado = 14;
              } else if (c == 'e') {
                lexema.append(c);
                estado = 16;
              } else if (c == 'm') {
                lexema.append(c);
                estado = 19;
              } else if (c == 'c') {
                lexema.append(c);
                estado = 22;
              } else if (c == 'f') {
                lexema.append(c);
                estado = 25;
              } else if (c == 'w') {
                lexema.append(c);
                estado = 29;
              } else if (this.isLetra(c)) {
                lexema.append(c);
                estado = 1;
              } else if (this.isDigito(c)) {
                lexema.append(c);
                estado = 2;
              } else if (c == ')' ||
                  c == '(' ||
                  c == '{' ||
                  c == '}' ||
                  c == ',' ||
                  c == ';') {
                lexema.append(c);
                estado = 5;
              } else if (c == '+' ||
                  c == '-' ||
                  c == '*' ||
                  c == '/' ||
                  c == '%') {
                lexema.append(c);
                estado = 6;
              } else if (c == '=') {
                lexema.append(c);
                estado = 7;
              } else if (c == '<') {
                lexema.append(c);
                estado = 8;
              } else if (c == '>') {
                lexema.append(c);
                estado = 9;
              } else if (c == '$') {
                lexema.append(c);
                estado = 99;
                this.back();
              } else {
                String characterString = valueOf(c);
    
                if (characterString.equals("'")) {
                  lexema.append(c);
                  estado = 11;
                } else {
                  lexema.append(c);
                  throw new RuntimeException("Erro: token inválido \"" + lexema.toString() + "\"");
                }
              }
              break;
            case 1:
              if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else {
                this.back();
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              }
              break;
            case 2:
              if (this.isDigito(c)) {
                lexema.append(c);
                estado = 2;
              } else if (c == '.') {
                lexema.append(c);
                estado = 3;
              } else {
                this.back();
                return new Token(lexema.toString(), Token.TIPO_INTEIRO);
              }
              break;
            case 3:
              if (this.isDigito(c)) {
                lexema.append(c);
                estado = 4;
              } else {
                throw new RuntimeException("Erro: número float inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 4:
              if (this.isDigito(c)) {
                lexema.append(c);
                estado = 4;
              } else {
                this.back();
                return new Token(lexema.toString(), Token.TIPO_REAL);
              }
              break;
            case 5:
              this.back();
              return new Token(lexema.toString(), Token.TIPO_CARACTER_ESPECIAL);
            case 6:
              this.back();
              return new Token(lexema.toString(), Token.TIPO_OPERADOR_ARITMETICO);
            case 7:
              if (c == '=') {
                lexema.append(c);
                estado = 10;
              } else {
                this.back();
                return new Token(lexema.toString(), Token.TIPO_OPERADOR_ATRIBUICAO);
              }
              break;
            case 8:
              estado = 10;
              if (c == '=' || c == '>') {
                lexema.append(c);
                estado = 10;
              }
              break;
            case 9:
              estado = 10;
              if (c == '=') {
                lexema.append(c);
                estado = 10;
              }
              break;
            case 10:
              this.back();
              return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
            case 11:
              if (isLetra(c) || isDigito(c)) {
                lexema.append(c);
                estado = 12;
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: char inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 12:
              String characterString = valueOf(c);
              if (characterString.equals("'")) {
                lexema.append(c);
                return new Token(lexema.toString(), Token.TIPO_CHAR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: char inválido \"" + lexema.toString() + "\"");
              }
            case 14:
              if (c == 'n') {
                lexema.append(c);
                estado = 15;
              } else if (c == 'f') {
                lexema.append(c);
                return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 15:
              if (c == 't') {
                lexema.append(c);
                return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 16:
              if (c == 'l') {
                lexema.append(c);
                estado = 17;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 17:
              if (c == 's') {
                lexema.append(c);
                estado = 18;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 18:
              if (c == 'e') {
                lexema.append(c);
                return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: token inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 19:
              if (c == 'a') {
                lexema.append(c);
                estado = 20;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 20:
              if (c == 'i') {
                lexema.append(c);
                estado = 21;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 21:
              if (c == 'n') {
                lexema.append(c);
                return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 22:
              if (c == 'h') {
                lexema.append(c);
                estado = 23;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 23:
              if (c == 'a') {
                lexema.append(c);
                estado = 24;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 24:
              if (c == 'n') {
                lexema.append(c);
                return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 25:
              if (c == 'l') {
                lexema.append(c);
                estado = 26;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 26:
              if (c == 'o') {
                lexema.append(c);
                estado = 27;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 27:
              if (c == 'a') {
                lexema.append(c);
                estado = 28;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 28:
              if (c == 't') {
                lexema.append(c);
                return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 29:
              if (c == 'h') {
                lexema.append(c);
                estado = 30;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 30:
              if (c == 'i') {
                lexema.append(c);
                estado = 31;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 31:
              if (c == 'l') {
                lexema.append(c);
                estado = 32;
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 32:
              if (c == 'e') {
                lexema.append(c);
                return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
              } else if (this.isLetra(c) || this.isDigito(c)) {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                lexema.append(c);
                estado = 1;
              } else if (c == ' ') {
                return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
              } else {
                lexema.append(c);
                throw new RuntimeException("Erro: identificador inválido \"" + lexema.toString() + "\"");
              }
              break;
            case 99:
              return new Token(lexema.toString(), Token.TIPO_FIM_CODIGO);
          }
        }
        return token;
      }
}
