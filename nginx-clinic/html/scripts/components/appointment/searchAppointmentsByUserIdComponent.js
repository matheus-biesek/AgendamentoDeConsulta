import { makeRequest } from "../../utils/request.js";

document.addEventListener("DOMContentLoaded", () => {
    const searchAppointmentsByUserIdComponent = document.querySelector(".search-appointments-by-user-id-component");
    
    searchAppointmentsByUserIdComponent.innerHTML = `
        <div class="search-appointments-container">
            <h2>Buscar Consultas por Usuário</h2>
            <form id="search-appointments-form">
                <div class="search-appointments-form-group">
                    <label for="search-appointments-user-id">ID do Usuário:</label>
                    <input type="text" id="search-appointments-user-id" name="userId" required>
                </div>
                <button type="submit" class="search-appointments-btn">Buscar Consultas</button>
            </form>
            <div id="search-appointments-message"></div>
            <div id="appointments-list" class="appointments-list" style="display: none;">
                <table class="appointments-table">
                    <thead>
                        <tr>
                            <th>ID da Consulta</th>
                            <th>ID da Grade</th>
                            <th>ID do Paciente</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody id="appointments-list-body"></tbody>
                </table>
            </div>
        </div>
    `;

    const form = document.getElementById('search-appointments-form');
    const messageDiv = document.getElementById('search-appointments-message');
    const appointmentsList = document.getElementById('appointments-list');
    const appointmentsListBody = document.getElementById('appointments-list-body');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `search-appointments-message ${isError ? 'error' : ''}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'search-appointments-message';
        }, 5000);
    }

    function displayAppointments(appointments) {
        if (!appointments || appointments.length === 0) {
            showMessage('Nenhuma consulta encontrada para este usuário.');
            appointmentsList.style.display = 'none';
            return;
        }

        appointmentsListBody.innerHTML = '';
        appointments.forEach(appointment => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${appointment.appointmentId}</td>
                <td>${appointment.timeSlotId}</td>
                <td>${appointment.patientId}</td>
                <td>
                    <span class="appointment-status ${appointment.active ? 'active' : 'inactive'}">
                        ${appointment.active ? 'Ativo' : 'Inativo'}
                    </span>
                </td>
            `;
            appointmentsListBody.appendChild(row);
        });

        appointmentsList.style.display = 'block';
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const userId = document.getElementById('search-appointments-user-id').value.trim();

        if (!userId) {
            showMessage('Por favor, insira o ID do usuário.', true);
            return;
        }

        try {
            const data = {
                userId
            };

            const response = await makeRequest('/rest-schedule/appointment/find-by-user-id', 'POST', data);
            displayAppointments(response);
        } catch (error) {
            console.error('Erro ao buscar consultas:', error);
            showMessage(error.message || 'Erro ao buscar consultas. Tente novamente.', true);
            appointmentsList.style.display = 'none';
        }
    });
});
// criar componente para buscar todas as consultas de um usuário