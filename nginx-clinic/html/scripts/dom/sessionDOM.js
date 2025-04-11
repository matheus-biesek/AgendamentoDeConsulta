// scripts/dom/sessionDOM.js
import { handleLogin, handleLogout } from "../controllers/sessionController.js";

document.addEventListener("DOMContentLoaded", () => {
    document.addEventListener("click", (e) => {
        if (e.target && e.target.id === "logout") {
            e.preventDefault();
            handleLogout();
        }
    });

    document.addEventListener("click", (e) => {
        if (e.target && e.target.id === "login-btn") {
            e.preventDefault();
            handleLogin();
        }
    });
});
