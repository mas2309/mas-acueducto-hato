/*
 * Registro del service worker y botón "Instalar app" (PWA).
 * El contexto de la aplicación se recibe en el atributo data-ctx del <script>.
 */
(function () {
    'use strict';

    var script = document.currentScript;
    var ctx = (script && script.getAttribute('data-ctx')) || '';

    if ('serviceWorker' in navigator) {
        window.addEventListener('load', function () {
            navigator.serviceWorker.register(ctx + '/sw.js', { scope: ctx + '/' })
                .catch(function (error) {
                    console.warn('[PWA] No se pudo registrar el service worker:', error);
                });
        });
    }

    // Chrome/Edge/Android: mostrar un botón propio para instalar la app cuando el navegador lo permita.
    var eventoInstalacion = null;

    function botones() {
        return document.querySelectorAll('[data-pwa-instalar]');
    }

    function mostrarBotones(visible) {
        botones().forEach(function (boton) {
            boton.classList.toggle('d-none', !visible);
        });
    }

    window.addEventListener('beforeinstallprompt', function (event) {
        event.preventDefault();
        eventoInstalacion = event;
        mostrarBotones(true);
    });

    window.addEventListener('appinstalled', function () {
        eventoInstalacion = null;
        mostrarBotones(false);
    });

    document.addEventListener('click', function (event) {
        var boton = event.target.closest('[data-pwa-instalar]');
        if (!boton || !eventoInstalacion) {
            return;
        }
        eventoInstalacion.prompt();
        eventoInstalacion.userChoice.finally(function () {
            eventoInstalacion = null;
            mostrarBotones(false);
        });
    });
})();
