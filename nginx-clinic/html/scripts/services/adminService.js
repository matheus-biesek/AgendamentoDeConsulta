//Update User Role Service
export async function updateUserRoleService(cpf, role) {
    const response = await fetch("/scheduling-service/admin/change-user-role", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ cpf, role })
    });

    if (!response.ok) {
        const responseData = await response.json();
        throw new Error(responseData.message || "Erro ao atualizar a role do usuário.");
    }

    return await response.json(); // Retorna a resposta do backend
}

//Create User Service
export async function createUserService(username, password, cpf, role) {
    const response = await fetch("/scheduling-service/admin/create-user", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password, cpf, role }) // Envia os dados no formato correto
    });

    if (!response.ok) {
        const responseData = await response.json();
        throw new Error(responseData.message || "Erro ao criar o usuário.");
    }

    return await response.json(); // Retorna a resposta do backend
}

//Search Room Service
export async function getAllRoomsService() {
    const response = await fetch("/scheduling-service/admin/search-all-room", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error("Erro ao buscar salas.");
    }

    return await response.json();
}