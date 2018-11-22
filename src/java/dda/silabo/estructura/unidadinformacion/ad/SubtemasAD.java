/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidadinformacion.ad;

import dda.panalitico.ws.SubtemaComunes;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidadinformacion.comunes.Subtemas;
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
public class SubtemasAD extends Subtemas {

    public void agregarSubtema(ResultSet rsSubtemas, AccesoDatos ad) {
        try {
            Integer idS = rsSubtemas.getInt("id_subtema");
            this.setId_temas_subtemas(idS);
            this.setDescripcion(rsSubtemas.getString("subtema"));
            this.setSistema(rsSubtemas.getString("sistema"));
        } catch (SQLException e) {
            Logger.getLogger("SubtemasAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidadinformacion.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void ingresarSubtemas(AccesoDatos ad, Integer idTema, Subtemas subtema, String unidadSistema) throws SQLException {
        String SQL2 = "";
        try {
            PreparedStatement ps = null;
            if (subtema.getSistema() != null) {
                unidadSistema = subtema.getSistema();
            }
            if (unidadSistema.equals("A") && subtema.getSistema() == null) {
                SQL2 = "INSERT INTO t_subtemas (subtema,id_tema,sistema) VALUES (?,?,?)";
                ps = ad.getCon().prepareStatement(SQL2);
                ps.setString(1, subtema.getDescripcion());
                ps.setInt(2, idTema);
                ps.setString(3, unidadSistema);
                ps.executeUpdate();
                ps.close();
            }
            if (subtema.getId_temas_subtemas() == 0 && unidadSistema.equals("S")) {
                SQL2 = "INSERT INTO t_subtemas (subtema,id_tema,sistema) VALUES (?,?,?)";
                ps = ad.getCon().prepareStatement(SQL2);
                ps.setString(1, subtema.getDescripcion());
                ps.setInt(2, idTema);
                ps.setString(3, unidadSistema);
                ps.executeUpdate();
                ps.close();
            }
            if (subtema.getId_temas_subtemas() > 0 && unidadSistema.equals("S")) {
                SQL2 = " UPDATE t_subtemas SET subtema=? WHERE (id_tema=? and id_subtema=?)";
                ps = ad.getCon().prepareStatement(SQL2);
                ps.setString(1, subtema.getDescripcion());
                ps.setInt(2, idTema);
                ps.setInt(3, subtema.getId_temas_subtemas());
                ps.executeUpdate();
                ps.close();
            }

            ad.getCon().commit();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminarSubtemasTema(Integer idTema, AccesoDatos ad) throws SQLException {
        try {
            String SQLDelete = "DELETE FROM t_subtemas WHERE (id_tema=?)";

            PreparedStatement ps = ad.getCon().prepareStatement(SQLDelete);
            ps.setInt(1, idTema);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void SubtemaEliminar(Integer id_temas_subtemas, AccesoDatos ad) throws SQLException {
        try {
            String SQLDelete = "DELETE FROM t_subtemas WHERE (id_subtema=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQLDelete);
            ps.setInt(1, id_temas_subtemas);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    void IngresarContenidosImportados(AccesoDatos ad, Integer idTema, SubtemaComunes subtema, String unidadSistema) throws SQLException {
        try {
            String SQL = "INSERT INTO t_subtemas(\n"
                    + "            subtema, id_tema, sistema)\n"
                    + "    VALUES (?,?,?);";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, subtema.getDesc());
            ps.setInt(2, idTema);
            ps.setString(3, unidadSistema);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
