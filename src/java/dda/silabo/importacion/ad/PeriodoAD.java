/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.importacion.ad;

import dda.silabo.importacion.comunes.Periodo;
import ec.edu.espoch.academico.ArrayOfPeriodo;
import java.util.List;

/**
 *
 * @author Jorge Zaruma
 */
public class PeriodoAD extends Periodo {

    public void agregarPeriodos(String codPeriodo) {
        List<ec.edu.espoch.academico.Periodo> periodos = getPeriodosAcademicos().getPeriodo();
        for (ec.edu.espoch.academico.Periodo periodo : periodos) {
            if (periodo.getCodigo().equals(codPeriodo)) {
                this.setCodPeriodo(codPeriodo);
                this.setDescPeriodo(periodo.getDescripcion());

            }
        }
    }

    public String getSQLSelect(String cedula, String codPeriodo) {
        String SQL = " SELECT distinct(t_entidad.codigo_entidad),t_entidad.id_entidad FROM t_docente INNER JOIN t_usuario_entidad ON t_docente.id_docente=t_usuario_entidad.id_usuario\n"
                + "                INNER JOIN t_periodo_academico ON t_periodo_academico.id_periodo=t_usuario_entidad.id_periodo\n"
                + "               INNER JOIN t_entidad ON t_usuario_entidad.id_entidad=t_entidad.id_entidad\n"
                + "                WHERE cedula='" + cedula + "' AND t_periodo_academico.codigo='" + codPeriodo + "'";
        return SQL;
    }

    private static ArrayOfPeriodo getPeriodosAcademicos() {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getPeriodosAcademicos();
    }

}
