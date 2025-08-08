package com.exemplo.sistemaacademico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.sistemaacademico.entity.Curso;
import com.exemplo.sistemaacademico.entity.Disciplina;
import com.exemplo.sistemaacademico.repository.CursoRepository;
import com.exemplo.sistemaacademico.repository.DisciplinaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio relacionada aos cursos.
 * 
 * Esta classe implementa todas as operações relacionadas à gestão de cursos,
 * incluindo criação, atualização, consulta e exclusão, além de operações
 * específicas como associação com disciplinas. O serviço garante a integridade
 * dos dados e implementa regras de negócio antes de delegar operações aos repositórios.
 */
@Service
@Transactional
public class CursoService {
    
    private final CursoRepository cursoRepository;
    private final DisciplinaRepository disciplinaRepository;
    
    @Autowired
    public CursoService(CursoRepository cursoRepository, DisciplinaRepository disciplinaRepository) {
        this.cursoRepository = cursoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }
    
    /**
     * Busca todos os cursos cadastrados no sistema.
     * Os cursos são retornados ordenados por nome para facilitar a navegação.
     * 
     * @return Lista de todos os cursos ordenados por nome
     */
    @Transactional(readOnly = true)
    public List<Curso> listarTodos() {
        return cursoRepository.findAllByOrderByNomeAsc();
    }
    
    /**
     * Busca um curso específico pelo seu ID.
     * 
     * @param id ID do curso a ser buscado
     * @return Optional contendo o curso encontrado, ou vazio se não existir
     */
    @Transactional(readOnly = true)
    public Optional<Curso> buscarPorId(Long id) {
        return cursoRepository.findById(id);
    }
    
    /**
     * Busca cursos pelo nome, implementando busca parcial case-insensitive.
     * Este método permite encontrar cursos mesmo com busca parcial.
     * 
     * @param nome Parte do nome do curso a ser buscado
     * @return Lista de cursos que contêm a string no nome
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarPorNome(String nome) {
        return cursoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Cria um novo curso no sistema.
     * 
     * Antes de criar o curso, verifica se já existe um curso com o mesmo nome
     * para evitar duplicações. Se existir, lança uma exceção informativa.
     * 
     * @param curso Objeto curso a ser criado
     * @return O curso criado com ID gerado
     * @throws IllegalArgumentException se já existir um curso com o mesmo nome
     */
    public Curso criarCurso(Curso curso) {
        // Validação para evitar cursos duplicados
        Optional<Curso> cursoExistente = cursoRepository.findByNome(curso.getNome());
        if (cursoExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe um curso com o nome: " + curso.getNome());
        }
        
        return cursoRepository.save(curso);
    }
    
    /**
     * Atualiza um curso existente.
     * 
     * Verifica se o curso existe antes de tentar atualizá-lo.
     * Se houver mudança no nome, verifica se não há conflito com outro curso existente.
     * 
     * @param id ID do curso a ser atualizado
     * @param cursoAtualizado Dados atualizados do curso
     * @return O curso atualizado
     * @throws RuntimeException se o curso não for encontrado
     * @throws IllegalArgumentException se houver conflito de nome
     */
    public Curso atualizarCurso(Long id, Curso cursoAtualizado) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + id));
        
        // Verifica se o nome foi alterado e se não há conflito
        if (!curso.getNome().equals(cursoAtualizado.getNome())) {
            Optional<Curso> cursoComMesmoNome = cursoRepository.findByNome(cursoAtualizado.getNome());
            if (cursoComMesmoNome.isPresent() && !cursoComMesmoNome.get().getIdCurso().equals(id)) {
                throw new IllegalArgumentException("Já existe outro curso com o nome: " + cursoAtualizado.getNome());
            }
        }
        
        curso.setNome(cursoAtualizado.getNome());
        return cursoRepository.save(curso);
    }
    
    /**
     * Remove um curso do sistema.
     * 
     * Antes de remover, verifica se o curso existe e se não possui alunos matriculados.
     * Cursos com alunos não podem ser removidos para manter a integridade referencial.
     * 
     * @param id ID do curso a ser removido
     * @throws RuntimeException se o curso não for encontrado
     * @throws IllegalStateException se o curso possuir alunos matriculados
     */
    public void removerCurso(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + id));
        
        // Verifica se há alunos matriculados
        Long numeroAlunos = cursoRepository.countAlunosPorCurso(id);
        if (numeroAlunos > 0) {
            throw new IllegalStateException("Não é possível remover o curso pois há " + 
                                          numeroAlunos + " aluno(s) matriculado(s)");
        }
        
        cursoRepository.delete(curso);
    }
    
    /**
     * Associa uma disciplina a um curso.
     * 
     * Verifica se tanto o curso quanto a disciplina existem antes de criar a associação.
     * Se a disciplina já estiver associada ao curso, não faz nada (operação idempotente).
     * 
     * @param idCurso ID do curso
     * @param idDisciplina ID da disciplina
     * @return O curso atualizado com a nova disciplina
     * @throws RuntimeException se curso ou disciplina não forem encontrados
     */
    public Curso adicionarDisciplina(Long idCurso, Long idDisciplina) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idCurso));
        
        Disciplina disciplina = disciplinaRepository.findById(idDisciplina)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada com ID: " + idDisciplina));
        
        // Adiciona a disciplina apenas se ainda não estiver associada
        if (!curso.getDisciplinas().contains(disciplina)) {
            curso.adicionarDisciplina(disciplina);
            return cursoRepository.save(curso);
        }
        
        return curso;
    }
    
    /**
     * Remove a associação entre um curso e uma disciplina.
     * 
     * @param idCurso ID do curso
     * @param idDisciplina ID da disciplina
     * @return O curso atualizado sem a disciplina
     * @throws RuntimeException se curso ou disciplina não forem encontrados
     */
    public Curso removerDisciplina(Long idCurso, Long idDisciplina) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idCurso));
        
        Disciplina disciplina = disciplinaRepository.findById(idDisciplina)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada com ID: " + idDisciplina));
        
        curso.removerDisciplina(disciplina);
        return cursoRepository.save(curso);
    }
    
    /**
     * Busca cursos que possuem uma disciplina específica.
     * 
     * @param idDisciplina ID da disciplina
     * @return Lista de cursos que possuem a disciplina
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarCursosComDisciplina(Long idDisciplina) {
        return cursoRepository.findCursosComDisciplina(idDisciplina);
    }
    
    /**
     * Conta o número de alunos matriculados em um curso.
     * 
     * @param idCurso ID do curso
     * @return Número de alunos matriculados
     */
    @Transactional(readOnly = true)
    public Long contarAlunosPorCurso(Long idCurso) {
        return cursoRepository.countAlunosPorCurso(idCurso);
    }
    
    /**
     * Busca cursos sem alunos matriculados.
     * Útil para relatórios gerenciais.
     * 
     * @return Lista de cursos sem alunos
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarCursosSemAlunos() {
        return cursoRepository.findCursosSemAlunos();
    }
}