<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Subir Documento';
    document.getElementById('nav-documentos').classList.add('active');
</script>

<%-- Breadcrumb --%>
<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='documentos'/>" class="text-decoration-none">
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

<%-- Form Card --%>
<div class="card form-card fade-in-item">
    <div class="card-body p-4">

        <div class="text-center mb-4">
            <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-2"
                 style="width:56px;height:56px;font-size:1.5rem;background:#dcfce7;color:#16a34a;">
                <i class="bi bi-folder2-open"></i>
            </div>
            <h5 class="fw-semibold mb-1">Subir Documento Importante</h5>
            <p class="text-muted mb-0" style="font-size:.8rem;">
                Actas, contratos, resoluciones y dem&aacute;s documentos del acueducto
            </p>
        </div>

        <s:form namespace="/admin" action="documento-guardar" method="post" enctype="multipart/form-data" theme="simple">

            <%-- Título --%>
            <div class="mb-3">
                <label for="titulo" class="form-label">
                    T&iacute;tulo <span class="text-danger">*</span>
                </label>
                <s:textfield name="documento.titulo" value="%{documento.titulo}"
                             id="titulo" cssClass="form-control"
                             placeholder="Ej: Acta asamblea general marzo 2026"
                             required="true" maxlength="100"/>
            </div>

            <%-- Descripción --%>
            <div class="mb-3">
                <label for="descripcion" class="form-label">Descripci&oacute;n</label>
                <textarea name="documento.descripcion" id="descripcion" class="form-control" rows="2" maxlength="300"
                          placeholder="Opcional. Ej: Aprobaci&oacute;n del aporte para las obras del tanque"><s:property value="documento.descripcion"/></textarea>
            </div>

            <%-- Archivo --%>
            <div class="mb-3">
                <label for="archivo" class="form-label">
                    Archivo <span class="text-danger">*</span>
                </label>
                <input type="file" name="archivo" id="archivo" class="form-control" required
                       accept=".pdf,.jpg,.jpeg,.png,.doc,.docx,.xls,.xlsx">
                <div class="form-text">Formatos: PDF, JPG, PNG, Word o Excel. M&aacute;ximo 10MB.</div>
            </div>

            <hr class="my-4">

            <div class="d-flex justify-content-between">
                <a href="<s:url namespace='/admin' action='documentos'/>" class="btn btn-light">
                    Cancelar
                </a>
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-upload me-1"></i> Subir Documento
                </button>
            </div>

        </s:form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
