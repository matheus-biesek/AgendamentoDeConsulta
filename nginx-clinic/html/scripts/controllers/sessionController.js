// scripts/controllers/sessionController.js

import { loginSession, logoutSession, navigateToRoute } from "../services/sessionService.js";

export async function handleLogin() {
    const cpf = document.getElementById("cpf").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!cpf || !password) {
        alert("Preencha todos os campos");
        return;
    }

    try {
        const roles = await loginSession(cpf, password);
        
        // Salva as roles no localStorage
        localStorage.setItem('userRoles', JSON.stringify(roles));
        
        // Se for admin ou secretário, sempre vai para a página de seleção
        if (roles.includes('admin') || roles.includes('secretary')) {
            window.location.href = 'role-selection.html';
        }
        // Se tiver apenas uma role e não for admin/secretário, redireciona diretamente
        else if (roles.length === 1) {
            navigateToRoute(roles[0]);
        } else {
            // Se tiver múltiplas roles, redireciona para a página de seleção
            window.location.href = 'role-selection.html';
        }
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
