// scripts/dom/sessionDOM.js
import { handleLogin, handleLogout } from "../controllers/sessionController.js";

document.addEventListener("DOMContentLoaded", () => {
    const logoutBtn = document.getElementById("logout");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            handleLogout();
        });
    }

    const loginBtn = document.getElementById("login-btn");
    if (loginBtn) {
        loginBtn.addEventListener("click", (e) => {
            e.preventDefault();
            handleLogin();
        });
    }
});
