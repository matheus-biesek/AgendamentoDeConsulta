import { makeRequest } from "../../utils/request.js";

document.addEventListener("DOMContentLoaded", () => {
    const myTimeSlotsComponent = document.querySelector(".my-time-slots-component");
    
    async function fetchMyTimeSlots() {
        try {
            const response = await makeRequest('/rest-schedule/time-slot/my-time-slots', 'GET');
            displayTimeSlots(response);
        } catch (error) {
            console.error('Erro ao buscar horários:', error);
            displayError('Erro ao carregar os horários. Por favor, tente novamente mais tarde.');
        }
    }

    function formatDate(dateArray) {
        const [year, month, day] = dateArray;
        return `${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/${year}`;
    }

    function displayTimeSlots(timeSlots) {
        if (!timeSlots || timeSlots.length === 0) {
            myTimeSlotsComponent.innerHTML = `
                <h2>Meus Horários</h2>
                <p class="my-time-slots-message">Não há horários cadastrados.</p>
            `;
            return;
        }

        // Agrupa os time slots por data
        const timeSlotsByDate = timeSlots.reduce((acc, slot) => {
            const date = formatDate(slot.date);
            if (!acc[date]) {
                acc[date] = [];
            }
            acc[date].push(slot);
            return acc;
        }, {});

        const timeSlotsHTML = Object.entries(timeSlotsByDate).map(([date, slots]) => `
            <div class="my-time-slots-date-group">
                <h3 class="my-time-slots-date">${date}</h3>
                <div class="my-time-slots-list">
                    ${slots.map(slot => `
                        <div class="my-time-slots-item">
                            <div class="my-time-slots-time">
                                ${slot.start_time.substring(0, 5)} - ${slot.end_time.substring(0, 5)}
                            </div>
                            <div class="my-time-slots-status ${slot.active ? 'active' : 'inactive'}">
                                ${slot.active ? 'Ativo' : 'Inativo'}
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `).join('');

        myTimeSlotsComponent.innerHTML = `
            <h2>Meus Horários</h2>
            <div class="my-time-slots-container">
                ${timeSlotsHTML}
            </div>
        `;
    }

    function displayError(message) {
        myTimeSlotsComponent.innerHTML = `
            <h2>Meus Horários</h2>
            <p class="my-time-slots-message error">${message}</p>
        `;
    }

    // Carrega os horários quando o componente é montado
    fetchMyTimeSlots();
});
