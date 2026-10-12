package com.mas.co.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Compila todos los JSP al arrancar la aplicación. Sin esto cada pantalla se compila la primera
 * vez que alguien la abre después de un despliegue y esa primera carga tarda más de un segundo.
 *
 * <p>Jasper solo compila dentro de una petición HTTP, así que al arrancar la aplicación se llama
 * a sí misma. El parámetro {@code jsp_precompile} hace que Jasper compile cada JSP sin ejecutarlo.
 * El endpoint solo responde con el token generado en este arranque.
 */
@Slf4j
@RestController
public class JspWarmupController {

    static final String PATH = "/internal/jsp-warmup";
    private static final String TOKEN_HEADER = "X-Warmup-Token";
    private static final String VIEWS_PATH = "/WEB-INF/views/";

    private final String token = UUID.randomUUID().toString();
    private volatile int port;

    @EventListener
    public void alIniciarServidor(ServletWebServerInitializedEvent event) {
        port = event.getWebServer().getPort();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void precompilarAlArrancar() {
        // Sin servidor web real (p. ej. en pruebas) no hay nada que precompilar
        if (port <= 0) {
            return;
        }
        Thread hilo = new Thread(this::llamarWarmup, "jsp-warmup");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void llamarWarmup() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + PATH + "?jsp_precompile=true"))
                    .header(TOKEN_HEADER, token)
                    .timeout(Duration.ofMinutes(5))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Precompilación de JSP: {} ({})", response.body(), response.statusCode());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException | RuntimeException e) {
            // No es crítico: los JSP se compilarán al primer uso, como antes
            log.warn("No se pudieron precompilar los JSP: {}", e.getMessage());
        }
    }

    @GetMapping(PATH)
    public ResponseEntity<String> precompilar(
            @RequestHeader(value = TOKEN_HEADER, required = false) String tokenRecibido,
            HttpServletRequest request, HttpServletResponse response) {

        if (!token.equals(tokenRecibido)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        long inicio = System.currentTimeMillis();
        List<String> jsps = new ArrayList<>();
        buscarJsps(request.getServletContext(), VIEWS_PATH, jsps);

        int compilados = 0;
        for (String jsp : jsps) {
            try {
                request.getRequestDispatcher(jsp).include(request, response);
                compilados++;
            } catch (ServletException | IOException | RuntimeException e) {
                log.warn("No se pudo precompilar {}: {}", jsp, e.getMessage());
            }
        }

        return ResponseEntity.ok(String.format("%d de %d JSP compilados en %d ms",
                compilados, jsps.size(), System.currentTimeMillis() - inicio));
    }

    private void buscarJsps(ServletContext servletContext, String path, List<String> jsps) {
        Set<String> recursos = servletContext.getResourcePaths(path);
        if (recursos == null) {
            return;
        }
        for (String recurso : recursos) {
            if (recurso.endsWith("/")) {
                buscarJsps(servletContext, recurso, jsps);
            } else if (recurso.endsWith(".jsp")) {
                jsps.add(recurso);
            }
        }
    }
}
