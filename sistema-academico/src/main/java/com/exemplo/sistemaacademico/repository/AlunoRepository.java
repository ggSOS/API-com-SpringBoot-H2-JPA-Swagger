package com.exemplo.sistemaacademico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemplo.sistemaacademico.entity.Aluno;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de acesso aos dados da entidade Aluno.
 * 
 * Esta interface define métodos para consultar informações sobre alunos,
 * explorando especialmente o relacionamento many-to-one com cursos.
 * Os métodos aqui implementados são fundamentais para funcionalidades
 * como busca de alunos, relatórios por curso e gestão de matrículas.
 */
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    /**
     * Busca um aluno pelo nome exato.
     * Útil para verificar a existência de um aluno específico no sistema
     * e para funcionalidades de busca precisa por nome completo.
     * 
     * @param nome O nome completo do aluno
     * @return Optional contendo o aluno encontrado, ou vazio se não existir
     */
    Optional<Aluno> findByNome(String nome);
    
    /**
     * Busca alunos cujo nome contenha uma determinada string, ignorando case.
     * Este método implementa busca flexível, permitindo encontrar alunos
     * mesmo com busca parcial pelo nome. Muito útil em interfaces de usuário
     * onde é necessário filtrar alunos dinamicamente.
     * 
     * @param nome Parte do nome do aluno a ser buscado
     * @return Lista de alunos que contêm a string no nome
     */
    List<Aluno> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca todos os alunos matriculados em um curso específico.
     * Este método é essencial para gerar listas de turma, relatórios
     * por curso e funcionalidades administrativas relacionadas à
     * gestão de alunos por curso.
     * 
     * @param idCurso ID do curso
     * @return Lista de alunos matriculados no curso especificado
     */
    List<Aluno> findByCurso_IdCurso(Long idCurso);
    
    /**
     * Busca alunos por nome do curso.
     * Este método permite buscar alunos utilizando o nome do curso
     * em vez do ID, oferecendo uma interface mais amigável para consultas.
     * 
     * @param nomeCurso Nome do curso
     * @return Lista de alunos matriculados no curso com o nome especificado
     */
    @Query("SELECT a FROM Aluno a WHERE a.curso.nome = :nomeCurso")
    List<Aluno> findAlunosPorNomeCurso(@Param("nomeCurso") String nomeCurso);
    
    /**
     * Conta o número de alunos matriculados em um curso específico.
     * Esta consulta otimizada retorna apenas a contagem, sendo mais
     * eficiente que carregar todos os objetos Aluno quando apenas
     * a quantidade é necessária para relatórios ou estatísticas.
     * 
     * @param idCurso ID do curso
     * @return Número de alunos matriculados no curso
     */
    Long countByCurso_IdCurso(Long idCurso);
    
    /**
     * Busca alunos ordenados por nome em ordem alfabética.
     * A ordenação alfabética facilita a navegação em listas de alunos
     * e melhora a experiência do usuário em relatórios e interfaces administrativas.
     * 
     * @return Lista de todos os alunos ordenados por nome
     */
    List<Aluno> findAllByOrderByNomeAsc();
    
    /**
     * Busca alunos de um curso específico ordenados por nome.
     * Combina a filtragem por curso com ordenação alfabética,
     * sendo ideal para gerar listas de turma organizadas.
     * 
     * @param idCurso ID do curso
     * @return Lista de alunos do curso ordenados por nome
     */
    List<Aluno> findByCurso_IdCursoOrderByNomeAsc(Long idCurso);
    
    /**
     * Busca alunos usando busca parcial tanto no nome do aluno quanto no nome do curso.
     * Este método implementa uma busca abrangente que permite encontrar alunos
     * baseado em qualquer combinação de nome do aluno e nome do curso,
     * oferecendo máxima flexibilidade nas consultas.
     * 
     * @param nomeAluno Parte do nome do aluno
     * @param nomeCurso Parte do nome do curso
     * @return Lista de alunos que atendem aos critérios de busca
     */
    @Query("SELECT a FROM Aluno a WHERE a.nome LIKE %:nomeAluno% AND a.curso.nome LIKE %:nomeCurso%")
    List<Aluno> findByNomeAlunoAndNomeCurso(@Param("nomeAluno") String nomeAluno, 
                                            @Param("nomeCurso") String nomeCurso);
}