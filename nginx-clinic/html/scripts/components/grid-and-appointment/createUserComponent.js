document.querySelector(".create-user-component").innerHTML = `
    <div class="create-user-container">
        <h2>Criar Usuário</h2>
        <form id="create-user-form" novalidate>
            <div class="form-group">
                <label for="name-create">Nome de Usuário:</label>
                <input type="text" id="name-create" placeholder="Digite o nome de usuário" required>
            </div>
            <div class="form-group">
                <label for="cpf-create">CPF:</label>
                <input type="text" id="cpf-create" placeholder="Digite o CPF do usuário" required>
            </div>
            <div class="form-group">
                <label for="password-create">Senha:</label>
                <input type="password" id="password-create" placeholder="Digite a senha" required>
            </div>
            <div class="form-group">
                <label for="confirm-password-create">Confirma Senha:</label>
                <input type="password" id="confirm-password-create" placeholder="Confirme a senha" required>
            </div>
            <div class="form-group">
                <label>Role:</label>
                <div class="role-options">
                    <input type="radio" id="admin-role-create" name="role-create" value="ADMIN" required>
                    <label for="admin-role-create">Admin</label>

                    <input type="radio" id="doutor-role-create" name="role-create" value="DOCTOR">
                    <label for="doutor-role-create">Doutor</label>

                    <input type="radio" id="paciente-role-create" name="role-create" value="PATIENT">
                    <label for="paciente-role-create">Paciente</label>

                    <input type="radio" id="tecnico-role-create" name="role-create" value="TECHNICIAN">
                    <label for="tecnico-role-create">Técnico</label>

                    <input type="radio" id="secretario-role-create" name="role-create" value="SECRETARY">
                    <label for="secretario-role-create">Secretario</label>
                </div>
            </div>
            <button type="submit" id="create-user-button">Criar</button>
        </form>
    </div>
`;