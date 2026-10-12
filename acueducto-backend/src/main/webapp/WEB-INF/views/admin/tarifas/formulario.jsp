<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Tarifas';
    document.getElementById('nav-tarifas').classList.add('active');
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
                <i class="bi bi-sliders"></i>
            </div>
            <h5 class="fw-semibold mb-1">Tarifas de Facturaci&oacute;n</h5>
            <p class="text-muted mb-0" style="font-size:.8rem;">
                Cargo fijo, recargo por no pago y valor del m&sup3; por rango de consumo
            </p>
        </div>

        <s:form namespace="/admin" action="tarifas-guardar" method="post" theme="simple">

            <%-- Cargos --%>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="cargoFijo" class="form-label">
                        Cargo Fijo ($) <span class="text-danger">*</span>
                    </label>
                    <input type="number" name="valores.cargoFijo" id="cargoFijo"
                           value="<s:property value='valores.cargoFijo'/>"
                           class="form-control" required min="0" step="0.01"/>
                    <div class="form-text">Se cobra en todas las facturas.</div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="noPago" class="form-label">
                        Recargo por No Pago ($) <span class="text-danger">*</span>
                    </label>
                    <input type="number" name="valores.noPago" id="noPago"
                           value="<s:property value='valores.noPago'/>"
                           class="form-control" required min="0" step="0.01"/>
                    <div class="form-text">Se cobra si la factura anterior no fue pagada.</div>
                </div>
            </div>

            <hr class="my-3">

            <%-- Rangos de consumo --%>
            <h6 class="fw-semibold mb-1">Rangos de Consumo</h6>
            <p class="text-muted" style="font-size:.8rem;">
                Cada m&sup3; se cobra al valor del rango en el que cae: los primeros al valor del rango 1,
                los siguientes al del rango 2, y as&iacute; sucesivamente.
            </p>

            <div class="table-responsive">
                <table class="table table-sm align-middle">
                    <thead>
                        <tr>
                            <th>Rango</th>
                            <th>Consumo</th>
                            <th>Hasta (m&sup3;) <span class="text-danger">*</span></th>
                            <th>Valor por m&sup3; ($) <span class="text-danger">*</span></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="fw-semibold">1</td>
                            <td class="text-muted text-nowrap" id="descripcionRango1"></td>
                            <td>
                                <input type="number" name="valores.rangoUno" id="rangoUno"
                                       value="<s:property value='valores.rangoUno'/>"
                                       class="form-control" required min="1" step="1" oninput="describirRangos()"/>
                            </td>
                            <td>
                                <input type="number" name="valores.valorUno"
                                       value="<s:property value='valores.valorUno'/>"
                                       class="form-control" required min="0" step="1"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="fw-semibold">2</td>
                            <td class="text-muted text-nowrap" id="descripcionRango2"></td>
                            <td>
                                <input type="number" name="valores.rangoDos" id="rangoDos"
                                       value="<s:property value='valores.rangoDos'/>"
                                       class="form-control" required min="2" step="1" oninput="describirRangos()"/>
                            </td>
                            <td>
                                <input type="number" name="valores.valorDos"
                                       value="<s:property value='valores.valorDos'/>"
                                       class="form-control" required min="0" step="1"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="fw-semibold">3</td>
                            <td class="text-muted text-nowrap" id="descripcionRango3"></td>
                            <td>
                                <input type="number" name="valores.rangoTres" id="rangoTres"
                                       value="<s:property value='valores.rangoTres'/>"
                                       class="form-control" required min="3" step="1" oninput="describirRangos()"/>
                            </td>
                            <td>
                                <input type="number" name="valores.valorTres"
                                       value="<s:property value='valores.valorTres'/>"
                                       class="form-control" required min="0" step="1"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="fw-semibold">4</td>
                            <td class="text-muted text-nowrap" id="descripcionRango4"></td>
                            <td class="text-muted" style="font-size:.85rem;">Sin l&iacute;mite</td>
                            <td>
                                <input type="number" name="valores.valorCuatro"
                                       value="<s:property value='valores.valorCuatro'/>"
                                       class="form-control" required min="0" step="1"/>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div id="errorRangos" class="alert alert-danger d-none" style="font-size:.85rem;">
                <i class="bi bi-exclamation-circle-fill me-1"></i>
                Los l&iacute;mites de los rangos deben ir de menor a mayor.
            </div>

            <div class="alert alert-info" style="font-size:.85rem;">
                <i class="bi bi-info-circle me-1"></i>
                Los cambios aplican a las facturas que se generen o editen desde ahora.
                Las facturas ya generadas conservan sus valores.
            </div>

            <hr class="my-4">

            <div class="d-flex justify-content-end">
                <button type="submit" class="btn btn-primary" id="guardarTarifas">
                    <i class="bi bi-check-lg me-1"></i> Guardar Tarifas
                </button>
            </div>

        </s:form>
    </div>
</div>

<script>
function describirRangos() {
    var uno = parseInt(document.getElementById('rangoUno').value) || 0;
    var dos = parseInt(document.getElementById('rangoDos').value) || 0;
    var tres = parseInt(document.getElementById('rangoTres').value) || 0;
    var enOrden = uno > 0 && dos > uno && tres > dos;

    document.getElementById('descripcionRango1').textContent = enOrden ? '1 a ' + uno + ' m³' : '-';
    document.getElementById('descripcionRango2').textContent = enOrden ? (uno + 1) + ' a ' + dos + ' m³' : '-';
    document.getElementById('descripcionRango3').textContent = enOrden ? (dos + 1) + ' a ' + tres + ' m³' : '-';
    document.getElementById('descripcionRango4').textContent = enOrden ? 'Más de ' + tres + ' m³' : '-';

    document.getElementById('errorRangos').classList.toggle('d-none', enOrden);
    document.getElementById('guardarTarifas').disabled = !enOrden;
}
document.addEventListener('DOMContentLoaded', describirRangos);
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
