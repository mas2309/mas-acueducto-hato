<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Ingresar Lectura';
    document.getElementById('nav-facturas').classList.add('active');
</script>

<%-- Breadcrumb --%>
<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='facturas'/>" class="text-decoration-none">
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
                 style="width:56px;height:56px;font-size:1.5rem;background:#eff6ff;color:#2563eb;">
                <i class="bi bi-speedometer"></i>
            </div>
            <h5 class="fw-semibold mb-1">Ingresar Nueva Lectura</h5>
            <p class="text-muted mb-0" style="font-size:.8rem;">
                Registre la lectura del medidor. El sistema calcular&aacute; autom&aacute;ticamente el consumo y valor de la factura.
            </p>
        </div>

        <s:form namespace="/admin" action="lectura-ingresar" method="post" theme="simple">

            <%-- Usuario --%>
            <div class="mb-3">
                <label for="usuarioId" class="form-label">
                    Usuario <span class="text-danger">*</span>
                </label>
                <select name="lectura.usuarioId" id="usuarioId" class="form-control" required>
                    <option value="">-- Seleccione un usuario --</option>
                    <s:iterator value="usuariosActivos">
                        <option value="<s:property value='id'/>"
                            <s:if test="id == lectura.usuarioId">selected</s:if>>
                            <s:property value="nombre"/> <s:property value="apellidos"/>
                        </option>
                    </s:iterator>
                </select>
            </div>

            <%-- Mes y Año --%>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="mes" class="form-label">
                        Mes <span class="text-danger">*</span>
                    </label>
                    <select name="lectura.mes" id="mes" class="form-control" required>
                        <option value="">-- Seleccione --</option>
                        <option value="Enero" <s:if test="lectura.mes == 'Enero'">selected</s:if>>Enero</option>
                        <option value="Febrero" <s:if test="lectura.mes == 'Febrero'">selected</s:if>>Febrero</option>
                        <option value="Marzo" <s:if test="lectura.mes == 'Marzo'">selected</s:if>>Marzo</option>
                        <option value="Abril" <s:if test="lectura.mes == 'Abril'">selected</s:if>>Abril</option>
                        <option value="Mayo" <s:if test="lectura.mes == 'Mayo'">selected</s:if>>Mayo</option>
                        <option value="Junio" <s:if test="lectura.mes == 'Junio'">selected</s:if>>Junio</option>
                        <option value="Julio" <s:if test="lectura.mes == 'Julio'">selected</s:if>>Julio</option>
                        <option value="Agosto" <s:if test="lectura.mes == 'Agosto'">selected</s:if>>Agosto</option>
                        <option value="Septiembre" <s:if test="lectura.mes == 'Septiembre'">selected</s:if>>Septiembre</option>
                        <option value="Octubre" <s:if test="lectura.mes == 'Octubre'">selected</s:if>>Octubre</option>
                        <option value="Noviembre" <s:if test="lectura.mes == 'Noviembre'">selected</s:if>>Noviembre</option>
                        <option value="Diciembre" <s:if test="lectura.mes == 'Diciembre'">selected</s:if>>Diciembre</option>
                    </select>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="anio" class="form-label">
                        A&ntilde;o <span class="text-danger">*</span>
                    </label>
                    <input type="number" name="lectura.anio" id="anio"
                           value="<s:property value='lectura.anio'/>"
                           class="form-control" placeholder="Ej: 2025"
                           required min="2020" max="2050"/>
                </div>
            </div>

            <%-- Lectura Actual --%>
            <div class="mb-3">
                <label for="lecturaActual" class="form-label">
                    Lectura Actual del Medidor <span class="text-danger">*</span>
                </label>
                <input type="number" name="lectura.lecturaActual" id="lecturaActual"
                       value="<s:property value='lectura.lecturaActual'/>"
                       class="form-control" placeholder="Ej: 1250"
                       required min="0" max="999999"/>
                <div class="form-text">Ingrese el valor que marca el medidor actualmente.</div>
            </div>

            <%-- Otros Cobros (opcional) --%>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="otrosCobros" class="form-label">Otros Cobros ($)</label>
                    <input type="number" name="lectura.otrosCobros" id="otrosCobros"
                           value="<s:property value='lectura.otrosCobros'/>"
                           class="form-control" placeholder="0"
                           min="0" step="0.01"/>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="otrosCobrosDescripcion" class="form-label">Descripci&oacute;n</label>
                    <input type="text" name="lectura.otrosCobrosDescripcion" id="otrosCobrosDescripcion"
                           value="<s:property value='lectura.otrosCobrosDescripcion'/>"
                           class="form-control" placeholder="Ej: Reconexión"/>
                </div>
            </div>

            <%-- Info --%>
            <div class="alert alert-info border-0 py-2 px-3" style="font-size:.8rem; background:#eff6ff;">
                <i class="bi bi-info-circle me-1"></i>
                El sistema calcular&aacute; autom&aacute;ticamente: lectura anterior, consumo, valor consumo, deuda anterior, cuota activa, cargo fijo y valor total.
            </div>

            <hr class="my-4">

            <div class="d-flex justify-content-between">
                <a href="<s:url namespace='/admin' action='facturas'/>" class="btn btn-light">
                    Cancelar
                </a>
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-check-lg me-1"></i> Ingresar Lectura
                </button>
            </div>

        </s:form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
