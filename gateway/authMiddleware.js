const jwt = require('jsonwebtoken');
const cookie = require('cookie');

const SECRET_KEY = "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";

function authMiddleware(req, res, next) {
  const cookies = req.headers.cookie ? cookie.parse(req.headers.cookie) : {};
  console.log("🔐 [Auth] Cookies recebidos:", cookies);

  const token = cookies.token;
  const csrfCookie = cookies.csrf;
  const csrfHeader = req.headers['x-csrf-token'];
  const method = req.method;

  console.log("🔐 [Auth] Método HTTP:", method);
  console.log("🔐 [Auth] Token:", token);
  console.log("🔐 [Auth] CSRF Cookie:", csrfCookie);
  console.log("🔐 [Auth] CSRF Header:", csrfHeader);

  if (!token) {
    console.warn("🚫 [Auth] Token não encontrado. Redirecionando para /blocked.html");
    return res.redirect('/blocked.html');
  }

  const isStateChangingMethod = ['POST', 'PUT', 'DELETE', 'PATCH'].includes(method);
  if (isStateChangingMethod && (!csrfCookie || csrfCookie !== csrfHeader)) {
    if (!csrfCookie) {
      console.warn("🚫 [Auth] CSRF inválido ou ausente em requisição de alteração de estado. Redirecionando.");
      return res.redirect('/blocked.html');
    }
    if (csrfCookie !== csrfHeader) {
      console.warn("🚫 [Auth] CSRF inválido, não bate com o header. Redirecionando.");
      return res.redirect('/blocked.html');
    }
  }

  try {
    const decoded = jwt.verify(token, Buffer.from(SECRET_KEY, 'base64'));
    req.user = decoded;
    
    console.log("✅ [Auth] JWT decodificado:", decoded);

    const jwtCsrf = decoded.csrf;

    if (isStateChangingMethod && (!csrfHeader || csrfHeader !== jwtCsrf)) {
      console.warn("🚫 [Auth] CSRF do header não bate com o do JWT. Redirecionando.");
      return res.redirect('/blocked.html');
    }

    console.log("✅ [Auth] Autenticação e CSRF verificados com sucesso.");
    next();
  } catch (err) {
    console.error("❌ [Auth] Erro ao verificar JWT:", err.message);
    
    // Para este tipo de erro, eu deixo a requisição parrar pro back end pois ele irá validar o refresh token.
    if (err.name === 'TokenExpiredError') {
      return next();
    }
      return res.status(401).json({
      error: 'UNAUTHORIZED',
      message: 'Não autorizado. Por favor, faça login novamente.'
    });
  }
}

module.exports = authMiddleware;
