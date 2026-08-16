# Plano de Ensino

## Unidade Curricular: Teste de Sistemas
### Curso Técnico em Desenvolvimento de Sistemas

*Direitos Reservados Karize Viecelli — @karizeviecelli*

---
link para página: 


*https://karizeviecelli.github.io/Testes_Java_2026/*
---

## 1. Identificação

| Campo | Descrição |
|---|---|
| **Unidade Curricular (UC)** | Teste de Sistemas |
| **Curso** | Técnico em Desenvolvimento de Sistemas |
| **Carga horária total** | 60 horas |
| **Número de aulas** | 15 aulas |
| **Duração de cada aula** | 4 horas |
| **Ferramentas/Tecnologias** | IntelliJ IDEA, Java, Maven, JUnit 5, Mockito, Spring Boot |
| **Pré-requisitos** | Lógica de Programação, Programação Orientada a Objetos (POO) — *revisão reforçada, pois a turma ainda apresenta dificuldades no tema* |

---

## 2. Ementa

Fundamentos de teste de software; tipos e níveis de teste; ambiente de desenvolvimento e ferramentas de teste (IntelliJ IDEA, Maven, JUnit 5); revisão de POO aplicada a testes; escrita de testes unitários; asserções e ciclo de vida de testes; testes parametrizados e testes de exceção; test doubles (mocks, stubs, fakes) com Mockito; cobertura de código; desenvolvimento orientado a testes (TDD); testes em aplicações Spring/Spring Boot (camadas de serviço, repositório e controller); boas práticas de teste; projeto integrador prático.

---

## 3. Objetivos Gerais

Capacitar o(a) estudante a compreender a importância dos testes de software no ciclo de desenvolvimento, aplicando conceitos de Programação Orientada a Objetos na construção de testes unitários e de integração com JUnit 5, utilizando a IDE IntelliJ, e a testar aplicações construídas com o framework Spring.

## 4. Objetivos Específicos

Ao final da UC, o(a) estudante será capaz de:

- Compreender os fundamentos, tipos e níveis de teste de software;
- Configurar um projeto Maven no IntelliJ IDEA, entendendo o papel do arquivo `pom.xml` e das dependências de teste;
- Revisar e aplicar corretamente conceitos de POO (classes, atributos, métodos, encapsulamento, construtores) na criação de classes de entidade;
- Criar e organizar testes unitários com JUnit 5, utilizando anotações e asserções corretamente;
- Escrever testes parametrizados e testes de exceção;
- Utilizar test doubles (mocks/stubs) com o Mockito;
- Interpretar relatórios de cobertura de código;
- Aplicar o ciclo de TDD (Red-Green-Refactor) em pequenos problemas;
- Testar camadas de uma aplicação Spring Boot (Service, Repository e Controller);
- Desenvolver, de forma autônoma, um pequeno projeto aplicando os testes aprendidos.

---

## 5. Metodologia

- Aulas expositivo-dialogadas com apoio de slides e exemplos ao vivo;
- Demonstrações práticas com código comentado, sempre seguidas de prática guiada;
- Criação conjunta de **classes de entidade** (POO) e das respectivas **classes de teste**, reforçando a relação entre modelagem de objetos e verificação de comportamento;
- Exercícios individuais/em dupla com apresentação de gabarito comentado;
- Retomada constante de conceitos de POO ao longo das aulas, sempre que um novo conceito de teste depender deles;
- Projeto integrador nas aulas finais, consolidando os conteúdos da UC.

## 6. Recursos Didáticos

- Computador com JDK instalado, IntelliJ IDEA (Community Edition), acesso à internet;
- Repositório de exemplos de código comentado (classes de entidade + classes de teste);
- Arquivo `pom.xml` de referência com as dependências do JUnit 5 (e, nas aulas finais, do Spring Boot Test), sempre acompanhado de explicação linha a linha;
- Slides de apoio e roteiros de prática por aula;
- Banco de dados em memória H2 (para as aulas de teste de repositório com Spring).

## 7. Avaliação

| Instrumento | Peso | Descrição |
|---|---|---|
| Exercícios práticos por aula | 40% | Atividades de fixação entregues ao final de cada aula |
| Avaliação intermediária (Aula 8) | 20% | Testes unitários e uso de Mockito |
| Projeto integrador (Aulas 13–15) | 40% | Aplicação Spring com suíte de testes completa (unitários + integração) |

Critério de aprovação: nota final ≥ 6,0 (escala 0–10) e frequência mínima de 75%.

---

## 8. Conteúdo Programático (Aula a Aula)

| Aula | Carga h. | Tema | Conteúdo | Observações |
|---|---|---|---|---|
| 1 | 4h | Fundamentos de Teste de Software + Revisão de POO | O que é testar software, por que testar, tipos de teste (unitário, integração, sistema, aceitação), pirâmide de testes. Revisão de POO: classes, atributos, métodos, encapsulamento, construtores | Diagnóstico do nível de POO da turma |
| 2 | 4h | Ambiente de Desenvolvimento | Instalação/configuração do IntelliJ IDEA, criação de projeto Maven, estrutura de pastas, **arquivo `pom.xml`**: o que é e para que serve, dependência do JUnit 5 explicada linha a linha | Entrega do `pom.xml` comentado |
| 3 | 4h | Primeiros Testes com JUnit 5 | Anotações `@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`; ciclo de vida de um teste; primeira classe de entidade + primeira classe de teste | Código 100% comentado |
| 4 | 4h | Asserções | `assertEquals`, `assertTrue/False`, `assertNull/NotNull`, `assertAll`, `assertThrows`; padrão AAA (Arrange-Act-Assert); boas práticas de nomenclatura de testes | Nova entidade + testes |
| 5 | 4h | Testes Parametrizados | `@ParameterizedTest`, `@ValueSource`, `@CsvSource`, `@MethodSource`; testes de exceção e timeout | |
| 6 | 4h | Organização e Suítes de Teste | `@DisplayName`, `@Nested`, `@Tag`, organização de pacotes de teste; revisão de herança e polimorfismo aplicada às entidades testadas | Reforço de POO |
| 7 | 4h | Test Doubles | Conceitos de dublês de teste: dummy, stub, fake, spy, mock; quando e por que usar | |
| 8 | 4h | Mockito I + Avaliação Intermediária | Introdução ao Mockito, `@Mock`, `when/thenReturn`, `verify`; aplicação prática | **Avaliação intermediária** |
| 9 | 4h | Mockito II | Mocks avançados, `ArgumentCaptor`, `@InjectMocks`, integração com JUnit 5 | |
| 10 | 4h | Cobertura de Código | Ferramenta de cobertura (JaCoCo), leitura de relatórios, metas de cobertura, limites e armadilhas de "100% de cobertura" | |
| 11 | 4h | TDD (Test Driven Development) | Ciclo Red-Green-Refactor; prática de TDD construindo uma pequena funcionalidade do zero | |
| 12 | 4h | Introdução ao Spring + Testes de Serviço | Visão geral do Spring/Spring Boot e injeção de dependência (ligação com POO); `@SpringBootTest`; testes de camada de Service | |
| 13 | 4h | Testes de Repositório (Spring Data) | `@DataJpaTest`, banco H2 em memória, testes de persistência | **Início do projeto integrador** |
| 14 | 4h | Testes de Controller (API REST) | `MockMvc`, testes de endpoints REST, status codes, corpo de resposta | Continuação do projeto integrador |
| 15 | 4h | Projeto Integrador — Entrega e Fechamento | Finalização e apresentação do projeto (aplicação Spring com testes unitários, de repositório e de controller); revisão geral da UC | **Entrega final** |

---

## 9. Bibliografia

**Básica**
- JUNIT 5 User Guide. Disponível em: https://junit.org/junit5/docs/current/user-guide/
- SPRING Framework Documentation. Disponível em: https://docs.spring.io/spring-framework/reference/
- PRESSMAN, Roger S.; MAXIM, Bruce R. *Engenharia de Software: uma abordagem profissional*. 8. ed. Porto Alegre: AMGH, 2016.

**Complementar**
- BECK, Kent. *Test Driven Development: By Example*. Addison-Wesley, 2002.
- FOWLER, Martin. *Refactoring: Improving the Design of Existing Code*. 2. ed. Addison-Wesley, 2018.
- Documentação oficial do Mockito. Disponível em: https://site.mockito.org/

---
Direitos Reservados Karize Viecelli — @karizeviecelli
