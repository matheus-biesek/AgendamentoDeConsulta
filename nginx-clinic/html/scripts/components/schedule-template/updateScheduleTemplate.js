import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".update-schedule-template-by-user-id-component");
    
    component.innerHTML = `
        <div class="update-template-container">
            <h2>Atualizar Template de Agendamento</h2>
            <form id="update-schedule-template-search-form">
                <div class="update-form-group">
                    <label for="update-schedule-template-search-id">ID do Template:</label>
                    <input type="text" id="update-schedule-template-search-id" name="update-schedule-template-search-id" required>
                </div>
                <button type="submit" class="update-btn">Buscar Template</button>
            </form>
            <div id="update-schedule-template-message"></div>
            <div id="update-schedule-template-details" class="template-details" style="display: none;">
                <h3>Detalhes do Template</h3>
                <form id="update-schedule-template-details-form">
                    <input type="hidden" id="update-schedule-template-id">
                    <div class="update-form-group">
                        <label for="update-schedule-template-room-id">ID da Sala:</label>
                        <input type="text" id="update-schedule-template-room-id" name="update-schedule-template-room-id" required>
                    </div>
                    <div class="update-form-group">
                        <label for="update-schedule-template-user-id">ID do Usuário:</label>
                        <input type="text" id="update-schedule-template-user-id" name="update-schedule-template-user-id" required>
                    </div>
                    <div class="update-form-group">
                        <label for="update-schedule-template-day">Dia da Semana:</label>
                        <select id="update-schedule-template-day" name="update-schedule-template-day" required>
                            <option value="MONDAY">Segunda-feira</option>
                            <option value="TUESDAY">Terça-feira</option>
                            <option value="WEDNESDAY">Quarta-feira</option>
                            <option value="THURSDAY">Quinta-feira</option>
                            <option value="FRIDAY">Sexta-feira</option>
                            <option value="SATURDAY">Sábado</option>
                            <option value="SUNDAY">Domingo</option>
                        </select>
                    </div>
                    <div class="update-form-group">
                        <label for="update-schedule-template-start">Horário de Início:</label>
                        <input type="time" id="update-schedule-template-start" name="update-schedule-template-start" required>
                    </div>
                    <div class="update-form-group">
                        <label for="update-schedule-template-end">Horário de Término:</label>
                        <input type="time" id="update-schedule-template-end" name="update-schedule-template-end" required>
                    </div>
                    <div class="update-form-group">
                        <label for="update-schedule-template-duration">Duração do Intervalo (minutos):</label>
                        <input type="number" id="update-schedule-template-duration" name="update-schedule-template-duration" min="1" required>
                    </div>
                    <button type="submit" class="update-btn">Atualizar Template</button>
                </form>
            </div>
        </div>
    `;

    const searchForm = document.getElementById('update-schedule-template-search-form');
    const detailsForm = document.getElementById('update-schedule-template-details-form');
    const messageDiv = document.getElementById('update-schedule-template-message');
    const templateDetails = document.getElementById('update-schedule-template-details');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `update-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'update-message';
        }, 5000);
    }

    function formatTime(time) {
        return time + ':00';
    }

    searchForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const templateId = document.getElementById('update-schedule-template-search-id').value.trim();

        if (!templateId) {
            showMessage('Por favor, insira o ID do template.', true);
            return;
        }

        try {
            const data = {
                scheduleTemplateId: templateId
            };

            const response = await makeRequest('/rest-schedule/schedule-template/find-by-id', 'POST', data);
            
            // Preencher o formulário com os dados do template
            document.getElementById('update-schedule-template-id').value = response.scheduleTemplateId;
            document.getElementById('update-schedule-template-room-id').value = response.roomId;
            document.getElementById('update-schedule-template-user-id').value = response.userId;
            document.getElementById('update-schedule-template-day').value = response.dayOfWeek;
            document.getElementById('update-schedule-template-start').value = response.startTime.substring(0, 5);
            document.getElementById('update-schedule-template-end').value = response.endTime.substring(0, 5);
            document.getElementById('update-schedule-template-duration').value = response.slotDurationMinutes;

            templateDetails.style.display = 'block';
        } catch (error) {
            console.error('Erro ao buscar template:', error);
            showMessage(error.message || 'Erro ao buscar template. Tente novamente.', true);
            templateDetails.style.display = 'none';
        }
    });

    detailsForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const templateId = document.getElementById('update-schedule-template-id').value;
        const roomId = document.getElementById('update-schedule-template-room-id').value.trim();
        const userId = document.getElementById('update-schedule-template-user-id').value.trim();
        const dayOfWeek = document.getElementById('update-schedule-template-day').value;
        const startTime = document.getElementById('update-schedule-template-start').value;
        const endTime = document.getElementById('update-schedule-template-end').value;
        const slotDurationMinutes = document.getElementById('update-schedule-template-duration').value;

        if (!roomId || !userId || !dayOfWeek || !startTime || !endTime || !slotDurationMinutes) {
            showMessage('Por favor, preencha todos os campos.', true);
            return;
        }

        if (startTime >= endTime) {
            showMessage('O horário de início deve ser anterior ao horário de término.', true);
            return;
        }

        try {
            const data = {
                scheduleTemplateId: templateId,
                roomId,
                userId,
                dayOfWeek,
                startTime: formatTime(startTime),
                endTime: formatTime(endTime),
                slotDurationMinutes: parseInt(slotDurationMinutes)
            };

            const response = await makeRequest('/rest-schedule/schedule-template/update', 'PUT', data);
            showMessage(response.message);
            templateDetails.style.display = 'none';
            searchForm.reset();
        } catch (error) {
            console.error('Erro ao atualizar template:', error);
            showMessage(error.message || 'Erro ao atualizar template. Tente novamente.', true);
        }
    });
});
