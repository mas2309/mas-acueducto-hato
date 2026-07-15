<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin - Acueducto</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/css/admin.css" rel="stylesheet">
</head>
<body>

    <%-- Sidebar Overlay (mobile) --%>
    <div id="sidebarOverlay" class="sidebar-overlay"></div>

    <%-- Sidebar --%>
    <aside id="sidebar" class="sidebar">
        <a href="<s:url namespace='/admin' action='dashboard'/>" class="sidebar-brand">
            <img src="${pageContext.request.contextPath}/static/img/logo.png" alt="Logo"
                 style="width:38px;height:38px;border-radius:.625rem;object-fit:cover;">
            <div class="brand-text">
                Acueducto El Hato
                <small>Panel de Administraci&oacute;n</small>
            </div>
        </a>

        <nav class="sidebar-nav">
            <div class="sidebar-section">General</div>
            <a href="<s:url namespace='/admin' action='dashboard'/>" class="sidebar-link" id="nav-dashboard">
                <i class="bi bi-grid-1x2"></i> Dashboard
            </a>

            <div class="sidebar-section">Gesti&oacute;n</div>
            <a href="<s:url namespace='/admin' action='usuarios'/>" class="sidebar-link" id="nav-usuarios">
                <i class="bi bi-people"></i> Usuarios
            </a>
            <a href="<s:url namespace='/admin' action='cuotas'/>" class="sidebar-link" id="nav-cuotas">
                <i class="bi bi-credit-card"></i> Cuotas
            </a>
            <a href="<s:url namespace='/admin' action='facturas'/>" class="sidebar-link" id="nav-facturas">
                <i class="bi bi-receipt"></i> Facturas
            </a>

            <div class="sidebar-section">Finanzas</div>
            <a href="<s:url namespace='/admin' action='ingresos'/>" class="sidebar-link" id="nav-ingresos">
                <i class="bi bi-graph-up-arrow"></i> Ingresos
            </a>
            <a href="<s:url namespace='/admin' action='gastos'/>" class="sidebar-link" id="nav-gastos">
                <i class="bi bi-graph-down-arrow"></i> Gastos
            </a>

            <div class="sidebar-section">Reportes</div>
            <a href="<s:url namespace='/admin' action='informes'/>" class="sidebar-link" id="nav-informes">
                <i class="bi bi-bar-chart-line"></i> Informes
            </a>

            <sec:authorize access="hasRole('ADMIN')">
            <div class="sidebar-section">Seguridad</div>
            <a href="<s:url namespace='/admin' action='admin-users'/>" class="sidebar-link" id="nav-admin-users">
                <i class="bi bi-shield-lock"></i> Usuarios Admin
            </a>
            </sec:authorize>
        </nav>
    </aside>

    <%-- Main Content --%>
    <div class="main-content">

        <%-- Top Bar --%>
        <header class="topbar">
            <div class="d-flex align-items-center gap-3">
                <button id="sidebarToggle" class="btn-sidebar-toggle">
                    <i class="bi bi-list"></i>
                </button>
                <div>
                    <h1 class="topbar-title" id="pageTitle"></h1>
                </div>
            </div>
            <form action="${pageContext.request.contextPath}/logout" method="post" class="d-inline">
                <button type="submit" class="btn btn-sm btn-outline-secondary" title="Cerrar sesi&oacute;n">
                    <i class="bi bi-box-arrow-right me-1"></i>Salir
                </button>
            </form>
        </header>

        <%-- Page Content --%>
        <main class="page-content fade-in">
