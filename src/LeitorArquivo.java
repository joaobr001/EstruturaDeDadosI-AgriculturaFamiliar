import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Leitura do arquivo de entrada da cooperativa (dados/cooperativa.txt), conforme RC6.
 *
 * Formato:
 *   linha 1 ........: nome;municipio;taxaAdministrativa
 *   cada cooperado .: matricula;nome;comunidade;N
 *   N linhas .......: data;rasas;qualidade
 *
 * Regras de tolerancia a erros (RC6):
 *  - linha malformada: informa o numero da linha, ignora o registro e continua;
 *  - arquivo inexistente: avisa e devolve uma cooperativa vazia;
 *  - entregas fora de ordem sao aceitas (Cooperado.adicionarEntrega faz a insercao ordenada).
 */
public class LeitorArquivo {

    /** Formato dd/MM/yyyy em modo estrito (recusa datas inexistentes como 31/02/2026). */
    public static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final String SEPARADOR = ";";

    /** Carrega o arquivo indicado. Nunca devolve null. */
    public static Cooperativa carregar(String caminho) {
        File arquivo = new File(caminho);
        if (!arquivo.isFile()) {
            System.out.println("Aviso: arquivo \"" + caminho + "\" nao encontrado. Iniciando com dados vazios.");
            return cooperativaVazia();
        }

        List<String> linhas = lerLinhas(arquivo);
        if (linhas == null) {
            return cooperativaVazia();
        }

        // primeira linha nao vazia = cabecalho da cooperativa
        int i = 0;
        while (i < linhas.size() && linhas.get(i).trim().isEmpty()) {
            i++;
        }
        if (i >= linhas.size()) {
            System.out.println("Aviso: arquivo \"" + caminho + "\" esta vazio. Iniciando com dados vazios.");
            return cooperativaVazia();
        }
        Cooperativa cooperativa = lerCabecalho(linhas.get(i), i + 1);
        i++;

        while (i < linhas.size()) {
            String linha = linhas.get(i);
            int numero = i + 1;
            i++;

            if (linha.trim().isEmpty()) {
                continue;
            }
            if (numeroDeCampos(linha) == 3) {
                aviso(numero, "linha com 3 campos fora de um cooperado (entrega sem cooperado "
                        + "ou cabecalho incompleto); ignorada");
                continue;
            }

            // ---- cabecalho do cooperado: matricula;nome;comunidade;N ----
            String[] p = linha.split(SEPARADOR, -1);
            String erro = null;
            int n = 0;
            String matricula = "";
            String nome = "";
            String comunidade = "";

            if (p.length != 4) {
                erro = "esperados 4 campos (matricula;nome;comunidade;N)";
            } else {
                matricula = p[0].trim();
                nome = p[1].trim();
                comunidade = p[2].trim();
                if (matricula.isEmpty() || nome.isEmpty() || comunidade.isEmpty()) {
                    erro = "matricula, nome e comunidade nao podem ser vazios";
                } else {
                    try {
                        n = Integer.parseInt(p[3].trim());
                        if (n < 0) {
                            erro = "quantidade de entregas negativa";
                        }
                    } catch (NumberFormatException e) {
                        erro = "quantidade de entregas invalida: \"" + p[3].trim() + "\"";
                    }
                }
                if (erro == null && cooperativa.buscarCooperado(matricula) != null) {
                    erro = "matricula duplicada: " + matricula;
                }
            }

            if (erro != null) {
                aviso(numero, erro + "; cooperado ignorado");
                // descarta tambem as linhas de entrega que pertenciam a ele
                while (i < linhas.size() && numeroDeCampos(linhas.get(i)) == 3) {
                    i++;
                }
                continue;
            }

            Cooperado cooperado = new Cooperado(matricula, nome, comunidade);

            // ---- N linhas de entrega: data;rasas;qualidade ----
            int lidas = 0;
            while (lidas < n) {
                while (i < linhas.size() && linhas.get(i).trim().isEmpty()) {
                    i++; // ignora linhas em branco no meio das entregas
                }
                if (i >= linhas.size() || numeroDeCampos(linhas.get(i)) != 3) {
                    aviso(numero, "cooperado " + matricula + " declara " + n
                            + " entrega(s), mas so ha " + lidas + " linha(s) de entrega");
                    break;
                }
                String linhaEntrega = linhas.get(i);
                int numeroEntrega = i + 1;
                i++;
                lidas++;
                lerEntrega(cooperado, linhaEntrega, numeroEntrega);
            }

            cooperativa.adicionarCooperador(cooperado);
        }

        int totalEntregas = 0;
        for (Cooperado c : cooperativa.getCooperados()) {
            totalEntregas += c.getEntregas().size();
        }
        System.out.println("Arquivo \"" + caminho + "\" carregado: "
                + cooperativa.getCooperados().size() + " cooperado(s), " + totalEntregas + " entrega(s).");
        return cooperativa;
    }

    // ------------------------------------------------------------------ auxiliares

    private static Cooperativa cooperativaVazia() {
        return new Cooperativa("Cooperativa", "", 0.0);
    }

    private static List<String> lerLinhas(File arquivo) {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler \"" + arquivo.getPath() + "\": " + e.getMessage()
                    + ". Iniciando com dados vazios.");
            return null;
        }
        // remove o BOM (marca de ordem de bytes) que alguns editores inserem no inicio
        if (!linhas.isEmpty() && linhas.get(0).startsWith("\uFEFF")) {
            linhas.set(0, linhas.get(0).substring(1));
        }
        return linhas;
    }

    private static Cooperativa lerCabecalho(String linha, int numero) {
        String[] p = linha.split(SEPARADOR, -1);
        if (p.length == 3) {
            String nome = p[0].trim();
            String municipio = p[1].trim();
            try {
                double taxa = Double.parseDouble(p[2].trim());
                if (!nome.isEmpty() && taxa >= 0 && taxa <= 100) {
                    return new Cooperativa(nome, municipio, taxa);
                }
            } catch (NumberFormatException e) {
                // cai no aviso abaixo
            }
        }
        aviso(numero, "cabecalho invalido (esperado nome;municipio;taxaAdministrativa); "
                + "usando valores padrao");
        return cooperativaVazia();
    }

    /** Le uma linha de entrega e a adiciona ao cooperado. Em caso de erro, avisa e ignora. */
    private static void lerEntrega(Cooperado cooperado, String linha, int numero) {
        String[] p = linha.split(SEPARADOR, -1);
        LocalDate data;
        int rasas;
        Qualidade qualidade;

        try {
            data = LocalDate.parse(p[0].trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            aviso(numero, "data invalida \"" + p[0].trim() + "\" (use dd/MM/yyyy); entrega ignorada");
            return;
        }
        try {
            rasas = Integer.parseInt(p[1].trim());
        } catch (NumberFormatException e) {
            aviso(numero, "quantidade de rasas invalida \"" + p[1].trim() + "\"; entrega ignorada");
            return;
        }
        if (rasas <= 0) {
            aviso(numero, "a quantidade de rasas deve ser positiva; entrega ignorada");
            return;
        }
        try {
            qualidade = Qualidade.valueOf(p[2].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            aviso(numero, "qualidade invalida \"" + p[2].trim()
                    + "\" (use ESPECIAL, PRIMEIRA ou SEGUNDA); entrega ignorada");
            return;
        }

        if (!cooperado.adicionarEntrega(new Entrega(data, rasas, qualidade))) {
            aviso(numero, "ja existe entrega do cooperado " + cooperado.getMatricula()
                    + " em " + data.format(FORMATO_DATA) + "; entrega ignorada");
        }
    }

    private static int numeroDeCampos(String linha) {
        return linha.split(SEPARADOR, -1).length;
    }

    private static void aviso(int numeroLinha, String mensagem) {
        System.out.println("Aviso (linha " + numeroLinha + "): " + mensagem);
    }
}
