// scripts/utils/request.js

export async function makeRequest(url, method = 'GET', body = null, headers = {}, timeout = 5000) {
    const csrfToken = getCookie("csrf");

    const customHeaders = {
        'Content-Type': 'application/json',
        ...headers,
    };

    // Adiciona o CSRF somente em métodos que modificam estado
    if (csrfToken && ['POST', 'PUT', 'PATCH', 'DELETE'].includes(method.toUpperCase())) {
        customHeaders['X-CSRF-TOKEN'] = csrfToken;
    }

    const options = {
        method,
        headers: customHeaders,
        credentials: 'include'
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), timeout);

    try {
        const response = await fetch(url, { ...options, signal: controller.signal });
        clearTimeout(timer);

        const contentType = response.headers.get("content-type");

        if (!response.ok) {
            let errorMessage = `Erro na requisição. Status: ${response.status}`;
            
            if (contentType && contentType.includes("application/json")) {
                const errorData = await response.json();
                errorMessage = errorData.message || errorMessage;
            } else {
                const text = await response.text();
                console.error("Resposta de erro não JSON:", text);
            }
            throw new Error(errorMessage);
        }

        if (!contentType) {
            console.warn("Nenhum conteúdo retornado");
            return null;
        }

        if (contentType.includes("application/json")) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (error) {
        clearTimeout(timer);
        console.log('Erro capturado:', error);

        if (error.name === 'AbortError') {
            console.error("A requisição foi abortada devido ao timeout.");
        } else {
            console.error("Erro na requisição:", error.message);
        }
        throw error;
    }
}

export function getCookie(name) {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [key, value] = cookie.trim().split('=');
        if (key === name) return decodeURIComponent(value);
    }
    return null;
}
