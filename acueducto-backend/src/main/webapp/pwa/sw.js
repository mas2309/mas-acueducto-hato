/*
 * Service worker de la PWA del panel administrativo.
 *
 * Reglas:
 *  - Las páginas (JSP) dependen de la sesión y muestran datos: NUNCA se guardan en caché.
 *    Se piden siempre a la red y, si no hay conexión, se muestra /offline.html.
 *  - Solo se cachean recursos estáticos (CSS, JS, imágenes, fuentes y Bootstrap del CDN).
 *  - Las peticiones que no son GET (formularios, subida de archivos, logout) no se tocan.
 *
 * Al cambiar los archivos precacheados o esta lógica, subir VERSION para invalidar la caché anterior.
 */
const VERSION = 'acueducto-v1';
const PRECACHE = VERSION + '-precache';
const RUNTIME = VERSION + '-runtime';

const BASE = new URL('./', self.location).pathname;
const OFFLINE_URL = BASE + 'offline.html';

const PRECACHE_URLS = [
  OFFLINE_URL,
  BASE + 'static/css/admin.css',
  BASE + 'static/js/admin.js',
  BASE + 'static/js/pwa.js',
  BASE + 'static/img/logo.png',
  BASE + 'static/img/pwa/icon-192.png'
];

const CDN_HOSTS = ['cdn.jsdelivr.net', 'fonts.googleapis.com', 'fonts.gstatic.com'];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(PRECACHE)
      .then((cache) => cache.addAll(PRECACHE_URLS))
      .then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(
        keys.filter((key) => key !== PRECACHE && key !== RUNTIME).map((key) => caches.delete(key))
      ))
      .then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', (event) => {
  const request = event.request;
  if (request.method !== 'GET') {
    return;
  }

  if (request.mode === 'navigate') {
    event.respondWith(
      fetch(request).catch(() => caches.match(OFFLINE_URL))
    );
    return;
  }

  const url = new URL(request.url);
  const esEstaticoPropio = url.origin === self.location.origin && url.pathname.startsWith(BASE + 'static/');
  const esCdn = CDN_HOSTS.includes(url.hostname);
  if (esEstaticoPropio || esCdn) {
    event.respondWith(staleWhileRevalidate(request));
  }
});

/** Responde con la copia en caché (si existe) y la actualiza en segundo plano. */
async function staleWhileRevalidate(request) {
  const cache = await caches.open(RUNTIME);
  // Busca en todas las cachés (precache incluida) para servir también los recursos precargados
  const enCache = await caches.match(request);
  const desdeRed = fetch(request)
    .then((response) => {
      if (response && (response.ok || response.type === 'opaque')) {
        cache.put(request, response.clone());
      }
      return response;
    })
    .catch(() => enCache || Response.error());
  return enCache || desdeRed;
}
