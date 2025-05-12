// scripts/services/sessionService.js
import { makeRequest } from "../utils/request.js";

const routes = {
    "admin": "admin/admin-update.html",
    "secretary": "secretary/secretary-making-appointment.html",
    "doctor": "doctor/doctor-appointment-schedule-grid.html",
    "technician": "technician/technician-schedule.html",
    "nurse": "nurse/nurse-appointment-schedule-grid.html",
    "patient": "patient/patient-info.html"
};

export async function loginSession(cpf, password) {
    const response = await makeRequest("/rest-auth/auth-session/login", "POST", { cpf, password });
    return response; // Agora retorna o array de roles
}

export async function logoutSession() {
    await makeRequest("/rest-auth/auth-session/logout", "POST");
    localStorage.removeItem("csrf-token");
}

export function getRouteForRole(role) {
    return routes[role] || "blocked.html";
}
