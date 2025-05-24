import { makeRequest } from "../../utils/request.js";

// criar componente para buscar todas as grades de horários de um usuário

document.addEventListener("DOMContentLoaded", () => {
    const findAllTimeSlotByUserComponent = document.querySelector(".find-all-time-slot-by-user-component");
    
    findAllTimeSlotByUserComponent.innerHTML = `
        <div class="find-time-slot-container">
            <h2>Buscar Grades Horárias do Profissional</h2>
            <form id="find-time-slot-form">
                <div class="find-time-slot-form-group">
                    <label for="find-time-slot-user-id">ID do Usuário:</label>
                    <input type="text" id="find-time-slot-user-id" name="find-time-slot-user-id" required>
                </div>
                <button type="submit" class="find-time-slot-btn">Buscar Grades Horárias</button>
            </form>
            <div id="find-time-slot-message"></div>
            <div id="time-slot-list" class="time-slot-list" style="display: none;">
                <table class="time-slot-table">
                    <thead>
                        <tr>
                            <th>ID da Grade</th>
                            <th>ID do Template</th>
                            <th>Status</th>
                            <th>ID da Sala</th>
                            <th>Horário Início</th>
                            <th>Horário Término</th>
                            <th>Data</th>
                        </tr>
                    </thead>
                    <tbody id="time-slot-list-body"></tbody>
                </table>
            </div>
        </div>
    `;

    const form = document.getElementById('find-time-slot-form');
    const messageDiv = document.getElementById('find-time-slot-message');
    const timeSlotList = document.getElementById('time-slot-list');
    const timeSlotListBody = document.getElementById('time-slot-list-body');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `find-time-slot-message ${isError ? 'error' : ''}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'find-time-slot-message';
        }, 5000);
    }

    function formatDate(dateArray) {
        const [year, month, day] = dateArray;
        return `${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/${year}`;
    }

    function displayTimeSlots(timeSlots) {
        if (!timeSlots || timeSlots.length === 0) {
            showMessage('Nenhuma grade horária encontrada para este usuário.');
            timeSlotList.style.display = 'none';
            return;
        }

        timeSlotListBody.innerHTML = '';
        timeSlots.forEach(slot => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${slot.time_slot_id}</td>
                <td>${slot.schedule_template_id}</td>
                <td>
                    <span class="time-slot-status ${slot.active ? 'active' : 'inactive'}">
                        ${slot.active ? 'Ativo' : 'Inativo'}
                    </span>
                </td>
                <td>${slot.room_id}</td>
                <td>${slot.start_time}</td>
                <td>${slot.end_time}</td>
                <td>${formatDate(slot.date)}</td>
            `;
            timeSlotListBody.appendChild(row);
        });

        timeSlotList.style.display = 'block';
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const userId = document.getElementById('find-time-slot-user-id').value.trim();

        if (!userId) {
            showMessage('Por favor, insira o ID do usuário.', true);
            return;
        }

        try {
            const data = {
                userId
            };

            const response = await makeRequest('/rest-schedule/time-slot/find-all-by-user-id', 'POST', data);
            displayTimeSlots(response);
        } catch (error) {
            console.error('Erro ao buscar grades horárias:', error);
            showMessage(error.message || 'Erro ao buscar grades horárias. Tente novamente.', true);
            timeSlotList.style.display = 'none';
        }
    });
});