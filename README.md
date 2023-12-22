# SISTEMA DE VENDAS - SPRING BOOT

API REST de vendas em Java utilizando:
- Java 17;
- Spring Boot 3;
- Spring Data JPA;
- Banco de dados MYSQL;
- Spring Validation;
- Spring Security;
- JWT;
- Lombok.

As funcionalidades desenvolvidas foram:
- Autenticação de usuários:
  - Com criação de 2 cargos de usuários distintos ("USER", "ADMIN").
- Login de usuário - Todos podem acessar este endpoint.
- CRUD de usuários:
  - Criar usuários - Todos podem acessar este endpoint;
  - Editar usuário - Apenas usuários autenticados podem editar seus próprios dados;
  - Deletar usuário - Apenas usuários autenticados podem se deletar;
  - Listar todos os usuários - Apenas usuários "ADMIN" podem acessar este endpoint;
  - Listar usuário por ID - Usuários "USER" podem visualizar apenas seus próprios dados, e usuários "ADMIN" podem visualizar os dados de todos.
- CRUD de produtos;
  - Criar produtos - Apenas usuários "ADMIN" podem acessar este endpoint;
  - Editar produtos - Apenas usuários "ADMIN" podem acessar este endpoint;
  - Deletar produtos - Apenas usuários "ADMIN" podem acessar este endpoint;
  - Listar todos os produtos - Apenas usuários autenticados podem acessar este endpoint;
  - Listar produto por ID - Apenas usuários autenticados podem acessar este endpoint.
- Cadastro e listagem de vendas:
  - Cadastro de vendas - Apenas usuários autenticados podem acessar este endpoint;
  - Listar todas as vendas - Apenas usuários "ADMIN" podem acessar este endpoint;
  - Listar vendas por ID - Usuários "USER" podem visualizar apenas suas próprias vendas, e usuários "ADMIN" podem visualizar todas as vendas;
  - Listar todas as vendas de um usuário específico - Usuários "USER" podem visualizar apenas suas próprias vendas, e usuários "ADMIN" podem visualizar as vendas de todos.
