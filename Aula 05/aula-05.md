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

Use quando apenas um valor varia entre as execuções.

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

Cada valor chega separadamente ao parâmetro `preco`.

Tipos suportados incluem `strings`, `ints`, `longs`, `doubles`, `floats`, `shorts`, `bytes`, `chars`, `booleans` e `classes`.

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

Mapeamento da primeira linha:

| Coluna | Texto na fonte | Parâmetro Java |
|---|---:|---|
| 1 | `100.00` | `double preco` |
| 2 | `10` | `int percentual` |
| 3 | `90.00` | `double esperado` |

### Textos que contêm vírgulas

Use outro delimitador:

```java
@ParameterizedTest
@CsvSource(
    value = {
        "Blumenau, SC;47",
        "Florianópolis, SC;48"
    },
    delimiter = ';'
)
void cidadeDevePossuirDdd(String cidade, int ddd) {
    // teste ilustrativo
}
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

O método fornecedor é `static` por padrão e retorna um fluxo de argumentos.

## 9. Nulos e vazios

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

Anotações úteis:

- `@NullSource`: uma execução com `null`;
- `@EmptySource`: uma execução com valor vazio;
- `@NullAndEmptySource`: combina as duas.

## 10. Casos de fronteira

Para uma regra que aceita percentuais entre 0 e 100:

- `0` e `100` são fronteiras válidas;
- `-1` e `101` estão imediatamente fora da faixa;
- `1` e `99` estão imediatamente dentro da faixa.

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

## 11. `@Timeout`

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
