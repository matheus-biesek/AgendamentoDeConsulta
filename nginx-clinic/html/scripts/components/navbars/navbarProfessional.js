// scripts/components/navbarProfessional.js
import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    document.querySelector(".navbar").innerHTML = `
        <a href="#" class="logo">
            <img src="../images/logo.png" alt="Logo" />
        </a>
        <ul class="nav-links">
            <li><a href="/professional/professional-grid.html">Grade e consultas</a></li>
            <li><a href="/professional/professional-appointment-start.html">Iniciar consulta</a></li>
            <li><a href="/role-selection.html">Trocar Perfil</a></li>
            <li><a id="navbar-professional-logout">Logout</a></li>
        </ul>
    `;

    const logoutButton = document.getElementById('navbar-professional-logout');

    async function handleLogout() {
        try {
            // Faz a requisição para o backend
            await makeRequest('/rest-auth/auth-session/logout', 'POST');

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

