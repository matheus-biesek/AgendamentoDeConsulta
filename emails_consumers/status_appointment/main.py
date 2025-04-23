import pika

def callback(ch, method, properties, body):
    print(f"Status recebido: {body.decode()}")

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

        queue_name = 'status-appointment'
        channel.queue_declare(queue=queue_name, durable=True)

        channel.basic_consume(
            queue=queue_name,
            on_message_callback=callback,
            auto_ack=True  # Mude para False se quiser ACK manual
        )

        print("Aguardando mensagens da fila 'status-appointment'...")
        channel.start_consuming()

    except pika.exceptions.AMQPConnectionError as e:
        print(f"Erro ao conectar ao RabbitMQ: {e}")

if __name__ == "__main__":
    main()
