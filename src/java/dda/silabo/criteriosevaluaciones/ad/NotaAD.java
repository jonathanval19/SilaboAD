/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.criteriosevaluaciones.ad;

import dda.silabo.criteriosevaluaciones.comunes.Nota;
import dda.silabo.db.AccesoDatos;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class NotaAD extends Nota {

    public void getActividadesAporteNota(int idAporte, int idActividad, AccesoDatos ad, Integer idSilabo) throws SQLException {
        String SQL = "select nota from t_actividades_aportes where (id_aporte=? and id_actividad=? and id_silabo=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idAporte);
            ps.setInt(2, idActividad);
            ps.setInt(3, idSilabo);
            ResultSet rsNota = ps.executeQuery();
            while (rsNota.next()) {
                this.setNota(rsNota.getInt("nota"));
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("NotaAD").log(java.util.logging.Level.SEVERE, "dda.silabo.criteriosevaluaciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }

    }

    public Integer getNotaTotalAporte(int idAporte, AccesoDatos ad, Integer idSilabo) throws SQLException {
        int nota = 0;
        String SQL = "select nota from t_actividades_aportes where (id_aporte=? and id_silabo=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idAporte);
            ps.setInt(2, idSilabo);
            ResultSet rsNota = ps.executeQuery();
            while (rsNota.next()) {
                nota += rsNota.getInt("nota");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("NotaAD").log(java.util.logging.Level.SEVERE, "dda.silabo.criteriosevaluaciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return nota;
    }

}
