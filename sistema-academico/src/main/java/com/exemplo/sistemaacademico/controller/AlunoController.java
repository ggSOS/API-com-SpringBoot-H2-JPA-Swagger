package com.exemplo.sistemaacademico.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.exemplo.sistemaacademico.entity.Aluno;
import com.exemplo.sistemaacademico.service.AlunoService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gerenciamento de alunos.
 * 
 * Este controlador fornece endpoints HTTP para operações relacionadas aos alunos,
 * incluindo matrícula, transferência entre cursos, consultas por curso e
 * relatórios. Todas as operações mantêm a integridade referencial com cursos.
 */
@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "API para gerenciamento de alunos")
@CrossOrigin(origins = "*") // Permite CORS para desenvolvimento frontend
public class AlunoController {
    
    private final AlunoService alunoService;
    
    @Autowired
    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }
    
    /**
     * Lista todos os alunos cadastrados.
     * 
     * @return Lista de alunos ordenados por nome
     */
    @GetMapping
    @Operation(
        summary = "Listar todos os alunos",
        description = "Retorna uma lista de todos os alunos cadastrados, ordenados por nome"
    )
    @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso")
    public ResponseEntity<List<Aluno>> listarTodos() {
        List<Aluno> alunos = alunoService.listarTodos();
        return ResponseEntity.ok(alunos);
    }
    
    /**
     * Busca um aluno específico por ID.
     * 
     * @param id ID do aluno
     * @return Dados do aluno encontrado ou 404 se não existir
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar aluno por ID",
        description = "Retorna os dados de um aluno específico baseado no ID fornecido"
    )
    @ApiResponse(responseCode = "200", description = "Aluno encontrado")
    @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    public ResponseEntity<Aluno> buscarPorId(
            @Parameter(description = "ID do aluno", required = true)
            @PathVariable Long id) {
        Optional<Aluno> aluno = alunoService.buscarPorId(id);
        return aluno.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Busca alunos por nome (busca parcial).
     * 
     * @param nome Parte do nome do aluno
     * @return Lista de alunos que contêm o nome especificado
     */
    @GetMapping("/buscar")
    @Operation(
        summary = "Buscar alunos por nome",
        description = "Retorna alunos cujo nome contenha a string fornecida (busca case-insensitive)"
    )
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<Aluno>> buscarPorNome(
            @Parameter(description = "Parte do nome do aluno", required = true)
            @RequestParam String nome) {
        List<Aluno> alunos = alunoService.buscarPorNome(nome);
        return ResponseEntity.ok(alunos);
    }
    
    /**
     * Busca alunos de um curso específico.
     * 
     * @param idCurso ID do curso
     * @return Lista de alunos matriculados no curso
     */
    @GetMapping("/curso/{idCurso}")
    @Operation(
        summary = "Buscar alunos por curso",
        description = "Retorna todos os alunos matriculados em um curso específico"
    )
    @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso")
    public ResponseEntity<List<Aluno>> buscarAlunosPorCurso(
            @Parameter(description = "ID do curso", required = true)
            @PathVariable Long idCurso) {
        List<Aluno> alunos = alunoService.buscarAlunosPorCurso(idCurso);
        return ResponseEntity.ok(alunos);
    }
    
    /**
     * Busca alunos pelo nome do curso.
     * 
     * @param nomeCurso Nome do curso
     * @return Lista de alunos do curso especificado
     */
    @GetMapping("/curso-nome")
    @Operation(
        summary = "Buscar alunos por nome do curso",
        description = "Retorna todos os alunos matriculados em um curso baseado no nome do curso"
    )
    @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso")
    public ResponseEntity<List<Aluno>> buscarAlunosPorNomeCurso(
            @Parameter(description = "Nome do curso", required = true)
            @RequestParam String nomeCurso) {
        List<Aluno> alunos = alunoService.buscarAlunosPorNomeCurso(nomeCurso);
        return ResponseEntity.ok(alunos);
    }
    
    /**
     * Cria um novo aluno e o matricula em um curso.
     * 
     * @param aluno Dados do aluno a ser criado
     * @param idCurso ID do curso onde matricular o aluno
     * @return Aluno criado com matrícula confirmada
     */
    @PostMapping
    @Operation(
        summary = "Criar novo aluno",
        description = "Cria um novo aluno no sistema e o matricula no curso especificado"
    )
    @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    public ResponseEntity<?> criarAluno(
            @Parameter(description = "Dados do aluno", required = true)
            @Valid @RequestBody Aluno aluno,
            @Parameter(description = "ID do curso para matrícula", required = true)
            @RequestParam Long idCurso) {
        try {
            Aluno alunoCriado = alunoService.criarAluno(aluno, idCurso);
            return ResponseEntity.status(HttpStatus.CREATED).body(alunoCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Atualiza um aluno existente.
     * 
     * @param id ID do aluno a ser atualizado
     * @param aluno Novos dados do aluno
     * @return Aluno atualizado
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar aluno",
        description = "Atualiza os dados de um aluno existente"
    )
    @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<?> atualizarAluno(
            @Parameter(description = "ID do aluno", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do aluno", required = true)
            @Valid @RequestBody Aluno aluno) {
        try {
            Aluno alunoAtualizado = alunoService.atualizarAluno(id, aluno);
            return ResponseEntity.ok(alunoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Transfere um aluno para outro curso.
     * 
     * @param idAluno ID do aluno
     * @param idNovoCurso ID do curso de destino
     * @return Aluno com curso atualizado
     */
    @PutMapping("/{idAluno}/transferir")
    @Operation(
        summary = "Transferir aluno de curso",
        description = "Transfere um aluno para outro curso"
    )
    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Aluno ou curso não encontrados")
    public ResponseEntity<?> transferirAluno(
            @Parameter(description = "ID do aluno", required = true)
            @PathVariable Long idAluno,
            @Parameter(description = "ID do novo curso", required = true)
            @RequestParam Long idNovoCurso) {
        try {
            Aluno aluno = alunoService.transferirAluno(idAluno, idNovoCurso);
            return ResponseEntity.ok(aluno);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Remove um aluno do sistema.
     * 
     * @param id ID do aluno a ser removido
     * @return Status da operação
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Remover aluno",
        description = "Remove um aluno do sistema completamente"
    )
    @ApiResponse(responseCode = "204", description = "Aluno removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    public ResponseEntity<?> removerAluno(
            @Parameter(description = "ID do aluno", required = true)
            @PathVariable Long id) {
        try {
            alunoService.removerAluno(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Conta alunos matriculados em um curso.
     * 
     * @param idCurso ID do curso
     * @return Número de alunos matriculados
     */
    @GetMapping("/curso/{idCurso}/count")
    @Operation(
        summary = "Contar alunos por curso",
        description = "Retorna o número de alunos matriculados em um curso específico"
    )
    @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso")
    public ResponseEntity<Long> contarAlunosPorCurso(
            @Parameter(description = "ID do curso", required = true)
            @PathVariable Long idCurso) {
        Long count = alunoService.contarAlunosPorCurso(idCurso);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Busca avançada de alunos por nome e curso.
     * 
     * @param nomeAluno Parte do nome do aluno (opcional)
     * @param nomeCurso Parte do nome do curso (opcional)
     * @return Lista de alunos que atendem aos critérios
     */
    @GetMapping("/busca-avancada")
    @Operation(
        summary = "Busca avançada de alunos",
        description = "Busca alunos combinando critérios de nome do aluno e nome do curso"
    )
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<Aluno>> buscaAvancada(
            @Parameter(description = "Parte do nome do aluno")
            @RequestParam(required = false) String nomeAluno,
            @Parameter(description = "Parte do nome do curso")
            @RequestParam(required = false) String nomeCurso) {
        List<Aluno> alunos = alunoService.buscarPorNomeAlunoECurso(nomeAluno, nomeCurso);
        return ResponseEntity.ok(alunos);
    }
    
    /**
     * Gera relatório de alunos por curso.
     * 
     * @return Relatório formatado em texto
     */
    @GetMapping("/relatorio")
    @Operation(
        summary = "Gerar relatório de alunos por curso",
        description = "Retorna um relatório formatado com a distribuição de alunos por curso"
    )
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    public ResponseEntity<String> gerarRelatorio() {
        String relatorio = alunoService.gerarRelatorioAlunosPorCurso();
        return ResponseEntity.ok(relatorio);
    }
}