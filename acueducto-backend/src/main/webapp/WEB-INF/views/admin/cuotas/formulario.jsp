<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent =
        '<s:if test="cuota.id != null">Editar</s:if><s:else>Nueva</s:else> Cuota';
    document.getElementById('nav-cuotas').classList.add('active');
</script>

<%-- Breadcrumb --%>
<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='cuotas'/>" class="text-decoration-none">
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
                 <s:if test='cuota.id != null'>background:#eff6ff;color:#2563eb;</s:if>
                 <s:else>background:#dcfce7;color:#16a34a;</s:else>">
                <s:if test="cuota.id != null"><i class="bi bi-pencil-square"></i></s:if>
                <s:else><i class="bi bi-credit-card"></i></s:else>
            </div>
            <h5 class="fw-semibold mb-1">
                <s:if test="cuota.id != null">Editar Cuota #<s:property value="cuota.id"/></s:if>
                <s:else>Registrar Nueva Cuota</s:else>
            </h5>
            <p class="text-muted mb-0" style="font-size:.8rem;">
                <s:if test="cuota.id != null">Modifique los campos necesarios</s:if>
                <s:else>Complete los datos de la nueva cuota</s:else>
            </p>
        </div>

        <s:form namespace="/admin" action="cuota-guardar" method="post" theme="simple">
            <s:hidden name="cuota.id" value="%{cuota.id}"/>

            <%-- Usuario --%>
            <div class="mb-3">
                <label for="usuarioId" class="form-label">
                    Usuario <span class="text-danger">*</span>
                </label>
                <select name="cuota.usuarioId" id="usuarioId" class="form-control" required>
                    <option value="">-- Seleccione un usuario --</option>
                    <s:iterator value="usuariosActivos">
                        <option value="<s:property value='id'/>"
                            <s:if test="id == cuota.usuarioId">selected</s:if>>
                            <s:property value="nombre"/> <s:property value="apellidos"/>
                        </option>
                    </s:iterator>
                </select>
            </div>

            <%-- Descripción --%>
            <div class="mb-3">
                <label for="descripcion" class="form-label">
                    Descripci&oacute;n <span class="text-danger">*</span>
                </label>
                <s:textfield name="cuota.descripcion" value="%{cuota.descripcion}"
                             id="descripcion" cssClass="form-control"
                             placeholder="Ej: Cuota mensual agua"
                             required="true" minlength="4" maxlength="20"/>
                <div class="form-text">Entre 4 y 20 caracteres.</div>
            </div>

            <%-- Valor Total y Número de Cuotas --%>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="valorTotal" class="form-label">
                        Valor Total ($) <span class="text-danger">*</span>
                    </label>
                    <input type="number" name="cuota.valorTotal" id="valorTotal"
                           value="<s:property value='cuota.valorTotal'/>"
                           class="form-control" placeholder="Ej: 300000"
                           required min="0" step="0.01" onchange="calcularValorCuota()"/>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="numeroCuota" class="form-label">
                        N&uacute;mero de Cuotas <span class="text-danger">*</span>
                    </label>
                    <input type="number" name="cuota.numeroCuota" id="numeroCuota"
                           value="<s:property value='cuota.numeroCuota'/>"
                           class="form-control" placeholder="Ej: 12"
                           required min="1" onchange="calcularValorCuota()"/>
                </div>
            </div>

            <%-- Valor Cuota (calculado) --%>
            <div class="mb-3">
                <label class="form-label">Valor por Cuota (calculado)</label>
                <div class="input-group">
                    <span class="input-group-text">$</span>
                    <input type="text" id="valorCuotaDisplay" class="form-control" readonly
                           value="<s:property value='cuota.valorCuota != null ? cuota.valorCuota : 0'/>"/>
                </div>
                <div class="form-text">Se calcula autom&aacute;ticamente: Valor Total / N&uacute;mero de Cuotas.</div>
            </div>

            <%-- Cuota Actual (solo en edición) --%>
            <s:if test="cuota.id != null">
                <div class="row mb-3">
                    <div class="col-md-4">
                        <label class="form-label">Cuota Actual</label>
                        <p class="mb-0 fw-semibold">
                            <s:property value="cuota.cuotaActual"/> / <s:property value="cuota.numeroCuota"/>
                        </p>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Estado</label>
                        <div>
                            <s:if test="cuota.activo">
                                <span class="badge-status badge-active" style="font-size:.85rem;">
                                    <i class="bi bi-check-circle-fill me-1"></i>Activa
                                </span>
                            </s:if>
                            <s:else>
                                <span class="badge-status badge-inactive" style="font-size:.85rem;">
                                    <i class="bi bi-dash-circle me-1"></i>Inactiva
                                </span>
                            </s:else>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Fecha de Registro</label>
                        <p class="mb-0 text-muted" style="font-size:.875rem;">
                            <i class="bi bi-calendar3 me-1"></i>
                            <s:property value="cuota.fechaInsert"/>
                        </p>
                    </div>
                </div>
            </s:if>
            <s:else>
                <s:hidden name="cuota.cuotaActual" value="0"/>
            </s:else>

            <hr class="my-4">

            <div class="d-flex justify-content-between">
                <a href="<s:url namespace='/admin' action='cuotas'/>" class="btn btn-light">
                    Cancelar
                </a>
                <button type="submit" class="btn btn-primary">
                    <s:if test="cuota.id != null">
                        <i class="bi bi-check-lg me-1"></i> Actualizar
                    </s:if>
                    <s:else>
                        <i class="bi bi-plus-lg me-1"></i> Crear Cuota
                    </s:else>
                </button>
            </div>

        </s:form>
    </div>
</div>

<script>
function calcularValorCuota() {
    var total = parseFloat(document.getElementById('valorTotal').value) || 0;
    var numCuotas = parseInt(document.getElementById('numeroCuota').value) || 0;
    var resultado = numCuotas > 0 ? (total / numCuotas).toFixed(2) : '0.00';
    document.getElementById('valorCuotaDisplay').value = resultado;
}
document.addEventListener('DOMContentLoaded', calcularValorCuota);
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
