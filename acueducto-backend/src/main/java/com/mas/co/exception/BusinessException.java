package com.mas.co.exception;

/**
 * Excepción para errores de lógica de negocio.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
public class BusinessException extends BaseException {

    public static final String INVOICE_ALREADY_EXISTS = "INVOICE_ALREADY_EXISTS";
    public static final String INVALID_READING = "INVALID_READING";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String INVOICE_NOT_FOUND = "INVOICE_NOT_FOUND";
    public static final String QUOTA_NOT_FOUND = "QUOTA_NOT_FOUND";
    public static final String QUOTA_NOT_ACTIVE = "QUOTA_NOT_ACTIVE";
    public static final String VALUES_NOT_CONFIGURED = "VALUES_NOT_CONFIGURED";
    public static final String CONSECUTIVE_NO_PAYMENTS = "CONSECUTIVE_NO_PAYMENTS";
    public static final String INVOICE_ALREADY_PAID = "INVOICE_ALREADY_PAID";
    public static final String INVALID_PAYMENT_METHOD = "INVALID_PAYMENT_METHOD";
    public static final String NON_SEQUENTIAL_INVOICE_PERIOD = "NON_SEQUENTIAL_INVOICE_PERIOD";

    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(String errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    // Factory methods for common business exceptions
    public static BusinessException invoiceAlreadyExists(String month, Integer year, Long userId) {
        return new BusinessException(
            INVOICE_ALREADY_EXISTS,
            String.format("Ya existe una factura para el mes %s del año %d para el usuario %d", month, year, userId),
            month, year, userId
        );
    }

    public static BusinessException invalidReading(Integer currentReading, Integer previousReading) {
        return new BusinessException(
            INVALID_READING,
            String.format("La lectura actual (%d) debe ser mayor a la anterior (%d)", currentReading, previousReading),
            currentReading, previousReading
        );
    }

    public static BusinessException userNotFound(Long userId) {
        return new BusinessException(
            USER_NOT_FOUND,
            String.format("Usuario con ID %d no encontrado o inactivo", userId),
            userId
        );
    }

    public static BusinessException invoiceNotFound(Long invoiceId) {
        return new BusinessException(
            INVOICE_NOT_FOUND,
            String.format("Factura con ID %d no encontrada", invoiceId),
            invoiceId
        );
    }

    public static BusinessException quotaNotFound(Long quotaId) {
        return new BusinessException(
            QUOTA_NOT_FOUND,
            String.format("Cuota con ID %d no encontrada o inactiva", quotaId),
            quotaId
        );
    }

    public static BusinessException quotaNotActive(Long quotaId) {
        return new BusinessException(
            QUOTA_NOT_ACTIVE,
            String.format("La cuota con ID %d no está activa para procesar pagos", quotaId),
            quotaId
        );
    }

    public static BusinessException invoiceAlreadyPaid(Long invoiceId) {
        return new BusinessException(
            INVOICE_ALREADY_PAID,
            String.format("La factura con ID %d ya ha sido pagada", invoiceId),
            invoiceId
        );
    }

    public static BusinessException invalidPaymentMethod(String method) {
        return new BusinessException(
            INVALID_PAYMENT_METHOD,
            String.format("El método de pago '%s' no es válido. Use 'EFECTIVO' o 'BANCO'", method),
            method
        );
    }

    public static BusinessException nonSequentialInvoicePeriod(String providedMonth, Integer providedYear, String expectedMonth, Integer expectedYear) {
        return new BusinessException(
            NON_SEQUENTIAL_INVOICE_PERIOD,
            String.format("El período de la factura (%s %d) no es consecutivo. Se esperaba el período (%s %d).", providedMonth, providedYear, expectedMonth, expectedYear),
            providedMonth, providedYear, expectedMonth, expectedYear
        );
    }
}