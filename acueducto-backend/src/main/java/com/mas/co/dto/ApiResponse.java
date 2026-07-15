package com.mas.co.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Respuesta estándar para todas las operaciones de la API.
 * 
 * @param <T> tipo de datos en la respuesta
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta estándar de la API")
public class ApiResponse<T> {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    @Builder.Default
    private Boolean success = true;

    @Schema(description = "Mensaje descriptivo de la operación", example = "Operación completada exitosamente")
    private String message;

    @Schema(description = "Datos de la respuesta")
    private T data;

    @Schema(description = "Timestamp de la respuesta", example = "2024-01-15T10:30:00")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Información de paginación")
    private PaginationInfo pagination;

    /**
     * Crea una respuesta exitosa con datos.
     * 
     * @param data datos de la respuesta
     * @param message mensaje descriptivo
     * @param <T> tipo de datos
     * @return respuesta exitosa
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Crea una respuesta exitosa con datos.
     * 
     * @param data datos de la respuesta
     * @param <T> tipo de datos
     * @return respuesta exitosa
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operación completada exitosamente");
    }

    /**
     * Crea una respuesta exitosa sin datos.
     * 
     * @param message mensaje descriptivo
     * @return respuesta exitosa
     */
    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * Crea una respuesta exitosa con paginación.
     * 
     * @param data datos de la respuesta
     * @param pagination información de paginación
     * @param <T> tipo de datos
     * @return respuesta exitosa con paginación
     */
    public static <T> ApiResponse<T> success(T data, PaginationInfo pagination) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Datos obtenidos exitosamente")
                .data(data)
                .pagination(pagination)
                .build();
    }
}