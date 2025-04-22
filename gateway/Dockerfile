# Usar uma imagem oficial do Node.js como base
FROM node:23-slim

# Criar e definir o diretório de trabalho dentro do container
WORKDIR /usr/src/app

# Copiar os arquivos do projeto para dentro do container
COPY . .

# Instalar as dependências
RUN npm install

# Expor a porta em que o app vai rodar
EXPOSE 3000

# Rodar o arquivo gateway.js quando o container iniciar
CMD [ "node", "gateway.js" ]
