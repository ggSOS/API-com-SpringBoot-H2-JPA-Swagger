# 🎓 Sistema Acadêmico - Backend Java

Um sistema completo de gerenciamento acadêmico desenvolvido em Java com Spring Boot, incluindo APIs REST para gestão de cursos, disciplinas e alunos.

## 📋 Características do Sistema

### Funcionalidades Principais
- ✅ **Gestão de Cursos**: CRUD completo com validações
- ✅ **Gestão de Disciplinas**: Operações completas de disciplinas
- ✅ **Gestão de Alunos**: Matrícula, transferência e consultas
- ✅ **Relacionamentos**: Associação Many-to-Many entre cursos e disciplinas
- ✅ **Validações**: Integridade referencial e regras de negócio
- ✅ **API REST**: Endpoints documentados com Swagger
- ✅ **Banco H2**: Banco em memória com dados de exemplo

### Tecnologias Utilizadas
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database**
- **Swagger/OpenAPI 3**
- **Maven**
- **Bean Validation**

## 🏗️ Arquitetura do Projeto

```
src/main/
├── resources/       # Configurações sem código (properties, sql.)
|  ├── application.properties
|  └── data.sql
└── java/com/exemplo/sistemaacademico/
   ├── entity/          # Entidades JPA (Curso, Disciplina, Aluno)
   |   ├── Curso.java
   |   ├── Disciplina.java
   |   └── Aluno.java
   ├── repository/      # Repositórios Spring Data JPA
   |   ├── CursoRepository.java
   |   ├── DisciplinaRepository.java
   |   └── AlunoRepository.java
   ├── service/         # Lógica de negócio e regras
   |   ├── CursoService.java
   |   ├── DisciplinaService.java
   |   └── AlunoService.java
   ├── controller/      # Controladores REST
   |   ├── CursoController.java
   |   ├── DisciplinaController.java
   |   └── AlunoController.java
   ├── config/          # Configurações (Swagger, etc.)
   |   └── SwaggerConfig.java
   └── SistemaAcademicoApplication.java  # Classe principal
```

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Passos para Execução

1. **Clone ou baixe os arquivos do projeto**

2. **Compile e execute a aplicação**:
   ```bash
   mvn spring-boot:run
   ```

3. **Ou execute via IDE**:
   - Importe o projeto no IntelliJ IDEA ou Eclipse
   - Execute a classe `SistemaAcademicoApplication.java`

4. **Acesse a aplicação**:
   - **API Base**: http://localhost:8080/sistema-academico
   - **Swagger UI**: http://localhost:8080/sistema-academico/swagger-ui.html
   - **Console H2**: http://localhost:8080/sistema-academico/h2-console

## 🗄️ Configuração do Banco H2

Para acessar o console do banco H2:

- **URL**: `jdbc:h2:mem:sistemaacademico`
- **Username**: `sa`
- **Password**: `password`

## 📚 Documentação da API

### Endpoints Principais

#### Cursos (`/api/cursos`)
- `GET /api/cursos` - Listar todos os cursos
- `GET /api/cursos/{id}` - Buscar curso por ID
- `GET /api/cursos/buscar?nome={nome}` - Buscar cursos por nome
- `POST /api/cursos` - Criar novo curso
- `PUT /api/cursos/{id}` - Atualizar curso
- `DELETE /api/cursos/{id}` - Remover curso
- `POST /api/cursos/{idCurso}/disciplinas/{idDisciplina}` - Associar disciplina
- `DELETE /api/cursos/{idCurso}/disciplinas/{idDisciplina}` - Desassociar disciplina

#### Disciplinas (`/api/disciplinas`)
- `GET /api/disciplinas` - Listar todas as disciplinas
- `GET /api/disciplinas/{id}` - Buscar disciplina por ID
- `GET /api/disciplinas/buscar?nome={nome}` - Buscar disciplinas por nome
- `POST /api/disciplinas` - Criar nova disciplina
- `PUT /api/disciplinas/{id}` - Atualizar disciplina
- `DELETE /api/disciplinas/{id}` - Remover disciplina
- `GET /api/disciplinas/curso/{idCurso}` - Disciplinas de um curso

#### Alunos (`/api/alunos`)
- `GET /api/alunos` - Listar todos os alunos
- `GET /api/alunos/{id}` - Buscar aluno por ID
- `GET /api/alunos/buscar?nome={nome}` - Buscar alunos por nome
- `POST /api/alunos?idCurso={idCurso}` - Criar novo aluno
- `PUT /api/alunos/{id}` - Atualizar aluno
- `DELETE /api/alunos/{id}` - Remover aluno
- `PUT /api/alunos/{id}/transferir?idNovoCurso={idCurso}` - Transferir aluno
- `GET /api/alunos/curso/{idCurso}` - Alunos de um curso

## 🧪 Dados de Teste

O sistema vem pré-carregado com dados de exemplo:

### Cursos Disponíveis
1. Ciência da Computação
2. Engenharia de Software
3. Sistemas de Informação
4. Análise e Desenvolvimento de Sistemas
5. Engenharia da Computação

### Disciplinas Exemplo
- Algoritmos e Estruturas de Dados
- Programação Orientada a Objetos
- Banco de Dados
- Engenharia de Software
- Redes de Computadores
- E mais 10 disciplinas...

### Alunos de Exemplo
- 20 alunos distribuídos entre os cursos
- Nomes realistas para facilitar os testes

## 🔧 Exemplos de Uso da API

### Criar um novo curso
```bash
curl -X POST http://localhost:8080/sistema-academico/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"nome": "Engenharia de Dados"}'
```

### Buscar alunos de um curso
```bash
curl http://localhost:8080/sistema-academico/api/alunos/curso/1
```

### Associar disciplina a um curso
```bash
curl -X POST http://localhost:8080/sistema-academico/api/cursos/1/disciplinas/5
```

## 🛡️ Validações Implementadas

- **Nomes únicos**: Cursos e disciplinas não podem ter nomes duplicados
- **Integridade referencial**: Não é possível remover cursos com alunos
- **Validação de dados**: Campos obrigatórios e limites de tamanho
- **Relacionamentos consistentes**: Manutenção automática de relacionamentos bidirecionais

## 🐛 Tratamento de Erros

A API retorna códigos HTTP apropriados:
- `200 OK` - Operação bem-sucedida
- `201 Created` - Recurso criado com sucesso
- `400 Bad Request` - Dados inválidos ou regra de negócio violada
- `404 Not Found` - Recurso não encontrado
- `409 Conflict` - Conflito (ex: tentativa de remover curso com alunos)

## 📈 Próximas Melhorias

- [ ] Autenticação e autorização
- [ ] Paginação nos endpoints de listagem
- [ ] Filtros avançados de busca
- [ ] Auditoria de operações
- [ ] Cache de consultas frequentes
- [ ] Testes unitários e de integração
- [ ] Docker e deploy em container

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para detalhes.

## 📞 Contato

- **Email**: dev@sistemaacademico.com
- **GitHub**: https://github.com/sistemaacademico
- **Documentação**: Use o Swagger UI para documentação interativa