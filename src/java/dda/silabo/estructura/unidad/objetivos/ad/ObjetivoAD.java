/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.objetivos.ad;

import dda.silabo.estructura.unidad.objetivos.comunes.Objetivo;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jorge Zaruma
 */
public class ObjetivoAD extends Objetivo {

    public void ObjetivoCargar(ResultSet rsObjetivo) {
        try {
            this.setIdObjetivo(rsObjetivo.getInt("id_objetivos"));
            this.setDescripcion(rsObjetivo.getString("descripcion"));
            this.setIdSubseccion(rsObjetivo.getInt("id_unidad_subseccion"));
            this.setIdSilabo(rsObjetivo.getInt("idSilabo"));
            this.setIdUnidad(rsObjetivo.getInt("id_unidad"));
        } catch (SQLException e) {

        }
    }

}
