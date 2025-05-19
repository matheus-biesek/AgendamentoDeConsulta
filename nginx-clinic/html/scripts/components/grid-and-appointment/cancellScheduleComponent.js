document.querySelector(".cancell-schedule-component").innerHTML = `
    <h2>Cancelar horário</h2>
    <form id="cancell-schedule-form" class="cancell-schedule-form">
        <div class="form-group">
            <label for="schedule-id">ID grade:</label>
            <input type="number" id="schedule-id" min="1" placeholder="Digite o ID da grade" required>
        </div>

        <div class="form-group">
            <label for="cancellation-reason">Motivo:</label>
            <input type="text" 
                   id="cancellation-reason" placeholder="Digite o motivo do cancelamento" required>
        </div>

        <button type="submit" class="cancel-button">Cancelar</button>
    </form>
`; 