import { updateUserRoleService, createUserService, getAllRoomsService } from "../services/adminService.js";

// Função para atualizar a role do usuário
export async function updateUserRole(cpf, role) {
    try {
        const response = await updateUserRoleService(cpf, role);
        return response; // Retorna a resposta do backend
    } catch (error) {
        throw new Error(error.message || "Erro ao atualizar a role.");
    }
}

// Função para criar um novo usuário
export async function createUser(username, password, cpf, role) {
    try {
        const response = await createUserService(username, password, cpf, role);
        return response; // Retorna a resposta do backend
    } catch (error) {
        throw new Error(error.message || "Erro ao criar o usuário.");
    }
}

// Função para buscar todas as salas
export async function getAllRooms() {
    try {
        return await getAllRoomsService();
    } catch (error) {
        throw new Error(error.message || "Erro ao buscar salas.");
    }
}