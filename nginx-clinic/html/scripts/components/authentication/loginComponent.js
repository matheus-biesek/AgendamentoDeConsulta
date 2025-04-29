// scripts/components/authetication/loginComponent.js

document.querySelector(".login-component").innerHTML = `
    <h2>Login</h2>
    <form>
        <input type="text" id="cpf" placeholder="CPF" required>
        <input type="password" id="password" placeholder="Senha" required> 
        <button type="submit" id="login-btn">Entrar</button>
    </form>
`;
