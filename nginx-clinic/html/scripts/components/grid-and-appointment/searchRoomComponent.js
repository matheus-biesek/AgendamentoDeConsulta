document.querySelector(".search-all-room-component").innerHTML = `
    <div class="search-room-container">
        <h2>Salas Disponíveis</h2>
        <div class="search-room-grid">
            <table>
                <thead>
                    <tr>
                        <th>Número da Sala</th>
                        <th>Tipo de Sala</th>
                    </tr>
                </thead>
                <tbody id="room-list">
                    <tr>
                        <td colspan="2" class="loading">Carregando salas...</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
`;