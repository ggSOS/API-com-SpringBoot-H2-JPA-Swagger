package com.exemplo.sistemaacademico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa a tabela DISCIPLINA no banco de dados.
 * 
 * Esta classe modela as disciplinas que podem ser oferecidas pelos cursos.
 * Uma disciplina pode estar associada a múltiplos cursos, criando um 
 * relacionamento many-to-many através da tabela CURSO_DISCIPLINA.
 */
@Entity
@Table(name = "DISCIPLINA")
public class Disciplina {
    
    /**
     * Identificador único da disciplina.
     * Usa geração automática para facilitar a inserção de novos registros.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DISCIPLINA")
    private Long idDisciplina;
    
    /**
     * Nome da disciplina.
     * Campo obrigatório que identifica a disciplina de forma única para o usuário.
     */
    @NotBlank(message = "O nome da disciplina é obrigatório")
    @Size(max = 80, message = "O nome da disciplina deve ter no máximo 80 caracteres")
    @Column(name = "NM", nullable = false, length = 80)
    private String nome;
    
    /**
     * Relacionamento many-to-many com Curso.
     * Uma disciplina pode ser oferecida em vários cursos.
     * O mappedBy indica que a entidade Curso é responsável pelo mapeamento.
     * JsonBackReference evita loops infinitos na serialização, complementando
     * o JsonManagedReference na entidade Curso.
     */
    @ManyToMany(mappedBy = "disciplinas")
    @JsonBackReference
    private Set<Curso> cursos = new HashSet<>();
    
    // Construtores
    public Disciplina() {}
    
    public Disciplina(String nome) {
        this.nome = nome;
    }
    
    // Getters e Setters
    public Long getIdDisciplina() {
        return idDisciplina;
    }
    public void setIdDisciplina(Long idDisciplina) {
        this.idDisciplina = idDisciplina;
    }
    
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Set<Curso> getCursos() {
        return cursos;
    }
    public void setCursos(Set<Curso> cursos) {
        this.cursos = cursos;
    }
    
    /**
     * Métodos utilitários para gerenciar relacionamentos.
     * Estes métodos mantêm a integridade dos relacionamentos bidirecionais,
     * garantindo que ambos os lados sejam sempre atualizados corretamente.
     */
    public void adicionarCurso(Curso curso) {
        this.cursos.add(curso);
        curso.getDisciplinas().add(this);
    }
    public void removerCurso(Curso curso) {
        this.cursos.remove(curso);
        curso.getDisciplinas().remove(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disciplina)) return false;
        Disciplina disciplina = (Disciplina) o;
        return idDisciplina != null && idDisciplina.equals(disciplina.idDisciplina);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Disciplina{" +
                "idDisciplina=" + idDisciplina +
                ", nome='" + nome + '\'' +
                '}';
    }
}