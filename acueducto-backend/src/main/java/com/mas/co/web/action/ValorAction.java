package com.mas.co.web.action;

import com.mas.co.dto.ValoresDto;
import com.mas.co.entity.Valores;
import com.mas.co.service.ValorService;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("valorAction")
@Scope("prototype")
public class ValorAction extends BaseAction {

    @Autowired
    private ValorService valorService;

    private ValoresDto valores;

    public String formulario() {
        Valores actuales = valorService.obtenerValores();
        valores = ValoresDto.builder()
                .cargoFijo(actuales.getCargoFijo())
                .noPago(actuales.getNoPago())
                .rangoUno(actuales.getRangoUno().intValue())
                .rangoDos(actuales.getRangoDos().intValue())
                .rangoTres(actuales.getRangoTres().intValue())
                .valorUno(actuales.getValorUno())
                .valorDos(actuales.getValorDos())
                .valorTres(actuales.getValorTres())
                .valorCuatro(actuales.getValorCuatro())
                .build();
        return SUCCESS;
    }

    public String guardar() {
        try {
            valorService.actualizarValores(valores);
            flashExito("Tarifas actualizadas. Aplican para las facturas que se generen desde ahora.");
        } catch (Exception e) {
            flashError(e.getMessage());
            return INPUT;
        }
        return SUCCESS;
    }

    // Getters y Setters

    @StrutsParameter(depth = 1)
    public ValoresDto getValores() {
        return valores;
    }

    @StrutsParameter(depth = 1)
    public void setValores(ValoresDto valores) {
        this.valores = valores;
    }
}
