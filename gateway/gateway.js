const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const authMiddleware = require('./authMiddleware');
const path = require('path');
require('dotenv').config();

// Constantes
const PORT = process.env.PORT || 3000;
const PUBLIC_PATHS = [
  '/rest-auth/auth-session/login',
  '/rest-auth/auth-session/logout',
  '/rest-auth/token/refresh',
  '/',
  '/index.html',
  '/blocked.html',
  '/refresh.html',
  '/rest-workers/workers/users-by-role', // TESTE
  '/rest-workers/workers/user-data', // TESTE
  '/rest-workers/workers/public', // TESTE
  '/rest-schedule/admin/public', // TESTE
  '/rest-schedule/admin/private', // TESTE
  '/rest-auth/user-management/register-user-by-secretary', // TESTE
  '/rest-auth/user-management/register-user-by-admin' // TESTE
];

const STATIC_FILE_EXTENSIONS = /\.(js|css|png|jpg|ico|woff2?)$/;

// Configurações dos serviços
const SERVICES = {
  AUTH: {
    path: '/rest-auth',
    target: 'http://wildfly-auth:8080/java_ee_auth-1.0-SNAPSHOT',
    pathRewrite: (path) => `/rest-auth${path}`
  },
  SCHEDULE: {
    path: '/rest-schedule',
    target: 'http://wildfly-schedule:8080/java_ee_schedule-1.0-SNAPSHOT',
    pathRewrite: (path) => `/rest-schedule${path}`
  },
  WORKERS: {
    path: '/rest-workers',
    target: 'http://wildfly-workers:8080/java_ee_workers-1.0-SNAPSHOT',
    pathRewrite: (path) => `/rest-workers${path}`
  },
  FRONTEND: {
    path: '/',
    target: 'http://nginx-clinic:80'
  }
};

// Funções auxiliares
const isPublicPath = (path) => {
  return PUBLIC_PATHS.includes(path) || path.match(STATIC_FILE_EXTENSIONS);
};

const createProxyConfig = (service) => ({
  target: service.target,
  changeOrigin: true,
  ...(service.pathRewrite && { pathRewrite: service.pathRewrite })
});

// Configuração do Express
const app = express();

// Middleware de autenticação
app.use((req, res, next) => {
  if (isPublicPath(req.path)) return next();
  return authMiddleware(req, res, next);
});

// Configuração dos proxies
Object.values(SERVICES).forEach(service => {
  app.use(service.path, createProxyMiddleware(createProxyConfig(service)));
});

// Inicialização do servidor
app.listen(PORT, () => {
  console.log(`🚀 Gateway rodando em http://localhost:${PORT}`);
  console.log('📡 Serviços configurados:');
  Object.entries(SERVICES).forEach(([name, service]) => {
    console.log(`   - ${name}: ${service.target}`);
  });
});
