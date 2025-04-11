// scripts/components/navbarAdmin.js

document.querySelector(".navbar").innerHTML = `
    <a href="#" class="logo">
        <img src="../images/logo.png" alt="Logo" />
    </a>
    <ul class="nav-links">
        <li><a href="/admin/admin-schedule-grid.html">Criar grade</a></li>
        <li><a href="/admin/admin-update.html">Atualizar usuários</a></li>
        <li><a id="logout">Logout</a></li>
    </ul>
`;
