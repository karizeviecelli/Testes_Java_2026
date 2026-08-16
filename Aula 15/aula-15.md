# Aula 15 — Projeto Integrador: Entrega e Fechamento

**Módulo:** 5 — Testando Aplicações Spring
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Conectar as três camadas do projeto integrador (Controller → Service → Repository) com um `@ExceptionHandler` funcional;
- Rodar a suíte de testes completa da aplicação de Tarefas, com relatório de cobertura;
- Revisar, de ponta a ponta, todos os conceitos estudados ao longo da UC;
- Entregar o projeto integrador final.

---

## 🖼️ Fechando o Tribunal do Código

Chegamos ao fim da nossa jornada pelo **Tribunal do Código**. Ao longo destas 15 aulas, aprendemos a construir bons "réus" (classes bem projetadas em POO), a conduzir audiências justas e organizadas (JUnit 5), a contratar atores quando necessário (test doubles e Mockito), a medir quantos processos já julgamos (cobertura), a escrever a acusação antes do crime (TDD), e finalmente a testar um tribunal inteiro, com todos os seus departamentos trabalhando em conjunto (Spring: Controller, Service, Repository). Hoje fechamos esse ciclo.

---

## 📚 Revisão geral da UC

| Aula | Tema | Conceito-chave |
|---|---|---|
| 01 | Fundamentos + POO | Tipos de teste, pirâmide de testes, encapsulamento |
| 02 | Ambiente | IntelliJ, Maven, pom.xml, dependência JUnit 5 |
| 03 | Primeiros testes | @Test, @BeforeEach, @AfterEach |
| 04 | Asserções + AAA | assertEquals, assertThrows, assertAll, Arrange-Act-Assert |
| 05 | Testes parametrizados | @ValueSource, @CsvSource, @MethodSource, @Timeout |
| 06 | Organização + POO | @Nested, @DisplayName, @Tag, herança/polimorfismo |
| 07 | Test doubles | Dummy, Stub, Fake, Spy, Mock |
| 08 | Mockito I | @Mock, when/thenReturn, verify |
| 09 | Mockito II | ArgumentCaptor, @InjectMocks |
| 10 | Cobertura de código | JaCoCo, cobertura de linha vs. branch |
| 11 | TDD | Red-Green-Refactor |
| 12 | Spring + Service | Injeção de dependência, testes de Service com Mockito |
| 13 | Spring + Repository | @DataJpaTest, H2, Spring Data JPA |
| 14 | Spring + Controller | MockMvc, @WebMvcTest, @MockBean, jsonPath |
| 15 | Fechamento | Integração completa, entrega do projeto |

---

## 📚 Conteúdo teórico — fechando as pontas do projeto integrador

### 1. Conectando exceções a status HTTP com `@ExceptionHandler`

Na Aula 14, testamos que uma busca por ID inexistente deveria retornar `404`, mas deixamos como pendência o "encanamento" que faz isso funcionar de verdade. Hoje fechamos essa ponta:

```java
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // aplica-se a TODOS os Controllers da aplicação
public class TratadorDeExcecoes {

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public String tratarTarefaNaoEncontrada(TarefaNaoEncontradaException e) {
        return e.getMessage();
    }
}
```

Com isso, sempre que um `@Service` lançar `TarefaNaoEncontradaException`, o Spring converte automaticamente essa exceção em uma resposta HTTP `404`, com a mensagem no corpo — exatamente o comportamento que já havíamos testado no `TarefaControllerTest`.

### 2. Visão de conjunto: uma requisição de ponta a ponta

```
Cliente HTTP
     │
     ▼
@RestController  ──►  delega para o Service
     │
     ▼
@Service         ──►  aplica regras de negócio, chama o Repository
     │
     ▼
@Repository      ──►  acessa o banco de dados (H2 em testes, real em produção)
```

Cada camada tem sua **própria estratégia de teste**, e isso é intencional:

| Camada | Estratégia de teste | Por quê |
|---|---|---|
| Controller | `@WebMvcTest` + `@MockBean` | Testa a "porta de entrada" HTTP, isolando o Service |
| Service | `@ExtendWith(MockitoExtension.class)` | Testa a regra de negócio, isolando o Repository |
| Repository | `@DataJpaTest` + H2 | Testa a persistência real, isolando Controller/Service |

### 3. Rodando a suíte completa e o relatório de cobertura

Ao rodar `mvn test` no projeto completo, todas as classes de teste — `TarefaTest` (se você criou testes da entidade), `TarefaServiceTest`, `TarefaRepositoryTest` e `TarefaControllerTest` — executam juntas, e o JaCoCo (Aula 10) gera um relatório consolidado de cobertura de todo o projeto.

### 4. Checklist final de um bom projeto de testes

Antes de entregar qualquer projeto, vale revisar:

- [ ] Cada classe de entidade tem uma classe de teste correspondente;
- [ ] Nomes de teste são descritivos (Aula 04);
- [ ] Testes cobrem tanto o caminho de sucesso quanto os de erro/exceção;
- [ ] Dependências externas são isoladas com dublês apropriados (Aula 07-09);
- [ ] A camada correta de teste é usada para cada camada da aplicação (Aula 12-14);
- [ ] A suíte inteira roda em segundos, não minutos (testes lentos são um sinal de alerta);
- [ ] Cobertura de código foi consultada como ferramenta de diagnóstico, não como meta cega (Aula 10).

---

<a id="atividade"></a>
## 💻 Atividade Prática — Entrega Final do Projeto Integrador

**Duração sugerida:** 4 horas (aula inteira)

### Passo a passo

1. Adicione o `TratadorDeExcecoes` (`@RestControllerAdvice`) ao projeto de Tarefas, conectando `TarefaNaoEncontradaException` ao status `404`;
2. Rode a suíte de testes completa (`Tarefa`, `TarefaService`, `TarefaRepository`, `TarefaController`) e confirme que tudo passa;
3. Gere o relatório de cobertura com JaCoCo e revise se alguma classe ficou com cobertura baixa — complete o que faltar;
4. Adicione **uma nova funcionalidade completa**, de ponta a ponta, seguindo o padrão das camadas: por exemplo, um endpoint `PUT /tarefas/{id}/concluir`, que marca uma tarefa como concluída. Implemente e teste as três camadas (Repository, se necessário; Service; Controller);
5. Revise o checklist final de qualidade acima antes de considerar o projeto pronto para entrega.

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito — nova funcionalidade completa (concluir tarefa)

```java
// TarefaService — novo método
public Tarefa concluir(Long id) {
    Tarefa tarefa = buscarPorId(id); // reaproveita o método já existente
    tarefa.setConcluida(true);
    return tarefaRepository.save(tarefa);
}
```

```java
// TarefaController — novo endpoint
@PutMapping("/{id}/concluir")
public Tarefa concluir(@PathVariable Long id) {
    return tarefaService.concluir(id);
}
```

```java
// TarefaServiceTest — novo teste (Mockito puro, sem Spring)
@Test
void concluirDeveMarcarTarefaComoConcluida() {
    Tarefa tarefa = new Tarefa("Estudar Spring");
    when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
    when(tarefaRepository.save(any())).thenReturn(tarefa);

    Tarefa resultado = tarefaService.concluir(1L);

    assertTrue(resultado.isConcluida());
}
```

```java
// TarefaControllerTest — novo teste (@WebMvcTest + @MockBean)
@Test
void concluirDeveRetornar200ComTarefaAtualizada() throws Exception {
    Tarefa tarefaConcluida = new Tarefa("Estudar Spring");
    tarefaConcluida.setConcluida(true);
    when(tarefaService.concluir(1L)).thenReturn(tarefaConcluida);

    mockMvc.perform(put("/tarefas/1/concluir"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.concluida").value(true));
}
```

> 💡 Note como a nova funcionalidade seguiu exatamente o mesmo padrão de camadas e de testes que usamos desde a Aula 12 — esse é o poder de uma arquitetura bem testada: adicionar funcionalidades novas se torna previsível e seguro.

---

## 🎓 Encerramento da UC

Parabéns por concluir a UC de Teste de Sistemas! Você percorreu o caminho completo: da revisão de POO e dos fundamentos de teste, passando pelo domínio do JUnit 5 e do Mockito, até testar uma aplicação Spring real, camada por camada. O hábito de testar — e de testar bem — é uma das habilidades mais valorizadas no mercado de desenvolvimento, e a base que você construiu aqui vai acompanhá-lo(a) em qualquer linguagem ou framework que usar daqui para frente.

**O tribunal está encerrado. Todos os processos foram julgados.**
