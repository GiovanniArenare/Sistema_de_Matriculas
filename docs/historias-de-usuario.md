# Histórias de Usuário — Sistema de Matrículas Universitárias

## 1. Objetivo

Este documento descreve as histórias de usuário do Sistema de Matrículas Universitárias, com base no enunciado do laboratório e no diagrama de casos de uso. As histórias orientam a implementação do protótipo com Java 25, Spring Boot 4.0.x, React + Vite e MongoDB Atlas.

## 2. Atores

- **Aluno:** consulta ofertas de disciplinas, realiza e cancela matrículas e acompanha suas inscrições.
- **Professor:** consulta as disciplinas atribuídas e os alunos matriculados.
- **Secretaria:** mantém dados acadêmicos, currículos e períodos de matrícula.
- **Sistema de Cobranças:** sistema externo notificado após a inscrição do aluno no semestre.

## 3. Histórias de usuário

### HU-01 — Realizar login

**Ator:** Aluno, Professor e Secretaria.

**História:** Como usuário do sistema, quero entrar com meu usuário e senha para acessar as funcionalidades autorizadas ao meu perfil.

**Critérios de aceitação:**
- O sistema solicita identificação de usuário e senha.
- Credenciais inválidas não permitem o acesso.
- Credenciais válidas permitem acessar somente as funcionalidades do perfil autenticado.
- As senhas não são armazenadas em texto puro.

### HU-02 — Consultar disciplinas disponíveis

**Ator:** Aluno.

**História:** Como aluno, quero consultar as disciplinas ofertadas para o semestre para decidir em quais desejo me inscrever.

**Critérios de aceitação:**
- O aluno autenticado visualiza as ofertas do semestre correspondente.
- O sistema apresenta identificação da disciplina e quantidade de vagas disponíveis.
- Ofertas que atingiram 60 inscrições são identificadas como sem vagas.

### HU-03 — Realizar matrícula

**Ator:** Aluno.

**História:** Como aluno, quero confirmar minha inscrição nas disciplinas selecionadas para participar do próximo semestre.

**Critérios de aceitação:**
- A inscrição só é permitida durante o período de matrícula aberto.
- O sistema verifica os limites de seleção e de vagas antes de confirmar.
- O aluno não pode se inscrever duas vezes na mesma oferta.
- As inscrições confirmadas ficam persistidas e podem ser consultadas.
- Após a inscrição no semestre, o sistema registra e envia a notificação ao Sistema de Cobranças.

### HU-04 — Selecionar disciplinas de primeira opção

**Ator:** Aluno.

**História:** Como aluno, quero selecionar até quatro disciplinas de primeira opção para definir minhas escolhas prioritárias no semestre.

**Critérios de aceitação:**
- O sistema permite selecionar no máximo quatro disciplinas de primeira opção.
- Uma tentativa de ultrapassar esse limite é rejeitada com mensagem explicativa.
- A seleção respeita o período de matrícula e a disponibilidade de vagas.

### HU-05 — Selecionar disciplinas alternativas

**Ator:** Aluno.

**História:** Como aluno, quero indicar até duas disciplinas alternativas para registrar outras opções de estudo no semestre.

**Critérios de aceitação:**
- O sistema permite selecionar no máximo duas disciplinas alternativas.
- Uma tentativa de ultrapassar esse limite é rejeitada com mensagem explicativa.
- A mesma oferta não pode constar simultaneamente como primeira opção e alternativa do mesmo aluno.
- O sistema identifica claramente quais inscrições são alternativas.

### HU-06 — Cancelar matrícula

**Ator:** Aluno.

**História:** Como aluno, quero cancelar uma matrícula realizada anteriormente para alterar minhas escolhas durante o período permitido.

**Critérios de aceitação:**
- O cancelamento só é permitido enquanto o período de matrícula estiver aberto.
- O aluno só pode cancelar suas próprias inscrições.
- A inscrição cancelada deixa de ocupar vaga na oferta.
- O sistema atualiza a consulta de matrículas após o cancelamento.

### HU-07 — Consultar matrículas

**Ator:** Aluno.

**História:** Como aluno, quero visualizar minhas matrículas para acompanhar as disciplinas escolhidas para o semestre.

**Critérios de aceitação:**
- O aluno autenticado consulta apenas suas próprias inscrições.
- Cada inscrição mostra a disciplina, o tipo de opção e sua situação.
- O sistema diferencia inscrições confirmadas, canceladas e ofertas posteriormente canceladas.

### HU-08 — Consultar disciplinas atribuídas

**Ator:** Professor.

**História:** Como professor, quero consultar as disciplinas que me foram atribuídas para conhecer minhas atividades acadêmicas no semestre.

**Critérios de aceitação:**
- O professor autenticado visualiza as ofertas vinculadas a ele.
- Cada oferta apresenta a disciplina, o semestre e a situação da oferta.
- O professor não recebe acesso administrativo por meio dessa consulta.

### HU-09 — Visualizar alunos matriculados

**Ator:** Professor.

**História:** Como professor, quero visualizar os alunos matriculados em cada disciplina sob minha responsabilidade para acompanhar a composição das turmas.

**Critérios de aceitação:**
- O professor seleciona uma oferta atribuída a ele.
- O sistema exibe a lista de alunos efetivamente matriculados nessa oferta.
- O professor não pode consultar a lista de uma oferta que não lhe foi atribuída.
- Inscrições canceladas não aparecem como matrículas ativas.

### HU-10 — Gerenciar alunos

**Ator:** Secretaria.

**História:** Como funcionário da secretaria, quero cadastrar, consultar e atualizar informações dos alunos para manter os registros acadêmicos organizados.

**Critérios de aceitação:**
- Apenas a secretaria pode utilizar as operações administrativas.
- É possível cadastrar, listar, consultar e atualizar dados de alunos.
- O sistema valida os campos obrigatórios e evita identificações acadêmicas duplicadas.

### HU-11 — Gerenciar professores

**Ator:** Secretaria.

**História:** Como funcionário da secretaria, quero cadastrar, consultar e atualizar informações dos professores para manter o corpo docente registrado.

**Critérios de aceitação:**
- Apenas a secretaria pode utilizar as operações administrativas.
- É possível cadastrar, listar, consultar e atualizar dados de professores.
- O sistema permite associar professores às ofertas de disciplinas.

### HU-12 — Gerenciar cursos

**Ator:** Secretaria.

**História:** Como funcionário da secretaria, quero gerenciar cursos para registrar seus nomes, créditos e disciplinas integrantes.

**Critérios de aceitação:**
- É possível cadastrar, consultar e atualizar cursos.
- Cada curso possui nome e quantidade de créditos definidos.
- O sistema permite associar disciplinas ao curso.
- Apenas a secretaria pode modificar essas informações.

### HU-13 — Gerenciar disciplinas

**Ator:** Secretaria.

**História:** Como funcionário da secretaria, quero gerenciar disciplinas e suas ofertas para disponibilizá-las nos semestres acadêmicos.

**Critérios de aceitação:**
- É possível cadastrar, consultar e atualizar disciplinas.
- O sistema permite criar ofertas de disciplinas para um semestre.
- Cada oferta identifica a disciplina e seu semestre.
- O limite de 60 alunos é aplicado por oferta, e não ao cadastro permanente da disciplina.

### HU-14 — Gerenciar currículo semestral

**Ator:** Secretaria.

**História:** Como funcionário da secretaria, quero organizar o currículo de cada semestre para definir as disciplinas oferecidas aos alunos.

**Critérios de aceitação:**
- A secretaria consegue criar e consultar a organização curricular de um semestre.
- O currículo relaciona cursos e disciplinas ofertadas.
- As ofertas do semestre ficam disponíveis para consulta pelos alunos conforme as regras do sistema.

### HU-15 — Gerenciar períodos de matrícula

**Ator:** Secretaria.

**História:** Como funcionário da secretaria, quero definir as datas de início e fim das matrículas para controlar quando os alunos podem se inscrever ou cancelar inscrições.

**Critérios de aceitação:**
- A secretaria informa o semestre e as datas de abertura e encerramento.
- O sistema não aceita um encerramento anterior à abertura.
- Fora do período aberto, novas matrículas e cancelamentos são bloqueados.
- Os alunos conseguem identificar se o período está aberto ou encerrado.

### HU-16 — Encerrar período de matrícula

**Ator:** Secretaria.

**História:** Como funcionário da secretaria, quero encerrar o período de matrículas e apurar as inscrições para determinar quais ofertas ocorrerão no semestre seguinte.

**Critérios de aceitação:**
- O encerramento impede novas inscrições e cancelamentos naquele período.
- O sistema conta as matrículas válidas de cada oferta.
- Ofertas com menos de três alunos matriculados são canceladas.
- Ofertas com três ou mais alunos permanecem ativas, respeitado o máximo de 60.
- A situação final de cada oferta fica disponível para consulta.

## 4. Regras de negócio consolidadas

| ID | Regra |
| --- | --- |
| RN-01 | Um aluno pode selecionar até 4 disciplinas de primeira opção por semestre. |
| RN-02 | Um aluno pode selecionar até 2 disciplinas alternativas por semestre. |
| RN-03 | Matrículas e cancelamentos só podem ocorrer durante o período autorizado. |
| RN-04 | Uma oferta de disciplina aceita no máximo 60 alunos inscritos. |
| RN-05 | Ao encerrar o período, ofertas com menos de 3 alunos inscritos são canceladas. |
| RN-06 | Após a inscrição no semestre, o Sistema de Cobranças deve ser notificado. |
| RN-07 | Professores podem consultar os alunos matriculados nas próprias disciplinas. |
| RN-08 | Alunos, professores e secretaria devem realizar login com senha válida. |

## 5. Pontos a confirmar com o Product Owner

Os itens abaixo não estão detalhados no enunciado e devem ser decididos antes da implementação definitiva:

- As quatro disciplinas de primeira opção são obrigatoriamente preenchidas ou representam apenas um limite máximo?
- As duas disciplinas alternativas ocupam vagas imediatamente ou são utilizadas como substituição caso alguma primeira opção não ocorra?
- Quando uma oferta é cancelada por ter menos de três alunos, como as inscrições e a cobrança correspondente são ajustadas?
- Como o sistema de cobranças recebe a notificação (API HTTP, fila ou outro mecanismo)?
- O encerramento ocorre automaticamente na data final ou é confirmado pela secretaria?

## 6. Rastreabilidade

As histórias HU-01 a HU-16 correspondem aos casos de uso principais do diagrama `docs/diagramas/casos-de-uso.puml`. As verificações de período, vagas e quantidade mínima de inscritos, assim como a notificação ao Sistema de Cobranças, aparecem como critérios de aceitação e regras de negócio das histórias relacionadas.
