// scripts/components/navbarDoctor.js

document.querySelector(".navbar").innerHTML = `
    <a href="#" class="logo">
        <img src="../images/logo.png" alt="Logo" />
    </a>
    <ul class="nav-links">
        <li><a href="/doctor/doctor-appointment-schedule-grid.html">Grade e consultas</a></li>
        <li><a href="/doctor/doctor-appointment-start.html">Iniciar consulta</a></li>
        <li><a href="/role-selection.html">Trocar Perfil</a></li>
        <li><a id="logout">Logout</a></li>
    </ul>
`;
