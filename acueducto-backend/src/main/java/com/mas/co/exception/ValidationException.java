package com.mas.co.exception;

import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Excepción para errores de validación.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Getter
public class ValidationException extends BaseException {

  public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
  public static final String FIELD_VALIDATION_ERROR = "FIELD_VALIDATION_ERROR";

  private final Map<String, List<String>> fieldErrors;

  public ValidationException(String message) {
    super(VALIDATION_ERROR, message);
    this.fieldErrors = Map.of();
  }

  public ValidationException(String message, Map<String, List<String>> fieldErrors) {
    super(FIELD_VALIDATION_ERROR, message);
    this.fieldErrors = fieldErrors;
  }

  public boolean hasFieldErrors() {
    return fieldErrors != null && !fieldErrors.isEmpty();
  }
}
