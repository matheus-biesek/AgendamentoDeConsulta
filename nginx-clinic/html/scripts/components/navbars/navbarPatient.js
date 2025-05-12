// scripts/components/navbarPatient.js

document.querySelector(".navbar").innerHTML = `
    <a href="#" class="logo">
        <img src="../images/logo.png" alt="Logo" />
    </a>
    <ul class="nav-links">
        <li><a href="/patient/patient-appointment.html">Consultas</a></li>
        <li><a href="/patient/patient-info.html">Dados pessoais</a></li>
        <li><a href="/role-selection.html">Trocar Perfil</a></li>
        <li><a id="logout">Logout</a></li>
    </ul>
`;