# Aula 12 — Introdução ao Spring + Testes de Serviço

**Módulo:** 5 — Testando Aplicações Spring
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Entender o que é o Spring/Spring Boot e o papel da Injeção de Dependência (IoC);
- Relacionar Injeção de Dependência com os conceitos de POO já estudados;
- Configurar as dependências de teste do Spring Boot no `pom.xml`;
- Testar a camada de **Service** de uma aplicação Spring, usando Mockito para isolar dependências.

---

## 🖼️ Retomando a analogia — o novo prédio do tribunal

Até aqui, construímos nosso "tribunal" à mão: nós mesmos criávamos os objetos e conectávamos as dependências (`new Checkout(new ServicoPagamentoStub())`). O **Spring** é como se mudássemos para um **prédio novo e mais moderno**, com departamentos especializados (`@Service`, `@Repository`, `@Controller`) e um "gerente de instalações" — o **container de Injeção de Dependência** — que monta e conecta essas peças automaticamente para nós.

---

## 📚 Conteúdo teórico

### 1. O que é o Spring / Spring Boot

O **Spring Framework** é um framework Java para construção de aplicações, com forte ênfase em **Injeção de Dependência (Dependency Injection / IoC — Inversion of Control)**. O **Spring Boot** é uma camada sobre o Spring que simplifica a configuração, permitindo criar aplicações prontas para rodar com pouquíssima configuração manual.

### 2. Injeção de Dependência — relembrando POO

Lembra quando, na Aula 07, fazíamos isso manualmente?

```java
// "Na mão": nós montamos as peças
ServicoPagamento pagamento = new ServicoPagamentoReal();
Checkout checkout = new Checkout(pagamento);
```

Com Spring, apenas **anotamos** as classes, e o framework se encarrega de instanciar e conectar tudo:

```java
import org.springframework.stereotype.Service;

@Service // diz ao Spring: "gerencie essa classe para mim"
public class CheckoutService {

    private final ServicoPagamento servicoPagamento;

    // O Spring identifica que CheckoutService precisa de um ServicoPagamento
    // e injeta a implementação automaticamente pelo construtor
    public CheckoutService(ServicoPagamento servicoPagamento) {
        this.servicoPagamento = servicoPagamento;
    }

    public boolean finalizarCompra(double valor) {
        return servicoPagamento.processar(valor);
    }
}
```

> 💡 Isso é exatamente o **mesmo princípio de POO** que já usávamos: programar contra uma interface, não contra uma implementação concreta. O Spring só automatiza a parte de "montar as peças".

### 3. As camadas típicas de uma aplicação Spring

| Camada | Anotação | Responsabilidade |
|---|---|---|
| Controller | `@RestController` | Recebe requisições HTTP, delega para o Service |
| Service | `@Service` | Contém as regras de negócio |
| Repository | `@Repository` | Acesso a dados (banco de dados) |

Vamos testar essas três camadas ao longo das próximas aulas — hoje é a vez do **Service**.

### 4. Adicionando as dependências de teste do Spring Boot

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Esse "starter" já traz JUnit 5, Mockito e outras bibliotecas de teste integradas — muitas das dependências que configuramos manualmente nas aulas anteriores já vêm inclusas nele.

### 5. Testando a camada de Service — igual ao que já sabemos!

A boa notícia: **testar um `@Service` é exatamente como testar qualquer classe com dependências mockadas**, técnica que já dominamos desde a Aula 08.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // não precisamos subir o Spring inteiro para testar o Service!
class CheckoutServiceTest {

    @Mock
    private ServicoPagamento servicoPagamentoMock;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    void finalizarCompraComPagamentoAprovadoDeveRetornarTrue() {
        when(servicoPagamentoMock.processar(150.0)).thenReturn(true);

        boolean resultado = checkoutService.finalizarCompra(150.0);

        assertTrue(resultado);
    }
}
```

> 💡 Repare: usamos `@ExtendWith(MockitoExtension.class)`, **não** `@SpringBootTest`. Para testar um Service isoladamente, não precisamos subir o contexto completo do Spring — isso deixaria o teste mais lento sem necessidade. `@SpringBootTest` é reservado para testes de integração mais amplos, que veremos adiante.

### 6. Quando usar `@SpringBootTest`

`@SpringBootTest` sobe o **contexto completo da aplicação Spring** — útil quando o teste precisa verificar a integração real entre várias camadas, mas é significativamente mais lento que um teste unitário com Mockito puro. Regra geral: **prefira testes unitários (Mockito) sempre que possível**, reservando `@SpringBootTest` para cenários de integração genuinamente necessários.

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 80 minutos

### Passo a passo

1. Adicione a dependência `spring-boot-starter-test` ao `pom.xml`;
2. Crie a interface `RepositorioProduto` com o método `Produto buscarPorId(Long id)`;
3. Crie a classe `ProdutoService`, anotada com `@Service`, com o construtor recebendo `RepositorioProduto` e o método `double calcularPrecoComDesconto(Long id, double percentualDesconto)`, que busca o produto e aplica o desconto sobre seu preço;
4. Escreva `ProdutoServiceTest` usando `@ExtendWith(MockitoExtension.class)`, `@Mock` e `@InjectMocks` — sem subir o Spring;
5. Teste o cenário de desconto aplicado corretamente, e o cenário de produto não encontrado (deve lançar exceção).

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
package br.edu.testesistemas.produto;

public interface RepositorioProduto {
    Produto buscarPorId(Long id);
}
```

```java
package br.edu.testesistemas.produto;

import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final RepositorioProduto repositorioProduto;

    public ProdutoService(RepositorioProduto repositorioProduto) {
        this.repositorioProduto = repositorioProduto;
    }

    public double calcularPrecoComDesconto(Long id, double percentualDesconto) {
        Produto produto = repositorioProduto.buscarPorId(id);
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
        return produto.getPreco() * (1 - percentualDesconto / 100);
    }
}
```

```java
package br.edu.testesistemas.produto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private RepositorioProduto repositorioProdutoMock;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void calcularPrecoComDescontoDeveAplicarPercentualCorretamente() {
        Produto produto = new Produto("Mouse", 100.0);
        when(repositorioProdutoMock.buscarPorId(1L)).thenReturn(produto);

        double precoFinal = produtoService.calcularPrecoComDesconto(1L, 10);

        assertEquals(90.0, precoFinal);
    }

    @Test
    void calcularPrecoComProdutoInexistenteDeveLancarExcecao() {
        when(repositorioProdutoMock.buscarPorId(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
            produtoService.calcularPrecoComDesconto(99L, 10)
        );
    }
}
```

---

**Próxima aula:** vamos descer uma camada e testar o **Repository**, usando `@DataJpaTest` e um banco de dados em memória (H2) — o início do nosso **projeto integrador**.
