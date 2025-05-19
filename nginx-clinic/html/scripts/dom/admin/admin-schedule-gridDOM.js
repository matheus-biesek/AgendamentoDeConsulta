import { searchAllRooms, searchAvailableRooms, searchWorkers, createSchedule, searchSchedule, cancellSchedule } from '../../controllers/adminController.js';

class AdminScheduleGrid {
    constructor() {
        this.updateInterval = null;
        this.roles = {
            'ATTENDANCE': 'Atendimento',
            'SURGERY': 'Cirurgia'
        };
        this.workerRoles = {
            'DOCTOR': 'Médico',
            'NURSE': 'Enfermeiro',
            'TECHNICIAN': 'Técnico'
        };
    }

    init() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.initializeComponents());
        } else {
            this.initializeComponents();
        }
    }

    initializeComponents() {
        this.fetchAndUpdateRooms();
        this.updateInterval = setInterval(() => this.fetchAndUpdateRooms(), 5000);
        this.initAvailableRoom();
        this.setupCleanup();
        this.initSearchWorkers();
        this.initScheduleDateTime();
        this.initSearchSchedule();
        this.initCancellSchedule();
    }

    // Métodos Genéricos de Utilidade
    validateFormInputs(inputs, customValidations = []) {
        if (Object.values(inputs).some(input => !input?.value)) {
            alert('Por favor, preencha todos os campos');
            return false;
        }
        return customValidations.every(validation => validation(inputs));
    }

    clearFormInputs(inputs) {
        Object.values(inputs).forEach(input => input.value = '');
    }

    showError(elementId, message, colspan = 3) {
        const element = document.getElementById(elementId);
        if (element) {
            element.innerHTML = `<tr><td colspan="${colspan}" class="error-message">${message}</td></tr>`;
        }
    }

    setLoading(element, message = 'Carregando...', colspan = 3) {
        if (element) {
            element.innerHTML = `<tr><td colspan="${colspan}">${message}</td></tr>`;
        }
    }

    validateDate(date) {
        const selectedDate = new Date(date);
        const today = new Date();
        if (selectedDate < today) {
            alert('A data selecionada não pode ser anterior à data atual');
            return false;
        }
        return true;
    }

    // Manipulação de Salas
    async fetchAndUpdateRooms() {
        try {
            const rooms = await searchAllRooms();
            this.updateRoomTable(rooms);
        } catch (error) {
            console.error('Erro ao atualizar tabela:', error);
            this.showError('room-list', 'Erro ao carregar salas.');
        }
    }

    updateRoomTable(rooms) {
        const roomList = document.getElementById('room-list');
        if (!roomList) return;

        roomList.innerHTML = rooms.map(room => `
            <tr>
                <td>${room.number}</td>
                <td>${this.roles[room.roomRole] || room.roomRole}</td>
            </tr>
        `).join('');
    }

    // Disponibilidade de Salas
    initAvailableRoom() {
        const elements = {
            searchButton: document.getElementById('search-availability'),
            dateInput: document.getElementById('reference-date'),
            roomNumber: document.getElementById('room-number')
        };

        if (!this.validateFormInputs(elements)) return;

        this.setCurrentDate(elements.dateInput);
        elements.roomNumber.value = elements.roomNumber.value || '1';

        this.handleSearch();
        elements.searchButton.addEventListener('click', () => this.handleSearch());
    }

    // Busca de Funcionários
    initSearchWorkers() {
        const searchButton = document.getElementById('search-worker-button');
        if (searchButton) {
            searchButton.addEventListener('click', () => this.handleWorkerSearch());
        }
    }

    async handleWorkerSearch() {
        const elements = {
            selectedRole: document.querySelector('input[name="worker-type"]:checked'),
            workersResults: document.getElementById('workers-results'),
            workersTable: document.querySelector('.workers-table')
        };

        if (!elements.selectedRole) {
            alert('Por favor, selecione um tipo de funcionário');
            return;
        }

        try {
            elements.workersTable.style.display = 'block';
            this.setLoading(elements.workersResults);

            const workers = await searchWorkers(elements.selectedRole.value);

            if (!workers?.length) {
                this.showError('workers-results', 
                    `Nenhum ${this.workerRoles[elements.selectedRole.value]} encontrado`);
                return;
            }

            elements.workersResults.innerHTML = workers.map(worker => `
                <tr>
                    <td>${worker.id}</td>
                    <td>${worker.name}</td>
                    <td>${worker.specialty || '-'}</td>
                </tr>
            `).join('');

        } catch (error) {
            console.error('Erro:', error);
            this.showError('workers-results', error.message);
        }
    }

    // Criação de Grade
    async createSchedule() {
        const elements = {
            workerId: document.getElementById('worker-id'),
            roomNumber: document.getElementById('room-number'),
            date: document.getElementById('schedule-date'),
            time: document.getElementById('schedule-time')
        };

        if (!this.validateFormInputs(elements, [
            inputs => this.validateDate(inputs.date.value)
        ])) return;

        try {
            const result = await createSchedule({
                workerID: Number(elements.workerId.value),
                room: Number(elements.roomNumber.value),
                date: elements.date.value,
                hour: elements.time.value
            });

            alert(result.message);
            this.clearFormInputs(elements);

        } catch (error) {
            console.error('Erro:', error);
            alert(error.message || 'Erro ao criar a grade');
        }
    }

    // Busca de Grade
    async handleScheduleSearch() {
        const workerId = document.getElementById('worker-id')?.value;
        const scheduleResults = document.getElementById('schedule-results');
        const scheduleTable = document.querySelector('.schedule-table');

        if (!workerId) {
            alert('Por favor, insira o ID do funcionário');
            return;
        }

        try {
            scheduleTable.style.display = 'block';
            this.setLoading(scheduleResults, 'Carregando...', 6);

            const schedules = await searchSchedule(Number(workerId));

            if (!schedules?.length) {
                this.showError('schedule-results', 'Nenhuma grade encontrada', 6);
                return;
            }

            scheduleResults.innerHTML = schedules.map(schedule => `
                <tr>
                    <td>${schedule.schedulesID}</td>
                    <td>${schedule.status}</td>
                    <td>${schedule.room}</td>
                    <td>${schedule.roomRole}</td>
                    <td>${schedule.date}</td>
                    <td>${schedule.hour}</td>
                </tr>
            `).join('');

        } catch (error) {
            console.error('Erro:', error);
            this.showError('schedule-results', error.message, 6);
        }
    }

    // Cancelamento de Grade
    initCancellSchedule() {
        const form = document.getElementById('cancell-schedule-form');
        if (form) {
            form.addEventListener('submit', (e) => this.handleCancellSchedule(e));
        }
    }

    async handleCancellSchedule(event) {
        event.preventDefault();

        const elements = {
            scheduleId: document.getElementById('schedule-id'),
            reason: document.getElementById('cancellation-reason')
        };

        if (!this.validateFormInputs(elements)) return;

        try {
            const result = await cancellSchedule(
                Number(elements.scheduleId.value),
                elements.reason.value
            );

            alert(result.message);
            this.clearFormInputs(elements);

        } catch (error) {
            console.error('Erro:', error);
            alert(error.message || 'Erro ao cancelar grade');
        }
    }

    // Utilitários
    setCurrentDate(dateInput) {
        const brazilTime = new Date(new Date().toLocaleString('en-US', {
            timeZone: 'America/Sao_Paulo'
        }));

        dateInput.value = brazilTime.toISOString().split('T')[0];
    }

    setupCleanup() {
        window.addEventListener('unload', () => {
            if (this.updateInterval) clearInterval(this.updateInterval);
        });
    }
}

// Inicialização
const adminGrid = new AdminScheduleGrid();
adminGrid.init();