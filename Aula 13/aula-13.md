# Aula 13 — Testes de Repositório com @DataJpaTest e H2

**Módulo:** 5 — Testando Aplicações Spring
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

- Entender o papel do Spring Data JPA na camada de Repository;
- Configurar um banco de dados H2 em memória para testes;
- Testar um `Repository` real com `@DataJpaTest`;
- Iniciar o **projeto integrador**, que será finalizado nas Aulas 14 e 15.

---

## 🖼️ Retomando a analogia — o cartório de registros do tribunal

Se o Service contém as regras do tribunal, o **Repository** é o **cartório**: onde os processos (nossos dados) são efetivamente arquivados e recuperados. Hoje vamos testar esse cartório de verdade — mas usando um "cartório temporário" (H2 em memória), que existe só durante o teste e desaparece logo depois, sem sujar nenhum banco de dados real.

---

## 📚 Conteúdo teórico

### 1. Spring Data JPA — Repository sem implementação manual

Com o Spring Data JPA, não escrevemos a implementação do Repository manualmente — basta **estender uma interface**:

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // JpaRepository já traz métodos prontos: save(), findById(), findAll(), delete()...

    // Métodos customizados: o Spring Data gera a implementação a partir do NOME do método
    Cliente findByEmail(String email);
}
```

`JpaRepository<Cliente, Long>` já traz gratuitamente métodos como `save()`, `findById()`, `findAll()` e `deleteById()`. E se o nome do método seguir a convenção do Spring Data (`findBy` + nome do atributo), o Spring **gera a implementação automaticamente**, sem escrevermos nenhuma consulta SQL.

### 2. A entidade JPA

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // diz ao JPA: "esta classe representa uma tabela no banco"
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // o banco gera o ID automaticamente
    private Long id;

    private String nome;
    private String email;

    // Construtor padrão exigido pelo JPA
    public Cliente() {}

    public Cliente(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
```

### 3. Configurando o H2 para testes

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

O H2 é um banco de dados **em memória**, criado do zero a cada execução dos testes e descartado ao final — perfeito para testar a camada de Repository sem depender de um banco real (Postgres, MySQL etc.) instalado na máquina.

### 4. Testando com `@DataJpaTest`

```java
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest // sobe SÓ a camada de persistência (JPA), com um H2 configurado automaticamente
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void salvarClienteDeveGerarIdAutomaticamente() {
        // Arrange + Act
        Cliente cliente = clienteRepository.save(new Cliente("Ana", "ana@email.com"));

        // Assert
        assertNotNull(cliente.getId());
    }

    @Test
    void findByEmailDeveEncontrarClienteCadastrado() {
        clienteRepository.save(new Cliente("Bruno", "bruno@email.com"));

        Cliente encontrado = clienteRepository.findByEmail("bruno@email.com");

        assertEquals("Bruno", encontrado.getNome());
    }
}
```

`@DataJpaTest` é uma anotação **especializada**: diferente de `@SpringBootTest`, ela sobe **apenas** a infraestrutura de persistência (repositórios, entidades, o banco H2), sem carregar Controllers, Services ou outras partes da aplicação — deixando o teste mais rápido e focado.

### 5. `@Autowired` — outra forma de injeção

`@Autowired` é a anotação do Spring que injeta uma dependência gerenciada pelo container — nos testes com `@DataJpaTest`, ela nos entrega uma instância real do `ClienteRepository`, já conectada ao banco H2 de teste.

---

<a id="atividade"></a>
## 💻 Atividade Prática — Início do Projeto Integrador

**Duração sugerida:** 80 minutos

Esta atividade dá início ao **projeto integrador**, que você vai continuar nas Aulas 14 e 15. A partir de agora, vamos construir uma pequena aplicação de gerenciamento de **Tarefas**.

### Passo a passo

1. Adicione a dependência do H2 ao `pom.xml`;
2. Crie a entidade `Tarefa` (`@Entity`), com `id`, `titulo` (String), `concluida` (boolean, iniciando `false`);
3. Crie a interface `TarefaRepository`, estendendo `JpaRepository<Tarefa, Long>`, com um método customizado `List<Tarefa> findByConcluida(boolean concluida)`;
4. Escreva `TarefaRepositoryTest` com `@DataJpaTest`, testando: salvar uma tarefa e confirmar que o ID foi gerado; salvar duas tarefas (uma concluída, outra não) e confirmar que `findByConcluida(false)` retorna só a pendente.

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

```java
package br.edu.testesistemas.tarefa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private boolean concluida;

    public Tarefa() {}

    public Tarefa(String titulo) {
        this.titulo = titulo;
        this.concluida = false;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
}
```

```java
package br.edu.testesistemas.tarefa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByConcluida(boolean concluida);
}
```

```java
package br.edu.testesistemas.tarefa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class TarefaRepositoryTest {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Test
    void salvarTarefaDeveGerarIdAutomaticamente() {
        Tarefa tarefa = tarefaRepository.save(new Tarefa("Estudar Spring"));

        assertNotNull(tarefa.getId());
    }

    @Test
    void findByConcluidaFalseDeveRetornarApenasPendentes() {
        Tarefa pendente = new Tarefa("Revisar PR");

        Tarefa concluida = new Tarefa("Configurar ambiente");
        concluida.setConcluida(true);

        tarefaRepository.save(pendente);
        tarefaRepository.save(concluida);

        List<Tarefa> pendentes = tarefaRepository.findByConcluida(false);

        assertEquals(1, pendentes.size());
        assertEquals("Revisar PR", pendentes.get(0).getTitulo());
    }
}
```

---

**Próxima aula:** vamos subir mais uma camada e testar o **Controller** — os endpoints REST da nossa aplicação de Tarefas — usando `MockMvc`, dando continuidade ao projeto integrador.
