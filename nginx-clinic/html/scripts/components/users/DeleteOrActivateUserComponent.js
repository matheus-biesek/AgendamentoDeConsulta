import { makeRequest } from "../../utils/request.js";

// Aguarda o DOM estar completamente carregado
document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".delete-or-activate-user-component");
    
    component.innerHTML = `
    <h2>Deletar ou Ativar Usuário</h2>
    <form id="delete-or-activate-user-form">
            <label for="delete-or-activate-user-cpf">CPF do Usuário:</label>
            <input type="text" id="delete-or-activate-user-cpf" placeholder="Digite o CPF do usuário" required>
            <div class="action-buttons">
                <button type="button" class="activate-btn" id="delete-or-activate-user-activate-btn">Ativar Usuário</button>
                <button type="button" class="delete-btn" id="delete-or-activate-user-delete-btn">Deletar Usuário</button>
            </div>
    </form>
        <div class="message" id="delete-or-activate-user-message"></div>
    `;

    // Obtém referências aos elementos após a inserção no DOM
    const form = document.getElementById("delete-or-activate-user-form");
    const activateBtn = document.getElementById("delete-or-activate-user-activate-btn");
    const deleteBtn = document.getElementById("delete-or-activate-user-delete-btn");
    const messageDiv = document.getElementById("delete-or-activate-user-message");
    const cpfInput = document.getElementById("delete-or-activate-user-cpf");

    function showMessage(text, isSuccess) {
        messageDiv.textContent = text;
        messageDiv.className = `message ${isSuccess ? 'success' : 'error'}`;
    }

    async function handleUserAction(active) {
        const cpfDeleteOrActivate = cpfInput.value.trim();
        
        if (!cpfDeleteOrActivate) {
            showMessage("Por favor, insira o CPF do usuário", false);
            return;
        }

        try {
            const cpf = cpfDeleteOrActivate;
            const response = await makeRequest(
                "/rest-auth/user-management/delete-or-activate-user",
                "POST",
                { cpf, active }
            );

            showMessage(response.message, true);
            form.reset();
        } catch (error) {
            console.error("Erro:", error);
            showMessage(error.message || "Erro ao processar a ação", false);
        }
    }

    // Adiciona os event listeners
    activateBtn.addEventListener("click", () => handleUserAction(true));
    deleteBtn.addEventListener("click", () => handleUserAction(false));
});

// Função para deletar ou ativar um usuário
// preciso criar a função que usar a função makeRequest do request.js para deletar ou ativar um usuário.
// necressario saber se é para deletar ou ativar o usuario
// Json de entrada:
//{
//	"cpf": "12345678901",
//	"active": "true"
//}
// a API retorna isto aqui:
//{
//	"message": "Usuário ativado com sucesso"
//}
// status 200
