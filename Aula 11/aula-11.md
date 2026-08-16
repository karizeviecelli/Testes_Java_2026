# Aula 11 — TDD: Test-Driven Development

**Módulo:** 4 — Qualidade e Boas Práticas
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Entender o ciclo **Red-Green-Refactor** do TDD;
- Praticar a escrita do teste **antes** do código de produção;
- Perceber como o TDD influencia o design das classes (interfaces mais simples e coesas);
- Construir uma pequena funcionalidade do zero, seguindo o ciclo rigorosamente.

---

## 🖼️ Retomando a analogia — escrevendo a acusação antes do crime

Até agora, sempre escrevemos o código primeiro e o teste depois. O TDD **inverte essa ordem**: escrevemos a "acusação" (o teste) **antes** de o código sequer existir. Isso obriga a pensar, desde o início, exatamente o que esperamos que aquele trecho de código faça — antes de nos preocuparmos em como implementá-lo.

---

## 📚 Conteúdo teórico

### 1. O ciclo Red-Green-Refactor

```
   ┌─────────┐      ┌──────────┐      ┌────────────┐
   │   RED   │ ───> │  GREEN   │ ───> │  REFACTOR  │ ───┐
   │ (falha) │      │ (passa)  │      │ (melhora)  │    │
   └─────────┘      └──────────┘      └────────────┘    │
        ▲                                                │
        └────────────────────────────────────────────────┘
```

1. **Red**: escreva um teste para uma funcionalidade que ainda não existe. Ele vai falhar (compilação quebrada ou asserção falha) — e está tudo bem, esse é o objetivo;
2. **Green**: escreva o **mínimo de código possível** para fazer aquele teste passar — nada além disso;
3. **Refactor**: com o teste passando como "rede de segurança", melhore o código (nomes, duplicação, clareza) sem alterar o comportamento;
4. Repita o ciclo para a próxima pequena funcionalidade.

### 2. Exemplo completo: uma calculadora de troco

**Passo 1 — RED:** escrevemos o teste antes de a classe `Caixa` existir.

```java
@Test
void calcularTrocoDeveRetornarDiferencaCorreta() {
    Caixa caixa = new Caixa();
    assertEquals(5.0, caixa.calcularTroco(20.0, 15.0));
}
```

Neste ponto, o código nem compila — `Caixa` ainda não existe. Isso é o "Red".

**Passo 2 — GREEN:** escrevemos o mínimo de código para o teste passar.

```java
public class Caixa {
    public double calcularTroco(double valorPago, double valorTotal) {
        return valorPago - valorTotal;
    }
}
```

Rodamos o teste: passa! Estamos no "Green". Note que não nos preocupamos ainda com validações, casos de erro, etc. — só o mínimo para o teste passar.

**Passo 3 — REFACTOR:** o código já está simples, então não há muito a melhorar agora. Seguimos para o próximo ciclo.

**Novo ciclo — RED:** e se `valorPago` for menor que `valorTotal`?

```java
@Test
void calcularTrocoComValorPagoMenorDeveLancarExcecao() {
    Caixa caixa = new Caixa();
    assertThrows(IllegalArgumentException.class, () -> caixa.calcularTroco(10.0, 15.0));
}
```

**GREEN:** ajustamos o código para fazer esse novo teste passar, sem quebrar o anterior:

```java
public double calcularTroco(double valorPago, double valorTotal) {
    if (valorPago < valorTotal) {
        throw new IllegalArgumentException("Valor pago insuficiente.");
    }
    return valorPago - valorTotal;
}
```

**REFACTOR:** poderíamos extrair a validação para um método próprio, se a classe crescer. Por ora, está claro o suficiente.

### 3. Por que o TDD ajuda no design

- Escrever o teste primeiro nos **força a pensar na interface pública** da classe antes da implementação — como ela vai ser usada?
- Tende a produzir métodos menores e mais focados, porque só escrevemos código para fazer um teste específico passar;
- O "medo de refatorar" desaparece: com uma suíte de testes já escrita, mudar a implementação interna é seguro.

### 4. TDD não é "escrever todos os testes primeiro"

Um erro comum é achar que TDD significa "escrever toda a suíte de testes de uma vez, depois todo o código". Na prática, o ciclo é **bem pequeno e iterativo**: um teste, um pedaço mínimo de implementação, um teste, um pedaço mínimo, e assim por diante — em ciclos de poucos minutos cada.

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 80 minutos

### Passo a passo

Pratique o TDD construindo a classe `ValidadorSenha`, que verifica se uma senha é "forte" (pelo menos 8 caracteres, ao menos 1 número). Siga **rigorosamente** o ciclo, na ordem:

1. **RED**: escreva um teste para `senhaForte("abcdefg1")` esperando `true` (a classe `ValidadorSenha` ainda nem existe);
2. **GREEN**: crie a classe com o mínimo de código para esse teste passar (pode até ser um `return true;` fixo, propositalmente ingênuo);
3. **RED**: escreva um segundo teste para `senhaForte("abc")` esperando `false` (uma senha curta demais) — que vai falhar com a implementação ingênua atual;
4. **GREEN**: ajuste a implementação para validar o tamanho mínimo, fazendo os dois testes passarem;
5. **RED**: escreva um terceiro teste para `senhaForte("abcdefgh")` esperando `false` (8 caracteres, mas sem nenhum número);
6. **GREEN**: ajuste novamente a implementação para checar a presença de ao menos um dígito;
7. **REFACTOR**: revise o código final, melhorando nomes e clareza, mantendo todos os testes passando.

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

**Evolução completa do ciclo TDD:**

```java
// Ciclo 1 — RED: o teste é escrito primeiro
@Test
void senhaComOitoCaracteresENumeroDeveSerForte() {
    assertTrue(ValidadorSenha.senhaForte("abcdefg1"));
}

// Ciclo 1 — GREEN: implementação mínima (propositalmente ingênua)
public class ValidadorSenha {
    public static boolean senhaForte(String senha) {
        return true; // "hack" mínimo só para o primeiro teste passar
    }
}
```

```java
// Ciclo 2 — RED: um novo teste expõe a fragilidade da implementação ingênua
@Test
void senhaCurtaDeveSerFraca() {
    assertFalse(ValidadorSenha.senhaForte("abc"));
}

// Ciclo 2 — GREEN: implementação evolui para validar o tamanho
public static boolean senhaForte(String senha) {
    return senha.length() >= 8;
}
```

```java
// Ciclo 3 — RED: mais um teste, cobrindo a exigência de número
@Test
void senhaSemNumeroDeveSerFraca() {
    assertFalse(ValidadorSenha.senhaForte("abcdefgh"));
}

// Ciclo 3 — GREEN: implementação final, cobrindo as duas regras
public static boolean senhaForte(String senha) {
    if (senha.length() < 8) {
        return false;
    }
    return senha.chars().anyMatch(Character::isDigit);
}
```

```java
// REFACTOR final: código já está claro, mas poderíamos extrair
// os dois critérios em métodos privados nomeados, se a classe crescer:
public class ValidadorSenha {

    public static boolean senhaForte(String senha) {
        return temTamanhoMinimo(senha) && temPeloMenosUmNumero(senha);
    }

    private static boolean temTamanhoMinimo(String senha) {
        return senha.length() >= 8;
    }

    private static boolean temPeloMenosUmNumero(String senha) {
        return senha.chars().anyMatch(Character::isDigit);
    }
}
```

> 💡 Note como a implementação **evoluiu junto com os testes**, um pequeno passo de cada vez — nunca escrevemos "toda a lógica de uma vez", e a cada passo tínhamos uma rede de segurança (os testes anteriores) garantindo que não quebramos nada.

---

**Próxima aula:** vamos dar um grande passo — conhecer o **Spring/Spring Boot** e aprender a testar a camada de **Service**, aplicando tudo o que já sabemos sobre Mockito em um contexto de injeção de dependência real.
