// scripts/components/navbarPatient.js

document.addEventListener("DOMContentLoaded", () => {
    document.body.insertAdjacentHTML("afterbegin", `
        <nav class="navbar">
            <a href="#" class="logo">
                <img src="../images/89c82119-dce2-438b-832c-405ed67de61c.png" alt="Logo" />
            </a>
            <ul class="nav-links">
                <li><a href="/patient/patient-appointment.html">Consultas</a></li>
                <li><a href="/patient/patient-info.html">Dados pessoais</a></li>
                <li><a id="logout" >Logout</a></li>
            </ul>
            <div class="menu-icon" id="menu-icon">&#9776;</div>
        </nav>
    `);
});