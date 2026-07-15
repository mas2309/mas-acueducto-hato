package com.mas.co.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.csms.v1.CsmsClient;
import com.huaweicloud.sdk.csms.v1.model.ShowSecretStageRequest;
import com.huaweicloud.sdk.csms.v1.model.ShowSecretVersionRequest;
import com.huaweicloud.sdk.csms.v1.region.CsmsRegion;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Configuration
@Profile("huawei")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DewDataSourceConfig {

    private static final String SECRET_NAME = "postgres-acueductos-credenciales";
    private static final String REGION = "la-south-2";
    private static final String METADATA_URL = "http://169.254.169.254/openstack/latest/securitykey";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Bean
    public DataSource dataSource() throws Exception {
        JsonNode secret = fetchSecret();

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(secret.get("url").asText());
        ds.setUsername(secret.get("username").asText());
        ds.setPassword(secret.get("password").asText());
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setMaximumPoolSize(20);
        ds.setMinimumIdle(10);
        ds.setConnectionTimeout(30000);
        ds.setIdleTimeout(600000);
        ds.setMaxLifetime(1800000);
        return ds;
    }

    private JsonNode fetchSecret() throws Exception {
        JsonNode credentials = getMetadataCredentials();

        BasicCredentials auth = new BasicCredentials()
                .withAk(credentials.get("access").asText())
                .withSk(credentials.get("secret").asText())
                .withSecurityToken(credentials.get("securitytoken").asText());

        CsmsClient client = CsmsClient.newBuilder()
                .withCredential(auth)
                .withRegion(CsmsRegion.valueOf(REGION))
                .build();

        ShowSecretStageRequest stageRequest = new ShowSecretStageRequest();
        stageRequest.setSecretName(SECRET_NAME);
        stageRequest.setStageName("SYSCURRENT");

        String versionId = client.showSecretStage(stageRequest)
                .getStage()
                .getVersionId();

        ShowSecretVersionRequest request = new ShowSecretVersionRequest();
        request.setSecretName(SECRET_NAME);
        request.setVersionId(versionId);

        String secretString = client.showSecretVersion(request)
                .getVersion()
                .getSecretString();

        return MAPPER.readTree(secretString);
    }

    private JsonNode getMetadataCredentials() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(METADATA_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readTree(response.body()).get("credential");
    }
}
