# AsheOs

## 🎯 Serviço de Registro de Ocorrências

Projeto de demonstração para portfólio, composto por uma API RESTful desenvolvida com Java 17, Spring Boot e outras tecnologias modernas. O serviço permite que usuários se registrem, autentiquem e criem ocorrências (como problemas de infraestrutura urbana), podendo também anexar arquivos a elas.

## 🚀 Guia de Instalação e Execução

Siga os passos abaixo para configurar e executar o projeto AsheOs:

1.  **Pré-requisitos:**
    *   Java Development Kit (JDK) 17 ou superior
    *   Maven 3.6.x ou superior
    *   Docker Desktop (opcional, para desenvolvimento com Docker Compose)

2.  **Clone o repositório:**
    ```bash
    git clone https://github.com/gammon64/asheos-api.git
    cd asheos-api
    ```

3.  **Configuração do Ambiente:**

    *   **Opção 1: Com Docker Compose (Recomendado para Desenvolvimento)**
        Este é o método mais simples. Certifique-se de ter o Docker em execução e rode o comando:
        ```bash
        docker-compose up -d
        ```
        Este comando irá iniciar os contêineres do **PostgreSQL** e do **Min.io** (para armazenamento de arquivos) com as configurações padrão do projeto.

    *   **Opção 2: Configuração Manual**
        Se não estiver usando Docker, você precisará:
        1.  Ter uma instância do **PostgreSQL** e um serviço de storage compatível com S3 (como o **Min.io** ou **AWS S3**) em execução.
        2.  Criar um banco de dados (ex: `asheos_db`).
        3.  Atualizar o arquivo `src/main/resources/application.properties` com as suas credenciais de banco de dados e storage.

4.  **Segredo do JWT:**
    Por segurança, a chave secreta para assinar os tokens JWT deve ser configurada. Crie um arquivo `.env` na raiz do projeto com o seguinte conteúdo:
    ```
    JWT_SECRET=seu_segredo_jwt_aqui
    ```
    >  substitua `seu_segredo_jwt_aqui` por uma chave secreta forte e única.

5.  **Construa o projeto:**
    ```bash
    ./mvnw clean install
    ```

5.  **Execute a aplicação:**
    ```bash
    ./mvnw spring-boot:run
        

## 📖 Acessando a API e a Documentação

Após iniciar a aplicação, você pode interagir com ela e visualizar a documentação gerada.

### Documentação da API (Spring REST Docs)

A documentação completa dos endpoints, incluindo exemplos de requisições e respostas, é gerada automaticamente durante a fase de testes do Maven (`./mvnw clean install`).
1.  **Gere a documentação** (se ainda não o fez):
    ```bash
    ./mvnw clean install
    ```
2.  **Acesse o arquivo HTML**: Abra o seguinte arquivo no seu navegador para visualizar a documentação completa:
    `file:///<caminho-absoluto-para-o-projeto>/asheos-api/target/generated-docs/index.html`

### Acessando os Endpoints

Você pode usar ferramentas como `curl`, Postman ou Insomnia para fazer requisições à API.

*   **URL Base:** `http://localhost:8084`
*   **Endpoints Principais:**
    *   `POST /auth/register`: Para registrar um novo usuário.
    *   `POST /auth/login`: Para autenticar e obter um token JWT.
    *   `GET /users/me`: Retorna os dados do usuário autenticado (requer autenticação).
    *   `POST /occurrences`: Cria uma nova ocorrência (requer autenticação).

> **Nota:** Para acessar endpoints protegidos, inclua o token JWT no cabeçalho `Authorization` da sua requisição: `Authorization: Bearer <seu_token_aqui>`.

## 🛠️ Principais Dependências

### Spring Boot Data JPA
Gerenciamento de dados relacionais com Persistence API e Hibernate.

### Spring Boot Security
Implementação de autenticação e autorização JWT para proteger endpoints.

### Spring Boot Web
Camada Web de API RESTful para comunicação cliente-servidor.

### Spring Boot Rest Docs
Geração automática de documentação para APIs RESTful.

### Spring Boot DevTools
Ferramenta para reinicialização automática e LiveReload.

### Spring Boot Docker Compose
Facilita a configuração e execução de contêineres Docker.

### Flyway
Gerenciamento de migrações de banco de dados.

### H2 Database
Banco de dados em memória para desenvolvimento de testes rápidos.

### PostgreSQL Driver
Driver JDBC para conexão com banco de dados PostgreSQL.

### Lombok
Redução de código boilerplate com anotações para getters, setters, construtores, etc.

### Spring Boot Validation
Validação de dados de entrada usando anotações.

### Auth0 JWT
Biblioteca para criação e verificação de tokens JWT para autenticação segura.