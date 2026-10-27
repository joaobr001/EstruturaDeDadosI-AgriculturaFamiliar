import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Gera a massa de dados grande (RC10) no mesmo formato de dados/cooperativa.txt.
 *
 * Uso:  java GeradorMassaDados [caminhoDeSaida]      (padrao: dados/grande.txt)
 *
 * Caracteristicas do arquivo gerado (semente fixa, entao sempre sai igual):
 *  - 30 cooperados (minimo exigido: 20) e mais de 100 entregas;
 *  - entregas entre 15/08/2026 e 15/10/2026, para haver varios meses nos relatorios;
 *  - 1 entrega por cooperado por dia (regra de negocio);
 *  - alguns cooperados com entregas fora de ordem no arquivo (testa a insercao ordenada);
 *  - 2 cooperados sem nenhuma entrega (testa o relatorio "sem entrega");
 *  - cooperados com 3 ou mais entregas em um mes (testa o bonus de regularidade).
 */
public class GeradorMassaDados {

    private static final int TOTAL_COOPERADOS = 30;
    private static final LocalDate PRIMEIRO_DIA = LocalDate.of(2026, 8, 15);
    private static final int DIAS_DO_PERIODO = 62; // 15/08 a 15/10

    private static final List<String> NOMES = List.of(
            "Raimunda", "Manoel", "Antonia", "Francisco", "Maria", "Raimundo", "Benedita",
            "Joaquim", "Luzia", "Sebastiao", "Francisca", "Pedro", "Rita", "Osmar", "Helena");
    private static final List<String> SOBRENOMES = List.of(
            "Ferreira", "do Carmo", "Lopes", "Santos", "Pereira", "Silva",
            "Costa", "Nunes", "Prado", "Cruz", "Vale", "Rocha");
    private static final List<String> COMUNIDADES = List.of(
            "Vila Espirito Santo", "Ilha de Jatoba", "Ilha de Santa Rosa", "Agrovila Palmares",
            "Comunidade Sao Joao", "Vila Planalto", "Ilha do Capim");

    public static void main(String[] args) {
        String caminho = args.length > 0 ? args[0] : "dados/grande.txt";
        gerar(caminho);
    }

    public static void gerar(String caminho) {
        File arquivo = new File(caminho);
        File pasta = arquivo.getAbsoluteFile().getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        Random sorteio = new Random(2026);
        int totalEntregas = 0;

        try (PrintWriter saida = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {

            saida.println("Cooperativa Mista do Rio Tocantins;Maraba;8.0");

            for (int i = 0; i < TOTAL_COOPERADOS; i++) {
                String matricula = String.format("C%03d", i + 1);
                String nome = NOMES.get(i % NOMES.size()) + " "
                        + SOBRENOMES.get((i * 7) % SOBRENOMES.size());
                String comunidade = COMUNIDADES.get(sorteio.nextInt(COMUNIDADES.size()));

                // dois cooperados nao entregam nada; os demais entregam de 3 a 8 vezes
                int quantidade = (i == 5 || i == 17) ? 0 : 3 + sorteio.nextInt(6);

                // dias distintos (1 entrega por dia): sorteia sem repeticao
                List<Integer> dias = new ArrayList<>();
                for (int d = 0; d < DIAS_DO_PERIODO; d++) {
                    dias.add(d);
                }
                Collections.shuffle(dias, sorteio);
                List<Integer> escolhidos = new ArrayList<>(dias.subList(0, quantidade));

                // a cada 4 cooperados, deixa as entregas fora de ordem no arquivo
                if (i % 4 != 0) {
                    Collections.sort(escolhidos);
                }

                saida.println(matricula + ";" + nome + ";" + comunidade + ";" + quantidade);
                for (int dia : escolhidos) {
                    LocalDate data = PRIMEIRO_DIA.plusDays(dia);
                    int rasas = 4 + sorteio.nextInt(22); // 4 a 25 rasas
                    saida.println(data.format(LeitorArquivo.FORMATO_DATA) + ";" + rasas + ";"
                            + sortearQualidade(sorteio));
                    totalEntregas++;
                }
            }

            if (saida.checkError()) {
                System.out.println("Erro ao gravar \"" + caminho + "\".");
                return;
            }
        } catch (IOException e) {
            System.out.println("Erro ao gerar \"" + caminho + "\": " + e.getMessage());
            return;
        }

        System.out.println("Arquivo \"" + caminho + "\" gerado: " + TOTAL_COOPERADOS
                + " cooperados e " + totalEntregas + " entregas.");
    }

    /** 20% ESPECIAL, 50% PRIMEIRA, 30% SEGUNDA. */
    private static String sortearQualidade(Random sorteio) {
        int x = sorteio.nextInt(100);
        if (x < 20) {
            return "ESPECIAL";
        } else if (x < 70) {
            return "PRIMEIRA";
        }
        return "SEGUNDA";
    }
}
