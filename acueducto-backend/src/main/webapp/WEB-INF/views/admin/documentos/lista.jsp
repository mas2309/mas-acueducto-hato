<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<script>document.getElementById('pageTitle').textContent = 'Documentos Importantes';</script>
<script>document.getElementById('nav-documentos').classList.add('active');</script>

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

<%-- Table Container --%>
<div class="table-container fade-in-item">

    <%-- Header: search + new button --%>
    <div class="table-header">
        <div class="d-flex align-items-center gap-3 flex-grow-1 flex-wrap">
            <h5><i class="bi bi-folder2-open me-2"></i>Documentos Importantes</h5>
            <form id="searchForm" action="<s:url namespace='/admin' action='documentos'/>" method="get" class="d-flex gap-2 ms-auto flex-wrap">
                <div class="search-box">
                    <i class="bi bi-search search-icon"></i>
                    <input type="text" id="searchInput" name="q" value="<s:property value='q'/>" class="form-control"
                           placeholder="Buscar por t&iacute;tulo..." style="width:240px;"/>
                </div>
                <button type="submit" class="btn btn-sm btn-primary">
                    <i class="bi bi-search"></i>
                </button>
                <button type="button" id="searchClear" class="btn btn-sm btn-outline-secondary" style="display:none;">
                    <i class="bi bi-x-lg"></i>
                </button>
            </form>
        </div>
        <sec:authorize access="hasRole('ESCRITURA')">
        <a href="<s:url namespace='/admin' action='documento-formulario'/>" class="btn btn-primary btn-sm">
            <i class="bi bi-upload me-1"></i> Subir Documento
        </a>
        </sec:authorize>
    </div>

    <%-- Table --%>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>T&iacute;tulo</th>
                    <th>Archivo</th>
                    <th>Fecha</th>
                    <th>Subido por</th>
                    <th class="text-center">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <s:if test="documentos != null && documentos.size() > 0">
                    <s:iterator value="documentos">
                        <tr>
                            <td>
                                <span class="fw-semibold"><s:property value="titulo"/></span>
                                <s:if test="descripcion != null && descripcion != ''">
                                    <br><small class="text-muted"><s:property value="descripcion"/></small>
                                </s:if>
                            </td>
                            <td>
                                <span class="text-muted" style="font-size:.85rem;">
                                    <i class="bi bi-paperclip me-1"></i><s:property value="archivoNombre"/>
                                </span>
                            </td>
                            <td><s:property value="fechaSubida"/></td>
                            <td>
                                <span class="text-muted">
                                    <i class="bi bi-person me-1"></i><s:property value="subidoPor"/>
                                </span>
                            </td>
                            <td class="text-center">
                                <div class="d-flex gap-1 justify-content-center">
                                    <a href="<s:property value='archivoUrl' escapeHtml='false'/>" target="_blank"
                                       class="btn-action btn-action-view" title="Abrir o descargar">
                                        <i class="bi bi-file-earmark-arrow-down"></i>
                                    </a>
                                    <sec:authorize access="hasRole('ESCRITURA')">
                                    <a href="<s:url namespace='/admin' action='documento-eliminar'>
                                                <s:param name='id' value='id'/>
                                             </s:url>"
                                       class="btn-action btn-action-delete"
                                       title="Eliminar"
                                       data-confirm="delete"
                                       data-name="<s:property value='titulo'/>">
                                        <i class="bi bi-trash3"></i>
                                    </a>
                                    </sec:authorize>
                                </div>
                            </td>
                        </tr>
                    </s:iterator>
                </s:if>
                <s:else>
                    <tr>
                        <td colspan="5">
                            <div class="empty-state">
                                <i class="bi bi-folder2-open"></i>
                                <p>No se encontraron documentos.</p>
                            </div>
                        </td>
                    </tr>
                </s:else>
            </tbody>
        </table>
    </div>

    <%-- Footer: info + pagination --%>
    <s:if test="documentos != null && documentos.size() > 0">
        <div class="table-footer">
            <span class="table-info">
                Mostrando p&aacute;gina <strong><s:property value="page + 1"/></strong>
                de <strong><s:property value="totalPages"/></strong>
                &mdash; <s:property value="totalElements"/> registros
            </span>

            <s:if test="totalPages > 1">
                <nav>
                    <ul class="pagination pagination-sm mb-0">
                        <s:iterator begin="0" end="%{totalPages - 1}" var="i">
                            <s:if test="#i == page">
                                <li class="page-item active">
                                    <span class="page-link"><s:property value="#i + 1"/></span>
                                </li>
                            </s:if>
                            <s:else>
                                <li class="page-item">
                                    <a class="page-link"
                                       href="<s:url namespace='/admin' action='documentos'>
                                                <s:param name='page' value='%{#i}'/>
                                                <s:param name='size' value='%{size}'/>
                                                <s:param name='q' value='%{q}'/>
                                             </s:url>">
                                        <s:property value="#i + 1"/>
                                    </a>
                                </li>
                            </s:else>
                        </s:iterator>
                    </ul>
                </nav>
            </s:if>
        </div>
    </s:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
