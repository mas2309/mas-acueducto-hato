<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>document.getElementById('pageTitle').textContent = 'Facturas';</script>
<script>document.getElementById('nav-facturas').classList.add('active');</script>

<%-- Alertas --%>
<s:if test="mensajeExito != null && mensajeExito != ''">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill"></i>
        <s:property value="mensajeExito"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>
<s:if test="mensajeError != null && mensajeError != ''">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-circle-fill"></i>
        <s:property value="mensajeError"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>

<%-- Table Container --%>
<div class="table-container fade-in-item">

    <%-- Header --%>
    <div class="table-header">
        <div class="d-flex align-items-center gap-3 flex-grow-1 flex-wrap">
            <h5><i class="bi bi-receipt me-2"></i>Facturas</h5>

            <%-- Filtros --%>
            <div class="btn-group btn-group-sm ms-2">
                <a href="<s:url namespace='/admin' action='facturas'/>"
                   class="btn btn-outline-secondary <s:if test='filtro == null || filtro == \"\"'>active</s:if>">
                    Todas
                </a>
                <a href="<s:url namespace='/admin' action='facturas'><s:param name='filtro' value='%{\"pendientes\"}'/></s:url>"
                   class="btn btn-outline-warning <s:if test='filtro == \"pendientes\"'>active</s:if>">
                    <i class="bi bi-clock"></i> Pendientes
                </a>
                <a href="<s:url namespace='/admin' action='facturas'><s:param name='filtro' value='%{\"vencidas\"}'/></s:url>"
                   class="btn btn-outline-danger <s:if test='filtro == \"vencidas\"'>active</s:if>">
                    <i class="bi bi-x-circle"></i> Vencidas
                </a>
                <a href="<s:url namespace='/admin' action='facturas'><s:param name='filtro' value='%{\"pagadas\"}'/></s:url>"
                   class="btn btn-outline-success <s:if test='filtro == \"pagadas\"'>active</s:if>">
                    <i class="bi bi-check2-all"></i> Pagadas
                </a>
            </div>

            <%-- Búsqueda --%>
            <form id="searchForm" action="<s:url namespace='/admin' action='facturas'/>" method="get" class="d-flex gap-2 ms-auto flex-wrap">
                <%-- Selector de año (solo visible en vista general) --%>
                <s:if test='filtro == null || filtro == ""'>
                    <select name="anioFiltro" class="form-control form-control-sm" style="width:90px;"
                            onchange="this.form.submit()">
                        <s:iterator begin="%{anioFiltro - 3}" end="%{anioFiltro}" var="a">
                            <option value="<s:property value='#a'/>" <s:if test="#a == anioFiltro">selected</s:if>>
                                <s:property value="#a"/>
                            </option>
                        </s:iterator>
                    </select>
                </s:if>
                <div class="search-box">
                    <i class="bi bi-search search-icon"></i>
                    <input type="text" id="searchInput" name="q" value="<s:property value='q'/>" class="form-control"
                           placeholder="Buscar por usuario..." style="width:220px;"/>
                </div>
                <button type="submit" class="btn btn-sm btn-primary">
                    <i class="bi bi-search"></i>
                </button>
                <button type="button" id="searchClear" class="btn btn-sm btn-outline-secondary" style="display:none;">
                    <i class="bi bi-x-lg"></i>
                </button>
            </form>
        </div>
        <a href="<s:url namespace='/admin' action='lectura-formulario'/>" class="btn btn-primary btn-sm">
            <i class="bi bi-plus-lg me-1"></i> Nueva Lectura
        </a>
    </div>

    <%-- Table --%>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Per&iacute;odo</th>
                    <th>Consumo</th>
                    <th>Valor Total</th>
                    <th>Estado</th>
                    <th class="text-center">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <s:if test="facturas != null && facturas.size() > 0">
                    <s:iterator value="facturas">
                        <tr class="fade-in-item">
                            <td class="fw-semibold">#<s:property value="id"/></td>
                            <td>
                                <span class="text-muted">
                                    <i class="bi bi-person me-1"></i><s:property value="usuarioNombre"/>
                                </span>
                            </td>
                            <td>
                                <s:property value="mes"/> <s:property value="anio"/>
                            </td>
                            <td>
                                <span title="Lectura: <s:property value='lecturaAnterior'/> → <s:property value='lecturaActual'/>">
                                    <s:property value="consumo"/> m&sup3;
                                </span>
                            </td>
                            <td class="fw-semibold">$<s:property value="valorTotal"/></td>
                            <td>
                                <s:if test="estadoPago == 'EFECTIVO'">
                                    <span class="badge-status badge-active">
                                        <i class="bi bi-cash me-1"></i>Efectivo
                                    </span>
                                </s:if>
                                <s:elseif test="estadoPago == 'BANCO'">
                                    <span class="badge-status badge-active">
                                        <i class="bi bi-bank me-1"></i>Banco
                                    </span>
                                </s:elseif>
                                <s:elseif test="estadoPago == 'VENCIDA'">
                                    <span class="badge-status badge-expired">
                                        <i class="bi bi-x-circle me-1"></i>Vencida
                                    </span>
                                </s:elseif>
                                <s:else>
                                    <span class="badge-status badge-pending">
                                        <i class="bi bi-clock me-1"></i>Pendiente
                                    </span>
                                </s:else>
                            </td>
                            <td class="text-center">
                                <div class="d-flex gap-1 justify-content-center">
                                    <a href="<s:url namespace='/admin' action='factura-detalle'>
                                                <s:param name='id' value='id'/>
                                             </s:url>"
                                       class="btn-action btn-action-view" title="Ver Detalle">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    <a href="<s:url namespace='/admin' action='factura-pdf'>
                                                <s:param name='id' value='id'/>
                                             </s:url>"
                                       class="btn-action btn-action-pdf" title="Descargar PDF">
                                        <i class="bi bi-file-pdf"></i>
                                    </a>
                                    <s:if test="estadoPago == 'NO_PAGO'">
                                        <a href="<s:url namespace='/admin' action='factura-pagar'>
                                                    <s:param name='id' value='id'/>
                                                    <s:param name='metodoPago' value='%{\"EFECTIVO\"}'/>
                                                 </s:url>"
                                           class="btn-action btn-action-pay"
                                           title="Pagar en Efectivo"
                                           data-confirm="pay-cash"
                                           data-name="Factura #<s:property value='id'/> - $<s:property value='valorTotal'/>">
                                            <i class="bi bi-cash"></i>
                                        </a>
                                        <a href="<s:url namespace='/admin' action='factura-pagar'>
                                                    <s:param name='id' value='id'/>
                                                    <s:param name='metodoPago' value='%{\"BANCO\"}'/>
                                                 </s:url>"
                                           class="btn-action btn-action-bank"
                                           title="Pagar por Banco"
                                           data-confirm="pay-bank"
                                           data-name="Factura #<s:property value='id'/> - $<s:property value='valorTotal'/>">
                                            <i class="bi bi-bank"></i>
                                        </a>
                                    </s:if>
                                    <a href="<s:url namespace='/admin' action='factura-eliminar'>
                                                <s:param name='id' value='id'/>
                                             </s:url>"
                                       class="btn-action btn-action-delete"
                                       title="Eliminar"
                                       data-confirm="delete"
                                       data-name="Factura #<s:property value='id'/> de <s:property value='usuarioNombre'/>">
                                        <i class="bi bi-trash3"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </s:iterator>
                </s:if>
                <s:else>
                    <tr>
                        <td colspan="7">
                            <div class="empty-state">
                                <i class="bi bi-receipt"></i>
                                <p>No se encontraron facturas.</p>
                            </div>
                        </td>
                    </tr>
                </s:else>
            </tbody>
        </table>
    </div>

    <%-- Footer: info + pagination --%>
    <s:if test="facturas != null && facturas.size() > 0">
        <div class="table-footer">
            <span class="table-info">
                Mostrando p&aacute;gina <strong><s:property value="page + 1"/></strong>
                de <strong><s:property value="totalPages"/></strong>
                &mdash; <s:property value="totalElements"/> registros
            </span>

            <s:if test="totalPages > 1">
                <nav>
                    <ul class="pagination pagination-sm mb-0">
                        <s:if test="page > 0">
                            <li class="page-item">
                                <a class="page-link"
                                   href="<s:url namespace='/admin' action='facturas'>
                                            <s:param name='page' value='%{page - 1}'/>
                                            <s:param name='size' value='%{size}'/>
                                            <s:param name='q' value='%{q}'/>
                                            <s:param name='filtro' value='%{filtro}'/>
                                            <s:param name='anioFiltro' value='%{anioFiltro}'/>
                                         </s:url>">
                                    <i class="bi bi-chevron-left"></i>
                                </a>
                            </li>
                        </s:if>
                        <s:else>
                            <li class="page-item disabled">
                                <span class="page-link"><i class="bi bi-chevron-left"></i></span>
                            </li>
                        </s:else>

                        <s:iterator begin="%{[page - 2] > 0 ? page - 2 : 0}" end="%{[page + 2] < totalPages - 1 ? page + 2 : totalPages - 1}" var="i">
                            <s:if test="#i == page">
                                <li class="page-item active">
                                    <span class="page-link"><s:property value="#i + 1"/></span>
                                </li>
                            </s:if>
                            <s:else>
                                <li class="page-item">
                                    <a class="page-link"
                                       href="<s:url namespace='/admin' action='facturas'>
                                                <s:param name='page' value='%{#i}'/>
                                                <s:param name='size' value='%{size}'/>
                                                <s:param name='q' value='%{q}'/>
                                                <s:param name='filtro' value='%{filtro}'/>
                                                <s:param name='anioFiltro' value='%{anioFiltro}'/>
                                             </s:url>">
                                        <s:property value="#i + 1"/>
                                    </a>
                                </li>
                            </s:else>
                        </s:iterator>

                        <s:if test="page < totalPages - 1">
                            <li class="page-item">
                                <a class="page-link"
                                   href="<s:url namespace='/admin' action='facturas'>
                                            <s:param name='page' value='%{page + 1}'/>
                                            <s:param name='size' value='%{size}'/>
                                            <s:param name='q' value='%{q}'/>
                                            <s:param name='filtro' value='%{filtro}'/>
                                            <s:param name='anioFiltro' value='%{anioFiltro}'/>
                                         </s:url>">
                                    <i class="bi bi-chevron-right"></i>
                                </a>
                            </li>
                        </s:if>
                        <s:else>
                            <li class="page-item disabled">
                                <span class="page-link"><i class="bi bi-chevron-right"></i></span>
                            </li>
                        </s:else>
                    </ul>
                </nav>
            </s:if>
        </div>
    </s:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
