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

import com.exemplo.sistemaacademico.entity.Disciplina;
import com.exemplo.sistemaacademico.service.DisciplinaService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gerenciamento de disciplinas.
 * 
 * Este controlador fornece endpoints HTTP para todas as operações relacionadas
 * às disciplinas, incluindo operações CRUD, consultas especializadas e
 * estatísticas. Todas as operações são documentadas com anotações Swagger.
 */
@RestController
@RequestMapping("/api/disciplinas")
@Tag(name = "Disciplinas", description = "API para gerenciamento de disciplinas")
@CrossOrigin(origins = "*") // Permite CORS para desenvolvimento frontend
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @Autowired
    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    /**
     * Lista todas as disciplinas cadastradas.
     * 
     * @return Lista de disciplinas ordenadas por nome
     */
    @GetMapping
    @Operation(summary = "Listar todas as disciplinas", description = "Retorna uma lista de todas as disciplinas cadastradas, ordenadas por nome")
    @ApiResponse(responseCode = "200", description = "Lista de disciplinas retornada com sucesso")
    public ResponseEntity<List<Disciplina>> listarTodas() {
        List<Disciplina> disciplinas = disciplinaService.buscarDisciplinasSemCursos();
        return ResponseEntity.ok(disciplinas);
    }

    /**
     * Lista disciplinas oferecidas por múltiplos cursos.
     * 
     * @return Lista de disciplinas interdisciplinares
     */
    @GetMapping("/multiplos-cursos")
    @Operation(summary = "Listar disciplinas com múltiplos cursos", description = "Retorna disciplinas que são oferecidas por mais de um curso")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Disciplina>> buscarDisciplinasComMultiplosCursos() {
        List<Disciplina> disciplinas = disciplinaService.buscarDisciplinasComMultiplosCursos();
        return ResponseEntity.ok(disciplinas);
    }

    /**
     * Busca uma disciplina específica por ID.
     * 
     * @param id ID da disciplina
     * @return Dados da disciplina encontrada ou 404 se não existir
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar disciplina por ID", description = "Retorna os dados de uma disciplina específica baseado no ID fornecido")
    @ApiResponse(responseCode = "200", description = "Disciplina encontrada")
    @ApiResponse(responseCode = "404", description = "Disciplina não encontrada")
    public ResponseEntity<Disciplina> buscarPorId(
            @Parameter(description = "ID da disciplina", required = true) @PathVariable Long id) {
        Optional<Disciplina> disciplina = disciplinaService.buscarPorId(id);
        return disciplina.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca disciplinas por nome (busca parcial).
     * 
     * @param nome Parte do nome da disciplina
     * @return Lista de disciplinas que contêm o nome especificado
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar disciplinas por nome", description = "Retorna disciplinas cujo nome contenha a string fornecida (busca case-insensitive)")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<Disciplina>> buscarPorNome(
            @Parameter(description = "Parte do nome da disciplina", required = true) @RequestParam String nome) {
        List<Disciplina> disciplinas = disciplinaService.buscarPorNome(nome);
        return ResponseEntity.ok(disciplinas);
    }

    /**
     * Cria uma nova disciplina.
     * 
     * @param disciplina Dados da disciplina a ser criada
     * @return Disciplina criada com ID gerado
     */
    @PostMapping
    @Operation(summary = "Criar nova disciplina", description = "Cria uma nova disciplina no sistema. O nome deve ser único.")
    @ApiResponse(responseCode = "201", description = "Disciplina criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou disciplina já existe")
    public ResponseEntity<?> criarDisciplina(
            @Parameter(description = "Dados da disciplina", required = true) @Valid @RequestBody Disciplina disciplina) {
        try {
            Disciplina disciplinaCriada = disciplinaService.criarDisciplina(disciplina);
            return ResponseEntity.status(HttpStatus.CREATED).body(disciplinaCriada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Atualiza uma disciplina existente.
     * 
     * @param id         ID da disciplina a ser atualizada
     * @param disciplina Novos dados da disciplina
     * @return Disciplina atualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar disciplina", description = "Atualiza os dados de uma disciplina existente")
    @ApiResponse(responseCode = "200", description = "Disciplina atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Disciplina não encontrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<?> atualizarDisciplina(
            @Parameter(description = "ID da disciplina", required = true) @PathVariable Long id,
            @Parameter(description = "Novos dados da disciplina", required = true) @Valid @RequestBody Disciplina disciplina) {
        try {
            Disciplina disciplinaAtualizada = disciplinaService.atualizarDisciplina(id, disciplina);
            return ResponseEntity.ok(disciplinaAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove uma disciplina.
     * 
     * @param id ID da disciplina a ser removida
     * @return Status da operação
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover disciplina", description = "Remove uma disciplina do sistema. Não é possível remover disciplinas associadas a cursos.")
    @ApiResponse(responseCode = "204", description = "Disciplina removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Disciplina não encontrada")
    @ApiResponse(responseCode = "409", description = "Disciplina está associada a cursos")
    public ResponseEntity<?> removerDisciplina(
            @Parameter(description = "ID da disciplina", required = true) @PathVariable Long id) {
        try {
            disciplinaService.removerDisciplina(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca disciplinas de um curso específico.
     * 
     * @param idCurso ID do curso
     * @return Lista de disciplinas do curso
     */
    @GetMapping("/curso/{idCurso}")
    @Operation(summary = "Buscar disciplinas por curso", description = "Retorna todas as disciplinas que pertencem a um curso específico")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<Disciplina>> buscarDisciplinasPorCurso(
            @Parameter(description = "ID do curso", required = true) @PathVariable Long idCurso) {
        List<Disciplina> disciplinas = disciplinaService.buscarDisciplinasPorCurso(idCurso);
        return ResponseEntity.ok(disciplinas);
    }

    /**
     * Conta quantos cursos oferecem uma disciplina.
     * 
     * @param id ID da disciplina
     * @return Número de cursos que oferecem a disciplina
     */
    @GetMapping("/{id}/cursos/count")
    @Operation(summary = "Contar cursos da disciplina", description = "Retorna o número de cursos que oferecem uma disciplina específica")
    @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso")
    public ResponseEntity<Long> contarCursos(
            @Parameter(description = "ID da disciplina", required = true) @PathVariable Long id) {
        Long count = disciplinaService.contarCursosPorDisciplina(id);
        return ResponseEntity.ok(count);
    }

    /**
     * Lista disciplinas sem cursos associados.
     * 
     * @return Lista de disciplinas órfãs
     */
    @GetMapping("/sem-cursos")
    @Operation(summary = "Listar disciplinas sem cursos", description = "Retorna todas as disciplinas que não estão associadas a nenhum curso")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Disciplina>> buscarDisciplinasSemCursos() {
        List<Disciplina> disciplinas = disciplinaService.listarTodas();
        return ResponseEntity.ok(disciplinas);
    }
}