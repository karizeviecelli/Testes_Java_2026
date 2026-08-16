# Aula 02 — Ambiente de Desenvolvimento e o arquivo pom.xml

**Módulo:** 1 — Fundamentos
**Carga horária:** 4 horas
**Professor(a):** [Nome/@handle da turma]

---

## 🎯 Objetivos da aula

Ao final desta aula, você será capaz de:

- Instalar e configurar o IntelliJ IDEA para desenvolvimento Java;
- Criar um projeto Maven do zero, entendendo sua estrutura de pastas;
- Explicar o que é o arquivo `pom.xml` e para que ele serve;
- Ler e explicar, linha a linha, a dependência do JUnit 5 no `pom.xml`;
- Migrar as classes de entidade criadas na Aula 01 para dentro de um projeto Maven configurado corretamente.

---

## 🖼️ Retomando a analogia — preparando o cartório do tribunal

Todo tribunal precisa de um **cartório**: o lugar onde ficam registrados os regimentos, os documentos e as ferramentas que tornam os julgamentos possíveis. Hoje, o IntelliJ é o nosso **prédio do tribunal**, o Maven é o **sistema de registros** e o `pom.xml` é o **livro de regimento interno** — nele declaramos exatamente quais "instrumentos" (dependências) o tribunal vai usar, sendo o JUnit 5 o principal deles.

---

## 📚 Conteúdo teórico

### 1. IntelliJ IDEA

O IntelliJ IDEA é uma IDE (Ambiente de Desenvolvimento Integrado) muito usada no mercado para desenvolvimento Java. Vamos usar a versão **Community** (gratuita), que já é suficiente para tudo que faremos nesta UC.

Principais recursos que vamos usar ao longo do curso:
- Criação e gerenciamento de projetos Maven;
- Execução de testes JUnit diretamente pela IDE (o famoso "play verde" ao lado de cada método de teste);
- Depurador (debugger) para investigar o comportamento do código passo a passo;
- Autocompletar e sugestões, que aceleram bastante a escrita de testes.

### 2. Maven e gerenciamento de dependências

**Maven** é uma ferramenta de **automação de build** e **gerenciamento de dependências** para projetos Java. Em vez de baixar manualmente um `.jar` do JUnit e configurá-lo no projeto, simplesmente declaramos no `pom.xml` **quais bibliotecas queremos usar**, e o Maven se encarrega de baixá-las e disponibilizá-las no projeto.

### 3. Estrutura de um projeto Maven

```
meu-projeto/
├── pom.xml                     ← o "regimento" do projeto
├── src/
│   ├── main/
│   │   └── java/                ← código de produção (nossas entidades)
│   └── test/
│       └── java/                ← código de teste (nossas classes de teste)
└── target/                      ← gerado automaticamente pelo Maven (build)
```

> 💡 Note que existem **duas pastas separadas**: `main` para o código "real" do sistema, e `test` para os testes. Essa separação é uma convenção do Maven e será fundamental a partir da Aula 03.

### 4. O arquivo `pom.xml`, linha a linha

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">

    <!-- Versão do modelo do POM (Project Object Model). Praticamente sempre 4.0.0 -->
    <modelVersion>4.0.0</modelVersion>

    <!-- Coordenadas GAV: identificam o projeto de forma única -->
    <groupId>br.edu.testesistemas</groupId>     <!-- "quem" mantém o projeto -->
    <artifactId>teste-sistemas-turma</artifactId> <!-- nome do projeto -->
    <version>1.0-SNAPSHOT</version>               <!-- versão atual (em desenvolvimento) -->

    <properties>
        <!-- Versão do Java usada para compilar o projeto -->
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <!-- Garante que arquivos com acentuação (como este!) sejam lidos corretamente -->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <!-- junit-jupiter é a dependência "guarda-chuva" do JUnit 5:
                 traz a API de testes, o motor de execução (engine) e o suporte
                 a testes parametrizados, tudo de uma vez -->
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <!-- scope "test": esta dependência só existe durante a compilação
                 e execução dos testes — ela NÃO vai para o artefato final
                 (o .jar) que roda em produção -->
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <!-- Plugin responsável por EXECUTAR os testes quando rodamos "mvn test" -->
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>

</project>
```

**Por que cada parte importa:**

| Elemento | Função |
|---|---|
| `groupId` / `artifactId` / `version` | Identificam unicamente o projeto (coordenadas GAV) |
| `properties` | Configurações gerais, como a versão do Java e o encoding dos arquivos |
| `dependencies` | Lista das bibliotecas externas que o projeto usa — aqui, o `junit-jupiter` |
| `scope test` | Diz ao Maven que essa dependência só é necessária para **testar**, não para rodar o sistema em produção |
| `maven-surefire-plugin` | Ferramenta que efetivamente **executa** os testes JUnit ao rodar `mvn test` |

### 5. Verificando que o ambiente está pronto

Depois de criar o projeto e colar o `pom.xml`, o próximo passo é sempre confirmar que tudo compila **antes** de escrever qualquer teste. Vamos mover as classes de entidade da Aula 01 (`Produto`, `ContaBancaria`) para `src/main/java` e confirmar que o projeto compila sem erros — é o nosso "check-in" no tribunal antes da primeira audiência, que começa na Aula 03.

---

<a id="atividade"></a>
## 💻 Atividade Prática

**Duração sugerida:** 60 minutos
**Formato:** individual

### Passo a passo

1. Instale o IntelliJ IDEA Community (caso ainda não tenha) e abra a IDE;
2. Crie um novo projeto Maven, escolhendo Java 17 (ou a versão disponível na sua máquina);
3. Substitua o conteúdo do `pom.xml` gerado automaticamente pelo `pom.xml` explicado nesta aula, ajustando `groupId`/`artifactId` se desejar;
4. Crie a pasta de pacote `br.edu.testesistemas.entidades` dentro de `src/main/java` e mova para lá as classes `Produto` e `ContaBancaria` da Aula 01;
5. Rode `mvn compile` (ou use o botão de build da IDE) e confirme que não há erros;
6. Responda: qual é a diferença entre colocar uma dependência com `scope test` e sem nenhum `scope` declarado?

[Ver Gabarito »](#gabarito)

---

<a id="gabarito"></a>
## ✅ Gabarito

O `pom.xml` final deve ser idêntico (ou muito próximo) ao apresentado na seção teórica desta aula, com `groupId`/`artifactId` personalizados pelo aluno, se optarem por isso.

**Resposta esperada para a pergunta de fixação:**

> Quando declaramos `<scope>test</scope>`, a dependência (no caso, o JUnit 5) fica disponível **apenas** durante a compilação e execução dos testes — ela nunca é incluída no artefato final (`.jar`) que vai rodar em produção. Sem nenhum `scope` declarado, o padrão é `compile`, o que significa que a dependência estaria disponível tanto para o código de produção quanto para os testes, e **seria empacotada junto com a aplicação final** — o que não faz sentido para uma biblioteca de testes.

---

**Próxima aula:** com o ambiente pronto, é hora da primeira audiência de verdade — vamos escrever nossos primeiros testes com JUnit 5 e conhecer o ciclo de vida de um teste (`@BeforeEach`, `@Test`, `@AfterEach`).
