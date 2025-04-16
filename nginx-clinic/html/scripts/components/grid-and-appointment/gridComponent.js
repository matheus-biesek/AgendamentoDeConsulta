document.querySelector(".appointment-component").innerHTML = `
    <table class="appointment-table">
        <thead>
            <tr>
                <th>ID Consulta</th>
                <th>Status</th>
                <th>Enfermeira</th>
                <th>Médico</th>
                <th>Especialização</th>
                <th>Sala</th>
                <th>Função</th>
                <th>Data</th>
                <th>Hora</th>
            </tr>
        </thead>
        <tbody>
            <!-- DADOS -->
              <tr>
                    <td>1</td>
                    <td>Confirmada</td>
                    <td>Maria Silva</td>
                    <td>Dr. João Souza</td>
                    <td>Cardiologia</td>
                    <td>101</td>
                    <td>Consulta</td>
                    <td>16/04/2025</td>
                    <td>14:00</td>
              </tr>
        </tbody>
    </table>
`;
