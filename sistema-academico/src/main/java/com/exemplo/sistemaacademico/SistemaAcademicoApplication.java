package com.exemplo.sistemaacademico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

/**
 * Classe principal da aplicação Sistema Acadêmico.
 * 
 * Esta classe é o ponto de entrada da aplicação Spring Boot.
 * Ela configura automaticamente todos os componentes necessários
 * através das anotações do Spring Boot, incluindo:
 * - Configuração do banco de dados H2
 * - Mapeamento JPA das entidades
 * - Configuração dos controladores REST
 * - Integração com Swagger para documentação da API
 * 
 * Para executar a aplicação:
 * 1. Execute este método main
 * 2. Acesse http://localhost:8080/sistema-academico
 * 3. Use o console H2: http://localhost:8080/sistema-academico/h2-console
 * 4. Veja a documentação da API: http://localhost:8080/sistema-academico/swagger-ui.html
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Sistema Acadêmico API",
        version = "1.0.0",
        description = "API REST para gerenciamento de sistema acadêmico com cursos, disciplinas e alunos",
        contact = @Contact(
            name = "Equipe de Desenvolvimento",
            email = "dev@sistemaacademico.com",
            url = "https://sistemaacademico.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    )
)
public class SistemaAcademicoApplication {
    
    /**
     * Método principal que inicia a aplicação Spring Boot.
     * 
     * O Spring Boot automaticamente:
     * - Configura um servidor Tomcat embarcado na porta 8080
     * - Inicializa o banco de dados H2 em memória
     * - Executa as migrações do schema (create-drop)
     * - Carrega os dados iniciais do arquivo data.sql
     * - Registra todos os controllers, services e repositories
     * - Configura o Swagger UI para documentação da API
     * 
     * @param args Argumentos da linha de comando
     */
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("🎓 INICIANDO SISTEMA ACADÊMICO");
        System.out.println("==============================================");
        System.out.println("📊 Banco de Dados: H2 (em memória)");
        System.out.println("🌐 Servidor: http://localhost:8080/sistema-academico");
        System.out.println("🗄️  Console H2: http://localhost:8080/sistema-academico/h2-console");
        System.out.println("📚 Swagger UI: http://localhost:8080/sistema-academico/swagger-ui.html");
        System.out.println("📋 API Docs: http://localhost:8080/sistema-academico/api-docs");
        System.out.println("==============================================");
        
        SpringApplication.run(SistemaAcademicoApplication.class, args);
        
        System.out.println("✅ Sistema Acadêmico iniciado com sucesso!");
        System.out.println("💡 Dica: Use o Swagger UI para testar as APIs interativamente");
    }
}