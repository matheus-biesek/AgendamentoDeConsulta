import { createUser } from "../../controllers/adminController.js";

document.getElementById("update-role-form").addEventListener("submit", async (event) => {
    event.preventDefault(); // Evita o comportamento padrão do formulário

    const cpf = document.getElementById("cpf").value;

    // Verifica se uma role foi selecionada
    const roleElement = document.querySelector('input[name="role-change"]:checked');
    if (!roleElement) {
        alert("Por favor, selecione uma role.");
        return;
    }
    const role = roleElement.value;

    try {
        const response = await fetch("/admin/update-role", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ cpf, role }),
        });

        if (!response.ok) {
            const errorData = await response.json();
            alert(errorData.message || "Erro ao atualizar a role.");
        } else {
            const successData = await response.json();
            alert(successData.message || "Role atualizada com sucesso.");
        }
    } catch (error) {
        console.error("Erro:", error);
        alert("Erro ao conectar ao servidor.");
    }
});

document.getElementById("create-user-form").addEventListener("submit", async (event) => {
    event.preventDefault(); // Evita o comportamento padrão do formulário

    const username = document.getElementById("name-create").value;
    const cpf = document.getElementById("cpf-create").value;
    const password = document.getElementById("password-create").value;
    const confirmPassword = document.getElementById("confirm-password-create").value;
    const roleElement = document.querySelector('input[name="role-create"]:checked');

    // Verifica se as senhas coincidem
    if (password !== confirmPassword) {
        alert("As senhas não coincidem. Por favor, tente novamente.");
        document.getElementById("password-create").classList.add("input-error");
        document.getElementById("confirm-password-create").classList.add("input-error");
        return;
    } else {
        document.getElementById("password-create").classList.remove("input-error");
        document.getElementById("confirm-password-create").classList.remove("input-error");
    }

    // Verifica se uma role foi selecionada
    if (!roleElement) {
        alert("Por favor, selecione uma role.");
        return;
    }

    const role = roleElement.value;

    try {
        const response = await createUser(username, password, cpf, role);
        alert(response.message || "Usuário criado com sucesso!");
    } catch (error) {
        console.error("Erro:", error);
        alert(error.message || "Erro ao criar o usuário.");
    }
});