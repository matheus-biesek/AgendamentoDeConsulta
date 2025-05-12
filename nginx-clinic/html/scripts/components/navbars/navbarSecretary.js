// scripts/components/navbarSecretary.js

document.querySelector(".navbar").innerHTML = `
    <a href="#" class="logo">
        <img src="../images/logo.png" alt="Logo" />
    </a>
    <ul class="nav-links">
        <li><a href="/secretary/secretary-cancell-appointment.html">Cancelar consulta</a></li>
        <li><a href="/secretary/secretary-create-patient.html">Criar paciente</a></li>
        <li><a href="/secretary/secretary-making-appointment.html">Agendar consulta</a></li>
        <li><a href="/role-selection.html">Trocar Perfil</a></li>
        <li><a id="logout">Logout</a></li>
    </ul>
`;