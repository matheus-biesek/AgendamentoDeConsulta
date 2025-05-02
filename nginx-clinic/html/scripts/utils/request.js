// scripts/utils/request.js

// Constantes
const DEFAULT_TIMEOUT = 5000;
const STATE_CHANGING_METHODS = ['POST', 'PUT', 'PATCH', 'DELETE'];
const REFRESH_ENDPOINT = '/rest-auth/token/refresh';

// Funções auxiliares
const logRequest = (message, type = 'info') => {
  const prefix = type === 'error' ? '❌' : type === 'warn' ? '⚠️' : '✅';
  console.log(`${prefix} [Request] ${message}`);
};

const getCookie = (name) => {
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
    const [key, value] = cookie.trim().split('=');
    if (key === name) return decodeURIComponent(value);
  }
  return null;
};

const createRequestOptions = (method, body, headers, csrfToken) => {
  const customHeaders = {
    'Content-Type': 'application/json',
    ...headers,
  };

  if (csrfToken && STATE_CHANGING_METHODS.includes(method.toUpperCase())) {
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

  return options;
};

const handleResponse = async (response, options) => {
  const contentType = response.headers.get("content-type");
  
  if (!response.ok) {
    let errorMessage = `Erro na requisição. Status: ${response.status}`;
    
    if (contentType?.includes("application/json")) {
      const errorData = await response.json();
      errorMessage = errorData.message || errorMessage;
      
      // Se for erro de token expirado, armazena a URL atual
      if (errorData.error === 'TOKEN_EXPIRED') {
        localStorage.setItem('redirectAfterRefresh', window.location.pathname + window.location.search);
      }
      
      return { error: errorData, status: response.status };
    }
    
    const text = await response.text();
    logRequest(`Resposta de erro não JSON: ${text}`, 'error');
    return { error: { message: errorMessage }, status: response.status };
  }

  if (!contentType) {
    logRequest("Nenhum conteúdo retornado", 'warn');
    return { data: null };
  }

  const data = contentType.includes("application/json") 
    ? await response.json() 
    : await response.text();

  return { data };
};

const refreshToken = async () => {
  logRequest("Token expirado, tentando refresh...");
  
  const refreshOptions = createRequestOptions('POST', null, {}, null);
  const refreshResponse = await fetch(REFRESH_ENDPOINT, refreshOptions);
  
  if (!refreshResponse.ok) {
    logRequest("Falha no refresh token", 'error');
    throw new Error("Falha ao atualizar o token");
  }
  
  logRequest("Refresh token bem sucedido");
  return true;
};

// Função principal
export async function makeRequest(url, method = 'GET', body = null, headers = {}, timeout = DEFAULT_TIMEOUT) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeout);
  
  try {
    const csrfToken = getCookie("csrf");
    const options = createRequestOptions(method, body, headers, csrfToken);
    
    let response = await fetch(url, { ...options, signal: controller.signal });
    clearTimeout(timer);
    
    const result = await handleResponse(response, options);
    
    // Se for erro 401 e for erro de token expirado
    if (result.status === 401 && result.error?.error === 'TOKEN_EXPIRED') {
      // Tenta refresh token
      await refreshToken();
      
      // Atualiza o CSRF token
      const newCsrfToken = getCookie("csrf");
      if (!newCsrfToken) {
        logRequest("Novo CSRF token não encontrado nos cookies", 'warn');
        throw new Error("Falha ao obter novo CSRF token");
      }
      
      // Refaz a requisição original com o novo CSRF
      const newOptions = createRequestOptions(method, body, headers, newCsrfToken);
      const newController = new AbortController();
      const newTimer = setTimeout(() => newController.abort(), timeout);
      
      try {
        response = await fetch(url, { ...newOptions, signal: newController.signal });
        clearTimeout(newTimer);
        
        const retryResult = await handleResponse(response, newOptions);
        if (retryResult.error) throw new Error(retryResult.error.message);
        return retryResult.data;
      } catch (retryError) {
        clearTimeout(newTimer);
        throw retryError;
      }
    }
    
    if (result.error) throw new Error(result.error.message);
    return result.data;
    
  } catch (error) {
    clearTimeout(timer);
    
    if (error.name === 'AbortError') {
      logRequest("A requisição foi abortada devido ao timeout", 'error');
    } else {
      logRequest(`Erro na requisição: ${error.message}`, 'error');
    }
    
    throw error;
  }
}
