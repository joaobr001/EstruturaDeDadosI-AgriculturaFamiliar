import java.util.Scanner;
void main(){
    Scanner sc = new Scanner(System.in);
    int opcao = -1;
    while (opcao != 0){
        IO.println("=== MENU DA COOPERATIVA===");
        IO.println("1. Registrar entrega de um cooperado");
        IO.println("2. Extrato de um cooperado em um mês");
        IO.println("3. Produção por comunidade em um mês");
        IO.println("4. Alterar a taxa administrativa da cooperativa");
        IO.println("0. Sair");

        opcao = sc.nextInt();

        switch (opcao){
            case 1:
                IO.println("[Opção 1 Selecionada: Registrar entrega]");
                break;
            case 2:
                IO.println("[Opção 2 Selecionada: Extrato do cooperado]");
                break;
            case 3:
                IO.println("[Opção 3 Selecionada: Produção por comunidade:");
            case 4:
                IO.println("[Opção 4 Selecionada: Alterar taxa administrativa:");
            case 0:
                IO.println("Encerrando sistema...");
            default:
                IO.println("Opção inválida! Digite de 0 a 4:");
        }
    }
}
