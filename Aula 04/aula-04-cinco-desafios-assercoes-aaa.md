# Aula 04 — Cinco desafios: asserções e padrão AAA

**Módulo:** 2 — JUnit 5 na Prática  
**Conteúdos:** padrão AAA, nomes de teste, `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`, `assertThrows` e `assertAll`

---

## Orientações gerais

Em todos os desafios:

1. crie a classe de produção em `src/main/java`;
2. crie a classe de teste correspondente em `src/test/java`;
3. organize cada teste usando Arrange–Act–Assert;
4. dê aos testes nomes que descrevam o cenário e o resultado esperado;
5. compare valores `double` usando delta, por exemplo `0.001`;
6. quando houver exceção, verifique também sua mensagem;
7. execute todos os testes antes da entrega.

> Os modelos mostram apenas a estrutura. As regras, valores e asserções devem ser completados pelos estudantes.

---

# Nível 1 — Básico

## Desafio 1 — Estados de uma `Lampada`

### Objetivo

Praticar `assertEquals`, `assertTrue`, `assertFalse` e o padrão AAA.

### Regras de negócio

Crie a classe `Lampada` com:

- atributo `comodo` do tipo `String`;
- atributo `ligada` do tipo `boolean`;
- atributo `intensidade` do tipo `int`;
- toda lâmpada deve começar desligada e com intensidade zero;
- `ligar()` deve deixar a lâmpada ligada e com intensidade 100;
- `desligar()` deve deixar a lâmpada desligada e com intensidade zero;
- crie os getters necessários.

### Testes obrigatórios

1. uma lâmpada recém-criada deve estar desligada;
2. uma lâmpada recém-criada deve ter intensidade zero;
3. `ligar()` deve alterar o estado para ligado;
4. `ligar()` deve alterar a intensidade para 100;
5. `desligar()` deve restaurar o estado e a intensidade iniciais.

### Modelo de organização

```java
@Test
void ligarDeveAlterarEstadoDaLampada() {
    // Arrange: crie uma lâmpada.

    // Act: ligue a lâmpada.

    // Assert: verifique o estado e a intensidade.
}
```

### Perguntas de análise

1. Qual asserção é mais clara para verificar o atributo `ligada`?
2. Por que `assertTrue(lampada.isLigada())` comunica melhor a intenção do que `assertEquals(true, lampada.isLigada())`?

---

## Desafio 2 — Estoque de um `Produto`

### Objetivo

Praticar cálculos com `double`, condições booleanas e validações no construtor.

### Regras de negócio

Crie a classe `Produto` com:

- `nome` do tipo `String`;
- `preco` do tipo `double`;
- `quantidadeEstoque` do tipo `int`;
- o preço deve ser maior que zero;
- a quantidade inicial não pode ser negativa;
- preço inválido lança `IllegalArgumentException` com a mensagem `O preço deve ser maior que zero.`;
- estoque inválido lança `IllegalArgumentException` com a mensagem `O estoque não pode ser negativo.`;
- `calcularValorEmEstoque()` retorna preço multiplicado pela quantidade;
- `temEstoque()` retorna `true` quando a quantidade for maior que zero.

### Testes obrigatórios

1. calcular corretamente o valor total em estoque;
2. retornar `true` quando houver produtos no estoque;
3. retornar `false` quando o estoque for zero;
4. rejeitar preço igual a zero;
5. rejeitar preço negativo;
6. rejeitar quantidade inicial negativa;
7. verificar as mensagens das exceções.

### Modelo de organização

```java
@Test
void calcularValorEmEstoqueDeveMultiplicarPrecoPelaQuantidade() {
    // Arrange
    Produto produto = new Produto(/* complete */);

    // Act
    double obtido = produto.calcularValorEmEstoque();

    // Assert
    assertEquals(/* esperado */, obtido, 0.001);
}
```

### Pergunta de análise

Por que a comparação de valores `double` deve utilizar delta?

---

# Nível 2 — Intermediário

## Desafio 3 — Dados opcionais de um `Usuario`

### Objetivo

Praticar `assertNull`, `assertNotNull`, `assertAll` e mudanças de estado.

### Regras de negócio

Crie a classe `Usuario` com:

- `nome` do tipo `String`;
- `email` do tipo `String`;
- `telefone` do tipo `String`;
- `ativo` do tipo `boolean`;
- o telefone deve começar como `null`;
- o usuário deve começar ativo;
- `definirTelefone(String telefone)` deve armazenar um telefone válido;
- telefone nulo ou em branco lança `IllegalArgumentException` com a mensagem `O telefone é obrigatório.`;
- `desativar()` deve alterar o atributo `ativo` para `false`;
- crie os getters necessários.

### Testes obrigatórios

1. usuário recém-criado deve ter telefone nulo;
2. usuário recém-criado deve estar ativo;
3. depois de `definirTelefone()`, o telefone não deve ser nulo;
4. o telefone obtido deve ser igual ao informado;
5. telefone nulo deve lançar exceção;
6. telefone em branco deve lançar exceção;
7. `desativar()` deve alterar o estado para inativo;
8. use `assertAll` para conferir nome, e-mail, telefone inicial e estado inicial.

### Modelo de organização

```java
@Test
void usuarioRecemCriadoDeveTerEstadoInicialCorreto() {
    // Arrange + Act
    Usuario usuario = new Usuario(/* complete */);

    // Assert: todas as verificações devem ser executadas.
    assertAll(
            () -> assertEquals(/* esperado */, usuario.getNome()),
            () -> assertEquals(/* esperado */, usuario.getEmail()),
            () -> assertNull(usuario.getTelefone()),
            () -> assertTrue(usuario.isAtivo())
    );
}
```

### Perguntas de análise

1. Qual é a diferença entre `null` e `""`?
2. O que muda no relatório quando usamos `assertAll`?

---

## Desafio 4 — Operações de uma `ContaDigital`

### Objetivo

Praticar `assertThrows`, captura da exceção, verificação da mensagem e testes de operações financeiras.

### Regras de negócio

Crie a classe `ContaDigital` com:

- `titular` do tipo `String`;
- `saldo` do tipo `double`, inicialmente zero;
- `depositar(double valor)`;
- `sacar(double valor)`;
- depósitos devem ser maiores que zero;
- depósito inválido lança `IllegalArgumentException` com a mensagem `O depósito deve ser maior que zero.`;
- saques devem ser maiores que zero;
- saque inválido lança `IllegalArgumentException` com a mensagem `O saque deve ser maior que zero.`;
- um saque não pode ultrapassar o saldo;
- saldo insuficiente lança `IllegalStateException` com a mensagem `Saldo insuficiente.`.

### Testes obrigatórios

1. o saldo inicial deve ser zero;
2. um depósito deve aumentar o saldo;
3. um saque válido deve reduzir o saldo;
4. depósito zero deve lançar exceção;
5. depósito negativo deve lançar exceção;
6. saque zero deve lançar exceção;
7. saque negativo deve lançar exceção;
8. saque maior que o saldo deve lançar `IllegalStateException`;
9. todas as mensagens devem ser verificadas;
10. uma operação rejeitada não deve alterar o saldo.

### Modelo de organização

```java
@Test
void saqueMaiorQueSaldoDeveLancarExcecao() {
    // Arrange: crie a conta e faça um depósito.

    // Act + Assert: execute o saque dentro de assertThrows.
    IllegalStateException excecao = assertThrows(
            IllegalStateException.class,
            () -> conta.sacar(/* valor */)
    );

    // Assert: verifique a mensagem e confirme que o saldo não mudou.
}
```

### Pergunta de análise

Por que saldo insuficiente foi representado por `IllegalStateException`, enquanto valores zero ou negativos usam `IllegalArgumentException`?

---

# Nível 3 — Avançado

## Desafio 5 — Fluxo completo de uma `ReservaHotel`

### Objetivo

Integrar todas as asserções estudadas em um cenário com validações, cálculo, valores nulos, mudança de estado e múltiplas exceções.

### Regras de negócio

Crie a classe `ReservaHotel` com:

- `hospede` do tipo `String`;
- `quantidadeDiarias` do tipo `int`;
- `valorDiaria` do tipo `double`;
- `confirmada` do tipo `boolean`;
- `codigoConfirmacao` do tipo `String`;
- uma nova reserva começa não confirmada e sem código;
- o nome do hóspede não pode ser nulo ou em branco;
- a quantidade de diárias deve ser maior que zero;
- o valor da diária deve ser maior que zero;
- `calcularTotal()` retorna quantidade de diárias multiplicada pelo valor da diária;
- `confirmar(String codigo)` confirma a reserva e armazena o código;
- o código não pode ser nulo ou em branco;
- uma reserva já confirmada não pode ser confirmada novamente.

### Mensagens das exceções

| Situação | Tipo | Mensagem |
|---|---|---|
| Hóspede ausente | `IllegalArgumentException` | `O hóspede é obrigatório.` |
| Diárias inválidas | `IllegalArgumentException` | `A quantidade de diárias deve ser maior que zero.` |
| Valor inválido | `IllegalArgumentException` | `O valor da diária deve ser maior que zero.` |
| Código ausente | `IllegalArgumentException` | `O código de confirmação é obrigatório.` |
| Segunda confirmação | `IllegalStateException` | `A reserva já está confirmada.` |

### Testes obrigatórios

1. usar `assertAll` para verificar o estado inicial completo;
2. usar `assertNull` para o código inicial;
3. calcular corretamente o total da reserva;
4. confirmar a reserva com um código válido;
5. usar `assertNotNull` depois da confirmação;
6. usar `assertTrue` para o estado confirmado;
7. testar hóspede nulo e em branco;
8. testar quantidade de diárias zero e negativa;
9. testar valor da diária zero e negativo;
10. testar código nulo e em branco;
11. testar uma segunda confirmação;
12. verificar todas as mensagens das exceções.

### Modelo de organização

```java
@Test
void confirmarDeveAlterarEstadoEArmazenarCodigo() {
    // Arrange
    ReservaHotel reserva = new ReservaHotel(/* complete */);

    // Act
    reserva.confirmar(/* código */);

    // Assert
    assertAll(
            () -> assertTrue(reserva.isConfirmada()),
            () -> assertNotNull(reserva.getCodigoConfirmacao()),
            () -> assertEquals(
                    /* código esperado */,
                    reserva.getCodigoConfirmacao()
            )
    );
}
```

### Perguntas de análise

1. Por que o código começa como `null` e não como String vazia?
2. Por que confirmar duas vezes representa um problema de estado?
3. O cálculo do total pertence ao Arrange, Act ou Assert?

---

## Critérios de avaliação

| Critério | Pontos |
|---|---:|
| Classes de produção respeitam as regras | 2,0 |
| Testes organizados em AAA | 1,5 |
| Asserções adequadas para cada situação | 2,0 |
| Exceções e mensagens verificadas | 1,5 |
| Nomes de testes claros e descritivos | 1,0 |
| Cobertura dos cenários obrigatórios | 1,5 |
| Código organizado e suíte integralmente verde | 0,5 |
| **Total** | **10,0** |

---

## Checklist de entrega

- [ ] Cada classe de produção está em seu arquivo correto?
- [ ] Cada classe possui uma classe de teste correspondente?
- [ ] Os testes apresentam Arrange, Act e Assert?
- [ ] Valores `double` foram comparados com delta?
- [ ] Exceções tiveram tipo e mensagem verificados?
- [ ] `assertAll` foi usado quando havia várias características relacionadas?
- [ ] Todos os testes estão verdes?
