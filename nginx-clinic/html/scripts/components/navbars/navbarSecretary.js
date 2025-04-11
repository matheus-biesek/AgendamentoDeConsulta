// scripts/components/navbarSecretary.js

document.addEventListener("DOMContentLoaded", () => {
    document.body.insertAdjacentHTML("afterbegin", `
        <nav class="navbar">
            <a href="#" class="logo">
                <img src="../images/89c82119-dce2-438b-832c-405ed67de61c.png" alt="Logo" />
            </a>
            <ul class="nav-links">
                <li><a href="/secretary/secretary-cancell-appointment.html">Cancelar consulta</a></li>
                <li><a href="/secretary/secretary-create-patient.html">Criar paciente</a></li>
                <li><a href="/secretary/secretary-making-appointment.html">Agendar consulta</a></li>
                <li><a id="logout" >Logout</a></li>
            </ul>
            <div class="menu-icon" id="menu-icon">&#9776;</div>
        </nav>
    `);
});