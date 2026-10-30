import java.util.Scanner;

Scanner sc = new Scanner(System.in);

void exibirMenu(){
    int opcao = -1;
    Cooperativa cooperativa = new Cooperativa("Açai","maraba",8.0);
    while (opcao != 0){

        IO.println("=== MENU DA COOPERATIVA===");
        IO.println("1. Registrar entrega de um cooperado");
        IO.println("2. Extrato de um cooperado em um mês");
        IO.println("3. Produção por comunidade em um mês");
        IO.println("4. Alterar a taxa administrativa da cooperativa");
        IO.println("5. Remover cooperador");
        IO.println("6. Detalhar um cooperador");
        IO.println("7. Adicionar Cooperador");
        IO.println("8. Listar Cooperadores");
        IO.println("9. Remoover entrega de Cooperador");
        IO.println("10. Buscar cooperador pela matricula ");
        IO.println("11. Remover entregadas anteriores a data limite ");
        IO.println("0. Sair");

        opcao = sc.nextInt();
        switch (opcao){
            case 1:
                IO.println("[Opção 1 Selecionada:  Registrar entrega]");
                RegistrarEntrega(cooperativa);
                break;
            case 2:
                IO.println("[Opção 2 Selecionada: Extrato do cooperado]");
                break;
            case 3:
                IO.println("[Opção 3 Selecionada: Produção por comunidade:]");
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
                IO.println("[Opção 10 Selecionada: Buscar cooperador pela matricula");
                buscarCooperador(cooperativa);
                break;
            case 11:
                IO.println("[Opção 11 Selecionada: Remover entregadas anteriores a data limite:");
                removerEntregasAnteriores(cooperativa);
                break;
            case 0:
                IO.println("Encerrando sistema...");
                break;
            default:
                IO.println("Opção inválida! Digite de 0 a 9:");
        }
    }
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
            IO.println("Respota inválida! Digite S ou N");
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

        cooperativa.adicionarCooperador(novoCooperado);

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
            IO.println("Respota inválida! Digite S ou N");
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
    IO.println("Deseja remover :"+ cooperado.getNome()+" ? S/M");
    String confirmacao = sc.next();
    switch (confirmacao.toLowerCase()){
        case "s":
            cooperativa.removerCoopeado(cooperado);
            IO.println("Cooperado removido");
            break;
        case "n":
            IO.println("Operação cancelada");
            break;
        default:
            IO.println("Respota inválida! Digite S ou N");
            break;
    }
}

void RegistrarEntrega(Cooperativa cooperativa){
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
        switch (q){
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
        IO.println("Deseja registrar entrega do cooperador "+cooperado.getNome()+" ? S/N");
        String confirmacao = sc.next();
        if(confirmacao.equalsIgnoreCase("S")){
            Entrega novaEntrega = new Entrega(data,rasas,qualidade);
            boolean sucesso = cooperado.adicionarEntrega(novaEntrega);
            if(!sucesso){
                IO.println("Erro, cooperado ja possui entrega nesta data");
            }else{
                IO.println("Sucesso: Entrega registrada!");
            }
        }else if(confirmacao.equalsIgnoreCase("N")){
            IO.println("Operação cancelada");
        }else{
            IO.println("Respota inválida! Digite S ou N");
        }
    }catch (CooperadoNaoExisteException|DataInvalidaException|QuantidadeInvalidaException|QualidadeInvalidaException e){
        IO.println("Erro:"+e.getMessage());

    }
}

void main(){
    exibirMenu();
}