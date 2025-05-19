document.querySelector(".search-workers-component").innerHTML = `
    <h2>Buscar ID dos Funcionários</h2>
    
    <div class="worker-type-options">
        <input type="radio" id="doctor-search" name="worker-type" value="DOCTOR">
        <label for="doctor-search">Médico</label>

        <input type="radio" id="nurse-search" name="worker-type" value="NURSE">
        <label for="nurse-search">Enfermeira</label>

        <input type="radio" id="technician-search" name="worker-type" value="TECHNICIAN">
        <label for="technician-search">Técnico</label>
    </div>

    <button type="button" id="search-worker-button" class="search-button">Buscar</button>

    <div class="workers-table" style="display: none;">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Especialização</th>
                </tr>
            </thead>
            <tbody id="workers-results">
                <!-- Results will be inserted here -->
            </tbody>
        </table>
    </div>
`;