# 🏥 Sistema de Gestão para Clínica Oftalmológica

Este repositório contém o desenvolvimento de um **sistema web modular** para gerenciamento de uma clínica oftalmológica, com foco em escalabilidade, segurança e organização baseada em boas práticas arquiteturais como **DDD (Domain-Driven Design)**, **Clean Architecture** e **microsserviços**.

> 🚧 **Projeto em desenvolvimento**. Esta documentação descreve a estrutura proposta e os objetivos do sistema.

---

## 🧱 Arquitetura da Aplicação

### 🌐 **Frontend – Interface do Usuário**

- Desenvolvido com **HTML, CSS e JavaScript puro**, sem frameworks SPA.
- Componentização reutilizável e organização por camadas:
  - `DOM`, `Controller`, `Service`, `Component`.
- Layout com `navbar`, `main` e `footer`, controlados por um CSS modular.
- Inspirado na Clean Architecture, adaptado a aplicações front-end não-SPA.
- Segurança:
  - Proteção contra **XSS**: nenhuma entrada de usuário é renderizada como HTML.
  - Proteção contra **CSRF**: cookies `SameSite=Strict`, `HttpOnly`, `Secure`, com **CSRF Token** incluído.
- Páginas privadas realizam **proxy reverso via Nginx** para validação de sessão com o backend.
- Estrutura de páginas:
  - Login/autenticação
  - Acesso específico por perfil: profissionais da saúde, administrativos, operacionais e pacientes.
- O frontend é **dockerizado** e serve estáticamente via **Nginx**.

---

### 🧠 **Backend – Núcleo da Aplicação**

- Baseado em **Arquitetura Hexagonal (Ports & Adapters)**.
- Dividido em três serviços principais:
  1. **Autenticação e Autorização**
  2. **Gestão de Profissionais**
  3. **Agendamentos e Consultas**
- Cada serviço roda em instâncias independentes com **comunicação via RabbitMQ** (assíncrona, orientada a eventos).
- Utiliza **Service Layer** para centralização das regras de negócio.
- Exposição de APIs RESTful com autenticação via **JWT**.
- Segurança de CORS configurada para permitir apenas chamadas do frontend autorizado.
- Cada serviço é **dockerizado**, rodando sobre **Wildfly** com configurações personalizadas de datasource PostgreSQL.

---

## 🧬 Banco de Dados – Organização por Domínio

O banco de dados é estruturado em **schemas independentes**, representando diferentes domínios do negócio, permitindo futura migração para bancos separados (multibanco).

### 🛡️ `auth_service`
- Gerencia usuários, senhas, funções (`ADMIN`, `SECRETARY`, `DOCTOR`, etc).
- Tabelas de auditoria para rastreamento de ações sensíveis.

### 🧑‍⚕️ `worker_service`
- Armazena dados dos profissionais (saúde e administrativos).
- Relacionado ao domínio de autenticação apenas via ID, sem FKs cruzadas.
- Especializações médicas e registros profissionais são tratados separadamente.

### 🗓️ `schedule_service`
- Gerencia salas, agendamentos, consultas e vínculos com pacientes.
- Auditoria completa das entidades de agendamento e atendimento.

---

## 🏗️ Visão Arquitetural e Escalabilidade

- Separação de contextos baseada em **DDD e Bounded Contexts**.
- Comunicação entre serviços por **DTOs e APIs**, evitando acoplamento direto entre bancos.
- Uso de **tabelas de auditoria** para rastreabilidade.
- Preparado para evolução futura em uma arquitetura full microsserviços.

---

## 🐳 Deploy Backend com Wildfly + Docker

### 📁 Estrutura de diretórios:
```
docker-image/
├── Dockerfile
├── configure-wildfly.cli
├── module.xml
├── postgresql-42.7.2.jar
```

### ⚙️ Arquivos importantes:

#### `configure-wildfly.cli`
- Remove H2 e adiciona driver PostgreSQL.
- Cria datasource `PostgresDS` com base no container do banco.

#### `module.xml`
- Define dependências do driver JDBC PostgreSQL.

#### `Dockerfile`
- Baseado na imagem Wildfly oficial.
- Adiciona usuário admin.
- Copia driver, XML e executa script `.cli`.

---

## 🔐 Segurança e Boas Práticas

- Isolamento de contexto por schema e serviço.
- Camadas bem definidas no backend para separação de responsabilidades.
- Comunicação segura e assíncrona com RabbitMQ.
- API segura com JWT e controle de CORS.
- Nenhuma renderização de HTML baseado em input do usuário no frontend.

---

## 📌 Objetivo do Projeto

Construir um sistema profissional, escalável e seguro para gestão de uma clínica oftalmológica, servindo como base para evolução futura em soluções baseadas em **microserviços**, **alta disponibilidade** e **experiência modular** tanto para pacientes quanto profissionais da saúde.

