// scripts/components/navbarAdmin.js
import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    document.querySelector(".navbar").innerHTML = `
        <a href="#" class="logo">
            <img src="../images/logo.png" alt="Logo" />
        </a>
        <ul class="nav-links">
            <li><a href="/admin/admin-schedule-grid.html">Criar grade</a></li>
            <li><a href="/admin/admin-update.html">Atualizar usuários</a></li>
            <li><a href="/role-selection.html">Trocar Perfil</a></li>
            <li><a id="navbar-admin-logout">Logout</a></li>
        </ul>
    `;

    const logoutButton = document.getElementById('navbar-admin-logout');

    async function handleLogout() {
        try {
            // Faz a requisição para o backend
            await makeRequest('/rest-auth/auth-session/logout', 'POST');

            // Remove os dados do localStorage
            localStorage.removeItem('redirectAfterRefresh');
            localStorage.removeItem('userRoles');
            
            // Redireciona para a página de login
            window.location.href = '/index.html';
        } catch (error) {
            console.error('Erro ao fazer logout:', error);
            // Mesmo com erro, remove os dados locais e redireciona
            localStorage.removeItem('redirectAfterRefresh');
            localStorage.removeItem('userRoles');
            window.location.href = '/index.html';
        }
    }

    logoutButton.addEventListener('click', handleLogout);
});
