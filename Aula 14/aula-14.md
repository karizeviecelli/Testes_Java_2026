# Aula 14 — Testes de Controller com MockMvc

**Módulo:** 5 — Testando Aplicações Spring
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Entender o papel do `@RestController` na arquitetura Spring;
- Testar endpoints REST usando `MockMvc`, sem subir um servidor HTTP real;
- Verificar status codes, corpo da resposta (JSON) e cabeçalhos;
- Continuar o **projeto integrador**, testando a API de Tarefas de ponta a ponta (Controller → Service → Repository).

---

## 🖼️ Retomando a analogia — a portaria do tribunal

Se o Repository é o cartório e o Service contém as regras, o **Controller** é a **portaria do tribunal**: é por onde as pessoas (as requisições HTTP) entram, apresentam seus documentos (o corpo da requisição) e recebem uma resposta (o status HTTP e o corpo de retorno). Hoje vamos testar essa portaria sem precisar realmente abrir as portas do prédio — usando `MockMvc`, que simula requisições HTTP sem subir um servidor de verdade.

---

## 📚 Conteúdo teórico

### 1. O `@RestController`

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public Tarefa criar(@RequestBody Tarefa tarefa) {
        return tarefaService.criar(tarefa);
    }

    @GetMapping("/{id}")
    public Tarefa buscarPorId(@PathVariable Long id) {
        return tarefaService.buscarPorId(id);
    }

    @GetMapping
    public List<Tarefa> listarTodas() {
        return tarefaService.listarTodas();
    }
}
```

O Controller **não contém regras de negócio** — ele apenas recebe a requisição HTTP e delega para o Service, retornando a resposta.

### 2. `MockMvc` — simulando requisições HTTP

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefaController.class) // sobe só a camada web, para este Controller específico
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // cria um mock do Service e o registra no contexto do Spring
    private TarefaService tarefaService;

    @Test
    void buscarPorIdDeveRetornar200ComTarefaEncontrada() throws Exception {
        when(tarefaService.buscarPorId(1L)).thenReturn(new Tarefa("Estudar Spring"));

        mockMvc.perform(get("/tarefas/1"))
            .andExpect(status().isOk())                          // verifica o status HTTP
            .andExpect(jsonPath("$.titulo").value("Estudar Spring")); // verifica o corpo JSON
    }
}
```

`@WebMvcTest` é outra anotação **especializada**: sobe apenas a infraestrutura web (Controllers, filtros, serialização JSON), sem conectar ao banco de dados real — a camada de Service é substituída por um `@MockBean`.

### 3. `@MockBean` vs. `@Mock`

| Anotação | Contexto |
|---|---|
| `@Mock` (Mockito puro) | Usada com `@ExtendWith(MockitoExtension.class)`, sem envolver o Spring |
| `@MockBean` | Usada dentro do contexto do Spring (`@WebMvcTest`, `@SpringBootTest`): cria o mock **e o registra no container do Spring**, substituindo o bean real |

### 4. Testando diferentes cenários HTTP

```java
@Test
void criarTarefaComTituloVazioDeveRetornar400() throws Exception {
    String jsonInvalido = "{\"titulo\": \"\"}";

    mockMvc.perform(post("/tarefas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonInvalido))
        .andExpect(status().isBadRequest());
}

@Test
void buscarTarefaInexistenteDeveRetornar404() throws Exception {
    when(tarefaService.buscarPorId(99L))
        .thenThrow(new TarefaNaoEncontradaException());

    mockMvc.perform(get("/tarefas/99"))
        .andExpect(status().isNotFound());
}
```

### 5. Verificando o corpo da resposta com `jsonPath`

```java
mockMvc.perform(get("/tarefas"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$[0].titulo").value("Estudar Spring"))
    .andExpect(jsonPath("$[0].concluida").value(false))
    .andExpect(jsonPath("$.length()").value(1));
```

`jsonPath("$...")` navega pela estrutura JSON da resposta, permitindo verificar valores específicos, tamanhos de listas, e a presença/ausência de campos.

---

<a id="atividade"></a>
## 💻 Atividade Prática — Continuação do Projeto Integrador

**Duração sugerida:** 80 minutos

Vamos completar a API REST de Tarefas, testando o `TarefaController`.

### Passo a passo

1. Crie `TarefaService` (`@Service`), com os métodos `criar(Tarefa)`, `buscarPorId(Long)` (lançando exceção customizada se não encontrar) e `listarTodas()`, delegando ao `TarefaRepository` da Aula 13;
2. Crie `TarefaController` (`@RestController`), com os endpoints `POST /tarefas`, `GET /tarefas/{id}` e `GET /tarefas`;
3. Escreva `TarefaControllerTest` usando `@WebMvcTest` e `@MockBean`;
4. Teste: criação com sucesso (`201` ou `200`), busca por ID existente (`200` + corpo correto), busca por ID inexistente (`404`), listagem retornando múltiplas tarefas.

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
package br.edu.testesistemas.tarefa;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public Tarefa criar(Tarefa tarefa) {
        return tarefaRepository.save(tarefa);
    }

    public Tarefa buscarPorId(Long id) {
        return tarefaRepository.findById(id)
            .orElseThrow(TarefaNaoEncontradaException::new);
    }

    public List<Tarefa> listarTodas() {
        return tarefaRepository.findAll();
    }
}
```

```java
package br.edu.testesistemas.tarefa;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException() {
        super("Tarefa não encontrada.");
    }
}
```

```java
package br.edu.testesistemas.tarefa;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public Tarefa criar(@RequestBody Tarefa tarefa) {
        return tarefaService.criar(tarefa);
    }

    @GetMapping("/{id}")
    public Tarefa buscarPorId(@PathVariable Long id) {
        return tarefaService.buscarPorId(id);
    }

    @GetMapping
    public List<Tarefa> listarTodas() {
        return tarefaService.listarTodas();
    }
}
```

```java
package br.edu.testesistemas.tarefa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefaController.class)
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // converte objetos Java em JSON

    @MockBean
    private TarefaService tarefaService;

    @Test
    void criarTarefaDeveRetornarTarefaCriada() throws Exception {
        Tarefa nova = new Tarefa("Estudar Spring");
        when(tarefaService.criar(any())).thenReturn(nova);

        mockMvc.perform(post("/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nova)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo").value("Estudar Spring"));
    }

    @Test
    void buscarPorIdExistenteDeveRetornar200() throws Exception {
        when(tarefaService.buscarPorId(1L)).thenReturn(new Tarefa("Estudar Spring"));

        mockMvc.perform(get("/tarefas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo").value("Estudar Spring"));
    }

    @Test
    void buscarPorIdInexistenteDeveRetornar404() throws Exception {
        when(tarefaService.buscarPorId(99L)).thenThrow(new TarefaNaoEncontradaException());

        mockMvc.perform(get("/tarefas/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listarTodasDeveRetornarListaCompleta() throws Exception {
        when(tarefaService.listarTodas())
            .thenReturn(List.of(new Tarefa("Tarefa 1"), new Tarefa("Tarefa 2")));

        mockMvc.perform(get("/tarefas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }
}
```

> 💡 Nota: para o teste de `404` funcionar como esperado, normalmente configuramos um `@ExceptionHandler` que mapeia `TarefaNaoEncontradaException` para o status HTTP correto — um detalhe de implementação que pode ser explorado como extensão do projeto integrador.

---

**Próxima aula:** vamos finalizar o **projeto integrador**, unindo Controller + Service + Repository em uma suíte de testes completa, e fazer uma revisão geral de toda a UC.
