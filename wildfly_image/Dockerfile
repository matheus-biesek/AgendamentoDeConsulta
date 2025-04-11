FROM quay.io/wildfly/wildfly:35.0.1.Final-jdk17

# -------------------------------------
# Configuração de usuário administrativo
# -------------------------------------
RUN /opt/jboss/wildfly/bin/add-user.sh \
    usuario 1234 --silent

# -------------------------------------
# Adiciona o driver JDBC do PostgreSQL
# -------------------------------------
ENV PGSQL_MODULE_PATH=/opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main

RUN mkdir -p ${PGSQL_MODULE_PATH}

COPY postgresql-42.7.2.jar ${PGSQL_MODULE_PATH}/
COPY module.xml ${PGSQL_MODULE_PATH}/

# Copia o script .cli
COPY configure-wildfly.cli /opt/jboss/wildfly/

# Executa o script CLI
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/jboss/wildfly/configure-wildfly.cli