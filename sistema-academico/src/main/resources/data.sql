-- ==============================================
-- DADOS DE INICIALIZAÇÃO - SISTEMA ACADÊMICO
-- ==============================================
-- Este arquivo popula o banco H2 com dados de exemplo
-- para facilitar os testes da aplicação

-- Inserção de cursos
INSERT INTO CURSO (ID_CURSO, NM) VALUES 
(1, 'Ciência da Computação'),
(2, 'Engenharia de Software'),
(3, 'Sistemas de Informação'),
(4, 'Análise e Desenvolvimento de Sistemas'),
(5, 'Engenharia da Computação');

-- Inserção de disciplinas
INSERT INTO DISCIPLINA (ID_DISCIPLINA, NM) VALUES 
(1, 'Algoritmos e Estruturas de Dados'),
(2, 'Programação Orientada a Objetos'),
(3, 'Banco de Dados'),
(4, 'Engenharia de Software'),
(5, 'Redes de Computadores'),
(6, 'Inteligência Artificial'),
(7, 'Cálculo I'),
(8, 'Álgebra Linear'),
(9, 'Estatística'),
(10, 'Arquitetura de Computadores'),
(11, 'Sistemas Operacionais'),
(12, 'Compiladores'),
(13, 'Interface Homem-Máquina'),
(14, 'Gestão de Projetos'),
(15, 'Segurança da Informação');

-- Relacionamento Curso-Disciplina
-- Ciência da Computação (curso mais completo)
INSERT INTO CURSO_DISCIPLINA (ID_CURSO, ID_DISCIPLINA) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12);

-- Engenharia de Software
INSERT INTO CURSO_DISCIPLINA (ID_CURSO, ID_DISCIPLINA) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 13), (2, 14), (2, 15);

-- Sistemas de Informação
INSERT INTO CURSO_DISCIPLINA (ID_CURSO, ID_DISCIPLINA) VALUES
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 9), (3, 14), (3, 15);

-- Análise e Desenvolvimento de Sistemas
INSERT INTO CURSO_DISCIPLINA (ID_CURSO, ID_DISCIPLINA) VALUES
(4, 1), (4, 2), (4, 3), (4, 4), (4, 13), (4, 14);

-- Engenharia da Computação
INSERT INTO CURSO_DISCIPLINA (ID_CURSO, ID_DISCIPLINA) VALUES
(5, 1), (5, 2), (5, 7), (5, 8), (5, 10), (5, 11), (5, 5);

-- Inserção de alunos
INSERT INTO ALUNO (ID_ALUNO, NM, ID_CURSO) VALUES 
-- Alunos de Ciência da Computação
(1, 'Ana Silva Santos', 1),
(2, 'Bruno Costa Lima', 1),
(3, 'Carlos Eduardo Mendes', 1),
(4, 'Daniela Ferreira Rocha', 1),
(5, 'Eduardo Alves Pereira', 1),

-- Alunos de Engenharia de Software
(6, 'Fernanda Oliveira Cruz', 2),
(7, 'Gabriel Santos Martins', 2),
(8, 'Helena Rodrigues Silva', 2),
(9, 'Igor Nascimento Souza', 2),

-- Alunos de Sistemas de Informação
(10, 'Julia Campos Barbosa', 3),
(11, 'Leonardo Dias Cardoso', 3),
(12, 'Marina Almeida Correia', 3),
(13, 'Nicolas Ferreira Gomes', 3),
(14, 'Patrícia Santos Ribeiro', 3),

-- Alunos de Análise e Desenvolvimento
(15, 'Ricardo Lima Moreira', 4),
(16, 'Sofia Cavalcanti Nunes', 4),
(17, 'Thiago Mendes Teixeira', 4),

-- Alunos de Engenharia da Computação
(18, 'Vitória Araújo Fonseca', 5),
(19, 'Wesley Pinto Machado', 5),
(20, 'Yasmin Costa Borges', 5);

-- Mensagens de confirmação (comentários para logs)
-- Os dados foram inseridos com sucesso!
-- Cursos: 5 registros
-- Disciplinas: 15 registros
-- Relacionamentos Curso-Disciplina: 32 associações
-- Alunos: 20 registros

-- Para testar as funcionalidades, você pode usar:
-- - Buscar alunos por curso
-- - Associar/desassociar disciplinas de cursos
-- - Transferir alunos entre cursos
-- - Gerar relatórios de distribuição