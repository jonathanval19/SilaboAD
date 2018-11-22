/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.roles.ad;

import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.roles.comunes.Roles;
import dda.silabo.usuarios.ad.UsuariosAD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adry
 */
public class RolesAD extends Roles {

    public void getRolesUsuarios(AccesoDatos ad, List<EntidadUnidos> entidades, String rolActivo) throws SQLException {
        String SQL = "";
        Integer idEntidad = 0;
        if (entidades.size() > 0) {
            idEntidad = Integer.parseInt(entidades.get(0).getId());
        }
        try {
            SQL = "SELECT tr.* from t_roles as tr\n"
                    + "where tr.id_tipo_entidad>? and tr.rol_char!='Gen' order by tr.id_tipo_entidad";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idEntidad);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                RolAD rolAD = new RolAD();
                rolAD.agregarRolEntidad(rsRoles);
                this.getRoles().add(rolAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        this.setTitulo("Roles del Sistema");
    }

    public void GuardarRoles(AccesoDatos ad) throws SQLException {
        String SQL2 = "";
        try {
            if (this.getRoles().size() > 0) {
                PreparedStatement ps = null;
                for (int i = 0; i < this.getRoles().size(); i++) {
                    String descRol = this.getRoles().get(i).getDescRol();
                    String rolChar = descRol.substring(0, 3);
                    if (this.getRoles().get(i).getIdRol() == 0) {
                        SQL2 = "INSERT INTO t_roles (descripcion,estado,rol_char) VALUES (?,?,?)";
                        ps = ad.getCon().prepareStatement(SQL2);
                        ps.setString(1, descRol);
                        ps.setString(2, "H");
                        ps.setString(3, rolChar);
                    } else {
                        SQL2 = " UPDATE t_roles SET descripcion=?, rol_char=? WHERE (id_rol=?)";
                        ps = ad.getCon().prepareStatement(SQL2);
                        ps.setString(1, this.getRoles().get(i).getDescRol());
                        ps.setString(2, rolChar);
                        ps.setInt(3, this.getRoles().get(i).getIdRol());
                    }
                    ps.executeUpdate();
                    ps.close();

                }
            }
            ad.getCon().commit();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RolUsuarioAD").log(Level.SEVERE, "dda.silabo.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
