package com.mas.co.web.action;

import com.mas.co.entity.Factura;
import com.mas.co.repository.FacturaRepository;
import com.mas.co.service.CuotaService;
import com.mas.co.service.UsuarioService;
import com.mas.co.service.impl.EstadoFacturaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("dashboardAction")
@Scope("prototype")
public class DashboardAction extends BaseAction {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CuotaService cuotaService;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private EstadoFacturaService estadoFacturaService;

    private long totalUsuarios;
    private long totalCuotasActivas;
    private long facturasPendientes;
    private long facturasVencidas;
    private double ingresosMesActual;
    private int consumoMesActual;
    private String mesActual;
    private int anioActual;
    private long totalFacturasMes;
    private double ingresoEfectivoMes;
    private double ingresoBancoMes;

    @Override
    public String execute() {
        totalUsuarios = usuarioService.obtenerUsuariosActivos().size();
        totalCuotasActivas = cuotaService.obtenerCuotasActivas().size();

        LocalDate hoy = LocalDate.now();
        anioActual = hoy.getYear();
        mesActual = obtenerNombreMes(hoy.getMonthValue());

        List<Factura> facturasMes = facturaRepository.findByMesAndAnio(mesActual, anioActual);
        totalFacturasMes = facturasMes.size();

        calcularMetricasMes(facturasMes);

        return SUCCESS;
    }

    private void calcularMetricasMes(List<Factura> facturasMes) {
        double ingTotal = 0;
        double ingEfectivo = 0;
        double ingBanco = 0;
        int consumo = 0;
        long pend = 0;
        long venc = 0;

        for (Factura f : facturasMes) {
            double valorFactura = f.getValorTotal() != null ? f.getValorTotal() : 0;
            ingTotal += valorFactura;
            consumo += f.getConsumo() != null ? f.getConsumo() : 0;

            if (Boolean.TRUE.equals(f.getPago())) {
                ingEfectivo += valorFactura;
            } else if (Boolean.TRUE.equals(f.getPagoBanco())) {
                ingBanco += valorFactura;
            } else {
                String estado = estadoFacturaService.calcularEstado(
                        false, false, f.getMes(), f.getAnio());
                if ("VENCIDA".equals(estado)) {
                    venc++;
                } else {
                    pend++;
                }
            }
        }

        ingresosMesActual = ingTotal;
        consumoMesActual = consumo;
        ingresoEfectivoMes = ingEfectivo;
        ingresoBancoMes = ingBanco;
        facturasPendientes = pend;
        facturasVencidas = venc;
    }

    private String obtenerNombreMes(int mes) {
        return switch (mes) {
            case 1 -> "Enero";
            case 2 -> "Febrero";
            case 3 -> "Marzo";
            case 4 -> "Abril";
            case 5 -> "Mayo";
            case 6 -> "Junio";
            case 7 -> "Julio";
            case 8 -> "Agosto";
            case 9 -> "Septiembre";
            case 10 -> "Octubre";
            case 11 -> "Noviembre";
            case 12 -> "Diciembre";
            default -> "";
        };
    }

    public long getTotalUsuarios() {
        return totalUsuarios;
    }

    public long getTotalCuotasActivas() {
        return totalCuotasActivas;
    }

    public long getFacturasPendientes() {
        return facturasPendientes;
    }

    public long getFacturasVencidas() {
        return facturasVencidas;
    }

    public double getIngresosMesActual() {
        return ingresosMesActual;
    }

    public int getConsumoMesActual() {
        return consumoMesActual;
    }

    public String getMesActual() {
        return mesActual;
    }

    public int getAnioActual() {
        return anioActual;
    }

    public long getTotalFacturasMes() {
        return totalFacturasMes;
    }

    public double getIngresoEfectivoMes() {
        return ingresoEfectivoMes;
    }

    public double getIngresoBancoMes() {
        return ingresoBancoMes;
    }
}
