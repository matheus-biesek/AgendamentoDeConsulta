const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Proxy serviço de autenticação
app.use('/rest-auth', createProxyMiddleware({
  target: 'http://wildfly-auth:8080/java_ee_auth-1.0-SNAPSHOT',
  changeOrigin: true,
  pathRewrite: (path, req) => {
    const newPath = `/rest-auth${path}`;
    console.log(`[PATH-REWRITE] Original: ${path} -> New: ${newPath}`);
    return newPath;
  }
}));

// Proxy serviço de agendamento
app.use('/rest-schedule', createProxyMiddleware({
  target: 'http://wildfly-schedule:8080/java_ee_schedule-1.0-SNAPSHOT',
  changeOrigin: true,
  pathRewrite: (path, req) => {
    const newPath = `/rest-schedule${path}`;
    console.log(`[PATH-REWRITE] Original: ${path} -> New: ${newPath}`);
    return newPath;
  }
}));

// Proxy serviço de trabalhadores
app.use('/rest-workers', createProxyMiddleware({
  target: 'http://wildfly-workers:8080/java_ee_workers-1.0-SNAPSHOT',
  changeOrigin: true,
  pathRewrite: (path, req) => {
    const newPath = `/rest-workers${path}`;
    console.log(`[PATH-REWRITE] Original: ${path} -> New: ${newPath}`);
    return newPath;
  }
}));

// Proxy do frontend
app.use('/', createProxyMiddleware({
  target: 'http://nginx-clinic:80',
  changeOrigin: true,
}));

app.listen(PORT, () => {
  console.log(`Gateway rodando em http://localhost:${PORT} - mudança`);
});