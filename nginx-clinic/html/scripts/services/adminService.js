const BASE_URL = '/scheduling-service/admin';
const VALID_WORKER_ROLES = ['DOCTOR', 'NURSE', 'TECHNICIAN'];

async function fetchApi(endpoint, options = {}) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers: { 'Content-Type': 'application/json' },
    });
    const data = await response.json();
    
    if (!response.ok) {
        throw new Error(data.message || 'Erro ao processar a requisição');
    }
    
    return data;
}

export const adminService = {
    // Função para pesquisar todos os trabalhadores
    async searchWorkers(userRole) {
        if (!VALID_WORKER_ROLES.includes(userRole)) {
            throw new Error(`Role inválida. Use: ${VALID_WORKER_ROLES.join(', ')}`);
        }

        try {
            const data = await fetchApi('/search-workers', {
                method: 'POST',
                body: JSON.stringify({ userRole })
            });

            if (!Array.isArray(data)) {
                throw new Error('Formato de resposta inválido');
            }

            // Validate worker data
            data.forEach(worker => {
                if (!worker.id || !worker.name) {
                    throw new Error('Dados do funcionário incompletos');
                }
                if (userRole === 'DOCTOR' && !worker.specialty) {
                    throw new Error('Especialidade do médico não informada');
                }
            });

            return data;
        } catch (error) {
            console.error('Erro ao buscar funcionários:', error);
            throw error;
        }
    },

    // Função para pesquisar salas disponíveis
    async searchAvailableRoom(room, date) {
        if (!room || !date) {
            throw new Error('Data e sala são obrigatórios');
        }

        try {
            const data = await fetchApi(`/available-room/${room}/${date}`, { 
                method: 'GET' 
            });

            if (!data.hours || !Array.isArray(data.hours)) {
                throw new Error('Formato de resposta inválido');
            }

            return data.hours;
        } catch (error) {
            console.error('Erro ao buscar horários:', error);
            throw error;
        }
    },

    // Função para buscar todas as salas
    async searchAllRooms() {
        try {
            const data = await fetchApi('/rooms', { method: 'GET' });
            
            if (!data.rooms || !Array.isArray(data.rooms)) {
                throw new Error('Formato de resposta inválido');
            }

            return data.rooms;
        } catch (error) {
            console.error('Erro ao buscar salas:', error);
            throw error;
        }
    },

    // Função para adicionar grade
    async createSchedule(workerId, room, date, hour) {
        if (!workerId || !room || !date || !hour) {
            throw new Error('Todos os campos são obrigatórios');
        }

        try {
            const data = await fetchApi('/create-schedules', {
                method: 'POST',
                body: JSON.stringify({ 
                    workerID: workerId,
                    room: room,
                    date: date,
                    hour: hour
                })
            });

            if (data.erro) {
                throw new Error(data.erro);
            }

            return data;
        } catch (error) {
            console.error('Erro ao criar grade:', error);
            throw error;
        }
    },

    // Função para buscar grade por trabalhador
    async searchScheduleByWorker(workerId) {
        try {
            const data = await fetchApi('/search-schedule-worker', {
                method: 'POST',
                body: JSON.stringify({ workerID: workerId })
            });

            if (!Array.isArray(data)) {
                throw new Error('Formato de resposta inválido');
            }

            return data;
        } catch (error) {
            console.error('Erro ao buscar grades:', error);
            throw error;
        }
    },

    // Função para cancelar grade
    async cancellSchedule(scheduleId, reason) {
    try {
        const data = await fetchApi('/cancell-schedule', {
            method: 'POST',
            body: JSON.stringify({
                scheduleID: scheduleId,
                reason: reason
            })
        });

        if (data.error) {
            throw new Error(data.error);
        }

        return data;
    } catch (error) {
        console.error('Erro ao cancelar grade:', error);
        throw error;
    }
}
};