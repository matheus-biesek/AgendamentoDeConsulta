export function renderAppointmentTable(data) {
    console.log("Renderizando tabela com os dados:", data);

    if (!data || data.length === 0) {
        document.querySelector(".appointment-component").innerHTML = "<p>Nenhum agendamento encontrado.</p>";
        return;
    }

    const tableHTML = `
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
                ${data.map(appointment => `
                    <tr>
                        <td>${appointment.id}</td>
                        <td>${appointment.status}</td>
                        <td>${appointment.nurse}</td>
                        <td>${appointment.doctor}</td>
                        <td>${appointment.specialization}</td>
                        <td>${appointment.room}</td>
                        <td>${appointment.function}</td>
                        <td>${appointment.date}</td>
                        <td>${appointment.time}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    document.querySelector(".appointment-component").innerHTML = tableHTML;
}