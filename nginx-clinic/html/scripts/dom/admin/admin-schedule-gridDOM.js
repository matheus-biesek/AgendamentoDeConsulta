import { getAllRooms } from '../../controllers/adminController.js';

//Search Rooms
function translateRoomRole(role) {
    const translations = {
        'ATTENDANCE': 'Atendimento',
        'SURGERY': 'Cirurgia',
        'EXAM': 'Exame'
    };
    return translations[role] || role;
}

async function updateRoomList() {
    try {
        const rooms = await getAllRooms();
        const roomList = document.getElementById('room-list');
        
        roomList.innerHTML = rooms.map(room => `
            <tr>
                <td>${room.number}</td>
                <td>${translateRoomRole(room.roomRole)}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao atualizar lista de salas:', error);
    }
}

// Atualização inicial
updateRoomList();

// Atualização automática a cada 5 segundos
setInterval(updateRoomList, 5000);