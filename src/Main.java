import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

Scanner sc = new Scanner(System.in);

void exibirMenu(){
    int opcao = -1;
    Cooperativa cooperativa = carregarArquivo("dados/cooperativa.txt");
    while (opcao != 0) {

        IO.println("=== MENU DA COOPERATIVA===");
        IO.println("1. Registrar entrega de um cooperado");
        IO.println("2. Extrato de um cooperado em um mês");
        IO.println("3. Produção por comunidade em um mês");
        IO.println("4. Alterar a taxa administrativa da cooperativa");
        IO.println("5. Remover cooperador");
        IO.println("6. Detalhar um cooperador");
        IO.println("7. Adicionar Cooperador");
        IO.println("8. Listar Cooperadores");
        IO.println("9. Remover entrega de Cooperador");
        IO.println("10. Buscar cooperador pela matricula");
        IO.println("11. Remover entregas anteriores a data limite");
        IO.println("12. Salvar dados");
        IO.println("13. Gerar relatorio de pagamento");
        IO.println("14. Gerar relatorio de cooperados sem entrega");
        IO.println("0. Sair");
        try {
            opcao = sc.nextInt();
        } catch (InputMismatchException e) {
            IO.println("Opção Inválida! Digite um número");
            sc.next();// remover texto invalido
            continue;
        }
        try {
            switch (opcao) {
                case 1:
                    IO.println("[Opção 1 Selecionada:  Registrar entrega]");
                    RegistrarEntrega(cooperativa);
                    break;
                case 2:
                    IO.println("[Opção 2 Selecionada: Extrato do cooperado]");
                    gerarExtratoCooperado(cooperativa);
                    break;
                case 3:
                    IO.println("[Opção 3 Selecionada: Produção por comunidade:]");
                    gerarProducaoComunidade(cooperativa);
                    break;
                case 4:
                    IO.println("[Opção 4 Selecionada: Alterar taxa administrativa:]");
                    alterarTaxa(cooperativa);
                    break;
                case 5:
                    IO.println("[Opção 5 Selecionada: Remover Cooperador]");
                    removerCoop(cooperativa);
                    break;
                case 6:
                    IO.println("[Opção 6 Selecionada: Detalhar Cooperador]");
                    detalharCooperador(cooperativa);
                    break;
                case 7:
                    IO.println("[Opção 7 Selecionada: Adicionar Cooperador]");
                    addCoop(cooperativa);
                    break;
                case 8:
                    IO.println("[Opção 8 Selecionada: Listar Cooperadores]");
                    listar(cooperativa);
                    break;
                case 9:
                    IO.println("[Opção 9 Selecionada: Remover Entrega de cooperador]");
                    removerEntrega(cooperativa);
                    break;
                case 10:
                    IO.println("[Opção 10 Selecionada: Buscar cooperador pela matricula]");
                    buscarCooperador(cooperativa);
                    break;
                case 11:
                    IO.println("[Opção 11 Selecionada: Remover entregas anteriores a data limite]");
                    removerEntregasAnteriores(cooperativa);
                    break;
                case 12:
                    IO.println("[Opção 12 Selecionada: Salvar dados]");
                    salvarArquivo(cooperativa, "dados/cooperativa.txt");
                    break;
                case 13:
                    IO.println("[Opção 13 Selecionada: Gerar relatório de pagamento");
                    gerarRelatorioPagamento(cooperativa);
                    break;
                case 14:
                    IO.println("[Opção 14 Selecionada: Gerar relatório de cooperados sem entrega]");
                    gerarRelatorioSemEntrega(cooperativa);
                    break;
                case 0:
                    IO.println("Deseja salvar os dados antes de sair? (S/N)");
                    String resposta = sc.next();

                    if (resposta.equalsIgnoreCase("S")) {
                        salvarArquivo(cooperativa, "dados/cooperativa.txt");
                    }
                    IO.println("Encerrando sistema...");
                    break;
                default:
                    IO.println("Opção inválida! Digite de 0 a 14:");
            }
        } catch (InputMismatchException e) {
            sc.next();
            IO.println("Erro: valor inválido! Digite um número.");
        } catch (DateTimeParseException e) {
            IO.println("Erro: data inválida! Utilize DD/MM/AAAA.");
        } catch (RuntimeException e) {
            IO.println("Erro: " + e.getMessage());
        }
    }
}

void gerarExtratoCooperado(Cooperativa cooperativa) {

    IO.println("Insira a matrícula do cooperado:");
    String matricula = sc.next();

    Cooperado c = cooperativa.buscarCooperado(matricula);

    if (c == null) {
        IO.println("Cooperado não encontrado!");
        return;
    }

    int[] mesAno = lerMesAno();

    if (mesAno == null) {
        return;
    }

    int mes = mesAno[0];
    int ano = mesAno[1];

    StringBuilder texto = new StringBuilder();

    texto.append("\n=== EXTRATO DO COOPERADO ===\n");
    texto.append("Nome: " + c.getNome() + "\n");
    texto.append("Matrícula: " + c.getMatricula() + "\n");
    texto.append("Comunidade: " + c.getComunidade() + "\n");

    texto.append("\n=== ENTREGAS DO MÊS ===\n");

    int quantidade = 0;

    for (Entrega e : c.getEntregas()) {

        if (e.getData().getMonthValue() == mes &&
                e.getData().getYear() == ano) {

            texto.append("--------------------------\n");
            texto.append("Data: " + e.getData() + "\n");
            texto.append("Rasas: " + e.getRasas() + "\n");
            texto.append("Qualidade: " + e.getQualidade() + "\n");
            texto.append("Preço por rasa: R$ " + e.precoRasa() + "\n");
            texto.append("Valor da entrega: R$ " + e.valor() + "\n");

            quantidade++;
        }
    }

    if (quantidade == 0) {
        IO.println("Nenhuma entrega registrada neste mês.");
        return;
    }

    double bruto = c.valorBruto(ano, mes);

    double taxa = bruto *
            cooperativa.getTaxaAdministrativa() / 100;

    double bonus = c.bonus(ano, mes);

    double liquido = c.liquido(
            ano,
            mes,
            cooperativa.getTaxaAdministrativa()
    );

    texto.append("\n=== RESUMO FINANCEIRO ===\n");
    texto.append("Quantidade de entregas: " + quantidade + "\n");
    texto.append("Total de rasas: " + c.rasas(ano, mes) + "\n");

    texto.append(String.format(Locale.US,
            "Valor bruto: R$ %.2f%n", bruto));

    texto.append(String.format(Locale.US,
            "Taxa administrativa: R$ %.2f%n", taxa));

    texto.append(String.format(Locale.US,
            "Bônus: R$ %.2f%n", bonus));

    texto.append(String.format(Locale.US,
            "Valor líquido: R$ %.2f%n", liquido));

    IO.print(texto.toString());
}


void gerarProducaoComunidade(Cooperativa cooperativa) {

    // Solicitar mês e ano
    int[] mesAno = lerMesAno();

    if (mesAno == null) {
        return;
    }

    int mes = mesAno[0];
    int ano = mesAno[1];

    // Criar listas para armazenar comunidades e produções
    ArrayList<String> comunidades = new ArrayList<>();
    ArrayList<Integer> producoes = new ArrayList<>();

    // Percorrer todos os cooperados
    for (Cooperado c : cooperativa.getCooperados()) {

        int rasas = c.rasas(ano, mes);

        if (rasas == 0) {
            continue;
        }

        String comunidade = c.getComunidade();

        // Verificar se a comunidade já está cadastrada
        int indice = comunidades.indexOf(comunidade);

        if (indice == -1) {

            comunidades.add(comunidade);
            producoes.add(rasas);

        } else {

            int totalAtual = producoes.get(indice);

            producoes.set(indice, totalAtual + rasas);
        }
    }

    // Exibir relatório
    IO.println("\n=== PRODUÇÃO POR COMUNIDADE ===");
    IO.println("Período: " + mes + "/" + ano);

    if (comunidades.isEmpty()) {
        IO.println("Nenhuma produção registrada neste mês.");
        return;
    }

    int totalGeral = 0;

    for (int i = 0; i < comunidades.size(); i++) {

        String comunidade = comunidades.get(i);
        int total = producoes.get(i);

        IO.println("--------------------------");
        IO.println("Comunidade: " + comunidade);
        IO.println("Total de rasas: " + total);

        totalGeral += total;
    }

    IO.println("--------------------------");
    IO.println("PRODUÇÃO TOTAL: " + totalGeral + " rasas");
}



void removerEntregasAnteriores(Cooperativa cooperativa) {
    IO.println("Insira a matrícula do cooperado:");
    String matricula = sc.next();

    Cooperado cooperado = cooperativa.buscarCooperado(matricula);

    if (cooperado == null) {
        IO.println("Cooperado não encontrado!");
        return;
    }
    IO.println("Insira a data limite (DD/MM/AAAA):");
    String dataStr = sc.next();
    try {
        LocalDate data = LocalDate.parse(
                dataStr,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        IO.println("Deseja remover todas as entregas anteriores a " + data + " de " + cooperado.getNome() + "? S/N");

        String confirmacao = sc.next();
        if (confirmacao.equalsIgnoreCase("S")) {
            int removidas = cooperado.removerEntregasAnteriores(data);

            IO.println(removidas + " entrega(s) removida(s)!");
        } else if (confirmacao.equalsIgnoreCase("N")) {
            IO.println("Operação cancelada");
        } else {
            IO.println("Resposta inválida! Digite S ou N");
        }
    } catch (DateTimeParseException e) {
        IO.println("Data inválida! Utilize DD/MM/AAAA.");
    }
}

void buscarCooperador(Cooperativa cooperativa){
    IO.println("Insira a matrícula do cooperado:");
    String matricula = sc.next();

    Cooperado cooperado = cooperativa.buscarCooperado(matricula);

    if(cooperado == null){
        IO.println("Cooperado não encontrado!");
    }else{
        IO.println("=== COOPERADO ENCONTRADO ===");
        IO.println("Nome: " + cooperado.getNome());
        IO.println("Matrícula: " + cooperado.getMatricula());
        IO.println("Comunidade: " + cooperado.getComunidade());
    }
}

void listar(Cooperativa cooperativa){
    IO.println("Deseja listar os cooperadores? S/N");
    String confirmacao = sc.next();
    switch (confirmacao.toLowerCase()){
        case "s":
            cooperativa.listarCooperador();
            break;
        case "n":
            IO.println("Operação cancelada");
            break;
        default:
            IO.println("Resposta inválida! Digite S ou N");
            break;
    }
}

void addCoop(Cooperativa cooperativa){
    IO.println("Deseja adicionar um cooperado novo? S/N");

    String confirmacao = sc.next();

    if(confirmacao.equalsIgnoreCase("S")){

        sc.nextLine(); // limpar quebra de linha

        IO.println("Insira o nome do cooperado:");
        String nome = sc.nextLine();

        IO.println("Insira a matricula do cooperado:");
        String matricula = sc.nextLine();

        // Verificar se a matrícula já existe
        if(cooperativa.buscarCooperado(matricula) != null){
            throw new CooperadoNaoExisteException(
                    "Já existe um cooperado com essa matrícula!"
            );
        }

        IO.println("Insira a comunidade do cooperado:");
        String comunidade = sc.nextLine();

        Cooperado novoCooperado = new Cooperado(matricula, nome, comunidade);

        cooperativa.adicionarCooperado(novoCooperado);

        IO.println("Cooperado cadastrado com sucesso!");

    }else if(confirmacao.equalsIgnoreCase("N")){
        IO.println("Operação cancelada");
    }else{
        IO.println("Resposta inválida! Digite S ou N");
    }
}

void removerEntrega(Cooperativa cooperativa){
    IO.println("Insira a matricula do cooperador para remover sua entrega");
    String matricula = sc.next();
    Cooperado cooperado = cooperativa.buscarCooperado(matricula);
    if(cooperado == null){
        throw new CooperadoNaoExisteException("Cooperado inexistente");
    }
    IO.println("Insira a data da entrega:");
    String dataStr = sc.next();
    LocalDate data = LocalDate.parse(dataStr,DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    Entrega entrega = cooperado.buscarEntrega(data);//Buscar entrega pela data e faz a entrega apontar pro resultado
    if(entrega == null){
        throw new EntregaNaoExisteException("Entrega não encontrada nesta data");
    }
    IO.println("Deseja remover a entrega do dia "+entrega.getData()+" ? S/N");
    String confirmacao = sc.next();
    switch (confirmacao.toLowerCase()){
        case "s":
            cooperado.removerEntrega(entrega);
            break;
        case "n":
            IO.println("Operação cancelada");
            break;
        default:
            IO.println("Resposta inválida! Digite S ou N");
            break;
    }
}

void detalharCooperador(Cooperativa cooperativa){
    IO.println("Insira a matricula do cooperador a ser investigado:");
    String matricula = sc.next();

    Cooperado cooperado = cooperativa.buscarCooperado(matricula);

    if(cooperado == null){
        throw new CooperadoNaoExisteException("Cooperado não existe");
    }

    IO.println("Deseja investigar " + cooperado.getNome() + " ? S/N");
    String confirmacao = sc.next();

    switch (confirmacao.toLowerCase()){
        case "s":
            IO.println("=== DADOS DO COOPERADO ===");
            IO.println("Nome: " + cooperado.getNome());
            IO.println("Matrícula: " + cooperado.getMatricula());
            IO.println("Comunidade: " + cooperado.getComunidade());

            IO.println("\n=== ENTREGAS DO COOPERADO ===");

            if(cooperado.getEntregas().isEmpty()){
                IO.println("Nenhuma entrega registrada.");
            }else{
                for(Entrega e : cooperado.getEntregas()){
                    IO.println("--------------");
                    IO.println("Data: " + e.getData());
                    IO.println("Rasas: " + e.getRasas());
                    IO.println("Qualidade: " + e.getQualidade());
                    IO.println("Preço por rasa: R$ " + e.precoRasa());
                    IO.println("Valor da entrega: R$ " + e.valor());
                }
            }
            break;

        case "n":
            IO.println("Operação cancelada");
            break;

        default:
            IO.println("Resposta inválida! Digite S ou N");
            break;
    }
}

void alterarTaxa(Cooperativa cooperativa){
    IO.println("Insira a nova taxa administrativa:");

    double novaTaxa = sc.nextDouble();

    novaTaxa = Math.round(novaTaxa * 10.0) / 10.0;

    cooperativa.setTaxaAdministrativa(novaTaxa);
}

void removerCoop(Cooperativa cooperativa){
    IO.println("Insira a matrícula do cooperador a ser removido");
    String matricula = sc.next();
    Cooperado cooperado = cooperativa.buscarCooperado(matricula);
    if(cooperado == null){
        throw new CooperadoNaoExisteException("Cooperado não existe");
    }
    IO.println("Deseja remover :"+ cooperado.getNome()+" ? S/N");
    String confirmacao = sc.next();
    switch (confirmacao.toLowerCase()){
        case "s":
            cooperativa.removerCooperado(cooperado);
            IO.println("Cooperado removido");
            break;
        case "n":
            IO.println("Operação cancelada");
            break;
        default:
            IO.println("Resposta inválida! Digite S ou N");
            break;
    }
}

void RegistrarEntrega(Cooperativa cooperativa) {
    IO.println("Insira a matricula do Cooperado:");
    String matricula = sc.next();
    //validar se matricula existe dentro da lista da cooperativa
    try {
        Cooperado cooperado = cooperativa.buscarCooperado(matricula);
        if (cooperado == null) {
            throw new CooperadoNaoExisteException("Cooperado não existe!");
        }
        // receber quantia de rasas
        int rasas;
        IO.println("Insira a quantia de rasas");
        //validar se quantia de rasas é inteiro
        try {
            rasas = sc.nextInt();
        } catch (InputMismatchException e) {
            sc.next();//limpar buffer
            throw new QuantidadeInvalidaException("A quantidade deve ser um número inteiro!");
        }
        //validar se quantia de rasas é > 0, caso sim, continuar
        if (rasas <= 0) {
            throw new QuantidadeInvalidaException("A quantidade deve ser maior que 0!");
        }
        //validar a data no formato dia mes e ano
        IO.println("Data da entrega(DD/MM/AAAA)");
        String dataStr = sc.next();
        LocalDate data;
        try {
            data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException("Formato de Data Inválido, Utilize DD/MM/AAAA");
        }
        //verificar se qualidade existe
        IO.println("Insira a qualidade:1-ESPECIAL 2-PRIMEIRA 3-SEGUNDA");
        int q = sc.nextInt();
        Qualidade qualidade = null;
        switch (q) {
            case 1:
                qualidade = Qualidade.ESPECIAL;
                break;
            case 2:
                qualidade = Qualidade.PRIMEIRA;
                break;
            case 3:
                qualidade = Qualidade.SEGUNDA;
                break;
            default:
                throw new QualidadeInvalidaException("Qualidade inserida não existe!");
        }

        //Validar se ja tem entrega, caso não, registrar ela e as informações
        IO.println("Deseja registrar entrega do cooperador " + cooperado.getNome() + " ? S/N");
        String confirmacao = sc.next();
        if (confirmacao.equalsIgnoreCase("S")) {
            Entrega novaEntrega = new Entrega(data, rasas, qualidade);
            boolean sucesso = cooperado.adicionarEntrega(novaEntrega);
            if (!sucesso) {
                IO.println("Erro, cooperado ja possui entrega nesta data");
            } else {
                IO.println("Sucesso: Entrega registrada!");
            }
        } else if (confirmacao.equalsIgnoreCase("N")) {
            IO.println("Operação cancelada");
        } else {
            IO.println("Resposta inválida! Digite S ou N");
        }
    } catch (CooperadoNaoExisteException | DataInvalidaException | QuantidadeInvalidaException |
             QualidadeInvalidaException e) {
        IO.println("Erro:" + e.getMessage());

    }
}
Cooperativa carregarArquivo(String caminho){
    File arquivo = new File(caminho);
    if (!arquivo.exists()){
        IO.println("Aviso: arquivo " + caminho + " não encontrado. Iniciando com dados vazios.");
        return new Cooperativa("Cooperativa", "", 0.0);
    }
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    int numLinha = 0;
    try (Scanner leitor = new Scanner(arquivo)){
        if (!leitor.hasNextLine()){
            return new Cooperativa("Cooperativa", "", 0.0);
        }
        numLinha++;
        String[] cab = leitor.nextLine().split(";");
        Cooperativa cooperativa = new Cooperativa(cab[0].trim(), cab[1].trim(), Double.parseDouble(cab[2].trim()));

        while (leitor.hasNextLine()){
            numLinha++;
            String linha = leitor.nextLine().trim();
            if (linha.isEmpty()) continue;
            try {
                String[] p = linha.split(";");
                String matricula = p[0].trim();
                String nome = p[1].trim();
                String comunidade = p[2].trim();
                int n = Integer.parseInt(p[3].trim());
                Cooperado c = new Cooperado(matricula, nome, comunidade);
                for (int i = 0; i < n && leitor.hasNextLine(); i++){
                    numLinha++;
                    String linhaEntrega = leitor.nextLine().trim();
                    try {
                        String[] pe = linhaEntrega.split(";");
                        LocalDate data = LocalDate.parse(pe[0].trim(), fmt);
                        int rasas = Integer.parseInt(pe[1].trim());
                        Qualidade q = Qualidade.valueOf(pe[2].trim().toUpperCase());
                        c.adicionarEntrega(new Entrega(data, rasas, q));
                    } catch (Exception e){
                        IO.println("Linha " + numLinha + " inválida (entrega ignorada).");
                    }
                }
                cooperativa.adicionarCooperado(c);
            } catch (Exception e){
                IO.println("Linha " + numLinha + " inválida (registro ignorado).");
            }
        }
        return cooperativa;
    } catch (Exception e){
        IO.println("Erro ao ler arquivo na linha " + numLinha + ". Iniciando com dados vazios.");
        return new Cooperativa("Cooperativa", "", 0.0);
    }
}

void salvarArquivo(Cooperativa cooperativa, String caminho){
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    try (PrintWriter pw = new PrintWriter(caminho)){
        pw.printf(Locale.US, "%s;%s;%.1f%n", cooperativa.getNome(), cooperativa.getMunicipio(), cooperativa.getTaxaAdministrativa());
        for (Cooperado c : cooperativa.getCooperados()){
            pw.println(c.getMatricula() + ";" + c.getNome() + ";" + c.getComunidade() + ";" + c.getEntregas().size());
            for (Entrega e : c.getEntregas()){
                pw.println(e.getData().format(fmt) + ";" + e.getRasas() + ";" + e.getQualidade().name());
            }
        }
        IO.println("Dados salvos em " + caminho);
    } catch (Exception e){
        IO.println("Erro ao salvar arquivo: " + e.getMessage());
    }
}

int[] lerMesAno(){
    try{
        IO.println("Insira o mês (1 a 12): ");
        int mes = sc.nextInt();
        IO.println("Insira o ano (ex.: 2026): ");
        int ano = sc.nextInt();
        if(mes < 1 || mes > 12){
            IO.println("Mês inválido! Digite de 1 a 12.");
            return null;
        }
        return new int[]{mes,ano};
    }catch(InputMismatchException e){
        sc.next();
        IO.println("Valor inválido! Digite apenas números");
        return null;
    }
}

void gravarRelatorio(String caminho, String texto){
    new File("saidas").mkdirs();
    try(PrintWriter pw = new PrintWriter(caminho)){
        pw.print(texto);
        IO.println("Relatório salvo em " + caminho);
    }catch(Exception e){
        IO.println("Erro ao salvar relatório: " + e.getMessage());
    }
}
void gerarRelatorioPagamento(Cooperativa cooperativa){
    int[] mesAno = lerMesAno();
    if(mesAno == null){
        return;
    }
    int mes = mesAno[0];
    int ano = mesAno[1];
    double taxa = cooperativa.getTaxaAdministrativa();

    ArrayList<Cooperado> ordenados = new ArrayList<>(cooperativa.getCooperados());

    Collections.sort(ordenados);

    StringBuilder texto = new StringBuilder();

    texto.append(String.format(Locale.US, "Folha %02d-%d %s (taxa %.1f%%)%n",
            mes, ano, cooperativa.getNome(), taxa));

    for(Cooperado c : ordenados){
        int rasas = c.rasas(ano, mes);
        if(rasas == 0){
            continue;
        }
        double bruto = c.valorBruto(ano, mes);
        double valorTaxa = bruto * taxa / 100;
        double bonus = c.bonus(ano, mes);
        double liquido = c.liquido(ano, mes, taxa);
        texto.append(String.format(Locale.US, "%s %s %d rasas bruto R$ %.2f taxa R$ %.2f bonus R$ %.2f liquido R$ %.2f%n",
                c.getMatricula(), c.getNome(), rasas, bruto, valorTaxa, bonus, liquido));
    }
    texto.append(String.format(Locale.US, "Total a pagar: R$ %.2f%n",
            cooperativa.folhaPagamento(ano,mes)));

    List<Cooperado> ranking = cooperativa.ranking(ano,mes);
    texto.append("Ranking: ");
    int limite = Math.min(3, ranking.size());

    for(int i = 0; i < limite; i++){
        Cooperado c = ranking.get(i);
        if(i > 0){
            texto.append(", ");
        }
        texto.append(String.format("%d° %s (%d rasas)",
                i + 1, c.getMatricula(), c.rasas(ano, mes)));
    }
    texto.append(String.format("%n"));

    IO.println(texto);
    gravarRelatorio(String.format("saidas/pagamento_%02d_%d.txt", mes, ano), texto.toString());
}

void gerarRelatorioSemEntrega(Cooperativa cooperativa){
    int[] mesAno = lerMesAno();
    if(mesAno == null){
        return;
    }
    int mes = mesAno[0];
    int ano = mesAno[1];

    ArrayList<Cooperado> ordenados = new ArrayList<>(cooperativa.getCooperados());
    Collections.sort(ordenados);

    StringBuilder texto = new StringBuilder();
    texto.append(String.format("Cooperados sem entrega em %02d-%d - %s%n",
            mes, ano, cooperativa.getNome()));

    int quantidade = 0;
    for(Cooperado c : ordenados){
        if(c.rasas(ano, mes) == 0){
            texto.append(c.getMatricula() + " " + c.getNome()
                    + " (" + c.getComunidade() + ")" + System.lineSeparator());
            quantidade++;
        }
    }   // <- aqui termina o for

    // estas linhas ficam DEPOIS do for: rodam uma vez só, com a lista já completa
    if(quantidade == 0){
        texto.append("Todos os cooperados fizeram entregas no mês." + System.lineSeparator());
    }
    IO.print(texto);
    gravarRelatorio(String.format("saidas/sem_entrega_%02d_%d.txt", mes, ano), texto.toString());
}

void main(){
    Locale.setDefault(Locale.US);
    exibirMenu();
}
