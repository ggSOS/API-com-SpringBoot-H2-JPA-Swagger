package com.exemplo.sistemaacademico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa a tabela CURSO no banco de dados.
 * 
 * Esta classe mapeia os cursos disponíveis na instituição de ensino.
 * Cada curso pode ter várias disciplinas associadas através da tabela
 * de relacionamento CURSO_DISCIPLINA, e também pode ter vários alunos matriculados.
 */
@Entity
@Table(name = "CURSO")
public class Curso {
    
    /**
     * Identificador único do curso.
     * Utiliza geração automática de valores para simplificar a inserção de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CURSO")
    private Long idCurso;
    
    /**
     * Nome do curso.
     * Campo obrigatório com validação de tamanho conforme especificação do banco.
     */
    @NotBlank(message = "O nome do curso é obrigatório")
    @Size(max = 80, message = "O nome do curso deve ter no máximo 80 caracteres")
    @Column(name = "NM", nullable = false, length = 80)
    private String nome;
    
    /**
     * Relacionamento many-to-many com Disciplina.
     * Um curso pode ter várias disciplinas, e uma disciplina pode pertencer a vários cursos.
     * O JsonManagedReference evita loops infinitos na serialização JSON.
     */
    @ManyToMany
    @JoinTable(
        name = "CURSO_DISCIPLINA",
        joinColumns = @JoinColumn(name = "ID_CURSO"),
        inverseJoinColumns = @JoinColumn(name = "ID_DISCIPLINA")
    )
    @JsonManagedReference
    private Set<Disciplina> disciplinas = new HashSet<>();
    
    /**
     * Relacionamento one-to-many com Aluno.
     * Um curso pode ter vários alunos matriculados.
     * O mapeamento é bidirecional, permitindo navegar do curso para os alunos e vice-versa.
     */
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Aluno> alunos = new HashSet<>();
    
    // Construtores
    public Curso() {}
    
    public Curso(String nome) {
        this.nome = nome;
    }
    
    // Getters e Setters
    public Long getIdCurso() {
        return idCurso;
    }
    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }
    
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Set<Disciplina> getDisciplinas() {
        return disciplinas;
    }
    public void setDisciplinas(Set<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }
    
    public Set<Aluno> getAlunos() {
        return alunos;
    }
    public void setAlunos(Set<Aluno> alunos) {
        this.alunos = alunos;
    }
    
    /**
     * Métodos utilitários para gerenciar relacionamentos de forma consistente.
     * Estes métodos garantem que ambos os lados do relacionamento sejam atualizados.
     */
    public void adicionarDisciplina(Disciplina disciplina) {
        this.disciplinas.add(disciplina);
        disciplina.getCursos().add(this);
    }
    public void removerDisciplina(Disciplina disciplina) {
        this.disciplinas.remove(disciplina);
        disciplina.getCursos().remove(this);
    }
    
    public void adicionarAluno(Aluno aluno) {
        this.alunos.add(aluno);
        aluno.setCurso(this);
    }
    public void removerAluno(Aluno aluno) {
        this.alunos.remove(aluno);
        aluno.setCurso(null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso)) return false;
        Curso curso = (Curso) o;
        return idCurso != null && idCurso.equals(curso.idCurso);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Curso{" +
                "idCurso=" + idCurso +
                ", nome='" + nome + '\'' +
                '}';
    }
}