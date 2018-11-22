/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.criteriosevaluaciones.ad;

import dda.panalitico.ws.ProcedimientoComunes;
import dda.panalitico.ws.ProcedimientosAD;
import dda.silabo.criteriosevaluaciones.comunes.CriteriosEvaluaciones;
import dda.silabo.db.AccesoDatos;
import dda.silabo.observaciones.ad.ObservacionesAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Jorge Zaruma
 */
public class CriteriosEvaluacionesAD extends CriteriosEvaluaciones {

    public void getEvaluaciones(AccesoDatos ad, Silabo silabo) throws SQLException {
        String SQL2 = "select * from t_actividades where id_silabo=? order by id_actividades";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL2);
            ps.setInt(1, silabo.getIdSilabo());
            ResultSet rsActividad = ps.executeQuery();
            while (rsActividad.next()) {
                ActividadEvaluarAD actividad = new ActividadEvaluarAD();
                actividad.getActividadesAporte(rsActividad, ad, silabo.getIdSilabo());
                this.getActividadesevaluar().add(actividad);
            }
            ad.getCon().commit();
            ps.close();
            this.setSilabos(silabo);
            this.setAyuda("<div style='text-align:justify'>Se debe seleccionar las calificaciones que corresponda a cada parcial teniendo en cuenta que lo siguiente: "
                    + " Primer Parcial: la sumatoria final de las evaluaciones debe tener maximo 8 puntos"
                    + " Segundo y Tercer Parcial la sumatoria final de las evaluaciones debe tener maximo 8 puntos "
                    + " Exámen principal esta establecido como una nota maxima de 12"
                    + " Exámen de Suspenso esta establecido como una nota maxima de 20</div>");
            this.setTitulo("Criterios de Evaluación");
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("CriteriosEvaluacionesAD").log(java.util.logging.Level.SEVERE, "dda.silabo.criteriosevaluaciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }

    }

    public void obtenerObservaciones(String tipo, Silabo silabo, AccesoDatos ad) {
        ObservacionesAD observacion = new ObservacionesAD();
        observacion.getListaObservacionesSeccion(tipo, silabo, 4, ad);
        this.setObservacion(observacion);

    }

    public void ingresarActividadesEvaluar(AccesoDatos ad, Integer idSilabo, String codAsginatura, String codCarrera) throws SQLException {
        try {
            ProcedimientosAD procedimientosAD = programaAnaliticoProcedimientosAsignatura(codAsginatura, codCarrera);
            if (!procedimientosAD.getCodPrograma().equals("0")) {
                List<ProcedimientoComunes> procedimientos = procedimientosAD.getObjListaProcedimientos().stream().filter(pr -> pr.getIntCodigoPrograma() != 0).collect(Collectors.toList());
                PreparedStatement ps = null;
                String SQL = "";
                for (ProcedimientoComunes p : procedimientos) {
                    SQL = "INSERT INTO t_actividades(\n"
                            + "            descripcion, id_silabo)\n"
                            + "    VALUES (?, ?);";
                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setString(1, p.getStrDescripcion());
                    ps.setInt(2, idSilabo);
                    ps.executeUpdate();
                    ps.close();
                    ad.getCon().commit();
                }
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    private static ProcedimientosAD programaAnaliticoProcedimientosAsignatura(java.lang.String codAsginatura, java.lang.String codCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.programaAnaliticoProcedimientosAsignatura(codAsginatura, codCarrera);
    }
}
