<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Ingresos';
    document.getElementById('nav-ingresos').classList.add('active');
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
        <h5><i class="bi bi-graph-up-arrow me-2"></i>Ingresos</h5>
        <div class="d-flex gap-2 align-items-center">
            <form action="<s:url namespace='/admin' action='ingresos-excel'/>" method="get" class="d-inline-flex align-items-center gap-2">
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
            <a href="<s:url namespace='/admin' action='ingreso-formulario'/>" class="btn btn-primary btn-sm">
                <i class="bi bi-plus-lg me-1"></i> Nuevo Ingreso
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
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <s:if test="ingresos != null && ingresos.size() > 0">
                    <s:iterator value="ingresos" var="i">
                        <tr>
                            <td><s:property value="#i.id"/></td>
                            <td><s:property value="#i.fecha"/></td>
                            <td><s:property value="#i.descripcion"/></td>
                            <td><span class="badge bg-success"><s:property value="#i.categoria"/></span></td>
                            <td><strong>$<s:property value="#i.monto"/></strong></td>
                            <td>
                                <div class="d-flex gap-1">
                                    <a href="<s:url namespace='/admin' action='ingreso-formulario'><s:param name='id' value='#i.id'/></s:url>"
                                       class="btn-action btn-action-edit" title="Editar">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <a href="<s:url namespace='/admin' action='ingreso-eliminar'><s:param name='id' value='#i.id'/></s:url>"
                                       class="btn-action btn-action-delete" title="Eliminar"
                                       onclick="return confirm('&iquest;Eliminar este ingreso?')">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </s:iterator>
                </s:if>
                <s:else>
                    <tr><td colspan="6"><div class="empty-state"><i class="bi bi-inbox"></i><p>No hay ingresos registrados</p></div></td></tr>
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
                            <a class="page-link" href="<s:url namespace='/admin' action='ingresos'><s:param name='page' value='#p'/></s:url>"><s:property value="#p + 1"/></a>
                        </li>
                    </s:iterator>
                </ul>
            </nav>
        </div>
    </s:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
