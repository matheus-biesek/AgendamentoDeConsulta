document.querySelector(".search-schedule-component").innerHTML = `
    <h2>Grade horária</h2>
    
    <div class="search-form">
        <div class="form-group">
            <label for="worker-id">ID funcionário:</label>
            <input type="number" id="worker-id" min="1" placeholder="Digite o ID do funcionário">
        </div>
        <button type="button" id="search-schedule" class="search-button">Buscar</button>
    </div>

    <div class="schedule-table" style="display: none;">
        <table>
            <thead>
                <tr>
                    <th>ID grade horária</th>
                    <th>Status</th>
                    <th>Sala</th>
                    <th>Função</th>
                    <th>Data</th>
                    <th>Horário</th>
                </tr>
            </thead>
            <tbody id="schedule-results">
                <!-- Results will be inserted here -->
            </tbody>
        </table>
    </div>
`;