import { fetchAppointmentsByDate } from "../../controllers/patientController.js";
import { renderAppointmentTable } from "../../components/grid-and-appointment/gridComponent.js";

document.addEventListener("DOMContentLoaded", () => {
    const searchButton = document.querySelector(".search-button");
    const dateInput = document.querySelector("#reference-date");

    if (searchButton && dateInput) {
        searchButton.addEventListener("click", async () => {
            const selectedDate = dateInput.value;

            if (!selectedDate) {
                alert("Por favor, selecione uma data.");
                return;
            }

            try {
                // Busca os dados do backend
                const data = await fetchAppointmentsByDate(selectedDate);

                // Atualiza a tabela com os dados recebidos
                renderAppointmentTable(data);
            } catch (error) {
                console.error("Erro ao buscar os dados:", error);
                alert("Erro ao buscar os dados. Por favor, tente novamente.");
            }
        });
    } else {
        console.error("Botão ou campo de data não encontrado no DOM.");
    }
});