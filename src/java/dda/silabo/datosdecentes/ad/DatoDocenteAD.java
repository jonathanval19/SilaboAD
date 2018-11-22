/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.datosdecentes.ad;

import dda.silabo.datosdocentes.comunes.DatoDocente;
import dda.silabo.datosdocentes.comunes.Titulos;
import dda.silabo.db.AccesoDatos;
import ec.edu.espoch.academico.DictadoMateria;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jorge Zaruma
 */
public class DatoDocenteAD extends DatoDocente {

    void agregarDocentesAsignatura(ResultSet rs, String rol, AccesoDatos ad) throws MalformedURLException, UnsupportedEncodingException, IOException {
        agregarDocente(rs, rol);
        try {
            agregarTitulos(3, ad);
            agregarTitulos(4, ad);
        } catch (Exception e) {
        }
    }

    void agregarDocente(ResultSet rs, String rol) throws MalformedURLException, UnsupportedEncodingException, IOException {
        try {
            this.setNombres(rs.getString("nombres"));
            this.setCedula(rs.getString("cedula"));
            this.setApellidos(rs.getString("apellidos"));
            this.setCorreo(rs.getString("email"));
            this.setTelefono(rs.getString("telefono"));
            this.setId(rs.getInt("id_cedula"));
            this.setRol(rol);
        } catch (SQLException e) {
        }
    }

    void agregarDocenteInicio(DictadoMateria materia) {
        try {
            this.setNombres(materia.getDocente().getNombres());
            this.setApellidos(materia.getDocente().getApellidos());
            this.setCedula(materia.getDocente().getCedula());
        } catch (Exception e) {
        }
    }

    private void agregarTitulos(int nivel, AccesoDatos ad) throws SQLException {
        try {
            String SQL = "SELECT id_usuario, descripcion, nivel, estado, id\n"
                    + "  FROM t_usuario_titulos where id_usuario=? and nivel=?;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getId());
            ps.setInt(2, nivel);
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                TitulosAD titulosAD = new TitulosAD();
                titulosAD.agregarTitulos(rs2);
                if (nivel == 3) {
                    this.getTercerNivel().add(titulosAD);
                } else {
                    this.getCuartoNivel().add(titulosAD);
                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void updateDocente(AccesoDatos ad) throws SQLException {
        try {
            String SQL = "UPDATE t_usuario_titulos\n"
                    + "   SET estado='ingreso'\n"
                    + " WHERE id_usuario=?;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getId());
            ps.executeUpdate();
            ps.close();

            for (Titulos titulos : this.getTercerNivel()) {
                SQL = "UPDATE t_usuario_titulos\n"
                        + "   SET estado='selected'\n"
                        + " WHERE id_usuario=? and id=?;";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, this.getId());
                ps.setInt(2, titulos.getId());
                ps.executeUpdate();
                ps.close();
            }
            for (Titulos titulos : this.getCuartoNivel()) {
                SQL = "UPDATE t_usuario_titulos\n"
                        + "   SET estado='selected'\n"
                        + " WHERE id_usuario=? and id=?;";

                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, this.getId());
                ps.setInt(2, titulos.getId());
                ps.executeUpdate();
                ps.close();
            }
            ad.getCon().commit();
            ps.close();
        } catch (Exception e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }
}
