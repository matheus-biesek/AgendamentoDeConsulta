import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".update-user-data-component");
    
    component.innerHTML = `
    <h2>Atualizar Dados do Usuário</h2>
        <div class="search-section">
            <label for="updateUserCpf">CPF do Usuário:</label>
            <input type="text" id="updateUserCpf" name="updateUserCpf" placeholder="Digite o CPF do usuário" required>
            <button type="button" id="searchUserBtn">Buscar Usuário</button>
        </div>
        
        <form id="updateUserDataForm" style="display: none;">
            <div class="form-group">
                <label for="updateUserName">Nome:</label>
                <input type="text" id="updateUserName" name="updateUserName" required>
            </div>
            
            <div class="form-group">
                <label for="updateUserEmail">Email:</label>
                <input type="email" id="updateUserEmail" name="updateUserEmail" required>
            </div>
            
            <div class="form-group">
                <label for="updateUserGender">Gênero:</label>
                <select id="updateUserGender" name="updateUserGender" required>
                    <option value="">Selecione o gênero</option>
                    <option value="MALE">Masculino</option>
                    <option value="FEMALE">Feminino</option>
                    <option value="OTHER">Outro</option>
                </select>
            </div>
            
            <button type="submit" id="updateUserBtn">Atualizar Dados</button>
    </form>
        
        <div class="message" id="updateUserMessage"></div>
    `;

    // Referências aos elementos
    const searchBtn = document.getElementById("searchUserBtn");
    const updateForm = document.getElementById("updateUserDataForm");
    const cpfInput = document.getElementById("updateUserCpf");
    const nameInput = document.getElementById("updateUserName");
    const emailInput = document.getElementById("updateUserEmail");
    const genderSelect = document.getElementById("updateUserGender");
    const messageDiv = document.getElementById("updateUserMessage");

    function showMessage(text, isSuccess) {
        messageDiv.textContent = text;
        messageDiv.className = `message ${isSuccess ? 'success' : 'error'}`;
        messageDiv.style.display = 'block';
    }

    async function searchUser() {
        const cpf = cpfInput.value.trim();
        
        if (!cpf) {
            showMessage("Por favor, insira o CPF do usuário", false);
            return;
        }

        try {
            const response = await makeRequest(
                "/rest-auth/user-management/search-user-data",
                "POST",
                { cpf }
            );

            if (response) {
                // Preenche o formulário com os dados do usuário
                nameInput.value = response.name || '';
                emailInput.value = response.email || '';
                genderSelect.value = response.gender || '';
                
                // Mostra o formulário
                updateForm.style.display = 'block';
                showMessage("Usuário encontrado. Preencha os campos que deseja atualizar.", true);
            }
        } catch (error) {
            showMessage(error.message || "Erro ao buscar usuário", false);
            updateForm.style.display = 'none';
        }
    }

    async function updateUserData(event) {
        event.preventDefault();
        
        const cpf = cpfInput.value.trim();
        const name = nameInput.value.trim();
        const email = emailInput.value.trim();
        const gender = genderSelect.value;

        if (!name || !email || !gender) {
            showMessage("Por favor, preencha todos os campos", false);
            return;
        }

        try {
            const response = await makeRequest(
                "/rest-auth/user-management/update-user-data",
                "POST",
                { cpf, name, email, gender }
            );

            if (response && response.message) {
                showMessage(response.message, true);
                // Limpa o formulário após sucesso
                updateForm.reset();
                updateForm.style.display = 'none';
                cpfInput.value = '';
            }
        } catch (error) {
            showMessage(error.message || "Erro ao atualizar dados do usuário", false);
        }
    }

    // Event listeners
    searchBtn.addEventListener("click", searchUser);
    updateForm.addEventListener("submit", updateUserData);
});

// colocar o input do cpf separado do form, a ideia e so na parte visual do usuario entender que o cpf e o meio de localizar para alterar os daods
// todos os dados sao obrigatorios.
// Função para atualizar os dados do usuário
// será usado o makeRequest do request.js para fazer a requisição
// API: /rest-auth/user-management/update-user-data
// Json de entrada:
//{
//	"name": "admin-boss",
//	"cpf": "12345678900",
//	"email": "teste0@teste0.com",
//	"gender": "MALE"
//}

// ou 

//{
//	"name": "admin-boss",
//	"cpf": "12345678900",
//	"email": "teste0@teste0.com",
//	"gender": "FEMALE"
//}

// ou

//{
//	"name": "admin-boss",
//	"cpf": "12345678900",
//	"email": "teste0@teste0.com",
//	"gender": "OTHER"
//}

// status OK

// Json de saida:
//{
//	"message": "Usuário atualizado com sucesso"
//}


