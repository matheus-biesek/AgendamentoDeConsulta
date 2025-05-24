import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import json
import logging

logger = logging.getLogger()

class EmailService:
    """
    Classe responsável por gerenciar o envio de emails.
    """
    def __init__(self, smtp_server: str, smtp_port: int, username: str, password: str):
        """
        Inicializa o serviço de email com as configurações do servidor SMTP.
        
        Args:
            smtp_server (str): Endereço do servidor SMTP
            smtp_port (int): Porta do servidor SMTP
            username (str): Nome de usuário para autenticação
            password (str): Senha para autenticação
        """
        self.smtp_server = smtp_server
        self.smtp_port = smtp_port
        self.username = username
        self.password = password

    def send_email(self, to_email: str, message: str) -> bool:
        """
        Envia um email para o destinatário especificado.
        
        Args:
            to_email (str): Email do destinatário
            message (str): Mensagem a ser enviada
            
        Returns:
            bool: True se o email foi enviado com sucesso, False caso contrário
        """
        try:
            # Criando a mensagem
            msg = MIMEMultipart()
            msg['From'] = self.username
            msg['To'] = to_email
            msg['Subject'] = "Atualização de Status da Consulta"

            # Adicionando o corpo da mensagem
            msg.attach(MIMEText(message, 'plain'))

            # Conectando ao servidor SMTP
            with smtplib.SMTP(self.smtp_server, self.smtp_port) as server:
                server.starttls()
                server.login(self.username, self.password)
                server.send_message(msg)

            logger.info(f"Email enviado com sucesso para {to_email}")
            return True

        except Exception as e:
            logger.error(f"Erro ao enviar email para {to_email}: {str(e)}")
            return False

    @staticmethod
    def parse_message(message: str) -> dict:
        """
        Converte a mensagem JSON recebida do RabbitMQ em um dicionário.
        
        Args:
            message (str): Mensagem JSON recebida
            
        Returns:
            dict: Dicionário contendo os dados da mensagem
        """
        try:
            return json.loads(message)
        except json.JSONDecodeError as e:
            logger.error(f"Erro ao decodificar mensagem JSON: {str(e)}")
            raise 