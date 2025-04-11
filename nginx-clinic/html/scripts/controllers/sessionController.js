// scripts/controllers/sessionController.js

import { loginSession, logoutSession } from "../services/sessionService.js";

export async function handleLogin() {
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!username || !password) {
        alert("Preencha todos os campos");
        return;
    }

    try {
        const nextPage = await loginSession(username, password);
        window.location.href = nextPage;
    } catch (error) {
        alert("Erro ao tentar fazer login.");
    }
}

export async function handleLogout() {
    try {
        await logoutSession();
        window.location.href = "/index.html";
    } catch (error) {
        alert("Erro ao tentar fazer logout.");
    }
}
