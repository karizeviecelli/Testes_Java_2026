# Aula 09 — Mockito II: ArgumentCaptor e @InjectMocks

**Módulo:** 3 — Dublês de Teste e Mockito
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Capturar argumentos passados a um mock com `ArgumentCaptor`;
- Usar `@InjectMocks` para injetar mocks automaticamente em uma classe testada;
- Combinar múltiplos mocks em um mesmo teste;
- Programar diferentes retornos consecutivos com `thenReturn` encadeado.

---

## 🖼️ Retomando a analogia — vasculhando as evidências entregues ao ator

Na Aula 08, aprendemos a confirmar **que** um ator (mock) recebeu uma "fala" (foi chamado). Hoje vamos além: vamos **capturar exatamente o que foi dito a ele** — os argumentos exatos passados na chamada — para inspecioná-los em detalhe. E, em vez de montar o "elenco" manualmente, vamos deixar o Mockito **montar o palco inteiro** para nós com `@InjectMocks`.

---

## 📚 Conteúdo teórico

### 1. `@InjectMocks` — montando o palco automaticamente

```java
@ExtendWith(MockitoExtension.class)
class CadastroUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioMock; // dependência mockada

    @InjectMocks
    private CadastroUsuario cadastro; // Mockito injeta o mock automaticamente aqui!
}
```

Com `@InjectMocks`, o Mockito olha para o construtor de `CadastroUsuario`, percebe que ele precisa de um `RepositorioUsuario`, e injeta o `repositorioMock` automaticamente — sem precisarmos escrever `new CadastroUsuario(repositorioMock)` manualmente em cada teste.

### 2. `ArgumentCaptor` — capturando o que foi passado

```java
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@ExtendWith(MockitoExtension.class)
class CadastroUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioMock;

    @InjectMocks
    private CadastroUsuario cadastro;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @Test
    void cadastrarDeveSalvarUsuarioComDadosCorretos() {
        // Act
        cadastro.cadastrar(new Usuario("Ana", "ana@email.com"));

        // Assert: capturamos o objeto Usuario que foi passado para salvar()
        verify(repositorioMock).salvar(usuarioCaptor.capture());
        Usuario usuarioCapturado = usuarioCaptor.getValue();

        assertEquals("Ana", usuarioCapturado.getNome());
        assertEquals("ana@email.com", usuarioCapturado.getEmail());
    }
}
```

`ArgumentCaptor` é útil quando queremos verificar **detalhes internos** do objeto passado ao mock — não apenas que o método foi chamado, mas *com o quê exatamente*.

### 3. Múltiplos mocks trabalhando juntos

```java
@ExtendWith(MockitoExtension.class)
class ProcessadorPedidoTest {

    @Mock
    private ServicoEstoque estoqueMock;

    @Mock
    private ServicoPagamento pagamentoMock;

    @Mock
    private ServicoEmail emailMock;

    @InjectMocks
    private ProcessadorPedido processador; // usa os TRÊS mocks acima

    @Test
    void processarPedidoComSucessoDeveNotificarCliente() {
        when(estoqueMock.temDisponivel(anyString(), anyInt())).thenReturn(true);
        when(pagamentoMock.processar(anyDouble())).thenReturn(true);

        processador.processar(new Pedido("cliente@email.com"));

        verify(emailMock).enviar(eq("cliente@email.com"), anyString());
    }
}
```

`@InjectMocks` cuida de conectar automaticamente **todos** os mocks declarados na classe ao objeto testado.

### 4. Retornos consecutivos com `thenReturn` encadeado

```java
when(servicoMock.consultar())
    .thenReturn("primeira resposta")
    .thenReturn("segunda resposta");

servicoMock.consultar(); // "primeira resposta"
servicoMock.consultar(); // "segunda resposta"
servicoMock.consultar(); // continua retornando "segunda resposta" (o último valor definido)
```

Isso é útil para simular sistemas que se comportam de forma diferente em chamadas sucessivas (ex.: simular uma falha temporária seguida de sucesso).

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 70 minutos

### Passo a passo

1. Reescreva `CadastroUsuarioTest` (das Aulas 07/08) usando `@InjectMocks` no lugar de `new CadastroUsuario(repositorioMock)`;
2. Adicione um `@Captor` para capturar o `Usuario` passado a `salvar()`, e verifique que o nome e e-mail capturados são exatamente os esperados;
3. Crie a classe `ServicoConsulta`, com um método `consultarSaldo()` que depende de um mock `ServicoBancoExterno`;
4. Programe o mock para retornar, em chamadas consecutivas, primeiro uma exceção simulada de indisponibilidade e depois um valor válido — simulando uma "segunda tentativa" bem-sucedida.

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
package br.edu.testesistemas.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioMock;

    @InjectMocks
    private CadastroUsuario cadastro; // Mockito injeta repositorioMock automaticamente

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @Test
    void cadastrarDeveSalvarUsuarioComDadosCorretos() {
        when(repositorioMock.buscarPorEmail(anyString())).thenReturn(null);

        cadastro.cadastrar(new Usuario("Ana", "ana@email.com"));

        verify(repositorioMock).salvar(usuarioCaptor.capture());
        Usuario capturado = usuarioCaptor.getValue();

        assertEquals("Ana", capturado.getNome());
        assertEquals("ana@email.com", capturado.getEmail());
    }
}
```

```java
package br.edu.testesistemas.banco;

public class ServicoConsulta {
    private ServicoBancoExterno bancoExterno;

    public ServicoConsulta(ServicoBancoExterno bancoExterno) {
        this.bancoExterno = bancoExterno;
    }

    public double consultarSaldoComRetentativa() {
        try {
            return bancoExterno.consultarSaldo();
        } catch (RuntimeException e) {
            // Em caso de falha, tenta novamente uma vez
            return bancoExterno.consultarSaldo();
        }
    }
}
```

```java
package br.edu.testesistemas.banco;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoConsultaTest {

    @Mock
    private ServicoBancoExterno bancoExternoMock;

    @InjectMocks
    private ServicoConsulta servicoConsulta;

    @Test
    void consultarSaldoDeveTentarNovamenteAposFalhaTemporaria() {
        // Primeira chamada: lança exceção. Segunda chamada: retorna valor válido
        when(bancoExternoMock.consultarSaldo())
            .thenThrow(new RuntimeException("Serviço indisponível"))
            .thenReturn(1500.0);

        double resultado = servicoConsulta.consultarSaldoComRetentativa();

        assertEquals(1500.0, resultado);
    }
}
```

---

**Próxima aula:** vamos aprender a medir **cobertura de código** com JaCoCo, entendendo o que os números realmente significam (e o que eles NÃO garantem).
