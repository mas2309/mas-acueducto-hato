<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = '<s:if test="gasto.id != null">Editar</s:if><s:else>Nuevo</s:else> Gasto';
    document.getElementById('nav-gastos').classList.add('active');
</script>

<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='gastos'/>" class="text-decoration-none">
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
                 style="width:56px;height:56px;font-size:1.5rem;background:#fef2f2;color:#dc2626;">
                <i class="bi bi-graph-down-arrow"></i>
            </div>
            <h5 class="fw-semibold mb-1">
                <s:if test="gasto.id != null">Editar Gasto #<s:property value="gasto.id"/></s:if>
                <s:else>Registrar Nuevo Gasto</s:else>
            </h5>
        </div>

        <s:form namespace="/admin" action="gasto-guardar" method="post" enctype="multipart/form-data" theme="simple">
            <s:hidden name="gasto.id" value="%{gasto.id}"/>

            <div class="mb-3">
                <label for="descripcion" class="form-label">Descripci&oacute;n <span class="text-danger">*</span></label>
                <s:textfield name="gasto.descripcion" value="%{gasto.descripcion}"
                             id="descripcion" cssClass="form-control"
                             placeholder="Ej: Compra de tuber&iacute;a PVC 3 pulgadas"
                             required="true" maxlength="200"/>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="monto" class="form-label">Monto <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text">$</span>
                        <s:textfield name="gasto.monto" value="%{gasto.monto}"
                                     id="monto" cssClass="form-control" type="number"
                                     placeholder="0" required="true" min="1"/>
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="fecha" class="form-label">Fecha <span class="text-danger">*</span></label>
                    <s:textfield name="gasto.fecha" value="%{gasto.fecha}"
                                 id="fecha" cssClass="form-control" type="date" required="true"/>
                </div>
            </div>

            <div class="mb-3">
                <label for="categoria" class="form-label">Categor&iacute;a <span class="text-danger">*</span></label>
                <s:select name="gasto.categoria" id="categoria" cssClass="form-control"
                          list="categorias" listValue="name()"
                          value="%{gasto.categoria}" required="true"/>
                <div class="form-text">
                    MANTENIMIENTO &bull; MATERIALES &bull; SERVICIOS &bull; NOMINA &bull; TRANSPORTE &bull; OTROS
                </div>
            </div>

            <div class="mb-3">
                <label for="responsable" class="form-label">Responsable del gasto</label>
                <s:textfield name="gasto.responsable" value="%{gasto.responsable}"
                             id="responsable" cssClass="form-control"
                             placeholder="Ej: Pedro G&oacute;mez"
                             maxlength="100"/>
                <div class="form-text">Persona que realiz&oacute; el gasto.</div>
            </div>

            <div class="mb-3">
                <label for="soporteFile" class="form-label">Soporte (imagen o PDF)</label>
                <input type="file" name="soporteFile" id="soporteFile" class="form-control"
                       accept=".pdf,.jpg,.jpeg,.png">
                <div class="form-text">Formatos: PDF, JPG, PNG. M&aacute;ximo 10MB.</div>
                <s:if test="gasto.soporteNombre != null && gasto.soporteNombre != ''">
                    <div class="mt-2">
                        <span class="badge bg-light text-dark">
                            <i class="bi bi-paperclip me-1"></i>
                            Archivo actual: <a href="<s:property value='gasto.soporteUrl'/>" target="_blank"><s:property value="gasto.soporteNombre"/></a>
                        </span>
                    </div>
                </s:if>
            </div>

            <s:if test="gasto.id != null">
                <div class="mb-3">
                    <label class="form-label">Estado de Pago</label>
                    <div>
                        <s:if test="gasto.pagado">
                            <span class="badge-status badge-active" style="font-size:.85rem;">
                                <i class="bi bi-check-circle-fill me-1"></i>Pagado el <s:property value="gasto.fechaPago"/>
                            </span>
                        </s:if>
                        <s:else>
                            <span class="badge-status badge-pending" style="font-size:.85rem;">
                                <i class="bi bi-clock me-1"></i>Pendiente de pago
                            </span>
                        </s:else>
                    </div>
                </div>
            </s:if>

            <hr class="my-4">
            <div class="d-flex justify-content-between">
                <a href="<s:url namespace='/admin' action='gastos'/>" class="btn btn-light">Cancelar</a>
                <button type="submit" class="btn btn-primary">
                    <s:if test="gasto.id != null"><i class="bi bi-check-lg me-1"></i> Actualizar</s:if>
                    <s:else><i class="bi bi-plus-lg me-1"></i> Crear Gasto</s:else>
                </button>
            </div>
        </s:form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
