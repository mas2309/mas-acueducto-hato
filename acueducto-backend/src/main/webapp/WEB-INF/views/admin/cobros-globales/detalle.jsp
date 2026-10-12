<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>
    document.getElementById('pageTitle').textContent = 'Cobro Global #<s:property value="cobroGlobal.id"/>';
    document.getElementById('nav-cobros-globales').classList.add('active');
</script>

<%-- Breadcrumb --%>
<nav class="mb-3" style="font-size:.8rem;">
    <a href="<s:url namespace='/admin' action='cobros-globales'/>" class="text-decoration-none">
        <i class="bi bi-arrow-left me-1"></i> Volver al listado
    </a>
</nav>

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

<%-- Resumen --%>
<div class="card fade-in-item mb-4">
    <div class="card-body p-4">
        <div class="d-flex justify-content-between align-items-start mb-3">
            <div>
                <h5 class="fw-semibold mb-1">
                    <i class="bi bi-collection me-2 text-primary"></i><s:property value="cobroGlobal.descripcion"/>
                </h5>
                <p class="text-muted mb-0" style="font-size:.85rem;">
                    <i class="bi bi-calendar3 me-1"></i>
                    Creado el <s:property value="cobroGlobal.fechaCreacion"/>
                    &mdash; $<s:property value="cobroGlobal.valorTotal"/> por usuario en
                    <s:property value="cobroGlobal.numeroCuotas"/> cuota(s) de $<s:property value="cobroGlobal.valorCuota"/>
                </p>
            </div>
            <div class="d-flex align-items-center gap-2">
                <s:if test="!cobroGlobal.activo">
                    <span class="badge-status badge-inactive" style="font-size:.85rem;">
                        <i class="bi bi-slash-circle me-1"></i>Cancelado
                    </span>
                </s:if>
                <s:elseif test="cobroGlobal.usuariosEnCobro > 0">
                    <span class="badge-status badge-active" style="font-size:.85rem;">
                        <i class="bi bi-check-circle-fill me-1"></i>En cobro
                    </span>
                    <a href="<s:url namespace='/admin' action='cobro-global-cancelar'>
                                <s:param name='id' value='cobroGlobal.id'/>
                             </s:url>"
                       class="btn btn-outline-warning btn-sm"
                       data-confirm="cancel-charge"
                       data-name="<s:property value='cobroGlobal.descripcion'/>">
                        <i class="bi bi-slash-circle me-1"></i> Cancelar cobro
                    </a>
                </s:elseif>
                <s:else>
                    <span class="badge-status badge-inactive" style="font-size:.85rem;">
                        <i class="bi bi-check2-all me-1"></i>Finalizado
                    </span>
                </s:else>
            </div>
        </div>

        <hr>

        <div class="row g-3">
            <div class="col-sm-6 col-lg-3">
                <label class="form-label text-muted" style="font-size:.75rem;">TOTAL DEL COBRO</label>
                <p class="fw-semibold mb-0">$<s:property value="cobroGlobal.valorEsperado"/></p>
                <small class="text-muted"><s:property value="cobroGlobal.totalUsuarios"/> usuarios</small>
            </div>
            <div class="col-sm-6 col-lg-3">
                <label class="form-label text-muted" style="font-size:.75rem;">FACTURADO</label>
                <p class="fw-semibold mb-0 text-primary">$<s:property value="cobroGlobal.valorFacturado"/></p>
                <small class="text-muted"><s:property value="cobroGlobal.usuariosCompletados"/> usuarios con todas las cuotas facturadas</small>
            </div>
            <div class="col-sm-6 col-lg-3">
                <label class="form-label text-muted" style="font-size:.75rem;">PAGO ANTICIPADO</label>
                <p class="fw-semibold mb-0 text-success">$<s:property value="cobroGlobal.valorAnticipado"/></p>
                <small class="text-muted"><s:property value="cobroGlobal.usuariosPagoAnticipado"/> usuarios</small>
            </div>
            <div class="col-sm-6 col-lg-3">
                <label class="form-label text-muted" style="font-size:.75rem;">PENDIENTE POR FACTURAR</label>
                <p class="fw-semibold mb-0 text-warning">$<s:property value="cobroGlobal.valorPendiente"/></p>
                <small class="text-muted"><s:property value="cobroGlobal.usuariosEnCobro"/> usuarios en cobro</small>
            </div>
        </div>
    </div>
</div>

<%-- Usuarios del cobro --%>
<div class="table-container fade-in-item">

    <%-- Header --%>
    <div class="table-header">
        <div class="d-flex align-items-center gap-3 flex-grow-1 flex-wrap">
            <h5><i class="bi bi-people me-2"></i>Usuarios del Cobro</h5>
        </div>
        <s:if test="cobroGlobal.activo && usuariosSinCobro != null && usuariosSinCobro.size() > 0">
            <form action="<s:url namespace='/admin' action='cobro-global-agregar-usuario'/>" method="post" class="d-flex gap-2">
                <input type="hidden" name="id" value="<s:property value='cobroGlobal.id'/>"/>
                <select name="usuarioId" class="form-control form-control-sm" style="width:240px;" required>
                    <option value="">-- Usuario sin este cobro --</option>
                    <s:iterator value="usuariosSinCobro">
                        <option value="<s:property value='id'/>">
                            <s:property value="nombre"/> <s:property value="apellidos"/>
                        </option>
                    </s:iterator>
                </select>
                <button type="submit" class="btn btn-primary btn-sm text-nowrap">
                    <i class="bi bi-person-plus me-1"></i> Aplicar cobro
                </button>
            </form>
        </s:if>
    </div>

    <%-- Table --%>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Usuario</th>
                    <th>Valor Cuota</th>
                    <th>Cuotas Facturadas</th>
                    <th>Estado</th>
                    <th class="text-center">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <s:if test="cuotas != null && cuotas.size() > 0">
                    <s:iterator value="cuotas">
                        <tr>
                            <td>
                                <i class="bi bi-person me-1 text-muted"></i><s:property value="usuarioNombre"/>
                            </td>
                            <td>$<s:property value="valorCuota"/></td>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <div class="progress flex-grow-1" style="height:6px; min-width:60px; max-width:160px;">
                                        <div class="progress-bar bg-primary" role="progressbar"
                                             style="width: <s:property value='numeroCuota > 0 ? (cuotaActual * 100 / numeroCuota) : 0'/>%">
                                        </div>
                                    </div>
                                    <small class="text-muted text-nowrap">
                                        <s:property value="cuotaActual"/>/<s:property value="numeroCuota"/>
                                    </small>
                                </div>
                            </td>
                            <td>
                                <s:if test="pagoAnticipado">
                                    <span class="badge-status badge-active">
                                        <i class="bi bi-cash-coin me-1"></i>Pago anticipado
                                    </span>
                                </s:if>
                                <s:elseif test="activo">
                                    <span class="badge-status badge-pending">
                                        <i class="bi bi-clock me-1"></i>En cobro
                                    </span>
                                </s:elseif>
                                <s:elseif test="cuotaActual >= numeroCuota">
                                    <span class="badge-status badge-active">
                                        <i class="bi bi-check2-all me-1"></i>Facturado
                                    </span>
                                </s:elseif>
                                <s:else>
                                    <span class="badge-status badge-inactive">
                                        <i class="bi bi-dash-circle me-1"></i>Desactivada
                                    </span>
                                </s:else>
                            </td>
                            <td class="text-center">
                                <div class="d-flex gap-1 justify-content-center">
                                    <s:if test="pagoAnticipado">
                                        <s:if test="cobroGlobal.activo">
                                            <a href="<s:url namespace='/admin' action='cobro-global-revertir-anticipado'>
                                                        <s:param name='id' value='cobroGlobal.id'/>
                                                        <s:param name='cuotaId' value='id'/>
                                                     </s:url>"
                                               class="btn-action btn-action-deactivate"
                                               title="Revertir pago anticipado"
                                               data-confirm="revert-prepay"
                                               data-name="<s:property value='usuarioNombre'/>">
                                                <i class="bi bi-arrow-counterclockwise"></i>
                                            </a>
                                        </s:if>
                                    </s:if>
                                    <s:elseif test="activo">
                                        <a href="<s:url namespace='/admin' action='cobro-global-pago-anticipado'>
                                                    <s:param name='id' value='cobroGlobal.id'/>
                                                    <s:param name='cuotaId' value='id'/>
                                                 </s:url>"
                                           class="btn-action btn-action-pay"
                                           title="Registrar pago anticipado"
                                           data-confirm="prepay"
                                           data-name="<s:property value='usuarioNombre'/> ($<s:property value='(numeroCuota - cuotaActual) * valorCuota'/>)">
                                            <i class="bi bi-cash-coin"></i>
                                        </a>
                                    </s:elseif>
                                </div>
                            </td>
                        </tr>
                    </s:iterator>
                </s:if>
                <s:else>
                    <tr>
                        <td colspan="5">
                            <div class="empty-state">
                                <i class="bi bi-people"></i>
                                <p>El cobro no tiene usuarios.</p>
                            </div>
                        </td>
                    </tr>
                </s:else>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
