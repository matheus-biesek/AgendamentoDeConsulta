// scripts/components/navbarSecretary.js
import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    document.querySelector(".navbar").innerHTML = `
        <a href="#" class="logo">
            <img src="../images/logo.png" alt="Logo" />
        </a>
        <ul class="nav-links">
            <li><a href="/secretary/secretary-cancell-appointment.html">Cancelar consulta</a></li>
            <li><a href="/secretary/secretary-create-patient.html">Criar paciente</a></li>
            <li><a href="/secretary/secretary-making-appointment.html">Agendar consulta</a></li>
            <li><a href="/role-selection.html">Trocar Perfil</a></li>
            <li><a id="navbar-secretary-logout">Logout</a></li>
        </ul>
    `;

    const logoutButton = document.getElementById('navbar-secretary-logout');

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