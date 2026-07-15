<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent =
        '<s:if test="adminUser.id != null">Editar</s:if><s:else>Nuevo</s:else> Usuario Admin';
    document.getElementById('nav-admin-users').classList.add('active');
</script>

<%-- Breadcrumb --%>
<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='admin-users'/>" class="text-decoration-none">
        <i class="bi bi-arrow-left me-1"></i> Volver al listado
    </a>
</nav>

<%-- Error alerts --%>
<s:if test="mensajeError != null && mensajeError != ''">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-circle-fill me-1"></i>
        <s:property value="mensajeError"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>

<%-- Form Card --%>
<div class="card form-card fade-in-item">
    <div class="card-body p-4">

        <div class="text-center mb-4">
            <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-2"
                 style="width:56px;height:56px;font-size:1.5rem;
                 <s:if test='adminUser.id != null'>background:#eff6ff;color:#2563eb;</s:if>
                 <s:else>background:#dcfce7;color:#16a34a;</s:else>">
                <s:if test="adminUser.id != null"><i class="bi bi-pencil-square"></i></s:if>
                <s:else><i class="bi bi-shield-plus"></i></s:else>
            </div>
            <h5 class="fw-semibold mb-1">
                <s:if test="adminUser.id != null">Editar Usuario #<s:property value="adminUser.id"/></s:if>
                <s:else>Registrar Nuevo Usuario Administrativo</s:else>
            </h5>
            <p class="text-muted mb-0" style="font-size:.8rem;">
                <s:if test="adminUser.id != null">Modifique los campos necesarios</s:if>
                <s:else>Complete los datos del nuevo usuario del sistema</s:else>
            </p>
        </div>

        <s:form namespace="/admin" action="admin-user-guardar" method="post">
            <s:hidden name="adminUser.id" value="%{adminUser.id}"/>

            <div class="mb-3">
                <label for="username" class="form-label">
                    Nombre de Usuario <span class="text-danger">*</span>
                </label>
                <s:textfield name="adminUser.username" value="%{adminUser.username}"
                             id="username" cssClass="form-control"
                             placeholder="Ej: operador1"
                             required="true" minlength="4" maxlength="50"
                             disabled="%{adminUser.id != null}"/>
                <s:if test="adminUser.id != null">
                    <s:hidden name="adminUser.username" value="%{adminUser.username}"/>
                </s:if>
                <div class="form-text">Entre 4 y 50 caracteres. No se puede cambiar despu&eacute;s.</div>
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">
                    Contrase&ntilde;a
                    <s:if test="adminUser.id == null"><span class="text-danger">*</span></s:if>
                </label>
                <input type="password" name="password" id="password" class="form-control"
                       placeholder="<s:if test='adminUser.id != null'>Dejar vac&iacute;o para no cambiar</s:if><s:else>M&iacute;nimo 6 caracteres</s:else>"
                       minlength="6" maxlength="100"
                       <s:if test="adminUser.id == null">required</s:if>>
                <div class="form-text">
                    <s:if test="adminUser.id != null">Solo complete si desea cambiar la contrase&ntilde;a.</s:if>
                    <s:else>M&iacute;nimo 6 caracteres.</s:else>
                </div>
            </div>

            <div class="mb-3">
                <label for="nombreCompleto" class="form-label">
                    Nombre Completo <span class="text-danger">*</span>
                </label>
                <s:textfield name="adminUser.nombreCompleto" value="%{adminUser.nombreCompleto}"
                             id="nombreCompleto" cssClass="form-control"
                             placeholder="Ej: Juan Carlos Garc&iacute;a"
                             required="true" maxlength="100"/>
            </div>

            <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <s:textfield name="adminUser.email" value="%{adminUser.email}"
                             id="email" cssClass="form-control" type="email"
                             placeholder="Ej: usuario@ejemplo.com"
                             maxlength="100"/>
            </div>

            <div class="mb-3">
                <label for="role" class="form-label">
                    Rol <span class="text-danger">*</span>
                </label>
                <s:select name="adminUser.role" id="role" cssClass="form-control"
                          list="roles" listValue="name()"
                          value="%{adminUser.role}"
                          required="true"/>
                <div class="form-text">
                    <strong>ADMIN:</strong> Acceso total &bull;
                    <strong>OPERADOR:</strong> Gesti&oacute;n de usuarios y facturas &bull;
                    <strong>CONSULTA:</strong> Solo lectura
                </div>
            </div>

            <s:if test="adminUser.id != null">
                <div class="mb-3">
                    <label class="form-label">Estado</label>
                    <div>
                        <div class="form-check form-switch">
                            <s:checkbox name="adminUser.activo" id="activo"
                                        cssClass="form-check-input"
                                        value="%{adminUser.activo}"/>
                            <label class="form-check-label" for="activo">Activo</label>
                        </div>
                    </div>
                </div>
            </s:if>

            <hr class="my-4">

            <div class="d-flex justify-content-between">
                <a href="<s:url namespace='/admin' action='admin-users'/>" class="btn btn-light">
                    Cancelar
                </a>
                <button type="submit" class="btn btn-primary">
                    <s:if test="adminUser.id != null">
                        <i class="bi bi-check-lg me-1"></i> Actualizar
                    </s:if>
                    <s:else>
                        <i class="bi bi-plus-lg me-1"></i> Crear Usuario
                    </s:else>
                </button>
            </div>

        </s:form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
