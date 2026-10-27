import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * Gravacao do estado da cooperativa no mesmo formato de entrada (RC7).
 * O arquivo gravado pode ser carregado de novo por LeitorArquivo sem perda de informacao.
 *
 * Como as entregas de cada cooperado ja ficam em ordem cronologica (insercao ordenada),
 * elas sao gravadas na ordem da lista.
 */
public class GravadorArquivo {

    /** A taxa administrativa e gravada com uma casa decimal (formato do arquivo). */
    public static double arredondarTaxa(double taxa) {
        return Math.round(taxa * 10.0) / 10.0;
    }

    /** Devolve true se gravou com sucesso. */
    public static boolean salvar(Cooperativa cooperativa, String caminho) {
        File arquivo = new File(caminho);
        File pasta = arquivo.getAbsoluteFile().getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        try (PrintWriter saida = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {

            saida.println(limpar(cooperativa.getNome()) + ";"
                    + limpar(cooperativa.getMunicipio()) + ";"
                    + String.format(Locale.US, "%.1f", cooperativa.getTaxaAdministrativa()));

            for (Cooperado c : cooperativa.getCooperados()) {
                List<Entrega> entregas = c.getEntregas();
                saida.println(limpar(c.getMatricula()) + ";"
                        + limpar(c.getNome()) + ";"
                        + limpar(c.getComunidade()) + ";"
                        + entregas.size());
                for (Entrega e : entregas) {
                    saida.println(e.getData().format(LeitorArquivo.FORMATO_DATA) + ";"
                            + e.getRasas() + ";"
                            + e.getQualidade().name());
                }
            }

            if (saida.checkError()) { // PrintWriter nao lanca IOException, so sinaliza
                System.out.println("Erro ao gravar \"" + caminho + "\".");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Erro ao gravar \"" + caminho + "\": " + e.getMessage());
            return false;
        }

        System.out.println("Dados gravados em \"" + caminho + "\".");
        return true;
    }

    /** O ';' e o separador do formato; se aparecer em um texto, e trocado por ','. */
    private static String limpar(String texto) {
        return texto.replace(';', ',').replace('\n', ' ').replace('\r', ' ').trim();
    }
}
