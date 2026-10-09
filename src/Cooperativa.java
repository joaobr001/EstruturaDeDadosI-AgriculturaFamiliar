import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cooperativa {
    private String nome;
    private String municipio;
    private double taxaAdministrativa;
    private final ArrayList<Cooperado> listaCooperados = new ArrayList<>();

    public Cooperativa(String nome, String municipio, double taxaAdministrativa){
        this.nome = nome;
        this.municipio = municipio;
        this.taxaAdministrativa = taxaAdministrativa;
    }
    public void listarCooperador(){
        for(Cooperado c : listaCooperados){
            IO.println(c);
        }
    }
    public void adicionarCoopeado(Cooperado c){
        listaCooperados.add(c);
    }

    public void removerCoopeado(Cooperado c){
        listaCooperados.remove(c);
    }

    public Cooperado buscarCooperado(String matricula){
        for (Cooperado c : listaCooperados){
            if (Objects.equals(c.getMatricula(), matricula)){
                // Retornar toString com as informações do cooperado
            }
        }
    }

    public double folhaPagamento(int ano, int mes){
        double soma = 0.0;
        for (Cooperado c : listaCooperados){
            // if (---------------------------)
            // Totalizar o valor gasto por funcionário no mês informado
        }
    }

    public List<Cooperado> ranking(int ano, int mes){
        // Ranking do mês: cooperados por total de rasas (maior primeiro; empate pela matrícula).
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
