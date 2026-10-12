package com.mas.co.web.action;

import com.mas.co.dto.NotaInformativaDto;
import com.mas.co.service.NotaInformativaService;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("notaInformativaAction")
@Scope("prototype")
public class NotaInformativaAction extends BaseAction {

    @Autowired
    private NotaInformativaService notaInformativaService;

    private NotaInformativaDto nota;

    public String formulario() {
        nota = notaInformativaService.obtenerNota();
        return SUCCESS;
    }

    public String guardar() {
        try {
            NotaInformativaDto resultado = notaInformativaService.actualizarNota(nota);
            flashExito(Boolean.TRUE.equals(resultado.getActiva())
                    ? "Nota guardada. Se imprimirá en los recibos desde ahora."
                    : "Nota guardada. Está desactivada: no se imprime en los recibos.");
        } catch (Exception e) {
            flashError(e.getMessage());
            return INPUT;
        }
        return SUCCESS;
    }

    // Getters y Setters

    @StrutsParameter(depth = 1)
    public NotaInformativaDto getNota() {
        return nota;
    }

    @StrutsParameter(depth = 1)
    public void setNota(NotaInformativaDto nota) {
        this.nota = nota;
    }
}
