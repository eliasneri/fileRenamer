# 📁 FileRenamer

Projeto Java para renomear arquivos baseado em registros de banco de dados, seguindo o padrão:

```
id_namePerson_nomeOriginal.extensão
```

---

# 📦 Tecnologias Utilizadas no Projeto

## ☕ Linguagem & Plataforma
- **Java 21**  
  Definido nas propriedades do projeto e utilizado pelo plugin `maven-compiler-plugin`.

## 🛠️ Gerenciador de Build
- **Apache Maven**  
  Utilizado para gerenciar dependências, build e plugins.

---

## 🗄️ Persistência de Dados
| Tecnologia         | Descrição                                           |
|--------------------|-----------------------------------------------------|
| **PostgreSQL JDBC** | Driver JDBC para conexão com banco PostgreSQL (`org.postgresql:postgresql:42.7.3`) |
| **Spring JDBC**     | Abstração de JDBC para acesso a dados com Spring (`org.springframework:spring-jdbc:5.3.34`) |

---

## 🧪 Testes
| Tecnologia         | Descrição                                           |
|--------------------|-----------------------------------------------------|
| **JUnit 5**         | Framework de testes unitários (`junit-jupiter-api`, `junit-jupiter-engine`) |
| **Mockito**         | Criação de mocks em testes (`mockito-core`, `mockito-junit-jupiter`) |
| **Maven Surefire**  | Plugin Maven para execução de testes (`maven-surefire-plugin:3.2.5`) |

---

## 🧰 Ferramentas de Desenvolvimento
| Tecnologia      | Descrição                                                   |
|------------------|-------------------------------------------------------------|
| **Lombok**        | Geração de código automático como getters/setters (`lombok:1.18.30`) |
| **JetBrains Annotations** | Anotações para análise estática e documentação (`org.jetbrains:annotations`) |

---

## 🪵 Log
| Tecnologia       | Descrição                                               |
|-------------------|---------------------------------------------------------|
| **SLF4J**          | API de logging (`slf4j-api:2.0.13`)                     |
| **Logback Classic** | Implementação de logging para SLF4J (`logback-classic:1.5.6`) |

---

## 📊 Cobertura de Testes
| Tecnologia     | Descrição                                                       |
|----------------|-----------------------------------------------------------------|
| **JaCoCo**      | Plugin para geração de relatórios de cobertura de código (`jacoco-maven-plugin:0.8.11`) |

---


## 🛠️ Configuração

1. Clone o repositório:

   ```bash
   git clone https://seu-repositorio.git
   ```

2. Configure o banco de dados:

   Edite o arquivo `src/main/resources/application.properties`:

   ```properties
   db.url=jdbc:postgresql://localhost:5432/seu_banco
   db.user=seu_usuario
   db.password=sua_senha
   file.folder.path=/caminho/para/arquivos
   ```

---

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/yourcompany/filerenamer/
│   │   ├── config/       # Configurações
│   │   ├── domain/       # Modelos de dados
│   │   ├── repository/   # Acesso a dados
│   │   ├── service/      # Lógica de negócio
│   │   ├── util/         # Utilitários
│   │   └── Main.java     # Ponto de entrada
│   └── resources/        # Arquivos de configuração
└── test/                 # Testes unitários
```

---

## ▶️ Execução

### Via Maven:

```bash
mvn clean package
java -jar target/FileRenamer-1.0-SNAPSHOT.jar
```

### No IntelliJ:

1. Abra o arquivo `Main.java`
2. Clique no ícone ▶️ ou pressione `Shift + F10`

---

## 🧪 Testes

Para executar todos os testes:

```bash
mvn test
```

### Testes implementados:

| Pacote     | Classe                    | Descrição                  |
|------------|---------------------------|----------------------------|
| `config`   | `DatabaseConfigTest`      | Configuração do banco      |
| `domain`   | `FileRecordTest`          | Modelos de dados           |
| `repository` | `PersonRepositoryTest`  | Acesso a dados             |
| `service`  | `FileRenameServiceTest`   | Lógica principal           |
| `util`     | `FileUtilsTest`           | Manipulação de arquivos    |

📊 Relatório de cobertura disponível em:

```
target/site/jacoco/index.html
```

---

## 📝 Logging

Configuração localizada em `src/main/resources/logback.xml`:

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
  <root level="INFO">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

---

## 📦 Dependências

Confira o arquivo `pom.xml` para a lista completa de dependências utilizadas no projeto.

---

## 📄 Licença

Este projeto está licenciado sob a licença [MIT](https://opensource.org/licenses/MIT).
