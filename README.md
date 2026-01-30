# DSCommerce

Projeto desenvolvido na trilha do curso "Java Spring Professional" da plataforma DEVSUPERIOR. É uma API RESTful exemplo para um e-commerce, demonstrando domínio, persistência JPA, serviços, controle de acesso e autenticação baseada em OAuth2/JWT.

**Principais conceitos demonstrados**
- Entidades de domínio e relacionamentos (Produtos, Categorias, Pedidos, Itens de Pedido, Usuários e Papéis).
- Camada de repositório com Spring Data JPA.
- Serviços e validações de negócio.
- Controladores REST e DTOs para exposição segura de recursos.
- Segurança: Authorization Server e Resource Server com OAuth2 / JWT.

**Stack**
- Java 11+ / 17
- Spring Boot
- Spring Data JPA
- Spring Security (OAuth2 + JWT)
- H2 / banco configurável via `application.yaml`
- Maven

**Estrutura relevante**
- Código: [src/main/java/com/devsuperior/dscommerce](src/main/java/com/devsuperior/dscommerce)
- Configuração: [src/main/resources/application.yaml](src/main/resources/application.yaml)
- Dados de exemplo: [src/main/resources/import.sql](src/main/resources/import.sql)
- Configuração de segurança: [src/main/java/com/devsuperior/dscommerce/config/AuthorizationServerConfig.java](src/main/java/com/devsuperior/dscommerce/config/AuthorizationServerConfig.java) e [src/main/java/com/devsuperior/dscommerce/config/ResourceServerConfig.java](src/main/java/com/devsuperior/dscommerce/config/ResourceServerConfig.java)

## Executando o projeto

Pré-requisitos: JDK compatível e Maven. Recomenda-se usar o wrapper incluído.

1. Build e testes:

```bash
./mvnw clean package
./mvnw test
```

2. Executar a aplicação:

```bash
./mvnw spring-boot:run
```

3. A aplicação carregará as configurações de [src/main/resources/application.yaml](src/main/resources/application.yaml) e os dados iniciais de [src/main/resources/import.sql](src/main/resources/import.sql).

## Endpoints principais (exemplos)
- `GET /products` — listar produtos (ver `ProductController`).
- `GET /categories` — listar categorias (ver `CategoryController`).
- `POST /orders` — criar pedido (ver `OrderController`).
- `POST /users` — operações com usuários (ver `UserController`).

Consulte os controladores em [src/main/java/com/devsuperior/dscommerce/controllers](src/main/java/com/devsuperior/dscommerce/controllers) para rotas completas.

## Segurança
A API utiliza um Authorization Server e Resource Server para emitir e validar tokens JWT. Para entender e ajustar a configuração autenticação/autorização veja:

- [src/main/java/com/devsuperior/dscommerce/config/AuthorizationServerConfig.java](src/main/java/com/devsuperior/dscommerce/config/AuthorizationServerConfig.java)
- [src/main/java/com/devsuperior/dscommerce/config/ResourceServerConfig.java](src/main/java/com/devsuperior/dscommerce/config/ResourceServerConfig.java)

Para testar endpoints protegidos, obtenha um token via o fluxo configurado (ex.: password grant personalizado) e envie o header `Authorization: Bearer <token>`.

## Dicas de desenvolvimento
- Importe o projeto na sua IDE como projeto Maven.
- Revise `application.yaml` para ajustar o datasource (H2 em memória ou outro banco).
- `import.sql` contém dados de exemplo para facilitar testes locais.

## Autor
Projeto do curso Java Spring Professional — adaptado/estudado por você.

## Licença
Consulte o arquivo LICENSE no repositório.

