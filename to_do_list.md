## 1. Configuração do Ambiente de Desenvolvimento

- [x]  Configurar ambiente Docker local
- [x]  Criar estrutura de diretórios do projeto
- [x]  Configurar repositório Git para controle de versão
- [x]  Preparar ferramentas de desenvolvimento (IDE, extensões, etc.)

## 2. Banco de Dados

- [ ]  Implementar schema `auth_service` para autenticação e usuários
- [ ]  Implementar schema `worker_service` para gerenciamento de profissionais
- [ ]  Implementar schema `schedule_service` para agendamentos e salas
- [ ]  Configurar tabelas de auditoria para todos os serviços
- [ ]  Criar scripts de migração e inicialização do banco

## 3. Backend - Configuração e Infraestrutura

- [x]  Configurar estrutura modular para arquitetura hexagonal
- [x]  Preparar imagem Docker do WildFly com configurações específicas
    - [x]  Criar arquivo `module.xml` para driver PostgreSQL
    - [x]  Configurar `configure-wildfly.cli` para datasource
    - [x]  Criar Dockerfile para o servidor
- [ ]  Configurar ambiente de microsserviços
- [ ]  Implementar sistema de mensageria com RabbitMQ

## 4. Backend - Implementação dos Serviços
- [ ]  CRUDs
	- [ ] trabalhadores
	    - [ ]  profissional da saude
	    - [ ] profissional administrativo
	    - [ ] profissional operacional
	    - [ ] Clientes.
	- [x] Autentificação
		- [x] Usuários
	- [ ] Agendamentos
		- [ ] Grade horaria
		- [ ] consulta
- [ ]  Serviço de Autenticação e Autorização
    - [ ]  Implementar login e gestão de usuários
    - [x]  Configurar JWT para autenticação
    - [x]  Implementar controle de acesso baseado em papéis
    - [x] implementação de token nos cookies
    - [ ] Implementação de CSRF token
- [ ]  Serviço de Agenda e Consultas
    - [ ]  Gestão de salas e recursos
    - [ ]  Sistema de agendamento
    - [ ]  Gestão de consultas médicas

## 5. Frontend - Estrutura e Componentes

- [ ]  Configurar estrutura de projeto HTML/CSS/JavaScript
- [ ]  Implementar componentes reutilizáveis
    - [x]  Header/Navbar
    - [x]  Footer
    - [x] CSS layout geral
    - [ ] Componentes relacionados a segurança do site (login, trocar de senha, acesso de privado etc...)
    - [ ] Componentes relacionado a agendamentos e grade horaris
- [x]  Implementar camadas DOM, Controller e Service

## 6. Frontend - Páginas e Funcionalidades

- [x]  Página de login/autenticação
- [ ]  Páginas para profissionais da saúde
    - [ ]  Dashboard para médicos
    - [ ]  Visualização de agenda
    - [ ]  Registro de consultas
- [ ]  Páginas para profissionais administrativos
    - [ ]  Gerenciamento de agendamentos
    - [ ]  Cadastro de pacientes
- [ ]  Páginas para profissionais operacionais
- [ ]  Páginas para pacientes
- [ ]  Implementar segurança contra XSS e CSRF

## 7. Configuração de Servidores

- [x]  Configurar Nginx
    - [x]  Criar arquivo `nginx.conf`
    - [x]  Configurar proxy reverso para backend
    - [ ]  Configurar HTTPS com certificados
    - [ ]  Implementar regras de segurança
- [ ]  Configurar integração entre frontend e backend

## 8. Testes

- [ ]  Implementar testes unitários com JUnit e Mockito
- [ ]  Configurar testes de integração
- [ ]  Implementar testes de frontend
- [ ]  Configurar pipeline de CI/CD para testes automatizados

## 9. Monitoramento e Logs

- [ ]  Configurar Logback para logging estruturado
- [ ]  Implementar monitoramento de serviços
- [ ]  Configurar alertas para falhas no sistema

## 10. Documentação

- [ ]  Documentar APIs (endpoints, contratos, exemplos)
- [x]  Criar documentação técnica do sistema
- [ ]  Elaborar manual do usuário
- [ ]  Documentar procedimentos de deploy

## 11. Deploy

- [ ]  Configurar Docker Compose para ambiente de produção
- [ ]  Realizar deploy no ngronk para simulação

## 12. Finalização e Revisão

- [ ]  Realizar revisão de código
- [ ]  Verificar cumprimento de requisitos
- [ ]  Realizar testes finais de segurança
- [ ]  Entregar documentação completa do projeto


# DOING

Matheus 
	Implementar schema `auth_service` para autenticação e usuários
	Implementar schema `worker_service` para gerenciamento de profissionais
	Implementar schema `schedule_service` para agendamentos e salas
	Configurar tabelas de auditoria para todos os serviços
	Criar scripts de migração e inicialização do banco

Bruna
	 Páginas para profissionais da saúde
		Componente para buscar consultas.
		Componente para cancelar consulta.
		Componente para Iniciar consulta.
		Componente para buscar consultas próximas.