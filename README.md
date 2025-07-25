# 📁 FileRenamer

Projeto Java para renomear arquivos baseado em registros de banco de dados, seguindo o padrão:

```
id_namePerson_nomeOriginal.extensão
```

---

## 📋 Requisitos

- Java 21+
- Maven 3.6+
- PostgreSQL (ou outro banco configurável)
- IntelliJ IDEA (recomendado) ou Eclipse

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
