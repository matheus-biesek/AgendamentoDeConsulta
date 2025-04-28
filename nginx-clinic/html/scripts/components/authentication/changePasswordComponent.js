document.querySelector(".change-password-component").innerHTML = `
    <div class="change-password">
        <h1>Trocar Senha</h1>
        <form id="change-password-form">
            <label for="last-password">Senha Atual:</label>
            <input type="password" id="last-password" required>
            
            <label for="new-password">Nova Senha:</label>
            <input type="password" id="new-password" required>
            
            <label for="confirm-password">Confirme a Nova Senha:</label>
            <input type="password" id="confirm-password" required>
            
            <button type="button" id="change-password-button">Trocar Senha</button>
        </form>
    </div>
`;