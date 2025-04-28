// Função auxiliar para lidar com respostas do servidor
async function handleResponse(response) {
    if (!response.ok) {
        const responseData = await response.json();
        throw new Error(responseData.message || "Erro ao processar a requisição.");
    }
    return await response.json();
}

// Função para verificar a senha atual no backend
export async function verifyCurrentPasswordService(currentPassword) {
    console.log("Verificando senha atual no backend...");
    const response = await fetch("/scheduling-service/patient/verify-password", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ currentPassword })
    });

    const responseData = await handleResponse(response);
    return responseData.isValid; // Supondo que o backend retorne um campo `isValid`
}

// Função para trocar a senha no backend
export async function changePasswordService(currentPassword, newPassword) {
    console.log("Trocando senha no backend...");
    const response = await fetch("/scheduling-service/patient/change-password", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            lastPassword: currentPassword,
            newPassword: newPassword
        })
    });

    const responseData = await handleResponse(response);
    return responseData.message || "Senha trocada com sucesso!";
}

// Função para buscar consultas por data no backend
export async function fetchAppointmentsByDateService(selectedDate) {
    console.log(`Buscando consultas para a data: ${selectedDate}`);
    const response = await fetch(`/scheduling-service/patient/appointments?date=${selectedDate}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    });

    const responseData = await handleResponse(response);
    return responseData.appointments; // Supondo que o backend retorne um campo `appointments`
}