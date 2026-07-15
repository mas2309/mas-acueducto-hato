package com.mas.co.service;

import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.PagoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz del servicio de Lectura.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
public interface LecturaService {

    /**
     * Obtiene todas las facturas paginadas ordenadas por creación.
     * 
     * @param pageable información de paginación
     * @return página de facturas
     */
    Page<LecturaDto> obtenerTodasFacturas(Pageable pageable);

    /**
     * Obtiene una factura por su ID.
     * 
     * @param facturaId ID de la factura
     * @return factura encontrada
     */
    LecturaDto obtenerFactura(Long facturaId);

    /**
     * Ingresa una nueva lectura y genera la factura.
     * 
     * @param lecturaDto datos de la lectura
     * @return lectura procesada con factura generada
     */
    LecturaDto ingresarLectura(LecturaDto lecturaDto);

    /**
     * Obtiene la última lectura de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return última lectura del usuario
     */
    LecturaDto obtenerUltimaLectura(Long usuarioId);

    /**
     * Obtiene facturas por usuario paginadas.
     * 
     * @param usuarioId ID del usuario
     * @param pageable información de paginación
     * @return página de facturas del usuario
     */
    Page<LecturaDto> obtenerFacturasPorUsuario(Long usuarioId, Pageable pageable);

    /**
     * Obtiene facturas por período paginadas.
     * 
     * @param mes mes de consulta
     * @param anio año de consulta
     * @param pageable información de paginación
     * @return página de facturas del período
     */
    Page<LecturaDto> obtenerFacturasPorPeriodo(String mes, Integer anio, Pageable pageable);

    /**
     * Obtiene facturas pendientes de pago.
     * 
     * @param pageable información de paginación
     * @return página de facturas pendientes
     */
    Page<LecturaDto> obtenerFacturasPendientes(Pageable pageable);

    /**
     * Obtiene facturas pagadas.
     * 
     * @param pageable información de paginación
     * @return página de facturas pagadas
     */
    Page<LecturaDto> obtenerFacturasPagadas(Pageable pageable);

    /**
     * Busca facturas por nombre de usuario.
     * 
     * @param termino término de búsqueda
     * @param pageable información de paginación
     * @return página de facturas encontradas
     */
    Page<LecturaDto> buscarFacturas(String termino, Pageable pageable);

    /**
     * Obtiene la deuda pendiente de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return monto de deuda pendiente
     */
    Double obtenerDeudaPendiente(Long usuarioId);

    /**
     * Actualiza una factura existente.
     * 
     * @param facturaId ID de la factura a actualizar
     * @param lecturaDto datos actualizados de la factura
     * @return factura actualizada
     */
    LecturaDto actualizarFactura(Long facturaId, LecturaDto lecturaDto);

    /**
     * Elimina una factura por su ID.
     * 
     * @param facturaId ID de la factura a eliminar
     */
    void eliminarFactura(Long facturaId);

    /**
     * Registra el pago de una factura.
     *
     * @param facturaId ID de la factura a pagar
     * @param pagoDto   Datos del pago
     * @return Factura actualizada
     */
    LecturaDto registrarPago(Long facturaId, PagoDto pagoDto);
}