/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.logros.ad;

import dda.silabo.estructura.unidad.logros.comunes.Logro;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jorge Zaruma
 */
public class LogroAD extends Logro {

    public void getLogro(ResultSet rsLogro) {
        try {
            this.setIdLogro(rsLogro.getInt("id_logro"));
            this.setDescripcion(rsLogro.getString("descripcion"));
            this.setIdSubseccion(rsLogro.getInt("id_unidad_subseccion"));
            this.setIdSilabo(rsLogro.getInt("idSilabo"));
            this.setIdUnidad(rsLogro.getInt("id_unidad"));
        } catch (SQLException e) {

        }
    }
}
