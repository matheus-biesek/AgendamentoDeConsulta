const BASE_URL = '/scheduling-service/admin';

const getHeaders = () => ({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`
});

async function fetchApi(endpoint, options = {}) {
    try {
        const response = await fetch(`${BASE_URL}${endpoint}`, {
            ...options,
            headers: getHeaders()
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || `Erro na requisição: ${response.status}`);
        }

        return data;
    } catch (error) {
        console.error(`Erro em ${endpoint}:`, error);
        throw new Error(`Falha na requisição para ${endpoint}: ${error.message}`);
    }
}

//Serviços da API
export const adminService = {
    /**
     * Updates user role
     * @param {string} cpf - User CPF
     * @param {string} role - New role
     */
    // Atualiza papel do usuário
    updateUserRole: async (cpf, role) => {
        return fetchApi('/change-user-role', {
            method: 'POST',
            body: JSON.stringify({ cpf, role })
        });
    },
    /**
     * Creates new user
     * @param {string} username
     * @param {string} password
     * @param {string} cpf
     * @param {string} role
     */

    // Cria novo usuário
    createUser: async (username, password, cpf, role) => {
        return fetchApi('/create-user', {
            method: 'POST',
            body: JSON.stringify({ username, password, cpf, role })
        });
    },

    /**
     * @returns {Promise<Array>} List of rooms
     */
    // Busca todas as salas
    getAllRooms: async () => {
        return fetchApi('/search-all-room', {
            method: 'GET'
        });
    },

    /**
     * @param {number} room
     * @param {string} date 
     * @returns {Promise<Array>} 
     */
    // Busca horários disponíveis
    getAvailableRoom: async (room, date) => {
        return fetchApi('/available-room', {
            method: 'POST',
            body: JSON.stringify({ room, date })
        });
    },

    // ...existing code...

/**
 * Busca trabalhadores por role
 * @param {string} userRole - Role do usuário (DOCTOR, NURSE, TECHNICIAN)
 * @returns {Promise<Array>} Lista de trabalhadores
 */
    searchWorkers: async (userRole) => {
        const validRoles = ['DOCTOR', 'NURSE', 'TECHNICIAN'];
        if (!validRoles.includes(userRole)) {
            throw new Error(`Role inválida. Use: ${validRoles.join(', ')}`);
        }

        try {
            const workers = await fetchApi('/search-workers', {
                method: 'POST',
                body: JSON.stringify({ userRole })
            });

            // Validate response format
            if (!Array.isArray(workers)) {
                throw new Error('Formato de resposta inválido');
            }

            // Validate each worker object
            workers.forEach(worker => {
                if (!worker.id || !worker.name) {
                    throw new Error('Dados do funcionário incompletos');
                }
            });

            return workers;
        } catch (error) {
            console.error('Erro ao buscar funcionários:', error);
            throw error;
        }
    }
};