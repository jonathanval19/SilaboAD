/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.criteriosevaluaciones.ad;

import dda.silabo.criteriosevaluaciones.comunes.ActividadEvaluar;
import dda.silabo.db.AccesoDatos;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class ActividadEvaluarAD extends ActividadEvaluar {

    public void getActividadesAporte(ResultSet rsActividad, AccesoDatos ad, Integer idSilabo) {
        try {
            this.setId_actividades(rsActividad.getInt("id_actividades"));
            this.setDescripcion(rsActividad.getString("descripcion"));
            AporteAD aporte = new AporteAD();
            this.setAportes(aporte.getActividadesAporte(rsActividad.getInt("id_actividades"), ad, idSilabo));
        } catch (SQLException e) {
            Logger.getLogger("ActividadEvaluarAD").log(java.util.logging.Level.SEVERE, "dda.silabo.criteriosevaluaciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }
}
