<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Detalle Factura #<s:property value="lectura.id"/>';
    document.getElementById('nav-facturas').classList.add('active');
</script>

<%-- Breadcrumb --%>
<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='facturas'/>" class="text-decoration-none">
        <i class="bi bi-arrow-left me-1"></i> Volver al listado
    </a>
</nav>

<div class="row g-4">

    <%-- Columna principal --%>
    <div class="col-lg-8">

        <%-- Info general --%>
        <div class="card fade-in-item mb-4">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <div>
                        <h5 class="fw-semibold mb-1">
                            <i class="bi bi-receipt me-2 text-primary"></i>Factura #<s:property value="lectura.id"/>
                        </h5>
                        <p class="text-muted mb-0" style="font-size:.85rem;">
                            <i class="bi bi-calendar3 me-1"></i>
                            Ingresada el <s:property value="lectura.fechaIngreso"/>
                        </p>
                    </div>
                    <div>
                        <s:if test="lectura.estadoPago == 'EFECTIVO'">
                            <span class="badge-status badge-active" style="font-size:.85rem;">
                                <i class="bi bi-cash me-1"></i>Pagada - Efectivo
                            </span>
                        </s:if>
                        <s:elseif test="lectura.estadoPago == 'BANCO'">
                            <span class="badge-status badge-active" style="font-size:.85rem;">
                                <i class="bi bi-bank me-1"></i>Pagada - Banco
                            </span>
                        </s:elseif>
                        <s:elseif test="lectura.estadoPago == 'VENCIDA'">
                            <span class="badge-status badge-expired" style="font-size:.85rem;">
                                <i class="bi bi-x-circle me-1"></i>Vencida
                            </span>
                        </s:elseif>
                        <s:else>
                            <span class="badge-status badge-pending" style="font-size:.85rem;">
                                <i class="bi bi-clock me-1"></i>Pendiente de Pago
                            </span>
                        </s:else>
                    </div>
                </div>

                <hr>

                <%-- Usuario y Período --%>
                <div class="row mb-3">
                    <div class="col-sm-6">
                        <label class="form-label text-muted" style="font-size:.75rem;">USUARIO</label>
                        <p class="fw-semibold mb-0">
                            <i class="bi bi-person me-1 text-primary"></i>
                            <s:property value="lectura.usuarioNombre"/>
                        </p>
                    </div>
                    <div class="col-sm-6">
                        <label class="form-label text-muted" style="font-size:.75rem;">PER&Iacute;ODO</label>
                        <p class="fw-semibold mb-0">
                            <i class="bi bi-calendar-month me-1 text-primary"></i>
                            <s:property value="lectura.mes"/> <s:property value="lectura.anio"/>
                        </p>
                    </div>
                </div>

                <%-- Lecturas y Consumo --%>
                <div class="row">
                    <div class="col-sm-4">
                        <label class="form-label text-muted" style="font-size:.75rem;">LECTURA ANTERIOR</label>
                        <p class="fw-semibold mb-0"><s:property value="lectura.lecturaAnterior"/></p>
                    </div>
                    <div class="col-sm-4">
                        <label class="form-label text-muted" style="font-size:.75rem;">LECTURA ACTUAL</label>
                        <p class="fw-semibold mb-0"><s:property value="lectura.lecturaActual"/></p>
                    </div>
                    <div class="col-sm-4">
                        <label class="form-label text-muted" style="font-size:.75rem;">CONSUMO</label>
                        <p class="fw-semibold mb-0">
                            <span class="text-primary"><s:property value="lectura.consumo"/> m&sup3;</span>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <%-- Desglose de valores --%>
        <div class="card fade-in-item">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-calculator me-2 text-primary"></i>Desglose de Factura
                </h6>

                <table class="table table-sm mb-0">
                    <tbody>
                        <tr>
                            <td class="text-muted">Valor Consumo (<s:property value="lectura.consumo"/> m&sup3;)</td>
                            <td class="text-end fw-semibold">$<s:property value="lectura.valorConsumo"/></td>
                        </tr>
                        <tr>
                            <td class="text-muted">Cargo Fijo</td>
                            <td class="text-end fw-semibold">$<s:property value="lectura.cargoFijo"/></td>
                        </tr>
                        <s:if test="lectura.valorCuota != null && lectura.valorCuota > 0">
                            <tr>
                                <td class="text-muted">
                                    Cuota
                                    <s:if test="lectura.cuotaId != null">
                                        <small class="text-muted">(#<s:property value="lectura.cuotaId"/>)</small>
                                    </s:if>
                                </td>
                                <td class="text-end fw-semibold">$<s:property value="lectura.valorCuota"/></td>
                            </tr>
                        </s:if>
                        <s:if test="lectura.deudaAnterior != null && lectura.deudaAnterior > 0">
                            <tr>
                                <td class="text-muted">Deuda Anterior</td>
                                <td class="text-end fw-semibold text-danger">$<s:property value="lectura.deudaAnterior"/></td>
                            </tr>
                        </s:if>
                        <s:if test="lectura.otrosCobros != null && lectura.otrosCobros > 0">
                            <tr>
                                <td class="text-muted">
                                    Otros Cobros
                                    <s:if test="lectura.otrosCobrosDescripcion != null && lectura.otrosCobrosDescripcion != ''">
                                        <small class="text-muted">(<s:property value="lectura.otrosCobrosDescripcion"/>)</small>
                                    </s:if>
                                </td>
                                <td class="text-end fw-semibold">$<s:property value="lectura.otrosCobros"/></td>
                            </tr>
                        </s:if>
                        <s:if test="lectura.noPago != null && lectura.noPago > 0">
                            <tr>
                                <td class="text-muted">Recargo por No Pago</td>
                                <td class="text-end fw-semibold text-warning">$<s:property value="lectura.noPago"/></td>
                            </tr>
                        </s:if>
                        <tr class="border-top">
                            <td class="fw-bold" style="font-size:1.05rem;">TOTAL</td>
                            <td class="text-end fw-bold text-primary" style="font-size:1.2rem;">
                                $<s:property value="lectura.valorTotal"/>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <%-- Columna lateral: acciones --%>
    <div class="col-lg-4">
        <div class="card fade-in-item">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-gear me-2"></i>Acciones
                </h6>

                <div class="d-grid gap-2">
                    <a href="<s:url namespace='/admin' action='factura-pdf'>
                                <s:param name='id' value='lectura.id'/>
                             </s:url>"
                       class="btn btn-outline-primary btn-sm">
                        <i class="bi bi-file-earmark-pdf me-1"></i> Descargar PDF
                    </a>
                    <s:if test="lectura.estadoPago == 'NO_PAGO'">
                        <a href="<s:url namespace='/admin' action='factura-pagar'>
                                    <s:param name='id' value='lectura.id'/>
                                    <s:param name='metodoPago' value='%{\"EFECTIVO\"}'/>
                                 </s:url>"
                           class="btn btn-success btn-sm"
                           data-confirm="pay-cash"
                           data-name="Factura #<s:property value='lectura.id'/> - $<s:property value='lectura.valorTotal'/>">
                            <i class="bi bi-cash me-1"></i> Pagar en Efectivo
                        </a>
                        <a href="<s:url namespace='/admin' action='factura-pagar'>
                                    <s:param name='id' value='lectura.id'/>
                                    <s:param name='metodoPago' value='%{\"BANCO\"}'/>
                                 </s:url>"
                           class="btn btn-primary btn-sm"
                           data-confirm="pay-bank"
                           data-name="Factura #<s:property value='lectura.id'/> - $<s:property value='lectura.valorTotal'/>">
                            <i class="bi bi-bank me-1"></i> Pagar por Banco
                        </a>
                        <hr>
                    </s:if>
                    <a href="<s:url namespace='/admin' action='factura-eliminar'>
                                <s:param name='id' value='lectura.id'/>
                             </s:url>"
                       class="btn btn-outline-danger btn-sm"
                       data-confirm="delete"
                       data-name="Factura #<s:property value='lectura.id'/>">
                        <i class="bi bi-trash3 me-1"></i> Eliminar Factura
                    </a>
                </div>
            </div>
        </div>
    </div>

</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
