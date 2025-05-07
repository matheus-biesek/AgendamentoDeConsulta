import pika
import logging
import time

# Configuração do logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger()

def callback(ch, method, properties, body):
    logger.info(f"Mensagem recebida: {body.decode()}")

def main():
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
