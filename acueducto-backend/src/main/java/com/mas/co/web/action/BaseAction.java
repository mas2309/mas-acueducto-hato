package com.mas.co.web.action;

import java.util.Map;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionSupport;

public abstract class BaseAction extends ActionSupport {

    private static final String FLASH_EXITO = "_flashExito";
    private static final String FLASH_ERROR = "_flashError";

    private String mensajeExito;
    private String mensajeError;
    private boolean flashLoaded = false;

    protected Map<String, Object> getSession() {
        return ActionContext.getContext().getSession();
    }

    protected void flashExito(String mensaje) {
        getSession().put(FLASH_EXITO, mensaje);
    }

    protected void flashError(String mensaje) {
        getSession().put(FLASH_ERROR, mensaje);
    }

    private void loadFlash() {
        if (!flashLoaded) {
            Object exito = getSession().remove(FLASH_EXITO);
            Object error = getSession().remove(FLASH_ERROR);
            mensajeExito = exito != null ? exito.toString() : null;
            mensajeError = error != null ? error.toString() : null;
            flashLoaded = true;
        }
    }

    public String getMensajeExito() {
        loadFlash();
        return mensajeExito;
    }

    public String getMensajeError() {
        loadFlash();
        return mensajeError;
    }
}
