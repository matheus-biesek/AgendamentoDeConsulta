import { makeRequest } from "../../utils/request.js";

document.querySelector(".change-user-role-component").innerHTML = `
    <div class="update-role-container">
        <h2>Alterar Role do Usuário</h2>
        <form id="update-role-form">
            <div class="form-group">
                <label for="user-id">ID:</label>
                <input type="text" id="user-id" placeholder="Digite o ID do usuário" required>
            </div>
            <div class="form-group">
                <div class="role-options">
                    <input type="radio" id="admin-change" name="role-change" value="ADMIN" required>
                    <label for="admin-change">Admin</label>

                    <input type="radio" id="doctor-change" name="role-change" value="DOCTOR">
                    <label for="doctor-change">Doutor</label>

                    <input type="radio" id="patient-change" name="role-change" value="PATIENT">
                    <label for="patient-change">Paciente</label>

                    <input type="radio" id="technician-change" name="role-change" value="TECHNICIAN">
                    <label for="technician-change">Técnico</label>

                    <input type="radio" id="secretary-change" name="role-change" value="SECRETARY">
                    <label for="secretary-change">Secretário</label>

                    <input type="radio" id="nurse-change" name="role-change" value="NURSE">
                    <label for="nurse-change">Enfermeiro</label>
                </div>
            </div>
            <div class="form-group">
                <label>Ação:</label>
                <select id="role-action" required>
                    <option value="">Selecione a ação</option>
                    <option value="add">Adicionar Role</option>
                    <option value="remove">Remover Role</option>
                </select>
            </div>
            <button type="submit" id="update-role-button">Alterar</button>
        </form>
    </div>
`;

// Função para adicionar role ao usuário
async function addRoleToUser(userId, role) {
    try {
        const response = await makeRequest('/rest-auth/user-management/add-role-to-user', 'POST', {
            id: userId,
            role: role
        });
        
        if (response) {
            alert('Função adicionada ao usuário com sucesso!');
            window.location.reload();
        }
    } catch (error) {
        alert(`Erro ao adicionar função: ${error.message}`);
        console.error('Erro:', error);
    }
}

// Função para remover role do usuário
async function removeRoleFromUser(userId, role) {
    try {
        const response = await makeRequest('/rest-auth/user-management/remove-role-from-user', 'DELETE', {
            id: userId,
            role: role
        });
        
        if (response) {
            alert('Função removida do usuário com sucesso!');
            window.location.reload();
        }
    } catch (error) {
        alert(`Erro ao remover função: ${error.message}`);
        console.error('Erro:', error);
    }
}

// Event Listener para o formulário
document.getElementById('update-role-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const userId = document.getElementById('user-id').value;
    const role = document.querySelector('input[name="role-change"]:checked')?.value;
    const action = document.getElementById('role-action').value;

    if (!userId || !role || !action) {
        alert('Por favor, preencha todos os campos!');
        return;
    }

    if (action === 'add') {
        await addRoleToUser(userId, role);
    } else {
        await removeRoleFromUser(userId, role);
    }
});