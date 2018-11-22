/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.criteriosevaluaciones.ad;

import dda.silabo.criteriosevaluaciones.comunes.Calificaciones;
import dda.silabo.db.AccesoDatos;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class CalificacionesAD extends Calificaciones {

    public void eliminarCalificaciones(int idSilabo, AccesoDatos ad) throws SQLException {
        try {

            String SQL = "DELETE FROM t_actividades_aportes where (id_silabo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminarCalificacionesImportacion(int idSilabo, AccesoDatos ad) throws SQLException {
        try {
            String SQL = "DELETE FROM t_actividades_aportes where (id_silabo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.executeUpdate();
            ps.close();
            SQL = "DELETE FROM t_actividades\n"
                    + " WHERE id_silabo=?";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void guardarCalificaciones(AccesoDatos ad, int idSilabo) throws SQLException {
        try {
            PreparedStatement ps = null;
            for (int i = 0; i < this.getEvaluaciones().size(); i++) {
                Integer actividad = Integer.parseInt(this.getEvaluaciones().get(i).getActividad());
                Integer aporte = Integer.parseInt(this.getEvaluaciones().get(i).getAporte());
                Integer nota = this.getEvaluaciones().get(i).getNota();
                String SQL = "INSERT INTO t_actividades_aportes (id_actividad,id_aporte,nota,id_silabo) VALUES(?,?,?,?)";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, actividad);
                ps.setInt(2, aporte);
                ps.setInt(3, nota);
                ps.setInt(4, idSilabo);
                ps.executeUpdate();
                 ad.getCon().commit();
                ps.close();
            }
           
            ps.close();
        } catch (NumberFormatException | SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("CalificacionesAD").log(java.util.logging.Level.SEVERE, "dda.silabo.criteriosevaluaciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void importarCalificaciones(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad) throws SQLException {

        try {
            eliminarCalificacionesImportacion(silaboActual.getIdSilabo(), ad);
            String SQL = "INSERT INTO t_actividades(descripcion, id_silabo)\n"
                    + "    select descripcion,'" + silaboActual.getIdSilabo() + "' from t_actividades where id_silabo=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.executeUpdate();
            ps.close();
            SQL = "insert into t_actividades_aportes (id_actividad,id_aporte,nota,id_silabo)\n"
                    + "select id_actividad,id_aporte,nota,'" + silaboActual.getIdSilabo() + "' from t_actividades_aportes \n"
                    + "where (id_silabo =?)";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("CalificacionesAD").log(Level.SEVERE, "dda.silabo.criteriosevaluaciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
