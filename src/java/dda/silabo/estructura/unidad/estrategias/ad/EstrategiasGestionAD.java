/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.estrategias.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.estrategias.comunes.Estrategias;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jorge Zaruma
 */
public class EstrategiasGestionAD extends Estrategias {

    public String guardarEstrategias(AccesoDatos ad) throws SQLException {
        String resp = "";
        String SQL2 = "";
        try {
            for (int i = 0; i < this.getEstrategias().size(); i++) {
                String d = this.getEstrategias().get(i).getDescripcion();
                Integer id = this.getEstrategias().get(i).getId_estrategia();
                PreparedStatement ps = null;

                if (id == 0) {
                    SQL2 = "INSERT INTO t_estrategias_metodologicas (descripcion, estado) VALUES (?,?)";
                    ps = ad.getCon().prepareStatement(SQL2);
                    ps.setString(1, d);
                    ps.setString(2, "H");
                    resp = "ok";
                } else {
                    SQL2 = " UPDATE t_estrategias_metodologicas SET descripcion=? WHERE (id_estrategia=?)";
                    ps = ad.getCon().prepareStatement(SQL2);
                    ps.setString(1, d);
                    ps.setInt(2, id);
                    resp = "ok";
                }
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    public String deleteEstrategia(Integer id, AccesoDatos ad, Integer numEstrategia) throws SQLException {
        String resp = "", SQL = "";
        if (numEstrategia == 0) {
            SQL = "DELETE FROM t_estrategias_metodologicas WHERE (id_estrategia=? or idpadre=?)";
            resp = "ok";
        } else {
            SQL = "Update  t_estrategias_metodologicas SET estado='D' WHERE (id_estrategia=? or idpadre=?) ";
            resp = "ok";
        }
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ps.setInt(2, id);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    public String habilitarEstrategia(Integer id, AccesoDatos ad) throws SQLException {
        String resp = "", SQL = "";
        SQL = "Update  t_estrategias_metodologicas SET estado='H' WHERE (id_estrategia=?) ";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
            resp = "ok";
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    public Integer getNumEstrategia(AccesoDatos ad, Integer id) throws SQLException {
        int result = 0;
        String SQL = "Select id_est_met from t_subseccion_estrategias_met where(id_est_met=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rsEstrategias = ps.executeQuery();
            while (rsEstrategias.next()) {
                result++;
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

}
