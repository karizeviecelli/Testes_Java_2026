# Aula 03 — Primeiros Testes com JUnit 5

**Módulo:** 2 — JUnit 5 na Prática
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

Ao final desta aula, você será capaz de:

- Explicar o ciclo de vida de um teste com `@BeforeEach`, `@Test` e `@AfterEach`;
- Escrever sua primeira classe de teste JUnit 5 do zero;
- Executar testes pelo IntelliJ e interpretar o resultado (verde/vermelho);
- Criar uma nova classe de entidade acompanhada, desde já, de sua respectiva classe de teste.

---

## 🖼️ Retomando a analogia — a primeira audiência

Com o cartório pronto (Aula 02), hoje abrimos a primeira **audiência** de verdade. Cada método anotado com `@Test` é uma audiência independente: o JUnit convoca a "testemunha" (nosso código), ouve o caso e anuncia o veredito — passou (verde) ou falhou (vermelho). E, assim como todo tribunal, existe um **ritual antes e depois de cada audiência** — é isso que `@BeforeEach` e `@AfterEach` representam.

---

## 📚 Conteúdo teórico

### 1. Onde os testes moram

Toda classe de teste vive em `src/test/java`, e por convenção **espelha o pacote** da classe testada. Se `Produto` está em `src/main/java/br/edu/testesistemas/entidades/Produto.java`, o teste fica em `src/test/java/br/edu/testesistemas/entidades/ProdutoTest.java`.

### 2. Anotações essenciais do JUnit 5

| Anotação | Quando executa |
|---|---|
| `@Test` | Marca um método como um caso de teste |
| `@BeforeEach` | Executa **antes de cada** `@Test` — ideal para preparar objetos que o teste vai usar |
| `@AfterEach` | Executa **depois de cada** `@Test` — ideal para "limpar a bagunça" |
| `@BeforeAll` | Executa **uma única vez**, antes de todos os testes da classe (método deve ser `static`) |
| `@AfterAll` | Executa **uma única vez**, depois de todos os testes da classe (método deve ser `static`) |

### 3. Primeira classe de entidade da aula: `Livro`

```java
package br.edu.testesistemas.entidades;

// Entidade que representa um Livro do acervo
public class Livro {

    private String titulo;
    private String autor;
    private boolean emprestado;

    // Construtor: todo livro nasce disponível (não emprestado)
    public Livro(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
        this.emprestado = false;
    }

    // Regra de negócio: só posso emprestar um livro que não está emprestado
    public void emprestar() {
        if (this.emprestado) {
            throw new IllegalStateException("Este livro já está emprestado.");
        }
        this.emprestado = true;
    }

    // Regra de negócio: só posso devolver um livro que está emprestado
    public void devolver() {
        if (!this.emprestado) {
            throw new IllegalStateException("Este livro não está emprestado.");
        }
        this.emprestado = false;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public boolean isEmprestado() {
        return emprestado;
    }
}
```

### 4. Primeira classe de teste: `LivroTest`

```java
package br.edu.testesistemas.entidades;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LivroTest {

    // Atributo que vai guardar o objeto usado em cada audiência (teste)
    private Livro livro;

    @BeforeEach
    void configurarCadaTeste() {
        // Executa ANTES de cada @Test: cria um Livro "fresco" para cada audiência,
        // garantindo que um teste não interfira no outro
        livro = new Livro("Clean Code", "Robert C. Martin");
        System.out.println("[BeforeEach] Livro criado para o teste.");
    }

    @AfterEach
    void encerrarCadaTeste() {
        // Executa DEPOIS de cada @Test: aqui poderíamos, por exemplo,
        // "descartar" recursos usados no teste (não há nada a limpar neste caso simples)
        System.out.println("[AfterEach] Teste finalizado.");
    }

    @Test
    void livroDeveNascerDisponivel() {
        // Verifica se um livro recém-criado começa como NÃO emprestado
        assertFalse(livro.isEmprestado());
    }

    @Test
    void emprestarDeveMarcarLivroComoEmprestado() {
        livro.emprestar();
        assertTrue(livro.isEmprestado());
    }

    @Test
    void devolverAposEmprestimoDeveLiberarLivro() {
        livro.emprestar();
        livro.devolver();
        assertFalse(livro.isEmprestado());
    }
}
```

**O que acontece quando rodamos essa classe:**

```
[BeforeEach] Livro criado para o teste.
✓ livroDeveNascerDisponivel
[AfterEach] Teste finalizado.

[BeforeEach] Livro criado para o teste.
✓ emprestarDeveMarcarLivroComoEmprestado
[AfterEach] Teste finalizado.

[BeforeEach] Livro criado para o teste.
✓ devolverAposEmprestimoDeveLiberarLivro
[AfterEach] Teste finalizado.
```

> 💡 Note que `@BeforeEach` roda **três vezes** — uma para cada `@Test`. Isso garante que cada audiência comece com um "réu" limpo, sem interferência das audiências anteriores.

### 5. Executando pelo IntelliJ

Ao lado de cada `@Test` (e ao lado do nome da classe), o IntelliJ mostra um ícone de "play" verde. Clicar nele executa aquele teste (ou todos os testes da classe) e mostra o resultado na aba de execução: ✅ verde para sucesso, ❌ vermelho para falha, com a mensagem de erro detalhada.

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 70 minutos
**Formato:** individual ou em dupla

### Passo a passo

1. No seu projeto Maven, crie a entidade `Funcionario` em `src/main/java`, com atributos `nome` (String), `salario` (double) e `ativo` (boolean, iniciando `true` no construtor);
2. Implemente o método `demitir()`, que só pode ser chamado se `ativo == true` (senão lança `IllegalStateException`) e marca `ativo = false`;
3. Implemente o método `aumentarSalario(double percentual)`, que lança `IllegalArgumentException` se `percentual <= 0`, e senão aumenta o salário proporcionalmente;
4. Crie a classe `FuncionarioTest` em `src/test/java`, no mesmo pacote;
5. Use `@BeforeEach` para instanciar um `Funcionario` novo antes de cada teste;
6. Escreva pelo menos 3 métodos `@Test`: um verificando que o funcionário nasce ativo, um verificando que `demitir()` funciona, e um verificando que `aumentarSalario()` aumenta o valor corretamente;
7. Rode os testes pelo IntelliJ e confirme que todos passam (verde).

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
package br.edu.testesistemas.entidades;

public class Funcionario {

    private String nome;
    private double salario;
    private boolean ativo;

    public Funcionario(String nome, double salario) {
        this.nome = nome;
        this.salario = salario;
        this.ativo = true;
    }

    public void demitir() {
        if (!this.ativo) {
            throw new IllegalStateException("Funcionário já está demitido.");
        }
        this.ativo = false;
    }

    public void aumentarSalario(double percentual) {
        if (percentual <= 0) {
            throw new IllegalArgumentException("O percentual deve ser maior que zero.");
        }
        this.salario += this.salario * (percentual / 100);
    }

    public String getNome() { return nome; }
    public double getSalario() { return salario; }
    public boolean isAtivo() { return ativo; }
}
```

```java
package br.edu.testesistemas.entidades;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuncionarioTest {

    private Funcionario funcionario;

    @BeforeEach
    void configurar() {
        // Um funcionário novo para cada teste, evitando que um teste "contamine" o outro
        funcionario = new Funcionario("Ana Silva", 3000.0);
    }

    @Test
    void funcionarioDeveNascerAtivo() {
        assertTrue(funcionario.isAtivo());
    }

    @Test
    void demitirDeveDesativarFuncionario() {
        funcionario.demitir();
        assertFalse(funcionario.isAtivo());
    }

    @Test
    void aumentarSalarioDeveAplicarPercentualCorretamente() {
        funcionario.aumentarSalario(10); // 10% de aumento
        assertEquals(3300.0, funcionario.getSalario());
    }
}
```

---

**Próxima aula:** vamos conhecer a família completa de asserções do JUnit 5 (`assertEquals`, `assertThrows`, `assertAll`...) e o padrão AAA (Arrange-Act-Assert) para deixar nossos testes ainda mais organizados.
