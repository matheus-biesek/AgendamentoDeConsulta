document.querySelector(".date-filter-component").innerHTML = `
    <div class="date-filter">
        <label for="reference-date">Data referência:</label>
        <input type="date" id="reference-date" class="reference-date-input">
        <button class="search-button">Buscar</button>
    </div>
`;

// Define a data atual no campo de data
const dateInput = document.getElementById("reference-date");
if (dateInput) {
    const today = new Date().toISOString().split("T")[0]; // Obtém a data atual no formato YYYY-MM-DD
    dateInput.value = today;
}