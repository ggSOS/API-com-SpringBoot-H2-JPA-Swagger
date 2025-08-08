package com.exemplo.sistemaacademico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.sistemaacademico.entity.Disciplina;
import com.exemplo.sistemaacademico.repository.DisciplinaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio relacionada às disciplinas.
 * 
 * Esta classe implementa todas as operações de gestão de disciplinas,
 * incluindo operações CRUD completas, validações de negócio e consultas
 * especializadas. O serviço garante a integridade dos dados e implementa
 * regras específicas do domínio acadêmico antes de interagir com o repositório.
 */
@Service
@Transactional
public class DisciplinaService {
    
    private final DisciplinaRepository disciplinaRepository;
    
    @Autowired
    public DisciplinaService(DisciplinaRepository disciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
    }
    
    /**
     * Lista todas as disciplinas cadastradas no sistema.
     * As disciplinas são retornadas em ordem alfabética para facilitar a navegação.
     * 
     * @return Lista de todas as disciplinas ordenadas por nome
     */
    @Transactional(readOnly = true)
    public List<Disciplina> listarTodas() {
        return disciplinaRepository.findAllByOrderByNomeAsc();
    }
    
    /**
     * Busca uma disciplina específica pelo seu ID.
     * 
     * @param id ID da disciplina a ser buscada
     * @return Optional contendo a disciplina encontrada, ou vazio se não existir
     */
    @Transactional(readOnly = true)
    public Optional<Disciplina> buscarPorId(Long id) {
        return disciplinaRepository.findById(id);
    }
    
    /**
     * Busca disciplinas pelo nome, implementando busca parcial case-insensitive.
     * Este método é especialmente útil para funcionalidades de busca em tempo real
     * em interfaces de usuário, permitindo encontrar disciplinas rapidamente.
     * 
     * @param nome Parte do nome da disciplina a ser buscada
     * @return Lista de disciplinas que contêm a string no nome
     */
    @Transactional(readOnly = true)
    public List<Disciplina> buscarPorNome(String nome) {
        return disciplinaRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Cria uma nova disciplina no sistema.
     * 
     * Antes de criar, verifica se já existe uma disciplina com o mesmo nome
     * para evitar duplicações que poderiam causar confusão na gestão acadêmica.
     * 
     * @param disciplina Objeto disciplina a ser criada
     * @return A disciplina criada com ID gerado automaticamente
     * @throws IllegalArgumentException se já existir uma disciplina com o mesmo nome
     */
    public Disciplina criarDisciplina(Disciplina disciplina) {
        // Validação para evitar disciplinas duplicadas
        Optional<Disciplina> disciplinaExistente = disciplinaRepository.findByNome(disciplina.getNome());
        if (disciplinaExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma disciplina com o nome: " + disciplina.getNome());
        }
        return disciplinaRepository.save(disciplina);
    }
    
    /**
     * Atualiza uma disciplina existente.
     * 
     * Verifica se a disciplina existe e, em caso de mudança de nome,
     * valida se não há conflito com outras disciplinas existentes.
     * Esta validação é crucial para manter a unicidade dos nomes de disciplinas.
     * 
     * @param id ID da disciplina a ser atualizada
     * @param disciplinaAtualizada Dados atualizados da disciplina
     * @return A disciplina atualizada
     * @throws RuntimeException se a disciplina não for encontrada
     * @throws IllegalArgumentException se houver conflito de nome
     */
    public Disciplina atualizarDisciplina(Long id, Disciplina disciplinaAtualizada) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada com ID: " + id));
        
        // Verifica se o nome foi alterado e se não há conflito
        if (!disciplina.getNome().equals(disciplinaAtualizada.getNome())) {
            Optional<Disciplina> disciplinaComMesmoNome = disciplinaRepository.findByNome(disciplinaAtualizada.getNome());
            if (disciplinaComMesmoNome.isPresent() && !disciplinaComMesmoNome.get().getIdDisciplina().equals(id)) {
                throw new IllegalArgumentException("Já existe outra disciplina com o nome: " + disciplinaAtualizada.getNome());
            }
        }
        
        disciplina.setNome(disciplinaAtualizada.getNome());
        return disciplinaRepository.save(disciplina);
    }
    
    /**
     * Remove uma disciplina do sistema.
     * 
     * Antes de remover, verifica se a disciplina existe e se não está associada
     * a nenhum curso. Disciplinas que fazem parte de grades curriculares não
     * podem ser removidas para manter a integridade dos dados acadêmicos.
     * 
     * @param id ID da disciplina a ser removida
     * @throws RuntimeException se a disciplina não for encontrada
     * @throws IllegalStateException se a disciplina estiver associada a cursos
     */
    public void removerDisciplina(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada com ID: " + id));
        
        // Verifica se a disciplina está associada a algum curso
        Long numeroCursos = disciplinaRepository.countCursosPorDisciplina(id);
        if (numeroCursos > 0) {
            throw new IllegalStateException("Não é possível remover a disciplina pois ela está associada a " + 
                                          numeroCursos + " curso(s)");
        }
        
        disciplinaRepository.delete(disciplina);
    }
    
    /**
     * Busca disciplinas que pertencem a um curso específico.
     * Este método é fundamental para a montagem de grades curriculares
     * e relatórios acadêmicos por curso.
     * 
     * @param idCurso ID do curso
     * @return Lista de disciplinas que pertencem ao curso
     */
    @Transactional(readOnly = true)
    public List<Disciplina> buscarDisciplinasPorCurso(Long idCurso) {
        return disciplinaRepository.findDisciplinasPorCurso(idCurso);
    }
    
    /**
     * Conta quantos cursos oferecem uma disciplina específica.
     * Esta informação é valiosa para análises acadêmicas e tomada de decisões
     * sobre a relevância e distribuição de disciplinas na instituição.
     * 
     * @param idDisciplina ID da disciplina
     * @return Número de cursos que oferecem esta disciplina
     */
    @Transactional(readOnly = true)
    public Long contarCursosPorDisciplina(Long idDisciplina) {
        return disciplinaRepository.countCursosPorDisciplina(idDisciplina);
    }
    
    /**
     * Busca disciplinas que não estão associadas a nenhum curso.
     * Útil para identificar disciplinas órfãs que podem precisar de revisão
     * ou remoção do sistema, contribuindo para a limpeza dos dados.
     * 
     * @return Lista de disciplinas não associadas a cursos
     */
    @Transactional(readOnly = true)
    public List<Disciplina> buscarDisciplinasSemCursos() {
        return disciplinaRepository.findDisciplinasSemCursos();
    }
    
    /**
     * Busca disciplinas que são oferecidas por múltiplos cursos.
     * Esta consulta é útil para identificar disciplinas interdisciplinares
     * ou de base comum, auxiliando no planejamento de recursos e otimização curricular.
     * 
     * @return Lista de disciplinas oferecidas por mais de um curso
     */
    @Transactional(readOnly = true)
    public List<Disciplina> buscarDisciplinasComMultiplosCursos() {
        return disciplinaRepository.findDisciplinasComMultiplosCursos();
    }
}