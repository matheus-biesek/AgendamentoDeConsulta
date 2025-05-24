import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".create-schedule-template-component");
    
    component.innerHTML = `
        <div class="create-schedule-template-container">
            <h2>Criar Template de Horários</h2>
            <form id="create-schedule-template-form">
                <div class="template-form-group">
                    <label for="template-user-id">ID do Usuário:</label>
                    <input type="text" id="template-user-id" name="template-user-id" required>
                </div>
                <div class="template-form-group">
                    <label for="template-room-id">ID da Sala:</label>
                    <input type="text" id="template-room-id" name="template-room-id" required>
                </div>
                <div class="template-form-group">
                    <label for="template-day-of-week">Dia da Semana:</label>
                    <select id="template-day-of-week" name="template-day-of-week" required>
                        <option value="">Selecione o dia</option>
                        <option value="MONDAY">Segunda-feira</option>
                        <option value="TUESDAY">Terça-feira</option>
                        <option value="WEDNESDAY">Quarta-feira</option>
                        <option value="THURSDAY">Quinta-feira</option>
                        <option value="FRIDAY">Sexta-feira</option>
                        <option value="SATURDAY">Sábado</option>
                        <option value="SUNDAY">Domingo</option>
                    </select>
                </div>
                <div class="template-form-group">
                    <label for="template-start-time">Horário de Início:</label>
                    <input type="time" id="template-start-time" name="template-start-time" required>
                </div>
                <div class="template-form-group">
                    <label for="template-end-time">Horário de Término:</label>
                    <input type="time" id="template-end-time" name="template-end-time" required>
                </div>
                <div class="template-form-group">
                    <label for="template-slot-duration">Duração do Intervalo (minutos):</label>
                    <input type="number" id="template-slot-duration" name="template-slot-duration" min="1" required>
                </div>
                <button type="submit" class="template-btn">Criar Template</button>
            </form>
            <div id="template-message"></div>
        </div>
    `;

    const form = document.getElementById('create-schedule-template-form');
    const messageDiv = document.getElementById('template-message');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `template-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'template-message';
        }, 5000);
    }

    function formatTime(time) {
        // Garante que o horário esteja no formato HH:mm:ss
        return time + ':00';
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const userId = document.getElementById('template-user-id').value.trim();
        const roomId = document.getElementById('template-room-id').value.trim();
        const dayOfWeek = document.getElementById('template-day-of-week').value;
        const startTime = document.getElementById('template-start-time').value;
        const endTime = document.getElementById('template-end-time').value;
        const slotDurationMinutes = document.getElementById('template-slot-duration').value;

        if (!userId || !roomId || !dayOfWeek || !startTime || !endTime || !slotDurationMinutes) {
            showMessage('Por favor, preencha todos os campos.', true);
            return;
        }

        // Validação de horários
        if (startTime >= endTime) {
            showMessage('O horário de início deve ser anterior ao horário de término.', true);
            return;
        }

        try {
            const data = {
                roomId,
                userId,
                dayOfWeek,
                startTime: formatTime(startTime),
                endTime: formatTime(endTime),
                slotDurationMinutes: parseInt(slotDurationMinutes)
            };

            console.log('Dados enviados:', JSON.stringify(data, null, 2));
            const response = await makeRequest('/rest-schedule/schedule-template/create', 'POST', data);
            
            // Se a resposta for uma string vazia ou não for um JSON válido, mas a requisição foi bem-sucedida
            if (!response || response === '') {
                showMessage('Template criado com sucesso!');
                form.reset();
                return;
            }

            // Se a resposta for um JSON válido
            if (response.message) {
                showMessage(response.message);
            } else {
                showMessage('Template criado com sucesso!');
            }
            form.reset();
        } catch (error) {
            console.error('Erro ao criar template:', error);
            // Se o erro for de parsing JSON mas a entidade foi criada
            if (error.message && error.message.includes('JSON.parse')) {
                showMessage('Template criado com sucesso!');
                form.reset();
            } else {
                showMessage(error.message || 'Erro ao criar template. Tente novamente.', true);
            }
        }
    });
});
