package com.exemplo.sistemaacademico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemplo.sistemaacademico.entity.Disciplina;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de acesso aos dados da entidade Disciplina.
 * 
 * Esta interface fornece métodos para consultar disciplinas no banco de dados,
 * incluindo consultas personalizadas que exploram o relacionamento many-to-many
 * com cursos. O Spring Data JPA automaticamente implementa estes métodos
 * baseado nas convenções de nomenclatura e anotações JPQL.
 */
@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    
    /**
     * Busca uma disciplina pelo nome exato.
     * Este método é fundamental para evitar a criação de disciplinas duplicadas
     * e para implementar funcionalidades de busca precisa.
     * 
     * @param nome O nome exato da disciplina
     * @return Optional contendo a disciplina encontrada, ou vazio se não existir
     */
    Optional<Disciplina> findByNome(String nome);
    
    /**
     * Busca disciplinas cujo nome contenha uma determinada string, ignorando case.
     * Este método implementa uma funcionalidade de busca flexível, permitindo
     * que usuários encontrem disciplinas mesmo digitando apenas parte do nome.
     * A busca case-insensitive torna a experiência mais amigável ao usuário.
     * 
     * @param nome Parte do nome da disciplina a ser buscada
     * @return Lista de disciplinas que contêm a string no nome
     */
    List<Disciplina> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca disciplinas que pertencem a um curso específico.
     * Esta consulta navega através do relacionamento many-to-many para encontrar
     * todas as disciplinas oferecidas por um determinado curso. É especialmente útil
     * para montagem de grades curriculares e relatórios acadêmicos.
     * 
     * @param idCurso ID do curso
     * @return Lista de disciplinas que pertencem ao curso especificado
     */
    @Query("SELECT d FROM Disciplina d JOIN d.cursos c WHERE c.idCurso = :idCurso")
    List<Disciplina> findDisciplinasPorCurso(@Param("idCurso") Long idCurso);
    
    /**
     * Conta quantos cursos oferecem uma disciplina específica.
     * Esta informação é valiosa para análises acadêmicas, permitindo identificar
     * disciplinas que são amplamente oferecidas versus disciplinas mais especializadas.
     * A consulta é otimizada para retornar apenas a contagem, não os objetos completos.
     * 
     * @param idDisciplina ID da disciplina
     * @return Número de cursos que oferecem esta disciplina
     */
    @Query("SELECT COUNT(c) FROM Curso c JOIN c.disciplinas d WHERE d.idDisciplina = :idDisciplina")
    Long countCursosPorDisciplina(@Param("idDisciplina") Long idDisciplina);
    
    /**
     * Busca disciplinas que não estão associadas a nenhum curso.
     * Esta consulta é útil para identificar disciplinas órfãs que podem precisar
     * ser removidas do sistema ou associadas a cursos apropriados. É uma ferramenta
     * importante para manutenção da qualidade dos dados acadêmicos.
     * 
     * @return Lista de disciplinas não associadas a cursos
     */
    @Query("SELECT d FROM Disciplina d WHERE d.cursos IS EMPTY")
    List<Disciplina> findDisciplinasSemCursos();
    
    /**
     * Busca disciplinas ordenadas por nome em ordem alfabética.
     * A ordenação alfabética facilita a navegação em listas longas de disciplinas
     * e melhora significativamente a experiência do usuário em interfaces
     * administrativas e de consulta.
     * 
     * @return Lista de disciplinas ordenadas por nome
     */
    List<Disciplina> findAllByOrderByNomeAsc();
    
    /**
     * Busca disciplinas que são oferecidas por múltiplos cursos.
     * Esta consulta identifica disciplinas interdisciplinares ou de base comum
     * que são compartilhadas entre diferentes cursos. Útil para análises
     * de otimização curricular e planejamento de recursos docentes.
     * 
     * @return Lista de disciplinas oferecidas por mais de um curso
     */
    @Query("SELECT d FROM Disciplina d WHERE SIZE(d.cursos) > 1")
    List<Disciplina> findDisciplinasComMultiplosCursos();
}