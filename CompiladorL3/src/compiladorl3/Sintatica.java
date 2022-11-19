package compiladorl3;
import java.util.List;
import java.util.ArrayList;

public class Sintatica {
  private Token token;
  private Lexico lexico;
  private List<Variavel> lista;

  public Sintatica(Lexico lexico) {
    this.lexico = lexico;
    lista = new ArrayList<>();
  }

  public void S() {
    this.token = this.lexico.nextToken();
    this.programa();
    if (this.token.getTipo() == Token.TIPO_FIM_CODIGO) {
      System.out.println("FIM DO COMPILADOR FEMINISTA");
    }
  }

  private void E() {
    this.T();
    this.El();
  }

  private void programa() {
    if (this.token.getLexema().equals("int")) {
      this.token = this.lexico.nextToken();
      if (this.token.getLexema().equals("main")) {
        this.token = this.lexico.nextToken();
        if (this.token.getLexema().equals("(")) {
          this.token = this.lexico.nextToken();
          if (this.token.getLexema().equals(")")) {
            this.token = this.lexico.nextToken();
            this.bloco();
          } else {
            throw new RuntimeException("jumente, na inicialização era para fechar parênteses com ) não " + this.token.getLexema());
          }
        } else {
          throw new RuntimeException("jumente, na inicialização era para ser ( não " + this.token.getLexema());
        }
      } else {
        throw new RuntimeException("jumente, na inicialização era para ser main não " + this.token.getLexema());
      }
    } else {
      throw new RuntimeException("jumente, na inicialização era para ser int não " + this.token.getLexema());
    }

  }

  private void declaracao() {
    Variavel v = new Variavel();
    if (this.token.getLexema().equals("float") ||
        this.token.getLexema().equals("int") ||
        this.token.getLexema().equals("char")) {
      if (this.token.getLexema().equals("float")) {
        v.tipo = 1;
      } else if (this.token.getLexema().equals("int")) {
        v.tipo = 0;
      } else {
        v.tipo = 2;
      }
      this.token = this.lexico.nextToken();
      if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR) {
        v.id = this.token.getLexema();
        Variavel v1 = this.findId(v.id);
        if (v1 != null) {
          throw new RuntimeException("jumente, ja existe a variavel " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if (this.token.getLexema().equals(";")) {
          this.token = this.lexico.nextToken();
          lista.add(v);
          if (!this.token.getLexema().equals("}")) {
            blocoi();
          }
        } else {
          throw new RuntimeException("jumente, era para finalizar declaracao com ; não com " + this.token.getLexema());
        }
      } else {
        throw new RuntimeException("jumente, era para ser Identificador ");
      }
    } else {
      throw new RuntimeException("jumente, era para ser float, int ou char não " + this.token.getLexema());
    }
  }

  private Variavel findId(String id) {
    for (Variavel v : lista) {
      if (v.id.equals(id)) {
        return v;
      }
    }
    return null;
  }

  private void blocoi() {
    if (this.token.getLexema().equals("float") || this.token.getLexema().equals("int")
        || this.token.getLexema().equals("char")) {
      declaracao();
    } else if (this.token.getLexema().equals("}")) {
      return;
    } else {
      comando();
    }
  }

  private void bloco() {
    if (this.token.getLexema().equals("{")) {
      this.token = this.lexico.nextToken();
      blocoi();
      if (this.token.getLexema().equals("}")) {
        this.token = this.lexico.nextToken();
      } else {
        throw new RuntimeException("jumente, para finalizar com } não " + this.token.getLexema());
      }

    } else {
      throw new RuntimeException("jumente, para o bloco não pode começar com " + this.token.getLexema());
    }
  }

  private void comando() {
    if (this.token.getLexema().equals("if")) {

      this.token = this.lexico.nextToken();
      if (this.token.getLexema().equals("(")) {
        this.token = this.lexico.nextToken();
        this.E();
        if (this.token.getTipo() == Token.TIPO_OPERADOR_RELACIONAL) {
          this.token = this.lexico.nextToken();
          this.E();
          if (this.token.getLexema().equals(")")) {
            this.token = this.lexico.nextToken();
            this.comando();
            if (this.token.getLexema().equals("else")) {
              this.token = this.lexico.nextToken();
              this.comando();
              if (this.token.getLexema().equals("?")) {
                this.token = this.lexico.nextToken();
                if (!this.token.getLexema().equals("}")) {
                  blocoi();
                }
              } else {
                throw new RuntimeException("jumente, era para ser ? não " + this.token.getLexema());
              }
            } else {
              throw new RuntimeException("jumente, era para ser else não " + this.token.getLexema());
            }
          } else {
            throw new RuntimeException("jumente, era para ser ) não " + this.token.getLexema());
          }
        } else {
          throw new RuntimeException("jumente, para ser um operador relacional");
        }
      } else {
        throw new RuntimeException("jumente, era para ser ( não " + this.token.getLexema());
      }
    } else if (this.token.getLexema().equals("while")) {
      this.token = this.lexico.nextToken();
      if (this.token.getLexema().equals("(")) {
        this.token = this.lexico.nextToken();
        this.E();
        if (this.token.getTipo() == Token.TIPO_OPERADOR_RELACIONAL) {
          this.token = this.lexico.nextToken();
          this.E();
          this.token = this.lexico.nextToken();
          this.comando();
        } else {
          throw new RuntimeException("jumente, para ser um operador relacional");
        }
      } else {
        throw new RuntimeException("jumente, para ser ( não " + this.token.getLexema());
      }
    } else if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR) {
      Variavel v = this.findId(this.token.getLexema());
      if (v == null) {
        throw new RuntimeException("jumente, nao existe a variavel " + this.token.getLexema());
      }
      this.token = this.lexico.nextToken();
      if (this.token.getLexema().equals("=")) {
        this.token = this.lexico.nextToken();
        if (v.tipo == Token.TIPO_CHAR) {
          this.Char();
        } else if (v.tipo == Token.TIPO_REAL) {
          this.E();
        } else {
          this.EI();
        }
        if (this.token.getLexema().equals(";")) {
          this.token = this.lexico.nextToken();
          if (!this.token.getLexema().equals("}")) {
            blocoi();
          }
        } else {
          throw new RuntimeException("jumente, era para finalizar atribuição com ; não com  " + this.token.getLexema());
        }
      } else {
        throw new RuntimeException("jumente, para ser = não " + this.token.getLexema());
      }
    } else if (this.token.getLexema().equals("{")) {
      this.bloco();
    } else if (this.token.getLexema().equals("else")) {

    } else if (this.token.getLexema().equals("?")) {

    } else {
      throw new RuntimeException("jumente, que comando é começa com = " + this.token.getLexema());
    }
  }

  private void El() {
    if (this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO) {
      this.OP();
      this.T();
      this.El();
    }
  }

  private void Char() {
    if (this.token.getTipo() == Token.TIPO_CHAR || (this.token.getTipo() == Token.TIPO_IDENTIFICADOR
        && this.findId(this.token.getLexema()) != null)) {
      
      if (this.token.getTipo() == Token.TIPO_CHAR || (this.token.getTipo() == Token.TIPO_IDENTIFICADOR
        && this.findId(this.token.getLexema()).tipo == Token.TIPO_CHAR)) {
      this.token = this.lexico.nextToken();
      } else {
      throw new RuntimeException("Era pra o identificador ser char");
      }
      
    } else {
      throw new RuntimeException("Era pra ser char");
    }
  }

  private void T() {
    if ((this.token.getTipo() == Token.TIPO_IDENTIFICADOR && (this.findId(this.token.getLexema()) != null)) ||
        this.token.getTipo() == Token.TIPO_INTEIRO ||
        this.token.getTipo() == Token.TIPO_REAL) {

      if ((this.token.getTipo() == Token.TIPO_IDENTIFICADOR
          && ((this.findId(this.token.getLexema()).tipo == Token.TIPO_INTEIRO)
              || (this.findId(this.token.getLexema()).tipo == Token.TIPO_REAL))
          ||
          this.token.getTipo() == Token.TIPO_INTEIRO || this.token.getTipo() == Token.TIPO_REAL)) {
        this.token = this.lexico.nextToken();
      } else {
        throw new RuntimeException("Era pra o identificador ser float ou inteiro");
      }
    } else {
      throw new RuntimeException("Era pra ser inteiro ou float");
    }
  }

  private void OP() {
    if (this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO) {
      this.token = this.lexico.nextToken();
    } else {
      throw new RuntimeException("Era pra ser um operador léxico perto de: " + this.token.getLexema());
    }
  }

  private void TI() {
    if ((this.token.getTipo() == Token.TIPO_IDENTIFICADOR&&(this.findId(this.token.getLexema()) != null)) ||
        this.token.getTipo() == Token.TIPO_INTEIRO) {
      if((this.token.getTipo() == Token.TIPO_IDENTIFICADOR&&(this.findId(this.token.getLexema()).tipo == Token.TIPO_INTEIRO))||  this.token.getTipo() == Token.TIPO_INTEIRO) {
      this.token = this.lexico.nextToken();
      }else{
        throw new RuntimeException("Era pra o identificador ser inteiro");
      }
    } else {
      throw new RuntimeException("Era pra ser inteiro ");
    }
  }

  private void EI() {
    this.TI();
    this.ElI();
  }

  private void ElI() {
    if (this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO) {
      this.OP();
      this.TI();
      this.ElI();
    }
  }

}