import pika

def callback(ch, method, properties, body):
    print(f"Mensagem recebida: {body.decode()}")

def main():
    try:
        connection = pika.BlockingConnection(pika.ConnectionParameters(
            host='rabbitmq',
            port=5672,
            credentials=pika.PlainCredentials('guest', 'guest'),
            virtual_host='/',
            connection_attempts=3,
            retry_delay=5
        ))
        print("Conectado ao RabbitMQ com sucesso!")
        channel = connection.channel()

        # Declara a fila que deseja consumir (ela deve existir ou ser criada aqui)
        queue_name = 'change-password'
        channel.queue_declare(queue=queue_name, durable=True)

        # Define qual função será chamada ao receber uma mensagem
        channel.basic_consume(
            queue=queue_name,
            on_message_callback=callback,
            auto_ack=True  # Pode mudar para False se quiser controlar o ack manualmente
        )

        print("Aguardando mensagens. Pressione CTRL+C para sair.")
        channel.start_consuming()  # Isso mantém o processo rodando

    except pika.exceptions.AMQPConnectionError as e:
        print(f"Erro ao conectar ao RabbitMQ: {e}")

if __name__ == "__main__":
    main()
