# Gabarito — Aula 04: cinco desafios de asserções e padrão AAA

> As soluções abaixo são referências. Outras implementações podem estar corretas quando respeitam as mesmas regras e verificam os mesmos comportamentos.

---

# Desafio 1 — `Lampada`

## Classe de produção

```java
package br.edu.testesistemas.desafios;

public class Lampada {

    private final String comodo;
    private boolean ligada;
    private int intensidade;

    public Lampada(String comodo) {
        this.comodo = comodo;
        this.ligada = false;
        this.intensidade = 0;
    }

    public void ligar() {
        ligada = true;
        intensidade = 100;
    }

    public void desligar() {
        ligada = false;
        intensidade = 0;
    }

    public String getComodo() {
        return comodo;
    }

    public boolean isLigada() {
        return ligada;
    }

    public int getIntensidade() {
        return intensidade;
    }
}
```

## Classe de teste

```java
package br.edu.testesistemas.desafios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LampadaTest {

    @Test
    void lampadaRecemCriadaDeveEstarDesligadaEComIntensidadeZero() {
        // Arrange + Act
        Lampada lampada = new Lampada("Sala");

        // Assert
        assertAll(
                () -> assertFalse(lampada.isLigada()),
                () -> assertEquals(0, lampada.getIntensidade())
        );
    }

    @Test
    void ligarDeveAlterarEstadoEIntensidade() {
        // Arrange
        Lampada lampada = new Lampada("Quarto");

        // Act
        lampada.ligar();

        // Assert
        assertAll(
                () -> assertTrue(lampada.isLigada()),
                () -> assertEquals(100, lampada.getIntensidade())
        );
    }

    @Test
    void desligarDeveRestaurarEstadoInicial() {
        // Arrange
        Lampada lampada = new Lampada("Cozinha");
        lampada.ligar();

        // Act
        lampada.desligar();

        // Assert
        assertAll(
                () -> assertFalse(lampada.isLigada()),
                () -> assertEquals(0, lampada.getIntensidade())
        );
    }
}
```

### Respostas de análise

1. `assertTrue` para ligada e `assertFalse` para desligada.
2. As asserções booleanas expressam diretamente o comportamento esperado.

---

# Desafio 2 — `Produto`

## Classe de produção

```java
package br.edu.testesistemas.desafios;

public class Produto {

    private final String nome;
    private final double preco;
    private final int quantidadeEstoque;

    public Produto(
            String nome,
            double preco,
            int quantidadeEstoque) {

        if (preco <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior que zero."
            );
        }

        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException(
                    "O estoque não pode ser negativo."
            );
        }

        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public double calcularValorEmEstoque() {
        return preco * quantidadeEstoque;
    }

    public boolean temEstoque() {
        return quantidadeEstoque > 0;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
}
```

## Classe de teste

```java
package br.edu.testesistemas.desafios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProdutoTest {

    @Test
    void calcularValorEmEstoqueDeveMultiplicarPrecoPelaQuantidade() {
        // Arrange
        Produto produto = new Produto("Teclado", 150.0, 4);

        // Act
        double obtido = produto.calcularValorEmEstoque();

        // Assert
        assertEquals(600.0, obtido, 0.001);
    }

    @Test
    void temEstoqueDeveRetornarTrueQuandoQuantidadeForPositiva() {
        // Arrange
        Produto produto = new Produto("Mouse", 80.0, 2);

        // Act
        boolean obtido = produto.temEstoque();

        // Assert
        assertTrue(obtido);
    }

    @Test
    void temEstoqueDeveRetornarFalseQuandoQuantidadeForZero() {
        // Arrange
        Produto produto = new Produto("Monitor", 900.0, 0);

        // Act
        boolean obtido = produto.temEstoque();

        // Assert
        assertFalse(obtido);
    }

    @Test
    void precoZeroDeveLancarExcecao() {
        // Act + Assert
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new Produto("Cabo", 0.0, 5)
        );

        assertEquals(
                "O preço deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void precoNegativoDeveLancarExcecao() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new Produto("Cabo", -1.0, 5)
        );

        assertEquals(
                "O preço deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void estoqueNegativoDeveLancarExcecao() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new Produto("Cabo", 20.0, -1)
        );

        assertEquals(
                "O estoque não pode ser negativo.",
                excecao.getMessage()
        );
    }
}
```

### Resposta de análise

O delta estabelece a diferença máxima aceita entre o resultado esperado e o obtido, evitando falhas causadas por pequenas imprecisões da representação binária de `double`.

---

# Desafio 3 — `Usuario`

## Classe de produção

```java
package br.edu.testesistemas.desafios;

public class Usuario {

    private final String nome;
    private final String email;
    private String telefone;
    private boolean ativo;

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.telefone = null;
        this.ativo = true;
    }

    public void definirTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException(
                    "O telefone é obrigatório."
            );
        }

        this.telefone = telefone;
    }

    public void desativar() {
        ativo = false;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
```

## Classe de teste

```java
package br.edu.testesistemas.desafios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    void usuarioRecemCriadoDeveTerDadosEEstadoInicialCorretos() {
        // Arrange + Act
        Usuario usuario = new Usuario(
                "Ana",
                "ana@email.com"
        );

        // Assert
        assertAll(
                () -> assertEquals("Ana", usuario.getNome()),
                () -> assertEquals(
                        "ana@email.com",
                        usuario.getEmail()
                ),
                () -> assertNull(usuario.getTelefone()),
                () -> assertTrue(usuario.isAtivo())
        );
    }

    @Test
    void definirTelefoneDeveArmazenarValorInformado() {
        // Arrange
        Usuario usuario = new Usuario(
                "Bruno",
                "bruno@email.com"
        );

        // Act
        usuario.definirTelefone("(47) 99999-0000");

        // Assert
        assertAll(
                () -> assertNotNull(usuario.getTelefone()),
                () -> assertEquals(
                        "(47) 99999-0000",
                        usuario.getTelefone()
                )
        );
    }

    @Test
    void telefoneNuloDeveLancarExcecao() {
        Usuario usuario = new Usuario(
                "Carla",
                "carla@email.com"
        );

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> usuario.definirTelefone(null)
        );

        assertEquals(
                "O telefone é obrigatório.",
                excecao.getMessage()
        );
    }

    @Test
    void telefoneEmBrancoDeveLancarExcecao() {
        Usuario usuario = new Usuario(
                "Daniel",
                "daniel@email.com"
        );

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> usuario.definirTelefone("   ")
        );

        assertEquals(
                "O telefone é obrigatório.",
                excecao.getMessage()
        );
    }

    @Test
    void desativarDeveAlterarEstadoParaInativo() {
        // Arrange
        Usuario usuario = new Usuario(
                "Eva",
                "eva@email.com"
        );

        // Act
        usuario.desativar();

        // Assert
        assertFalse(usuario.isAtivo());
    }
}
```

### Respostas de análise

1. `null` representa ausência de objeto; `""` é um objeto `String` com zero caracteres.
2. `assertAll` executa todas as asserções agrupadas e apresenta todas as falhas, em vez de interromper na primeira.

---

# Desafio 4 — `ContaDigital`

## Classe de produção

```java
package br.edu.testesistemas.desafios;

public class ContaDigital {

    private final String titular;
    private double saldo;

    public ContaDigital(String titular) {
        this.titular = titular;
        this.saldo = 0.0;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "O depósito deve ser maior que zero."
            );
        }

        saldo += valor;
    }

    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "O saque deve ser maior que zero."
            );
        }

        if (valor > saldo) {
            throw new IllegalStateException(
                    "Saldo insuficiente."
            );
        }

        saldo -= valor;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }
}
```

## Classe de teste

```java
package br.edu.testesistemas.desafios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContaDigitalTest {

    @Test
    void contaRecemCriadaDeveTerSaldoZero() {
        ContaDigital conta = new ContaDigital("Maria");

        assertEquals(0.0, conta.getSaldo(), 0.001);
    }

    @Test
    void depositarDeveAumentarSaldo() {
        // Arrange
        ContaDigital conta = new ContaDigital("Maria");

        // Act
        conta.depositar(100.0);

        // Assert
        assertEquals(100.0, conta.getSaldo(), 0.001);
    }

    @Test
    void sacarDeveReduzirSaldo() {
        // Arrange
        ContaDigital conta = new ContaDigital("Maria");
        conta.depositar(100.0);

        // Act
        conta.sacar(40.0);

        // Assert
        assertEquals(60.0, conta.getSaldo(), 0.001);
    }

    @Test
    void depositoZeroDeveLancarExcecaoESaldoNaoDeveMudar() {
        ContaDigital conta = new ContaDigital("Maria");

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> conta.depositar(0.0)
        );

        assertAll(
                () -> assertEquals(
                        "O depósito deve ser maior que zero.",
                        excecao.getMessage()
                ),
                () -> assertEquals(
                        0.0,
                        conta.getSaldo(),
                        0.001
                )
        );
    }

    @Test
    void depositoNegativoDeveLancarExcecao() {
        ContaDigital conta = new ContaDigital("Maria");

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> conta.depositar(-10.0)
        );

        assertEquals(
                "O depósito deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void saqueZeroDeveLancarExcecao() {
        ContaDigital conta = new ContaDigital("Maria");
        conta.depositar(100.0);

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> conta.sacar(0.0)
        );

        assertEquals(
                "O saque deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void saqueNegativoDeveLancarExcecao() {
        ContaDigital conta = new ContaDigital("Maria");
        conta.depositar(100.0);

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> conta.sacar(-10.0)
        );

        assertEquals(
                "O saque deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void saqueMaiorQueSaldoDeveLancarExcecaoESaldoNaoDeveMudar() {
        ContaDigital conta = new ContaDigital("Maria");
        conta.depositar(50.0);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> conta.sacar(100.0)
        );

        assertAll(
                () -> assertEquals(
                        "Saldo insuficiente.",
                        excecao.getMessage()
                ),
                () -> assertEquals(
                        50.0,
                        conta.getSaldo(),
                        0.001
                )
        );
    }
}
```

### Resposta de análise

Um valor zero ou negativo é um argumento inválido para a operação. Já o saque maior que o saldo é incompatível com o estado atual da conta, embora o valor informado possa ser positivo.

---

# Desafio 5 — `ReservaHotel`

## Classe de produção

```java
package br.edu.testesistemas.desafios;

public class ReservaHotel {

    private final String hospede;
    private final int quantidadeDiarias;
    private final double valorDiaria;
    private boolean confirmada;
    private String codigoConfirmacao;

    public ReservaHotel(
            String hospede,
            int quantidadeDiarias,
            double valorDiaria) {

        if (hospede == null || hospede.isBlank()) {
            throw new IllegalArgumentException(
                    "O hóspede é obrigatório."
            );
        }

        if (quantidadeDiarias <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de diárias deve ser maior que zero."
            );
        }

        if (valorDiaria <= 0) {
            throw new IllegalArgumentException(
                    "O valor da diária deve ser maior que zero."
            );
        }

        this.hospede = hospede;
        this.quantidadeDiarias = quantidadeDiarias;
        this.valorDiaria = valorDiaria;
        this.confirmada = false;
        this.codigoConfirmacao = null;
    }

    public double calcularTotal() {
        return quantidadeDiarias * valorDiaria;
    }

    public void confirmar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "O código de confirmação é obrigatório."
            );
        }

        if (confirmada) {
            throw new IllegalStateException(
                    "A reserva já está confirmada."
            );
        }

        confirmada = true;
        codigoConfirmacao = codigo;
    }

    public String getHospede() {
        return hospede;
    }

    public int getQuantidadeDiarias() {
        return quantidadeDiarias;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public String getCodigoConfirmacao() {
        return codigoConfirmacao;
    }
}
```

## Classe de teste

```java
package br.edu.testesistemas.desafios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaHotelTest {

    @Test
    void reservaRecemCriadaDeveTerDadosEEstadoInicialCorretos() {
        ReservaHotel reserva = new ReservaHotel(
                "Ana Silva",
                3,
                250.0
        );

        assertAll(
                () -> assertEquals(
                        "Ana Silva",
                        reserva.getHospede()
                ),
                () -> assertEquals(
                        3,
                        reserva.getQuantidadeDiarias()
                ),
                () -> assertEquals(
                        250.0,
                        reserva.getValorDiaria(),
                        0.001
                ),
                () -> assertFalse(reserva.isConfirmada()),
                () -> assertNull(reserva.getCodigoConfirmacao())
        );
    }

    @Test
    void calcularTotalDeveMultiplicarDiariasPeloValor() {
        // Arrange
        ReservaHotel reserva = new ReservaHotel(
                "Bruno Costa",
                4,
                180.0
        );

        // Act
        double obtido = reserva.calcularTotal();

        // Assert
        assertEquals(720.0, obtido, 0.001);
    }

    @Test
    void confirmarDeveAlterarEstadoEArmazenarCodigo() {
        // Arrange
        ReservaHotel reserva = new ReservaHotel(
                "Carla Souza",
                2,
                300.0
        );

        // Act
        reserva.confirmar("RES-2026-001");

        // Assert
        assertAll(
                () -> assertTrue(reserva.isConfirmada()),
                () -> assertNotNull(
                        reserva.getCodigoConfirmacao()
                ),
                () -> assertEquals(
                        "RES-2026-001",
                        reserva.getCodigoConfirmacao()
                )
        );
    }

    @Test
    void hospedeNuloDeveLancarExcecao() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new ReservaHotel(null, 2, 200.0)
        );

        assertEquals(
                "O hóspede é obrigatório.",
                excecao.getMessage()
        );
    }

    @Test
    void hospedeEmBrancoDeveLancarExcecao() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new ReservaHotel("   ", 2, 200.0)
        );

        assertEquals(
                "O hóspede é obrigatório.",
                excecao.getMessage()
        );
    }

    @Test
    void quantidadeZeroDeveLancarExcecao() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new ReservaHotel("Diego", 0, 200.0)
        );

        assertEquals(
                "A quantidade de diárias deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void quantidadeNegativaDeveLancarExcecao() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new ReservaHotel("Diego", -1, 200.0)
        );

        assertEquals(
                "A quantidade de diárias deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void valorZeroDeveLancarExcecao() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new ReservaHotel("Eva", 2, 0.0)
        );

        assertEquals(
                "O valor da diária deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void valorNegativoDeveLancarExcecao() {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> new ReservaHotel("Eva", 2, -1.0)
        );

        assertEquals(
                "O valor da diária deve ser maior que zero.",
                excecao.getMessage()
        );
    }

    @Test
    void codigoNuloDeveLancarExcecao() {
        ReservaHotel reserva = new ReservaHotel(
                "Fábio",
                2,
                150.0
        );

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> reserva.confirmar(null)
        );

        assertEquals(
                "O código de confirmação é obrigatório.",
                excecao.getMessage()
        );
    }

    @Test
    void codigoEmBrancoDeveLancarExcecao() {
        ReservaHotel reserva = new ReservaHotel(
                "Fábio",
                2,
                150.0
        );

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> reserva.confirmar("   ")
        );

        assertEquals(
                "O código de confirmação é obrigatório.",
                excecao.getMessage()
        );
    }

    @Test
    void confirmarDuasVezesDeveLancarExcecao() {
        ReservaHotel reserva = new ReservaHotel(
                "Gabriela",
                5,
                220.0
        );
        reserva.confirmar("RES-001");

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> reserva.confirmar("RES-002")
        );

        assertAll(
                () -> assertEquals(
                        "A reserva já está confirmada.",
                        excecao.getMessage()
                ),
                () -> assertEquals(
                        "RES-001",
                        reserva.getCodigoConfirmacao()
                )
        );
    }
}
```

### Respostas de análise

1. `null` representa claramente que nenhum código foi atribuído à reserva.
2. Após a primeira confirmação, o estado da reserva já não permite repetir a operação.
3. A chamada de `calcularTotal()` é a ação; portanto, pertence ao Act. O valor retornado é conferido no Assert.

---

## Síntese das asserções utilizadas

| Asserção | Aplicação nos desafios |
|---|---|
| `assertEquals` | Valores, textos, saldos, totais e mensagens |
| `assertTrue` | Lâmpada ligada, estoque disponível, usuário ativo e reserva confirmada |
| `assertFalse` | Estados iniciais ou após desligar/desativar |
| `assertNull` | Telefone e código ainda não informados |
| `assertNotNull` | Telefone e código depois de atribuídos |
| `assertThrows` | Regras inválidas e operações proibidas |
| `assertAll` | Conferência de várias características relacionadas |
