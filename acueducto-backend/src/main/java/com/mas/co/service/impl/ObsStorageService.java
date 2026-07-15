package com.mas.co.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mas.co.service.StorageService;
import com.obs.services.ObsClient;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Profile("!dev")
public class ObsStorageService implements StorageService {

  private static final String BUCKET_NAME = "acueducto-hato";
  private static final String ENDPOINT = "obs.la-south-2.myhuaweicloud.com";
  private static final String METADATA_URL = "http://169.254.169.254/openstack/latest/securitykey";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public String uploadFile(MultipartFile file, String folder) {
    String objectKey = buildObjectKey(folder, file.getOriginalFilename());

    try {
      JsonNode credentials = getMetadataCredentials();
      ObsClient obsClient =
          new ObsClient(
              credentials.get("access").asText(),
              credentials.get("secret").asText(),
              credentials.get("securitytoken").asText(),
              ENDPOINT);

      try (InputStream inputStream = file.getInputStream()) {
        obsClient.putObject(BUCKET_NAME, objectKey, inputStream);
        String url = buildPublicUrl(objectKey);
        log.info("Archivo subido a OBS: {}", url);
        return url;
      } finally {
        obsClient.close();
      }
    } catch (Exception e) {
      log.error("Error al subir archivo a OBS: {}", e.getMessage(), e);
      throw new RuntimeException("Error al subir archivo a OBS: " + e.getMessage(), e);
    }
  }

  @Override
  public String uploadFile(File file, String objectKey) {
    try {
      JsonNode credentials = getMetadataCredentials();
      ObsClient obsClient =
          new ObsClient(
              credentials.get("access").asText(),
              credentials.get("secret").asText(),
              credentials.get("securitytoken").asText(),
              ENDPOINT);

      try {
        obsClient.putObject(BUCKET_NAME, objectKey, file);
        String url = buildPublicUrl(objectKey);
        log.info("Archivo subido a OBS: {}", url);
        return url;
      } finally {
        obsClient.close();
      }
    } catch (Exception e) {
      log.error("Error al subir archivo a OBS: {}", e.getMessage(), e);
      throw new RuntimeException("Error al subir archivo a OBS: " + e.getMessage(), e);
    }
  }

  @Override
  public void deleteFile(String fileUrl) {
    if (fileUrl == null || fileUrl.isBlank()) {
      return;
    }

    try {
      String objectKey = extractObjectKey(fileUrl);
      JsonNode credentials = getMetadataCredentials();
      ObsClient obsClient =
          new ObsClient(
              credentials.get("access").asText(),
              credentials.get("secret").asText(),
              credentials.get("securitytoken").asText(),
              ENDPOINT);

      try {
        obsClient.deleteObject(BUCKET_NAME, objectKey);
        log.info("Archivo eliminado de OBS: {}", objectKey);
      } finally {
        obsClient.close();
      }
    } catch (Exception e) {
      log.error("Error al eliminar archivo de OBS: {}", e.getMessage(), e);
    }
  }

  private String buildPublicUrl(String objectKey) {
    return "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + objectKey;
  }

  private String buildObjectKey(String folder, String originalFilename) {
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
    String extension = getExtension(originalFilename);
    String uniqueName = UUID.randomUUID().toString() + extension;
    return folder + "/" + date + "/" + uniqueName;
  }

  private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "";
    }
    return filename.substring(filename.lastIndexOf('.'));
  }

  private String extractObjectKey(String objectUrl) {
    String prefix = "https://" + BUCKET_NAME + "." + ENDPOINT + "/";
    if (objectUrl.startsWith(prefix)) {
      return objectUrl.substring(prefix.length());
    }
    return objectUrl;
  }

  private JsonNode getMetadataCredentials() throws Exception {
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(METADATA_URL)).GET().build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return MAPPER.readTree(response.body()).get("credential");
  }
}
