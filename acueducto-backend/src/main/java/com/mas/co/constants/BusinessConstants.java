package com.mas.co.constants;

import java.util.Map;

/**
 * Constantes de negocio para el sistema de acueducto.
 *
 * @author MAS Development Team
 * @version 1.0
 */
public final class BusinessConstants {

  private BusinessConstants() {
    // Utility class
  }

  // Payment Types
  public static final String PAYMENT_TYPE_CASH = "EFECTIVO";
  public static final String PAYMENT_TYPE_BANK = "BANCO";
  public static final String PAYMENT_TYPE_NO_PAYMENT = "NO_PAGO";

  // User Status
  public static final boolean USER_ACTIVE = true;

  // Quota Status
  public static final boolean QUOTA_ACTIVE = true;

  // Default Values
  public static final double DEFAULT_DEBT = 0.0;
  public static final double DEFAULT_OTHER_CHARGES = 0.0;
  public static final int DEFAULT_CONSUMPTION = 0;

  // Business Rules
  public static final int MIN_READING_VALUE = 0;
  public static final int MAX_READING_VALUE = 999999;
  public static final int MIN_NAME_LENGTH = 4;
  public static final int MAX_NAME_LENGTH = 20;

  // Month Maps
  public static final Map<String, Integer> MONTH_TO_INT =
      Map.ofEntries(
          Map.entry("Enero", 1),
          Map.entry("Febrero", 2),
          Map.entry("Marzo", 3),
          Map.entry("Abril", 4),
          Map.entry("Mayo", 5),
          Map.entry("Junio", 6),
          Map.entry("Julio", 7),
          Map.entry("Agosto", 8),
          Map.entry("Septiembre", 9),
          Map.entry("Octubre", 10),
          Map.entry("Noviembre", 11),
          Map.entry("Diciembre", 12));

  public static final Map<Integer, String> INT_TO_MONTH =
      Map.ofEntries(
          Map.entry(1, "Enero"),
          Map.entry(2, "Febrero"),
          Map.entry(3, "Marzo"),
          Map.entry(4, "Abril"),
          Map.entry(5, "Mayo"),
          Map.entry(6, "Junio"),
          Map.entry(7, "Julio"),
          Map.entry(8, "Agosto"),
          Map.entry(9, "Septiembre"),
          Map.entry(10, "Octubre"),
          Map.entry(11, "Noviembre"),
          Map.entry(12, "Diciembre"));
}
