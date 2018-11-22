/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.parametros.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.parametros.comunes.Parametro;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jorge Zaruma
 */
public class ParametroAD extends Parametro {

    void ParametrosCargar(ResultSet rsParametros) {
        try {
            this.setId(rsParametros.getInt("id_parametro"));
            this.setDescripcion(rsParametros.getString("descripcion"));
            this.setValor(rsParametros.getString("valor"));
        } catch (SQLException e) {

        }
    }

    public void ParametroSilaboCargar(AccesoDatos ad, Integer idParametro) throws SQLException {
        try {
            String SQL = "Select * from t_parametros where id_parametro=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idParametro);
            ResultSet rsParametros = ps.executeQuery();
            while (rsParametros.next()) {
                ParametrosCargar(rsParametros);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

}
