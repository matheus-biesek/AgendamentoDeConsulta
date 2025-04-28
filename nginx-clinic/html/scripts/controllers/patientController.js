import {
    verifyCurrentPasswordService,
    changePasswordService,
    fetchAppointmentsByDateService
} from "../services/patientService.js";

// Função para buscar consultas por data
export async function fetchAppointmentsByDate(selectedDate) {
    return await fetchAppointmentsByDateService(selectedDate);
}

// Função para verificar a senha atual
export async function verifyCurrentPassword(currentPassword) {
    return await verifyCurrentPasswordService(currentPassword);
}

// Função para trocar a senha
export async function changePassword(currentPassword, newPassword) {
    return await changePasswordService(currentPassword, newPassword);
}