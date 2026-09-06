package com.mas.co.service.impl;

import com.mas.co.service.StorageService;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Servicio de almacenamiento compatible con S3 para Contabo Object Storage,
 * alojado en la VPS. Reemplaza a Huawei Cloud OBS.
 *
 * <p>URL pública con el formato de Contabo: {@code {endpoint}/{tenantId}:{bucket}/{key}}.
 */
@Slf4j
@Service
@Profile("!dev")
public class ContaboStorageService implements StorageService {

    @Value("${storage.endpoint}")
    private String endpoint;

    @Value("${storage.tenant-id}")
    private String tenantId;

    @Value("${storage.access-key}")
    private String accessKey;

    @Value("${storage.secret-key}")
    private String secretKey;

    @Value("${storage.bucket}")
    private String bucket;

    private S3Client client;

    @PostConstruct
    void init() {
        if (endpoint == null || endpoint.isBlank() || accessKey == null || accessKey.isBlank()
                || secretKey == null || secretKey.isBlank() || tenantId == null || tenantId.isBlank()
                || bucket == null || bucket.isBlank()) {
            throw new IllegalStateException(
                    "Configuración de storage incompleta: revisa las variables de entorno "
                            + "STORAGE_ENDPOINT, STORAGE_TENANT_ID, STORAGE_BUCKET, "
                            + "CONTABO_ACCESS_KEY y CONTABO_SECRET_KEY (ninguna puede estar vacía).");
        }
        log.info("Inicializando ContaboStorageService: endpoint={}, tenantId={}, bucket={}, "
                        + "URL base resultante={}",
                endpoint, tenantId, bucket, endpoint + "/" + tenantId + ":" + bucket);
        client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("us-east-1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        String key = buildObjectKey(folder, file.getOriginalFilename());
        try {
            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            String url = buildUrl(key);
            log.info("Archivo subido a Contabo: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Error al subir archivo '{}' a Contabo: {}", key, e.getMessage(), e);
            throw new RuntimeException("Error al subir archivo a Contabo: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFile(File file, String objectKey) {
        try {
            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(objectKey)
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build(),
                    RequestBody.fromFile(file));
            String url = buildUrl(objectKey);
            log.info("Archivo subido a Contabo: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Error al subir archivo '{}' a Contabo: {}", objectKey, e.getMessage(), e);
            throw new RuntimeException("Error al subir archivo a Contabo: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        try {
            String key = extractObjectKey(fileUrl);
            client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
            log.info("Archivo eliminado de Contabo: {}", key);
        } catch (Exception e) {
            log.error("Error al eliminar archivo de Contabo: {}", e.getMessage(), e);
        }
    }

    private String buildObjectKey(String folder, String originalFilename) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String uniqueName = UUID.randomUUID() + getExtension(originalFilename);
        return folder + "/" + date + "/" + uniqueName;
    }

    private String buildUrl(String key) {
        return endpoint + "/" + tenantId + ":" + bucket + "/" + key;
    }

    private String extractObjectKey(String fileUrl) {
        String marker = tenantId + ":" + bucket + "/";
        int idx = fileUrl.indexOf(marker);
        return idx >= 0 ? fileUrl.substring(idx + marker.length()) : fileUrl;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
