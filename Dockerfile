### Etapa 1: CONSTRUÇÃO
# Compila o Código e gera o arquivo war

# Usando Maven com JDK 17
FROM maven:4.0.0-rc-4-amazoncorretto-17 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia o POM e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Empacota o projeto pulando testes
RUN mvn clean package -DskipTests

### Etapa 2: EXECUÇÃO
# Copia e executa o aplicativo no contêiner docker

# Usando Amazon Corretto 17
FROM amazoncorretto:17-alpine-jdk

# Define a porta usada
EXPOSE 8080

# Define o nome final do arquivo
ARG JAR_FILE=target/*.jar

# Copia o arquivo jar do estágio de construção
COPY --from=build /app/${JAR_FILE} app.jar

# Define o ponto de entrada da aplicação
ENTRYPOINT ["java","-jar","/app.jar"]