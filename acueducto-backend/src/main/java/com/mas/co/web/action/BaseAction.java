package com.mas.co.web.action;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

@Slf4j
public abstract class BaseAction extends ActionSupport {

    private static final String FLASH_EXITO = "_flashExito";
    private static final String FLASH_ERROR = "_flashError";

    private String mensajeExito;
    private String mensajeError;
    private boolean flashLoaded = false;

    protected Map<String, Object> getSession() {
        return ActionContext.getContext().getSession();
    }

    protected void flashExito(String mensaje) {
        getSession().put(FLASH_EXITO, mensaje);
    }

    protected void flashError(String mensaje) {
        getSession().put(FLASH_ERROR, mensaje);
    }

    private void loadFlash() {
        if (!flashLoaded) {
            Object exito = getSession().remove(FLASH_EXITO);
            Object error = getSession().remove(FLASH_ERROR);
            mensajeExito = exito != null ? exito.toString() : null;
            mensajeError = error != null ? error.toString() : null;
            flashLoaded = true;
        }
    }

    public String getMensajeExito() {
        loadFlash();
        return mensajeExito;
    }

    public String getMensajeError() {
        loadFlash();
        return mensajeError;
    }

    /**
     * Archivo recibido en un formulario multipart.
     *
     * @param archivo     archivo en disco con el contenido subido
     * @param nombre      nombre original del archivo en el equipo del usuario
     * @param contentType tipo MIME informado por el navegador
     * @param temporal    true si el archivo lo creó la acción y debe borrarse con {@link #liberar()};
     *                    los archivos que guarda Struts los elimina el propio Struts al terminar la petición
     */
    protected record ArchivoSubido(File archivo, String nombre, String contentType, boolean temporal) {

        public long tamano() {
            return archivo.length();
        }

        public void liberar() {
            if (temporal && archivo.exists() && !archivo.delete()) {
                log.warn("No se pudo eliminar archivo temporal: {}", archivo.getAbsolutePath());
            }
        }
    }

    /**
     * Obtiene el archivo enviado en el campo indicado de un formulario multipart.
     *
     * <p>El filtro de Struts ({@code StrutsPrepareAndExecuteFilter}) procesa el cuerpo multipart
     * antes de llegar a la acción y lo deja disponible en {@link MultiPartRequestWrapper}. En ese
     * punto el stream de la petición ya fue consumido, por lo que {@code request.getPart(...)} del
     * contenedor no encuentra el archivo. Por eso se lee primero lo que parseó Struts y solo se usa
     * {@code getPart} como respaldo si la petición no pasó por el parser de Struts.
     *
     * @return el archivo recibido, o {@code null} si la petición no es multipart o el campo viene vacío
     * @throws IllegalStateException si Struts rechazó el archivo (p. ej. por superar el tamaño máximo)
     */
    protected ArchivoSubido obtenerArchivoSubido(String campo) throws IOException {
        HttpServletRequest request = ActionContext.getContext().getServletRequest();
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            log.debug("Petición no multipart (Content-Type: {}); no hay archivo en '{}'", contentType, campo);
            return null;
        }

        MultiPartRequestWrapper multipart = buscarMultipartStruts(request);
        if (multipart != null) {
            if (multipart.hasErrors()) {
                String detalle = multipart.getErrors().stream()
                        .map(LocalizedMessage::getDefaultMessage)
                        .findFirst()
                        .orElse("error desconocido");
                log.warn("Struts rechazó el archivo del campo '{}': {}", campo, detalle);
                throw new IllegalStateException("No se pudo recibir el archivo: " + detalle);
            }
            UploadedFile[] archivos = multipart.getFiles(campo);
            if (archivos == null || archivos.length == 0 || archivos[0] == null
                    || archivos[0].length() == null || archivos[0].length() == 0) {
                log.debug("El campo '{}' no trae archivo", campo);
                return null;
            }
            UploadedFile subido = archivos[0];
            log.info("Archivo recibido en '{}': '{}' ({}, {} bytes)",
                    campo, subido.getOriginalName(), subido.getContentType(), subido.length());
            return new ArchivoSubido(new File(subido.getAbsolutePath()), subido.getOriginalName(),
                    subido.getContentType(), false);
        }

        return leerConPartDelContenedor(request, campo);
    }

    private ArchivoSubido leerConPartDelContenedor(HttpServletRequest request, String campo) throws IOException {
        Part part;
        try {
            part = request.getPart(campo);
        } catch (jakarta.servlet.ServletException e) {
            throw new IOException("No se pudo leer el archivo del formulario: " + e.getMessage(), e);
        }
        if (part == null || part.getSize() == 0) {
            log.debug("El campo '{}' no trae archivo (Part del contenedor)", campo);
            return null;
        }
        File temporal = File.createTempFile("subida_", ".tmp");
        part.write(temporal.getAbsolutePath());
        log.info("Archivo recibido en '{}' vía Part: '{}' ({}, {} bytes)",
                campo, part.getSubmittedFileName(), part.getContentType(), part.getSize());
        return new ArchivoSubido(temporal, part.getSubmittedFileName(), part.getContentType(), true);
    }

    private static MultiPartRequestWrapper buscarMultipartStruts(ServletRequest request) {
        ServletRequest actual = request;
        while (actual != null) {
            if (actual instanceof MultiPartRequestWrapper multipart) {
                return multipart;
            }
            actual = actual instanceof ServletRequestWrapper wrapper ? wrapper.getRequest() : null;
        }
        return null;
    }
}
