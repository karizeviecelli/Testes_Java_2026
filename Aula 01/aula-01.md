# Aula 01 — Fundamentos de Teste de Software + Revisão de POO

**Módulo:** 1 — Fundamentos
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

Ao final desta aula, você será capaz de:

- Explicar o que é testar software e por que isso é indispensável no desenvolvimento profissional;
- Diferenciar os principais tipos e níveis de teste (unitário, integração, sistema, aceitação);
- Reconhecer a Pirâmide de Testes e o papel de cada camada;
- Retomar, com segurança, os pilares de POO necessários para os testes que faremos ao longo da UC: classes, atributos, métodos, construtores e encapsulamento;
- Criar uma classe de entidade simples, bem encapsulada, pronta para ser testada nas próximas aulas.

---

## 🖼️ Analogia inicial — O Tribunal do Código

Ao longo de toda esta UC, vamos usar uma analogia única: **o código é um cidadão, e todo cidadão pode ser levado a julgamento.**

- Uma **classe** é como uma pessoa: tem características (atributos) e comportamentos (métodos).
- Um **teste** é uma **audiência**: convocamos o "réu" (um objeto da nossa classe), apresentamos **evidências** (entradas) e chegamos a um **veredito** (o resultado é o esperado ou não).
- O **JUnit** é o **sistema judicial** que organiza essas audiências de forma automática, rápida e repetível.
- Mais adiante, vamos conhecer **testemunhas contratadas** (mocks, com o Mockito), aprender a **medir quantos processos já foram julgados** (cobertura de código) e até a **escrever a acusação antes de o crime acontecer** (TDD).

Hoje é o dia de conhecer o tribunal e, principalmente, de garantir que sabemos **quem são os réus** — ou seja, revisar como construímos boas classes em POO, porque não dá pra julgar bem um cidadão mal definido.

---

## 📚 Conteúdo teórico

### 1. O que é testar software?

Testar é o processo de **executar um sistema (ou parte dele) com o objetivo de encontrar defeitos** e verificar se ele se comporta como esperado. Testar não prova que não existem bugs — prova que, **para os cenários testados**, o comportamento foi o esperado.

**Por que testar é importante?**

- Reduz o custo de correção de erros (um bug encontrado em produção custa muito mais do que um bug encontrado durante o desenvolvimento);
- Aumenta a confiança para alterar o código (refatorar) sem "quebrar" o que já funcionava;
- Documenta o comportamento esperado do sistema — um teste bem escrito é também uma forma de documentação viva;
- É uma exigência de mercado: praticamente toda vaga de desenvolvimento espera conhecimento em testes automatizados.

### 2. Tipos e níveis de teste

| Nível | O que verifica | Exemplo |
|---|---|---|
| **Unitário** | Uma unidade isolada de código (um método, uma classe) | Testar se o método `calcularDesconto()` de uma classe `Pedido` retorna o valor correto |
| **Integração** | A comunicação entre duas ou mais unidades/módulos | Testar se o `PedidoRepository` realmente grava um pedido no banco |
| **Sistema** | O sistema como um todo, de ponta a ponta | Testar o fluxo completo de compra, do carrinho ao pagamento |
| **Aceitação** | Se o sistema atende aos requisitos do negócio/usuário | Validar com o cliente se a funcionalidade entregue resolve o problema dele |

### 3. A Pirâmide de Testes

A pirâmide é um modelo que sugere a **proporção ideal** de testes em cada nível:

```
        /\
       /  \      Testes de Sistema/E2E  (poucos, lentos, caros)
      /----\
     /      \    Testes de Integração   (quantidade média)
    /--------\
   /          \  Testes Unitários       (muitos, rápidos, baratos)
  /____________\
```

Nesta UC, vamos trabalhar principalmente na **base da pirâmide** (testes unitários com JUnit 5) e, mais adiante, subiremos até a camada de integração ao testarmos aplicações Spring.

### 4. Revisão de POO — os "réus" do nosso tribunal

Antes de escrever qualquer teste, precisamos saber construir bem uma **classe de entidade**. Vamos revisar os quatro pilares que mais vão aparecer:

**4.1 Classe, atributo e método**

Uma classe é um "molde" que descreve características (atributos) e comportamentos (métodos) de um tipo de objeto.

```java
// Classe de entidade: representa um Produto do nosso sistema
public class Produto {

    // Atributos: características de um Produto
    private String nome;
    private double preco;
    private int quantidadeEmEstoque;

    // Construtor: define como um Produto "nasce"
    public Produto(String nome, double preco, int quantidadeEmEstoque) {
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }

    // Método: um comportamento do Produto
    public boolean estaDisponivel() {
        // Um produto está disponível se houver ao menos 1 unidade em estoque
        return this.quantidadeEmEstoque > 0;
    }

    // Getters: forma controlada de "ler" os atributos de fora da classe
    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeEmEstoque() {
        return quantidadeEmEstoque;
    }
}
```

**4.2 Encapsulamento**

Encapsular significa **proteger o estado interno do objeto**, controlando como ele pode ser lido e alterado. Por isso os atributos são `private`, e usamos métodos públicos (`get`/`set`) para acessá-los de forma controlada.

```java
// Exemplo de encapsulamento "de verdade": o set valida a regra de negócio
public void setPreco(double novoPreco) {
    // Uma classe bem encapsulada não deixa o objeto entrar em um estado inválido
    if (novoPreco < 0) {
        throw new IllegalArgumentException("O preço não pode ser negativo.");
    }
    this.preco = novoPreco;
}
```

> 💡 Isso é exatamente o tipo de regra que, em breve, vamos **testar**: "o que acontece quando eu tento colocar um preço negativo?" — essa pergunta é a semente de um caso de teste.

**4.3 Por que isso importa para testes?**

Cada método público de uma classe bem encapsulada é uma **porta de entrada testável**. Quando a classe:

- tem construtores claros → sabemos como criar o "réu" para a audiência;
- tem métodos com regras de negócio explícitas → sabemos quais vereditos (resultados) esperar;
- protege seu estado interno (encapsulamento) → conseguimos confiar que, se o teste passou, o objeto realmente está em um estado válido.

Uma classe mal projetada (atributos públicos, sem validações, sem métodos claros) é muito mais difícil — às vezes impossível — de testar de forma significativa.

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 60 minutos
**Formato:** individual ou em dupla

### Passo a passo

1. Crie uma classe de entidade chamada `ContaBancaria`, com os atributos: `titular` (String), `numeroConta` (String) e `saldo` (double).
2. Implemente um construtor que receba `titular` e `numeroConta`, iniciando o `saldo` em `0.0`.
3. Implemente os métodos:
   - `depositar(double valor)`: soma o valor ao saldo. Deve lançar `IllegalArgumentException` se `valor` for menor ou igual a zero.
   - `sacar(double valor)`: subtrai o valor do saldo. Deve lançar `IllegalArgumentException` se `valor` for maior que o saldo disponível.
4. Implemente os getters necessários, mas **não** implemente um `setSaldo()` direto — o saldo só deve mudar através de `depositar` e `sacar`.
5. Responda por escrito (2 a 3 frases cada):
   - Quais "perguntas" (casos de teste) você faria a essa classe para ter confiança de que ela está correta?
   - Por que não devemos ter um `setSaldo()` público nessa classe?

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
// Classe de entidade: representa uma Conta Bancária simples
public class ContaBancaria {

    private String titular;
    private String numeroConta;
    private double saldo;

    // Construtor: toda conta nasce com saldo zero
    public ContaBancaria(String titular, String numeroConta) {
        this.titular = titular;
        this.numeroConta = numeroConta;
        this.saldo = 0.0;
    }

    // Depositar: só aceita valores positivos
    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }
        this.saldo += valor;
    }

    // Sacar: só permite valores positivos e que não ultrapassem o saldo
    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
        }
        if (valor > this.saldo) {
            throw new IllegalArgumentException("Saldo insuficiente para este saque.");
        }
        this.saldo -= valor;
    }

    // Getters — leitura controlada do estado da conta
    public String getTitular() {
        return titular;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public double getSaldo() {
        return saldo;
    }

    // Note que NÃO existe um setSaldo(): o saldo só pode mudar
    // através de depositar() e sacar(), que aplicam as regras de negócio.
}
```

**Perguntas de fixação — respostas esperadas:**

- *Quais perguntas (casos de teste) você faria a essa classe?*
  Exemplos válidos: "O saldo realmente aumenta ao depositar um valor válido?", "O que acontece se eu tentar depositar um valor negativo ou zero?", "O saque é recusado quando o valor é maior que o saldo?", "O saldo inicial de uma conta nova é realmente 0.0?". Essas perguntas são exatamente os **casos de teste** que vamos formalizar com JUnit 5 a partir da Aula 3.

- *Por que não devemos ter um `setSaldo()` público?*
  Porque isso quebraria o encapsulamento: qualquer código externo poderia colocar a conta em um estado inválido (ex.: saldo negativo, saldo "mágico" sem passar por depósito/saque), tornando o comportamento da classe imprevisível — e, por consequência, muito mais difícil de testar com confiança.

[« Voltar para a Atividade](#atividade)

---

**Próxima aula:** vamos preparar o "cartório" do nosso tribunal — configurar o IntelliJ IDEA, criar um projeto Maven e entender, linha a linha, o arquivo `pom.xml` que traz a dependência do JUnit 5.
