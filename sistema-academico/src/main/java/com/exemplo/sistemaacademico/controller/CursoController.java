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

import com.exemplo.sistemaacademico.entity.Curso;
import com.exemplo.sistemaacademico.service.CursoService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gerenciamento de cursos.
 * 
 * Este controlador expõe endpoints HTTP para todas as operações relacionadas
 * a cursos, incluindo CRUD completo, associação com disciplinas e consultas
 * especializadas. Todas as operações são documentadas com Swagger/OpenAPI.
 */
@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "API para gerenciamento de cursos")
@CrossOrigin(origins = "*") // Permite CORS para desenvolvimento frontend
public class CursoController {
    
    private final CursoService cursoService;
    
    @Autowired
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }
    
    /**
     * Lista todos os cursos cadastrados.
     * 
     * @return Lista de cursos ordenados por nome
     */
    @GetMapping
    @Operation(
        summary = "Listar todos os cursos",
        description = "Retorna uma lista de todos os cursos cadastrados, ordenados por nome"
    )
    @ApiResponse(responseCode = "200", description = "Lista de cursos retornada com sucesso")
    public ResponseEntity<List<Curso>> listarTodos() {
        List<Curso> cursos = cursoService.listarTodos();
        return ResponseEntity.ok(cursos);
    }
    
    /**
     * Busca um curso específico por ID.
     * 
     * @param id ID do curso
     * @return Dados do curso encontrado ou 404 se não existir
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar curso por ID",
        description = "Retorna os dados de um curso específico baseado no ID fornecido"
    )
    @ApiResponse(responseCode = "200", description = "Curso encontrado")
    @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    public ResponseEntity<Curso> buscarPorId(
            @Parameter(description = "ID do curso", required = true)
            @PathVariable Long id) {
        Optional<Curso> curso = cursoService.buscarPorId(id);
        return curso.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Busca cursos por nome (busca parcial).
     * 
     * @param nome Parte do nome do curso
     * @return Lista de cursos que contêm o nome especificado
     */
    @GetMapping("/buscar")
    @Operation(
        summary = "Buscar cursos por nome",
        description = "Retorna cursos cujo nome contenha a string fornecida (busca case-insensitive)"
    )
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<Curso>> buscarPorNome(
            @Parameter(description = "Parte do nome do curso", required = true)
            @RequestParam String nome) {
        List<Curso> cursos = cursoService.buscarPorNome(nome);
        return ResponseEntity.ok(cursos);
    }
    
    /**
     * Cria um novo curso.
     * 
     * @param curso Dados do curso a ser criado
     * @return Curso criado com ID gerado
     */
    @PostMapping
    @Operation(
        summary = "Criar novo curso",
        description = "Cria um novo curso no sistema. O nome deve ser único."
    )
    @ApiResponse(responseCode = "201", description = "Curso criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou curso já existe")
    public ResponseEntity<?> criarCurso(
            @Parameter(description = "Dados do curso", required = true)
            @Valid @RequestBody Curso curso) {
        try {
            Curso cursoCriado = cursoService.criarCurso(curso);
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Atualiza um curso existente.
     * 
     * @param id ID do curso a ser atualizado
     * @param curso Novos dados do curso
     * @return Curso atualizado
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar curso",
        description = "Atualiza os dados de um curso existente"
    )
    @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<?> atualizarCurso(
            @Parameter(description = "ID do curso", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do curso", required = true)
            @Valid @RequestBody Curso curso) {
        try {
            Curso cursoAtualizado = cursoService.atualizarCurso(id, curso);
            return ResponseEntity.ok(cursoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Remove um curso.
     * 
     * @param id ID do curso a ser removido
     * @return Status da operação
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Remover curso",
        description = "Remove um curso do sistema. Não é possível remover cursos com alunos matriculados."
    )
    @ApiResponse(responseCode = "204", description = "Curso removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    @ApiResponse(responseCode = "409", description = "Curso possui alunos matriculados")
    public ResponseEntity<?> removerCurso(
            @Parameter(description = "ID do curso", required = true)
            @PathVariable Long id) {
        try {
            cursoService.removerCurso(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Adiciona uma disciplina a um curso.
     * 
     * @param idCurso ID do curso
     * @param idDisciplina ID da disciplina
     * @return Curso atualizado
     */
    @PostMapping("/{idCurso}/disciplinas/{idDisciplina}")
    @Operation(
        summary = "Adicionar disciplina ao curso",
        description = "Associa uma disciplina existente a um curso"
    )
    @ApiResponse(responseCode = "200", description = "Disciplina adicionada com sucesso")
    @ApiResponse(responseCode = "404", description = "Curso ou disciplina não encontrados")
    public ResponseEntity<?> adicionarDisciplina(
            @Parameter(description = "ID do curso", required = true)
            @PathVariable Long idCurso,
            @Parameter(description = "ID da disciplina", required = true)
            @PathVariable Long idDisciplina) {
        try {
            Curso curso = cursoService.adicionarDisciplina(idCurso, idDisciplina);
            return ResponseEntity.ok(curso);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Remove uma disciplina de um curso.
     * 
     * @param idCurso ID do curso
     * @param idDisciplina ID da disciplina
     * @return Curso atualizado
     */
    @DeleteMapping("/{idCurso}/disciplinas/{idDisciplina}")
    @Operation(
        summary = "Remover disciplina do curso",
        description = "Remove a associação entre um curso e uma disciplina"
    )
    @ApiResponse(responseCode = "200", description = "Disciplina removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Curso ou disciplina não encontrados")
    public ResponseEntity<?> removerDisciplina(
            @Parameter(description = "ID do curso", required = true)
            @PathVariable Long idCurso,
            @Parameter(description = "ID da disciplina", required = true)
            @PathVariable Long idDisciplina) {
        try {
            Curso curso = cursoService.removerDisciplina(idCurso, idDisciplina);
            return ResponseEntity.ok(curso);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Busca cursos que possuem uma disciplina específica.
     * 
     * @param idDisciplina ID da disciplina
     * @return Lista de cursos que possuem a disciplina
     */
    @GetMapping("/com-disciplina/{idDisciplina}")
    @Operation(
        summary = "Buscar cursos com disciplina específica",
        description = "Retorna todos os cursos que possuem uma determinada disciplina"
    )
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<Curso>> buscarCursosComDisciplina(
            @Parameter(description = "ID da disciplina", required = true)
            @PathVariable Long idDisciplina) {
        List<Curso> cursos = cursoService.buscarCursosComDisciplina(idDisciplina);
        return ResponseEntity.ok(cursos);
    }
    
    /**
     * Conta alunos matriculados em um curso.
     * 
     * @param id ID do curso
     * @return Número de alunos matriculados
     */
    @GetMapping("/{id}/alunos/count")
    @Operation(
        summary = "Contar alunos do curso",
        description = "Retorna o número de alunos matriculados em um curso específico"
    )
    @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso")
    public ResponseEntity<Long> contarAlunos(
            @Parameter(description = "ID do curso", required = true)
            @PathVariable Long id) {
        Long count = cursoService.contarAlunosPorCurso(id);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Lista cursos sem alunos matriculados.
     * 
     * @return Lista de cursos sem alunos
     */
    @GetMapping("/sem-alunos")
    @Operation(
        summary = "Listar cursos sem alunos",
        description = "Retorna todos os cursos que não possuem alunos matriculados"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Curso>> buscarCursosSemAlunos() {
        List<Curso> cursos = cursoService.buscarCursosSemAlunos();
        return ResponseEntity.ok(cursos);
    }
}