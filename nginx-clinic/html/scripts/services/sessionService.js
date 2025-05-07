// scripts/services/sessionService.js
import { makeRequest } from "../utils/request.js";

const routes = {
    "ADMIN": "admin/admin-update.html",
    "SECRETARY": "secretary/secretary-making-appointment.html",
    "DOCTOR": "doctor/doctor-appointment-schedule-grid.html",
    "TECHNICIAN": "technician/technician-schedule.html",
    "NURSE": "nurse/nurse-appointment-schedule-grid.html",
    "PATIENT": "patient/patient-info.html"
};

export async function loginSession(cpf, password) {

    const response = await makeRequest("/rest-auth/auth-session/login", "POST", { cpf, password });

    const role = response.role;
    return routes[role] || "blocked.html";
}

export async function logoutSession() {
    await makeRequest("/rest-auth/auth-session/logout", "POST");
    localStorage.removeItem("csrf-token");
}
