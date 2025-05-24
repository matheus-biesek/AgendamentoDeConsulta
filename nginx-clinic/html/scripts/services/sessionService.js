// scripts/services/sessionService.js
import { makeRequest } from "../utils/request.js";

const routes = {
    "admin": "admin/admin-update.html",
    "secretary": "secretary/secretary-making-appointment.html",
    "professional": "professional/professional-grid.html",
    "patient": "patient/patient-info.html"
};

export async function loginSession(cpf, password) {
    const response = await makeRequest("/rest-auth/auth-session/login", "POST", { cpf, password });
    return response;
}

export async function logoutSession() {
    await makeRequest("/rest-auth/auth-session/logout", "POST");
    localStorage.removeItem("csrf-token");
}

export function getAvailableRoutes(roles) {
    const availableRoutes = new Set();

    // Verifica cada role individualmente
    if (roles.includes("admin")) {
        availableRoutes.add("admin");
        availableRoutes.add("professional");
    }

    if (roles.includes("secretary")) {
        availableRoutes.add("secretary");
        availableRoutes.add("professional");
    }

    if (roles.includes("doctor") || roles.includes("nurse") || roles.includes("technician")) {
        availableRoutes.add("professional");
    }

    if (roles.includes("patient")) {
        availableRoutes.add("patient");
    }

    return Array.from(availableRoutes);
}

export function navigateToRoute(role) {
    // Se for um profissional da saúde, redireciona para a página de profissional
    if (role === "doctor" || role === "nurse" || role === "technician") {
        window.location.href = routes["professional"];
        return;
    }

    // Para outras roles, verifica se existe uma rota específica
    if (routes[role]) {
        window.location.href = routes[role];
    } else {
        window.location.href = "blocked.html";
    }
}

// preciso alterar a função caso as condições a baixo nao seja atendida
// quando o usuario tiver uma destas roles: doctor,nurse,secretary,technician,admin terá a opção de rota dos professionais 

// 