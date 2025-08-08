package com.exemplo.sistemaacademico.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * Configuração personalizada do Swagger/OpenAPI.
 * 
 * Esta classe configura a documentação automática da API,
 * definindo informações detalhadas sobre o sistema, contatos,
 * licença e servidores disponíveis. A documentação gerada
 * será acessível através do Swagger UI.
 */
@Configuration
public class SwaggerConfig {
    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    
    /**
     * Configura a documentação OpenAPI personalizada.
     * 
     * Esta configuração define metadados completos sobre a API,
     * incluindo informações de contato, licença e servidores
     * disponíveis para teste da API.
     * 
     * @return Configuração OpenAPI personalizada
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema Acadêmico - API REST")
                        .version("1.0.0")
                        .description("""
                                ## Sistema de Gerenciamento Acadêmico
                                
                                Esta API fornece endpoints completos para gerenciamento de um sistema acadêmico,
                                incluindo operações para:
                                
                                ### 📚 **Funcionalidades Principais**
                                - **Cursos**: Criação, edição, remoção e consulta de cursos
                                - **Disciplinas**: Gerenciamento completo de disciplinas
                                - **Alunos**: Matrícula, transferência e consulta de alunos
                                - **Relacionamentos**: Associação entre cursos e disciplinas
                                
                                ### 🛠️ **Tecnologias Utilizadas**
                                - **Spring Boot 3.2.0**: Framework principal
                                - **Spring Data JPA**: Persistência e mapeamento objeto-relacional
                                - **H2 Database**: Banco de dados em memória para desenvolvimento
                                - **Swagger/OpenAPI 3**: Documentação automática da API
                                - **Bean Validation**: Validação de dados de entrada
                                
                                ### 🚀 **Como Usar**
                                1. **Explore os endpoints**: Use as seções abaixo para ver todas as operações disponíveis
                                2. **Teste interativo**: Clique em qualquer endpoint e use o botão "Try it out"
                                3. **Dados de exemplo**: O sistema já vem com dados pré-carregados para teste
                                4. **Console do banco**: Acesse `/h2-console` para visualizar os dados diretamente
                                
                                ### 📊 **Modelo de Dados**
                                - **Curso** ↔ **Disciplina**: Relacionamento Many-to-Many
                                - **Curso** → **Aluno**: Relacionamento One-to-Many
                                
                                ### ⚠️ **Notas Importantes**
                                - Este é um ambiente de desenvolvimento com banco H2 em memória
                                - Os dados são reinicializados a cada restart da aplicação
                                - Todas as operações são validadas quanto à integridade referencial
                                """)
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@sistemaacademico.com")
                                .url("https://github.com/sistemaacademico"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080" + contextPath)
                                .description("Servidor de Desenvolvimento Local"),
                        new Server()
                                .url("https://api.sistemaacademico.com")
                                .description("Servidor de Produção (Exemplo)")
                ));
    }
}