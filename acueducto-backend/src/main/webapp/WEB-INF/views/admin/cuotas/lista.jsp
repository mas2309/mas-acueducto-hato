<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>document.getElementById('pageTitle').textContent = 'Cuotas';</script>
<script>document.getElementById('nav-cuotas').classList.add('active');</script>

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
            <h5><i class="bi bi-credit-card me-2"></i>Listado de Cuotas</h5>
            <form id="searchForm" action="<s:url namespace='/admin' action='cuotas'/>" method="get" class="d-flex gap-2 ms-auto flex-wrap">
                <div class="search-box">
                    <i class="bi bi-search search-icon"></i>
                    <input type="text" id="searchInput" name="q" value="<s:property value='q'/>" class="form-control"
                           placeholder="Buscar por descripción..." style="width:240px;"/>
                </div>
                <button type="submit" class="btn btn-sm btn-primary">
                    <i class="bi bi-search"></i>
                </button>
                <button type="button" id="searchClear" class="btn btn-sm btn-outline-secondary" style="display:none;">
                    <i class="bi bi-x-lg"></i>
                </button>
            </form>
        </div>
        <a href="<s:url namespace='/admin' action='cuota-formulario'/>" class="btn btn-primary btn-sm">
            <i class="bi bi-plus-lg me-1"></i> Nueva Cuota
        </a>
    </div>

    <%-- Table --%>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descripci&oacute;n</th>
                    <th>Usuario</th>
                    <th>Valor Total</th>
                    <th>Valor Cuota</th>
                    <th>Progreso</th>
                    <th>Estado</th>
                    <th class="text-center">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <s:if test="cuotas != null && cuotas.size() > 0">
                    <s:iterator value="cuotas">
                        <tr class="fade-in-item">
                            <td class="fw-semibold">#<s:property value="id"/></td>
                            <td><s:property value="descripcion"/></td>
                            <td>
                                <span class="text-muted">
                                    <i class="bi bi-person me-1"></i><s:property value="usuarioNombre"/>
                                </span>
                            </td>
                            <td>$<s:property value="valorTotal"/></td>
                            <td>$<s:property value="valorCuota"/></td>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <div class="progress flex-grow-1" style="height:6px; min-width:60px;">
                                        <div class="progress-bar bg-primary" role="progressbar"
                                             style="width: <s:property value='numeroCuota > 0 ? (cuotaActual * 100 / numeroCuota) : 0'/>%">
                                        </div>
                                    </div>
                                    <small class="text-muted text-nowrap">
                                        <s:property value="cuotaActual"/>/<s:property value="numeroCuota"/>
                                    </small>
                                </div>
                            </td>
                            <td>
                                <s:if test="activo">
                                    <span class="badge-status badge-active">
                                        <i class="bi bi-check-circle-fill me-1"></i>Activa
                                    </span>
                                </s:if>
                                <s:else>
                                    <span class="badge-status badge-inactive">
                                        <i class="bi bi-dash-circle me-1"></i>Inactiva
                                    </span>
                                </s:else>
                            </td>
                            <td class="text-center">
                                <div class="d-flex gap-1 justify-content-center">
                                    <s:if test="activo">
                                        <a href="<s:url namespace='/admin' action='cuota-pago'>
                                                    <s:param name='id' value='id'/>
                                                 </s:url>"
                                           class="btn-action btn-action-pay"
                                           title="Registrar Pago"
                                           data-confirm="pay"
                                           data-name="<s:property value='descripcion'/> (cuota <s:property value='cuotaActual + 1'/>/<s:property value='numeroCuota'/>)">
                                            <i class="bi bi-cash-coin"></i>
                                        </a>
                                    </s:if>
                                    <a href="<s:url namespace='/admin' action='cuota-formulario'>
                                                <s:param name='id' value='id'/>
                                             </s:url>"
                                       class="btn-action btn-action-edit" title="Editar">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <s:if test="activo">
                                        <a href="<s:url namespace='/admin' action='cuota-desactivar'>
                                                    <s:param name='id' value='id'/>
                                                 </s:url>"
                                           class="btn-action btn-action-deactivate"
                                           title="Desactivar"
                                           data-confirm="deactivate"
                                           data-name="<s:property value='descripcion'/>">
                                            <i class="bi bi-toggle-off"></i>
                                        </a>
                                    </s:if>
                                    <a href="<s:url namespace='/admin' action='cuota-eliminar'>
                                                <s:param name='id' value='id'/>
                                             </s:url>"
                                       class="btn-action btn-action-delete"
                                       title="Eliminar"
                                       data-confirm="delete"
                                       data-name="<s:property value='descripcion'/>">
                                        <i class="bi bi-trash3"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </s:iterator>
                </s:if>
                <s:else>
                    <tr>
                        <td colspan="8">
                            <div class="empty-state">
                                <i class="bi bi-credit-card"></i>
                                <p>No se encontraron cuotas.</p>
                            </div>
                        </td>
                    </tr>
                </s:else>
            </tbody>
        </table>
    </div>

    <%-- Footer: info + pagination --%>
    <s:if test="cuotas != null && cuotas.size() > 0">
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
                                   href="<s:url namespace='/admin' action='cuotas'>
                                            <s:param name='page' value='%{page - 1}'/>
                                            <s:param name='size' value='%{size}'/>
                                            <s:param name='q' value='%{q}'/>
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

                        <s:iterator begin="0" end="%{totalPages - 1}" var="i">
                            <s:if test="#i == page">
                                <li class="page-item active">
                                    <span class="page-link"><s:property value="#i + 1"/></span>
                                </li>
                            </s:if>
                            <s:else>
                                <li class="page-item">
                                    <a class="page-link"
                                       href="<s:url namespace='/admin' action='cuotas'>
                                                <s:param name='page' value='%{#i}'/>
                                                <s:param name='size' value='%{size}'/>
                                                <s:param name='q' value='%{q}'/>
                                             </s:url>">
                                        <s:property value="#i + 1"/>
                                    </a>
                                </li>
                            </s:else>
                        </s:iterator>

                        <s:if test="page < totalPages - 1">
                            <li class="page-item">
                                <a class="page-link"
                                   href="<s:url namespace='/admin' action='cuotas'>
                                            <s:param name='page' value='%{page + 1}'/>
                                            <s:param name='size' value='%{size}'/>
                                            <s:param name='q' value='%{q}'/>
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
