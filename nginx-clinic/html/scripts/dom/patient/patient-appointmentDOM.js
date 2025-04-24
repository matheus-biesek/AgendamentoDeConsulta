import { fetchAppointmentsByDate } from "../../controllers/patientController.js";
import { renderAppointmentTable } from "../../components/grid-and-appointment/appointmentComponent.js";

document.addEventListener("DOMContentLoaded", async () => {
    const searchButton = document.querySelector(".search-button");
    const dateInput = document.querySelector("#reference-date");

    if (!searchButton || !dateInput) {
        console.error("Botão ou campo de data não encontrado no DOM.");
        return;
    }

    try {
        // Carrega as próximas consultas ao carregar a página
        const today = new Date().toISOString().split("T")[0]; // Data atual no formato YYYY-MM-DD
        const upcomingAppointments = await fetchAppointmentsByDate(today);
        renderAppointmentTable(upcomingAppointments);
    } catch (error) {
        console.error("Erro ao carregar as próximas consultas:", error);
        alert("Erro ao carregar as próximas consultas. Por favor, tente novamente.");
    }

    // Configura o evento de clique no botão "Buscar"
    searchButton.addEventListener("click", async () => {
        const selectedDate = dateInput.value;

        if (!selectedDate) {
            alert("Por favor, selecione uma data.");
            return;
        }

        try {
            // Busca as consultas filtradas pela data selecionada
            const filteredAppointments = await fetchAppointmentsByDate(selectedDate);
            renderAppointmentTable(filteredAppointments);
        } catch (error) {
            console.error("Erro ao buscar as consultas:", error);
            alert("Erro ao buscar as consultas. Por favor, tente novamente.");
        }
    });
});