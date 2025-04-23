const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const authMiddleware = require('./authMiddleware');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

const publicPaths = [
  '/rest-auth/auth-session/login',
  '/rest-auth/auth-session/logout',
  '/',
  '/index.html',
  '/blocked.html'
];

app.use((req, res, next) => {
  const isPublic = publicPaths.includes(req.path) ||
    req.path.match(/\.(js|css|png|jpg|ico|woff2?)$/);
  if (isPublic) return next();

  return authMiddleware(req, res, next);
});

app.use('/rest-auth', createProxyMiddleware({
  target: 'http://wildfly-auth:8080/java_ee_auth-1.0-SNAPSHOT',
  changeOrigin: true,
  pathRewrite: (path, req) => `/rest-auth${path}`
}));

app.use('/rest-schedule', createProxyMiddleware({
  target: 'http://wildfly-schedule:8080/java_ee_schedule-1.0-SNAPSHOT',
  changeOrigin: true,
  pathRewrite: (path, req) => `/rest-schedule${path}`
}));

app.use('/rest-workers', createProxyMiddleware({
  target: 'http://wildfly-workers:8080/java_ee_workers-1.0-SNAPSHOT',
  changeOrigin: true,
  pathRewrite: (path, req) => `/rest-workers${path}`
}));

app.use('/', createProxyMiddleware({
  target: 'http://nginx-clinic:80',
  changeOrigin: true,
}));

app.listen(PORT, () => {
  console.log(`Gateway rodando em http://localhost:${PORT}`);
});
