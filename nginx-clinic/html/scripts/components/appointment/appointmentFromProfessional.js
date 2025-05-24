import { makeRequest } from "../../utils/request.js";

document.addEventListener("DOMContentLoaded", () => {
    const professionalAppointmentComponent = document.querySelector(".professional-appointment-component");
    
    async function fetchProfessionalAppointments() {
        try {
            const response = await makeRequest('/rest-schedule/appointment/find-by-professional-id', 'GET');
            displayAppointments(response);
        } catch (error) {
            console.error('Erro ao buscar consultas:', error);
            displayError('Erro ao carregar as consultas. Por favor, tente novamente mais tarde.');
        }
    }

    function displayAppointments(appointments) {
        if (!appointments || appointments.length === 0) {
            professionalAppointmentComponent.innerHTML = `
                <h2>Consultas do Profissional</h2>
                <p class="message">Não há consultas agendadas.</p>
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

        professionalAppointmentComponent.innerHTML = `
            <h2>Consultas do Profissional</h2>
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
        `;
    }

    function displayError(message) {
        professionalAppointmentComponent.innerHTML = `
            <h2>Consultas do Profissional</h2>
            <p class="message error">${message}</p>
        `;
    }

    // Carrega as consultas quando o componente é montado
    fetchProfessionalAppointments();
});


