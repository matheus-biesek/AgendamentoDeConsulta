import pika
import logging
import time
import os
from email_service import EmailService

# Configuração do logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger()

# Configurações do serviço de email
SMTP_SERVER = os.getenv('SMTP_SERVER', 'smtp.gmail.com')
SMTP_PORT = int(os.getenv('SMTP_PORT', '587'))
SMTP_USERNAME = os.getenv('SMTP_USERNAME', '')
SMTP_PASSWORD = os.getenv('SMTP_PASSWORD', '')

# Inicialização do serviço de email
email_service = EmailService(
    smtp_server=SMTP_SERVER,
    smtp_port=SMTP_PORT,
    username=SMTP_USERNAME,
    password=SMTP_PASSWORD
)

def callback(ch, method, properties, body):
    """
    Função de callback que processa as mensagens recebidas do RabbitMQ.
    
    Args:
        ch: Canal do RabbitMQ
        method: Método de entrega
        properties: Propriedades da mensagem
        body: Corpo da mensagem
    """
    try:
        # Decodifica a mensagem
        message = body.decode()
        logger.info(f"Mensagem recebida: {message}")
        
        # Processa a mensagem JSON
        data = email_service.parse_message(message)
        
        # Envia o email
        if 'email' in data and 'message' in data:
            success = email_service.send_email(data['email'], data['message'])
            if success:
                logger.info(f"Email enviado com sucesso para {data['email']}")
            else:
                logger.error(f"Falha ao enviar email para {data['email']}")
        else:
            logger.error("Mensagem inválida: campos 'email' ou 'message' não encontrados")
            
    except Exception as e:
        logger.error(f"Erro ao processar mensagem: {str(e)}")

def main():
    """
    Função principal que configura e inicia o consumer do RabbitMQ.
    """
    while True:
        try:
            connection = pika.BlockingConnection(pika.ConnectionParameters(
                host='rabbitmq',
                port=5672,
                credentials=pika.PlainCredentials('guest', 'guest'),
                virtual_host='/',
                connection_attempts=3,
                retry_delay=5
            ))
            logger.info("Conectado ao RabbitMQ com sucesso!")
            channel = connection.channel()

            queue_name = 'status-appointment'
            channel.queue_declare(queue=queue_name, durable=True)

            channel.basic_consume(
                queue=queue_name,
                on_message_callback=callback,
                auto_ack=True
            )

            logger.info("Aguardando mensagens. Pressione CTRL+C para sair.")
            channel.start_consuming()

        except pika.exceptions.AMQPConnectionError as e:
            logger.error(f"Erro ao conectar ao RabbitMQ: {e}. Tentando novamente...")
            time.sleep(5)

if __name__ == "__main__":
    main()
