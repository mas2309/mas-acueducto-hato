<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Nota en Recibos';
    document.getElementById('nav-nota-recibo').classList.add('active');
</script>

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

<%-- Form Card --%>
<div class="card form-card fade-in-item">
    <div class="card-body p-4">

        <div class="text-center mb-4">
            <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-2"
                 style="width:56px;height:56px;font-size:1.5rem;background:#eff6ff;color:#2563eb;">
                <i class="bi bi-megaphone"></i>
            </div>
            <h5 class="fw-semibold mb-1">Nota Informativa en Recibos</h5>
            <p class="text-muted mb-0" style="font-size:.8rem;">
                Aviso general que se imprime en todos los recibos mientras est&eacute; activo
            </p>
        </div>

        <s:form namespace="/admin" action="nota-recibo-guardar" method="post" theme="simple">

            <%-- Texto --%>
            <div class="mb-3">
                <label for="texto" class="form-label">Texto de la nota</label>
                <textarea name="nota.texto" id="texto" class="form-control" rows="3" maxlength="150"
                          placeholder="Ej: El 15 de marzo se suspende el servicio por mantenimiento del tanque."
                          oninput="contarCaracteres()"><s:property value="nota.texto"/></textarea>
                <div class="form-text d-flex justify-content-between">
                    <span>Se imprime como un solo p&aacute;rrafo, debajo del total.</span>
                    <span><span id="contador">0</span>/150</span>
                </div>
            </div>

            <%-- Activa --%>
            <div class="form-check form-switch mb-3">
                <input type="checkbox" class="form-check-input" name="nota.activa" id="activa" value="true"
                       <s:if test="nota.activa">checked</s:if>/>
                <label class="form-check-label" for="activa">Imprimir la nota en los recibos</label>
            </div>

            <s:if test="nota.fechaActualizacion != null">
                <p class="text-muted" style="font-size:.8rem;">
                    <i class="bi bi-calendar3 me-1"></i>
                    &Uacute;ltima modificaci&oacute;n: <s:property value="nota.fechaActualizacion"/>
                </p>
            </s:if>

            <div class="alert alert-info" style="font-size:.85rem;">
                <i class="bi bi-info-circle me-1"></i>
                La nota aplica a los recibos que se descarguen o impriman desde ahora, tambi&eacute;n los de
                per&iacute;odos anteriores. Los tel&eacute;fonos la reciben en su siguiente sincronizaci&oacute;n.
            </div>

            <hr class="my-4">

            <div class="d-flex justify-content-end">
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-check-lg me-1"></i> Guardar Nota
                </button>
            </div>

        </s:form>
    </div>
</div>

<script>
function contarCaracteres() {
    document.getElementById('contador').textContent = document.getElementById('texto').value.length;
}
document.addEventListener('DOMContentLoaded', contarCaracteres);
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
