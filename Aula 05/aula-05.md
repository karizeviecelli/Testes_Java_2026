# Aula 05 — Testes Parametrizados com JUnit 5

**Unidade curricular:** Teste de Sistemas  
**Carga horária:** 4 horas  
**Tema central:** executar a mesma regra de teste com diferentes conjuntos de dados

---

## 1. Objetivos de aprendizagem

Ao final da aula, o estudante deverá ser capaz de:

- reconhecer testes repetidos que podem ser parametrizados;
- usar `@ParameterizedTest` no JUnit 5;
- escolher entre `@ValueSource`, `@CsvSource` e `@MethodSource`;
- relacionar as colunas da fonte aos parâmetros do método de teste;
- criar nomes legíveis para cada execução;
- testar casos comuns, fronteiras e valores inválidos;
- usar `@Timeout` com cautela;
- interpretar qual conjunto de dados provocou uma falha.

## 2. Organização sugerida das 4 horas

| Etapa | Tempo | Estratégia |
|---|---:|---|
| Retomada da Aula 4 | 15 min | Revisão de AAA, `assertEquals` e `assertThrows` |
| Exposição dialogada | 55 min | Slides, comparação de códigos e quiz |
| Demonstração ao vivo | 35 min | Construção da suíte `DescontoTest` |
| Intervalo | 10 min | — |
| Prática guiada | 70 min | Implementação em sete etapas |
| Desafio autônomo | 30 min | Testes da `CalculadoraFrete` |
| Socialização e feedback | 25 min | Leitura de falhas e rubrica formativa |

---

## 3. Ideia central: mesma pergunta, dados diferentes

Um teste parametrizado separa duas coisas:

1. **a regra de verificação**, escrita uma única vez no corpo do método;
2. **os dados dos cenários**, fornecidos por uma anotação ou por um método.

Se quatro casos usam a mesma ação e a mesma asserção, mas mudam apenas a entrada e o resultado esperado, provavelmente existe uma oportunidade de parametrização.

### Antes: repetição de código

```java
@Test
void descontoDe10PorCentoEm100() {
    assertEquals(90.0, Desconto.calcular(100.0, 10), 0.001);
}

@Test
void descontoDe20PorCentoEm200() {
    assertEquals(160.0, Desconto.calcular(200.0, 20), 0.001);
}

@Test
void descontoZeroMantemPreco() {
    assertEquals(80.0, Desconto.calcular(80.0, 0), 0.001);
}
```

### Depois: regra única com vários conjuntos

```java
@ParameterizedTest
@CsvSource({
    "100.0, 10,  90.0",
    "200.0, 20, 160.0",
    " 80.0,  0,  80.0"
})
void calcularDeveAplicarPercentual(
        double preco,
        int percentual,
        double esperado) {

    // Act: executa a regra com os dados da linha atual.
    double obtido = Desconto.calcular(preco, percentual);

    // Assert: compara valores double usando tolerância.
    assertEquals(esperado, obtido, 0.001);
}
```

Três linhas no `@CsvSource` produzem três execuções independentes.

---

## 4. Classe de produção usada na aula

Crie em `src/main/java/org/example/Desconto.java`:

```java
package org.example;

/**
 * Classe utilitária responsável pelo cálculo de descontos.
 */
public final class Desconto {

    // Evita que alguém crie objetos de uma classe que só possui método estático.
    private Desconto() {
    }

    /**
     * Calcula o preço após aplicar um percentual de desconto.
     *
     * @param preco preço original, maior ou igual a zero
     * @param percentual percentual entre 0 e 100
     * @return preço final depois do desconto
     */
    public static double calcular(double preco, int percentual) {
        // A validação ocorre antes de qualquer cálculo.
        if (preco < 0) {
            throw new IllegalArgumentException(
                    "O preço não pode ser negativo."
            );
        }

        // Zero e cem são valores válidos e representam as fronteiras.
        if (percentual < 0 || percentual > 100) {
            throw new IllegalArgumentException(
                    "O percentual deve estar entre 0 e 100."
            );
        }

        // A divisão por 100.0 mantém o cálculo em ponto flutuante.
        double valorDoDesconto = preco * percentual / 100.0;

        return preco - valorDoDesconto;
    }
}
```

## 5. Preparação do projeto

Os testes parametrizados pertencem ao módulo de parâmetros do JUnit 5. Em projetos Maven, a dependência agregadora é suficiente:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.4</version>
    <scope>test</scope>
</dependency>
```

Imports mais usados:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
```

> Todos os imports desta aula começam com `org.junit.jupiter`. Se aparecer `org.junit.Test`, o projeto está misturando JUnit 4 e JUnit 5.

---

## 6. `@ValueSource`: um argumento simples

```java
@ParameterizedTest(name = "preço inválido: {0}")
@ValueSource(doubles = {-0.01, -1.0, -100.0})
void precoNegativoDeveLancarExcecao(double preco) {
    // Arrange: o preço vem da anotação e o percentual é fixo.
    int percentual = 10;

    // Act: assertThrows executa a expressão e captura a exceção.
    IllegalArgumentException excecao = assertThrows(
        IllegalArgumentException.class,
        () -> Desconto.calcular(preco, percentual)
      );

    // Assert: a mensagem documenta a regra de negócio.
     assertEquals(
        "O preço não pode ser negativo.",
        excecao.getMessage()
      );
    }
```

Use quando apenas um valor varia entre as execuções.

```java
// Indica que este é um teste parametrizado.
// Isso significa que o mesmo teste será executado várias vezes,
// recebendo um valor diferente em cada execução.
//
// {0} representa o primeiro argumento recebido pelo método.
// Exemplos de nomes exibidos no relatório:
// "preço inválido: -0.01"
// "preço inválido: -1.0"
// "preço inválido: -100.0"
@ParameterizedTest(name = "preço inválido: {0}")

// Fornece os valores que serão enviados ao parâmetro "preco".
// Como o parâmetro é double, usamos doubles.
//
// O teste será executado três vezes:
// 1ª execução: preco = -0.01
// 2ª execução: preco = -1.0
// 3ª execução: preco = -100.0
@ValueSource(doubles = {-0.01, -1.0, -100.0})
void precoNegativoDeveLancarExcecao(double preco) {

    // ARRANGE — Preparação
    //
    // O preço não precisa ser criado aqui, pois é recebido
    // como parâmetro por meio do @ValueSource.
    //
    // O percentual de desconto será o mesmo nas três execuções.
    int percentual = 10;

    // ACT — Ação
    //
    // O assertThrows verifica se o código executado dentro da
    // expressão lambda lança a exceção esperada.
    //
    // IllegalArgumentException.class:
    // informa qual tipo de exceção esperamos.
    //
    // () -> Desconto.calcular(preco, percentual):
    // é uma expressão lambda que representa o código que será executado.
    //
    // Se Desconto.calcular() não lançar a exceção, o teste falha.
    // Se lançar outra exceção, o teste também falha.
    //
    // A exceção lançada é capturada e armazenada na variável "excecao".
    IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> Desconto.calcular(preco, percentual)
    );

    // ASSERT — Verificação
    //
    // Além de verificar o tipo da exceção, também conferimos
    // se a mensagem está correta.
    //
    // excecao.getMessage() recupera a mensagem da exceção lançada.
    assertEquals(
            "O preço não pode ser negativo.", // Resultado esperado
            excecao.getMessage()              // Resultado obtido
    );
}
```

Cada valor chega separadamente ao parâmetro `preco`.

Tipos suportados incluem `strings`, `ints`, `longs`, `doubles`, `floats`, `shorts`, `bytes`, `chars`, `booleans` e `classes`.

##    6.1. O que é um teste parametrizado?

Um teste parametrizado recebe diferentes dados sem que seja necessário repetir o código:
```java
@ValueSource(doubles = {-0.01, -1.0, -100.0})
```

É equivalente a escrever três testes separados:
```java
precoNegativoDeveLancarExcecao(-0.01);
precoNegativoDeveLancarExcecao(-1.0);
precoNegativoDeveLancarExcecao(-100.0);
```
A vantagem é evitar repetição e facilitar a inclusão de novos casos.

##    6.2. O padrão AAA

O teste está organizado segundo o padrão Arrange–Act–Assert:

|Etapa	|Significado	|Neste teste|
|:-------|:----------:|---------------:|
|Arrange|	Preparar os dados	|Define percentual = 10|
|Act	|Executar o comportamento	|Chama Desconto.calcular()|
|Assert	|Conferir o resultado	|Verifica a mensagem da exceção|
---------------------------------------------------------------

##    6.3. O que assertThrows verifica?

```java
IllegalArgumentException excecao = assertThrows(
    IllegalArgumentException.class,
    () -> Desconto.calcular(preco, percentual)
);
```

Ele verifica duas coisas:

   -    1 - O método realmente lançou uma exceção.
   -    2 - A exceção é do tipo IllegalArgumentException.

Além disso, devolve a exceção capturada, permitindo verificar sua mensagem.

##    6.4. O que é a expressão lambda?
```java
() -> Desconto.calcular(preco, percentual)
```
Essa expressão significa:

**“Quando o assertThrows solicitar, execute o método Desconto.calcular.”**

Ela poderia ser representada de maneira mais extensa:
```java
() -> {
    Desconto.calcular(preco, percentual);
}
```

A chamada não pode ser feita diretamente assim:

```java
// Incorreto
assertThrows(
    IllegalArgumentException.class,
    Desconto.calcular(preco, percentual)
);
```
O assertThrows precisa receber uma ação para executar e monitorar, não o resultado imediato do método.


##    6.5. Por que conferir a mensagem?

Verificar apenas a classe da exceção confirma que ocorreu um erro, mas não garante que ele aconteceu pelo motivo correto.
```java
assertEquals(
    "O preço não pode ser negativo.",
    excecao.getMessage()
);
```
Esse assertEquals compara:
```java
assertEquals(resultadoEsperado, resultadoObtido);
```
Assim, o teste documenta precisamente a regra de negócio.

Imports necessários
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
```
**Pequeno desafio:**

Por que esse teste não utiliza 0.0 entre os valores inválidos? 

Isso depende de outra regra: **preço zero é permitido ou também deve lançar exceção?**


## 7. `@CsvSource`: vários argumentos simples

Cada String representa uma execução. As colunas são enviadas aos parâmetros na mesma ordem.

```java
@ParameterizedTest(
        name = "caso {index}: R$ {0} - {1}% deve resultar em R$ {2}"
)
@CsvSource({
    "100.00,  10,  90.00",
    "200.00,  25, 150.00",
    " 80.00,   0,  80.00",
    " 50.00, 100,   0.00"
})
void calcularDeveAplicarPercentual(
        double preco,
        int percentual,
        double esperado) {

    // Act
    double obtido = Desconto.calcular(preco, percentual);

    // Assert: esperado, obtido e delta.
    assertEquals(esperado, obtido, 0.001);
}
```


## Objetivo do teste

Esse código verifica se o método `Desconto.calcular()` aplica corretamente diferentes percentuais de desconto.

Em vez de criar quatro métodos de teste, usamos um **teste parametrizado**. O JUnit executa o mesmo método quatro vezes, usando uma linha diferente do `@CsvSource` em cada execução.

Pense no `@CsvSource` como uma pequena tabela:

|     Preço | Desconto | Resultado esperado |
| --------: | -------: | -----------------: |
| R$ 100,00 |      10% |           R$ 90,00 |
| R$ 200,00 |      25% |          R$ 150,00 |
|  R$ 80,00 |       0% |           R$ 80,00 |
|  R$ 50,00 |     100% |            R$ 0,00 |

## Código completamente comentado

```java
// Indica que este método é um teste parametrizado.
// Isso permite executar o mesmo teste várias vezes,
// utilizando diferentes conjuntos de valores.
@ParameterizedTest(

        // Define como cada execução aparecerá no relatório do JUnit.
        //
        // {index} = número da execução do teste.
        // {0}     = primeiro valor recebido: preço.
        // {1}     = segundo valor recebido: percentual.
        // {2}     = terceiro valor recebido: resultado esperado.
        //
        // Exemplo exibido no relatório:
        // caso 1: R$ 100.00 - 10% deve resultar em R$ 90.00
        name = "caso {index}: R$ {0} - {1}% deve resultar em R$ {2}"
)

// Fornece os dados que serão usados pelo teste.
//
// Cada String representa uma execução.
// Os valores são separados por vírgulas e enviados,
// na mesma ordem, aos parâmetros do método de teste.
@CsvSource({

    // preco = 100.00
    // percentual = 10
    // esperado = 90.00
    "100.00,  10,  90.00",

    // preco = 200.00
    // percentual = 25
    // esperado = 150.00
    "200.00,  25, 150.00",

    // Caso de fronteira: desconto de 0%.
    // O preço deve permanecer igual.
    " 80.00,   0,  80.00",

    // Caso de fronteira: desconto de 100%.
    // O resultado deve ser zero.
    " 50.00, 100,   0.00"
})

// Nome descritivo do comportamento esperado.
//
// O método será executado quatro vezes.
// Em cada execução, os parâmetros receberão
// os valores de uma linha do @CsvSource.
void calcularDeveAplicarPercentual(

        // Recebe o primeiro valor de cada linha.
        double preco,

        // Recebe o segundo valor de cada linha.
        int percentual,

        // Recebe o terceiro valor de cada linha.
        double esperado) {

    // ACT — Ação
    //
    // Executa o comportamento que está sendo testado.
    // O método calcular() recebe o preço e o percentual.
    //
    // O resultado devolvido é armazenado na variável "obtido".
    double obtido = Desconto.calcular(preco, percentual);

    // ASSERT — Verificação
    //
    // Compara o resultado esperado com o resultado obtido.
    //
    // 1º argumento: valor esperado.
    // 2º argumento: valor obtido pelo método.
    // 3º argumento: delta ou margem de tolerância.
    //
    // O teste será aprovado se a diferença entre os valores
    // for menor ou igual a 0.001.
    assertEquals(esperado, obtido, 0.001);
}
```

## Entendendo cada parte

### 1. `@ParameterizedTest`

```java
@ParameterizedTest
```

Essa anotação informa ao JUnit:

> “Este método não será executado apenas uma vez. Ele receberá diferentes conjuntos de dados.”

Sem teste parametrizado, seria necessário escrever algo parecido com:

```java
@Test
void deveAplicarDezPorCento() {
    double obtido = Desconto.calcular(100.00, 10);
    assertEquals(90.00, obtido, 0.001);
}

@Test
void deveAplicarVinteECincoPorCento() {
    double obtido = Desconto.calcular(200.00, 25);
    assertEquals(150.00, obtido, 0.001);
}
```

O teste parametrizado reduz repetição e facilita a inclusão de novos casos.

---

### 2. Nome de cada execução

```java
name = "caso {index}: R$ {0} - {1}% deve resultar em R$ {2}"
```

Os marcadores são substituídos pelos valores de cada execução:

| Marcador  | Representa                      |
| --------- | ------------------------------- |
| `{index}` | Número da execução              |
| `{0}`     | Primeiro argumento: `preco`     |
| `{1}`     | Segundo argumento: `percentual` |
| `{2}`     | Terceiro argumento: `esperado`  |

O relatório ficará semelhante a:

```text
caso 1: R$ 100.00 - 10% deve resultar em R$ 90.00
caso 2: R$ 200.00 - 25% deve resultar em R$ 150.00
caso 3: R$ 80.00 - 0% deve resultar em R$ 80.00
caso 4: R$ 50.00 - 100% deve resultar em R$ 0.00
```

Isso ajuda a identificar rapidamente qual conjunto de dados falhou.

---

### 3. `@CsvSource`

```java
@CsvSource({
    "100.00, 10, 90.00",
    "200.00, 25, 150.00"
})
```

CSV significa **Comma-Separated Values**, ou valores separados por vírgulas.

Cada linha representa um cenário:

```text
preço, percentual, resultado esperado
```

O JUnit converte automaticamente os textos para os tipos declarados no método:

```java
void calcularDeveAplicarPercentual(
    double preco,
    int percentual,
    double esperado
)
```

A relação acontece pela posição:

```text
"100.00, 10, 90.00"
     ↓     ↓     ↓
  preco percentual esperado
```

Portanto, a ordem dos valores precisa corresponder à ordem dos parâmetros.

---

### 4. Onde está o Arrange?

O teste segue o padrão AAA:

* **Arrange:** preparação;
* **Act:** ação;
* **Assert:** verificação.

Neste caso, o `Arrange` está implicitamente no `@CsvSource`:

```java
@CsvSource({
    "100.00, 10, 90.00"
})
```

Os valores já chegam preparados ao método:

```java
double preco,
int percentual,
double esperado
```

Assim, o corpo do teste precisa apenas executar e verificar.

---

### 5. Cálculo esperado

Para calcular o desconto, podemos usar:

```text
desconto = preço × percentual ÷ 100
```

Depois:

```text
valor final = preço − desconto
```

No primeiro cenário:

```text
desconto = 100 × 10 ÷ 100
desconto = 10

valor final = 100 − 10
valor final = 90
```

O teste espera:

```java
esperado = 90.00;
```

---

## Por que utilizar o delta?

O teste usa:

```java
assertEquals(esperado, obtido, 0.001);
```

Para números inteiros, normalmente fazemos uma comparação direta:

```java
assertEquals(10, resultado);
```

Entretanto, valores `double` podem apresentar pequenas imprecisões porque são armazenados em representação binária.

Um cálculo que deveria produzir:

```text
90.00
```

poderia, em determinadas operações, resultar internamente em algo como:

```text
89.999999999
```

O `delta` define uma margem aceitável:

```java
0.001
```

O JUnit verifica:

```text
|esperado − obtido| ≤ delta
```

Por exemplo:

```text
esperado = 90.000
obtido   = 89.9995

diferença = 0.0005
```

Como `0.0005` é menor que `0.001`, o teste passa.

O terceiro argumento não é uma quantidade de casas decimais. Ele é uma **tolerância numérica**.

## Casos de fronteira presentes

O teste não verifica apenas valores comuns. Ele também cobre os limites válidos:

```java
"80.00, 0, 80.00"
```

Com desconto de `0%`, o preço deve permanecer igual.

```java
"50.00, 100, 0.00"
```

Com desconto de `100%`, o resultado deve ser zero.

Esses testes são importantes porque erros costumam aparecer nos limites das regras.

## Imports necessários

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
```

Pergunta de verificação: se acrescentarmos a linha abaixo, quais valores serão recebidos por `preco`, `percentual` e `esperado`?

```java
"300.00, 50, 150.00"
```

## 8. `@MethodSource`: objetos e dados construídos

Use quando a anotação ficaria difícil de ler ou quando os casos incluem objetos.
```java
@ParameterizedTest(name = "{0}")
@MethodSource("cenariosDeDesconto")
void calcularDeveAtenderCenarios(
        String descricao,
        double preco,
        int percentual,
        double esperado) {

    double obtido = Desconto.calcular(preco, percentual);

    // A descrição também funciona como mensagem em caso de falha.
    assertEquals(esperado, obtido, 0.001, descricao);
}

static Stream<Arguments> cenariosDeDesconto() {
    return Stream.of(
        Arguments.of("sem desconto", 80.0, 0, 80.0),
        Arguments.of("desconto parcial", 200.0, 25, 150.0),
        Arguments.of("desconto total", 50.0, 100, 0.0)
    );
}
```

O método fornecedor é static por padrão e retorna um fluxo de argumentos.

Antes de observar os detalhes, pense: e se cada cenário de teste precisasse receber não apenas números, mas também objetos como `Produto`, `Cliente` ou `Cupom`? O `@CsvSource` trabalha bem com dados simples, mas começaria a ficar limitado. É nesse ponto que o `@MethodSource` se torna útil.

## O que esse código faz?

Esse é um teste parametrizado do JUnit 5 que verifica três situações:

| Descrição        |     Preço | Desconto | Resultado esperado |
| ---------------- | --------: | -------: | -----------------: |
| Sem desconto     |  R$ 80,00 |       0% |           R$ 80,00 |
| Desconto parcial | R$ 200,00 |      25% |          R$ 150,00 |
| Desconto total   |  R$ 50,00 |     100% |            R$ 0,00 |

O método `cenariosDeDesconto()` fornece os dados. Para cada `Arguments.of(...)`, o JUnit executa novamente o teste `calcularDeveAtenderCenarios()`.

É como uma professora que prepara uma lista de exercícios e entrega cada questão, uma de cada vez, para o mesmo procedimento de correção.

## Código completamente comentado

```java
// Indica que o método abaixo é um teste parametrizado.
//
// Diferentemente de @Test, que normalmente executa o método uma vez,
// @ParameterizedTest permite executar o mesmo teste várias vezes,
// utilizando conjuntos diferentes de dados.
//
// name = "{0}" define o nome de cada execução no relatório.
// {0} representa o primeiro argumento recebido pelo teste,
// que, neste caso, é a variável "descricao".
@ParameterizedTest(name = "{0}")

// Informa que os argumentos do teste serão fornecidos
// pelo método chamado "cenariosDeDesconto".
//
// O nome escrito aqui deve ser exatamente igual ao nome
// do método fornecedor declarado mais abaixo.
@MethodSource("cenariosDeDesconto")
void calcularDeveAtenderCenarios(

        // Recebe o primeiro valor de Arguments.of().
        // É usado para identificar e explicar o cenário.
        String descricao,

        // Recebe o segundo valor: preço original.
        double preco,

        // Recebe o terceiro valor: percentual do desconto.
        int percentual,

        // Recebe o quarto valor: resultado que esperamos obter.
        double esperado) {

    // ACT — Ação
    //
    // Executa o método que está sendo testado.
    // O resultado calculado é guardado na variável "obtido".
    double obtido = Desconto.calcular(preco, percentual);

    // ASSERT — Verificação
    //
    // Compara o resultado esperado com o resultado obtido.
    //
    // 1º argumento: resultado esperado;
    // 2º argumento: resultado obtido;
    // 3º argumento: delta, ou margem de tolerância;
    // 4º argumento: mensagem exibida se o teste falhar.
    //
    // O teste será aprovado se:
    // |esperado - obtido| <= 0.001
    assertEquals(esperado, obtido, 0.001, descricao);
}

// Método fornecedor dos argumentos.
//
// Ele é static porque, por padrão, o JUnit precisa acessar
// os dados antes de criar uma instância da classe de teste.
static Stream<Arguments> cenariosDeDesconto() {

    // Stream.of() cria um fluxo contendo os diferentes
    // conjuntos de argumentos utilizados pelo teste.
    return Stream.of(

        // Primeira execução:
        // descricao = "sem desconto"
        // preco = 80.0
        // percentual = 0
        // esperado = 80.0
        Arguments.of(
            "sem desconto",
            80.0,
            0,
            80.0
        ),

        // Segunda execução:
        // descricao = "desconto parcial"
        // preco = 200.0
        // percentual = 25
        // esperado = 150.0
        Arguments.of(
            "desconto parcial",
            200.0,
            25,
            150.0
        ),

        // Terceira execução:
        // descricao = "desconto total"
        // preco = 50.0
        // percentual = 100
        // esperado = 0.0
        Arguments.of(
            "desconto total",
            50.0,
            100,
            0.0
        )
    );
}
```

Imports necessários:

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
```

---

#  Conceitos aplicados

## 1. Teste parametrizado

A anotação:

```java
@ParameterizedTest(name = "{0}")
```

indica que o mesmo teste será executado várias vezes.

Sem parametrização, precisaríamos criar três métodos:

```java
@Test
void deveManterPrecoSemDesconto() {
    assertEquals(80.0, Desconto.calcular(80.0, 0), 0.001);
}

@Test
void deveAplicarDescontoParcial() {
    assertEquals(150.0, Desconto.calcular(200.0, 25), 0.001);
}

@Test
void deveAplicarDescontoTotal() {
    assertEquals(0.0, Desconto.calcular(50.0, 100), 0.001);
}
```

O teste parametrizado evita repetição sem eliminar os diferentes cenários.

## 2. O que é `@MethodSource`?

```java
@MethodSource("cenariosDeDesconto")
```

Essa anotação informa ao JUnit:

> “Busque os dados do teste no método chamado `cenariosDeDesconto`.”

O método fornecedor é:

```java
static Stream<Arguments> cenariosDeDesconto()
```

A ligação ocorre pelo nome:

```text
@MethodSource("cenariosDeDesconto")
                       │
                       ▼
static Stream<Arguments> cenariosDeDesconto()
```

Se o nome estiver escrito incorretamente, o JUnit não encontrará a fonte dos argumentos e o teste apresentará erro.

## 3. O que é `Arguments`?

Cada `Arguments.of()` representa uma linha ou um cenário:

```java
Arguments.of("desconto parcial", 200.0, 25, 150.0)
```

Os dados são associados aos parâmetros do teste pela posição:

| Posição | Valor fornecido      | Parâmetro que recebe |
| ------: | -------------------- | -------------------- |
|       0 | `"desconto parcial"` | `String descricao`   |
|       1 | `200.0`              | `double preco`       |
|       2 | `25`                 | `int percentual`     |
|       3 | `150.0`              | `double esperado`    |

Portanto, a ordem e os tipos devem ser compatíveis.

Este cenário produz uma chamada equivalente a:

```java
calcularDeveAtenderCenarios(
    "desconto parcial",
    200.0,
    25,
    150.0
);
```

## 4. O que significa `{0}`?

```java
@ParameterizedTest(name = "{0}")
```

O marcador `{0}` representa o primeiro argumento do teste:

```java
String descricao
```

Assim, as execuções aparecem aproximadamente desta maneira no relatório:

```text
sem desconto
desconto parcial
desconto total
```

Também poderíamos criar um nome mais detalhado:

```java
@ParameterizedTest(
    name = "caso {index}: {0} — R$ {1} com {2}% deve resultar em R$ {3}"
)
```

Os marcadores seriam:

| Marcador  | Significado        |
| --------- | ------------------ |
| `{index}` | Número da execução |
| `{0}`     | Descrição          |
| `{1}`     | Preço              |
| `{2}`     | Percentual         |
| `{3}`     | Resultado esperado |

## 5. O que é `Stream<Arguments>`?

```java
Stream<Arguments>
```

Um `Stream` representa uma sequência de elementos que podem ser processados.

Neste caso, cada elemento é um objeto do tipo `Arguments`:

```java
return Stream.of(
    Arguments.of(...),
    Arguments.of(...),
    Arguments.of(...)
);
```

Podemos entender o fluxo assim:

```text
cenário 1 → cenário 2 → cenário 3
```

O JUnit percorre esse fluxo e executa o teste para cada elemento.

### Por que usar `Stream`?

O `Stream` permite:

* fornecer vários cenários;
* gerar casos de teste dinamicamente;
* filtrar ou transformar dados;
* construir dados usando lógica Java;
* trabalhar com objetos complexos.

## 6. Por que o método fornecedor é `static`?

```java
static Stream<Arguments> cenariosDeDesconto()
```

Normalmente, o JUnit cria uma nova instância da classe de teste para cada execução. O método `static` pertence à classe, e não a uma instância específica.

Isso permite ao JUnit obter os dados independentemente da criação de um objeto da classe de teste.

Analogia: um método `static` funciona como um material disponível na sala dos professores; não é necessário chamar uma professora específica para acessá-lo.

## 7. O padrão AAA

O teste segue o padrão Arrange–Act–Assert, embora o `Arrange` esteja fora do corpo do teste.

### Arrange — preparação

Os dados são preparados no método fornecedor:

```java
Arguments.of("desconto parcial", 200.0, 25, 150.0)
```

### Act — ação

O método é executado:

```java
double obtido = Desconto.calcular(preco, percentual);
```

### Assert — verificação

O resultado é comparado:

```java
assertEquals(esperado, obtido, 0.001, descricao);
```

## 8. Por que utilizar o `delta`?

```java
assertEquals(esperado, obtido, 0.001, descricao);
```

O `double` utiliza representação binária e pode apresentar pequenas diferenças de precisão.

O JUnit verifica:

```text
|esperado − obtido| ≤ 0.001
```

Exemplo:

```text
esperado = 150.0000
obtido   = 149.9995
diferença = 0.0005
```

Como `0.0005` é menor que `0.001`, o teste passa.

O `delta` não representa a quantidade de casas decimais. Ele representa a diferença máxima aceitável.

## 9. A mensagem de falha

```java
assertEquals(esperado, obtido, 0.001, descricao);
```

O quarto argumento é uma mensagem adicional.

Se o cenário `"desconto parcial"` falhar, a mensagem ajuda a identificar o contexto:

```text
desconto parcial ==> expected: <150.0> but was: <...>
```

A descrição cumpre duas funções:

* nomeia a execução por meio de `{0}`;
* complementa a mensagem de falha do `assertEquals`.

---

#  Análise aprofundada

## 1. Por que usar `@MethodSource` em vez de `@CsvSource`?

Para esses valores simples, as duas opções funcionariam. O `@MethodSource` se destaca quando os dados precisam ser construídos com Java.

| Característica                 | `@CsvSource`   | `@MethodSource`       |
| ------------------------------ | -------------- | --------------------- |
| Números e textos simples       | Excelente      | Funciona              |
| Objetos personalizados         | Limitado       | Excelente             |
| Dados calculados               | Pouco adequado | Excelente             |
| Reutilização da fonte          | Limitada       | Maior                 |
| Leitura rápida de poucos casos | Melhor         | Pode ser mais extenso |
| Lógica para gerar cenários     | Não            | Sim                   |
| Segurança de tipos             | Menor          | Maior                 |

Exemplo com `@CsvSource`:

```java
@CsvSource({
    "80.0, 0, 80.0",
    "200.0, 25, 150.0"
})
```

Os dados são inicialmente escritos como texto e convertidos pelo JUnit.

Com `@MethodSource`:

```java
Arguments.of("desconto parcial", 200.0, 25, 150.0)
```

Os valores já são objetos Java com seus respectivos tipos.

## 2. Uso com objetos complexos

A maior vantagem aparece quando o teste utiliza objetos.

Considere:

```java
class Produto {

    private final String nome;
    private final double preco;

    Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public double getPreco() {
        return preco;
    }
}
```

O método fornecedor pode criar diferentes produtos:

```java
static Stream<Arguments> cenariosComProdutos() {
    return Stream.of(
        Arguments.of(
            "Notebook com 10% de desconto",
            new Produto("Notebook", 3000.0),
            10,
            2700.0
        ),
        Arguments.of(
            "Mouse sem desconto",
            new Produto("Mouse", 100.0),
            0,
            100.0
        )
    );
}
```

E o teste recebe o objeto diretamente:

```java
@ParameterizedTest(name = "{0}")
@MethodSource("cenariosComProdutos")
void deveCalcularDescontoDoProduto(
        String descricao,
        Produto produto,
        int percentual,
        double esperado) {

    double obtido = Desconto.calcular(
        produto.getPreco(),
        percentual
    );

    assertEquals(esperado, obtido, 0.001, descricao);
}
```

Isso seria muito mais difícil de representar diretamente em um `@CsvSource`.

## 3. O método fornecedor pode gerar dados dinamicamente

Como o fornecedor é um método Java, ele pode usar cálculos:

```java
static Stream<Arguments> cenariosDeDesconto() {
    double precoBase = 100.0;

    return Stream.of(
        Arguments.of("sem desconto", precoBase, 0, precoBase),
        Arguments.of("metade do preço", precoBase, 50, precoBase / 2),
        Arguments.of("desconto total", precoBase, 100, 0.0)
    );
}
```

Também pode transformar coleções:

```java
static Stream<Arguments> cenariosDeDesconto() {
    return List.of(0, 25, 50, 100)
            .stream()
            .map(percentual -> {
                double preco = 200.0;
                double esperado =
                        preco - preco * percentual / 100.0;

                return Arguments.of(
                        percentual + "% de desconto",
                        preco,
                        percentual,
                        esperado
                );
            });
}
```

Contudo, existe um cuidado: se o resultado esperado for calculado com a mesma fórmula da implementação, o teste pode repetir o mesmo erro do código de produção. Para casos de negócio importantes, valores esperados explícitos costumam ser mais claros.

## 4. Método fornecedor não está limitado a `Stream`

O `@MethodSource` aceita outras formas de retorno, como:

```java
static List<Arguments> cenariosDeDesconto() {
    return List.of(
        Arguments.of("sem desconto", 80.0, 0, 80.0),
        Arguments.of("desconto total", 50.0, 100, 0.0)
    );
}
```

Também pode trabalhar com arrays e outros tipos iteráveis. O `Stream<Arguments>` é muito usado porque expressa claramente uma sequência de cenários.



## 5. É obrigatório ser `static`?

Por padrão, sim. Entretanto, é possível utilizar um método fornecedor não estático quando a classe de teste usa:

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DescontoTest {
```

Exemplo:

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DescontoTest {

    @ParameterizedTest
    @MethodSource("cenariosDeDesconto")
    void calcularDeveAtenderCenarios(
            String descricao,
            double preco,
            int percentual,
            double esperado) {

        double obtido = Desconto.calcular(preco, percentual);

        assertEquals(esperado, obtido, 0.001, descricao);
    }

    // Agora não precisa ser static.
    Stream<Arguments> cenariosDeDesconto() {
        return Stream.of(
            Arguments.of("sem desconto", 80.0, 0, 80.0)
        );
    }
}
```

Isso ocorre porque `PER_CLASS` faz o JUnit utilizar uma única instância da classe de teste.

Para exemplos simples, manter o fornecedor como `static` costuma ser mais direto.

## 6. Fonte externa à classe

O método fornecedor também pode ficar em outra classe. Nesse caso, usamos o nome totalmente qualificado:

```java
@MethodSource(
    "org.example.CenariosDeDesconto#cenariosValidos"
)
```

Classe fornecedora:

```java
package org.example;

public class CenariosDeDesconto {

    public static Stream<Arguments> cenariosValidos() {
        return Stream.of(
            Arguments.of("sem desconto", 80.0, 0, 80.0),
            Arguments.of("desconto parcial", 200.0, 25, 150.0)
        );
    }
}
```

Essa solução é útil quando vários testes precisam compartilhar os mesmos dados.

## 7. Tipagem e autoboxing

Dentro de:

```java
Arguments.of("sem desconto", 80.0, 0, 80.0)
```

os tipos primitivos são convertidos temporariamente em objetos:

| Valor primitivo | Objeto correspondente |
| --------------- | --------------------- |
| `double`        | `Double`              |
| `int`           | `Integer`             |

Esse processo é chamado de autoboxing.

Depois, ao entregar os dados para:

```java
double preco,
int percentual,
double esperado
```

o Java/JUnit converte os objetos novamente para tipos primitivos, em um processo chamado unboxing.

## 8. Valores monetários: `double` ou `BigDecimal`?

Para ensinar testes e tolerância numérica, o uso de `double` é adequado. Em sistemas financeiros reais, porém, `BigDecimal` costuma ser a escolha mais segura.

```java
BigDecimal preco = new BigDecimal("200.00");
```

Isso evita diversas imprecisões binárias associadas ao `double`.

Um teste com `BigDecimal` pode comparar diretamente os valores:

```java
assertEquals(
    new BigDecimal("150.00"),
    obtido
);
```

Há um detalhe: em `BigDecimal`, `equals()` também considera a escala:

```java
new BigDecimal("150.0")
new BigDecimal("150.00")
```

Esses valores têm o mesmo valor numérico, mas escalas diferentes. Quando a escala não for relevante, podemos usar:

```java
assertEquals(
    0,
    esperado.compareTo(obtido)
);
```

---

## Versão final organizada

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DescontoTest {

    // Executa o mesmo teste para cada cenário fornecido pelo método.
    // {0} apresenta a descrição como nome da execução.
    @ParameterizedTest(name = "{0}")

    // Indica o método responsável por fornecer os argumentos.
    @MethodSource("cenariosDeDesconto")
    void calcularDeveAtenderCenarios(
            String descricao,
            double preco,
            int percentual,
            double esperado) {

        // Act: executa o comportamento que está sendo testado.
        double obtido = Desconto.calcular(preco, percentual);

        // Assert: compara o resultado esperado com o resultado obtido.
        // O delta de 0.001 permite uma pequena diferença entre os doubles.
        // A descrição será apresentada como mensagem adicional se houver falha.
        assertEquals(esperado, obtido, 0.001, descricao);
    }

    // Arrange: fornece os conjuntos de dados do teste.
    //
    // É static porque, por padrão, o JUnit acessa a fonte
    // sem depender de uma instância da classe de teste.
    static Stream<Arguments> cenariosDeDesconto() {
        return Stream.of(
            // descrição, preço, percentual, resultado esperado
            Arguments.of("sem desconto",       80.0,   0,  80.0),
            Arguments.of("desconto parcial",  200.0,  25, 150.0),
            Arguments.of("desconto total",     50.0, 100,   0.0)
        );
    }
}
```

Em resumo, o fluxo é:

1. O JUnit encontra `@MethodSource("cenariosDeDesconto")`.
2. Executa o método fornecedor.
3. Obtém um `Stream` com três conjuntos de `Arguments`.
4. Distribui cada conjunto entre os parâmetros do teste.
5. Executa `Desconto.calcular()`.
6. Compara o resultado esperado com o obtido.
7. Exibe a descrição do cenário no relatório.

Desafio de verificação: como você adicionaria um cenário chamado `"metade do preço"`, com preço de `120.0`, desconto de `50%` e resultado esperado de `60.0`?


## 9. Nulos, vazios e espaços em branco

Para parâmetros que aceitam String, coleções ou arrays:

```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"   ", "\t"})
void nomeAusenteDeveSerRejeitado(String nome) {
    assertThrows(
            IllegalArgumentException.class,
            () -> Cadastro.validarNome(nome)
    );
}
```


##  O que esse teste verifica?

Este teste confere se o sistema rejeita nomes ausentes ou inválidos.

Antes de analisar o código, pense nestes quatro valores:

```text
null
""
"   "
"\t"
```

Todos são inválidos para um nome, mas não significam exatamente a mesma coisa:

| Valor   | Significado                         |
| ------- | ----------------------------------- |
| `null`  | Nenhum objeto foi fornecido         |
| `""`    | Texto existente, mas sem caracteres |
| `"   "` | Texto contendo apenas espaços       |
| `"\t"`  | Texto contendo uma tabulação        |

O teste será executado quatro vezes, uma para cada valor.

## Código completamente comentado

```java
// Indica que o método é um teste parametrizado.
//
// O mesmo método será executado várias vezes,
// recebendo um valor diferente no parâmetro "nome".
@ParameterizedTest

// Combina duas fontes de dados:
//
// @NullSource:
// fornece o valor null.
//
// @EmptySource:
// fornece uma String vazia: "".
//
// Portanto, esta anotação gera duas execuções:
// 1ª execução: nome = null
// 2ª execução: nome = ""
@NullAndEmptySource

// Acrescenta outros valores ao teste.
//
// "   " representa uma String contendo três espaços.
// "\t" representa uma String contendo uma tabulação.
//
// Esses valores não são tecnicamente vazios,
// porque possuem caracteres de espaço em branco.
@ValueSource(strings = {
        "   ",
        "\t"
})
void nomeAusenteDeveSerRejeitado(String nome) {

    // Verifica se Cadastro.validarNome() lança
    // uma IllegalArgumentException.
    //
    // IllegalArgumentException.class:
    // tipo da exceção que esperamos receber.
    //
    // () -> Cadastro.validarNome(nome):
    // expressão lambda que entrega ao assertThrows
    // o código que deverá ser executado e monitorado.
    assertThrows(
            IllegalArgumentException.class,
            () -> Cadastro.validarNome(nome)
    );
}
```

Imports necessários:

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
```

---

#  Conceitos aplicados

## 1. Teste parametrizado

```java
@ParameterizedTest
```

Essa anotação informa ao JUnit que o método deve ser executado várias vezes.

Um teste comum:

```java
@Test
void nomeAusenteDeveSerRejeitado() {
    // Executa uma vez.
}
```

Um teste parametrizado:

```java
@ParameterizedTest
void nomeAusenteDeveSerRejeitado(String nome) {
    // Executa uma vez para cada argumento fornecido.
}
```

Neste caso, os dados vêm de duas fontes:

```java
@NullAndEmptySource
@ValueSource(strings = {"   ", "\t"})
```

As fontes são combinadas, produzindo:

| Execução | Valor recebido por `nome` | Origem                |
| -------: | ------------------------- | --------------------- |
|        1 | `null`                    | `@NullAndEmptySource` |
|        2 | `""`                      | `@NullAndEmptySource` |
|        3 | `"   "`                   | `@ValueSource`        |
|        4 | `"\t"`                    | `@ValueSource`        |

## 2. Diferença entre `null` e vazio

### Valor `null`

```java
String nome = null;
```

Significa que a variável não aponta para nenhum objeto `String`.

Por isso, isto provoca `NullPointerException`:

```java
nome.isEmpty();
```

Antes de chamar métodos da `String`, devemos verificar:

```java
if (nome == null) {
    throw new IllegalArgumentException("O nome é obrigatório.");
}
```

### String vazia

```java
String nome = "";
```

Aqui existe um objeto `String`, mas ele possui zero caracteres:

```java
nome.length(); // 0
```

Podemos verificar com:

```java
nome.isEmpty(); // true
```

### String em branco

```java
String nome = "   ";
```

Essa String não está vazia:

```java
nome.isEmpty(); // false
```

Ela contém três caracteres de espaço. Porém, está em branco:

```java
nome.isBlank(); // true
```

O método `isBlank()` considera inválida uma String formada somente por caracteres de espaço em branco.

## 3. Por que usar `@ValueSource`?

`@EmptySource` fornece uma String realmente vazia:

```java
""
```

Ela não fornece automaticamente textos formados por espaços:

```java
"   "
```

Nem tabulações:

```java
"\t"
```

Por isso esses casos são acrescentados por meio de:

```java
@ValueSource(strings = {"   ", "\t"})
```

Isso testa uma regra mais completa:

> O nome não pode ser nulo, vazio nem formado somente por espaços em branco.

## 4. O que é `\t`?

```java
"\t"
```

É uma sequência de escape que representa uma tabulação.

Assim como:

| Sequência | Representação              |
| --------- | -------------------------- |
| `\t`      | Tabulação                  |
| `\n`      | Quebra de linha            |
| `\"`      | Aspas dentro de uma String |
| `\\`      | Barra invertida            |

Embora não seja visível como uma letra, a tabulação é um caractere. Portanto:

```java
"\t".isEmpty(); // false
"\t".isBlank(); // true
```

## 5. O que `assertThrows` verifica?

```java
assertThrows(
        IllegalArgumentException.class,
        () -> Cadastro.validarNome(nome)
);
```

O `assertThrows` verifica se o código lança a exceção esperada.

O resultado será:

| Comportamento de `validarNome()` | Resultado do teste |
| -------------------------------- | ------------------ |
| Lança `IllegalArgumentException` | Passa              |
| Não lança nenhuma exceção        | Falha              |
| Lança uma exceção incompatível   | Falha              |

## 6. Função da expressão lambda

```java
() -> Cadastro.validarNome(nome)
```

O lambda entrega uma ação para o JUnit executar posteriormente.

Sem o lambda, o método seria executado antes que o `assertThrows` pudesse monitorá-lo.

Podemos ler assim:

> “JUnit, execute `Cadastro.validarNome(nome)` e verifique se ocorre a exceção esperada.”

A versão extensa seria:

```java
assertThrows(
        IllegalArgumentException.class,
        () -> {
            Cadastro.validarNome(nome);
        }
);
```

---

# Aspectos importantes

## 1. `@NullAndEmptySource` é uma anotação composta

Esta anotação:

```java
@NullAndEmptySource
```

equivale à combinação de:

```java
@NullSource
@EmptySource
```

Portanto, estes dois códigos têm a mesma finalidade:

```java
@NullAndEmptySource
```

```java
@NullSource
@EmptySource
```

A forma combinada é mais curta e comunica diretamente a intenção.

## 2. Tipos aceitos pelas anotações

### `@NullSource`

Pode fornecer `null` para parâmetros que aceitam referência:

```java
String nome
List<String> nomes
Produto produto
int[] numeros
```

Não pode fornecer `null` para tipos primitivos:

```java
int idade
double preco
boolean ativo
```

Tipos primitivos não aceitam `null`.

Este teste seria inválido:

```java
@ParameterizedTest
@NullSource
void testar(int valor) {
}
```

Para aceitar `null`, seria necessário usar a classe correspondente:

```java
@ParameterizedTest
@NullSource
void testar(Integer valor) {
}
```

### `@EmptySource`

Pode criar valores vazios para tipos compatíveis, como:

```java
String
List
Set
Map
int[]
String[]
```

Exemplos conceituais:

```java
""                  // String vazia
List.of()           // Lista vazia
Set.of()            // Conjunto vazio
Map.of()            // Mapa vazio
new int[]{}         // Array vazio
new String[]{}      // Array vazio
```

## 3. Validação correta na classe de produção

Uma possível implementação de `Cadastro.validarNome()` seria:

```java
public class Cadastro {

    public static void validarNome(String nome) {

        // A ordem é importante.
        //
        // Primeiro verificamos null.
        // Somente depois chamamos isBlank(),
        // pois não podemos executar um método em null.
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome é obrigatório."
            );
        }
    }
}
```

O operador `||` utiliza curto-circuito.

Se:

```java
nome == null
```

for verdadeiro, o Java não executará:

```java
nome.isBlank()
```

Isso evita uma `NullPointerException`.

## 4. O teste verifica o tipo, mas não a mensagem

O teste original confirma apenas a classe da exceção:

```java
assertThrows(
        IllegalArgumentException.class,
        () -> Cadastro.validarNome(nome)
);
```

Para verificar também a mensagem, devemos capturar a exceção:

```java
@ParameterizedTest(name = "nome inválido: [{0}]")
@NullAndEmptySource
@ValueSource(strings = {"   ", "\t"})
void nomeAusenteDeveSerRejeitado(String nome) {

    // Act: executa a validação e captura a exceção.
    IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> Cadastro.validarNome(nome)
    );

    // Assert: verifica o motivo do erro.
    assertEquals(
            "O nome é obrigatório.",
            excecao.getMessage()
    );
}
```

Import adicional:

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
```

Essa versão documenta duas exigências:

1. Deve ocorrer uma `IllegalArgumentException`.
2. A mensagem deve ser `"O nome é obrigatório."`.

## 5. `assertThrows` aceita subclasses

Se esperarmos:

```java
RuntimeException.class
```

e o método lançar:

```java
IllegalArgumentException
```

o teste passa porque `IllegalArgumentException` é uma subclasse de `RuntimeException`.

Para exigir exatamente um tipo de exceção, o JUnit oferece:

```java
assertThrowsExactly(
        IllegalArgumentException.class,
        () -> Cadastro.validarNome(nome)
);
```

Em testes de regra de negócio, `assertThrows` normalmente é suficiente.

## Versão final recomendada e comentada

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CadastroTest {

    // Executa o teste uma vez para cada valor fornecido.
    //
    // {index}: número da execução.
    // {0}: valor recebido pelo parâmetro "nome".
    @ParameterizedTest(
            name = "caso {index}: nome inválido = [{0}]"
    )

    // Fornece dois valores:
    // 1. null
    // 2. String vazia: ""
    @NullAndEmptySource

    // Fornece valores que possuem caracteres,
    // mas são formados somente por espaços em branco.
    @ValueSource(strings = {
            "   ", // Três espaços
            "\t"   // Uma tabulação
    })
    void nomeAusenteDeveSerRejeitado(String nome) {

        // Act: executa a validação e captura a exceção.
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> Cadastro.validarNome(nome)
        );

        // Assert: verifica se a mensagem corresponde
        // à regra de negócio esperada.
        assertEquals(
                "O nome é obrigatório.",
                excecao.getMessage()
        );
    }
}
```

Em resumo, o teste garante que quatro formas diferentes de “nome ausente” sejam rejeitadas: `null`, vazio, espaços e tabulação.

Desafio: `"\n"` representa uma quebra de linha. Você acredita que `"\n".isEmpty()` e `"\n".isBlank()` retornarão o mesmo resultado?


Anotações úteis:

- `@NullSource`: uma execução com `null`;
- `@EmptySource`: uma execução com valor vazio;
- `@NullAndEmptySource`: combina as duas.

## 10. Casos de fronteira

Como já estudamos `@ParameterizedTest`, `@ValueSource`, lambda, `assertThrows` e verificação de mensagens, agora o conceito novo é a **análise de valores-limite**, também chamada de teste de fronteira.

## O que é uma fronteira?

Uma fronteira é o ponto em que uma entrada deixa de ser válida e passa a ser inválida — ou o contrário.

A regra informa:

```text
O percentual deve estar entre 0 e 100.
```

Isso normalmente significa que os extremos estão incluídos:

```text
0 ≤ percentual ≤ 100
```

Consequentemente:

| Percentual | Situação | Motivo                         |
| ---------: | -------- | ------------------------------ |
|       `-1` | Inválido | Imediatamente abaixo do mínimo |
|        `0` | Válido   | Limite mínimo                  |
|        `1` | Válido   | Imediatamente acima do mínimo  |
|       `99` | Válido   | Imediatamente abaixo do máximo |
|      `100` | Válido   | Limite máximo                  |
|      `101` | Inválido | Imediatamente acima do máximo  |

Analogia: imagine a entrada permitida entre os números 0 e 100 como um terreno cercado.

* `0` e `100` estão sobre a cerca, mas pertencem ao terreno;
* `1` e `99` estão logo dentro;
* `-1` e `101` estão logo fora.

Testar esses pontos ajuda a verificar se a “cerca” foi instalada no lugar correto.

---


Um conjunto forte de testes exercita os dois lados da transição.

```java
@ParameterizedTest
@ValueSource(ints = {-1, 101})
void percentualForaDoIntervaloDeveFalhar(int percentual) {
    IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> Desconto.calcular(100.0, percentual)
    );

    assertEquals(
            "O percentual deve estar entre 0 e 100.",
            excecao.getMessage()
    );
}
```


## Código comentado

```java
// Executa o mesmo teste para cada percentual
// fornecido pelo @ValueSource.
@ParameterizedTest

// Fornece os dois valores inteiros que estão
// imediatamente fora do intervalo válido:
//
// -1  → imediatamente abaixo do limite mínimo 0.
// 101 → imediatamente acima do limite máximo 100.
@ValueSource(ints = {
        -1,
        101
})
void percentualForaDoIntervaloDeveFalhar(int percentual) {

    // O preço 100.0 permanece fixo para que somente
    // o percentual seja alterado entre as execuções.
    //
    // Esperamos que percentuais fora da faixa provoquem
    // uma IllegalArgumentException.
    IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> Desconto.calcular(100.0, percentual)
    );

    // Verifica se a mensagem corresponde à regra de negócio.
    //
    // Isso confirma que a exceção foi causada pelo percentual
    // inválido, e não por outra validação do método.
    assertEquals(
            "O percentual deve estar entre 0 e 100.",
            excecao.getMessage()
    );
}
```

Esse teste será executado duas vezes:

```text
1ª execução: percentual = -1
2ª execução: percentual = 101
```

---

# Análise de valores-limite

## 1. Por que testar as fronteiras?

Muitos erros acontecem nos operadores utilizados nas condições.

A regra correta é:

```java
if (percentual < 0 || percentual > 100) {
    throw new IllegalArgumentException(
            "O percentual deve estar entre 0 e 100."
    );
}
```

Observe os operadores:

```java
percentual < 0
percentual > 100
```

Eles permitem que `0` e `100` sejam válidos.

Um erro comum seria:

```java
if (percentual <= 0 || percentual >= 100) {
    throw new IllegalArgumentException(
            "O percentual deve estar entre 0 e 100."
    );
}
```

Nesse código incorreto:

* `0` seria rejeitado;
* `100` também seria rejeitado.

Esse é um exemplo de erro de limite, frequentemente chamado de erro **off-by-one**: a implementação erra por uma unidade na transição entre valores válidos e inválidos.

## 2. Por que testar valores imediatamente próximos?

Poderíamos utilizar:

```java
@ValueSource(ints = {-500, 900})
```

Esses valores confirmariam que entradas muito distantes são inválidas. Porém, não verificariam com precisão onde acontece a transição.

Considere uma implementação incorreta:

```java
if (percentual < -10 || percentual > 110) {
    throw new IllegalArgumentException();
}
```

Os valores `-500` e `900` seriam rejeitados, e o teste passaria. Mas `-1` e `101`, que deveriam ser inválidos, seriam aceitos.

Por isso usamos valores próximos à fronteira:

```java
-1, 0, 1
```

e:

```java
99, 100, 101
```

## 3. O teste apresentado verifica somente o lado inválido

O código atual testa:

```java
-1
101
```

Ele comprova que valores logo fora do intervalo são rejeitados.

Entretanto, ainda precisamos comprovar que as fronteiras válidas são aceitas:

```java
0
100
```

E que os valores logo dentro também funcionam:

```java
1
99
```

Um conjunto mais completo teria dois testes:

```java
// Valores válidos.
@ValueSource(ints = {0, 1, 99, 100})
```

```java
// Valores inválidos.
@ValueSource(ints = {-1, 101})
```

## 4. Por que o preço permanece fixo?

Na chamada:

```java
Desconto.calcular(100.0, percentual)
```

o preço sempre será:

```java
100.0
```

Somente o percentual varia.

Essa decisão ajuda a isolar a regra testada:

```text
Variável analisada: percentual
Valor controlado: preço
```

Se o teste variasse preço e percentual ao mesmo tempo, uma falha poderia ter diferentes causas.

Manter os outros dados fixos é semelhante a um experimento científico: alteramos uma variável enquanto controlamos as demais.

---

# Testando o lado válido da fronteira

Para testar entradas válidas, não basta verificar que nenhuma exceção aconteceu. É melhor conferir o resultado produzido.

```java
// Define um nome claro para cada execução.
//
// {0} representa o percentual.
// {1} representa o resultado esperado.
@ParameterizedTest(
        name = "{0}% deve resultar em R$ {1}"
)

// Exercita os dois limites válidos e os valores
// imediatamente dentro do intervalo.
@CsvSource({
        // percentual, resultado esperado

        // Limite mínimo válido.
        "0,   100.0",

        // Imediatamente dentro da fronteira mínima.
        "1,    99.0",

        // Imediatamente dentro da fronteira máxima.
        "99,    1.0",

        // Limite máximo válido.
        "100,   0.0"
})
void percentualNaFronteiraDeveSerAceito(
        int percentual,
        double esperado) {

    // O preço permanece fixo em R$ 100,00.
    double obtido = Desconto.calcular(
            100.0,
            percentual
    );

    // Verifica se o percentual foi aplicado corretamente.
    assertEquals(esperado, obtido, 0.001);
}
```

Tabela dos cálculos:

| Percentual | Cálculo     | Resultado |
| ---------: | ----------- | --------: |
|       `0%` | `100 − 0`   |     `100` |
|       `1%` | `100 − 1`   |      `99` |
|      `99%` | `100 − 99`  |       `1` |
|     `100%` | `100 − 100` |       `0` |

---

# Partições de equivalência

Os testes de fronteira estão relacionados a outro conceito: **particionamento de equivalência**.

A entrada pode ser dividida em três grupos:

| Partição         | Intervalo              | Situação |
| ---------------- | ---------------------- | -------- |
| Abaixo do mínimo | `percentual < 0`       | Inválida |
| Dentro da faixa  | `0 ≤ percentual ≤ 100` | Válida   |
| Acima do máximo  | `percentual > 100`     | Inválida |

Dentro de cada grupo, esperamos um comportamento semelhante.

Por exemplo:

```text
-1, -10 e -500
```

pertencem à mesma partição inválida. Não é necessário testar todos os números negativos. Podemos escolher um valor representativo, priorizando o mais próximo da fronteira:

```java
-1
```

O mesmo vale para valores acima do limite:

```java
101
```

## Fronteira de três pontos

Para cada limite, podemos escolher três pontos:

```text
imediatamente fora | na fronteira | imediatamente dentro
```

Para o limite inferior:

```text
-1 | 0 | 1
```

Para o limite superior:

```text
99 | 100 | 101
```

Esse conjunto é forte porque examina os dois lados de cada transição.

## Domínio discreto

O tipo do percentual é:

```java
int percentual
```

Um `int` trabalha com valores inteiros. Portanto, existe um valor imediatamente anterior e outro imediatamente posterior:

```text
Anterior a 0    → -1
Posterior a 0   → 1
Anterior a 100  → 99
Posterior a 100 → 101
```

Esse tipo de domínio é chamado de **discreto**.

Se o percentual fosse `double`, não haveria um único valor imediatamente posterior a `0`, pois existiriam inúmeros valores possíveis:

```text
0.1
0.01
0.001
0.0001
```

Nesse caso, seria necessário definir a precisão aceita pela regra de negócio.

---

# Conjunto completo recomendado

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DescontoTest {

    // Verifica os valores imediatamente fora
    // dos limites inferior e superior.
    @ParameterizedTest(
            name = "{0}% deve ser rejeitado"
    )
    @ValueSource(ints = {-1, 101})
    void percentualForaDoIntervaloDeveFalhar(
            int percentual) {

        // Executa o cálculo e captura a exceção.
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> Desconto.calcular(100.0, percentual)
        );

        // Confirma que a exceção aconteceu
        // pela violação da regra do percentual.
        assertEquals(
                "O percentual deve estar entre 0 e 100.",
                excecao.getMessage()
        );
    }

    // Verifica as fronteiras válidas e os valores
    // imediatamente dentro do intervalo.
    @ParameterizedTest(
            name = "{0}% deve resultar em R$ {1}"
    )
    @CsvSource({
            // percentual, esperado
            "0,   100.0", // Limite inferior
            "1,    99.0", // Logo dentro do limite inferior
            "99,    1.0", // Logo dentro do limite superior
            "100,   0.0"  // Limite superior
    })
    void percentualDentroDoIntervaloDeveFuncionar(
            int percentual,
            double esperado) {

        // Executa o cálculo usando um preço controlado.
        double obtido = Desconto.calcular(
                100.0,
                percentual
        );

        // Confirma o resultado com tolerância para double.
        assertEquals(esperado, obtido, 0.001);
    }
}
```

Os conceitos novos deste tópico são:

| Conceito                 | Finalidade                                               |
| ------------------------ | -------------------------------------------------------- |
| Caso de fronteira        | Testar os extremos de uma regra                          |
| Limite inclusivo         | Os extremos `0` e `100` são aceitos                      |
| Vizinho interno          | Valor imediatamente dentro da faixa                      |
| Vizinho externo          | Valor imediatamente fora da faixa                        |
| Erro off-by-one          | Erro de uma unidade nos limites                          |
| Partição de equivalência | Agrupamento de entradas com comportamento semelhante     |
| Domínio discreto         | Conjunto com valores separados, como os números inteiros |
| Isolamento da variável   | Manter o preço fixo e variar somente o percentual        |

Desafio: se a regra aceitasse idades entre `18` e `65`, inclusive, quais seis valores formariam um teste completo de fronteiras?





## 11. `@Timeout`: limite de tempo para o teste

```java
@Test
@Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
void calcularDeveTerminarRapidamente() {
    Desconto.calcular(250.0, 15);
}
```

O teste falha se a chamada ultrapassar o limite. Entretanto:

- `@Timeout` não substitui benchmark;
- limites muito curtos podem produzir falhas instáveis;
- use-o para proteger contra travamentos ou lentidão evidente.

---


O novo conceito deste tópico é a criação de um **limite máximo de tempo** para a execução de um teste.

A ideia é responder:

> Além de produzir o resultado correto, o método termina dentro de um tempo aceitável?

## Código completamente comentado

```java
// Indica que este é um teste comum do JUnit.
// Diferentemente de @ParameterizedTest,
// este método será executado somente uma vez.
@Test

// Define o tempo máximo permitido para a execução.
//
// value = 100:
// o valor numérico do limite.
//
// unit = TimeUnit.MILLISECONDS:
// informa que o valor 100 está em milissegundos.
//
// Portanto, o teste deve terminar em até 100 milissegundos.
@Timeout(
        value = 100,
        unit = TimeUnit.MILLISECONDS
)
void calcularDeveTerminarRapidamente() {

    // Executa o método que será monitorado.
    //
    // Se a execução terminar dentro de 100 milissegundos,
// o teste passa em relação ao tempo.
//
// Se ultrapassar 100 milissegundos,
// o JUnit marca o teste como falho.
    Desconto.calcular(250.0, 15);
}
```

Imports necessários:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
```

---

# O que é timeout?

`Timeout` significa “tempo limite”.

Imagine uma prova em que o aluno possui 60 minutos para terminar:

* se entregar antes de 60 minutos, está dentro do prazo;
* se ultrapassar 60 minutos, o limite foi excedido.

No teste:

```java
@Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
```

o limite é de 100 milissegundos:

```text
1 segundo = 1.000 milissegundos
100 milissegundos = 0,1 segundo
```

O comportamento esperado é:

| Tempo de execução | Resultado |
| ----------------: | --------- |
|              5 ms | Passa     |
|             30 ms | Passa     |
|             99 ms | Passa     |
|    Mais de 100 ms | Falha     |

O teste não exige que o método leve exatamente 100 milissegundos. Esse é apenas o tempo máximo permitido.

---

# Elementos da anotação

## 1. O atributo `value`

```java
value = 100
```

Indica a quantidade de tempo permitida.

O tipo desse atributo é `long`, e o valor precisa ser positivo.

Somente o número não é suficiente: precisamos saber se ele representa segundos, minutos ou milissegundos.

## 2. O atributo `unit`

```java
unit = TimeUnit.MILLISECONDS
```

Indica a unidade usada para interpretar o número `100`.

Sem essa informação, não saberíamos se o limite seria:

```text
100 segundos
100 milissegundos
100 minutos
```

## 3. O que é `TimeUnit`?

`TimeUnit` é um `enum` da biblioteca padrão do Java:

```java
java.util.concurrent.TimeUnit
```

Um `enum` representa um conjunto fechado de opções.

Algumas unidades disponíveis são:

| Constante      | Unidade        |
| -------------- | -------------- |
| `NANOSECONDS`  | Nanossegundos  |
| `MICROSECONDS` | Microssegundos |
| `MILLISECONDS` | Milissegundos  |
| `SECONDS`      | Segundos       |
| `MINUTES`      | Minutos        |
| `HOURS`        | Horas          |
| `DAYS`         | Dias           |

Exemplos:

```java
@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
```

Limite de 500 milissegundos.

```java
@Timeout(value = 2, unit = TimeUnit.SECONDS)
```

Limite de 2 segundos.

```java
@Timeout(value = 1, unit = TimeUnit.MINUTES)
```

Limite de 1 minuto.

## 4. Unidade padrão

Se omitirmos `unit`, o JUnit utiliza segundos:

```java
@Timeout(2)
```

É equivalente a:

```java
@Timeout(
        value = 2,
        unit = TimeUnit.SECONDS
)
```

Para fins didáticos, declarar a unidade explicitamente torna a intenção mais clara. A unidade padrão e os valores aceitos estão documentados na [API oficial do JUnit](https://docs.junit.org/5.14.4/api/org.junit.jupiter.api/org/junit/jupiter/api/Timeout.html).

---

# O que exatamente o teste verifica?

Este teste verifica somente uma característica:

```text
O método termina dentro de 100 milissegundos?
```

Ele não verifica se o cálculo está correto.

Por exemplo, o método poderia retornar um resultado completamente errado em 1 milissegundo e o teste ainda passaria:

```java
public static double calcular(
        double preco,
        int percentual) {

    // Resultado errado, mas execução rápida.
    return -999;
}
```

O teste de timeout passaria porque não existe uma asserção verificando o resultado.

Uma versão mais completa pode verificar tempo e resultado:

```java
@Test
@Timeout(
        value = 100,
        unit = TimeUnit.MILLISECONDS
)
void calcularDeveTerminarRapidamente() {

    // Act: executa o cálculo.
    double obtido = Desconto.calcular(250.0, 15);

    // Assert: verifica também o resultado funcional.
    assertEquals(212.50, obtido, 0.001);
}
```

Nesse caso, o teste verifica:

1. O método termina em até 100 milissegundos.
2. O resultado calculado é `212.50`.

Entretanto, separar responsabilidades também pode ser uma boa escolha:

```java
@Test
void calcularDeveProduzirResultadoCorreto() {
    double obtido = Desconto.calcular(250.0, 15);
    assertEquals(212.50, obtido, 0.001);
}

@Test
@Timeout(
        value = 100,
        unit = TimeUnit.MILLISECONDS
)
void calcularDeveTerminarRapidamente() {
    Desconto.calcular(250.0, 15);
}
```

Assim, quando houver falha, o nome do teste indica se o problema é de resultado ou de tempo.

---

# Contra quais problemas o timeout protege?

## 1. Laço infinito

Considere um erro na implementação:

```java
public static double calcular(
        double preco,
        int percentual) {

    // Erro: a condição nunca se torna falsa.
    while (percentual >= 0) {
        System.out.println("Calculando...");
    }

    return preco;
}
```

Sem limite de tempo, o teste poderia ficar executando indefinidamente.

Com:

```java
@Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
```

o JUnit identifica que o método ultrapassou o tempo permitido e marca o teste como falho.

## 2. Algoritmo excessivamente lento

Um método pode terminar, mas realizar trabalho desnecessário:

```java
public static double calcular(
        double preco,
        int percentual) {

    // Simulação de processamento desnecessário.
    for (long i = 0; i < 10_000_000_000L; i++) {
        // Operação sem utilidade.
    }

    return preco - preco * percentual / 100.0;
}
```

O resultado pode estar correto, mas o método é excessivamente lento. O timeout ajuda a detectar essa lentidão evidente.

## 3. Espera por recurso externo

Um teste pode ficar esperando:

* resposta de rede;
* consulta ao banco de dados;
* liberação de um arquivo;
* desbloqueio de uma thread;
* resposta de um serviço externo.

O timeout impede que a suíte inteira permaneça bloqueada indefinidamente.

---

# Por que `@Timeout` não é benchmark?

Um benchmark mede desempenho com maior rigor.

Ele costuma considerar:

* várias execuções;
* aquecimento da JVM;
* compilação JIT;
* média, mediana e variação;
* consumo de memória;
* condições controladas;
* interferência do sistema operacional.

O `@Timeout` realiza uma pergunta mais simples:

```text
A execução ultrapassou o limite definido?
```

Ele não responde com precisão:

```text
Qual implementação é mais rápida?
Quanto o desempenho melhorou?
Qual é o tempo médio?
Quantas operações são realizadas por segundo?
```

Para avaliações rigorosas de desempenho em Java, é comum utilizar o **JMH — Java Microbenchmark Harness**. O `@Timeout` é mais apropriado como proteção contra travamentos e regressões de desempenho muito evidentes.

## Comparação

| Característica               | `@Timeout`          | Benchmark                  |
| ---------------------------- | ------------------- | -------------------------- |
| Detecta travamento           | Sim                 | Pode detectar              |
| Define tempo máximo          | Sim                 | Não é o objetivo principal |
| Mede tempo com precisão      | Não                 | Sim                        |
| Executa aquecimento da JVM   | Não necessariamente | Sim                        |
| Compara desempenho           | Não adequadamente   | Sim                        |
| Adequado para teste unitário | Sim                 | Normalmente separado       |

---

# Por que limites muito curtos são perigosos?

Considere:

```java
@Timeout(
        value = 1,
        unit = TimeUnit.MILLISECONDS
)
```

Mesmo que o método normalmente execute rapidamente, o teste pode falhar devido a fatores externos:

* computador ocupado;
* antivírus;
* depurador ativo;
* inicialização da JVM;
* coleta de lixo;
* servidor de integração contínua sobrecarregado;
* escalonamento das threads pelo sistema operacional.

Esse tipo de teste é chamado de **instável** ou **flaky** quando passa algumas vezes e falha em outras sem alteração no código.

Analogia: se uma pessoa costuma percorrer um trajeto em dez minutos, estabelecer um limite de dez minutos e um segundo é frágil. Um semáforo diferente já provocaria atraso. Um limite razoável deve possuir margem de segurança.

## Exemplo frágil

```java
@Timeout(
        value = 1,
        unit = TimeUnit.MILLISECONDS
)
```

## Exemplo com margem

```java
@Timeout(
        value = 500,
        unit = TimeUnit.MILLISECONDS
)
```

Isso não significa que `500 ms` seja sempre o valor correto. O limite deve considerar:

* comportamento esperado;
* ambiente de execução;
* custo real da operação;
* risco de travamento;
* necessidade do sistema.

---

# Aplicação no método e na classe

## Timeout em um método

```java
@Test
@Timeout(2)
void operacaoDeveTerminar() {
    executarOperacao();
}
```

O limite vale apenas para esse teste.

## Timeout em uma classe

A anotação também pode ser colocada na classe:

```java
// Aplica o limite aos métodos de teste da classe.
@Timeout(
        value = 2,
        unit = TimeUnit.SECONDS
)
class DescontoTest {

    @Test
    void deveCalcularDezPorCento() {
        Desconto.calcular(100.0, 10);
    }

    @Test
    void deveCalcularVintePorCento() {
        Desconto.calcular(100.0, 20);
    }
}
```

Nesse caso, os métodos de teste da classe recebem o limite de dois segundos. A documentação oficial também permite o uso em métodos de ciclo de vida como `@BeforeEach`, `@AfterEach`, `@BeforeAll` e `@AfterAll`. [JUnit `@Timeout`](https://docs.junit.org/5.14.4/api/org.junit.jupiter.api/org/junit/jupiter/api/Timeout.html)

---

# Thread de execução

Uma **thread** é uma linha de execução dentro do programa.

O JUnit permite controlar se o código monitorado deve executar:

* na mesma thread do teste;
* em uma thread separada.

Exemplo explícito na mesma thread:

```java
@Test
@Timeout(
        value = 100,
        unit = TimeUnit.MILLISECONDS,
        threadMode = Timeout.ThreadMode.SAME_THREAD
)
void deveTerminarNaMesmaThread() {
    Desconto.calcular(250.0, 15);
}
```

Exemplo em uma thread separada:

```java
@Test
@Timeout(
        value = 100,
        unit = TimeUnit.MILLISECONDS,
        threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
void deveTerminarEmThreadSeparada() {
    Desconto.calcular(250.0, 15);
}
```

O modo padrão declarado pela anotação é `INFERRED`: o JUnit consulta a configuração do projeto e, quando ela não existe, utiliza a mesma thread. A [documentação oficial](https://docs.junit.org/5.14.4/api/org.junit.jupiter.api/org/junit/jupiter/api/Timeout.html) descreve `SAME_THREAD` e `SEPARATE_THREAD`.

Para este exemplo didático, não é necessário configurar `threadMode`. A forma simples é suficiente:

```java
@Timeout(
        value = 100,
        unit = TimeUnit.MILLISECONDS
)
```

---

# Versão final recomendada e comentada

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DescontoTest {

    // Indica que o método é um teste do JUnit.
    @Test

    // Estabelece o limite máximo de execução:
    //
    // value = 100:
    // quantidade de tempo permitida.
    //
    // unit = MILLISECONDS:
    // o valor está expresso em milissegundos.
    //
    // 100 ms equivalem a 0,1 segundo.
    @Timeout(
            value = 100,
            unit = TimeUnit.MILLISECONDS
    )
    void calcularDeveTerminarRapidamente() {

        // Act: executa o método monitorado.
        double obtido = Desconto.calcular(250.0, 15);

        // Assert: além do tempo, verifica se
        // o cálculo produziu o resultado correto.
        assertEquals(212.50, obtido, 0.001);
    }
}
```

Os conceitos novos deste tópico são:

| Conceito          | Finalidade                                    |
| ----------------- | --------------------------------------------- |
| `@Timeout`        | Determinar o tempo máximo do teste            |
| `value`           | Informar a quantidade de tempo                |
| `TimeUnit`        | Informar a unidade de tempo                   |
| `enum`            | Representar um conjunto fechado de opções     |
| Timeout na classe | Aplicar limite a vários testes                |
| Teste instável    | Teste que falha por variações do ambiente     |
| Benchmark         | Medição rigorosa de desempenho                |
| Thread            | Linha de execução do programa                 |
| `threadMode`      | Definir em qual thread o teste será executado |

Desafio: qual é mais adequado para proteger contra um possível laço infinito: `@Timeout` ou um benchmark?




## 12. Erros frequentes

| Erro | Causa provável | Correção |
|---|---|---|
| `No ParameterResolver` | O método possui parâmetro, mas não recebeu fonte adequada | Use `@ParameterizedTest` e uma fonte |
| `ArgumentConversionException` | O texto não pode ser convertido para o tipo do parâmetro | Corrija o valor ou o tipo Java |
| `PreconditionViolationException` | O método indicado em `@MethodSource` não existe ou não fornece dados | Confira nome, retorno e `static` |
| `Expected ... Actual ...` | A execução ocorreu, mas o resultado divergiu | Leia os argumentos do caso e revise regra ou expectativa |
| `params cannot be resolved` | Dependência ou import ausente | Confira `junit-jupiter` e os imports |

## 13. Perguntas de fixação

1. Qual fonte usar quando apenas uma String varia?
2. Qual fonte é adequada para preço, percentual e resultado esperado?
3. Quatro linhas em `@CsvSource` produzem quantas execuções?
4. Por que comparar `double` com delta?
5. Quais valores devem ser testados ao redor da faixa 0–100?

### Respostas

1. `@ValueSource(strings = {...})`.
2. `@CsvSource`.
3. Quatro execuções.
4. Porque números de ponto flutuante podem apresentar pequenas diferenças de representação.
5. Pelo menos `0`, `100`, `-1` e `101`; `1` e `99` também fortalecem a cobertura.

---

## 14. Prática guiada

Implemente a suíte da classe `Desconto`:

1. crie a classe de produção;
2. prepare os imports;
3. teste resultados com `@CsvSource`;
4. teste preços negativos com `@ValueSource`;
5. teste percentuais imediatamente fora das fronteiras;
6. adicione um teste de timeout;
7. execute, provoque uma falha e interprete o relatório.

## 15. Desafio autônomo — CalculadoraFrete

Regra:

- frete comum = `8 + pesoKg * 2`;
- entrega expressa = frete comum acrescido de 50%;
- peso deve ser maior que zero;
- mensagem inválida: `O peso deve ser maior que zero.`

### Classe de produção — gabarito

```java
package org.example;

public final class CalculadoraFrete {

    private CalculadoraFrete() {
    }

    public static double calcular(double pesoKg, boolean entregaExpressa) {
        if (pesoKg <= 0) {
            throw new IllegalArgumentException(
                    "O peso deve ser maior que zero."
            );
        }

        double freteComum = 8.0 + pesoKg * 2.0;

        if (entregaExpressa) {
            return freteComum * 1.5;
        }

        return freteComum;
    }
}
```

### Testes — gabarito

```java
package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraFreteTest {

    @ParameterizedTest(
            name = "peso={0}, expressa={1}, esperado={2}"
    )
    @CsvSource({
        "0.01, false,  8.02",
        "1.00, false, 10.00",
        "5.00, false, 18.00",
        "1.00, true,  15.00",
        "5.00, true,  27.00"
    })
    void calcularDeveRetornarFreteCorreto(
            double peso,
            boolean expressa,
            double esperado) {

        double obtido = CalculadoraFrete.calcular(peso, expressa);

        assertEquals(esperado, obtido, 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.01, -10.0})
    void pesoInvalidoDeveLancarExcecao(double peso) {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> CalculadoraFrete.calcular(peso, false)
        );

        assertEquals(
                "O peso deve ser maior que zero.",
                excecao.getMessage()
        );
    }
}
```

## 16. Critérios de avaliação

- fonte de dados adequada ao cenário;
- correspondência correta entre dados e parâmetros;
- cobertura de casos válidos, inválidos e fronteiras;
- asserções corretas, incluindo delta e mensagem;
- nomes descritivos e comentários que explicam decisões;
- execução integral da suíte sem falhas inesperadas.

---

**Próxima aula:** organização e leitura de resultados de teste, mantendo o foco na qualidade das evidências produzidas pela suíte.
