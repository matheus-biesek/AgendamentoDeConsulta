// scripts/components/navbarTechnician.js

document.querySelector(".navbar").innerHTML = `
    <a href="#" class="logo">
        <img src="../images/logo.png" alt="Logo" />
    </a>
    <ul class="nav-links">
        <li><a href="/technician/technician-appointment-start.html">Iniciar grade</a></li>
        <li><a href="/technician/technician-schedule.html">Grade</a></li>
        <li><a href="/role-selection.html">Trocar Perfil</a></li>
        <li><a id="logout">Logout</a></li>
    </ul>
`;
