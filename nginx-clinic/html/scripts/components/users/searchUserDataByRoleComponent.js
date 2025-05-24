import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".search-user-data-by-role-component");
    
    component.innerHTML = `
        <div class="search-user-role-container">
            <h2>Buscar Usuários por Função</h2>
            <form id="search-user-role-form">
                <div class="role-form-group">
                    <label for="role">Função:</label>
                    <select id="role" name="role" required>
                        <option value="">Selecione uma função</option>
                        <option value="ADMIN">Administrador</option>
                        <option value="DOCTOR">Médico</option>
                        <option value="NURSE">Enfermeiro</option>
                        <option value="SECRETARY">Secretária</option>
                        <option value="TECHNICIAN">Técnico</option>
                        <option value="PATIENT">Paciente</option>
                    </select>
                </div>
                <div class="role-form-group">
                    <label for="active">Status:</label>
                    <select id="active" name="active" required>
                        <option value="">Selecione um status</option>
                        <option value="true">Ativo</option>
                        <option value="false">Inativo</option>
                    </select>
                </div>
                <button type="submit" class="role-btn">Buscar</button>
            </form>
            <div id="role-message"></div>
            <div id="user-list" class="user-list" style="display: none;">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>CPF</th>
                            <th>Email</th>
                            <th>Gênero</th>
                            <th>Funções</th>
                            <th>Status</th>
                            <th>Bloqueado</th>
                        </tr>
                    </thead>
                    <tbody id="user-list-body"></tbody>
                </table>
            </div>
        </div>
    `;

    const form = document.getElementById('search-user-role-form');
    const messageDiv = document.getElementById('role-message');
    const userList = document.getElementById('user-list');
    const userListBody = document.getElementById('user-list-body');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `role-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'role-message';
        }, 5000);
    }

    function displayUsers(users) {
        if (!users || users.length === 0) {
            showMessage('Nenhum usuário encontrado com os critérios selecionados.');
            userList.style.display = 'none';
            return;
        }

        userListBody.innerHTML = '';
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.cpf}</td>
                <td>${user.email}</td>
                <td>${user.gender || '-'}</td>
                <td>${user.roles ? user.roles.join(', ') : '-'}</td>
                <td>${user.active ? 'Ativo' : 'Inativo'}</td>
                <td>${user.blocked ? 'Sim' : 'Não'}</td>
            `;
            userListBody.appendChild(row);
        });

        userList.style.display = 'block';
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const role = document.getElementById('role').value;
        const active = document.getElementById('active').value;

        if (!role || active === '') {
            showMessage('Por favor, selecione a função e o status.', true);
            return;
        }

        try {
            const data = {
                role: role,
                active: active === 'true'
            };

            const response = await makeRequest('/rest-auth/user-management/get-users-by-role-and-active', 'POST', data);
            displayUsers(response);
        } catch (error) {
            console.error('Erro ao buscar usuários:', error);
            showMessage(error.message || 'Erro ao buscar usuários. Tente novamente.', true);
            userList.style.display = 'none';
        }
    });
});
