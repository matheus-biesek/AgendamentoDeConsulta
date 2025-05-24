import { makeRequest } from "../../utils/request.js";

document.addEventListener('DOMContentLoaded', () => {
    const component = document.querySelector(".find-template-by-user-id-component");
    
    component.innerHTML = `
        <div class="find-template-container">
            <h2>Buscar Templates de Agendamento</h2>
            <form id="find-template-by-user-form">
                <div class="template-form-group">
                    <label for="find-template-user-id">ID do Usuário:</label>
                    <input type="text" id="find-template-user-id" name="find-template-user-id" required>
                </div>
                <button type="submit" class="template-btn">Buscar Templates</button>
            </form>
            <div id="find-template-message"></div>
            <div id="find-template-list" class="template-list" style="display: none;">
                <table>
                    <thead>
                        <tr>
                            <th>ID do Template</th>
                            <th>ID da Sala</th>
                            <th>Dia da Semana</th>
                            <th>Horário Início</th>
                            <th>Horário Término</th>
                            <th>Duração (min)</th>
                        </tr>
                    </thead>
                    <tbody id="find-template-list-body"></tbody>
                </table>
            </div>
        </div>
    `;

    const form = document.getElementById('find-template-by-user-form');
    const messageDiv = document.getElementById('find-template-message');
    const templateList = document.getElementById('find-template-list');
    const templateListBody = document.getElementById('find-template-list-body');

    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `template-message ${isError ? 'error' : 'success'}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'template-message';
        }, 5000);
    }

    function getDayOfWeekInPortuguese(day) {
        const days = {
            'MONDAY': 'Segunda-feira',
            'TUESDAY': 'Terça-feira',
            'WEDNESDAY': 'Quarta-feira',
            'THURSDAY': 'Quinta-feira',
            'FRIDAY': 'Sexta-feira',
            'SATURDAY': 'Sábado',
            'SUNDAY': 'Domingo'
        };
        return days[day] || day;
    }

    function displayTemplates(templates) {
        if (!templates || templates.length === 0) {
            showMessage('Nenhum template encontrado para este usuário.');
            templateList.style.display = 'none';
            return;
        }

        templateListBody.innerHTML = '';
        templates.forEach(template => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${template.scheduleTemplateId}</td>
                <td>${template.roomId}</td>
                <td>${getDayOfWeekInPortuguese(template.dayOfWeek)}</td>
                <td>${template.startTime}</td>
                <td>${template.endTime}</td>
                <td>${template.slotDurationMinutes}</td>
            `;
            templateListBody.appendChild(row);
        });

        templateList.style.display = 'block';
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const userId = document.getElementById('find-template-user-id').value.trim();

        if (!userId) {
            showMessage('Por favor, insira o ID do usuário.', true);
            return;
        }

        try {
            const data = {
                userId: userId
            };

            const response = await makeRequest('/rest-schedule/schedule-template/find-by-user-id', 'POST', data);
            displayTemplates(response);
        } catch (error) {
            console.error('Erro ao buscar templates:', error);
            showMessage(error.message || 'Erro ao buscar templates. Tente novamente.', true);
            templateList.style.display = 'none';
        }
    });
});
