<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = '<s:if test="ingreso.id != null">Editar</s:if><s:else>Nuevo</s:else> Ingreso';
    document.getElementById('nav-ingresos').classList.add('active');
</script>

<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='ingresos'/>" class="text-decoration-none">
        <i class="bi bi-arrow-left me-1"></i> Volver al listado
    </a>
</nav>

<s:if test="mensajeError != null && mensajeError != ''">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-circle-fill me-1"></i><s:property value="mensajeError"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</s:if>

<div class="card form-card fade-in-item">
    <div class="card-body p-4">
        <div class="text-center mb-4">
            <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-2"
                 style="width:56px;height:56px;font-size:1.5rem;background:#dcfce7;color:#16a34a;">
                <i class="bi bi-graph-up-arrow"></i>
            </div>
            <h5 class="fw-semibold mb-1">
                <s:if test="ingreso.id != null">Editar Ingreso #<s:property value="ingreso.id"/></s:if>
                <s:else>Registrar Nuevo Ingreso</s:else>
            </h5>
        </div>

        <s:form namespace="/admin" action="ingreso-guardar" method="post">
            <s:hidden name="ingreso.id" value="%{ingreso.id}"/>

            <div class="mb-3">
                <label for="descripcion" class="form-label">Descripci&oacute;n <span class="text-danger">*</span></label>
                <s:textfield name="ingreso.descripcion" value="%{ingreso.descripcion}"
                             id="descripcion" cssClass="form-control"
                             placeholder="Ej: Pago factura enero - Juan Garc&iacute;a"
                             required="true" maxlength="200"/>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="monto" class="form-label">Monto <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text">$</span>
                        <s:textfield name="ingreso.monto" value="%{ingreso.monto}"
                                     id="monto" cssClass="form-control" type="number"
                                     placeholder="0" required="true" min="1"/>
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="fecha" class="form-label">Fecha <span class="text-danger">*</span></label>
                    <s:textfield name="ingreso.fecha" value="%{ingreso.fecha}"
                                 id="fecha" cssClass="form-control" type="date" required="true"/>
                </div>
            </div>

            <div class="mb-3">
                <label for="categoria" class="form-label">Categor&iacute;a <span class="text-danger">*</span></label>
                <s:select name="ingreso.categoria" id="categoria" cssClass="form-control"
                          list="categorias" listValue="name()"
                          value="%{ingreso.categoria}" required="true"/>
            </div>

            <hr class="my-4">
            <div class="d-flex justify-content-between">
                <a href="<s:url namespace='/admin' action='ingresos'/>" class="btn btn-light">Cancelar</a>
                <button type="submit" class="btn btn-primary">
                    <s:if test="ingreso.id != null"><i class="bi bi-check-lg me-1"></i> Actualizar</s:if>
                    <s:else><i class="bi bi-plus-lg me-1"></i> Crear Ingreso</s:else>
                </button>
            </div>
        </s:form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
