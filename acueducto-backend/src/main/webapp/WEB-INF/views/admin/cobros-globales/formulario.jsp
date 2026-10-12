<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Nuevo Cobro Global';
    document.getElementById('nav-cobros-globales').classList.add('active');
</script>

<%-- Breadcrumb --%>
<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='cobros-globales'/>" class="text-decoration-none">
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
                 style="width:56px;height:56px;font-size:1.5rem;background:#dcfce7;color:#16a34a;">
                <i class="bi bi-collection"></i>
            </div>
            <h5 class="fw-semibold mb-1">Registrar Nuevo Cobro Global</h5>
            <p class="text-muted mb-0" style="font-size:.8rem;">
                Cuota extraordinaria que deben aportar todos los usuarios
            </p>
        </div>

        <s:form namespace="/admin" action="cobro-global-guardar" method="post" theme="simple">

            <%-- Descripción --%>
            <div class="mb-3">
                <label for="descripcion" class="form-label">
                    Descripci&oacute;n <span class="text-danger">*</span>
                </label>
                <s:textfield name="cobroGlobal.descripcion" value="%{cobroGlobal.descripcion}"
                             id="descripcion" cssClass="form-control"
                             placeholder="Ej: Obras tanque"
                             required="true" minlength="4" maxlength="20"/>
                <div class="form-text">Entre 4 y 20 caracteres. Es el texto que ver&aacute; el usuario en su factura.</div>
            </div>

            <%-- Valor Total y Número de Cuotas --%>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="valorTotal" class="form-label">
                        Valor Total por Usuario ($) <span class="text-danger">*</span>
                    </label>
                    <input type="number" name="cobroGlobal.valorTotal" id="valorTotal"
                           value="<s:property value='cobroGlobal.valorTotal'/>"
                           class="form-control" placeholder="Ej: 100000"
                           required min="1" step="0.01" onchange="calcularValorCuota()"/>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="numeroCuotas" class="form-label">
                        N&uacute;mero de Cuotas <span class="text-danger">*</span>
                    </label>
                    <input type="number" name="cobroGlobal.numeroCuotas" id="numeroCuotas"
                           value="<s:property value='cobroGlobal.numeroCuotas'/>"
                           class="form-control" placeholder="Ej: 2"
                           required min="1" onchange="calcularValorCuota()"/>
                </div>
            </div>

            <%-- Valor Cuota (calculado) --%>
            <div class="mb-3">
                <label class="form-label">Valor por Cuota (calculado)</label>
                <div class="input-group">
                    <span class="input-group-text">$</span>
                    <input type="text" id="valorCuotaDisplay" class="form-control" readonly value="0"/>
                </div>
                <div class="form-text">Se calcula autom&aacute;ticamente: Valor Total / N&uacute;mero de Cuotas.</div>
            </div>

            <div class="alert alert-info" style="font-size:.85rem;">
                <i class="bi bi-info-circle me-1"></i>
                El cobro se aplicar&aacute; a los <strong><s:property value="totalUsuariosActivos"/></strong> usuarios activos
                y llegar&aacute; una cuota en cada una de sus pr&oacute;ximas facturas. Los usuarios que se registren
                despu&eacute;s no lo reciben autom&aacute;ticamente: se agregan desde el detalle del cobro.
            </div>

            <hr class="my-4">

            <div class="d-flex justify-content-between">
                <a href="<s:url namespace='/admin' action='cobros-globales'/>" class="btn btn-light">
                    Cancelar
                </a>
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-plus-lg me-1"></i> Crear Cobro Global
                </button>
            </div>

        </s:form>
    </div>
</div>

<script>
function calcularValorCuota() {
    var total = parseFloat(document.getElementById('valorTotal').value) || 0;
    var numCuotas = parseInt(document.getElementById('numeroCuotas').value) || 0;
    var resultado = numCuotas > 0 ? (total / numCuotas).toFixed(2) : '0.00';
    document.getElementById('valorCuotaDisplay').value = resultado;
}
document.addEventListener('DOMContentLoaded', calcularValorCuota);
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
