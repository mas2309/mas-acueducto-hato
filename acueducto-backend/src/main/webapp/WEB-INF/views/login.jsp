<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - Acueducto El Hato</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        :root {
            --primary: #0d6efd;
            --primary-dark: #0a58ca;
        }
        body {
            font-family: 'Inter', system-ui, sans-serif;
            background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }
        .login-container {
            width: 100%;
            max-width: 420px;
        }
        .login-card {
            background: #fff;
            border-radius: 1rem;
            box-shadow: 0 25px 50px rgba(0,0,0,.25);
            overflow: hidden;
        }
        .login-header {
            background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%);
            padding: 2rem 2rem 1.5rem;
            text-align: center;
            color: #fff;
        }
        .login-header img {
            width: 64px;
            height: 64px;
            border-radius: .75rem;
            object-fit: cover;
            margin-bottom: .75rem;
            box-shadow: 0 4px 6px rgba(0,0,0,.2);
        }
        .login-header h4 {
            font-weight: 700;
            margin-bottom: .25rem;
            font-size: 1.1rem;
        }
        .login-header p {
            font-size: .8rem;
            opacity: .75;
            margin: 0;
        }
        .login-body {
            padding: 2rem;
        }
        .form-label {
            font-weight: 600;
            font-size: .8rem;
            color: #374151;
        }
        .form-control {
            border-radius: .5rem;
            border: 1px solid #e2e8f0;
            padding: .65rem .875rem;
            font-size: .875rem;
            transition: all .2s ease;
        }
        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(13,110,253,.15);
        }
        .input-group-text {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: .5rem 0 0 .5rem;
            color: #64748b;
        }
        .input-group .form-control {
            border-radius: 0 .5rem .5rem 0;
        }
        .btn-login {
            width: 100%;
            padding: .7rem;
            font-weight: 600;
            font-size: .9rem;
            border-radius: .5rem;
            background: linear-gradient(135deg, #3b82f6, #1d4ed8);
            border: none;
            color: #fff;
            transition: all .2s ease;
        }
        .btn-login:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(37,99,235,.4);
            background: linear-gradient(135deg, #2563eb, #1e40af);
            color: #fff;
        }
        .alert {
            border: none;
            border-radius: .5rem;
            font-size: .8rem;
            padding: .75rem 1rem;
        }
        .login-footer {
            text-align: center;
            padding: 0 2rem 1.5rem;
            font-size: .7rem;
            color: #94a3b8;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <img src="${pageContext.request.contextPath}/static/img/logo.png" alt="Logo">
                <h4>Acueducto El Hato</h4>
                <p>Panel de Administraci&oacute;n</p>
            </div>

            <div class="login-body">
                <c:if test="${param.error != null}">
                    <div class="alert alert-danger d-flex align-items-center gap-2">
                        <i class="bi bi-exclamation-circle"></i>
                        Usuario o contrase&ntilde;a incorrectos.
                    </div>
                </c:if>

                <c:if test="${param.locked != null}">
                    <div class="alert alert-danger d-flex align-items-center gap-2">
                        <i class="bi bi-shield-lock"></i>
                        Cuenta bloqueada por m&uacute;ltiples intentos fallidos. Intente en <strong>${param.minutes}</strong> minutos.
                    </div>
                </c:if>

                <c:if test="${param.logout != null}">
                    <div class="alert alert-success d-flex align-items-center gap-2">
                        <i class="bi bi-check-circle"></i>
                        Sesi&oacute;n cerrada exitosamente.
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label">Usuario</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-person"></i></span>
                            <input type="text" class="form-control" id="username" name="username"
                                   placeholder="Ingrese su usuario" required autofocus>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="password" class="form-label">Contrase&ntilde;a</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock"></i></span>
                            <input type="password" class="form-control" id="password" name="password"
                                   placeholder="Ingrese su contrase&ntilde;a" required>
                        </div>
                    </div>


                    <button type="submit" class="btn btn-login">
                        <i class="bi bi-box-arrow-in-right me-2"></i>Iniciar Sesi&oacute;n
                    </button>
                </form>
            </div>

            <div class="login-footer">
                &copy; 2025 Junta Administradora Acueducto El Hato &bull; Tibasosa, Boyac&aacute;
            </div>
        </div>
    </div>
</body>
</html>
