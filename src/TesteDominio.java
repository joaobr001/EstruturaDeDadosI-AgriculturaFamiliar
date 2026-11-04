import java.time.LocalDate;
import java.util.List;

public class TesteDominio {
    static int ok = 0, falhas = 0;

    static void verifica(String nome, boolean condicao) {
        if (condicao) { ok++; System.out.println("[OK]    " + nome); }
        else { falhas++; System.out.println("[FALHA] " + nome); }
    }

    static boolean perto(double a, double b) { return Math.abs(a - b) < 0.005; }

    static LocalDate d(int dia, int mes, int ano) { return LocalDate.of(ano, mes, dia); }

    static Entrega e(int dia, int mes, int rasas, Qualidade q) { return new Entrega(d(dia, mes, 2026), rasas, q); }

    public static void main(String[] args) {
        // ---------- Entrega ----------
        verifica("T01 preco ESPECIAL = 95", e(1, 9, 1, Qualidade.ESPECIAL).precoRasa() == 95.0);
        verifica("T02 preco PRIMEIRA = 80", e(1, 9, 1, Qualidade.PRIMEIRA).precoRasa() == 80.0);
        verifica("T03 preco SEGUNDA = 60", e(1, 9, 1, Qualidade.SEGUNDA).precoRasa() == 60.0);
        verifica("T04 valor = rasas x preco (10 x 95)", e(1, 9, 10, Qualidade.ESPECIAL).valor() == 950.0);

        // ---------- Cooperado: insercao ordenada e regras ----------
        Cooperado c = new Cooperado("C010", "Raimunda Ferreira", "Vila Espirito Santo");
        verifica("T05 aceita entrega valida", c.adicionarEntrega(e(3, 9, 10, Qualidade.ESPECIAL)));
        c.adicionarEntrega(e(17, 9, 8, Qualidade.ESPECIAL));
        c.adicionarEntrega(e(10, 9, 12, Qualidade.PRIMEIRA));       // entra no meio
        c.adicionarEntrega(e(27, 8, 15, Qualidade.PRIMEIRA));       // entra no inicio
        List<Entrega> l = c.getEntregas();
        boolean ordenada = l.size() == 4;
        for (int i = 1; i < l.size(); i++) {
            if (l.get(i - 1).getData().isAfter(l.get(i).getData())) ordenada = false;
        }
        verifica("T06 insercao ordenada (inicio, meio e fim) mantem ordem cronologica", ordenada);
        verifica("T07 primeira entrega da lista e a de 27/08", l.getFirst().getData().equals(d(27, 8, 2026)));
        verifica("T08 rejeita 2a entrega no mesmo dia", !c.adicionarEntrega(e(3, 9, 5, Qualidade.SEGUNDA)));
        verifica("T09 lista nao cresce com entrega duplicada", c.getEntregas().size() == 4);
        verifica("T10 rejeita rasas = 0", !c.adicionarEntrega(e(20, 9, 0, Qualidade.SEGUNDA)));
        verifica("T11 rejeita rasas negativas", !c.adicionarEntrega(e(21, 9, -4, Qualidade.SEGUNDA)));

        // ---------- Cooperado: calculos mensais ----------
        verifica("T12 rasas de setembro = 30 (ignora 27/08)", c.rasas(2026, 9) == 30);
        verifica("T13 rasas de agosto = 15", c.rasas(2026, 8) == 15);
        verifica("T14 rasas de mes sem entrega = 0", c.rasas(2026, 10) == 0);
        verifica("T15 rasas de outro ano = 0", c.rasas(2025, 9) == 0);
        verifica("T16 bruto de setembro = 2670.00", perto(c.valorBruto(2026, 9), 2670.00));
        verifica("T17 bonus com 3 entregas = 2% (53.40)", perto(c.bonus(2026, 9), 53.40));
        verifica("T18 liquido de setembro, taxa 8% = 2509.80", perto(c.liquido(2026, 9, 8.0), 2509.80));
        verifica("T19 bonus de agosto (1 entrega) = 0", c.bonus(2026, 8) == 0);
        Cooperado dois = new Cooperado("C004", "Manoel do Carmo", "Ilha de Jatoba");
        dois.adicionarEntrega(e(5, 9, 20, Qualidade.SEGUNDA));
        dois.adicionarEntrega(e(19, 9, 18, Qualidade.PRIMEIRA));
        verifica("T20 sem bonus com 2 entregas (limite 3)", dois.bonus(2026, 9) == 0);
        verifica("T21 liquido sem bonus = 2428.80", perto(dois.liquido(2026, 9, 8.0), 2428.80));
        verifica("T22 taxa 0% => liquido = bruto + bonus", perto(c.liquido(2026, 9, 0.0), 2670.00 + 53.40));

        // ---------- Cooperado: busca e remocao ----------
        verifica("T23 buscarEntrega encontra pela data", c.buscarEntrega(d(10, 9, 2026)) != null);
        verifica("T24 buscarEntrega devolve null se nao existe", c.buscarEntrega(d(11, 9, 2026)) == null);
        Entrega alvo = c.buscarEntrega(d(10, 9, 2026));
        c.removerEntrega(alvo);
        verifica("T25 removerEntrega tira da lista", c.buscarEntrega(d(10, 9, 2026)) == null && c.getEntregas().size() == 3);

        // Remocao condicional: entregas CONSECUTIVAS que atendem a condicao (caso em que um laco crescente pularia)
        Cooperado r = new Cooperado("C099", "Teste Remocao", "Comunidade X");
        for (int dia = 1; dia <= 6; dia++) r.adicionarEntrega(e(dia, 9, 1, Qualidade.SEGUNDA));
        int removidas = r.removerEntregasAnteriores(d(5, 9, 2026));
        verifica("T26 remocao condicional remove todas as consecutivas (4 de 6)", removidas == 4);
        verifica("T27 restam as entregas >= data limite (05 e 06)", r.getEntregas().size() == 2
                && r.getEntregas().getFirst().getData().equals(d(5, 9, 2026)));
        verifica("T28 data limite e exclusiva (entrega no proprio dia fica)", r.buscarEntrega(d(5, 9, 2026)) != null);
        verifica("T29 remocao condicional sem nada a remover devolve 0", r.removerEntregasAnteriores(d(1, 1, 2000)) == 0);
        Cooperado vazio = new Cooperado("C098", "Sem Entregas", "Y");
        verifica("T30 remocao condicional em lista vazia devolve 0", vazio.removerEntregasAnteriores(d(1, 1, 2030)) == 0);
        verifica("T31 remocao condicional pode esvaziar a lista", r.removerEntregasAnteriores(d(1, 1, 2030)) == 2 && r.getEntregas().isEmpty());

        // ---------- Cooperado: Comparable ----------
        verifica("T32 compareTo ordena por matricula", dois.compareTo(c) < 0 && c.compareTo(dois) > 0);

        // ---------- Cooperativa ----------
        Cooperativa coop = new Cooperativa("Cooperativa Mista do Rio Tocantins", "Maraba", 8.0);
        Cooperado c10 = new Cooperado("C010", "Raimunda Ferreira", "Vila Espirito Santo");
        c10.adicionarEntrega(e(3, 9, 10, Qualidade.ESPECIAL));
        c10.adicionarEntrega(e(10, 9, 12, Qualidade.PRIMEIRA));
        c10.adicionarEntrega(e(17, 9, 8, Qualidade.ESPECIAL));
        c10.adicionarEntrega(e(27, 8, 15, Qualidade.PRIMEIRA));
        Cooperado c04 = new Cooperado("C004", "Manoel do Carmo", "Ilha de Jatoba");
        c04.adicionarEntrega(e(5, 9, 20, Qualidade.SEGUNDA));
        c04.adicionarEntrega(e(19, 9, 18, Qualidade.PRIMEIRA));
        Cooperado c07 = new Cooperado("C007", "Antonia Lopes", "Vila Espirito Santo");
        c07.adicionarEntrega(e(2, 9, 6, Qualidade.ESPECIAL));
        c07.adicionarEntrega(e(12, 9, 9, Qualidade.ESPECIAL));
        c07.adicionarEntrega(e(26, 9, 7, Qualidade.PRIMEIRA));
        Cooperado c01 = new Cooperado("C001", "Sem Entrega", "Ilha de Jatoba");
        coop.adicionarCooperado(c10);
        coop.adicionarCooperado(c04);
        coop.adicionarCooperado(c07);
        coop.adicionarCooperado(c01);

        verifica("T33 buscarCooperado encontra pela matricula", coop.buscarCooperado("C007") == c07);
        verifica("T34 buscarCooperado devolve null se nao existe", coop.buscarCooperado("C999") == null);
        verifica("T35 folhaPagamento de 09/2026 = 6804.50", perto(coop.folhaPagamento(2026, 9), 6804.50));
        verifica("T36 folhaPagamento de mes sem entregas = 0", coop.folhaPagamento(2026, 12) == 0);

        List<Cooperado> rank = coop.ranking(2026, 9);
        verifica("T37 ranking exclui quem nao entregou (3 cooperados)", rank.size() == 3);
        verifica("T38 ranking: 1o C004 (38), 2o C010 (30), 3o C007 (22)",
                rank.get(0) == c04 && rank.get(1) == c10 && rank.get(2) == c07);

        Cooperativa emp = new Cooperativa("Empate", "X", 5.0);
        Cooperado z = new Cooperado("C030", "Z", "A"); z.adicionarEntrega(e(1, 9, 10, Qualidade.SEGUNDA));
        Cooperado a = new Cooperado("C020", "A", "A"); a.adicionarEntrega(e(2, 9, 10, Qualidade.SEGUNDA));
        Cooperado m = new Cooperado("C025", "M", "A"); m.adicionarEntrega(e(3, 9, 99, Qualidade.SEGUNDA));
        emp.adicionarCooperado(z); emp.adicionarCooperado(a); emp.adicionarCooperado(m);
        List<Cooperado> r2 = emp.ranking(2026, 9);
        verifica("T39 ranking: empate de rasas desempata por matricula (C020 antes de C030)",
                r2.get(0) == m && r2.get(1) == a && r2.get(2) == z);
        int antes = coop.getCooperados().size();
        coop.ranking(2026, 9);
        verifica("T40 ranking nao altera a lista original (ordena copia)",
                coop.getCooperados().size() == antes && coop.getCooperados().getFirst() == c10);
        coop.removerCooperado(c01);
        verifica("T41 removerCooperado tira da lista", coop.buscarCooperado("C001") == null && coop.getCooperados().size() == 3);

        // ---------- Cooperativa: taxa alterada, ranking vazio e outro ano ----------
        coop.setTaxaAdministrativa(10.0);
        verifica("T42 folhaPagamento acompanha a taxa alterada (10% => 6658.60)", perto(coop.folhaPagamento(2026, 9), 6658.60));
        verifica("T43 ranking de mes sem entregas e lista vazia", coop.ranking(2026, 12).isEmpty());
        verifica("T44 folhaPagamento de outro ano = 0", coop.folhaPagamento(2025, 9) == 0);

        System.out.println();
        System.out.println("Resultado: " + ok + " OK, " + falhas + " falha(s), total " + (ok + falhas));
        if (falhas > 0) System.exit(1);
    }
}
