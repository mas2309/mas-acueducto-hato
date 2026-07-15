document.addEventListener('DOMContentLoaded', function () {

    // ===== Sidebar Toggle (mobile) =====
    var toggleBtn = document.getElementById('sidebarToggle');
    var sidebar = document.getElementById('sidebar');
    var overlay = document.getElementById('sidebarOverlay');

    function closeSidebar() {
        if (sidebar) sidebar.classList.remove('show');
        if (overlay) overlay.classList.remove('show');
    }

    if (toggleBtn) {
        toggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('show');
            overlay.classList.toggle('show');
        });
    }

    if (overlay) {
        overlay.addEventListener('click', closeSidebar);
    }

    // ===== Active Sidebar Link =====
    var currentPath = window.location.pathname + window.location.search;
    document.querySelectorAll('.sidebar-link').forEach(function (link) {
        var href = link.getAttribute('href');
        if (href && currentPath.indexOf(href.split('?')[0]) !== -1) {
            document.querySelectorAll('.sidebar-link.active').forEach(function (el) {
                el.classList.remove('active');
            });
            link.classList.add('active');
        }
    });

    // ===== Auto-dismiss Alerts =====
    document.querySelectorAll('.alert-dismissible').forEach(function (alert) {
        setTimeout(function () {
            var bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            bsAlert.close();
        }, 4000);
    });

    // ===== Confirm Modal =====
    var confirmModal = document.getElementById('confirmModal');
    if (confirmModal) {
        var bsModal = new bootstrap.Modal(confirmModal);
        var confirmBtn = document.getElementById('confirmModalBtn');
        var confirmTitle = document.getElementById('confirmModalTitle');
        var confirmMsg = document.getElementById('confirmModalMsg');
        var confirmIcon = document.getElementById('confirmModalIcon');

        document.querySelectorAll('[data-confirm]').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                var type = el.getAttribute('data-confirm');
                var href = el.getAttribute('href');
                var name = el.getAttribute('data-name') || '';

                if (type === 'delete') {
                    confirmTitle.textContent = 'Eliminar Registro';
                    confirmMsg.innerHTML = '¿Está seguro de eliminar <strong>' + name + '</strong>?<br><small class="text-muted">Esta acción no se puede deshacer.</small>';
                    confirmIcon.className = 'confirm-icon confirm-icon-danger';
                    confirmIcon.innerHTML = '<i class="bi bi-exclamation-triangle"></i>';
                    confirmBtn.className = 'btn btn-danger';
                    confirmBtn.textContent = 'Sí, eliminar';
                } else if (type === 'deactivate') {
                    confirmTitle.textContent = 'Desactivar Registro';
                    confirmMsg.innerHTML = '¿Desea desactivar <strong>' + name + '</strong>?';
                    confirmIcon.className = 'confirm-icon confirm-icon-warning';
                    confirmIcon.innerHTML = '<i class="bi bi-toggle-off"></i>';
                    confirmBtn.className = 'btn btn-warning';
                    confirmBtn.textContent = 'Sí, desactivar';
                } else if (type === 'pay') {
                    confirmTitle.textContent = 'Registrar Pago';
                    confirmMsg.innerHTML = '¿Confirma el pago de <strong>' + name + '</strong>?';
                    confirmIcon.className = 'confirm-icon confirm-icon-success';
                    confirmIcon.innerHTML = '<i class="bi bi-cash-coin"></i>';
                    confirmBtn.className = 'btn btn-success';
                    confirmBtn.textContent = 'Sí, registrar pago';
                } else if (type === 'pay-cash') {
                    confirmTitle.textContent = 'Pago en Efectivo';
                    confirmMsg.innerHTML = '¿Registrar pago en <strong>efectivo</strong> de <strong>' + name + '</strong>?';
                    confirmIcon.className = 'confirm-icon confirm-icon-success';
                    confirmIcon.innerHTML = '<i class="bi bi-cash"></i>';
                    confirmBtn.className = 'btn btn-success';
                    confirmBtn.textContent = 'Sí, pagar';
                } else if (type === 'pay-bank') {
                    confirmTitle.textContent = 'Pago por Banco';
                    confirmMsg.innerHTML = '¿Registrar pago por <strong>banco</strong> de <strong>' + name + '</strong>?';
                    confirmIcon.className = 'confirm-icon confirm-icon-success';
                    confirmIcon.innerHTML = '<i class="bi bi-bank"></i>';
                    confirmBtn.className = 'btn btn-primary';
                    confirmBtn.textContent = 'Sí, pagar';
                }

                confirmBtn.onclick = function () {
                    window.location.href = href;
                };

                bsModal.show();
            });
        });
    }

    // ===== Search Clear =====
    var searchInput = document.getElementById('searchInput');
    var searchClear = document.getElementById('searchClear');
    var searchForm = document.getElementById('searchForm');

    if (searchInput && searchClear && searchForm) {
        function toggleClearBtn() {
            searchClear.style.display = searchInput.value.trim() ? 'inline-block' : 'none';
        }
        toggleClearBtn();
        searchInput.addEventListener('input', toggleClearBtn);

        searchClear.addEventListener('click', function () {
            searchInput.value = '';
            searchForm.submit();
        });
    }

    // ===== Fade-in animation =====
    document.querySelectorAll('.fade-in-item').forEach(function (el, i) {
        el.style.animationDelay = (i * 0.05) + 's';
        el.classList.add('fade-in');
    });
});
