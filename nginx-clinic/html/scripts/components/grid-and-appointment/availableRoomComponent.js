document.querySelector(".available-romm-component").innerHTML = `
    <div class="available-room-container">
        <h2>Horários disponíveis para sala</h2>
        <div class="search-form">
            <div class="form-group">
                <label>Disponibilidade para sala:</label>
                <input type="text" id="room-number" placeholder="Digite o número da sala">
            </div>
            <div class="form-group">
                <label>Data de referência:</label>
                <input type="date" id="reference-date" value="${new Date().toISOString().split('T')[0]}">
            </div>
            <button type="button" id="search-availability">Buscar</button>
        </div>
        <div class="schedule-grid" style="display: none;">
            <h3>Horário</h3>
            <div id="available-times"></div>
        </div>
    </div>
`;