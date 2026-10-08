import java.util.Scanner;
Scanner sc = new Scanner(System.in);
void main(){
    int opcao = -1;
    while (opcao != 0){
        IO.println("=== MENU DA COOPERATIVA===");
        IO.println("1. Registrar entrega de um cooperado");
        IO.println("2. Extrato de um cooperado em um mês");
        IO.println("3. Produção por comunidade em um mês");
        IO.println("4. Alterar a taxa administrativa da cooperativa");
        IO.println("0. Sair");

        opcao = sc.nextInt();
        //lembrar do break
        switch (opcao){
            case 1:
                IO.println("[Opção 1 Selecionada: Registrar entrega de um cooperado]");
                RegistrarEntrega(cooperativa);
                break;
            case 2:
                IO.println("[Opção 2 Selecionada: Extrato do cooperado]");
                break;
            case 3:
                IO.println("[Opção 3 Selecionada: Produção por comunidade:");
                break;
            case 4:
                IO.println("[Opção 4 Selecionada: Alterar taxa administrativa:");
                break;
            case 0:
                IO.println("Encerrando sistema...");
                break;
            default:
                IO.println("Opção inválida! Digite de 0 a 4:");
        }
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
        Entrega novaEntrega = new Entrega(data,rasas,qualidade);
        boolean sucesso = cooperado.adicionarEntrega(novaEntrega);
        if(!sucesso){
            IO.println("Erro, cooperado ja possui entrega nesta data");
        }else{
            IO.println("Sucesso: Entrega registrada!");
        }
    }catch (CooperadoNaoExisteException|DataInvalidaException|QuantidadeInvalidaException|QualidadeInvalidaException e){
        IO.println("Erro:"+e.getMessage());

    }
}