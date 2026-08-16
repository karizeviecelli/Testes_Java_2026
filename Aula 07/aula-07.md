# Aula 07 — Test Doubles: Dummy, Stub, Fake, Spy e Mock

**Módulo:** 3 — Dublês de Teste e Mockito
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Entender por que precisamos de "dublês" ao testar código que depende de outras partes do sistema;
- Diferenciar dummy, stub, fake, spy e mock;
- Identificar quando cada tipo de dublê é apropriado;
- Criar manualmente (sem framework ainda) um stub simples para destravar um teste.

---

## 🖼️ Retomando a analogia — os atores contratados

Nem toda testemunha em um julgamento está realmente disponível. Às vezes o tribunal contrata um **ator** para representar uma testemunha ausente — alguém que segue um roteiro específico. Isso é exatamente o que um **test double** faz: substitui uma dependência real (um banco de dados, uma API externa, um serviço de e-mail) por um "ator" controlado, para que possamos testar nosso código **isoladamente**, sem depender de sistemas externos lentos, instáveis ou fora do nosso controle.

---

## 📚 Conteúdo teórico

### 1. O problema: dependências externas

Imagine uma classe `NotificadorPedido` que, ao confirmar um pedido, envia um e-mail de verdade através de um serviço externo:

```java
public class NotificadorPedido {
    private ServicoEmail servicoEmail; // depende de algo externo!

    public void notificar(Pedido pedido) {
        servicoEmail.enviar(pedido.getEmailCliente(), "Pedido confirmado!");
    }
}
```

Se testarmos essa classe "de verdade", cada execução de teste **enviaria um e-mail real**. Isso é lento, não-determinístico (depende da internet) e indesejável. Precisamos de um **dublê** que finja ser o `ServicoEmail`.

### 2. Os cinco tipos de test double

| Tipo | O que faz |
|---|---|
| **Dummy** | Um objeto "de preenchimento", passado apenas para satisfazer a assinatura de um método, mas nunca realmente usado |
| **Stub** | Retorna respostas pré-definidas e fixas para as chamadas feitas durante o teste, sem lógica real |
| **Fake** | Tem uma implementação funcional, mas simplificada (ex.: um banco de dados em memória no lugar de um banco real) |
| **Spy** | Um objeto real (ou parcialmente real) que "grava" as chamadas feitas a ele, permitindo verificar depois quais métodos foram chamados |
| **Mock** | Um objeto "programado" com expectativas: você define o comportamento esperado e verifica se as interações aconteceram como previsto |

### 3. Exemplo de Dummy

```java
// O parâmetro "logger" é exigido pela assinatura do método, mas o teste
// não se importa com o que ele faz — é apenas um "preenchimento"
public class ServicoLogDummy implements ServicoLog {
    @Override
    public void registrar(String mensagem) {
        // Não faz nada — dummy não tem comportamento real
    }
}
```

### 4. Exemplo de Stub (criado manualmente)

```java
// Um Stub simples: sempre retorna o mesmo resultado fixo, sem se conectar a nada real
public class ServicoEmailStub implements ServicoEmail {
    @Override
    public boolean enviar(String destinatario, String mensagem) {
        return true; // sempre "finge" que o envio deu certo
    }
}
```

```java
@Test
void notificarPedidoDeveChamarEnvioDeEmail() {
    // Arrange: usamos o Stub no lugar do serviço real
    ServicoEmailStub stub = new ServicoEmailStub();
    NotificadorPedido notificador = new NotificadorPedido(stub);
    Pedido pedido = new Pedido("cliente@email.com");

    // Act
    boolean resultado = notificador.notificar(pedido);

    // Assert: como o Stub sempre retorna true, sabemos o que esperar
    assertTrue(resultado);
}
```

### 5. Exemplo de Fake

```java
// Fake: implementação funcional, só que simplificada (em memória, sem banco real)
public class RepositorioClienteFake implements RepositorioCliente {
    private List<Cliente> clientes = new ArrayList<>();

    @Override
    public void salvar(Cliente cliente) {
        clientes.add(cliente); // "persiste" só na memória, não em um banco real
    }

    @Override
    public Cliente buscarPorEmail(String email) {
        return clientes.stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }
}
```

### 6. Spy e Mock — uma prévia

Spy e Mock geralmente são criados com o auxílio de um **framework** (o Mockito, que veremos nas próximas duas aulas), porque programá-los manualmente é trabalhoso. Por enquanto, entenda a diferença conceitual:

- **Spy**: "Eu quero saber se e como esse objeto foi usado" (grava as interações);
- **Mock**: "Eu quero definir o comportamento esperado E verificar as interações previstas" (mistura stub + verificação).

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 70 minutos

### Passo a passo

1. Crie a interface `ServicoPagamento`, com o método `boolean processar(double valor)`;
2. Crie a classe `Checkout`, que recebe um `ServicoPagamento` no construtor e possui o método `boolean finalizarCompra(double valor)`, que delega ao `ServicoPagamento`;
3. Crie um **Stub** manual `ServicoPagamentoStubAprovado`, que sempre retorna `true`;
4. Crie um **Stub** manual `ServicoPagamentoStubRecusado`, que sempre retorna `false`;
5. Escreva `CheckoutTest` com dois testes: um usando o stub "aprovado" (esperando `true`) e outro usando o stub "recusado" (esperando `false`).

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
package br.edu.testesistemas.pagamento;

public interface ServicoPagamento {
    boolean processar(double valor);
}
```

```java
package br.edu.testesistemas.pagamento;

public class Checkout {
    private ServicoPagamento servicoPagamento;

    public Checkout(ServicoPagamento servicoPagamento) {
        this.servicoPagamento = servicoPagamento;
    }

    public boolean finalizarCompra(double valor) {
        return servicoPagamento.processar(valor);
    }
}
```

```java
package br.edu.testesistemas.pagamento;

// Stub que sempre simula um pagamento aprovado
public class ServicoPagamentoStubAprovado implements ServicoPagamento {
    @Override
    public boolean processar(double valor) {
        return true;
    }
}

// Stub que sempre simula um pagamento recusado
public class ServicoPagamentoStubRecusado implements ServicoPagamento {
    @Override
    public boolean processar(double valor) {
        return false;
    }
}
```

```java
package br.edu.testesistemas.pagamento;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckoutTest {

    @Test
    void finalizarCompraComPagamentoAprovadoDeveRetornarTrue() {
        // Arrange: usamos o dublê que sempre aprova
        Checkout checkout = new Checkout(new ServicoPagamentoStubAprovado());

        // Act
        boolean resultado = checkout.finalizarCompra(150.0);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void finalizarCompraComPagamentoRecusadoDeveRetornarFalse() {
        // Arrange: usamos o dublê que sempre recusa
        Checkout checkout = new Checkout(new ServicoPagamentoStubRecusado());

        // Act
        boolean resultado = checkout.finalizarCompra(150.0);

        // Assert
        assertFalse(resultado);
    }
}
```

> 💡 Note como testamos `Checkout` **sem depender de um serviço de pagamento real** — nenhuma cobrança de verdade acontece durante o teste, e conseguimos testar os dois cenários (aprovado/recusado) com controle total.

---

**Próxima aula:** vamos conhecer o **Mockito**, o framework que automatiza a criação de stubs e mocks, eliminando a necessidade de escrever classes dublês manualmente como fizemos hoje.
