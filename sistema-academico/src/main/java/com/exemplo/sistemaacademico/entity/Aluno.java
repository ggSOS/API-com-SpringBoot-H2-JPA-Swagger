package com.exemplo.sistemaacademico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Entidade que representa a tabela ALUNO no banco de dados.
 * 
 * Esta classe modela os estudantes matriculados na instituição.
 * Cada aluno deve estar associado a exatamente um curso, estabelecendo
 * um relacionamento many-to-one com a entidade Curso.
 */
@Entity
@Table(name = "ALUNO")
public class Aluno {
    
    /**
     * Identificador único do aluno.
     * Funciona como número de matrícula do estudante no sistema.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALUNO")
    private Long idAluno;
    
    /**
     * Nome completo do aluno.
     * Campo obrigatório para identificação do estudante.
     */
    @NotBlank(message = "O nome do aluno é obrigatório")
    @Size(max = 80, message = "O nome do aluno deve ter no máximo 80 caracteres")
    @Column(name = "NM", nullable = false, length = 80)
    private String nome;
    
    /**
     * Relacionamento many-to-one com Curso.
     * Cada aluno deve estar matriculado em exatamente um curso.
     * A anotação JoinColumn especifica a chave estrangeira na tabela ALUNO.
     * JsonBackReference evita loops infinitos na serialização JSON quando
     * um curso é serializado com sua lista de alunos.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CURSO", nullable = false)
    @JsonBackReference
    private Curso curso;
    
    // Construtores
    public Aluno() {}
    
    public Aluno(String nome) {
        this.nome = nome;
    }
    
    public Aluno(String nome, Curso curso) {
        this.nome = nome;
        this.curso = curso;
    }
    
    // Getters e Setters
    public Long getIdAluno() {
        return idAluno;
    }
    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
    }
    
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Curso getCurso() {
        return curso;
    }
    public void setCurso(Curso curso) {
        this.curso = curso;
    }
    
    /**
     * Método utilitário para facilitar a matrícula do aluno em um curso.
     * Este método garante que o relacionamento bidirecional seja mantido
     * consistente, adicionando o aluno à lista de alunos do curso.
     */
    public void matricularEmCurso(Curso curso) {
        // Remove o aluno do curso anterior, se houver
        if (this.curso != null) {
            this.curso.getAlunos().remove(this);
        }
        // Estabelece o novo relacionamento
        this.curso = curso;
        if (curso != null) {
            curso.getAlunos().add(this);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aluno)) return false;
        Aluno aluno = (Aluno) o;
        return idAluno != null && idAluno.equals(aluno.idAluno);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Aluno{" +
                "idAluno=" + idAluno +
                ", nome='" + nome + '\'' +
                ", curso=" + (curso != null ? curso.getNome() : "null") +
                '}';
    }
}