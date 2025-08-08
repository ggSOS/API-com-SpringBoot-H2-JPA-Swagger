package com.exemplo.sistemaacademico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemplo.sistemaacademico.entity.Curso;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de acesso aos dados da entidade Curso.
 * 
 * Esta interface estende JpaRepository, que fornece automaticamente
 * implementações para operações CRUD básicas como save(), findById(),
 * findAll(), delete(), etc. Além disso, define métodos de consulta
 * personalizados específicos para o domínio de cursos.
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    /**
     * Busca um curso pelo nome exato.
     * Útil para verificar se já existe um curso com determinado nome
     * antes de criar um novo, evitando duplicações.
     * 
     * @param nome O nome do curso a ser buscado
     * @return Optional contendo o curso encontrado, ou vazio se não existir
     */
    Optional<Curso> findByNome(String nome);
    
    /**
     * Busca cursos cujo nome contenha uma determinada string, ignorando maiúsculas/minúsculas.
     * Este método é útil para funcionalidades de busca e filtro na interface do usuário,
     * permitindo que usuários encontrem cursos digitando apenas parte do nome.
     * 
     * @param nome Parte do nome do curso a ser buscado
     * @return Lista de cursos que contêm a string no nome
     */
    List<Curso> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca cursos que possuem uma disciplina específica.
     * Este método utiliza JPQL (Java Persistence Query Language) para navegar
     * através do relacionamento many-to-many entre curso e disciplina.
     * É especialmente útil para encontrar todos os cursos que oferecem
     * uma determinada disciplina.
     * 
     * @param idDisciplina ID da disciplina
     * @return Lista de cursos que contêm a disciplina especificada
     */
    @Query("SELECT c FROM Curso c JOIN c.disciplinas d WHERE d.idDisciplina = :idDisciplina")
    List<Curso> findCursosComDisciplina(@Param("idDisciplina") Long idDisciplina);
    
    /**
     * Conta o número de alunos matriculados em um curso específico.
     * Esta consulta é otimizada para retornar apenas a contagem,
     * sem carregar todos os objetos Aluno na memória, sendo mais
     * eficiente para grandes volumes de dados.
     * 
     * @param idCurso ID do curso
     * @return Número de alunos matriculados no curso
     */
    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.curso.idCurso = :idCurso")
    Long countAlunosPorCurso(@Param("idCurso") Long idCurso);
    
    /**
     * Busca cursos que não possuem alunos matriculados.
     * Útil para relatórios gerenciais ou para identificar cursos
     * que podem precisar de ações de marketing ou revisão.
     * 
     * @return Lista de cursos sem alunos
     */
    @Query("SELECT c FROM Curso c WHERE c.alunos IS EMPTY")
    List<Curso> findCursosSemAlunos();
    
    /**
     * Busca cursos ordenados pelo nome em ordem alfabética.
     * Facilita a apresentação organizada dos cursos em interfaces
     * de usuário, melhorando a experiência do usuário.
     * 
     * @return Lista de cursos ordenados por nome
     */
    List<Curso> findAllByOrderByNomeAsc();
}