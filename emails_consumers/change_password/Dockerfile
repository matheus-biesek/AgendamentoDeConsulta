FROM python:3.13-alpine

# Defina o diretório de trabalho
WORKDIR /app

# Copie o requirements.txt para o container
COPY requirements.txt .

# Instale as dependências do projeto
RUN pip install --no-cache-dir -r requirements.txt

# Copie o código da aplicação para dentro do container
COPY . .

# Defina o comando para rodar o consumer
CMD ["python", "main.py"]
