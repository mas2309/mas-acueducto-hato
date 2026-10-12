<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>document.getElementById('pageTitle').textContent = 'Cobros Globales';</script>
<script>document.getElementById('nav-cobros-globales').classList.add('active');</script>

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
            <h5><i class="bi bi-collection me-2"></i>Listado de Cobros Globales</h5>
        </div>
        <a href="<s:url namespace='/admin' action='cobro-global-formulario'/>" class="btn btn-primary btn-sm">
            <i class="bi bi-plus-lg me-1"></i> Nuevo Cobro Global
        </a>
    </div>

    <%-- Table --%>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descripci&oacute;n</th>
                    <th>Fecha</th>
                    <th>Valor por Usuario</th>
                    <th>Cuotas</th>
                    <th>Usuarios</th>
                    <th>Facturado</th>
                    <th>Estado</th>
                    <th class="text-center">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <s:if test="cobrosGlobales != null && cobrosGlobales.size() > 0">
                    <s:iterator value="cobrosGlobales">
                        <tr class="fade-in-item">
                            <td class="fw-semibold">#<s:property value="id"/></td>
                            <td><s:property value="descripcion"/></td>
                            <td>
                                <span class="text-muted"><s:property value="fechaCreacion"/></span>
                            </td>
                            <td>$<s:property value="valorTotal"/></td>
                            <td>
                                <s:property value="numeroCuotas"/> x $<s:property value="valorCuota"/>
                            </td>
                            <td>
                                <s:property value="totalUsuarios"/>
                                <s:if test="usuariosPagoAnticipado > 0">
                                    <small class="text-muted">(<s:property value="usuariosPagoAnticipado"/> anticipados)</small>
                                </s:if>
                            </td>
                            <td>
                                $<s:property value="valorFacturado"/>
                                <small class="text-muted">de $<s:property value="valorEsperado"/></small>
                            </td>
                            <td>
                                <s:if test="!activo">
                                    <span class="badge-status badge-inactive">
                                        <i class="bi bi-slash-circle me-1"></i>Cancelado
                                    </span>
                                </s:if>
                                <s:elseif test="usuariosEnCobro > 0">
                                    <span class="badge-status badge-active">
                                        <i class="bi bi-check-circle-fill me-1"></i>En cobro
                                    </span>
                                </s:elseif>
                                <s:else>
                                    <span class="badge-status badge-inactive">
                                        <i class="bi bi-check2-all me-1"></i>Finalizado
                                    </span>
                                </s:else>
                            </td>
                            <td class="text-center">
                                <div class="d-flex gap-1 justify-content-center">
                                    <a href="<s:url namespace='/admin' action='cobro-global-detalle'>
                                                <s:param name='id' value='id'/>
                                             </s:url>"
                                       class="btn-action btn-action-view" title="Ver usuarios">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    <s:if test="activo && usuariosEnCobro > 0">
                                        <a href="<s:url namespace='/admin' action='cobro-global-cancelar'>
                                                    <s:param name='id' value='id'/>
                                                 </s:url>"
                                           class="btn-action btn-action-deactivate"
                                           title="Cancelar cobro"
                                           data-confirm="cancel-charge"
                                           data-name="<s:property value='descripcion'/>">
                                            <i class="bi bi-slash-circle"></i>
                                        </a>
                                    </s:if>
                                    <s:if test="valorFacturado == 0 && usuariosPagoAnticipado == 0">
                                        <a href="<s:url namespace='/admin' action='cobro-global-eliminar'>
                                                    <s:param name='id' value='id'/>
                                                 </s:url>"
                                           class="btn-action btn-action-delete"
                                           title="Eliminar"
                                           data-confirm="delete"
                                           data-name="<s:property value='descripcion'/>">
                                            <i class="bi bi-trash3"></i>
                                        </a>
                                    </s:if>
                                </div>
                            </td>
                        </tr>
                    </s:iterator>
                </s:if>
                <s:else>
                    <tr>
                        <td colspan="9">
                            <div class="empty-state">
                                <i class="bi bi-collection"></i>
                                <p>No se encontraron cobros globales.</p>
                            </div>
                        </td>
                    </tr>
                </s:else>
            </tbody>
        </table>
    </div>

    <%-- Footer: info + pagination --%>
    <s:if test="cobrosGlobales != null && cobrosGlobales.size() > 0">
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
                                   href="<s:url namespace='/admin' action='cobros-globales'>
                                            <s:param name='page' value='%{page - 1}'/>
                                            <s:param name='size' value='%{size}'/>
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
                                       href="<s:url namespace='/admin' action='cobros-globales'>
                                                <s:param name='page' value='%{#i}'/>
                                                <s:param name='size' value='%{size}'/>
                                             </s:url>">
                                        <s:property value="#i + 1"/>
                                    </a>
                                </li>
                            </s:else>
                        </s:iterator>

                        <s:if test="page < totalPages - 1">
                            <li class="page-item">
                                <a class="page-link"
                                   href="<s:url namespace='/admin' action='cobros-globales'>
                                            <s:param name='page' value='%{page + 1}'/>
                                            <s:param name='size' value='%{size}'/>
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
