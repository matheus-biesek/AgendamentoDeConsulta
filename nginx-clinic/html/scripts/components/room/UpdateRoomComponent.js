import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    const updateRoomComponent = document.querySelector(".update-room-component");

    updateRoomComponent.innerHTML = `
        <div class="update-room-container">
            <h2>Atualizar Sala</h2>
            <form id="update-room-form">
                <div class="update-form-group">
                    <label for="update-room-id">ID da Sala:</label>
                    <input type="text" id="update-room-id" name="update-room-id" required>
                    <button type="button" id="update-search-btn" class="update-room-btn">Buscar Sala</button>
                </div>
                <div id="update-room-fields" style="display: none;">
                    <div class="update-form-group">
                        <label for="update-room-number">Número da Sala:</label>
                        <input type="text" id="update-room-number" name="update-room-number" required>
                    </div>
                    <div class="update-form-group">
                        <label for="update-room-function">Tipo de Sala:</label>
                        <select id="update-room-function" name="update-room-function" required>
                            <option value="">Selecione o tipo</option>
                            <option value="PRIVATE_OFFICE">Consultório Particular</option>
                            <option value="SURGERY">Sala de Cirurgia</option>
                            <option value="CONSULTATION">Sala de Consulta</option>
                        </select>
                    </div>
                    <div class="update-form-group">
                        <label for="update-user-id">ID do Usuário (opcional):</label>
                        <input type="text" id="update-user-id" name="update-user-id">
                    </div>
                    <button type="submit" class="update-room-btn">Atualizar Sala</button>
                </div>
            </form>
            <div id="update-message"></div>
        </div>
    `;

    const updateForm = document.getElementById('update-room-form');
    const updateMessageDiv = document.getElementById('update-message');
    const updateSearchBtn = document.getElementById('update-search-btn');
    const updateRoomIdInput = document.getElementById('update-room-id');
    const updateRoomFields = document.getElementById('update-room-fields');
    const updateRoomNumberInput = document.getElementById('update-room-number');
    const updateRoomFunctionSelect = document.getElementById('update-room-function');
    const updateUserIdInput = document.getElementById('update-user-id');

    function showUpdateMessage(text, isError = false) {
        updateMessageDiv.textContent = text;
        updateMessageDiv.className = `update-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            updateMessageDiv.textContent = '';
            updateMessageDiv.className = 'update-message';
        }, 5000);
    }

    async function searchRoom() {
        const roomId = updateRoomIdInput.value.trim();
        
        if (!roomId) {
            showUpdateMessage('Por favor, insira o ID da sala.', true);
            return;
        }

        try {
            const response = await makeRequest('/rest-schedule/room/search', 'POST', { id: roomId });
            
            updateRoomNumberInput.value = response.number.trim();
            updateRoomFunctionSelect.value = response.function;
            updateUserIdInput.value = response.user_id || '';
            
            // Mostra os campos após encontrar a sala
            updateRoomFields.style.display = 'block';
            showUpdateMessage('Sala encontrada com sucesso!');
        } catch (error) {
            console.error('Erro ao buscar sala:', error);
            showUpdateMessage(error.message || 'Erro ao buscar sala. Tente novamente.', true);
            // Esconde os campos em caso de erro
            updateRoomFields.style.display = 'none';
        }
    }

    updateSearchBtn.addEventListener('click', searchRoom);

    updateForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const roomId = updateRoomIdInput.value.trim();
        const roomNumber = updateRoomNumberInput.value.trim();
        const roomFunction = updateRoomFunctionSelect.value;
        const userId = updateUserIdInput.value.trim();

        if (!roomId || !roomNumber || !roomFunction) {
            showUpdateMessage('Por favor, preencha todos os campos obrigatórios.', true);
            return;
        }

        try {
            const data = {
                id: roomId,
                number: roomNumber,
                function: roomFunction,
                userId: userId || null
            };

            const response = await makeRequest('/rest-schedule/room/update', 'PUT', data);
            showUpdateMessage(response.message);
            updateForm.reset();
            // Esconde os campos após atualização
            updateRoomFields.style.display = 'none';
        } catch (error) {
            console.error('Erro ao atualizar sala:', error);
            showUpdateMessage(error.message || 'Erro ao atualizar sala. Tente novamente.', true);
        }
    });
});

// Criar função para atualizar uma sala.
// API /rest-schedule/room/update
// JSON de entrada:
// {
// 	"id": "4ef9b342-6fcf-4294-a755-022f6da7545e",
// 	"number": "10",
// 	"function": "PRIVATE_OFFICE",
// 	"userId": "49bed682-969c-45ad-889b-ab11bc574c19"
// }
// JSON de saida:
// {
// 	"message": "Sala atualizada com sucesso"
// }
// Basicamente a função precisa primeiro pedir o id da sala, e realizar uma requisição na api /rest-schedule/room/search para pegar os dados da sala.
//Json de entrada:
//{
//	"id": "4ef9b342-6fcf-4294-a755-022f6da7545e"
//}
//Json de saida:
//{
//	"room_id": "2326b63e-07d5-4932-9012-5c276a5bb7cc",
//	"number": "2  ",
//	"function": "PRIVATE_OFFICE",
// 	"user_id": "066697fc-e9e7-4326-9acb-f09eae81fb19"
// }

// colocar a função como o outro tipo de input para que o usuario nao tenha que digitar o tipo da sala

