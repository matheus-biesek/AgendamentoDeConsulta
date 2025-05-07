document.querySelector(".change-user-role-component").innerHTML = `
    <div class="update-role-container">
        <h2>Alterar Role do Usuário</h2>
        <form id="update-role-form">
            <div class="form-group">
                <label for="cpf">CPF:</label>
                <input type="text" id="cpf" placeholder="Digite o CPF do usuário" required>
            </div>
            <div class="form-group">
                <label>Role:</label>
                <div class="role-options">
                    <input type="radio" id="admin-change" name="role-change" value="Admin" required>
                    <label for="admin-change">Admin</label>

                    <input type="radio" id="doutor-change" name="role-change" value="Doutor">
                    <label for="doutor-change">Doutor</label>

                    <input type="radio" id="paciente-change" name="role-change" value="Paciente">
                    <label for="paciente-change">Paciente</label>

                    <input type="radio" id="tecnico-change" name="role-change" value="Técnico">
                    <label for="tecnico-change">Técnico</label>

                    <input type="radio" id="secretario-change" name="role-change" value="Secretario">
                    <label for="secretario-change">Secretario</label>
                </div>
            </div>
            <button type="submit" id="update-role-button">Alterar</button>
        </form>
    </div>
`;