import { 
    updateUserRoleService, 
    createUserService, 
    getAllRooms, 
    getAvailableRoomByDate, 
    searchWorkers as searchWorkersService,
    createSchedule as createScheduleService,
    searchScheduleByWorker as searchScheduleService 
} from "../services/adminService.js";

const ERROR_MESSAGES = {
    UPDATE_ROLE: "Erro ao atualizar a role.",
    CREATE_USER: "Erro ao criar o usuário.",
    INVALID_FORMAT: "Formato de resposta inválido",
    ROOMS_ERROR: "Não foi possível carregar as salas",
    SCHEDULE_ERROR: "Não foi possível buscar horários disponíveis",
    WORKERS_ERROR: "Não foi possível buscar os trabalhadores",
    GRID_ERROR: "Não foi possível criar a grade"
};

export {
    updateUserRole,
    createUser,
    searchAllRooms,
    searchAvailableRooms,
    searchWorkers,
    createSchedule,
    searchSchedule,
    cancellSchedule
};

async function updateUserRole(cpf, role) {
    try {
        return await updateUserRoleService(cpf, role);
    } catch (error) {
        console.error('Erro no controller:', error);
        throw new Error(error.message || ERROR_MESSAGES.UPDATE_ROLE);
    }
}

async function createUser(username, password, cpf, role) {
    try {
        return await createUserService(username, password, cpf, role);
    } catch (error) {
        console.error('Erro no controller:', error);
        throw new Error(error.message || ERROR_MESSAGES.CREATE_USER);
    }
}

async function searchAllRooms() {
    try {
        const rooms = await getAllRooms();
        if (!Array.isArray(rooms)) {
            throw new Error(ERROR_MESSAGES.INVALID_FORMAT);
        }
        return rooms;
    } catch (error) {
        console.error('Erro no controller:', error);
        throw new Error(ERROR_MESSAGES.ROOMS_ERROR);
    }
}

async function searchAvailableRooms(roomNumber, date) {
    try {
        const availableHours = await getAvailableRoomByDate(roomNumber, date);
        if (!Array.isArray(availableHours)) {
            throw new Error(ERROR_MESSAGES.INVALID_FORMAT);
        }
        return availableHours;
    } catch (error) {
        console.error("Erro no controller:", error);
        throw new Error(ERROR_MESSAGES.SCHEDULE_ERROR);
    }
}

async function searchWorkers(userRole) {
    try {
        return await searchWorkersService(userRole);
    } catch (error) {
        console.error('Erro no controller:', error);
        throw new Error(ERROR_MESSAGES.WORKERS_ERROR);
    }
}

// Função para criar uma nova grade de horários
async function createSchedule(scheduleData) {
    try {
        // Validate input data
        if (!scheduleData.workerID || !scheduleData.room || !scheduleData.date || !scheduleData.hour) {
            throw new Error('Todos os campos são obrigatórios');
        }

        const response = await createScheduleService(
            scheduleData.workerID,
            scheduleData.room,
            scheduleData.date,
            scheduleData.hour
        );

        return {
            success: true,
            message: response.message || 'Grade adicionada com sucesso!'
        };
    } catch (error) {
        console.error('Erro no controller:', error);
        throw new Error(error.message || ERROR_MESSAGES.GRID_ERROR);
    }
}

/// Função para buscar horários de um trabalhador específico
async function searchSchedule(workerId) {
    try {
        if (!workerId) {
            throw new Error('ID do funcionário é obrigatório');
        }
        return await searchScheduleService(workerId);
    } catch (error) {
        console.error('Erro no controller:', error);
        throw new Error(error.message || ERROR_MESSAGES.SCHEDULE_ERROR);
    }
}

// Função para cancelar uma grade de horários
async function cancellSchedule(scheduleId, reason) {
    try {
        if (!scheduleId || !reason) {
            throw new Error('ID da grade e motivo são obrigatórios');
        }

        const response = await adminService.cancelSchedule(scheduleId, reason);
        return {
            success: true,
            message: response.message || 'Grade cancelada com sucesso!'
        };
    } catch (error) {
        console.error('Erro no controller:', error);
        throw new Error(error.message || 'Erro ao cancelar grade');
    }
}