/**
 * Este arquivo implementa um utilitário para fazer requisições HTTP com tratamento automático de:
 * - CSRF Token
 * - Refresh Token
 * - Timeout
 * - Tratamento de erros
 * - Logging
 */

// Constantes
const DEFAULT_TIMEOUT = 5000; // Timeout padrão de 5 segundos para as requisições
const STATE_CHANGING_METHODS = ['POST', 'PUT', 'PATCH', 'DELETE']; // Métodos que alteram estado e precisam de CSRF token
const REFRESH_ENDPOINT = '/rest-auth/token/refresh'; // Endpoint para refresh do token

/**
 * Função auxiliar para logging de requisições
 * @param {string} message - Mensagem a ser logada
 * @param {string} type - Tipo do log (info, warn, error)
 */
const logRequest = (message, type = 'info') => {
  const prefix = type === 'error' ? '❌' : type === 'warn' ? '⚠️' : '✅';
  console.log(`${prefix} [Request] ${message}`);
};

/**
 * Função para obter o valor de um cookie específico
 * @param {string} name - Nome do cookie
 * @returns {string|null} - Valor do cookie ou null se não encontrado
 */
const getCookie = (name) => {
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
    const [key, value] = cookie.trim().split('=');
    if (key === name) return decodeURIComponent(value);
  }
  return null;
};

/**
 * Cria as opções para a requisição fetch
 * @param {string} method - Método HTTP (GET, POST, etc)
 * @param {object} body - Corpo da requisição
 * @param {object} headers - Headers adicionais
 * @param {string} csrfToken - Token CSRF para métodos que alteram estado
 * @returns {object} - Opções configuradas para o fetch
 */
const createRequestOptions = (method, body, headers, csrfToken) => {
  const customHeaders = {
    'Content-Type': 'application/json',
    ...headers,
  };

  // Adiciona CSRF token apenas para métodos que alteram estado
  if (csrfToken && STATE_CHANGING_METHODS.includes(method.toUpperCase())) {
    customHeaders['X-CSRF-TOKEN'] = csrfToken;
  }

  const options = {
    method,
    headers: customHeaders,
    credentials: 'include' // Inclui cookies na requisição
  };

  if (body) {
    options.body = JSON.stringify(body);
  }

  return options;
};

/**
 * Trata a resposta da requisição
 * @param {Response} response - Objeto Response do fetch
 * @param {object} options - Opções da requisição
 * @returns {object} - Objeto com dados ou erro da resposta
 */
const handleResponse = async (response, options) => {
  const contentType = response.headers.get("content-type");
  
  // Tratamento de erros
  if (!response.ok) {
    let errorMessage = `Erro na requisição. Status: ${response.status}`;
    
    // Se a resposta for JSON, extrai a mensagem de erro
    if (contentType?.includes("application/json")) {
      const errorData = await response.json();
      errorMessage = errorData.message || errorMessage;
      
      // Se for erro de token expirado, armazena a URL atual para redirecionamento após refresh
      if (errorData.error === 'TOKEN_EXPIRED') {
        localStorage.setItem('redirectAfterRefresh', window.location.pathname + window.location.search);
      }
      
      return { error: errorData, status: response.status };
    }
    
    // Se não for JSON, usa o texto da resposta como mensagem de erro
    const text = await response.text();
    logRequest(`Resposta de erro não JSON: ${text}`, 'error');
    return { error: { message: errorMessage }, status: response.status };
  }

  // Tratamento de resposta vazia
  if (!contentType) {
    logRequest("Nenhum conteúdo retornado", 'warn');
    return { data: null };
  }

  // Converte a resposta para JSON ou texto
  const data = contentType.includes("application/json") 
    ? await response.json() 
    : await response.text();

  return { data };
};

/**
 * Função para atualizar o token de acesso
 * @returns {Promise<boolean>} - true se o refresh foi bem sucedido
 */
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

/**
 * Função principal para fazer requisições HTTP
 * @param {string} url - URL da requisição
 * @param {string} method - Método HTTP (GET, POST, etc)
 * @param {object} body - Corpo da requisição
 * @param {object} headers - Headers adicionais
 * @param {number} timeout - Timeout em milissegundos
 * @returns {Promise<any>} - Dados da resposta
 */
export async function makeRequest(url, method = 'GET', body = null, headers = {}, timeout = DEFAULT_TIMEOUT) {
  // Configuração do timeout
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeout);
  
  try {
    // Obtém CSRF token e cria opções da requisição
    const csrfToken = getCookie("csrfToken");
    const options = createRequestOptions(method, body, headers, csrfToken);
    
    // Faz a requisição
    let response = await fetch(url, { ...options, signal: controller.signal });
    clearTimeout(timer);
    
    const result = await handleResponse(response, options);
    
    // Se for erro 401 e for erro de token expirado, tenta refresh
    if (result.status === 401 && result.error?.error === 'TOKEN_EXPIRED') {
      // Tenta refresh token
      await refreshToken();
      
      // Atualiza o CSRF token
      const newCsrfToken = getCookie("csrfToken");
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
    
    // Tratamento específico para erro de timeout
    if (error.name === 'AbortError') {
      logRequest("A requisição foi abortada devido ao timeout", 'error');
    } else {
      logRequest(`Erro na requisição: ${error.message}`, 'error');
    }
    
    throw error;
  }
}
