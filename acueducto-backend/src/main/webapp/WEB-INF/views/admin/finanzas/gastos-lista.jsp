<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Gastos';
    document.getElementById('nav-gastos').classList.add('active');
</script>

<s:if test="mensajeExito != null && mensajeExito != ''">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-1"></i><s:property value="mensajeExito"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>
<s:if test="mensajeError != null && mensajeError != ''">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-circle-fill me-1"></i><s:property value="mensajeError"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>

<div class="table-container fade-in-item">
    <div class="table-header">
        <h5><i class="bi bi-graph-down-arrow me-2"></i>Gastos</h5>
        <div class="d-flex gap-2 align-items-center">
            <form action="<s:url namespace='/admin' action='gastos-excel'/>" method="get" class="d-inline-flex align-items-center gap-2">
                <select name="anio" class="form-control form-control-sm" style="width:auto;">
                    <option value="2024">2024</option>
                    <option value="2025">2025</option>
                    <option value="2026" selected>2026</option>
                    <option value="2027">2027</option>
                </select>
                <button type="submit" class="btn btn-success btn-sm">
                    <i class="bi bi-file-earmark-excel me-1"></i>Excel
                </button>
            </form>
            <a href="<s:url namespace='/admin' action='gasto-formulario'/>" class="btn btn-primary btn-sm">
                <i class="bi bi-plus-lg me-1"></i> Nuevo Gasto
            </a>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Descripci&oacute;n</th>
                    <th>Categor&iacute;a</th>
                    <th>Monto</th>
                    <th>Responsable</th>
                    <th>Estado</th>
                    <th>Registrado por</th>
                    <th>Soporte</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <s:if test="gastos != null && gastos.size() > 0">
                    <s:iterator value="gastos" var="g">
                        <tr>
                            <td><s:property value="#g.id"/></td>
                            <td><s:property value="#g.fecha"/></td>
                            <td><s:property value="#g.descripcion"/></td>
                            <td><span class="badge bg-secondary"><s:property value="#g.categoria"/></span></td>
                            <td><strong>$<s:property value="#g.monto"/></strong></td>
                            <td><s:property value="#g.responsable"/></td>
                            <td>
                                <s:if test="#g.pagado">
                                    <span class="badge-status badge-active">Pagado</span>
                                </s:if>
                                <s:else>
                                    <span class="badge-status badge-pending">Pendiente</span>
                                </s:else>
                            </td>
                            <td><s:property value="#g.registradoPorNombre"/></td>
                            <td>
                                <s:if test="#g.soporteUrl != null && #g.soporteUrl != ''">
                                    <a href="<s:property value='#g.soporteUrl' escapeHtml='false'/>" target="_blank"
                                       class="btn-action btn-action-view" title="<s:property value='#g.soporteNombre'/>">
                                        <i class="bi bi-file-earmark-arrow-down"></i>
                                    </a>
                                </s:if>
                                <s:else>
                                    <span class="text-muted" style="font-size:.75rem;">&mdash;</span>
                                </s:else>
                            </td>
                            <td>
                                <div class="d-flex gap-1">
                                    <s:if test="!#g.pagado">
                                        <a href="<s:url namespace='/admin' action='gasto-pagar'><s:param name='id' value='#g.id'/></s:url>"
                                           class="btn-action btn-action-pay" title="Marcar pagado"
                                           onclick="return confirm('&iquest;Marcar como pagado?')">
                                            <i class="bi bi-check-lg"></i>
                                        </a>
                                    </s:if>
                                    <a href="<s:url namespace='/admin' action='gasto-formulario'><s:param name='id' value='#g.id'/></s:url>"
                                       class="btn-action btn-action-edit" title="Editar">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <a href="<s:url namespace='/admin' action='gasto-eliminar'><s:param name='id' value='#g.id'/></s:url>"
                                       class="btn-action btn-action-delete" title="Eliminar"
                                       onclick="return confirm('&iquest;Eliminar este gasto?')">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </s:iterator>
                </s:if>
                <s:else>
                    <tr><td colspan="10"><div class="empty-state"><i class="bi bi-inbox"></i><p>No hay gastos registrados</p></div></td></tr>
                </s:else>
            </tbody>
        </table>
    </div>

    <s:if test="totalPages > 1">
        <div class="table-footer">
            <div class="table-info"><s:property value="totalElements"/> registros</div>
            <nav>
                <ul class="pagination mb-0">
                    <s:iterator begin="0" end="%{totalPages - 1}" var="p">
                        <li class="page-item <s:if test='#p == page'>active</s:if>">
                            <a class="page-link" href="<s:url namespace='/admin' action='gastos'><s:param name='page' value='#p'/></s:url>"><s:property value="#p + 1"/></a>
                        </li>
                    </s:iterator>
                </ul>
            </nav>
        </div>
    </s:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
