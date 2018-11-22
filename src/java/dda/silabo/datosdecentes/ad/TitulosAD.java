/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.datosdecentes.ad;

import dda.silabo.datosdocentes.comunes.Titulos;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jorge Zaruma
 */
public class TitulosAD extends Titulos {
    
    void agregarTitulos(ResultSet rs2) {
        try {
            this.setDescripcion(rs2.getString("descripcion"));
            this.setNivel(rs2.getInt("nivel"));
            this.setSelected(rs2.getString("estado"));
            this.setId(rs2.getInt("id"));
        } catch (SQLException e) {
        }
    }
    
}
