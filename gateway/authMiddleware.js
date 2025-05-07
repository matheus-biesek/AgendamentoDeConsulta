const jwt = require('jsonwebtoken');
const cookie = require('cookie');

// Constantes
const SECRET_KEY = "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";
const STATE_CHANGING_METHODS = ['POST', 'PUT', 'DELETE', 'PATCH'];
const BLOCKED_PAGE = '/blocked.html';

// Funções auxiliares
const logAuth = (message, type = 'info') => {
  const prefix = type === 'error' ? '❌' : type === 'warn' ? '🚫' : '✅';
  console.log(`${prefix} [Auth] ${message}`);
};

const verifyCsrf = (csrfCookie, csrfHeader, method) => {
  if (!STATE_CHANGING_METHODS.includes(method)) return true;
  
  if (!csrfCookie) {
    logAuth('CSRF ausente em requisição de alteração de estado. Redirecionando.', 'warn');
    return false;
  }
  
  if (csrfCookie !== csrfHeader) {
    logAuth('CSRF inválido, não bate com o header. Redirecionando.', 'warn');
    return false;
  }
  
  return true;
};

const handleTokenError = (err, res) => {
  if (err.name === 'TokenExpiredError') {
    // Se for uma requisição HTML, redireciona para a página de refresh
    if (res.req.headers.accept?.includes('text/html')) {
      const currentUrl = res.req.originalUrl;
      return res.redirect(`/refresh.html?redirect=${encodeURIComponent(currentUrl)}`);
    }
    
    return res.status(401).json({
      error: 'TOKEN_EXPIRED',
      message: 'Token expirado',
      shouldRefresh: true,
      refreshEndpoint: '/rest-auth/token/refresh'
    });
  }
  
  return res.status(401).json({
    error: 'UNAUTHORIZED',
    message: 'Não autorizado. Por favor, faça login novamente.'
  });
};

// Middleware principal
function authMiddleware(req, res, next) {
  // Extração de dados da requisição
  const cookies = req.headers.cookie ? cookie.parse(req.headers.cookie) : {};
  const { token, csrf: csrfCookie } = cookies;
  const csrfHeader = req.headers['x-csrf-token'];
  const { method } = req;

  // Logging inicial
  logAuth(`Método HTTP: ${method}`);
  logAuth(`Token: ${token ? 'presente' : 'ausente'}`);
  logAuth(`CSRF Cookie: ${csrfCookie ? 'presente' : 'ausente'}`);
  logAuth(`CSRF Header: ${csrfHeader ? 'presente' : 'ausente'}`);

  // Verificação do token
  if (!token) {
    logAuth('Token não encontrado. Redirecionando para /blocked.html', 'warn');
    return res.redirect(BLOCKED_PAGE);
  }

  // Verificação do CSRF
  if (!verifyCsrf(csrfCookie, csrfHeader, method)) {
    return res.redirect(BLOCKED_PAGE);
  }

  // Verificação do JWT
  try {
    const decoded = jwt.verify(token, Buffer.from(SECRET_KEY, 'base64'));
    req.user = decoded;
    
    logAuth(`JWT decodificado: ${JSON.stringify(decoded)}`);

    // Verificação adicional do CSRF no JWT (opcional)
    if (STATE_CHANGING_METHODS.includes(method)) {
      const jwtCsrf = decoded.csrf;
      if (!csrfHeader || csrfHeader !== jwtCsrf) {
        logAuth('CSRF do header não bate com o do JWT. Redirecionando.', 'warn');
        return res.redirect(BLOCKED_PAGE);
      }
    }

    logAuth('Autenticação e CSRF verificados com sucesso.');
    next();
  } catch (err) {
    logAuth(`Erro ao verificar JWT: ${err.message}`, 'error');
    return handleTokenError(err, res);
  }
}

module.exports = authMiddleware;
