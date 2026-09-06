package com.mas.co.constants;

/**
 * Constantes centralizadas para la API del sistema de acueducto.
 *
 * @author MAS Development Team
 * @version 1.0
 */
public final class ApiConstants {

    private ApiConstants() {

    }

    // API Paths
    public static final String API_BASE_PATH = "/api/v1";
    public static final String AUTH_PATH = API_BASE_PATH + "/auth";
    public static final String USERS_PATH = API_BASE_PATH + "/users";
    public static final String INVOICES_PATH = API_BASE_PATH + "/invoices";
    public static final String PAYMENTS_PATH = API_BASE_PATH + "/payments";
    public static final String QUOTAS_PATH = API_BASE_PATH + "/quotas";
    public static final String VALUES_PATH = API_BASE_PATH + "/values";
    public static final String REPORTS_PATH = API_BASE_PATH + "/reports";
    public static final String INGRESOS_PATH = API_BASE_PATH + "/ingresos";
    public static final String GASTOS_PATH = API_BASE_PATH + "/gastos";

    // HTTP Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CONTENT_TYPE_JSON = "application/json";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "id";
    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Validation Messages
    public static final String VALIDATION_NOT_NULL = "El campo no puede ser nulo";
    public static final String VALIDATION_NOT_EMPTY = "El campo no puede estar vacío";
    public static final String VALIDATION_SIZE_MIN_MAX = "El tamaño debe estar entre {min} y {max} caracteres";
    public static final String VALIDATION_POSITIVE = "El valor debe ser positivo";
    public static final String VALIDATION_EMAIL = "Debe ser un email válido";
}