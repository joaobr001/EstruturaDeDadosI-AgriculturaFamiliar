import java.time.LocalDate;
import java.util.LinkedList;

public class Cooperado {
    private String matricula;
    private String nome;
    private String comunidade;
    private LinkedList<Entrega> entregas;

    public Cooperado(String matricula, String nome, String comunidade){
        this.matricula = matricula;
        this.nome = nome;
        this.comunidade = comunidade;
    }
    public boolean adicionarEntrega(Entrega nova){
        if (nova.getRasas() == 0){
            return false;
        }
        for (Entrega e : entregas){
            if (e.getData().equals(nova.getData())){
                return false;
            }
        }
        int i = 0;
        for (Entrega e : entregas) {
            if (e.getData().isAfter(nova.getData())) {
                break;
            }
            i++;
        }
        entregas.add(i, nova);
        return true;
    }
    public Entrega buscarEntrega(LocalDate data) {//Percorrer a lista de entregas
        for (Entrega e : entregas) {
            if (e.getData().equals(data)) {
                return e;
            }
        }
        return null;
    }
    public void removerEntrega(Entrega e){//checa se esta vazio, e remove a entrega
        if(entregas.isEmpty()){
            IO.println("Esse cooperado não possui entregas");
            return;
        }
        boolean removido = entregas.remove(e); // diz se a operação foi concluida

        if (removido) {
            IO.println("Entrega removida!");
        } else {
            IO.println("Entrega não encontrada!");
        }
    }

    public int rasas(int ano, int mes){
        int total = 0;
        for (Entrega e : entregas){
            if (e.getData().getYear() == ano && e.getData().getMonthValue() == mes){
                total += e.getRasas();
            }
        }
        return total;
    }

    public double valorBruto(int ano, int mes){
        double valor = 0;
        for (Entrega e : entregas){
            if (e.getData().getYear() == ano && e.getData().getMonthValue() == mes){
                valor += e.getRasas();
            }
            valor = valor * e.precoRasa();
        }
        return valor;
    }

    public double bonus(int ano, int mes){
        int qtd = 0;
        for (Entrega e : entregas){
            if (e.getData().getYear() == ano && e.getData().getMonthValue() == mes){
                qtd++;
            }
        }
        if (qtd >= 3){
            return valorBruto(ano, mes) * 0.02;
        }
        return 0;
    }

    public double liquido(int ano, int mes, double taxa){
        double bruto = valorBruto(ano, mes);
        double valorTaxa = bruto * taxa / 100;
        return bruto - valorTaxa + bonus(ano, mes);
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getComunidade() {
        return comunidade;
    }

    public void setComunidade(String comunidade) {
        this.comunidade = comunidade;
    }
    @Override
    public String toString() {//Usado no metodo de listar para escrever cada cooperado, troca o print padrão da classe
        return "Matrícula: " + matricula + " | Nome: " + nome + " | Comunidade: " + comunidade;
    }
}
