import { makeRequest } from "../../utils/request.js";

document.addEventListener("DOMContentLoaded", () => {
    const createAppointmentComponent = document.querySelector(".create-appointment-component");
    
    createAppointmentComponent.innerHTML = `
        <div class="create-appointment-container">
            <h2>Criar Consulta</h2>
            <form id="create-appointment-form">
                <div class="create-appointment-form-group">
                    <label for="create-appointment-patient-id">ID do Paciente:</label>
                    <input type="text" id="create-appointment-patient-id" name="patientId" required>
                </div>
                <div class="create-appointment-form-group">
                    <label for="create-appointment-time-slot-id">ID da Grade Horária:</label>
                    <input type="text" id="create-appointment-time-slot-id" name="timeSlotId" required>
                </div>
                <button type="submit" class="create-appointment-btn">Criar Consulta</button>
            </form>
            <div id="create-appointment-message"></div>
        </div>
    `;

    const form = document.getElementById('create-appointment-form');
    const messageDiv = document.getElementById('create-appointment-message');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `create-appointment-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'create-appointment-message';
        }, 5000);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const patientId = document.getElementById('create-appointment-patient-id').value.trim();
        const timeSlotId = document.getElementById('create-appointment-time-slot-id').value.trim();

        if (!patientId || !timeSlotId) {
            showMessage('Por favor, preencha todos os campos.', true);
            return;
        }

        try {
            const data = {
                patientId,
                timeSlotId
            };

            const response = await makeRequest('/rest-schedule/appointment/create', 'POST', data);
            showMessage(response.message);
            form.reset();
        } catch (error) {
            console.error('Erro ao criar consulta:', error);
            showMessage(error.message || 'Erro ao criar consulta. Tente novamente.', true);
        }
    });
});

