package compiladorl3;

public class Variavel implements Comparable<Variavel>{
    int tipo;
  String id;
  @Override
  public int compareTo(Variavel v){
    return this.id.compareTo(v.id);
  }
}
