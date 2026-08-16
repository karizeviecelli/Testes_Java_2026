# Aula 04 — Asserções e o padrão AAA

**Módulo:** 2 — JUnit 5 na Prática
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

Ao final desta aula, você será capaz de:

- Conhecer e aplicar as principais asserções do JUnit 5: `assertEquals`, `assertTrue/False`, `assertNull/NotNull`, `assertAll`, `assertThrows`;
- Organizar qualquer teste usando o padrão **AAA (Arrange-Act-Assert)**;
- Escrever nomes de teste claros e descritivos;
- Criar uma nova entidade acompanhada de testes que usem múltiplas asserções.

---

## 🖼️ Retomando a analogia — as evidências da audiência

Se `@Test` é a audiência, as **asserções** são as **evidências apresentadas ao juiz**. Cada `assert` é uma pergunta objetiva: "o valor obtido é igual ao esperado?", "uma exceção foi lançada?". O padrão **AAA** é o roteiro que todo bom advogado segue: primeiro organiza o caso (**Arrange**), depois provoca o evento em julgamento (**Act**), e só então apresenta as evidências (**Assert**).

---

## 📚 Conteúdo teórico

### 1. As principais asserções do JUnit 5

| Asserção | O que verifica |
|---|---|
| `assertEquals(esperado, obtido)` | Se dois valores são iguais |
| `assertTrue(condicao)` / `assertFalse(condicao)` | Se uma condição booleana é verdadeira/falsa |
| `assertNull(obj)` / `assertNotNull(obj)` | Se um objeto é nulo ou não |
| `assertThrows(Excecao.class, () -> {...})` | Se um bloco de código lança a exceção esperada |
| `assertAll(...)` | Agrupa várias asserções, executando todas mesmo que uma falhe (mostra todos os erros de uma vez) |

### 2. O padrão AAA (Arrange-Act-Assert)

Organizar um teste em três partes bem definidas deixa qualquer teste mais fácil de ler:

```java
@Test
void depositoDeveAumentarSaldo() {
    // Arrange (organizar): prepara os objetos e dados necessários
    ContaBancaria conta = new ContaBancaria("Maria", "12345-6");

    // Act (agir): executa a ação que está sendo testada
    conta.depositar(100.0);

    // Assert (verificar): confirma que o resultado é o esperado
    assertEquals(100.0, conta.getSaldo());
}
```

### 3. Testando exceções com `assertThrows`

```java
@Test
void sacarValorMaiorQueSaldoDeveLancarExcecao() {
    // Arrange
    ContaBancaria conta = new ContaBancaria("Maria", "12345-6");
    conta.depositar(50.0);

    // Act + Assert (aqui os dois se combinam: a "ação" é a chamada dentro da lambda)
    IllegalArgumentException excecao = assertThrows(
        IllegalArgumentException.class,
        () -> conta.sacar(100.0)
    );

    // Podemos até verificar a mensagem da exceção
    assertEquals("Saldo insuficiente para este saque.", excecao.getMessage());
}
```

### 4. Agrupando verificações com `assertAll`

```java
@Test
void contaRecemCriadaDeveTerDadosCorretos() {
    // Arrange + Act
    ContaBancaria conta = new ContaBancaria("João", "99999-9");

    // Assert: assertAll roda TODAS as verificações, mesmo que uma falhe,
    // e mostra todos os erros de uma vez (ao invés de parar no primeiro)
    assertAll(
        () -> assertEquals("João", conta.getTitular()),
        () -> assertEquals("99999-9", conta.getNumeroConta()),
        () -> assertEquals(0.0, conta.getSaldo())
    );
}
```

### 5. Nomenclatura clara de testes

Um bom nome de teste conta uma pequena história: **o que** está sendo testado + **em que condição** + **o que se espera**.

```java
// ✗ Nome vago — não diz nada sobre o cenário
void teste1() { ... }

// ✓ Nome descritivo — conta a história completa
void sacarValorMaiorQueSaldoDeveLancarExcecao() { ... }
```

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 70 minutos

### Passo a passo

1. Crie a entidade `Retangulo`, com atributos `largura` e `altura` (double), validados no construtor: ambos devem ser maiores que zero (senão, `IllegalArgumentException`);
2. Implemente os métodos `calcularArea()` e `calcularPerimetro()`;
3. Crie `RetanguloTest` e escreva, usando o padrão AAA:
   - Um teste com `assertEquals` para `calcularArea()`;
   - Um teste com `assertEquals` para `calcularPerimetro()`;
   - Um teste com `assertThrows` verificando que o construtor rejeita largura/altura inválidas;
   - Um teste com `assertAll` verificando `largura`, `altura` e `area` de um retângulo específico ao mesmo tempo.

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
package br.edu.testesistemas.entidades;

public class Retangulo {

    private double largura;
    private double altura;

    public Retangulo(double largura, double altura) {
        if (largura <= 0 || altura <= 0) {
            throw new IllegalArgumentException("Largura e altura devem ser maiores que zero.");
        }
        this.largura = largura;
        this.altura = altura;
    }

    public double calcularArea() {
        return largura * altura;
    }

    public double calcularPerimetro() {
        return 2 * (largura + altura);
    }

    public double getLargura() { return largura; }
    public double getAltura() { return altura; }
}
```

```java
package br.edu.testesistemas.entidades;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RetanguloTest {

    @Test
    void calcularAreaDeveMultiplicarLarguraPorAltura() {
        // Arrange
        Retangulo retangulo = new Retangulo(4.0, 5.0);
        // Act + Assert
        assertEquals(20.0, retangulo.calcularArea());
    }

    @Test
    void calcularPerimetroDeveSomarTodosOsLados() {
        Retangulo retangulo = new Retangulo(4.0, 5.0);
        assertEquals(18.0, retangulo.calcularPerimetro());
    }

    @Test
    void criarRetanguloComLarguraInvalidaDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Retangulo(-1, 5.0));
    }

    @Test
    void retanguloDeveTerTodosOsDadosCorretos() {
        Retangulo retangulo = new Retangulo(3.0, 6.0);
        assertAll(
            () -> assertEquals(3.0, retangulo.getLargura()),
            () -> assertEquals(6.0, retangulo.getAltura()),
            () -> assertEquals(18.0, retangulo.calcularArea())
        );
    }
}
```

---

**Próxima aula:** vamos aprender a rodar o mesmo teste várias vezes com dados diferentes usando testes parametrizados (`@ParameterizedTest`), evitando repetir código.
