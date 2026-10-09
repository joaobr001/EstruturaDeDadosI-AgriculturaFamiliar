import java.time.LocalDate;

public class Entrega {
    private LocalDate data;
    private int rasas;
    private Qualidade qualidade;

    public Entrega(LocalDate data, int rasas, Qualidade qualidade) {
        this.data = data;
        this.rasas = rasas;
        this.qualidade = qualidade;
    }

    public double precoRasa(){
        return switch (qualidade) {
            case ESPECIAL -> 95.0;
            case PRIMEIRA -> 80.0;
            default -> 60.0;
        };
    }

    public double valor(){
        return rasas * precoRasa();
    }

    public LocalDate getData() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getRasas() {
        return rasas;
    }

    public void setRasas(int rasas) {
        this.rasas = rasas;
    }

    public Qualidade getQualidade() {
        return qualidade;
    }

    public void setQualidade(Qualidade qualidade) {
        this.qualidade = qualidade;
    }
}
