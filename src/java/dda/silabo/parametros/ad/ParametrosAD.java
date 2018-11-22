/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.parametros.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.parametros.comunes.Parametro;
import dda.silabo.parametros.comunes.Parametros;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jorge Zaruma
 */
public class ParametrosAD extends Parametros {

    public void ParametrosCargar(AccesoDatos ad) throws SQLException {
        try {
            String SQL = "Select * from t_parametros";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsParametros = ps.executeQuery();
            while (rsParametros.next()) {
                ParametroAD parametroAD = new ParametroAD();
                parametroAD.ParametrosCargar(rsParametros);
                this.getParametros().add(parametroAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        this.setTitulo("Parámetros de Sílabo");
        this.setAyuda("Son los parámetros adicionales que se presentaran una vez que se haya elaborado el Sílabo de Asignatura");
    }

    public void ParametrosGuardar(AccesoDatos ad) throws SQLException {
        String SQL = "";
        try {
            PreparedStatement ps = null;
            for (Parametro parametro : this.getParametros()) {
                SQL = "UPDATE t_parametros SET descripcion=?, valor=? WHERE (id_parametro=?)";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, parametro.getDescripcion());
                ps.setString(2, parametro.getValor());
                ps.setInt(3, parametro.getId());
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

}
