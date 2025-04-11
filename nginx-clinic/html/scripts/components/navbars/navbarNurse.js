// scripts/components/navbarNurse.js

document.querySelector(".navbar").innerHTML = `
    <a href="#" class="logo">
        <img src="../images/logo.png" alt="Logo" />
    </a>
    <ul class="nav-links">
        <li><a href="/nurse/nurse-appointment-schedule-grid.html">Grade e consultas</a></li>
        <li><a href="/nurse/nurse-appointment-start.html">Iniciar consulta</a></li>
        <li><a id="logout" >Logout</a></li>
    </ul>
`;