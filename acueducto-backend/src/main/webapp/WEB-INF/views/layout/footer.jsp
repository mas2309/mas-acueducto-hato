        </main>

        <footer class="main-footer">
            &copy; 2025 Acueducto &mdash; Panel de Administraci&oacute;n
        </footer>
    </div>

    <%-- Confirm Modal --%>
    <div class="modal fade modal-confirm" id="confirmModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h6 class="modal-title" id="confirmModalTitle"></h6>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="confirmModalIcon"></div>
                    <p id="confirmModalMsg" class="mb-0"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-sm" id="confirmModalBtn"></button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/admin.js"></script>
</body>
</html>
