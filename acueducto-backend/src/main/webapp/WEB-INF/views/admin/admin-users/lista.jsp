<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Usuarios Administrativos';
    document.getElementById('nav-admin-users').classList.add('active');
</script>

<%-- Flash messages --%>
<s:if test="mensajeExito != null && mensajeExito != ''">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-1"></i>
        <s:property value="mensajeExito"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>
<s:if test="mensajeError != null && mensajeError != ''">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-circle-fill me-1"></i>
        <s:property value="mensajeError"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>

<%-- Table --%>
<div class="table-container fade-in-item">
    <div class="table-header">
        <h5><i class="bi bi-shield-lock me-2"></i>Usuarios del Sistema</h5>
        <a href="<s:url namespace='/admin' action='admin-user-formulario'/>" class="btn btn-primary btn-sm">
            <i class="bi bi-plus-lg me-1"></i> Nuevo Usuario
        </a>
    </div>

    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Nombre Completo</th>
                    <th>Email</th>
                    <th>Rol</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <s:if test="adminUsers != null && adminUsers.size() > 0">
                    <s:iterator value="adminUsers" var="u">
                        <tr>
                            <td><s:property value="#u.id"/></td>
                            <td><strong><s:property value="#u.username"/></strong></td>
                            <td><s:property value="#u.nombreCompleto"/></td>
                            <td><s:property value="#u.email"/></td>
                            <td>
                                <s:if test="#u.role.name() == 'ADMIN'">
                                    <span class="badge bg-primary">Admin</span>
                                </s:if>
                                <s:elseif test="#u.role.name() == 'OPERADOR'">
                                    <span class="badge bg-success">Operador</span>
                                </s:elseif>
                                <s:else>
                                    <span class="badge bg-secondary">Consulta</span>
                                </s:else>
                            </td>
                            <td>
                                <s:if test="#u.activo">
                                    <span class="badge-status badge-active">Activo</span>
                                </s:if>
                                <s:else>
                                    <span class="badge-status badge-inactive">Inactivo</span>
                                </s:else>
                            </td>
                            <td>
                                <div class="d-flex gap-1">
                                    <a href="<s:url namespace='/admin' action='admin-user-formulario'><s:param name='id' value='#u.id'/></s:url>"
                                       class="btn-action btn-action-edit" title="Editar">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <button type="button" class="btn-action btn-action-delete"
                                            title="Eliminar"
                                            onclick="confirmDelete('<s:url namespace="/admin" action="admin-user-eliminar"><s:param name="id" value="#u.id"/></s:url>', '<s:property value="#u.username"/>')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </s:iterator>
                </s:if>
                <s:else>
                    <tr>
                        <td colspan="7">
                            <div class="empty-state">
                                <i class="bi bi-shield-exclamation"></i>
                                <p>No hay usuarios administrativos registrados</p>
                            </div>
                        </td>
                    </tr>
                </s:else>
            </tbody>
        </table>
    </div>
</div>

<script>
function confirmDelete(url, username) {
    showConfirmModal(
        'Eliminar Usuario',
        '&iquest;Est&aacute; seguro de eliminar al usuario <strong>' + username + '</strong>?',
        'danger',
        function() { window.location.href = url; }
    );
}
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
