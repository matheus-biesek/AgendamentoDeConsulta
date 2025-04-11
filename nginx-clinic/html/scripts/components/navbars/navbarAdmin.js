// scripts/components/navbarAdmin.js

document.addEventListener("DOMContentLoaded", () => {
    document.body.insertAdjacentHTML("afterbegin", `
        <nav class="navbar">
            <a href="#" class="logo">
                <img src="../images/89c82119-dce2-438b-832c-405ed67de61c.png" alt="Logo" />
            </a>
            <ul class="nav-links">
                <li><a href="/admin/admin-schedule-grid.html">Criar grade</a></li>
                <li><a href="/admin/admin-update.html">Atualizar usuários</a></li>
                <li><a id="logout" >Logout</a></li>
            </ul>
            <div class="menu-icon" id="menu-icon">&#9776;</div>
        </nav>
    `);
});