package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.DocumentoDto;
import com.mas.co.entity.Documento;
import com.mas.co.exception.BusinessException;
import com.mas.co.exception.ValidationException;
import com.mas.co.repository.DocumentoRepository;
import com.mas.co.service.StorageService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para DocumentoServiceImpl")
class DocumentoServiceImplTest {

  private static final String URL = "https://storage/tenant:bucket/documentos/2026/10/abc.pdf";

  @Mock private DocumentoRepository documentoRepository;

  @Mock private StorageService storageService;

  @InjectMocks private DocumentoServiceImpl documentoService;

  @TempDir Path tempDir;

  private File archivo;
  private DocumentoDto documentoDto;

  @BeforeEach
  void setUp() throws IOException {
    archivo = Files.write(tempDir.resolve("subida.tmp"), new byte[] {1, 2, 3}).toFile();
    documentoDto =
        DocumentoDto.builder().titulo("  Acta asamblea marzo  ").descripcion("  ").build();
  }

  @Test
  @DisplayName("Debe subir el archivo a la carpeta de documentos y registrar el documento")
  void debeSubirArchivoYRegistrarDocumento() {
    when(storageService.uploadFile(eq(archivo), anyString())).thenReturn(URL);
    when(documentoRepository.save(any(Documento.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    DocumentoDto resultado = documentoService.crear(documentoDto, archivo, "Acta Marzo.PDF", "admin");

    ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
    verify(storageService).uploadFile(eq(archivo), keyCaptor.capture());
    assertTrue(keyCaptor.getValue().matches("documentos/\\d{4}/\\d{1,2}/[0-9a-f-]{36}\\.pdf"));

    assertEquals("Acta asamblea marzo", resultado.getTitulo());
    assertNull(resultado.getDescripcion());
    assertEquals(URL, resultado.getArchivoUrl());
    assertEquals("Acta Marzo.PDF", resultado.getArchivoNombre());
    assertEquals("admin", resultado.getSubidoPor());
  }

  @Test
  @DisplayName("No debe subir nada si falta el archivo")
  void noDebeSubirSinArchivo() {
    assertThrows(
        ValidationException.class, () -> documentoService.crear(documentoDto, null, null, "admin"));

    verify(storageService, never()).uploadFile(any(File.class), anyString());
    verify(documentoRepository, never()).save(any());
  }

  @Test
  @DisplayName("No debe aceptar tipos de archivo no permitidos")
  void noDebeAceptarTipoNoPermitido() {
    assertThrows(
        ValidationException.class,
        () -> documentoService.crear(documentoDto, archivo, "programa.exe", "admin"));

    verify(storageService, never()).uploadFile(any(File.class), anyString());
  }

  @Test
  @DisplayName("No debe aceptar un documento sin título")
  void noDebeAceptarSinTitulo() {
    documentoDto.setTitulo("   ");

    assertThrows(
        ValidationException.class,
        () -> documentoService.crear(documentoDto, archivo, "acta.pdf", "admin"));

    verify(storageService, never()).uploadFile(any(File.class), anyString());
  }

  @Test
  @DisplayName("Debe borrar el archivo subido si no se puede registrar el documento")
  void debeBorrarArchivoSiFallaElRegistro() {
    when(storageService.uploadFile(eq(archivo), anyString())).thenReturn(URL);
    when(documentoRepository.save(any(Documento.class))).thenThrow(new IllegalStateException("BD caída"));

    assertThrows(
        IllegalStateException.class,
        () -> documentoService.crear(documentoDto, archivo, "acta.pdf", "admin"));

    verify(storageService).deleteFile(URL);
  }

  @Test
  @DisplayName("Debe eliminar el documento y su archivo")
  void debeEliminarDocumentoYArchivo() {
    Documento documento = Documento.builder().id(5L).titulo("Acta").archivoUrl(URL).build();
    when(documentoRepository.findById(5L)).thenReturn(Optional.of(documento));

    documentoService.eliminar(5L);

    verify(storageService).deleteFile(URL);
    verify(documentoRepository).delete(documento);
  }

  @Test
  @DisplayName("Debe lanzar excepción al eliminar un documento inexistente")
  void debeLanzarExcepcionAlEliminarInexistente() {
    when(documentoRepository.findById(9L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> documentoService.eliminar(9L));

    verify(storageService, never()).deleteFile(anyString());
  }

  @Test
  @DisplayName("Debe buscar por título solo cuando hay texto de búsqueda")
  void debeBuscarPorTituloSoloConTexto() {
    Pageable pageable = PageRequest.of(0, 15);
    Documento documento = Documento.builder().id(1L).titulo("Acta").archivoUrl(URL).build();
    when(documentoRepository.findAllByOrderByFechaSubidaDescIdDesc(pageable))
        .thenReturn(new PageImpl<>(List.of(documento)));
    when(documentoRepository.findByTituloContainingIgnoreCaseOrderByFechaSubidaDescIdDesc("acta", pageable))
        .thenReturn(new PageImpl<>(List.of(documento)));

    assertEquals(1, documentoService.listar("  ", pageable).getTotalElements());
    assertEquals(1, documentoService.listar(" acta ", pageable).getTotalElements());

    verify(documentoRepository).findAllByOrderByFechaSubidaDescIdDesc(pageable);
    verify(documentoRepository)
        .findByTituloContainingIgnoreCaseOrderByFechaSubidaDescIdDesc("acta", pageable);
  }
}
