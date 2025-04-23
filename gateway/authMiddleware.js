const jwt = require('jsonwebtoken');
const cookie = require('cookie');

const SECRET_KEY = "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";

function authMiddleware(req, res, next) {
  const cookies = req.headers.cookie ? cookie.parse(req.headers.cookie) : {};

  const token = cookies.token;
  const csrfCookie = cookies.csrf;
  const csrfHeader = req.headers['x-csrf-token'];

  if (!token) {
    return res.redirect('/blocked.html');
  }

  const isStateChangingMethod = ['POST', 'PUT', 'DELETE', 'PATCH'].includes(req.method);
  if (isStateChangingMethod && (!csrfCookie || csrfCookie !== csrfHeader)) {
    return res.redirect('/blocked.html');
  }

  try {
    const decoded = jwt.verify(token, Buffer.from(SECRET_KEY, 'base64'));
    req.user = decoded;
  
    const jwtCsrf = decoded.csrf;
  
    if (isStateChangingMethod && (!csrfHeader || csrfHeader !== jwtCsrf)) {
      return res.redirect('/blocked.html');
    }
  
    next();
  } catch (err) {
    return res.redirect('/blocked.html');
  }  
}

module.exports = authMiddleware;
