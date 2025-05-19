document.querySelector(".create-schedule-component").innerHTML = `
        <h2>Adicionar grade</h2>
        <form id="create-schedule-form" class="create-schedule-form">
            <div class="form-group">
                <label for="worker-id">ID Funcionário:</label>
                <input type="number" id="worker-id" min="1" placeholder="Digite o ID do funcionário" required>
            </div>

            <div class="form-group">
                <label for="room-number">Sala:</label>
                <input type="number" id="room-number" min="1" placeholder="Digite o número da sala" required>
            </div>

            <div class="form-group">
                <label for="schedule-date">Data:</label>
                <input type="date" id="schedule-date" required>
            </div>

            <div class="form-group">
                <label for="schedule-time">Hora:</label>
                <input type="time" id="schedule-time" step="1800"required>
            </div>

            <button type="submit" class="add-schedule-button">Adicionar</button>
        </form>
`;