/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.entidad.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.roles.comunes.Rol;
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
public class RolUsuarioAD extends Rol {

    public int getRolUsuarioID(int idcedula, AccesoDatos ad) throws SQLException {
        int result = 0;
        String SQL = "select * from t_rol_docente where id_docente =?";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idcedula);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                result++;
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return result;
    }

    public void agregarRoles(String descRol, int idcedula, AccesoDatos ad) throws SQLException {
        int idRol = 0;
        if (selectRolUsuario(descRol, ad) == 0) {
            idRol = insertRolUsuario(descRol, ad);
        } else {
            idRol = selectRolUsuario(descRol, ad);
        }
        if (idRol != 0 && idcedula != 0) {
            insertRolDocente(idcedula, idRol, ad);
        }
    }

    private int selectRolUsuario(String descRol, AccesoDatos ad) throws SQLException {
        int result = 0;
        String SQL = "select * from t_rol_usuario where descripcion = ?";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, descRol);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                result = rsRoles.getInt("id_rol");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return result;
    }

    private int insertRolUsuario(String descRol, AccesoDatos ad) throws SQLException {
        int result = 0;
        try {
            String SQL = "insert into t_rol_usuario (descripcion) values(?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, descRol);
            ps.executeUpdate();
            result = selectRolUsuario(descRol, ad);
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger("RecursosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            ad.getCon().rollback();

            ad.Desconectar();
        }
        return result;
    }

    private void insertRolDocente(int idcedula, int idRol, AccesoDatos ad) throws SQLException {
        try {
            String SQL = "insert into t_rol_docente values (?,?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idcedula);
            ps.setInt(2, idRol);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger("RecursosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void agregarRolDocente(ResultSet rsRoles) {
        try {
            this.setDescRol(rsRoles.getString("descripcion"));
            this.setIdRol(rsRoles.getInt("id_rol"));
        } catch (Exception e) {
            Logger.getLogger("RecursosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
