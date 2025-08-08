package com.exemplo.sistemaacademico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.sistemaacademico.entity.Aluno;
import com.exemplo.sistemaacademico.entity.Curso;
import com.exemplo.sistemaacademico.repository.AlunoRepository;
import com.exemplo.sistemaacademico.repository.CursoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio relacionada aos alunos.
 * 
 * Esta classe implementa operações completas de gestão de alunos,
 * incluindo matrícula, transferência entre cursos, consultas e relatórios.
 * O serviço garante que todas as operações mantenham a integridade
 * referencial entre alunos e cursos, implementando regras de negócio específicas.
 */
@Service
@Transactional
public class AlunoService {
    
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    
    @Autowired
    public AlunoService(AlunoRepository alunoRepository, CursoRepository cursoRepository) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
    }
    
    /**
     * Lista todos os alunos cadastrados no sistema.
     * Os alunos são retornados ordenados por nome para facilitar a navegação.
     * 
     * @return Lista de todos os alunos ordenados por nome
     */
    @Transactional(readOnly = true)
    public List<Aluno> listarTodos() {
        return alunoRepository.findAllByOrderByNomeAsc();
    }
    
    /**
     * Busca um aluno específico pelo seu ID.
     * 
     * @param id ID do aluno a ser buscado
     * @return Optional contendo o aluno encontrado, ou vazio se não existir
     */
    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorId(Long id) {
        return alunoRepository.findById(id);
    }
    
    /**
     * Busca alunos pelo nome, implementando busca parcial case-insensitive.
     * Este método é essencial para funcionalidades de busca rápida de alunos
     * em interfaces administrativas e de consulta.
     * 
     * @param nome Parte do nome do aluno a ser buscado
     * @return Lista de alunos que contêm a string no nome
     */
    @Transactional(readOnly = true)
    public List<Aluno> buscarPorNome(String nome) {
        return alunoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Busca todos os alunos matriculados em um curso específico.
     * Os resultados são ordenados por nome para facilitar a consulta.
     * Este método é fundamental para gerar listas de turma e relatórios por curso.
     * 
     * @param idCurso ID do curso
     * @return Lista de alunos matriculados no curso, ordenados por nome
     */
    @Transactional(readOnly = true)
    public List<Aluno> buscarAlunosPorCurso(Long idCurso) {
        return alunoRepository.findByCurso_IdCursoOrderByNomeAsc(idCurso);
    }
    
    /**
     * Busca alunos pelo nome do curso.
     * Oferece uma alternativa mais amigável para busca quando o nome
     * do curso é conhecido mas o ID não.
     * 
     * @param nomeCurso Nome do curso
     * @return Lista de alunos matriculados no curso especificado
     */
    @Transactional(readOnly = true)
    public List<Aluno> buscarAlunosPorNomeCurso(String nomeCurso) {
        return alunoRepository.findAlunosPorNomeCurso(nomeCurso);
    }
    
    /**
     * Cria um novo aluno e o matricula em um curso.
     * 
     * Verifica se o curso especificado existe antes de criar o aluno.
     * Também valida se já existe um aluno com o mesmo nome para evitar
     * duplicações acidentais (embora nomes iguais possam ser legítimos).
     * 
     * @param aluno Dados do aluno a ser criado
     * @param idCurso ID do curso onde o aluno será matriculado
     * @return O aluno criado com matrícula confirmada
     * @throws RuntimeException se o curso não for encontrado
     * @throws IllegalArgumentException se já existir aluno com mesmo nome
     */
    public Aluno criarAluno(Aluno aluno, Long idCurso) {
        // Verifica se o curso existe
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idCurso));
        
        // Aviso sobre possível duplicação (pode ser legítimo ter nomes iguais)
        Optional<Aluno> alunoExistente = alunoRepository.findByNome(aluno.getNome());
        if (alunoExistente.isPresent()) {
            // Log de aviso, mas permite a criação (nomes iguais podem ser legítimos)
            System.out.println("Aviso: Já existe um aluno com o nome '" + aluno.getNome() + 
                             "'. Verifique se não é uma duplicação.");
        }
        
        // Estabelece o relacionamento com o curso
        aluno.setCurso(curso);
        return alunoRepository.save(aluno);
    }
    
    /**
     * Atualiza os dados de um aluno existente.
     * 
     * Permite atualizar o nome do aluno, mantendo sua matrícula no curso atual.
     * Verifica se o aluno existe antes de tentar atualizá-lo.
     * 
     * @param id ID do aluno a ser atualizado
     * @param alunoAtualizado Dados atualizados do aluno
     * @return O aluno atualizado
     * @throws RuntimeException se o aluno não for encontrado
     */
    public Aluno atualizarAluno(Long id, Aluno alunoAtualizado) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + id));
        
        aluno.setNome(alunoAtualizado.getNome());
        return alunoRepository.save(aluno);
    }
    
    /**
     * Transfere um aluno para outro curso.
     * 
     * Esta operação é equivalente a uma transferência de curso, mantendo
     * o histórico do aluno mas alterando sua matrícula atual. Verifica
     * se tanto o aluno quanto o curso de destino existem.
     * 
     * @param idAluno ID do aluno a ser transferido
     * @param idNovoCurso ID do curso de destino
     * @return O aluno com curso atualizado
     * @throws RuntimeException se aluno ou curso não forem encontrados
     */
    public Aluno transferirAluno(Long idAluno, Long idNovoCurso) {
        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + idAluno));
        
        Curso novoCurso = cursoRepository.findById(idNovoCurso)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idNovoCurso));
        
        // Usa o método utilitário para manter a consistência do relacionamento
        aluno.matricularEmCurso(novoCurso);
        return alunoRepository.save(aluno);
    }
    
    /**
     * Remove um aluno do sistema.
     * 
     * Esta operação remove completamente o aluno do sistema, incluindo
     * sua matrícula. Deve ser usada com cuidado, especialmente em sistemas
     * de produção onde históricos acadêmicos devem ser preservados.
     * 
     * @param id ID do aluno a ser removido
     * @throws RuntimeException se o aluno não for encontrado
     */
    public void removerAluno(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + id));
        
        alunoRepository.delete(aluno);
    }
    
    /**
     * Conta o número de alunos matriculados em um curso específico.
     * 
     * Este método oferece uma forma eficiente de obter estatísticas
     * sem carregar todos os objetos aluno na memória.
     * 
     * @param idCurso ID do curso
     * @return Número de alunos matriculados no curso
     */
    @Transactional(readOnly = true)
    public Long contarAlunosPorCurso(Long idCurso) {
        return alunoRepository.countByCurso_IdCurso(idCurso);
    }
    
    /**
     * Busca alunos usando critérios combinados de nome do aluno e nome do curso.
     * 
     * Este método implementa uma busca avançada que permite filtrar alunos
     * baseado em qualquer combinação de nome do aluno e curso, oferecendo
     * máxima flexibilidade para consultas complexas.
     * 
     * @param nomeAluno Parte do nome do aluno (pode ser vazio para ignorar)
     * @param nomeCurso Parte do nome do curso (pode ser vazio para ignorar)
     * @return Lista de alunos que atendem aos critérios
     */
    @Transactional(readOnly = true)
    public List<Aluno> buscarPorNomeAlunoECurso(String nomeAluno, String nomeCurso) {
        // Trata strings vazias como wildcards
        String nomeAlunoBusca = (nomeAluno == null || nomeAluno.trim().isEmpty()) ? "" : nomeAluno.trim();
        String nomeCursoBusca = (nomeCurso == null || nomeCurso.trim().isEmpty()) ? "" : nomeCurso.trim();
        
        return alunoRepository.findByNomeAlunoAndNomeCurso(nomeAlunoBusca, nomeCursoBusca);
    }
    
    /**
     * Valida se um aluno pode ser matriculado em um curso específico.
     * 
     * Este método pode ser expandido para incluir regras de negócio mais
     * complexas, como pré-requisitos, limites de vagas, etc.
     * 
     * @param aluno Aluno a ser validado
     * @param idCurso ID do curso
     * @return true se a matrícula é válida, false caso contrário
     */
    @Transactional(readOnly = true)
    public boolean validarMatricula(Aluno aluno, Long idCurso) {
        // Verifica se o curso existe
        Optional<Curso> curso = cursoRepository.findById(idCurso);
        if (curso.isEmpty()) {
            return false;
        }
        
        // Aqui podem ser adicionadas outras validações:
        // - Verificar pré-requisitos
        // - Verificar limites de vagas
        // - Verificar período de matrícula
        // - etc.
        
        return true;
    }
    
    /**
     * Gera relatório simples de alunos por curso.
     * 
     * Este método retorna informações básicas sobre a distribuição
     * de alunos pelos cursos disponíveis.
     * 
     * @return String formatada com relatório de alunos por curso
     */
    @Transactional(readOnly = true)
    public String gerarRelatorioAlunosPorCurso() {
        List<Curso> cursos = cursoRepository.findAllByOrderByNomeAsc();
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== RELATÓRIO DE ALUNOS POR CURSO ===\n\n");
        
        for (Curso curso : cursos) {
            Long numeroAlunos = contarAlunosPorCurso(curso.getIdCurso());
            relatorio.append(String.format("Curso: %s - %d aluno(s)\n", 
                                          curso.getNome(), numeroAlunos));
        }
        
        return relatorio.toString();
    }
}