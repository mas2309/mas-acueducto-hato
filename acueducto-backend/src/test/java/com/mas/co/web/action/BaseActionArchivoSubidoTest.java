package com.mas.co.web.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import org.apache.struts2.ActionContext;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BaseActionArchivoSubidoTest {

    private final AccionDePrueba accion = new AccionDePrueba();

    @TempDir
    Path tempDir;

    @AfterEach
    void limpiarContexto() {
        ActionContext.clear();
    }

    @Test
    void usaElArchivoQueYaParseoStrutsSinLlamarGetPart() throws Exception {
        File enDisco = Files.writeString(tempDir.resolve("upload_123.tmp"), "%PDF-1.4").toFile();
        UploadedFile subido = mock(UploadedFile.class);
        when(subido.length()).thenReturn(enDisco.length());
        when(subido.getAbsolutePath()).thenReturn(enDisco.getAbsolutePath());
        when(subido.getOriginalName()).thenReturn("acta.pdf");
        when(subido.getContentType()).thenReturn("application/pdf");

        MultiPartRequestWrapper wrapper = mockMultipart();
        when(wrapper.getFiles("archivo")).thenReturn(new UploadedFile[] {subido});
        conRequest(wrapper);

        BaseAction.ArchivoSubido resultado = accion.obtenerArchivoSubido("archivo");

        assertThat(resultado).isNotNull();
        assertThat(resultado.nombre()).isEqualTo("acta.pdf");
        assertThat(resultado.contentType()).isEqualTo("application/pdf");
        assertThat(resultado.archivo()).isEqualTo(enDisco);
        assertThat(resultado.temporal()).isFalse();
        verify(wrapper, never()).getPart(anyString());

        // El archivo de Struts no se borra desde la acción: lo limpia Struts al final de la petición
        resultado.liberar();
        assertThat(enDisco).exists();
    }

    @Test
    void retornaNullSiElCampoNoTraeArchivo() throws Exception {
        MultiPartRequestWrapper wrapper = mockMultipart();
        when(wrapper.getFiles("archivo")).thenReturn(null);
        conRequest(wrapper);

        assertThat(accion.obtenerArchivoSubido("archivo")).isNull();
    }

    @Test
    void propagaElErrorDeStrutsCuandoRechazaElArchivo() {
        MultiPartRequestWrapper wrapper = mockMultipart();
        when(wrapper.hasErrors()).thenReturn(true);
        when(wrapper.getErrors()).thenReturn(List.of(
                new LocalizedMessage(getClass(), "struts.messages.upload.error.SizeLimitExceededException",
                        "el archivo supera el tamaño máximo", new Object[0])));
        conRequest(wrapper);

        assertThatThrownBy(() -> accion.obtenerArchivoSubido("archivo"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("tamaño máximo");
    }

    @Test
    void retornaNullSiLaPeticionNoEsMultipart() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        conRequest(request);

        assertThat(accion.obtenerArchivoSubido("archivo")).isNull();
        verify(request, never()).getPart(anyString());
    }

    private MultiPartRequestWrapper mockMultipart() {
        MultiPartRequestWrapper wrapper = mock(MultiPartRequestWrapper.class);
        when(wrapper.getContentType()).thenReturn("multipart/form-data; boundary=abc");
        return wrapper;
    }

    private void conRequest(HttpServletRequest request) {
        ActionContext.of(new HashMap<>()).withServletRequest(request).bind();
    }

    private static class AccionDePrueba extends BaseAction {
    }
}
