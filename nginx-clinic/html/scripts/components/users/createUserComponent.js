import { makeRequest } from "../../utils/request.js";

document.querySelector(".create-user-component").innerHTML = `
        <h2>Criar Usuário</h2>
        <form id="create-user-form" novalidate>
            <div class="form-group">
                <label for="name-create">Nome de Usuário:</label>
                <input type="text" id="name-create" placeholder="Digite o nome de usuário" required>

                <label for="cpf-create">CPF:</label>
                <input type="text" id="cpf-create" placeholder="Digite o CPF do usuário" required>

                <label for="email-create">Email:</label>
                <input type="email" id="email-create" placeholder="Digite o email do usuário" required>

                <label for="password-create">Senha:</label>
                <input type="password" id="password-create" placeholder="Digite a senha" required>

                <label for="confirm-password-create">Confirma Senha:</label>
                <input type="password" id="confirm-password-create" placeholder="Confirme a senha" required>

                <div class="form-group">
                    <label for="role">Tipo de Usuário:</label>
                    <select id="role" required>
                        <option value="">Selecione o tipo de usuário</option>
                        <option value="PATIENT">Paciente</option>
                        <option value="">Usuário sem cargo</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="gender">Sexo:</label>
                    <select id="gender" required>
                        <option value="">Selecione o sexo</option>
                        <option value="MALE">Masculino</option>
                        <option value="FEMALE">Feminino</option>
                        <option value="OTHER">Outro</option>
                    </select>
                </div>
            </div>

            <button type="submit" id="create-user-button">Criar</button>
        </form>
`;

// Função para criar usuário
async function createUser(userData) {
    try {
        const response = await makeRequest('/rest-auth/user-management/register-user', 'POST', userData);
        
        if (response) {
            alert('Usuário criado com sucesso!');
            window.location.reload();
        }
    } catch (error) {
        alert(`Erro ao criar usuário: ${error.message}`);
        console.error('Erro:', error);
    }
}

// Event Listener para o formulário
document.getElementById('create-user-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const password = document.getElementById('password-create').value;
    const confirmPassword = document.getElementById('confirm-password-create').value;

    if (password !== confirmPassword) {
        alert('As senhas não coincidem!');
        return;
    }

    const userData = {
        name: document.getElementById('name-create').value,
        cpf: document.getElementById('cpf-create').value,
        email: document.getElementById('email-create').value,
        password: password,
        gender: document.getElementById('gender').value
    };

    // Adiciona role apenas se não for vazio
    const role = document.getElementById('role').value;
    if (role) {
        userData.role = role;
    }

    await createUser(userData);
});