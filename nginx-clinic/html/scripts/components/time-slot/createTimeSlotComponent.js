import { makeRequest } from "../../utils/request.js";

document.addEventListener("DOMContentLoaded", () => {
    const createTimeSlotComponent = document.querySelector(".create-time-slot-component");
    
    createTimeSlotComponent.innerHTML = `
        <div class="create-time-slot-container">
            <h2>Criar Grade Horária</h2>
            <form id="create-time-slot-form">
                <div class="time-slot-form-group">
                    <label for="time-slot-user-id">ID do Usuário:</label>
                    <input type="text" id="time-slot-user-id" name="time-slot-user-id" required>
                </div>
                <div class="time-slot-form-group">
                    <label for="time-slot-room-id">ID da Sala:</label>
                    <input type="text" id="time-slot-room-id" name="time-slot-room-id" required>
                </div>
                <div class="time-slot-form-group">
                    <label for="time-slot-start-date">Data Inicial:</label>
                    <input type="date" id="time-slot-start-date" name="time-slot-start-date" required>
                </div>
                <div class="time-slot-form-group">
                    <label for="time-slot-end-date">Data Final:</label>
                    <input type="date" id="time-slot-end-date" name="time-slot-end-date" required>
                </div>
                <button type="submit" class="time-slot-btn">Criar Grade Horária</button>
            </form>
            <div id="time-slot-message"></div>
        </div>
    `;

    const form = document.getElementById('create-time-slot-form');
    const messageDiv = document.getElementById('time-slot-message');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `time-slot-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'time-slot-message';
        }, 5000);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const userId = document.getElementById('time-slot-user-id').value.trim();
        const roomId = document.getElementById('time-slot-room-id').value.trim();
        const startDate = document.getElementById('time-slot-start-date').value;
        const endDate = document.getElementById('time-slot-end-date').value;

        if (!userId || !roomId || !startDate || !endDate) {
            showMessage('Por favor, preencha todos os campos.', true);
            return;
        }

        if (startDate > endDate) {
            showMessage('A data inicial não pode ser posterior à data final.', true);
            return;
        }

        try {
            const data = {
                userId,
                roomId,
                startDate,
                endDate
            };

            const response = await makeRequest('/rest-schedule/time-slot/create', 'POST', data);
            showMessage(response.message);
            form.reset();
        } catch (error) {
            console.error('Erro ao criar grade horária:', error);
            showMessage(error.message || 'Erro ao criar grade horária. Tente novamente.', true);
        }
    });
});