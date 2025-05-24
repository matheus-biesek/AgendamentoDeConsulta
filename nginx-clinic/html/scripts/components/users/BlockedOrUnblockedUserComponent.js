import { makeRequest } from "../../utils/request.js";

// Aguarda o DOM estar completamente carregado
document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".blocked-or-unblocked-user-component");
    
    component.innerHTML = `
        <h2>Bloquear ou Desbloquear Usuário</h2>
        <form id="blockedOrUnblockedUserForm">
            <label for="blockUserCpf">CPF do Usuário:</label>
            <input type="text" id="blockUserCpf" name="blockUserCpf" placeholder="Digite o CPF do usuário" required>
            <div class="action-buttons">
                <button type="button" class="block-btn" id="blockBtn">Bloquear Usuário</button>
                <button type="button" class="unblock-btn" id="unblockBtn">Desbloquear Usuário</button>
            </div>
        </form>
        <div class="message" id="blockUserMessage"></div>
    `;

    // Obtém referências aos elementos após a inserção no DOM
    const form = document.getElementById("blockedOrUnblockedUserForm");
    const blockBtn = document.getElementById("blockBtn");
    const unblockBtn = document.getElementById("unblockBtn");
    const messageDiv = document.getElementById("blockUserMessage");
    const cpfInput = document.getElementById("blockUserCpf");

    function showMessage(text, isSuccess) {
        console.log("Mostrando mensagem:", text, "Sucesso:", isSuccess);
        messageDiv.textContent = text;
        messageDiv.className = `message ${isSuccess ? 'success' : 'error'}`;
        messageDiv.style.display = 'block';
    }

    async function handleUserAction(blocked) {
        const cpf = cpfInput.value.trim();
        console.log("CPF inserido:", cpf);
        console.log("Ação:", blocked ? "Bloquear" : "Desbloquear");
        
        if (!cpf) {
            showMessage("Por favor, insira o CPF do usuário", false);
            return;
        }

        try {
            const response = await makeRequest(
                "/rest-auth/user-management/block-or-unblock-user",
                "POST",
                { cpf, blocked }
            );

            // Exibe a mensagem retornada pela API
            if (response && response.message) {
                showMessage(response.message, true);
            } else {
                const defaultMessage = `Usuário ${blocked ? 'bloqueado' : 'desbloqueado'} com sucesso!`;
                console.log("Usando mensagem padrão:", defaultMessage);
                showMessage(defaultMessage, true);
            }
            
            form.reset();
        } catch (error) {
            console.error("Erro completo:", error);
            const errorMessage = error.message || `Erro ao ${blocked ? 'bloquear' : 'desbloquear'} o usuário`;
            showMessage(errorMessage, false);
        }
    }

    // Adiciona os event listeners
    blockBtn.addEventListener("click", () => handleUserAction(true));
    unblockBtn.addEventListener("click", () => handleUserAction(false));
});
