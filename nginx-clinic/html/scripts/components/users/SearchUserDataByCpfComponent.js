import { makeRequest } from "../../utils/request.js";

document.querySelector(".search-user-data-by-cpf-component").innerHTML = `
    <h2>Buscar Dados do Usuário por CPF</h2>
    <form id="searchUserForm">
        <label for="cpf">CPF:</label>
        <input type="text" id="cpf" placeholder="Digite o CPF do usuário" required>
        <button type="submit">Buscar</button>
    </form>
    <div class="user-data-display">
        <p><strong>ID:</strong> <span id="userId"></span></p>
        <p><strong>Nome:</strong> <span id="userName"></span></p>
        <p><strong>CPF:</strong> <span id="userCpf"></span></p>
        <p><strong>Email:</strong> <span id="userEmail"></span></p>
        <p><strong>Gênero:</strong> <span id="userGender"></span></p>
        <div class="status-container">
            <p><strong>Status:</strong> 
                <span id="userStatus" class="status-badge"></span>
            </p>
            <p><strong>Bloqueio:</strong> 
                <span id="userBlocked" class="status-badge"></span>
            </p>
        </div>
        <div class="roles" id="userRoles"></div>
    </div>
`;

const form = document.getElementById("searchUserForm");
const userDataDisplay = document.querySelector(".user-data-display");

form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const cpf = document.getElementById("cpf").value;

    try {
        const userData = await makeRequest(
            "/rest-auth/user-management/search-user-data",
            "POST",
            { cpf }
        );
        
        displayUserData(userData);
    } catch (error) {
        console.error("Erro:", error);
        alert("Erro ao buscar dados do usuário");
    }
});

function displayUserData(userData) {
    document.getElementById("userId").textContent = userData.id;
    document.getElementById("userName").textContent = userData.name;
    document.getElementById("userCpf").textContent = userData.cpf;
    document.getElementById("userEmail").textContent = userData.email;
    document.getElementById("userGender").textContent = userData.gender;
    
    // Atualiza o status de ativação
    const statusElement = document.getElementById("userStatus");
    statusElement.textContent = userData.active ? "Ativo" : "Inativo";
    statusElement.className = `status-badge ${userData.active ? 'active' : 'inactive'}`;
    
    // Atualiza o status de bloqueio
    const blockedElement = document.getElementById("userBlocked");
    blockedElement.textContent = userData.blocked ? "Bloqueado" : "Desbloqueado";
    blockedElement.className = `status-badge ${userData.blocked ? 'blocked' : 'unblocked'}`;

    const rolesContainer = document.getElementById("userRoles");
    rolesContainer.innerHTML = "";
    userData.roles.forEach(role => {
        const roleTag = document.createElement("span");
        roleTag.className = "role-tag";
        roleTag.textContent = role;
        rolesContainer.appendChild(roleTag);
    });

    userDataDisplay.classList.add("active");
}
