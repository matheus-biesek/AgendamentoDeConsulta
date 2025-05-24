import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".create-room-component");

    component.innerHTML = `
        <div class="create-room-container">
            <h2>Criar Sala</h2>
            <form id="create-room-form">
                <div class="form-group">
                    <label for="room-number">Número da Sala:</label>
                    <input type="text" id="room-number" name="room-number" required>
                </div>
                <div class="form-group">
                    <label for="room-function">Tipo de Sala:</label>
                    <select id="room-function" name="room-function" required>
                        <option value="">Selecione o tipo</option>
                        <option value="PRIVATE_OFFICE">Consultório Particular</option>
                        <option value="SURGERY">Sala de Cirurgia</option>
                        <option value="CONSULTATION">Sala de Consulta</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="user-id">ID do Usuário (opcional):</label>
                    <input type="text" id="user-id" name="user-id">
                </div>
                <button type="submit">Criar Sala</button>
            </form>
            <div id="message"></div>
        </div>
    `;

    const form = document.getElementById('create-room-form');
    const messageDiv = document.getElementById('message');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'message';
        }, 5000);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const roomNumber = document.getElementById('room-number').value.trim();
        const roomFunction = document.getElementById('room-function').value;
        const userId = document.getElementById('user-id').value.trim();

        if (!roomNumber || !roomFunction) {
            showMessage('Por favor, preencha todos os campos obrigatórios.', true);
            return;
        }

        try {
            const data = {
                number: roomNumber,
                function: roomFunction,
                userId: userId || null
            };

            const response = await makeRequest('/rest-schedule/room/create', 'POST', data);
            showMessage(response.message);
            form.reset();
        } catch (error) {
            console.error('Erro ao criar sala:', error);
            showMessage(error.message || 'Erro ao criar sala. Tente novamente.', true);
        }
    });
});


