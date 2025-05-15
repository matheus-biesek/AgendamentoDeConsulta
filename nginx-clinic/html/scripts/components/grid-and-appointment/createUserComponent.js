document.querySelector(".create-user-component").innerHTML = `
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
            <button type="submit" id="create-user-button">Criar</button>
        </form>
`;