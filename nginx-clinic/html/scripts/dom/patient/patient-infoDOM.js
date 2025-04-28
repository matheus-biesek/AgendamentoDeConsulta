import { verifyCurrentPassword, changePassword } from "../../controllers/patientController.js";

document.addEventListener("DOMContentLoaded", () => {
    const changePasswordButton = document.getElementById("change-password-button");

    changePasswordButton.addEventListener("click", async () => {
        const currentPassword = document.getElementById("last-password").value;
        const newPassword = document.getElementById("new-password").value;
        const confirmPassword = document.getElementById("confirm-password").value;

        // Verifica se a nova senha e a confirmação são iguais
        if (newPassword !== confirmPassword) {
            alert("A nova senha e a confirmação não coincidem. Por favor, tente novamente.");
            return;
        }

        try {
            // Verifica se a senha atual está correta
            const isCurrentPasswordValid = await verifyCurrentPassword(currentPassword);
            if (!isCurrentPasswordValid) {
                alert("A senha atual está incorreta. Por favor, tente novamente.");
                return;
            }

            // Tenta trocar a senha
            const successMessage = await changePassword(currentPassword, newPassword);
            alert(successMessage);
        } catch (error) {
            alert(error.message || "Erro ao trocar a senha. Por favor, tente novamente.");
        }
    });
});