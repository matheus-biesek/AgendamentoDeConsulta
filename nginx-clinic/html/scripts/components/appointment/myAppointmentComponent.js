import { makeRequest } from "../../utils/request.js";

document.addEventListener("DOMContentLoaded", () => {
    const myAppointmentComponent = document.querySelector(".my-appointment-component");
    
    async function fetchMyAppointments() {
        try {
            const response = await makeRequest('/rest-schedule/appointment/my-appointments', 'GET');
            displayAppointments(response);
        } catch (error) {
            console.error('Erro ao buscar consultas:', error);
            displayError('Erro ao carregar suas consultas. Por favor, tente novamente mais tarde.');
        }
    }

    function displayAppointments(appointments) {
        if (!appointments || appointments.length === 0) {
            myAppointmentComponent.innerHTML = `
                <div class="my-appointments-container">
                    <h2>Minhas Consultas</h2>
                    <p class="my-appointments-message">Você não possui consultas agendadas.</p>
                </div>
            `;
            return;
        }

        const appointmentsHTML = appointments.map(appointment => `
            <tr>
                <td>${appointment.appointmentId}</td>
                <td>${appointment.timeSlotId}</td>
                <td>${appointment.patientId}</td>
                <td>
                    <span class="appointment-status ${appointment.active ? 'active' : 'inactive'}">
                        ${appointment.active ? 'Ativa' : 'Inativa'}
                    </span>
                </td>
            </tr>
        `).join('');

        myAppointmentComponent.innerHTML = `
            <div class="my-appointments-container">
                <h2>Minhas Consultas</h2>
                <div class="appointments-list">
                    <table class="appointments-table">
                        <thead>
                            <tr>
                                <th>ID da Consulta</th>
                                <th>ID do Horário</th>
                                <th>ID do Paciente</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${appointmentsHTML}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    }

    function displayError(message) {
        myAppointmentComponent.innerHTML = `
            <div class="my-appointments-container">
                <h2>Minhas Consultas</h2>
                <p class="my-appointments-message error">${message}</p>
            </div>
        `;
    }

    // Carrega as consultas quando o componente é montado
    fetchMyAppointments();
});
