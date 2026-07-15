<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>document.getElementById('pageTitle').textContent = 'Dashboard';</script>
<script>document.getElementById('nav-dashboard').classList.add('active');</script>

<%-- Banner de bienvenida con logo --%>
<div class="card mb-4 fade-in-item" style="background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%); border:none;">
    <div class="card-body p-4">
        <div class="d-flex align-items-center justify-content-between">
            <div class="text-white">
                <h4 class="fw-bold mb-1">Junta Administradora Acueducto El Hato</h4>
                <p class="mb-0 opacity-75" style="font-size:.9rem;">
                    Panel de Administraci&oacute;n &mdash; <s:property value="mesActual"/> <s:property value="anioActual"/>
                </p>
                <p class="mb-0 opacity-50" style="font-size:.75rem;">NIT: 900003723 &bull; Tibasosa, Boyac&aacute;</p>
            </div>
            <img src="${pageContext.request.contextPath}/static/img/logo.png"
                 alt="Logo Acueducto" style="height:70px; opacity:.9; filter: drop-shadow(0 2px 4px rgba(0,0,0,.3));">
        </div>
    </div>
</div>

<%-- KPI Cards --%>
<div class="row g-3 mb-4">
    <div class="col-6 col-xl-3 fade-in-item">
        <div class="stat-card stat-card-blue">
            <div class="stat-icon"><i class="bi bi-currency-dollar"></i></div>
            <div class="stat-value" style="font-size:1.4rem;">$<s:property value="ingresosMesActual"/></div>
            <div class="stat-label">Ingresos del Mes</div>
        </div>
    </div>
    <div class="col-6 col-xl-3 fade-in-item">
        <div class="stat-card stat-card-green">
            <div class="stat-icon"><i class="bi bi-droplet-fill"></i></div>
            <div class="stat-value" style="font-size:1.4rem;"><s:property value="consumoMesActual"/> m&sup3;</div>
            <div class="stat-label">Consumo del Mes</div>
        </div>
    </div>
    <div class="col-6 col-xl-3 fade-in-item">
        <div class="stat-card stat-card-amber">
            <div class="stat-icon"><i class="bi bi-people-fill"></i></div>
            <div class="stat-value" style="font-size:1.4rem;"><s:property value="totalUsuarios"/></div>
            <div class="stat-label">Usuarios Activos</div>
        </div>
    </div>
    <div class="col-6 col-xl-3 fade-in-item">
        <div class="stat-card stat-card-red">
            <div class="stat-icon"><i class="bi bi-clock-history"></i></div>
            <div class="stat-value" style="font-size:1.4rem;"><s:property value="facturasPendientes"/></div>
            <div class="stat-label">Pendientes de Pago</div>
        </div>
    </div>
</div>

<%-- Resumen del mes + Accesos rápidos --%>
<div class="row g-4 mb-4">
    <%-- Resumen del mes --%>
    <div class="col-lg-5 fade-in-item">
        <div class="card h-100">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-calendar-check me-2 text-primary"></i>Resumen de <s:property value="mesActual"/>
                </h6>
                <canvas id="chartMes" height="180"></canvas>
                <div class="row mt-3 text-center" style="font-size:.8rem;">
                    <div class="col-4">
                        <div class="fw-bold text-success">$<s:property value="ingresoEfectivoMes"/></div>
                        <div class="text-muted">Efectivo</div>
                    </div>
                    <div class="col-4">
                        <div class="fw-bold text-primary">$<s:property value="ingresoBancoMes"/></div>
                        <div class="text-muted">Banco</div>
                    </div>
                    <div class="col-4">
                        <div class="fw-bold text-dark"><s:property value="totalFacturasMes"/></div>
                        <div class="text-muted">Facturas</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Accesos rápidos + Estado --%>
    <div class="col-lg-7 fade-in-item">
        <div class="card h-100">
            <div class="card-body p-4">
                <h6 class="fw-semibold mb-3">
                    <i class="bi bi-lightning-fill me-2 text-warning"></i>Accesos R&aacute;pidos
                </h6>
                <div class="row g-3">
                    <div class="col-sm-6">
                        <a href="<s:url namespace='/admin' action='lectura-formulario'/>"
                           class="d-flex align-items-center gap-3 p-3 rounded-3 text-decoration-none quick-link">
                            <div class="quick-link-icon" style="background:#eff6ff;color:#2563eb;">
                                <i class="bi bi-speedometer"></i>
                            </div>
                            <div>
                                <div class="fw-semibold text-dark" style="font-size:.85rem;">Ingresar Lectura</div>
                                <div class="text-muted" style="font-size:.7rem;">Registrar nueva lectura</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-sm-6">
                        <a href="<s:url namespace='/admin' action='facturas'/>"
                           class="d-flex align-items-center gap-3 p-3 rounded-3 text-decoration-none quick-link">
                            <div class="quick-link-icon" style="background:#dcfce7;color:#16a34a;">
                                <i class="bi bi-receipt"></i>
                            </div>
                            <div>
                                <div class="fw-semibold text-dark" style="font-size:.85rem;">Ver Facturas</div>
                                <div class="text-muted" style="font-size:.7rem;">Gestionar y pagar</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-sm-6">
                        <a href="<s:url namespace='/admin' action='usuarios'/>"
                           class="d-flex align-items-center gap-3 p-3 rounded-3 text-decoration-none quick-link">
                            <div class="quick-link-icon" style="background:#fef3c7;color:#d97706;">
                                <i class="bi bi-people"></i>
                            </div>
                            <div>
                                <div class="fw-semibold text-dark" style="font-size:.85rem;">Usuarios</div>
                                <div class="text-muted" style="font-size:.7rem;"><s:property value="totalUsuarios"/> activos</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-sm-6">
                        <a href="<s:url namespace='/admin' action='informes'/>"
                           class="d-flex align-items-center gap-3 p-3 rounded-3 text-decoration-none quick-link">
                            <div class="quick-link-icon" style="background:#f5f3ff;color:#7c3aed;">
                                <i class="bi bi-bar-chart-line"></i>
                            </div>
                            <div>
                                <div class="fw-semibold text-dark" style="font-size:.85rem;">Informes</div>
                                <div class="text-muted" style="font-size:.7rem;">Reportes y estad&iacute;sticas</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-sm-6">
                        <a href="<s:url namespace='/admin' action='cuotas'/>"
                           class="d-flex align-items-center gap-3 p-3 rounded-3 text-decoration-none quick-link">
                            <div class="quick-link-icon" style="background:#fef2f2;color:#dc2626;">
                                <i class="bi bi-credit-card"></i>
                            </div>
                            <div>
                                <div class="fw-semibold text-dark" style="font-size:.85rem;">Cuotas</div>
                                <div class="text-muted" style="font-size:.7rem;"><s:property value="totalCuotasActivas"/> activas</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-sm-6">
                        <a href="<s:url namespace='/admin' action='usuario-formulario'/>"
                           class="d-flex align-items-center gap-3 p-3 rounded-3 text-decoration-none quick-link">
                            <div class="quick-link-icon" style="background:#ecfdf5;color:#059669;">
                                <i class="bi bi-person-plus"></i>
                            </div>
                            <div>
                                <div class="fw-semibold text-dark" style="font-size:.85rem;">Nuevo Usuario</div>
                                <div class="text-muted" style="font-size:.7rem;">Registrar suscriptor</div>
                            </div>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- Indicadores adicionales --%>
<div class="row g-3 fade-in-item">
    <div class="col-md-4">
        <div class="card">
            <div class="card-body p-3 d-flex align-items-center gap-3">
                <div class="rounded-3 d-flex align-items-center justify-content-center"
                     style="width:44px;height:44px;background:#dcfce7;color:#16a34a;font-size:1.2rem;flex-shrink:0;">
                    <i class="bi bi-check2-all"></i>
                </div>
                <div>
                    <div class="fw-bold" style="font-size:1.1rem;"><s:property value="totalFacturasMes - facturasPendientes"/></div>
                    <div class="text-muted" style="font-size:.75rem;">Facturas Pagadas este Mes</div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card">
            <div class="card-body p-3 d-flex align-items-center gap-3">
                <div class="rounded-3 d-flex align-items-center justify-content-center"
                     style="width:44px;height:44px;background:#fee2e2;color:#dc2626;font-size:1.2rem;flex-shrink:0;">
                    <i class="bi bi-exclamation-triangle"></i>
                </div>
                <div>
                    <div class="fw-bold" style="font-size:1.1rem;"><s:property value="facturasVencidas"/></div>
                    <div class="text-muted" style="font-size:.75rem;">Facturas Vencidas</div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card">
            <div class="card-body p-3 d-flex align-items-center gap-3">
                <div class="rounded-3 d-flex align-items-center justify-content-center"
                     style="width:44px;height:44px;background:#eff6ff;color:#2563eb;font-size:1.2rem;flex-shrink:0;">
                    <i class="bi bi-credit-card"></i>
                </div>
                <div>
                    <div class="fw-bold" style="font-size:1.1rem;"><s:property value="totalCuotasActivas"/></div>
                    <div class="text-muted" style="font-size:.75rem;">Cuotas Activas</div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.7/dist/chart.umd.min.js"></script>
<script>
new Chart(document.getElementById('chartMes'), {
    type: 'doughnut',
    data: {
        labels: ['Efectivo', 'Banco', 'Pendiente'],
        datasets: [{
            data: [
                <s:property value="ingresoEfectivoMes"/>,
                <s:property value="ingresoBancoMes"/>,
                <s:property value="ingresosMesActual - ingresoEfectivoMes - ingresoBancoMes"/>
            ],
            backgroundColor: ['#22c55e', '#3b82f6', '#e2e8f0'],
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: { position: 'bottom', labels: { boxWidth: 10, font: { size: 11 } } }
        },
        cutout: '70%'
    }
});
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
