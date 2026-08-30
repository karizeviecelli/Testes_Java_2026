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
- relacionar os dados da fonte aos parâmetros do método de teste;
- criar nomes legíveis para cada execução;
- testar casos comuns, valores inválidos e fronteiras;
- usar fontes para valores nulos, vazios e em branco;
- aplicar `@Timeout` com cautela;
- interpretar qual conjunto de dados provocou uma falha.

## 2. Organização sugerida das 4 horas

| Etapa | Tempo | Estratégia |
|---|---:|---|
| Retomada | 15 min | Revisão de AAA, `assertEquals` e `assertThrows` |
| Exposição dialogada | 55 min | Comparação de códigos e escolha das fontes |
| Demonstração ao vivo | 35 min | Construção da suíte `DescontoTest` |
| Intervalo | 10 min | — |
| Prática guiada | 70 min | Implementação em sete etapas |
| Desafio autônomo | 30 min | Testes da `CalculadoraFrete` |
| Socialização e feedback | 25 min | Leitura de falhas e avaliação formativa |

---

## 3. Fundamentos comuns

Os conceitos desta seção serão utilizados em todos os exemplos posteriores. Eles são explicados aqui uma única vez.

### 3.1. O que é um teste parametrizado?

Um teste parametrizado executa a mesma regra várias vezes, alterando apenas os dados.

Ele separa:

1. **a regra de verificação**, escrita no método de teste;
2. **os dados dos cenários**, fornecidos por uma fonte.

Antes da parametrização:

```java
@Test
void descontoDe10PorCentoEm100() {
    assertEquals(90.0, Desconto.calcular(100.0, 10), 0.001);
}

@Test
void descontoDe20PorCentoEm200() {
    assertEquals(160.0, Desconto.calcular(200.0, 20), 0.001);
}
```

Depois da parametrização:

```java
@ParameterizedTest
@CsvSource({
    "100.0, 10,  90.0",
    "200.0, 20, 160.0"
})
void calcularDeveAplicarPercentual(
        double preco,
        int percentual,
        double esperado) {

    double obtido = Desconto.calcular(preco, percentual);

    assertEquals(esperado, obtido, 0.001);
}
```

As duas linhas do `@CsvSource` produzem duas execuções independentes.

### 3.2. Padrão AAA

O padrão **Arrange–Act–Assert** organiza o teste em três momentos:

| Etapa | Função | Exemplo |
|---|---|---|
| Arrange | Preparar os dados | Valores fornecidos pela fonte |
| Act | Executar o comportamento | `Desconto.calcular(...)` |
| Assert | Verificar o resultado | `assertEquals(...)` |

Nos testes parametrizados, o Arrange frequentemente fica na fonte de argumentos.

### 3.3. Nome de cada execução

O atributo `name` melhora a leitura do relatório:

```java
@ParameterizedTest(
        name = "caso {index}: entrada={0}, esperado={1}"
)
```

| Marcador | Significado |
|---|---|
| `{index}` | Número da execução |
| `{0}` | Primeiro argumento |
| `{1}` | Segundo argumento |
| `{2}` | Terceiro argumento |

Os índices dos argumentos começam em zero.

### 3.4. Exceções, lambda e mensagem

```java
IllegalArgumentException excecao = assertThrows(
        IllegalArgumentException.class,
        () -> Desconto.calcular(preco, percentual)
);

assertEquals(
        "O preço não pode ser negativo.",
        excecao.getMessage()
);
```

Nesse código:

- `assertThrows` executa uma ação e verifica o tipo da exceção;
- `() -> ...` é uma expressão lambda que entrega a ação ao JUnit;
- a exceção capturada permite verificar também a mensagem;
- conferir a mensagem ajuda a confirmar qual regra de negócio falhou.

A chamada precisa estar na lambda porque o JUnit deve controlar o momento da execução:

```java
// Correto: entrega uma ação ao JUnit.
() -> Desconto.calcular(preco, percentual)
```

```java
// Incorreto: tentaria executar o método antes do assertThrows.
Desconto.calcular(preco, percentual)
```

### 3.5. Comparação de `double` com delta

Números `double` podem apresentar pequenas diferenças de representação binária. Por isso, usamos uma tolerância:

```java
assertEquals(esperado, obtido, 0.001);
```

O JUnit verifica:

```text
|esperado − obtido| ≤ 0.001
```

O delta não representa a quantidade de casas decimais; representa a diferença máxima aceita.

> Em sistemas financeiros reais, `BigDecimal` costuma ser mais apropriado. Nesta aula, `double` é usado para praticar testes com tolerância.

---

## 4. Classe de produção usada na aula

Crie `src/main/java/org/example/Desconto.java`:

```java
package org.example;

/**
 * Classe utilitária responsável pelo cálculo de descontos.
 */
public final class Desconto {

    // Impede a criação de objetos de uma classe que só possui método estático.
    private Desconto() {
    }

    /**
     * Calcula o preço após aplicar um percentual de desconto.
     *
     * @param preco preço original, maior ou igual a zero
     * @param percentual percentual entre 0 e 100, inclusive
     * @return preço final depois do desconto
     */
    public static double calcular(double preco, int percentual) {
        // Valida o preço antes de fazer o cálculo.
        if (preco < 0) {
            throw new IllegalArgumentException(
                    "O preço não pode ser negativo."
            );
        }

        // Os limites 0 e 100 são válidos.
        if (percentual < 0 || percentual > 100) {
            throw new IllegalArgumentException(
                    "O percentual deve estar entre 0 e 100."
            );
        }

        // O uso de 100.0 mantém o cálculo em ponto flutuante.
        double valorDoDesconto = preco * percentual / 100.0;

        return preco - valorDoDesconto;
    }
}
```

## 5. Preparação do projeto

Em projetos Maven:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.4</version>
    <scope>test</scope>
</dependency>
```

Imports utilizados na aula:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
```

> Todos os imports desta aula começam com `org.junit.jupiter`. O import `org.junit.Test` pertence ao JUnit 4.

---

## 6. `@ValueSource`: um argumento simples

Use `@ValueSource` quando apenas um argumento variar entre as execuções.

```java
// {0} será substituído pelo preço da execução atual.
@ParameterizedTest(name = "preço inválido: {0}")

// Fornece três valores double ao parâmetro preco.
@ValueSource(doubles = {-0.01, -1.0, -100.0})
void precoNegativoDeveLancarExcecao(double preco) {
    // O percentual permanece fixo para isolar a validação do preço.
    int percentual = 10;

    // Executa o cálculo e captura a exceção esperada.
    IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> Desconto.calcular(preco, percentual)
    );

    // Confirma a regra de negócio que causou a exceção.
    assertEquals(
            "O preço não pode ser negativo.",
            excecao.getMessage()
    );
}
```

O teste terá três execuções:

```text
preço inválido: -0.01
preço inválido: -1.0
preço inválido: -100.0
```

Tipos disponíveis incluem `strings`, `ints`, `longs`, `doubles`, `floats`, `shorts`, `bytes`, `chars`, `booleans` e `classes`.

**Verificação:** por que `0.0` não aparece entre os preços inválidos? Consulte a regra da classe `Desconto`.

---

## 7. `@CsvSource`: vários argumentos simples

Use `@CsvSource` quando cada cenário precisar fornecer mais de um valor simples.

Pense na anotação como uma pequena tabela:

| Preço | Percentual | Esperado |
|---:|---:|---:|
| 100,00 | 10% | 90,00 |
| 200,00 | 25% | 150,00 |
| 80,00 | 0% | 80,00 |
| 50,00 | 100% | 0,00 |

```java
@ParameterizedTest(
        name = "caso {index}: R$ {0} - {1}% = R$ {2}"
)

// Cada String representa uma execução.
// A ordem das colunas deve acompanhar a ordem dos parâmetros.
@CsvSource({
        // preço, percentual, esperado
        "100.00,  10,  90.00",
        "200.00,  25, 150.00",
        " 80.00,   0,  80.00",
        " 50.00, 100,   0.00"
})
void calcularDeveAplicarPercentual(
        double preco,       // Primeira coluna
        int percentual,     // Segunda coluna
        double esperado) {  // Terceira coluna

    double obtido = Desconto.calcular(preco, percentual);

    assertEquals(esperado, obtido, 0.001);
}
```

O JUnit converte os textos da linha para os tipos dos parâmetros:

```text
"100.00, 10, 90.00"
     ↓     ↓     ↓
  double  int  double
```

### Quando o `@CsvSource` fica grande?

O JUnit não estabelece uma pequena quantidade máxima de linhas. Entretanto, muitas linhas prejudicam a leitura da classe.

| Situação | Fonte sugerida |
|---|---|
| Poucos dados simples | `@ValueSource` |
| Poucos cenários com várias colunas | `@CsvSource` |
| Muitos cenários tabulares | `@CsvFileSource` |
| Objetos ou dados construídos | `@MethodSource` |

Por padrão, cada coluna do `@CsvSource` aceita até 4.096 caracteres. Esse limite pode ser configurado com `maxCharsPerColumn`.

**Verificação:** na linha `"300.00, 50, 150.00"`, quais valores chegam a `preco`, `percentual` e `esperado`?

---

## 8. `@MethodSource`: objetos e dados construídos

Use `@MethodSource` quando:

- os cenários incluem objetos;
- os dados precisam ser construídos com Java;
- o `@CsvSource` ficaria difícil de ler;
- a fonte será reutilizada.

```java
// O primeiro argumento, descricao, será o nome da execução.
@ParameterizedTest(name = "{0}")

// Indica o método que fornecerá os argumentos.
@MethodSource("cenariosDeDesconto")
void calcularDeveAtenderCenarios(
        String descricao,
        double preco,
        int percentual,
        double esperado) {

    double obtido = Desconto.calcular(preco, percentual);

    // A descrição também será exibida se a asserção falhar.
    assertEquals(esperado, obtido, 0.001, descricao);
}

// Método fornecedor dos cenários.
// static: pertence à classe e pode ser acessado sem criar um objeto.
// Stream<Arguments>: sequência de conjuntos de argumentos.
// (): o método não recebe parâmetros.
static Stream<Arguments> cenariosDeDesconto() {
    return Stream.of(
            // descrição, preço, percentual, esperado
            Arguments.of("sem desconto",      80.0,   0,  80.0),
            Arguments.of("desconto parcial", 200.0,  25, 150.0),
            Arguments.of("desconto total",    50.0, 100,   0.0)
    );
}
```

### 8.1. Relação entre a fonte e o teste

```text
Arguments.of("desconto parcial", 200.0, 25, 150.0)
                        ↓           ↓    ↓    ↓
                   descricao     preco  %  esperado
```

A ordem e os tipos devem ser compatíveis com os parâmetros do teste.

### 8.2. Significado de `static Stream<Arguments>`

| Parte | Significado |
|---|---|
| `static` | O método pertence à classe |
| `Stream` | Fornece uma sequência de elementos |
| `<Arguments>` | Cada elemento contém os dados de uma execução |
| `cenariosDeDesconto` | Nome usado pelo `@MethodSource` |
| `()` | O método não recebe parâmetros |

Por padrão, o método fornecedor local é `static`. Assim, o JUnit consegue acessá-lo sem depender de uma instância da classe de teste.

### 8.3. Uso com objetos

```java
static Stream<Arguments> cenariosComProdutos() {
    return Stream.of(
            Arguments.of(
                    "Notebook com 10%",
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

O teste pode receber o objeto diretamente:

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

> Evite calcular o resultado esperado com a mesma fórmula do código de produção. O teste poderia repetir exatamente o mesmo erro da implementação.

**Verificação:** como adicionar o cenário `"metade do preço"`, com preço `120.0`, desconto `50%` e resultado `60.0`?

---

## 9. Nulos, vazios e espaços em branco

As entradas abaixo podem parecer iguais para o usuário, mas são diferentes para o Java:

| Valor | Significado | `isEmpty()` | `isBlank()` |
|---|---|---:|---:|
| `null` | Ausência de objeto | Não pode ser chamado | Não pode ser chamado |
| `""` | String com zero caracteres | `true` | `true` |
| `"   "` | String com espaços | `false` | `true` |
| `"\t"` | String com tabulação | `false` | `true` |
| `"Ana"` | String preenchida | `false` | `false` |

```java
@ParameterizedTest(name = "nome inválido: [{0}]")

// Produz duas execuções: null e "".
@NullAndEmptySource

// Acrescenta espaços e tabulação.
// Esses valores possuem caracteres, mas estão em branco.
@ValueSource(strings = {"   ", "\t"})
void nomeAusenteDeveSerRejeitado(String nome) {
    assertThrows(
            IllegalArgumentException.class,
            () -> Cadastro.validarNome(nome)
    );
}
```

Anotações relacionadas:

| Anotação | Valor fornecido |
|---|---|
| `@NullSource` | Uma execução com `null` |
| `@EmptySource` | Uma execução com valor vazio |
| `@NullAndEmptySource` | Combina as duas anteriores |

`@NullSource` não pode ser usado com tipos primitivos como `int` e `double`, pois eles não aceitam `null`. Use classes como `Integer` e `Double` quando a ausência de valor fizer parte do cenário.

Uma validação segura seria:

```java
public static void validarNome(String nome) {
    // O operador || usa curto-circuito.
    // Se nome for null, nome.isBlank() não será executado.
    if (nome == null || nome.isBlank()) {
        throw new IllegalArgumentException(
                "O nome é obrigatório."
        );
    }
}
```

**Verificação:** por que `"   ".isEmpty()` retorna `false`, mas `"   ".isBlank()` retorna `true`?

---

## 10. Casos de fronteira

Uma fronteira é o ponto em que uma entrada muda de válida para inválida.

Para a regra:

```text
0 ≤ percentual ≤ 100
```

temos:

| Valor | Situação |
|---:|---|
| `-1` | Imediatamente fora do limite mínimo |
| `0` | Limite mínimo válido |
| `1` | Imediatamente dentro do limite mínimo |
| `99` | Imediatamente dentro do limite máximo |
| `100` | Limite máximo válido |
| `101` | Imediatamente fora do limite máximo |

### 10.1. Valores externos

```java
@ParameterizedTest(name = "{0}% deve ser rejeitado")

// Exercita os valores imediatamente fora da faixa.
@ValueSource(ints = {-1, 101})
void percentualForaDoIntervaloDeveFalhar(int percentual) {
    // O preço fica fixo para isolar a regra do percentual.
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

### 10.2. Fronteiras válidas e vizinhos internos

```java
@ParameterizedTest(name = "{0}% deve resultar em R$ {1}")
@CsvSource({
        // percentual, esperado
        "0,   100.0",
        "1,    99.0",
        "99,    1.0",
        "100,   0.0"
})
void percentualDentroDoIntervaloDeveFuncionar(
        int percentual,
        double esperado) {

    double obtido = Desconto.calcular(100.0, percentual);

    assertEquals(esperado, obtido, 0.001);
}
```

### 10.3. Por que testar os dois lados?

Os valores próximos aos limites detectam erros como:

```java
// Incorreto: rejeita 0 e 100, embora sejam válidos.
if (percentual <= 0 || percentual >= 100) {
    throw new IllegalArgumentException();
}
```

Esse tipo de falha é conhecido como erro **off-by-one**: a transição foi implementada uma unidade antes ou depois do ponto correto.

Os valores também representam três partições de equivalência:

| Partição | Intervalo | Situação |
|---|---|---|
| Abaixo do mínimo | `percentual < 0` | Inválida |
| Dentro da faixa | `0 ≤ percentual ≤ 100` | Válida |
| Acima do máximo | `percentual > 100` | Inválida |

Como `percentual` é `int`, o domínio é discreto: existem valores imediatamente anteriores e posteriores aos limites.

**Verificação:** para uma idade válida entre 18 e 65, inclusive, quais seis valores formam um conjunto forte de fronteiras?

---

## 11. `@Timeout`: limite de tempo

`@Timeout` determina o tempo máximo permitido para o teste.

```java
// Executa uma única vez.
@Test

// O teste deve terminar em até 100 milissegundos.
@Timeout(
        value = 100,
        unit = TimeUnit.MILLISECONDS
)
void calcularDeveTerminarRapidamente() {
    double obtido = Desconto.calcular(250.0, 15);

    // O timeout verifica o tempo; a asserção verifica o resultado.
    assertEquals(212.50, obtido, 0.001);
}
```

Elementos da anotação:

| Elemento | Função |
|---|---|
| `value = 100` | Quantidade de tempo permitida |
| `unit = TimeUnit.MILLISECONDS` | Unidade usada para interpretar o valor |

`TimeUnit` é um `enum` do Java. Algumas opções são:

```java
TimeUnit.NANOSECONDS
TimeUnit.MICROSECONDS
TimeUnit.MILLISECONDS
TimeUnit.SECONDS
TimeUnit.MINUTES
```

Se `unit` for omitido, o JUnit utiliza segundos:

```java
@Timeout(2) // Dois segundos
```

### Uso adequado

Use `@Timeout` para proteger a suíte contra:

- laços infinitos;
- esperas que não terminam;
- travamentos;
- lentidão evidente.

### Timeout não é benchmark

| `@Timeout` | Benchmark |
|---|---|
| Verifica se um limite foi ultrapassado | Mede desempenho de forma controlada |
| Protege contra travamentos | Compara implementações |
| Não realiza medição estatística rigorosa | Considera aquecimento e várias execuções |

Limites excessivamente curtos podem gerar testes instáveis, pois o tempo varia conforme a máquina, a carga do sistema, a JVM e o ambiente de integração contínua. Use uma margem coerente com o risco que deseja detectar.

**Verificação:** para proteger contra um possível laço infinito, você usaria `@Timeout` ou um benchmark?

---

## 12. Como escolher a fonte

| Necessidade | Fonte recomendada |
|---|---|
| Um argumento simples | `@ValueSource` |
| Vários argumentos simples | `@CsvSource` |
| Muitos registros em arquivo | `@CsvFileSource` |
| Objetos ou dados construídos | `@MethodSource` |
| Somente `null` | `@NullSource` |
| Somente valor vazio | `@EmptySource` |
| `null` e vazio | `@NullAndEmptySource` |

---

## 13. Erros frequentes

| Erro | Causa provável | Correção |
|---|---|---|
| `No ParameterResolver` | O método possui parâmetro, mas não recebeu fonte adequada | Use `@ParameterizedTest` e uma fonte |
| `ArgumentConversionException` | O valor não pode ser convertido para o tipo do parâmetro | Corrija o dado ou o tipo Java |
| `PreconditionViolationException` | O `@MethodSource` não foi encontrado ou não forneceu dados | Confira nome, retorno e `static` |
| `Expected ... Actual ...` | O resultado obtido divergiu do esperado | Leia o cenário e revise regra ou expectativa |
| Parâmetros não resolvidos | Dependência ou import ausente | Confira `junit-jupiter` e os imports |
| Teste passa isolado e falha na suíte | Limite de tempo muito curto ou estado compartilhado | Aumente a margem e elimine dependências entre testes |

---

## 14. Perguntas de fixação

1. Qual fonte usar quando apenas uma String varia?
2. Qual fonte é adequada para preço, percentual e resultado esperado?
3. Quatro linhas em `@CsvSource` produzem quantas execuções?
4. Por que comparar `double` com delta?
5. Quando `@MethodSource` é preferível a `@CsvSource`?
6. Qual é a diferença entre `null`, `""` e `"   "`?
7. Quais valores devem ser testados ao redor da faixa de 0 a 100?
8. Por que `@Timeout` não substitui um benchmark?

### Respostas

1. `@ValueSource(strings = {...})`.
2. `@CsvSource`.
3. Quatro execuções.
4. Porque `double` pode apresentar pequenas diferenças de representação binária.
5. Quando os casos usam objetos, dados construídos ou lógica Java.
6. `null` é ausência de objeto; `""` tem zero caracteres; `"   "` possui caracteres, mas está em branco.
7. `-1`, `0`, `1`, `99`, `100` e `101`.
8. Porque ele apenas verifica se um limite foi ultrapassado; não realiza medição estatística de desempenho.

---

## 15. Prática guiada

Implemente a suíte da classe `Desconto`:

1. crie a classe de produção;
2. prepare a classe de teste e os imports;
3. teste resultados com `@CsvSource`;
4. teste preços negativos com `@ValueSource`;
5. teste percentuais imediatamente fora das fronteiras;
6. teste as fronteiras válidas e seus vizinhos internos;
7. adicione um teste com `@Timeout`;
8. provoque uma falha e interprete o relatório.

---

## 16. Desafio autônomo — `CalculadoraFrete`

### Regra de negócio

- frete comum = `8 + pesoKg × 2`;
- entrega expressa = frete comum acrescido de 50%;
- o peso deve ser maior que zero;
- mensagem para peso inválido: `O peso deve ser maior que zero.`

### Classe de produção — gabarito

```java
package org.example;

/**
 * Calcula o frete comum ou expresso de uma entrega.
 */
public final class CalculadoraFrete {

    // Impede a criação de objetos da classe utilitária.
    private CalculadoraFrete() {
    }

    public static double calcular(
            double pesoKg,
            boolean entregaExpressa) {

        // Zero e valores negativos são inválidos.
        if (pesoKg <= 0) {
            throw new IllegalArgumentException(
                    "O peso deve ser maior que zero."
            );
        }

        // Valor base mais R$ 2,00 por quilograma.
        double freteComum = 8.0 + pesoKg * 2.0;

        // A modalidade expressa acrescenta 50%.
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

    // Verifica diferentes pesos e modalidades.
    @ParameterizedTest(
            name = "peso={0}, expressa={1}, esperado={2}"
    )
    @CsvSource({
            // peso, expressa, esperado
            "0.01, false,  8.02", // Fronteira válida
            "1.00, false, 10.00",
            "5.00, false, 18.00",
            "1.00, true,  15.00",
            "5.00, true,  27.00"
    })
    void calcularDeveRetornarFreteCorreto(
            double peso,
            boolean expressa,
            double esperado) {

        double obtido = CalculadoraFrete.calcular(
                peso,
                expressa
        );

        assertEquals(esperado, obtido, 0.001);
    }

    // Verifica a fronteira inválida e valores negativos.
    @ParameterizedTest(name = "peso inválido: {0}")
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

---

## 17. Desafio 2 — `CalculadoraEstacionamento`

### Nível intermediário

Implemente a classe utilitária `CalculadoraEstacionamento` e seus testes parametrizados.

### Regras de negócio

- o método deverá ser `calcular(int horas, boolean fimDeSemana)`;
- a primeira hora custa R$ 5,00;
- cada hora adicional custa R$ 3,00;
- aos finais de semana, o valor final recebe acréscimo de 20%;
- a permanência deve estar entre 1 e 24 horas, inclusive;
- valores fora da faixa lançam `IllegalArgumentException`;
- mensagem: `A permanência deve estar entre 1 e 24 horas.`.

### Testes obrigatórios

1. use `@CsvSource` com pelo menos seis cenários válidos;
2. teste o mesmo número de horas em dia comum e no fim de semana;
3. inclua as fronteiras válidas `1` e `24`;
4. inclua os vizinhos internos `2` e `23`;
5. teste os valores inválidos `0`, `-1` e `25` com `@ValueSource`;
6. verifique o tipo e a mensagem da exceção;
7. compare os resultados `double` usando delta;
8. crie nomes legíveis para todas as execuções.

### Exemplo de estrutura

O modelo mostra como organizar os métodos, mas os dados e as asserções devem ser completados pelo estudante:

```java
class CalculadoraEstacionamentoTest {

    @ParameterizedTest(
            name = "horas={0}, fim de semana={1}, esperado={2}"
    )
    @CsvSource({
            // horas, fimDeSemana, esperado
            // Complete com os cenários válidos.
    })
    void calcularDeveRetornarValorCorreto(
            int horas,
            boolean fimDeSemana,
            double esperado) {

        // Act: execute CalculadoraEstacionamento.calcular(...).

        // Assert: compare esperado e obtido usando delta.
    }

    @ParameterizedTest(name = "permanência inválida: {0}")
    @ValueSource(ints = {/* complete com os valores inválidos */})
    void permanenciaInvalidaDeveLancarExcecao(int horas) {
        // Use assertThrows e verifique a mensagem.
    }
}
```

### Perguntas de análise

1. Por que `1` e `24` precisam ser testados mesmo sendo valores válidos?
2. Qual erro seria detectado por `0`, mas talvez não fosse detectado por `-100`?
3. Por que é importante testar o mesmo número de horas nas duas modalidades?

---

## 18. Desafio 3 — `CalculadoraPedido`

### Nível avançado

Neste desafio, os cenários incluem um objeto. Por isso, a principal fonte de dados deverá ser `@MethodSource`.

Crie o registro:

```java
public record Item(
        String nome,
        double precoUnitario,
        int quantidade) {
}
```

Depois, implemente a classe utilitária `CalculadoraPedido` e o método:

```java
public static double calcular(
        Item item,
        int percentualCupom)
```

### Regras de negócio

- o item é obrigatório;
- o nome não pode ser nulo, vazio ou formado somente por espaços;
- o preço unitário deve ser maior que zero;
- a quantidade deve ser maior que zero;
- o percentual do cupom deve estar entre 0 e 30, inclusive;
- subtotal = preço unitário multiplicado pela quantidade;
- valor final = subtotal menos o percentual do cupom.

### Mensagens das exceções

| Situação inválida | Mensagem esperada |
|---|---|
| Item nulo | `O item é obrigatório.` |
| Nome ausente | `O nome do item é obrigatório.` |
| Preço zero ou negativo | `O preço deve ser maior que zero.` |
| Quantidade zero ou negativa | `A quantidade deve ser maior que zero.` |
| Cupom fora da faixa | `O cupom deve estar entre 0 e 30.` |

### Testes obrigatórios

1. use `@MethodSource` com pelo menos cinco objetos `Item` válidos;
2. inclua cupons de `0%`, `1%`, `29%` e `30%`;
3. teste os cupons inválidos `-1` e `31`;
4. use `@NullSource` para testar um item nulo;
5. use `@NullAndEmptySource` e `@ValueSource` para testar nomes ausentes ou em branco;
6. teste preço e quantidade com valores válidos e inválidos próximos às fronteiras;
7. verifique todas as mensagens de exceção;
8. compare os resultados com delta;
9. adicione um `@Timeout` de um segundo;
10. use a descrição do cenário como nome de cada execução.

### Exemplo de estrutura

```java
class CalculadoraPedidoTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("cenariosDePedido")
    void calcularDeveAtenderCenariosValidos(
            String descricao,
            Item item,
            int percentualCupom,
            double esperado) {

        // Act: execute o cálculo.

        // Assert: compare esperado e obtido usando delta.
    }

    static Stream<Arguments> cenariosDePedido() {
        return Stream.of(
                // Arguments.of(
                //     "descrição do cenário",
                //     new Item("nome", preço, quantidade),
                //     percentualCupom,
                //     resultadoEsperado
                // )
        );
    }

    @ParameterizedTest
    @NullSource
    void itemNuloDeveLancarExcecao(Item item) {
        // Use assertThrows e verifique a mensagem.
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void nomeAusenteDeveLancarExcecao(String nome) {
        // Crie um Item usando o nome recebido.
        // Depois verifique a exceção e sua mensagem.
    }

    @ParameterizedTest
    @ValueSource(ints = {/* complete */})
    void cupomInvalidoDeveLancarExcecao(int percentualCupom) {
        // Crie um Item válido e teste o cupom recebido.
    }
}
```

### Perguntas de análise

1. Por que `@MethodSource` é mais adequado para os cenários válidos deste desafio?
2. O que aconteceria se a validação chamasse `item.nome().isBlank()` antes de verificar se `item` é nulo?
3. Por que o resultado esperado não deve ser calculado com a mesma fórmula usada na classe de produção?
4. Quais testes detectariam um erro que aceitasse cupom de `31%`?

---

## 19. Critérios de avaliação

- fonte de dados adequada ao cenário;
- correspondência correta entre dados e parâmetros;
- cobertura de casos válidos, inválidos e fronteiras;
- asserções corretas, incluindo delta e mensagem;
- nomes descritivos e comentários que explicam decisões;
- execução integral da suíte sem falhas inesperadas.

---

**Próxima aula:** organização e leitura dos resultados, mantendo o foco na qualidade das evidências produzidas pela suíte de testes.
