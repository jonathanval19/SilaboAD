/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.actividades.ad;

import dda.silabo.estructura.unidad.actividades.comunes.Actividad;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class ActividadAD extends Actividad {
    
    public void getActividad(ResultSet rsActividades, String rol) {
        try {
//            if (!rol.equals("Coordinador")) {
            this.setId_actividades_aprendizaje(rsActividades.getInt("id_actividades_aprendizaje"));
            this.setDescripcion(rsActividades.getString("descripcion"));
            this.setTipo_actividad(rsActividades.getString("tipo_actividad"));
            this.setEstado(rsActividades.getString("estado"));
            this.setRol(rsActividades.getString("rol"));
            if (this.getRol() == null) {
                this.setRol("Adm");
            }
            Integer idEscenario = rsActividades.getInt("id_act_aprend");
            if (idEscenario != 0) {
                this.setChv_check("checked");
            }
            
        } catch (SQLException e) {
            Logger.getLogger("EscenarioAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }
    
    public void getActividad(ResultSet rsActividades) {
        try {
            this.setId_actividades_aprendizaje(rsActividades.getInt("id_act_aprend"));
        } catch (SQLException e) {
            Logger.getLogger("EscenarioAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }
}
