# Aula 07 — Testes de Integração

**Unidade curricular:** Teste de Sistemas  
**Carga horária:** 4 horas  
**Tema central:** diferenciar testes unitários e de integração, testar colaboração entre componentes e diagnosticar falhas de integração

---

## 1. Objetivos de aprendizagem

Ao final da aula, o estudante deverá ser capaz de:

- diferenciar teste unitário de teste de integração;
- identificar componentes que colaboram em um cenário;
- estruturar testes com Arrange, Act e Assert;
- escrever testes de integração com JUnit 5;
- interpretar falhas que envolvem mais de uma classe;
- usar testes menores para apoiar o diagnóstico;
- reconhecer o problema das dependências externas.

## 2. Organização sugerida das 4 horas

| Etapa | Tempo | Estratégia |
|---|---:|---|
| Retomada da Aula 6 | 15 min | Organização da suíte e leitura das evidências |
| Exposição dialogada | 55 min | Unitário × integração e pirâmide de testes |
| Demonstração ao vivo | 35 min | `Produto → Pedido → CalculadoraDesconto` |
| Intervalo | 10 min | — |
| Prática guiada | 80 min | Implementação em sete etapas |
| Desafio autônomo | 35 min | `Livro → Carrinho → CalculadoraDesconto` |
| Socialização e feedback | 10 min | Rubrica e ponte para mocks |

---

## 3. Questão-problema

Se uma classe funciona corretamente sozinha, podemos afirmar que o sistema inteiro funciona?

Não. Aplicações reais são compostas por partes que colaboram.

**Analogia:** um músico pode tocar perfeitamente sozinho. Isso não garante que a banda inteira esteja sincronizada.

- teste unitário: testa o músico;
- teste de integração: testa a banda.

## 4. Unitário × integração

| Critério | Unitário | Integração |
|---|---|---|
| Foco | Uma pequena unidade | Colaboração entre componentes |
| Diagnóstico | Mais localizado | Pode exigir investigação |
| Exemplo | `Produto` valida preço | `Pedido` soma `Produto` |

## 5. Domínio da demonstração

```text
Produto
   ↓
Pedido
   ↓
CalculadoraDesconto
```

### Produto.java

```java
public class Produto {
    private String nome;
    private double preco;

    public Produto(String nome, double preco) {
        if (preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() { return nome; }
    public double getPreco() { return preco; }
}
```

### ProdutoTest.java

```java
@Test
void deveCriarProdutoComPrecoCorreto() {
    Produto produto = new Produto("Teclado", 200);
    assertEquals(200, produto.getPreco(), 0.001);
}
```

Esse teste é unitário porque observa uma pequena unidade.

## 6. Pedido.java

```java
public class Pedido {
    private List<Produto> produtos = new ArrayList<>();

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    public double calcularTotal() {
        double total = 0;

        for (Produto produto : produtos) {
            total += produto.getPreco();
        }

        return total;
    }
}
```

## 7. Primeiro teste de integração

```java
@Test
void deveCalcularTotalDosProdutos() {
    Produto teclado = new Produto("Teclado", 200);
    Produto mouse = new Produto("Mouse", 100);
    Pedido pedido = new Pedido();

    pedido.adicionarProduto(teclado);
    pedido.adicionarProduto(mouse);

    assertEquals(300, pedido.calcularTotal(), 0.001);
}
```

Agora o resultado depende da colaboração entre `Produto` e `Pedido`.

## 8. Arrange, Act e Assert

- **Arrange:** preparar objetos e dados.
- **Act:** executar o comportamento.
- **Assert:** verificar a expectativa.

## 9. CalculadoraDesconto

```java
public class CalculadoraDesconto {
    public double aplicar(double valor, double percentual) {
        return valor - (valor * percentual / 100);
    }
}
```

Em `Pedido`:

```java
public double calcularTotalComDesconto(double percentual) {
    double total = calcularTotal();
    CalculadoraDesconto calculadora = new CalculadoraDesconto();

    return calculadora.aplicar(total, percentual);
}
```

## 10. Fluxo integrado

```java
@Test
void deveCalcularPedidoComDesconto() {
    Produto teclado = new Produto("Teclado", 200);
    Produto mouse = new Produto("Mouse", 100);
    Pedido pedido = new Pedido();

    pedido.adicionarProduto(teclado);
    pedido.adicionarProduto(mouse);

    double resultado = pedido.calcularTotalComDesconto(10);

    assertEquals(270, resultado, 0.001);
}
```

## 11. Investigação de falha

Troque temporariamente:

```java
total += produto.getPreco();
```

por:

```java
total -= produto.getPreco();
```

Execute os testes e registre:

1. qual teste falhou;
2. resultado esperado;
3. resultado obtido;
4. qual teste menor ajuda a localizar a causa.

## 12. Pirâmide de testes

```text
             /\
            /E2E\
           /----\
          /Integração\
         /----------\
        / Unitários  \
       /______________\
```

Os tipos se complementam. Testes de integração não substituem testes unitários.

## 13. Prática guiada

1. Criar `Produto` com validação.
2. Criar `Pedido`.
3. Testar um produto no pedido.
4. Testar vários produtos.
5. Criar e testar `CalculadoraDesconto`.
6. Integrar desconto ao pedido.
7. Provocar e diagnosticar uma falha.

## 14. Desafio autônomo

Construa:

```text
Livro
  ↓
Carrinho
  ↓
CalculadoraDesconto
```

Regras:

- adicionar livros;
- calcular total;
- aplicar desconto;
- rejeitar desconto negativo;
- rejeitar desconto superior a 50%;
- criar testes unitários e de integração;
- verificar R$ 150 com 10% de desconto = R$ 135.

## 15. Ponte para a Aula 08

```text
Pedido
   ↓
ServicoPagamento
   ↓
API externa
```

Como testar `Pedido` sem realizar um pagamento verdadeiro?

**Próxima aula:** dependências, dublês de teste, mocks e Mockito.
