<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>document.getElementById('pageTitle').textContent = 'Informes';</script>
<script>document.getElementById('nav-informes').classList.add('active');</script>

<%-- Filtros + Exportar --%>
<div class="card mb-4 fade-in-item">
    <div class="card-body py-3 px-4">
        <div class="d-flex justify-content-between align-items-end flex-wrap gap-2">
            <form action="<s:url namespace='/admin' action='informes'/>" method="get" class="d-flex gap-2 align-items-end flex-wrap">
                <div>
                    <label class="form-label mb-1" style="font-size:.75rem;font-weight:600;">A&Ntilde;O</label>
                    <select name="anio" class="form-control form-control-sm" style="width:100px;">
                        <s:iterator value="aniosDisponibles">
                            <option value="<s:property/>" <s:if test="top == anio">selected</s:if>><s:property/></option>
                        </s:iterator>
                    </select>
                </div>
                <div>
                    <label class="form-label mb-1" style="font-size:.75rem;font-weight:600;">MES (opcional)</label>
                    <select name="mes" class="form-control form-control-sm" style="width:140px;">
                        <option value="">-- Todo el a&ntilde;o --</option>
                        <option value="Enero" <s:if test="mes == 'Enero'">selected</s:if>>Enero</option>
                        <option value="Febrero" <s:if test="mes == 'Febrero'">selected</s:if>>Febrero</option>
                        <option value="Marzo" <s:if test="mes == 'Marzo'">selected</s:if>>Marzo</option>
                        <option value="Abril" <s:if test="mes == 'Abril'">selected</s:if>>Abril</option>
                        <option value="Mayo" <s:if test="mes == 'Mayo'">selected</s:if>>Mayo</option>
                        <option value="Junio" <s:if test="mes == 'Junio'">selected</s:if>>Junio</option>
                        <option value="Julio" <s:if test="mes == 'Julio'">selected</s:if>>Julio</option>
                        <option value="Agosto" <s:if test="mes == 'Agosto'">selected</s:if>>Agosto</option>
                        <option value="Septiembre" <s:if test="mes == 'Septiembre'">selected</s:if>>Septiembre</option>
                        <option value="Octubre" <s:if test="mes == 'Octubre'">selected</s:if>>Octubre</option>
                        <option value="Noviembre" <s:if test="mes == 'Noviembre'">selected</s:if>>Noviembre</option>
                        <option value="Diciembre" <s:if test="mes == 'Diciembre'">selected</s:if>>Diciembre</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary btn-sm">
                    <i class="bi bi-funnel me-1"></i> Generar
                </button>
            </form>
            <a href="<s:url namespace='/admin' action='informes-excel'>
                        <s:param name='anio' value='anio'/>
                        <s:param name='mes' value='mes'/>
                     </s:url>"
               class="btn btn-success btn-sm">
                <i class="bi bi-file-earmark-excel me-1"></i> Exportar Excel
            </a>
        </div>
    </div>
</div>

<%-- KPI Cards --%>
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-xl-3 fade-in-item">
        <div class="stat-card stat-card-blue">
            <div class="stat-icon"><i class="bi bi-currency-dollar"></i></div>
            <div class="stat-value" style="font-size:1.5rem;">$<s:property value="informe.totalIngresos"/></div>
            <div class="stat-label">Ingresos Totales</div>
        </div>
    </div>
    <div class="col-sm-6 col-xl-3 fade-in-item">
        <div class="stat-card stat-card-green">
            <div class="stat-icon"><i class="bi bi-droplet"></i></div>
            <div class="stat-value" style="font-size:1.5rem;"><s:property value="informe.totalMetrosCubicos"/> m&sup3;</div>
            <div class="stat-label">Consumo Total</div>
        </div>
    </div>
    <div class="col-sm-6 col-xl-3 fade-in-item">
        <div class="stat-card stat-card-amber">
            <div class="stat-icon"><i class="bi bi-receipt"></i></div>
            <div class="stat-value" style="font-size:1.5rem;"><s:property value="informe.totalFacturas"/></div>
            <div class="stat-label">Facturas Generadas</div>
        </div>
    </div>
    <div class="col-sm-6 col-xl-3 fade-in-item">
        <div class="stat-card stat-card-red">
            <div class="stat-icon"><i class="bi bi-exclamation-triangle"></i></div>
            <div class="stat-value" style="font-size:1.5rem;"><s:property value="informe.facturasVencidas"/></div>
            <div class="stat-label">Facturas Vencidas</div>
        </div>
    </div>
</div>

<%-- Método de Pago + Discriminación Ingresos --%>
<div class="row g-4 mb-4">
    <%-- Método de pago --%>
    <div class="col-lg-4 fade-in-item">
        <div class="card h-100">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-wallet2 me-2 text-primary"></i>Ingresos por M&eacute;todo de Pago
                </h6>
                <canvas id="chartMetodoPago" height="200"></canvas>
                <table class="table table-sm mt-3 mb-0">
                    <tbody>
                        <tr>
                            <td><span class="d-inline-block rounded-circle me-2" style="width:10px;height:10px;background:#22c55e;"></span>Efectivo</td>
                            <td class="text-end fw-semibold">$<s:property value="informe.ingresoEfectivo"/></td>
                            <td class="text-end text-muted"><s:property value="informe.facturasPagadasEfectivo"/> fact.</td>
                        </tr>
                        <tr>
                            <td><span class="d-inline-block rounded-circle me-2" style="width:10px;height:10px;background:#3b82f6;"></span>Banco</td>
                            <td class="text-end fw-semibold">$<s:property value="informe.ingresoBanco"/></td>
                            <td class="text-end text-muted"><s:property value="informe.facturasPagadasBanco"/> fact.</td>
                        </tr>
                        <tr>
                            <td><span class="d-inline-block rounded-circle me-2" style="width:10px;height:10px;background:#94a3b8;"></span>Pendiente</td>
                            <td class="text-end fw-semibold">$<s:property value="informe.ingresoPendiente"/></td>
                            <td class="text-end text-muted"><s:property value="informe.facturasPendientes + informe.facturasVencidas"/> fact.</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <%-- Discriminación por concepto --%>
    <div class="col-lg-4 fade-in-item">
        <div class="card h-100">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-pie-chart me-2 text-primary"></i>Discriminaci&oacute;n de Ingresos
                </h6>
                <canvas id="chartIngresos" height="200"></canvas>
            </div>
        </div>
    </div>

    <%-- Tabla de conceptos --%>
    <div class="col-lg-4 fade-in-item">
        <div class="card h-100">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-list-check me-2 text-primary"></i>Detalle por Concepto
                </h6>
                <table class="table table-sm mb-0">
                    <thead>
                        <tr>
                            <th style="font-size:.7rem;">CONCEPTO</th>
                            <th class="text-end" style="font-size:.7rem;">VALOR</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr><td>Consumo</td><td class="text-end fw-semibold">$<s:property value="informe.totalConsumo"/></td></tr>
                        <tr><td>Cargo Fijo</td><td class="text-end fw-semibold">$<s:property value="informe.totalCargoFijo"/></td></tr>
                        <tr><td>Cuotas</td><td class="text-end fw-semibold">$<s:property value="informe.totalCuotas"/></td></tr>
                        <tr><td>Otros Cobros</td><td class="text-end fw-semibold">$<s:property value="informe.totalOtrosCobros"/></td></tr>
                        <tr><td>No Pago</td><td class="text-end fw-semibold">$<s:property value="informe.totalNoPago"/></td></tr>
                        <tr><td>Deuda Anterior</td><td class="text-end fw-semibold">$<s:property value="informe.totalDeudaAnterior"/></td></tr>
                        <tr class="border-top"><td class="fw-bold">TOTAL</td><td class="text-end fw-bold text-primary">$<s:property value="informe.totalIngresos"/></td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%-- Gráficos mensuales --%>
<s:if test="informe.meses != null && informe.meses.size() > 1">
<div class="row g-4 mb-4">
    <div class="col-lg-6 fade-in-item">
        <div class="card h-100">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-graph-up me-2 text-primary"></i>Ingresos Mensuales (Efectivo vs Banco)
                </h6>
                <canvas id="chartIngresosMensuales" height="200"></canvas>
            </div>
        </div>
    </div>
    <div class="col-lg-6 fade-in-item">
        <div class="card h-100">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-droplet-half me-2 text-primary"></i>Consumo Mensual (m&sup3;)
                </h6>
                <canvas id="chartConsumoMensual" height="200"></canvas>
            </div>
        </div>
    </div>
</div>
</s:if>

<%-- Tabla detalle mensual --%>
<s:if test="informe.detalleMensual != null && informe.detalleMensual.size() > 0">
<div class="card fade-in-item">
    <div class="card-body p-4">
        <h6 class="fw-semibold mb-3">
            <i class="bi bi-table me-2 text-primary"></i>Detalle Mensual
        </h6>
        <div class="table-responsive">
            <table class="table table-sm table-hover">
                <thead>
                    <tr>
                        <th style="font-size:.7rem;">MES</th>
                        <th class="text-center" style="font-size:.7rem;">FACT.</th>
                        <th class="text-end" style="font-size:.7rem;">CONSUMO</th>
                        <th class="text-end" style="font-size:.7rem;">C. FIJO</th>
                        <th class="text-end" style="font-size:.7rem;">CUOTAS</th>
                        <th class="text-end" style="font-size:.7rem;">OTROS</th>
                        <th class="text-end" style="font-size:.7rem;">NO PAGO</th>
                        <th class="text-end" style="font-size:.7rem;">TOTAL</th>
                        <th class="text-end" style="font-size:.7rem;">EFECTIVO</th>
                        <th class="text-end" style="font-size:.7rem;">BANCO</th>
                        <th class="text-center" style="font-size:.7rem;">m&sup3;</th>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="informe.detalleMensual">
                        <tr>
                            <td class="fw-semibold"><s:property value="mes"/></td>
                            <td class="text-center"><s:property value="cantidadFacturas"/></td>
                            <td class="text-end">$<s:property value="ingresoConsumo"/></td>
                            <td class="text-end">$<s:property value="ingresoCargoFijo"/></td>
                            <td class="text-end">$<s:property value="ingresoCuotas"/></td>
                            <td class="text-end">$<s:property value="ingresoOtrosCobros"/></td>
                            <td class="text-end">$<s:property value="ingresoNoPago"/></td>
                            <td class="text-end fw-bold">$<s:property value="ingresoTotal"/></td>
                            <td class="text-end text-success">$<s:property value="ingresoEfectivo"/></td>
                            <td class="text-end text-primary">$<s:property value="ingresoBanco"/></td>
                            <td class="text-center"><s:property value="consumoM3"/></td>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
        </div>
    </div>
</div>
</s:if>

<%-- Chart.js --%>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.7/dist/chart.umd.min.js"></script>
<script>
// Método de pago (Doughnut)
new Chart(document.getElementById('chartMetodoPago'), {
    type: 'doughnut',
    data: {
        labels: ['Efectivo', 'Banco', 'Pendiente'],
        datasets: [{
            data: [
                <s:property value="informe.ingresoEfectivo"/>,
                <s:property value="informe.ingresoBanco"/>,
                <s:property value="informe.ingresoPendiente"/>
            ],
            backgroundColor: ['#22c55e','#3b82f6','#94a3b8'],
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        plugins: { legend: { display: false } },
        cutout: '65%'
    }
});

// Discriminación de ingresos (Doughnut)
new Chart(document.getElementById('chartIngresos'), {
    type: 'doughnut',
    data: {
        labels: ['Consumo', 'Cargo Fijo', 'Cuotas', 'Otros', 'No Pago', 'Deuda Ant.'],
        datasets: [{
            data: [
                <s:property value="informe.totalConsumo"/>,
                <s:property value="informe.totalCargoFijo"/>,
                <s:property value="informe.totalCuotas"/>,
                <s:property value="informe.totalOtrosCobros"/>,
                <s:property value="informe.totalNoPago"/>,
                <s:property value="informe.totalDeudaAnterior"/>
            ],
            backgroundColor: ['#3b82f6','#22c55e','#f59e0b','#8b5cf6','#ef4444','#64748b'],
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        plugins: { legend: { position: 'bottom', labels: { boxWidth: 10, font: { size: 10 } } } },
        cutout: '60%'
    }
});

<s:if test="informe.meses != null && informe.meses.size() > 1">
// Ingresos mensuales stacked (Efectivo vs Banco)
new Chart(document.getElementById('chartIngresosMensuales'), {
    type: 'bar',
    data: {
        labels: [<s:iterator value="informe.meses" status="st">'<s:property/>'<s:if test="!#st.last">,</s:if></s:iterator>],
        datasets: [{
            label: 'Efectivo',
            data: [<s:iterator value="informe.ingresosEfectivoMensual" status="st"><s:property/><s:if test="!#st.last">,</s:if></s:iterator>],
            backgroundColor: '#22c55e',
            borderRadius: 2
        },{
            label: 'Banco',
            data: [<s:iterator value="informe.ingresosBancoMensual" status="st"><s:property/><s:if test="!#st.last">,</s:if></s:iterator>],
            backgroundColor: '#3b82f6',
            borderRadius: 2
        }]
    },
    options: {
        responsive: true,
        plugins: { legend: { position: 'top', labels: { boxWidth: 12, font: { size: 11 } } } },
        scales: { x: { stacked: true }, y: { stacked: true, beginAtZero: true } }
    }
});

// Consumo mensual (Line)
new Chart(document.getElementById('chartConsumoMensual'), {
    type: 'line',
    data: {
        labels: [<s:iterator value="informe.meses" status="st">'<s:property/>'<s:if test="!#st.last">,</s:if></s:iterator>],
        datasets: [{
            label: 'Consumo (m\u00b3)',
            data: [<s:iterator value="informe.consumoMensualM3" status="st"><s:property/><s:if test="!#st.last">,</s:if></s:iterator>],
            borderColor: '#22c55e',
            backgroundColor: 'rgba(34,197,94,0.1)',
            fill: true,
            tension: 0.3,
            pointRadius: 4
        }]
    },
    options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
    }
});
</s:if>
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
