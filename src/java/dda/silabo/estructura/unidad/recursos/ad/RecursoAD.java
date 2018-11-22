/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.recursos.ad;

import dda.silabo.estructura.unidad.recursos.comunes.Recurso;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class RecursoAD extends Recurso {

    public void RecursosCargar(ResultSet rsRecursos, String rol) {
        try {
            this.setIdRecurso(rsRecursos.getInt("id_recursos"));
            this.setStrDescripcion(rsRecursos.getString("descripcion"));
            this.setEstado(rsRecursos.getString("estado"));
            if (rsRecursos.getInt("id_recurso") != 0) {
                this.setChv_check("checked");
            }
        } catch (SQLException e) {
            Logger.getLogger("RecursoAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void asignarRecursoSeleccionado(ResultSet rsRecursos) {
        try {
            this.setIdRecurso(rsRecursos.getInt("id_recurso"));
        } catch (SQLException e) {
            Logger.getLogger("RecursoAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
