# Aula 06 — Organização de Testes, Herança e Polimorfismo

**Unidade curricular:** Teste de Sistemas  
**Carga horária:** 4 horas  
**Tema central:** tornar a suíte legível com `@DisplayName`, `@Nested` e `@Tag` e testar hierarquias de classes

---

## 1. Objetivos de aprendizagem

Ao final da aula, o estudante deverá ser capaz de:

- usar `@DisplayName` em classes e métodos de teste;
- agrupar cenários relacionados com `@Nested`;
- categorizar testes com `@Tag`;
- usar `@BeforeEach` dentro de um contexto aninhado;
- explicar herança, sobrescrita e polimorfismo;
- testar o contrato comum de uma superclasse abstrata;
- testar regras e validações específicas de cada subclasse;
- interpretar uma árvore de resultados do JUnit.

## 2. Organização sugerida das 4 horas

| Etapa | Tempo | Estratégia |
|---|---:|---|
| Retomada da Aula 5 | 15 min | Parametrização dentro de grupos de teste |
| Exposição dialogada | 55 min | Organização, POO e leitura de relatórios |
| Demonstração ao vivo | 35 min | Hierarquia `FormaGeometrica` |
| Intervalo | 10 min | — |
| Prática guiada | 80 min | Implementação em sete etapas |
| Desafio autônomo | 35 min | Hierarquia de pagamentos |
| Socialização e feedback | 10 min | Rubrica e próximo passo |

---

## 3. Por que organizar uma suíte?

Uma suíte pode estar correta e ainda ser difícil de compreender:

```text
FormaGeometricaTest
 ├─ teste1() ✔
 ├─ teste2() ✔
 ├─ teste3() ✔
 └─ teste4() ✘
```

O relatório não informa qual forma falhou, qual regra estava sendo verificada ou qual comportamento era esperado.

Uma saída organizada comunica a intenção:

```text
Hierarquia de formas geométricas
├─ Ao trabalhar com Quadrado
│  ├─ Lado 4 deve produzir área 16 ✔
│  └─ Lado zero deve ser rejeitado ✔
├─ Ao trabalhar com Círculo
│  └─ Raio 2 deve produzir área igual a 4π ✔
└─ Ao usar formas polimorficamente
   └─ Cada objeto deve executar sua própria fórmula ✔
```

## 4. `@DisplayName`: linguagem natural no relatório

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Hierarquia de formas geométricas")
class FormaGeometricaTest {

    @Test
    @DisplayName("Quadrado de lado 4 deve ter área 16")
    void calcularAreaDoQuadrado() {
        Quadrado quadrado = new Quadrado(4.0);

        assertEquals(16.0, quadrado.calcularArea(), 0.001);
    }
}
```

O nome Java permanece útil para manutenção; o texto do `@DisplayName` aparece na árvore de execução.

Uma boa frase costuma indicar:

1. contexto;
2. ação ou regra;
3. resultado esperado.

Evite nomes vagos como “Teste do círculo” ou “Deve funcionar”.

## 5. `@Nested`: contextos dentro da suíte

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

class FormaGeometricaTest {

    @Nested
    @DisplayName("Ao trabalhar com Quadrado")
    class TestesQuadrado {
        // Cálculo e validações do quadrado.
    }

    @Nested
    @DisplayName("Ao trabalhar com Círculo")
    class TestesCirculo {
        // Cálculo e validações do círculo.
    }
}
```

Regras importantes:

- a classe aninhada é interna e não deve ser `static`;
- cada grupo deve representar um contexto compreensível;
- os testes continuam independentes;
- é possível combinar `@Nested` com testes parametrizados.

## 6. Preparação com `@BeforeEach`

```java
@Nested
@DisplayName("Ao trabalhar com Quadrado")
class TestesQuadrado {

    private Quadrado quadrado;

    @BeforeEach
    void prepararQuadrado() {
        // Uma nova instância é criada antes de cada método deste grupo.
        quadrado = new Quadrado(4.0);
    }

    @Test
    @DisplayName("Lado 4 deve produzir área 16")
    void areaDeveSerDezesseis() {
        assertEquals(16.0, quadrado.calcularArea(), 0.001);
    }
}
```

Não use `@BeforeEach` para esconder dados importantes do cenário. Se um valor ajuda a compreender o teste, pode ser melhor mantê-lo no próprio método.

## 7. `@Tag`: categorias para execução seletiva

```java
@Nested
@Tag("geometria-circular")
@DisplayName("Ao trabalhar com Círculo")
class TestesCirculo {

    @Test
    @Tag("rapido")
    @DisplayName("Raio 2 deve produzir área igual a 4π")
    void areaDeveUsarPi() {
        // Este teste pertence a geometria-circular e rapido.
    }
}
```

Tags úteis podem representar:

- velocidade: `rapido`, `lento`;
- camada: `unidade`, `integracao`;
- recurso: `banco`, `api`;
- domínio: `geometria-circular`, `cartao`.

Padronize a escrita. `rapido`, `rápido` e `fast` são três tags diferentes.

---

## 8. Revisão de POO: herança

Herança permite definir um contrato geral e especializá-lo em subclasses.

### Superclasse abstrata

```java
package org.example.formas;

public abstract class FormaGeometrica {

    // Método abstrato: toda subclasse concreta deverá implementá-lo.
    public abstract double calcularArea();

    // Método concreto: comportamento comum herdado.
    public String descricao() {
        return "Forma geométrica";
    }
}
```

Não é possível criar `new FormaGeometrica()`, pois a classe é abstrata.

### Subclasse Quadrado

```java
package org.example.formas;

public class Quadrado extends FormaGeometrica {

    private final double lado;

    public Quadrado(double lado) {
        if (lado <= 0) {
            throw new IllegalArgumentException(
                    "O lado deve ser maior que zero."
            );
        }

        this.lado = lado;
    }

    @Override
    public double calcularArea() {
        return lado * lado;
    }
}
```

### Subclasse Círculo

```java
package org.example.formas;

public class Circulo extends FormaGeometrica {

    private final double raio;

    public Circulo(double raio) {
        if (raio <= 0) {
            throw new IllegalArgumentException(
                    "O raio deve ser maior que zero."
            );
        }

        this.raio = raio;
    }

    @Override
    public double calcularArea() {
        return Math.PI * raio * raio;
    }
}
```

## 9. Polimorfismo

```java
FormaGeometrica forma1 = new Quadrado(4.0);
FormaGeometrica forma2 = new Circulo(2.0);

double area1 = forma1.calcularArea(); // executa Quadrado.calcularArea()
double area2 = forma2.calcularArea(); // executa Circulo.calcularArea()
```

As referências têm o mesmo tipo declarado, mas apontam para objetos reais diferentes. Java seleciona a implementação sobrescrita em tempo de execução.

## 10. O que testar em uma hierarquia

| Nível | Evidência necessária | Exemplo |
|---|---|---|
| Contrato comum | Toda implementação cumpre a ideia geral | Toda forma válida produz área positiva |
| Subclasse | Fórmula específica | Quadrado usa lado² |
| Subclasse | Validação específica | Raio zero é rejeitado |
| Polimorfismo | O objeto real determina o comportamento | Mesma referência geral, resultados diferentes |
| Herança concreta | Método comum não sobrescrito | `descricao()` é herdado |

## 11. Suíte completa organizada

```java
package org.example.formas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Hierarquia de formas geométricas")
class FormaGeometricaTest {

    @Nested
    @DisplayName("Ao trabalhar com Quadrado")
    class TestesQuadrado {

        @Test
        @DisplayName("Lado 4 deve produzir área 16")
        void areaDeveSerLadoAoQuadrado() {
            Quadrado quadrado = new Quadrado(4.0);

            assertEquals(
                    16.0,
                    quadrado.calcularArea(),
                    0.001
            );
        }

        @Test
        @DisplayName("Lado zero deve ser rejeitado")
        void ladoZeroDeveLancarExcecao() {
            IllegalArgumentException erro = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Quadrado(0)
            );

            assertEquals(
                    "O lado deve ser maior que zero.",
                    erro.getMessage()
            );
        }
    }

    @Nested
    @Tag("geometria-circular")
    @DisplayName("Ao trabalhar com Círculo")
    class TestesCirculo {

        @Test
        @DisplayName("Raio 2 deve produzir área igual a 4π")
        void areaDeveUsarPi() {
            Circulo circulo = new Circulo(2.0);

            assertEquals(
                    Math.PI * 4,
                    circulo.calcularArea(),
                    0.0001
            );
        }

        @Test
        @DisplayName("Raio negativo deve ser rejeitado")
        void raioNegativoDeveLancarExcecao() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Circulo(-1.0)
            );
        }
    }

    @Nested
    @DisplayName("Ao usar formas polimorficamente")
    class TestesPolimorfismo {

        @Test
        @DisplayName("Cada objeto deve executar sua própria fórmula")
        void cadaFormaDeveCalcularSuaArea() {
            FormaGeometrica quadrado = new Quadrado(3.0);
            FormaGeometrica circulo = new Circulo(1.0);

            assertAll(
                () -> assertEquals(
                        9.0,
                        quadrado.calcularArea(),
                        0.001
                ),
                () -> assertEquals(
                        Math.PI,
                        circulo.calcularArea(),
                        0.0001
                )
            );
        }
    }
}
```

## 12. Integração com testes parametrizados

```java
@Nested
@DisplayName("Ao trabalhar com Quadrado")
class TestesQuadrado {

    @ParameterizedTest(name = "lado {0} gera área {1}")
    @CsvSource({
        "1.0,  1.0",
        "2.0,  4.0",
        "4.0, 16.0"
    })
    void areaDeveSerLadoAoQuadrado(
            double lado,
            double esperado) {

        Quadrado quadrado = new Quadrado(lado);

        assertEquals(
                esperado,
                quadrado.calcularArea(),
                0.001
        );
    }
}
```

`@Nested` organiza o contexto; `@ParameterizedTest` organiza os dados.

## 13. Erros frequentes

| Problema | Causa | Correção |
|---|---|---|
| Grupo não aparece | Classe interna sem `@Nested` | Adicione a anotação |
| JUnit não executa o grupo | Classe `@Nested` declarada `static` | Remova `static` |
| Relatório continua vago | `@DisplayName` genérico | Descreva cenário e expectativa |
| Filtro não encontra testes | Tags com grafias diferentes | Padronize os nomes |
| Teste polimórfico não prova nada | Apenas chama o método | Compare resultados específicos |
| Comparação de círculo falha por casas decimais | `double` sem delta | Use tolerância em `assertEquals` |

## 14. Perguntas de fixação

1. Qual anotação altera o nome mostrado no relatório?
2. Uma classe `@Nested` deve ser estática?
3. Uma tag aplicada à classe aninhada alcança quais testes?
4. Por que `FormaGeometrica forma = new Circulo(2)` é polimorfismo?
5. Por que ainda precisamos testar Círculo separadamente?

### Respostas

1. `@DisplayName`.
2. Não. Ela deve ser uma classe interna não estática.
3. Todos os testes pertencentes àquele contexto.
4. A referência usa o tipo geral, mas executa o comportamento do objeto concreto.
5. Porque a fórmula e as validações de Círculo são específicas dessa subclasse.

---

## 15. Desafio autônomo — hierarquia de pagamentos

Regras:

- `Pagamento` é abstrato e possui `valor` maior que zero;
- `PagamentoPix` retorna taxa zero;
- `PagamentoCartao` retorna 2,5% do valor;
- valor inválido lança `IllegalArgumentException`;
- mensagem: `O valor deve ser maior que zero.`

### Código de produção — gabarito

```java
package org.example.pagamentos;

public abstract class Pagamento {
    protected final double valor;

    protected Pagamento(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "O valor deve ser maior que zero."
            );
        }

        this.valor = valor;
    }

    public abstract double calcularTaxa();
}
```

```java
package org.example.pagamentos;

public class PagamentoPix extends Pagamento {

    public PagamentoPix(double valor) {
        super(valor);
    }

    @Override
    public double calcularTaxa() {
        return 0.0;
    }
}
```

```java
package org.example.pagamentos;

public class PagamentoCartao extends Pagamento {

    public PagamentoCartao(double valor) {
        super(valor);
    }

    @Override
    public double calcularTaxa() {
        return valor * 0.025;
    }
}
```

### Suíte — gabarito

```java
package org.example.pagamentos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Formas de pagamento")
class PagamentoTest {

    @Nested
    @DisplayName("Ao pagar com Pix")
    class TestesPix {

        @Test
        @DisplayName("Pix não deve cobrar taxa")
        void taxaDeveSerZero() {
            PagamentoPix pix = new PagamentoPix(200.0);

            assertEquals(0.0, pix.calcularTaxa(), 0.001);
        }

        @Test
        @DisplayName("Valor zero deve ser rejeitado")
        void valorZeroDeveFalhar() {
            IllegalArgumentException erro = assertThrows(
                    IllegalArgumentException.class,
                    () -> new PagamentoPix(0.0)
            );

            assertEquals(
                    "O valor deve ser maior que zero.",
                    erro.getMessage()
            );
        }
    }

    @Nested
    @Tag("cartao")
    @DisplayName("Ao pagar com Cartão")
    class TestesCartao {

        @Test
        @DisplayName("Cartão deve cobrar 2,5% de taxa")
        void taxaDeveSerDoisEMeioPorCento() {
            PagamentoCartao cartao = new PagamentoCartao(200.0);

            assertEquals(5.0, cartao.calcularTaxa(), 0.001);
        }
    }

    @Nested
    @DisplayName("Ao usar pagamentos polimorficamente")
    class TestesPolimorfismo {

        @Test
        @DisplayName("Cada pagamento deve calcular sua própria taxa")
        void cadaTipoDeveCalcularTaxaCorreta() {
            Pagamento pix = new PagamentoPix(200.0);
            Pagamento cartao = new PagamentoCartao(200.0);

            assertAll(
                () -> assertEquals(0.0, pix.calcularTaxa(), 0.001),
                () -> assertEquals(5.0, cartao.calcularTaxa(), 0.001)
            );
        }
    }
}
```

## 16. Critérios de avaliação

- modelagem correta da hierarquia;
- sobrescrita coerente dos métodos;
- classes `@Nested` organizadas por contexto;
- nomes legíveis com `@DisplayName`;
- tags padronizadas e úteis;
- regras específicas e exceções verificadas;
- teste polimórfico com evidência concreta;
- código legível e testes independentes.

---

**Síntese:** organizar testes não é apenas “arrumar arquivos”. É tornar as evidências compreensíveis para quem precisa localizar uma falha, revisar uma regra ou ampliar a suíte.
