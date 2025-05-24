import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".search-all-room-component");

    component.innerHTML = `
    <div class="search-room-container">
        <h2>Salas Disponíveis</h2>
        <div class="search-room-grid">
            <table>
                <thead>
                    <tr>
                            <th>ID da Sala</th>
                        <th>Número da Sala</th>
                        <th>Tipo de Sala</th>
                            <th>Status</th>
                            <th>ID do Usuário</th>
                    </tr>
                </thead>
                <tbody id="room-list">
                    <tr>
                            <td colspan="5" class="loading">Carregando salas...</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
`;

    const roomList = document.getElementById("room-list");

    function isRoomAvailable(userId) {
        return !userId || userId === '00000000-0000-0000-0000-000000000000';
    }

    async function loadRooms() {
        try {
            const rooms = await makeRequest("/rest-schedule/room/search-all", "GET");
            
            if (rooms && rooms.length > 0) {
                // Ordena as salas: primeiro as disponíveis, depois as ocupadas
                // Em cada grupo, ordena pelo número da sala
                const sortedRooms = rooms.sort((a, b) => {
                    // Primeiro compara por disponibilidade
                    if (isRoomAvailable(a.user_id) && !isRoomAvailable(b.user_id)) return -1;
                    if (!isRoomAvailable(a.user_id) && isRoomAvailable(b.user_id)) return 1;
                    
                    // Se ambas estão disponíveis ou ocupadas, ordena pelo número
                    const numA = parseInt(a.number.trim());
                    const numB = parseInt(b.number.trim());
                    return numA - numB;
                });

                roomList.innerHTML = sortedRooms.map(room => `
                    <tr>
                        <td>${room.room_id}</td>
                        <td>${room.number.trim()}</td>
                        <td>${formatRoomFunction(room.function)}</td>
                        <td>
                            <span class="room-status ${isRoomAvailable(room.user_id) ? 'available' : 'occupied'}">
                                ${isRoomAvailable(room.user_id) ? 'Disponível' : 'Ocupada'}
                            </span>
                        </td>
                        <td>${isRoomAvailable(room.user_id) ? '-' : room.user_id}</td>
                    </tr>
                `).join('');
            } else {
                roomList.innerHTML = `
                    <tr>
                        <td colspan="5" class="loading">Nenhuma sala encontrada</td>
                    </tr>
                `;
            }
        } catch (error) {
            console.error("Erro ao carregar salas:", error);
            roomList.innerHTML = `
                <tr>
                    <td colspan="5" class="loading">Erro ao carregar salas</td>
                </tr>
            `;
        }
    }

    function formatRoomFunction(roomFunction) {
        const functions = {
            'PRIVATE_OFFICE': 'Consultório Particular',
            'SURGERY': 'Sala de Cirurgia',
            'CONSULTATION': 'Sala de Consulta'
        };
        return functions[roomFunction] || roomFunction;
    }

    // Carrega as salas ao iniciar
    loadRooms();
});

// Preciso criar a função para ver a salas que existem.
// API /rest-schedule/room/search-all
// Json de saida:
//[
//	{
//		"room_id": "2326b63e-07d5-4932-9012-5c276a5bb7cc",
//		"number": "2  ",
//		"function": "PRIVATE_OFFICE",
//		"user_id": "066697fc-e9e7-4326-9acb-f09eae81fb19"
//	},
//	{
//		"room_id": "048d5bcd-b8d1-4bef-a5c0-1f834dd00093",
//		"number": "3  ",
//		"function": "CONSULTATION",
//		"user_id": null
//	},
//	{
//		"room_id": "309ecac3-ed0e-4045-b67e-fc4de2e41975",
//		"number": "4  ",
//		"function": "PRIVATE_OFFICE",
//		"user_id": "82f8f7c6-71a4-44f2-95e3-930c891a9bae"
//	},
//	{
//		"room_id": "5cbb4dc8-52bc-4b37-b07a-6dd49920f56b",
//		"number": "1  ",
//		"function": "PRIVATE_OFFICE",
//		"user_id": "49bed682-969c-45ad-889b-ab11bc574c19"
//	}
//]

