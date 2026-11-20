<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Metadatos de la PWA (instalación como aplicación). Se incluye en el <head> de header.jsp y login.jsp. --%>
<link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
<meta name="theme-color" content="#1e293b">
<meta name="application-name" content="Acueducto">
<meta name="mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-title" content="Acueducto">
<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
<link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/static/img/pwa/favicon-32.png">
<link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/static/img/pwa/apple-touch-icon.png">
