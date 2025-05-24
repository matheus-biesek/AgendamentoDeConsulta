import { makeRequest } from "../../utils/request.js";

document.addEventListener("DOMContentLoaded", () => {
    const cancelTimeSlotComponent = document.querySelector(".cancel-time-slot-component");
    
    cancelTimeSlotComponent.innerHTML = `
        <div class="cancel-time-slot-container">
            <h2>Cancelar Grade Horária</h2>
            <form id="cancel-time-slot-form">
                <div class="cancel-time-slot-form-group">
                    <label for="cancel-time-slot-id">ID da Grade Horária:</label>
                    <input type="text" id="cancel-time-slot-id" name="cancel-time-slot-id" required>
                </div>
                <button type="submit" class="cancel-time-slot-btn">Cancelar Grade Horária</button>
            </form>
            <div id="cancel-time-slot-message"></div>
        </div>
    `;

    const form = document.getElementById('cancel-time-slot-form');
    const messageDiv = document.getElementById('cancel-time-slot-message');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `cancel-time-slot-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'cancel-time-slot-message';
        }, 5000);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const timeSlotId = document.getElementById('cancel-time-slot-id').value.trim();

        if (!timeSlotId) {
            showMessage('Por favor, insira o ID da grade horária.', true);
            return;
        }

        try {
            const data = {
                timeSlotId
            };

            const response = await makeRequest('/rest-schedule/time-slot/cancel-by-id', 'PUT', data);
            showMessage(response.message);
            form.reset();
        } catch (error) {
            console.error('Erro ao cancelar grade horária:', error);
            showMessage(error.message || 'Erro ao cancelar grade horária. Tente novamente.', true);
        }
    });
}); 