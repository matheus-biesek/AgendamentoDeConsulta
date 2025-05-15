document.querySelector(".change-user-role-component").innerHTML = `
    <div class="update-role-container">
        <h2>Alterar Role do Usuário</h2>
        <form id="update-role-form">
            <div class="form-group">
                <label for="cpf">CPF:</label>
                <input type="text" id="cpf" placeholder="Digite o CPF do usuário" required>
            </div>
            <div class="form-group">
                <div class="role-options">
                    <input type="radio" id="admin-change" name="role-change" value="Admin" required>
                    <label for="admin-change">Admin</label>

                    <input type="radio" id="doctor-change" name="role-change" value="Doutor">
                    <label for="doctor-change">Doutor</label>

                    <input type="radio" id="patient-change" name="role-change" value="Paciente">
                    <label for="pactient-change">Paciente</label>

                    <input type="radio" id="technician-change" name="role-change" value="Técnico">
                    <label for="technician-change">Técnico</label>

                    <input type="radio" id="secretary-change" name="role-change" value="Secretario">
                    <label for="secretary-change">Secretario</label>

                    <input type="radio" id="nurse-change" name="role-change" value="Enfermeira">
                    <label for="nurse-change">Enfermeira</label>
                </div>
            </div>
            <button type="submit" id="update-role-button">Alterar</button>
        </form>
    </div>
`;