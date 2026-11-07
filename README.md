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

Classes auxiliares: `Main` (menu, leitura e gravação de arquivos) e as exceções `CooperadoNaoExisteException`, `EntregaNaoExisteException`, `DataInvalidaException`, `QualidadeInvalidaException` e `QuantidadeInvalidaException`.

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

| Item                        | Descrição                                                                                                                                                                                                                                                | Onde                                                                                                  |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| (a) Busca linear            | Percorre a lista comparando a chave e devolve o objeto ou `null`                                                                                                                                                                                         | `Cooperativa.buscarCooperado`, `Cooperado.buscarEntrega`                                              |
| (b) Inserção ordenada       | Percorre as entregas até a primeira com data posterior à nova e insere com `add(i, x)`; se não houver, insere no fim                                                                                                                                     | `Cooperado.adicionarEntrega`                                                                          |
| (c) Remoção condicional     | Remove todas as entregas anteriores a uma data limite. O laço do índice vai do **fim para o início** com o `remove(i)`: ao remover a posição `i`, o elemento seguinte passa a ocupar `i`, e num laço crescente o índice avançaria sem examiná-lo ( e um `for-each` lançaria `ConcurrentModificationException`). Devolve quantas entregas saíram | `Cooperado.removerEntregasAnteriores`  |
| (d) Ordenação de relatórios | Solução adotada: `Comparable` + `Collections.sort` em uma **cópia** da lista de cooperados (`Cooperado.compareTo` compara pela matrícula), usada nos relatórios em ordem de matrícula. O ranking monta uma lista nova por inserção ordenada (maior total de rasas primeiro; empate pela matrícula). A lista original nunca é alterada | `Cooperado.compareTo`, `Cooperativa.ranking`, `Main.gerarRelatorioPagamento` e `Main.gerarRelatorioSemEntrega` |

## 6. Como compilar e executar (RC1)

**Versão do JDK:** 25 ou superior. O `Main.java` usa recursos do Java 25 (classe implícita com método `main` de instância e a classe `IO`). O projeto no IntelliJ está configurado com o OpenJDK 26.

Execute os comandos **a partir da raiz do repositório**, para que as pastas `dados/` e `saidas/` sejam encontradas:

```bash
# compilar (gera os .class na pasta out/)
javac -d out src/*.java

# executar o programa
java -cp out Main
```

**Testes unitários (Java puro, sem JUnit):** cobrem `Entrega`, `Cooperado` e `Cooperativa`.

```bash
# compilar o código e o teste
javac -d out src/*.java

# executar os testes
java -cp out TesteDominio
```

Resultado esperado na última linha: `Resultado: 44 OK, 0 falha(s), total 44`. O menu, a leitura e a gravação de arquivos (classe `Main`) foram conferidos executando o programa com `dados/cooperativa.txt` e `dados/grande.txt` e comparando as saídas com `dados/esperado.txt` e com os valores calculados à parte.

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

Saída esperada da folha de pagamento para 09/2026 com o arquivo de exemplo (`dados/esperado.txt`):

```
Folha 09-2026 Cooperativa Mista do Rio Tocantins (taxa 8.0%)
C004 Manoel do Carmo 38 rasas bruto R$ 2640.00 taxa R$ 211.20 bonus R$ 0.00 liquido R$ 2428.80
C007 Antonia Lopes 22 rasas bruto R$ 1985.00 taxa R$ 158.80 bonus R$ 39.70 liquido R$ 1865.90
C010 Raimunda Ferreira 30 rasas bruto R$ 2670.00 taxa R$ 213.60 bonus R$ 53.40 liquido R$ 2509.80
Total a pagar: R$ 6804.50
Ranking: 1° C004 (38 rasas), 2° C010 (30 rasas), 3° C007 (22 rasas)
```

A entrega de 27/08/2026 não entra no mês de setembro.

Só entram na folha os cooperados com pelo menos uma entrega no mês, em ordem de matrícula. O ranking mostra no máximo 3 cooperados.

Exemplo do relatório de cooperados sem entrega para 10/2026 com o arquivo de exemplo:

```
Cooperados sem entrega em 10-2026 - Cooperativa Mista do Rio Tocantins
C004 Manoel do Carmo (Ilha de Jatoba)
C007 Antonia Lopes (Vila Espirito Santo)
C010 Raimunda Ferreira (Vila Espirito Santo)
```

Se todos entregaram no mês, o arquivo traz a linha `Todos os cooperados fizeram entregas no mês.`

O programa grava sempre em `saidas/` (com o mesmo nome para o mesmo mês). Por isso, os relatórios gerados com a massa grande foram copiados para `saidas/grande/`.

### Massa de dados grande (RC10)

`dados/grande.txt`: 30 cooperados e mais de 100 entregas, com entregas fora de ordem, cooperados sem entrega e cooperados com 3 ou mais entregas no mês.

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
10. Buscar cooperador pela matricula
11. Remover entregas anteriores a data limite
12. Salvar dados
13. Gerar relatorio de pagamento
14. Gerar relatorio de cooperados sem entrega
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
| 10 | Busca um cooperado pela matrícula | Informe a matrícula; mostra o cooperado ou avisa que não existe |
| 11 | Remove entregas anteriores a uma data limite | Informe a data limite (`dd/MM/yyyy`); a própria data limite não é removida; mostra quantas entregas saíram |
| 12 | Salva os dados | Grava o estado atual em `dados/cooperativa.txt` |
| 13 | Gera o relatório de pagamento | Informe o mês (1 a 12) e o ano; exibe a folha na tela e grava em `saidas/pagamento_MM_yyyy.txt` |
| 14 | Gera o relatório de cooperados sem entrega | Informe o mês (1 a 12) e o ano; exibe a lista na tela e grava em `saidas/sem_entrega_MM_yyyy.txt` |
| 0 | Sai | Pergunta se deseja salvar os dados (`S`/`N`) e encerra o programa |

### Exemplos de tela

Os exemplos usam o arquivo de exemplo (`dados/cooperativa.txt`). O que o usuário digita aparece em uma linha própria, logo abaixo da pergunta. Depois de cada opção, o menu é exibido de novo.

**Tela inicial**

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
9. Remover entrega de Cooperador
10. Buscar cooperador pela matricula
11. Remover entregas anteriores a data limite
12. Salvar dados
13. Gerar relatorio de pagamento
14. Gerar relatorio de cooperados sem entrega
0. Sair
```

**Opção 1 — registrar entrega**

```
[Opção 1 Selecionada:  Registrar entrega]
Insira a matricula do Cooperado:
C004
Insira a quantia de rasas
15
Data da entrega(DD/MM/AAAA)
21/09/2026
Insira a qualidade:1-ESPECIAL 2-PRIMEIRA 3-SEGUNDA
2
Deseja registrar entrega do cooperador Manoel do Carmo ? S/N
S
Sucesso: Entrega registrada!
```

Se o cooperado já tem entrega na data informada (aqui, 05/09/2026), a entrega é recusada:

```
Deseja registrar entrega do cooperador Manoel do Carmo ? S/N
S
Erro, cooperado ja possui entrega nesta data
```

Entradas inválidas mostram uma mensagem e voltam ao menu:

```
Insira a matricula do Cooperado:
C999
Erro:Cooperado não existe!
```

```
Insira a quantia de rasas
-5
Erro:A quantidade deve ser maior que 0!
```

```
Data da entrega(DD/MM/AAAA)
31-09-2026
Erro:Formato de Data Inválido, Utilize DD/MM/AAAA
```

```
Insira a qualidade:1-ESPECIAL 2-PRIMEIRA 3-SEGUNDA
7
Erro:Qualidade inserida não existe!
```

```
Insira a qualidade:1-ESPECIAL 2-PRIMEIRA 3-SEGUNDA
abc
Erro: valor inválido! Digite um número.
```

**Opção 2 — extrato de um cooperado em um mês**

```
[Opção 2 Selecionada: Extrato do cooperado]
Insira a matrícula do cooperado:
C004
Insira o mês (1 a 12):
9
Insira o ano (ex.: 2026):
2026

=== EXTRATO DO COOPERADO ===
Nome: Manoel do Carmo
Matrícula: C004
Comunidade: Ilha de Jatoba

=== ENTREGAS DO MÊS ===
--------------------------
Data: 2026-09-05
Rasas: 20
Qualidade: SEGUNDA
Preço por rasa: R$ 60.0
Valor da entrega: R$ 1200.0
--------------------------
Data: 2026-09-19
Rasas: 18
Qualidade: PRIMEIRA
Preço por rasa: R$ 80.0
Valor da entrega: R$ 1440.0

=== RESUMO FINANCEIRO ===
Quantidade de entregas: 2
Total de rasas: 38
Valor bruto: R$ 2640.00
Taxa administrativa: R$ 211.20
Bônus: R$ 0.00
Valor líquido: R$ 2428.80
```

Com uma matrícula que não existe:

```
Insira a matrícula do cooperado:
C999
Cooperado não encontrado!
```

Com um mês em que o cooperado não entregou (aqui, 10/2026):

```
Insira o ano (ex.: 2026):
2026
Nenhuma entrega registrada neste mês.
```

**Opção 3 — produção por comunidade em um mês**

```
[Opção 3 Selecionada: Produção por comunidade:]
Insira o mês (1 a 12):
9
Insira o ano (ex.: 2026):
2026

=== PRODUÇÃO POR COMUNIDADE ===
Período: 9/2026
--------------------------
Comunidade: Vila Espirito Santo
Total de rasas: 52
--------------------------
Comunidade: Ilha de Jatoba
Total de rasas: 38
--------------------------
PRODUÇÃO TOTAL: 90 rasas
```

As comunidades aparecem na ordem em que surgem no arquivo, e só entram as que tiveram entrega no mês. Se nenhuma comunidade entregou (aqui, 10/2026), aparece `Nenhuma produção registrada neste mês.` Com um mês fora de 1 a 12, aparece `Mês inválido! Digite de 1 a 12.`

**Opção 4 — alterar a taxa administrativa**

```
[Opção 4 Selecionada: Alterar taxa administrativa:]
Insira a nova taxa administrativa:
10.0
```

A taxa é arredondada para uma casa decimal e passa a valer nos relatórios (o cabeçalho da folha passa a mostrar `taxa 10.0%`).

Com texto no lugar do número:

```
Insira a nova taxa administrativa:
abc
Erro: valor inválido! Digite um número.
```

**Opção 5 — remover cooperador**

```
[Opção 5 Selecionada: Remover Cooperador]
Insira a matrícula do cooperador a ser removido
C007
Deseja remover :Antonia Lopes ? S/N
S
Cooperado removido
```

Respondendo `N`, aparece `Operação cancelada`.

Com uma matrícula que não existe:

```
Insira a matrícula do cooperador a ser removido
C999
Erro: Cooperado não existe
```

**Opção 6 — detalhar cooperador**

```
[Opção 6 Selecionada: Detalhar Cooperador]
Insira a matricula do cooperador a ser investigado:
C004
Deseja investigar Manoel do Carmo ? S/N
S
=== DADOS DO COOPERADO ===
Nome: Manoel do Carmo
Matrícula: C004
Comunidade: Ilha de Jatoba

=== ENTREGAS DO COOPERADO ===
--------------
Data: 2026-09-05
Rasas: 20
Qualidade: SEGUNDA
Preço por rasa: R$ 60.0
Valor da entrega: R$ 1200.0
--------------
Data: 2026-09-19
Rasas: 18
Qualidade: PRIMEIRA
Preço por rasa: R$ 80.0
Valor da entrega: R$ 1440.0
```

Com uma matrícula que não existe:

```
Insira a matricula do cooperador a ser investigado:
C999
Erro: Cooperado não existe
```

**Opção 7 — adicionar cooperador**

```
[Opção 7 Selecionada: Adicionar Cooperador]
Deseja adicionar um cooperado novo? S/N
S
Insira o nome do cooperado:
Joao da Silva
Insira a matricula do cooperado:
C050
Insira a comunidade do cooperado:
Ilha de Jatoba
Cooperado cadastrado com sucesso!
```

Com uma matrícula que já está cadastrada:

```
Insira a matricula do cooperado:
C004
Erro: Já existe um cooperado com essa matrícula!
```

**Opção 8 — listar cooperadores**

```
[Opção 8 Selecionada: Listar Cooperadores]
Deseja listar os cooperadores? S/N
S
Matrícula: C010 | Nome: Raimunda Ferreira | Comunidade: Vila Espirito Santo
Matrícula: C004 | Nome: Manoel do Carmo | Comunidade: Ilha de Jatoba
Matrícula: C007 | Nome: Antonia Lopes | Comunidade: Vila Espirito Santo
```

**Opção 9 — remover entrega de um cooperador**

```
[Opção 9 Selecionada: Remover Entrega de cooperador]
Insira a matricula do cooperador para remover sua entrega
C004
Insira a data da entrega:
05/09/2026
Deseja remover a entrega do dia 2026-09-05 ? S/N
S
Entrega removida!
```

Se não há entrega na data informada:

```
Insira a data da entrega:
01/01/2026
Erro: Entrega não encontrada nesta data
```

Com uma data em formato errado:

```
Insira a data da entrega:
abc
Erro: data inválida! Utilize DD/MM/AAAA.
```

**Opção 10 — buscar cooperador pela matrícula**

```
[Opção 10 Selecionada: Buscar cooperador pela matricula]
Insira a matrícula do cooperado:
C004
=== COOPERADO ENCONTRADO ===
Nome: Manoel do Carmo
Matrícula: C004
Comunidade: Ilha de Jatoba
```

Com uma matrícula que não existe:

```
Insira a matrícula do cooperado:
C999
Cooperado não encontrado!
```

**Opção 11 — remover entregas anteriores a uma data limite**

```
[Opção 11 Selecionada: Remover entregas anteriores a data limite]
Insira a matrícula do cooperado:
C010
Insira a data limite (DD/MM/AAAA):
10/09/2026
Deseja remover todas as entregas anteriores a 2026-09-10 de Raimunda Ferreira? S/N
S
2 entrega(s) removida(s)!
```

Com uma data em formato errado:

```
Insira a data limite (DD/MM/AAAA):
99/99/2026
Data inválida! Utilize DD/MM/AAAA.
```

**Opção 12 — salvar dados**

```
[Opção 12 Selecionada: Salvar dados]
Dados salvos em dados/cooperativa.txt
```

**Opção 13 — relatório de pagamento (09/2026)**

```
[Opção 13 Selecionada: Gerar relatório de pagamento]
Insira o mês (1 a 12):
9
Insira o ano (ex.: 2026):
2026
Folha 09-2026 Cooperativa Mista do Rio Tocantins (taxa 8.0%)
C004 Manoel do Carmo 38 rasas bruto R$ 2640.00 taxa R$ 211.20 bonus R$ 0.00 liquido R$ 2428.80
C007 Antonia Lopes 22 rasas bruto R$ 1985.00 taxa R$ 158.80 bonus R$ 39.70 liquido R$ 1865.90
C010 Raimunda Ferreira 30 rasas bruto R$ 2670.00 taxa R$ 213.60 bonus R$ 53.40 liquido R$ 2509.80
Total a pagar: R$ 6804.50
Ranking: 1° C004 (38 rasas), 2° C010 (30 rasas), 3° C007 (22 rasas)

Relatório salvo em saidas/pagamento_09_2026.txt
```

Com um mês fora do intervalo:

```
Insira o mês (1 a 12):
13
Insira o ano (ex.: 2026):
2026
Mês inválido! Digite de 1 a 12.
```

**Opção 14 — cooperados sem entrega (10/2026)**

```
[Opção 14 Selecionada: Gerar relatório de cooperados sem entrega]
Insira o mês (1 a 12):
10
Insira o ano (ex.: 2026):
2026
Cooperados sem entrega em 10-2026 - Cooperativa Mista do Rio Tocantins
C004 Manoel do Carmo (Ilha de Jatoba)
C007 Antonia Lopes (Vila Espirito Santo)
C010 Raimunda Ferreira (Vila Espirito Santo)
Relatório salvo em saidas/sem_entrega_10_2026.txt
```

Quando todos os cooperados entregaram no mês (09/2026):

```
Cooperados sem entrega em 09-2026 - Cooperativa Mista do Rio Tocantins
Todos os cooperados fizeram entregas no mês.
Relatório salvo em saidas/sem_entrega_09_2026.txt
```

**Opção 0 — sair**

```
Deseja salvar os dados antes de sair? (S/N)
S
Dados salvos em dados/cooperativa.txt
Encerrando sistema...
```

Respondendo `N`, o programa encerra sem gravar (`Encerrando sistema...`).

**Opção inexistente ou texto no menu**

```
abc
Opção inválida! Digite um número
99
Opção inválida! Digite de 0 a 14:
```

## 9. Status da implementação

- [x] Classes `Cooperativa`, `Cooperado`, `Entrega` e enumeração `Qualidade`
- [x] Entregas em `LinkedList` com inserção ordenada e regra de uma entrega por dia
- [x] Leitura e gravação de arquivo (`carregarArquivo` e `salvarArquivo` em `Main`)
- [x] Massa de dados grande (`dados/grande.txt`)
- [x] Menu com registro, listagem, detalhe, cadastro e remoção
- [x] `folhaPagamento` e `ranking` em `Cooperativa`
- [x] Opções 2 e 3 do menu (extrato e produção por comunidade)
- [x] Menu: buscar por matrícula, salvar, gerar relatórios e perguntar se deseja salvar ao sair
- [x] Carregar `dados/cooperativa.txt` ao iniciar o programa
- [x] Relatórios gravados em `saidas/` (folha de pagamento e cooperados sem entrega)
- [x] Remoção condicional (RC5c) e ordenação dos relatórios (RC5d)
- [x] Testes unitários das classes de domínio (`src/TesteDominio.java`, 44 testes)

## 10. Limitações conhecidas

- O programa deve ser executado **a partir da raiz do repositório**, porque os caminhos (`dados/` e `saidas/`) são relativos. Se `dados/cooperativa.txt` não for encontrado, o programa avisa e começa com a cooperativa vazia; nesse caso, salvar ao sair gravaria um arquivo vazio por cima.
- Os relatórios das opções 13 e 14 têm nome fixo por mês e ano (`pagamento_MM_aaaa.txt` e `sem_entrega_MM_aaaa.txt`). Gerar de novo sobrescreve o arquivo anterior, e os relatórios da massa grande foram copiados à mão para `saidas/grande/`.
- As opções 2 e 3 só mostram o resultado na tela; não gravam arquivo.
- Ao salvar, o arquivo é regravado com as entregas de cada cooperado em ordem de data, então a ordem das linhas pode mudar em relação ao arquivo original.
- A taxa administrativa (opção 4) é arredondada para uma casa decimal, não mostra mensagem de confirmação e aceita qualquer número, inclusive negativo. O ano informado nos relatórios também não é limitado a um intervalo.
- No extrato (opção 2) e no detalhe do cooperador (opção 6), as datas aparecem como `AAAA-MM-DD` e os valores por rasa e por entrega sem duas casas decimais.
- As comunidades da opção 3 aparecem na ordem em que surgem no arquivo, não em ordem alfabética.

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
src/        código-fonte Java, TesteDominio.java (testes unitários das classes de domínio)
dados/      cooperativa.txt (exemplo do enunciado) e grande.txt (massa grande) e esperado.txt (saída esperada da folha de 09/2026)
saidas/     relatórios gerados com o arquivo de exemplo (saidas/grande/ tem os gerados com a massa grande)
docs/       documentacao.pdf
slides/     apresentacao.pdf
```