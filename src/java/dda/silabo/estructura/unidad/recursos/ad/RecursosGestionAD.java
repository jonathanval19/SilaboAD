/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.recursos.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.recursos.comunes.Recursos;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class RecursosGestionAD extends Recursos {

    public void GuardarAdmiRecursos(AccesoDatos ad) throws SQLException {
        String SQL2 = "";
        try {
            for (int i = 0; i < this.getRecursos().size(); i++) {
                String d = this.getRecursos().get(i).getStrDescripcion();
                Integer id = this.getRecursos().get(i).getIdRecurso();
                PreparedStatement ps = null;
                if (id == 0) {
                    SQL2 = "INSERT INTO t_recursos (descripcion,estado) VALUES (?,?)";
                    ps = ad.getCon().prepareStatement(SQL2);
                    ps.setString(1, d);
                    ps.setString(2, "H");
                } else {
                    SQL2 = " UPDATE t_recursos SET descripcion=? WHERE (id_recursos=?)";
                    ps = ad.getCon().prepareStatement(SQL2);
                    ps.setString(1, d);
                    ps.setInt(2, id);
                }
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RecursosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public String RecursosEliminarAdm(Integer id, Integer numRecursos, AccesoDatos ad) throws SQLException {
        String resp = "", SQL = "";
        PreparedStatement ps = null;
        try {
            if (numRecursos == 0) {
                SQL = "delete from t_recursos where (id_recursos=?)";
                resp = "ok";
            } else {
                SQL = "Update t_recursos SET estado='D' where (id_recursos=?)";
                resp = "ok";
            }
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ps.executeUpdate();
            ad.getCon().commit();
//            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RecursosGestionAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    public String RecursoHabilitarAdm(Integer id, AccesoDatos ad) throws SQLException {
        String resp = "", SQL = "";
        try {
            SQL = "Update  t_recursos SET estado='H' WHERE (id_recursos=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ps.executeUpdate();
            resp = "ok";
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RecursosGestionAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    public Integer getNumRecurso(AccesoDatos ad, Integer id) throws SQLException {
        Integer result = 0;
        String SQL = "Select id_recurso from t_subseccion_recursos where(id_recurso=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rsRecursos = ps.executeQuery();
            while (rsRecursos.next()) {
                result++;
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RecursosGestionAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }
}
