import { fetchAppointmentsByDate } from "../../controllers/patientController.js";
import { renderAppointmentTable } from "../../components/grid-and-appointment/appointmentComponent.js";

class AppointmentHandler {
    constructor() {
        this.searchButton = document.querySelector(".search-button");
        this.dateInput = document.querySelector("#reference-date");
    }

    init() {
        if (!this.searchButton || !this.dateInput) {
            console.error("Elementos necessários não encontrados no DOM.");
            return;
        }

        this.initDateFilter();
        this.loadInitialAppointments();
        this.setupEventListeners();
    }

    initDateFilter() {
        const today = new Date().toISOString().split("T")[0];
        this.dateInput.value = today;
    }

    async loadInitialAppointments() {
        try {
            const appointments = await fetchAppointmentsByDate(this.dateInput.value);
            renderAppointmentTable(appointments);
        } catch (error) {
            this.handleError("carregar", error);
        }
    }

    setupEventListeners() {
        this.searchButton.addEventListener("click", () => this.handleSearch());
    }

    async handleSearch() {
        if (!this.dateInput.value) {
            alert("Por favor, selecione uma data.");
            return;
        }

        try {
            const appointments = await fetchAppointmentsByDate(this.dateInput.value);
            renderAppointmentTable(appointments);
        } catch (error) {
            this.handleError("buscar", error);
        }
    }

    handleError(action, error) {
        console.error(`Erro ao ${action} as consultas:`, error);
        alert(`Erro ao ${action} as consultas. Por favor, tente novamente.`);
    }
}

// Inicialização
document.addEventListener("DOMContentLoaded", () => {
    const handler = new AppointmentHandler();
    handler.init();
});