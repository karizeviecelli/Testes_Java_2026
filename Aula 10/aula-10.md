# Aula 10 — Cobertura de Código com JaCoCo

**Módulo:** 4 — Qualidade e Boas Práticas
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Configurar o JaCoCo no `pom.xml` para medir cobertura de código;
- Gerar e interpretar um relatório de cobertura;
- Entender a diferença entre cobertura de linha e cobertura de branch (decisão);
- Compreender os limites da cobertura de código — o que ela garante e o que **não** garante.

---

## 🖼️ Retomando a analogia — quantos processos o tribunal já julgou?

Cobertura de código responde a uma pergunta simples: **quanto do nosso código foi executado pelos testes?** É como perguntar "quantos processos o tribunal já julgou, de um total de casos existentes?". Mas atenção: um processo "julgado" não significa necessariamente "julgado corretamente" — e é exatamente essa a armadilha da cobertura de código que vamos discutir hoje.

---

## 📚 Conteúdo teórico

### 1. Configurando o JaCoCo no `pom.xml`

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.12</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal> <!-- instrumenta o código antes dos testes -->
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal> <!-- gera o relatório após os testes -->
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Depois de rodar `mvn test`, o relatório fica disponível em `target/site/jacoco/index.html` — um relatório HTML navegável, classe por classe.

### 2. Cobertura de linha vs. cobertura de branch (decisão)

```java
public String classificarIdade(int idade) {
    if (idade < 18) {
        return "menor de idade";
    } else {
        return "maior de idade";
    }
}
```

- **Cobertura de linha**: mede se cada linha do código foi executada ao menos uma vez;
- **Cobertura de branch**: mede se cada **caminho de decisão** (`if`/`else`, cada ramo de um `switch`) foi executado.

Um único teste com `idade = 20` cobre 100% das *linhas* deste método... mas apenas 50% dos *branches* (só passou pelo caminho `else`, nunca pelo `if`).

```java
// Para 100% de cobertura de branch, precisamos de pelo menos 2 testes:
@Test
void idadeMenorQue18DeveRetornarMenorDeIdade() {
    assertEquals("menor de idade", classificarIdade(15));
}

@Test
void idadeMaiorOuIgualA18DeveRetornarMaiorDeIdade() {
    assertEquals("maior de idade", classificarIdade(20));
}
```

### 3. Lendo o relatório do JaCoCo

O relatório mostra, para cada classe/pacote:
- **Instructions**: % de instruções de bytecode executadas;
- **Branches**: % de caminhos de decisão executados;
- Cores: verde (coberto), vermelho (não coberto), amarelo (parcialmente coberto — ex.: só um branch do `if` foi testado).

### 4. O que cobertura de código NÃO garante

Este é o ponto mais importante da aula: **cobertura alta não significa ausência de bugs**.

```java
public int dividir(int a, int b) {
    return a / b; // e se b for 0?
}

@Test
void dividirDeveRetornarResultadoCorreto() {
    assertEquals(5, dividir(10, 2)); // 100% de cobertura de linha... mas nunca testamos b=0!
}
```

Esse teste cobre 100% das linhas do método `dividir`, mas **não testa o caso `b = 0`**, que lançaria uma `ArithmeticException` em produção. Cobertura mede **execução**, não **qualidade das verificações**. É perfeitamente possível ter 100% de cobertura com testes fracos, sem asserções significativas, ou sem cobrir casos de borda importantes.

### 5. Metas de cobertura: um guia, não um objetivo cego

- Times costumam definir metas como "mínimo de 80% de cobertura" — um bom indicador de que **partes importantes** não estão completamente esquecidas;
- Perseguir 100% de cobertura a qualquer custo pode levar a testes artificiais, escritos só para "pintar a linha de verde", sem valor real;
- Cobertura é uma **ferramenta de diagnóstico** ("o que eu esqueci de testar?"), não um **certificado de qualidade**.

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 70 minutos

### Passo a passo

1. Adicione o plugin do JaCoCo ao `pom.xml` do seu projeto;
2. Rode `mvn test` e abra o relatório gerado em `target/site/jacoco/index.html`;
3. Escolha uma classe do seu projeto com cobertura abaixo de 100% e escreva os testes que faltam para cobri-la completamente (linha E branch);
4. Escreva, propositalmente, um método com um `if/else` mal coberto (só um teste, cobrindo só um dos branches) e depois complete a cobertura com um segundo teste;
5. Reflita e escreva, em 3-4 frases: por que 100% de cobertura em uma classe simples como `ContaBancaria` não significa que ela está livre de bugs?

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

**Exemplo de branch mal coberto → depois corrigido:**

```java
public class Classificador {
    public String classificarNota(double nota) {
        if (nota >= 7.0) {
            return "Aprovado";
        } else {
            return "Reprovado";
        }
    }
}
```

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassificadorTest {

    // Sozinho, este teste cobre 100% das LINHAS, mas só 50% dos BRANCHES
    @Test
    void notaAltaDeveSerAprovado() {
        assertEquals("Aprovado", new Classificador().classificarNota(8.0));
    }

    // Este segundo teste completa a cobertura de branch, testando o outro caminho
    @Test
    void notaBaixaDeveSerReprovado() {
        assertEquals("Reprovado", new Classificador().classificarNota(5.0));
    }
}
```

**Resposta esperada para a reflexão:**

> 100% de cobertura em `ContaBancaria` significa apenas que toda linha e todo caminho de decisão do código foi **executado** por algum teste. Isso não garante que as **asserções estão corretas**, nem que **todos os cenários de negócio relevantes** (valores-limite, entradas inesperadas, combinações de operações) foram pensados. Um teste mal escrito, com uma asserção fraca ou ausente, pode "passar" pela linha de código sem realmente verificar se o comportamento está correto — daí a importância de aliar cobertura a um bom raciocínio de casos de teste, e não usá-la como única métrica de qualidade.

---

**Próxima aula:** vamos experimentar o **TDD (Test-Driven Development)** na prática: escrever o teste **antes** do código, seguindo o ciclo Red-Green-Refactor.
