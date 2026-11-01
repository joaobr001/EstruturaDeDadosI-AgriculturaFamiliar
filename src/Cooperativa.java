import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Comparator;
import java.util.Locale;

public class Cooperativa {
    private String nome;
    private String municipio;
    private double taxaAdministrativa;
    private final ArrayList<Cooperado> listaCooperados;

    public Cooperativa(String nome, String municipio, double taxaAdministrativa){
        this.nome = nome;
        this.municipio = municipio;
        this.taxaAdministrativa = taxaAdministrativa;
        this.listaCooperados = new ArrayList<>();
    }

    public void listarCooperador(){
        for(Cooperado c : listaCooperados){
            IO.println(c);
        }
    }

    public void adicionarCooperado(Cooperado c){
        listaCooperados.add(c);
    }

    public void removerCooperado(Cooperado c){
        listaCooperados.remove(c);
    }

    public ArrayList<Cooperado> getCooperados() {
        return listaCooperados;
    }

    public Cooperado buscarCooperado(String matricula){
        for (Cooperado c : listaCooperados){
            if (Objects.equals(c.getMatricula(), matricula)){
                return c;
            }
        }
        return null;
    }

   public double folhaPagamento(int ano, int mes) {
    double total = 0.0;
       for (Cooperado c : listaCooperados) {
           total += c.liquido(ano, mes, taxaAdministrativa);
       }
       return total;
   }

    public List<Cooperado> ranking(int ano, int mes){
        List<Cooperado> listaRanking = new ArrayList<>();
        for (Cooperado c : listaCooperados) {
            int rasasC = c.rasas(ano, mes);
            if (rasasC == 0) {
                continue; // se não entregou nada no mês, não entra no ranking
            }
            int i = 0;
            for (Cooperado atual : listaRanking) {
                int rasasAtual = atual.rasas(ano, mes);
                if (rasasC > rasasAtual) {
                    break;
                }
                if (rasasC == rasasAtual && c.compareTo(atual) < 0) {
                    break;
                }
                i++;
            }
            listaRanking.add(i, c);
        }
        return listaRanking;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public double getTaxaAdministrativa() {
        return taxaAdministrativa;
    }

    public void setTaxaAdministrativa(double taxaAdministrativa) {
        this.taxaAdministrativa = taxaAdministrativa;
    }
}
