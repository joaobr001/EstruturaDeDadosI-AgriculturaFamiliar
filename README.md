[README.md](https://github.com/user-attachments/files/34049759/README.md)
# Cooperativa de Açaí — Domínio 5 (Agricultura Familiar)

Trabalho de Implementação **Aplicações de Listas em Java: ArrayList e LinkedList**
Disciplina: **Estrutura de Dados I** — Turma SI-2026
Professor: Adam D. F. dos Santos
Universidade Federal do Sul e Sudeste do Pará (UNIFESSPA) — Faculdade de Sistemas de Informação

---

## 1. Identificação da equipe

| Nome | Matrícula  |
|---------------------------------|--------------|
| Diogo Bentes Godinho Santos     | 202640602003 |
| João Paulo Sousa Mesquita       | 202640602025 |
| Karine Francisca Pardim Messias | 202640602007 |
| Maria Luiza Oliveira Rodrigues  | 202640602012 |
| Samuel Patrício Martins Soares  | 202640602029 |

**Domínio escolhido:** 5 — Agricultura familiar: cooperativa de açaí.

## 2. Sobre o sistema

Aplicação de terminal que gerencia uma cooperativa de extrativistas de açaí. Os cooperados fazem entregas de açaí medidas em **rasas** (cestos de cerca de 14 kg). Todo mês, o sistema calcula quanto cada cooperado deve receber conforme a quantidade e a qualidade entregues, desconta a taxa administrativa da cooperativa e premia a regularidade com um bônus.

O programa lê os dados de um arquivo texto, mantém os objetos em listas (`ArrayList` e `LinkedList`), oferece um menu interativo de manutenção e consulta e gera relatórios em arquivo.

### Regras de negócio

| Regra | Descrição |
|-------|-----------|
| Entregas | Ficam em ordem cronológica (inserção ordenada). Só é aceita **uma entrega por cooperado por dia**, e a quantidade de rasas deve ser **positiva**. |
| Preço por rasa | `ESPECIAL` R$ 95,00 · `PRIMEIRA` R$ 80,00 · `SEGUNDA` R$ 60,00 |
| Valor bruto do mês | Soma de `rasas × preço da qualidade` das entregas do mês |
| Taxa | `bruto × taxaAdministrativa / 100` |
| Bônus de regularidade | 2% do bruto, se o cooperado fez **3 ou mais entregas** no mês |
| Líquido | `bruto − taxa + bônus` |
| Ranking do mês | Cooperados por total de rasas (maior primeiro; empate pela matrícula) |
| Chave | A matrícula identifica o cooperado e não pode se repetir |

## 3. Modelagem

```
Cooperativa  1 ──── * Cooperado  1 ──── * Entrega
 (ArrayList)           (LinkedList)

«enum» Qualidade: ESPECIAL, PRIMEIRA, SEGUNDA
```

| Classe | Atributos | Papel |
|--------|-----------|-------|
| `Cooperativa` | nome, município, taxaAdministrativa, lista de cooperados | Entidade raiz; cadastro, busca, folha de pagamento e ranking |
| `Cooperado` | matrícula, nome, comunidade, lista de entregas | Entidade principal; cálculos mensais (rasas, bruto, bônus, líquido) |
| `Entrega` | data, rasas, qualidade | Elemento composto; preço da rasa e valor da entrega |
| `Qualidade` | — | Enumeração apenas com as constantes |

Classes auxiliares: `LeitorArquivo`, `GravadorArquivo`, `GeradorMassaDados`, `Main` (menu) e as exceções `CooperadoNaoExisteException`, `EntregaNaoExisteException`, `DataInvalidaException`, `QualidadeInvalidaException` e `QuantidadeInvalidaException`.

Os atributos são privados, e cada lista é criada na classe dona e alterada apenas pelos métodos `adicionar...` e `remover...` (composição, RC4). Datas usam `java.time`.

## 4. Escolha das listas (RC3)

| Coleção | Lista | Justificativa |
|---------|-------|---------------|
| Cooperados de uma `Cooperativa` | `ArrayList` | Usada para busca linear por matrícula, percursos completos e ordenação de cópias para relatórios (por matrícula e por ranking). Não há inserção ou remoção frequente no meio, e o `ArrayList` percorre e ordena com menor custo de memória e melhor localidade. |
| Entregas de um `Cooperado` | `LinkedList` | Exigida pelo enunciado. Mantida sempre em ordem cronológica: acha-se a posição `i` do primeiro elemento posterior à nova entrega e insere-se com `add(i, x)`. Entregas novas costumam ser as mais recentes, então a inserção tende a ocorrer no fim ou perto dele. |

Complexidades das operações principais:

| Operação | Onde | Custo |
|----------|------|-------|
| Busca linear por matrícula | `Cooperativa.buscarCooperado` | O(n) |
| Busca de entrega por data | `Cooperado.buscarEntrega` | O(m) |
| Inserção ordenada | `Cooperado.adicionarEntrega` | O(m) para achar a posição + O(1) para inserir na `LinkedList` |
| Cálculo mensal (rasas, bruto, bônus) | `Cooperado` | O(m), com um `for-each` |
| Remoção de entrega | `Cooperado.removerEntrega` | O(m) |

*(n = número de cooperados, m = número de entregas do cooperado.)*

## 5. Algoritmos do RC5

| Item | Descrição | Onde |
|------|-----------|------|
| (a) Busca linear | Percorre a lista comparando a chave e devolve o objeto ou `null` | `Cooperativa.buscarCooperado`, `Cooperado.buscarEntrega` |
| (b) Inserção ordenada | Percorre as entregas até a primeira com data posterior à nova e insere com `add(i, x)`; se não houver, insere no fim | `Cooperado.adicionarEntrega` |
| (c) Remoção condicional | [PREENCHER: indicar a classe e o método quando implementado — laço de índice do fim para o início com `remove(i)`] | [PREENCHER] |
| (d) Ordenação de relatórios | [PREENCHER: indicar a solução adotada — `Comparable` com `Collections.sort` em uma cópia, ou classe auxiliar de linha de relatório] | [PREENCHER] |

## 6. Como compilar e executar (RC1)

**Versão do JDK:** 25 ou superior. O `Main.java` usa recursos do Java 25 (classe implícita com método `main` de instância e a classe `IO`). O projeto no IntelliJ está configurado com o OpenJDK 26.

Execute os comandos **a partir da raiz do repositório**, para que as pastas `dados/` e `saidas/` sejam encontradas:

```bash
# compilar (gera os .class na pasta out/)
javac -d out src/*.java

# executar o programa
java -cp out Main

# (opcional) gerar a massa de dados grande em dados/grande.txt
java -cp out GeradorMassaDados
```

## 7. Formato dos arquivos

### Entrada: `dados/cooperativa.txt`

Campos separados por ponto e vírgula (`;`), datas em `dd/MM/yyyy`, sem acentos.

```
nome;municipio;taxaAdministrativa
matricula;nome;comunidade;N
data;rasas;qualidade        <- N linhas, uma por entrega
```

Exemplo:

```
Cooperativa Mista do Rio Tocantins;Maraba;8.0
C010;Raimunda Ferreira;Vila Espirito Santo;4
03/09/2026;10;ESPECIAL
10/09/2026;12;PRIMEIRA
17/09/2026;8;ESPECIAL
27/08/2026;15;PRIMEIRA
C004;Manoel do Carmo;Ilha de Jatoba;2
05/09/2026;20;SEGUNDA
19/09/2026;18;PRIMEIRA
```

As entregas podem vir fora de ordem (como a de 27/08/2026): a leitura usa a inserção ordenada, e a gravação as regrava em ordem cronológica.

**Tratamento de erros na leitura (RC6):** linha malformada → o programa informa o número da linha, ignora o registro e continua. Arquivo inexistente → avisa e inicia com dados vazios.

### Gravação (RC7)

A opção de salvar grava o estado atual no mesmo formato de entrada, e o arquivo gravado pode ser carregado de novo sem perda de informação. A taxa administrativa é gravada com uma casa decimal.

### Saída: pasta `saidas/` (RC8)

Os relatórios são exibidos no terminal e gravados em arquivo texto. Valores numéricos usam ponto como separador decimal.

| Relatório | Arquivo |
|-----------|---------|
| Folha de pagamento do mês, com total e ranking dos 3 maiores produtores | `saidas/pagamento_MM_yyyy.txt` |
| Cooperados sem entrega no mês | `saidas/sem_entrega_MM_yyyy.txt` |

Saída esperada da folha de pagamento para 09/2026 com o arquivo de exemplo:

```
Folha 09/2026 - Cooperativa Mista do Rio Tocantins (taxa 8.0%)
C004 Manoel do Carmo 38 rasas bruto R$ 2640.00 taxa R$ 211.20 bonus R$ 0.00 liquido R$ 2428.80
C007 Antonia Lopes 22 rasas bruto R$ 1985.00 taxa R$ 158.80 bonus R$ 39.70 liquido R$ 1865.90
C010 Raimunda Ferreira 30 rasas bruto R$ 2670.00 taxa R$ 213.60 bonus R$ 53.40 liquido R$ 2509.80
Total a pagar: R$ 6804.50
Ranking: 1o C004 (38 rasas), 2o C010 (30 rasas), 3o C007 (22 rasas)
```

A entrega de 27/08/2026 não entra no mês de setembro.

### Massa de dados grande (RC10)

`dados/grande.txt`, gerado por `GeradorMassaDados` (semente fixa, então sempre sai igual): 30 cooperados e mais de 100 entregas, com entregas fora de ordem, cooperados sem entrega e cooperados com 3 ou mais entregas no mês.

## 8. Manual de utilização (menu)

Ao executar, o programa mostra o menu e repete até o usuário escolher `0`. Entradas inválidas (texto onde se espera número, data em formato errado, matrícula inexistente, opção inexistente) geram mensagem de erro e não encerram o programa.

```
=== MENU DA COOPERATIVA===
1. Registrar entrega de um cooperado
2. Extrato de um cooperado em um mês
3. Produção por comunidade em um mês
4. Alterar a taxa administrativa da cooperativa
5. Remover cooperador
6. Detalhar um cooperador
7. Adicionar Cooperador
8. Listar Cooperadores
9. Remover Entrega de cooperador
0. Sair
```

| Opção | O que faz | Como usar |
|-------|-----------|-----------|
| 1 | Registra uma entrega | Informe a matrícula, a quantidade de rasas (inteiro maior que 0), a data (`dd/MM/yyyy`) e a qualidade (1-ESPECIAL, 2-PRIMEIRA, 3-SEGUNDA); confirme com `S`. Recusa se o cooperado já tem entrega na data. |
| 2 | Extrato de um cooperado em um mês | Entregas, bruto, taxa, bônus e líquido do mês |
| 3 | Produção por comunidade | Total de rasas por comunidade em um mês |
| 4 | Altera a taxa administrativa | Informe a nova taxa (por exemplo, `8.0`) |
| 5 | Remove um cooperado | Informe a matrícula e confirme com `S` |
| 6 | Detalha um cooperado | Informe a matrícula; mostra nome, matrícula e comunidade |
| 7 | Cadastra um cooperado | Informe nome, matrícula e comunidade |
| 8 | Lista os cooperados | Mostra matrícula, nome e comunidade de cada um |
| 9 | Remove uma entrega | Informe a matrícula e a data da entrega; confirme com `S` |
| 0 | Sai | Encerra o programa |

[PREENCHER: acrescentar um exemplo de tela de cada opção, como pede o documento de funcionamento.]

## 9. Status da implementação

- [x] Classes `Cooperativa`, `Cooperado`, `Entrega` e enumeração `Qualidade`
- [x] Entregas em `LinkedList` com inserção ordenada e regra de uma entrega por dia
- [x] Leitura de arquivo com tratamento de linhas inválidas (`LeitorArquivo`)
- [x] Gravação no formato de entrada (`GravadorArquivo`)
- [x] Gerador de massa de dados (`GeradorMassaDados`)
- [x] Menu com registro, listagem, detalhe, cadastro e remoção
- [ ] `folhaPagamento` e `ranking` em `Cooperativa`
- [ ] Opções 2 e 3 do menu (extrato e produção por comunidade)
- [ ] Menu: buscar por matrícula, salvar, gerar relatórios e perguntar se deseja salvar ao sair
- [ ] Carregar `dados/cooperativa.txt` ao iniciar o programa
- [ ] Relatórios gravados em `saidas/` (folha de pagamento e cooperados sem entrega)
- [ ] Remoção condicional (RC5c) e ordenação dos relatórios (RC5d)

## 10. Limitações conhecidas

[PREENCHER ao final do desenvolvimento.]

## 11. Divisão de tarefas

| Membro | Responsabilidades |
|--------|-------------------|
| João Paulo Sousa Mesquita   | Modelagem (classes, enumeração, cálculos) |
| Karine Francisca P. Messias | Arquivos (leitura, gravação, massa de dados) |
| Diogo Bentes G. Santos      | Interface (menu e validação de entradas) |
| Samuel Patrício M. Soares   | Regras e relatórios |
| Maria Luiza O. Rodrigues    | Documento e testes |

## 12. Estrutura do repositório

```
README.md
src/        código-fonte Java
dados/      cooperativa.txt (exemplo do enunciado) e grande.txt (massa grande)
saidas/     relatórios gerados a partir dos arquivos de exemplo
docs/       documentacao.pdf
slides/     apresentacao.pdf
```
