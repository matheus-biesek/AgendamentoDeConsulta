export async function fetchAppointmentsByDate(selectedDate) {
    const mockData = [
        {
            id: 1,
            status: "Confirmada",
            nurse: "Maria Silva",
            doctor: "Dr. João Souza",
            specialization: "Cardiologia",
            room: "101",
            function: "Consulta",
            date: "2025-04-18",
            time: "14:00"
        },
        {
            id: 2,
            status: "Pendente",
            nurse: "Ana Costa",
            doctor: "Dr. Pedro Lima",
            specialization: "Dermatologia",
            room: "202",
            function: "Exame",
            date: "2025-04-16",
            time: "10:30"
        }
    ];

    // Filtra os dados com base na data selecionada
    return mockData.filter(appointment => appointment.date === selectedDate);
}   

// Função para trocar a senha do paciente
