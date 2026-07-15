<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent =
        '<s:if test="usuario.id != null">Editar</s:if><s:else>Nuevo</s:else> Usuario';
    document.getElementById('nav-usuarios').classList.add('active');
</script>

<%-- Breadcrumb --%>
<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='usuarios'/>" class="text-decoration-none">
        <i class="bi bi-arrow-left me-1"></i> Volver al listado
    </a>
</nav>

<%-- Error alerts --%>
<s:if test="mensajeError != null && mensajeError != ''">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-circle-fill"></i>
        <s:property value="mensajeError"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>
<s:if test="hasActionErrors()">
    <div class="alert alert-danger">
        <i class="bi bi-exclamation-circle-fill me-1"></i>
        <s:actionerror/>
    </div>
</s:if>

<%-- Form Card --%>
<div class="card form-card fade-in-item">
    <div class="card-body p-4">

        <div class="text-center mb-4">
            <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-2"
                 style="width:56px;height:56px;font-size:1.5rem;
                 <s:if test='usuario.id != null'>background:#eff6ff;color:#2563eb;</s:if>
                 <s:else>background:#dcfce7;color:#16a34a;</s:else>">
                <s:if test="usuario.id != null"><i class="bi bi-pencil-square"></i></s:if>
                <s:else><i class="bi bi-person-plus"></i></s:else>
            </div>
            <h5 class="fw-semibold mb-1">
                <s:if test="usuario.id != null">Editar Usuario #<s:property value="usuario.id"/></s:if>
                <s:else>Registrar Nuevo Usuario</s:else>
            </h5>
            <p class="text-muted mb-0" style="font-size:.8rem;">
                <s:if test="usuario.id != null">Modifique los campos necesarios</s:if>
                <s:else>Complete los datos del nuevo usuario</s:else>
            </p>
        </div>

        <s:form namespace="/admin" action="usuario-guardar" method="post">
            <s:hidden name="usuario.id" value="%{usuario.id}"/>

            <div class="mb-3">
                <label for="nombre" class="form-label">
                    Nombres <span class="text-danger">*</span>
                </label>
                <s:textfield name="usuario.nombre" value="%{usuario.nombre}"
                             id="nombre" cssClass="form-control"
                             placeholder="Ej: Juan Carlos"
                             required="true" minlength="4" maxlength="20"/>
                <div class="form-text">Entre 4 y 20 caracteres.</div>
            </div>

            <div class="mb-3">
                <label for="apellidos" class="form-label">
                    Apellidos <span class="text-danger">*</span>
                </label>
                <s:textfield name="usuario.apellidos" value="%{usuario.apellidos}"
                             id="apellidos" cssClass="form-control"
                             placeholder="Ej: García López"
                             required="true" minlength="4" maxlength="20"/>
                <div class="form-text">Entre 4 y 20 caracteres.</div>
            </div>

            <s:if test="usuario.id != null">
                <div class="row mb-3">
                    <div class="col-6">
                        <label class="form-label">Estado</label>
                        <div>
                            <s:if test="usuario.activo">
                                <span class="badge-status badge-active" style="font-size:.85rem;">
                                    <i class="bi bi-check-circle-fill me-1"></i>Activo
                                </span>
                            </s:if>
                            <s:else>
                                <span class="badge-status badge-inactive" style="font-size:.85rem;">
                                    <i class="bi bi-dash-circle me-1"></i>Inactivo
                                </span>
                            </s:else>
                        </div>
                    </div>
                    <div class="col-6">
                        <label class="form-label">Fecha de Registro</label>
                        <p class="mb-0 text-muted" style="font-size:.875rem;">
                            <i class="bi bi-calendar3 me-1"></i>
                            <s:property value="usuario.fechaInsert"/>
                        </p>
                    </div>
                </div>
            </s:if>

            <hr class="my-4">

            <div class="d-flex justify-content-between">
                <a href="<s:url namespace='/admin' action='usuarios'/>" class="btn btn-light">
                    Cancelar
                </a>
                <button type="submit" class="btn btn-primary">
                    <s:if test="usuario.id != null">
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
