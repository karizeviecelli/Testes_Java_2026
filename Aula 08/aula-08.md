# Aula 08 — Mockito I: @Mock, when/thenReturn, verify

**Módulo:** 3 — Dublês de Teste e Mockito
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Configurar a dependência do Mockito no `pom.xml`;
- Criar mocks automaticamente com `@Mock` e `@ExtendWith(MockitoExtension.class)`;
- Programar o comportamento de um mock com `when(...).thenReturn(...)`;
- Verificar interações com `verify(...)`;
- Realizar a **avaliação intermediária** da UC.

---

## 🖼️ Retomando a analogia — contratando atores profissionais

Na Aula 07, criamos nossos "atores" manualmente — escrevendo classes stub à mão. Isso funciona, mas dá trabalho e é repetitivo. O **Mockito** é a agência de casting profissional do nosso tribunal: com poucas linhas, ele cria um "ator" (mock) que podemos programar e cuja atuação (interações) podemos verificar depois.

---

## 📚 Conteúdo teórico

### 1. Adicionando o Mockito ao `pom.xml`

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

O `mockito-junit-jupiter` traz a integração oficial do Mockito com o JUnit 5, incluindo a extensão que usaremos a seguir.

### 2. Criando um mock com `@Mock`

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) // ativa o suporte do Mockito nesta classe de teste
class CheckoutTest {

    @Mock
    private ServicoPagamento servicoPagamentoMock; // Mockito cria o "ator" automaticamente
}
```

Note como isso substitui, com uma única anotação, todo o trabalho manual de criar uma classe `ServicoPagamentoStubAprovado` que fizemos na Aula 07!

### 3. Programando o comportamento com `when/thenReturn`

```java
import static org.mockito.Mockito.when;

@Test
void finalizarCompraComPagamentoAprovado() {
    // Arrange: "quando processar() for chamado com qualquer double, retorne true"
    when(servicoPagamentoMock.processar(anyDouble())).thenReturn(true);

    Checkout checkout = new Checkout(servicoPagamentoMock);

    // Act
    boolean resultado = checkout.finalizarCompra(150.0);

    // Assert
    assertTrue(resultado);
}
```

`anyDouble()` é um **matcher** do Mockito: significa "aceite qualquer valor double" — útil quando não importa o valor exato passado.

### 4. Verificando interações com `verify`

Além de definir o que o mock retorna, podemos confirmar que ele **foi realmente chamado**, e como:

```java
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@Test
void finalizarCompraDeveChamarProcessarUmaVez() {
    when(servicoPagamentoMock.processar(anyDouble())).thenReturn(true);
    Checkout checkout = new Checkout(servicoPagamentoMock);

    checkout.finalizarCompra(150.0);

    // Assert: confirma que processar(150.0) foi chamado exatamente 1 vez
    verify(servicoPagamentoMock, times(1)).processar(150.0);
}
```

`verify` é a "prova documental" de que a interação esperada realmente aconteceu — algo que um Stub manual não nos dava facilmente.

### 5. Diferença de foco: `when/thenReturn` vs. `verify`

| Abordagem | Pergunta que responde |
|---|---|
| `when(...).thenReturn(...)` | "O que o mock deve devolver quando chamado?" |
| `verify(...)` | "O mock foi realmente chamado, e da forma esperada?" |

Um mesmo teste pode (e frequentemente deve) usar as duas técnicas juntas.

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 60 minutos (o restante da aula é dedicado à avaliação intermediária)

### Passo a passo

1. Adicione as dependências do Mockito ao seu `pom.xml`;
2. Reescreva a classe `CadastroUsuarioTest` da Aula 07, substituindo o `RepositorioUsuarioFake` por um `@Mock` do Mockito;
3. Use `when(...).thenReturn(...)` para simular que um e-mail já existe (retornando um usuário) e para simular que ele não existe (retornando `null`);
4. Use `verify(...)` para confirmar que `salvar()` foi chamado exatamente uma vez no caminho de sucesso, e que `salvar()` **nunca** foi chamado no caminho de e-mail duplicado (`verify(mock, never()).salvar(any())`).

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
package br.edu.testesistemas.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioMock;

    @Test
    void cadastrarNovoUsuarioDeveSalvarNoRepositorio() {
        // Arrange: simula que o e-mail ainda NÃO existe
        when(repositorioMock.buscarPorEmail(anyString())).thenReturn(null);
        CadastroUsuario cadastro = new CadastroUsuario(repositorioMock);

        // Act
        cadastro.cadastrar(new Usuario("Ana", "ana@email.com"));

        // Assert: confirma que salvar() foi chamado exatamente 1 vez
        verify(repositorioMock, times(1)).salvar(any(Usuario.class));
    }

    @Test
    void cadastrarEmailDuplicadoNaoDeveChamarSalvar() {
        // Arrange: simula que o e-mail JÁ existe
        Usuario usuarioExistente = new Usuario("Ana", "ana@email.com");
        when(repositorioMock.buscarPorEmail("ana@email.com")).thenReturn(usuarioExistente);
        CadastroUsuario cadastro = new CadastroUsuario(repositorioMock);

        // Act + Assert
        assertThrows(IllegalStateException.class, () ->
            cadastro.cadastrar(new Usuario("Outra Ana", "ana@email.com"))
        );

        // Assert: garante que salvar() NUNCA foi chamado nesse cenário de erro
        verify(repositorioMock, never()).salvar(any());
    }
}
```

> 💡 Compare este código com o da Aula 07: fizemos **muito mais** (incluindo a verificação de que `salvar()` nunca foi chamado no caso de erro) com **menos código manual**.

---

## 📝 Avaliação Intermediária

A avaliação intermediária desta UC cobre os conteúdos das Aulas 01 a 08: fundamentos de teste, POO aplicado a testes, JUnit 5 (anotações, asserções, testes parametrizados, organização), test doubles e os fundamentos do Mockito vistos hoje. A prova é prática: os alunos recebem uma classe de entidade com uma dependência externa e devem escrever a suíte de testes completa, incluindo ao menos um mock com Mockito.

---

**Próxima aula:** vamos aprofundar o uso do Mockito com `ArgumentCaptor`, `@InjectMocks` e outras ferramentas para cenários mais complexos.
