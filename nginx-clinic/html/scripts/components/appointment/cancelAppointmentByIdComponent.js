import { makeRequest } from "../../utils/request.js";

document.addEventListener("DOMContentLoaded", () => {
    const cancelAppointmentByIdComponent = document.querySelector(".cancel-appointment-by-id-component");
    
    cancelAppointmentByIdComponent.innerHTML = `
        <div class="cancel-appointment-container">
            <h2>Cancelar Consulta</h2>
            <form id="cancel-appointment-form">
                <div class="cancel-appointment-form-group">
                    <label for="cancel-appointment-id">ID da Consulta:</label>
                    <input type="text" id="cancel-appointment-id" name="appointmentId" required>
                </div>
                <button type="submit" class="cancel-appointment-btn">Cancelar Consulta</button>
            </form>
            <div id="cancel-appointment-message"></div>
        </div>
    `;

    const form = document.getElementById('cancel-appointment-form');
    const messageDiv = document.getElementById('cancel-appointment-message');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `cancel-appointment-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'cancel-appointment-message';
        }, 5000);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const appointmentId = document.getElementById('cancel-appointment-id').value.trim();

        if (!appointmentId) {
            showMessage('Por favor, insira o ID da consulta.', true);
            return;
        }

        try {
            const data = {
                appointmentId
            };

            const response = await makeRequest('/rest-schedule/appointment/cancel', 'PUT', data);
            showMessage(response.message);
            form.reset();
        } catch (error) {
            console.error('Erro ao cancelar consulta:', error);
            showMessage(error.message || 'Erro ao cancelar consulta. Tente novamente.', true);
        }
    });
});
