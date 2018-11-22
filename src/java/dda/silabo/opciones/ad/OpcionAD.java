/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.opciones.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.opciones.comunes.Opcion;
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
public class OpcionAD extends Opcion {

    public void getOpcionRol(ResultSet rsOpciones) {
        try {
            this.setDescRol(rsOpciones.getString("descripcion"));
            this.setIdOpcion(rsOpciones.getInt("id_opcion"));
            this.setEstado(rsOpciones.getString("estado"));
            int op = rsOpciones.getInt("id_rol");
            if (op != 0) {
                this.setCheck("checked");
            }
        } catch (SQLException e) {

        }
    }

    public void deleteOpcion(AccesoDatos ad, int numOpcion, int idOpcion) throws SQLException {
        String SQL = "";
        try {
            if (numOpcion == 0) {
                SQL = "DELETE FROM t_opciones WHERE (id_opcion=?)";
            } else {
                SQL = "Update  t_opciones SET estado='D' WHERE (id_opcion='?')";
            }
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idOpcion);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RecursosGestionAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public Integer existeOpcionAsignada(AccesoDatos ad, int idOpcion) throws SQLException {
        Integer result = 0;
        String SQL = "Select id_opcion from t_roles_opciones where(id_opcion=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idOpcion);
            ResultSet rsOpciones = ps.executeQuery();
            while (rsOpciones.next()) {
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

    public void habilitarOpcion(AccesoDatos ad, int idOpcion) throws SQLException {
        String SQL = "";
        try {
            SQL = "Update  t_opciones SET estado='H' WHERE (id_opcion=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idOpcion);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RecursosGestionAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    void ReporteOpcionesUsuario(ResultSet rs) {
        try {
            this.setIdOpcion(rs.getInt("id_tipo_entidad"));
            this.setDescRol(rs.getString("descripcion"));
        } catch (Exception e) {
        }
    }
}
