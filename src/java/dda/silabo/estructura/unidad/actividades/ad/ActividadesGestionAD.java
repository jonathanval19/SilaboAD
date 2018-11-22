/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.actividades.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.actividades.comunes.Actividad;
import dda.silabo.estructura.unidad.actividades.comunes.Actividades;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Usuario
 */
public class ActividadesGestionAD extends Actividades {

    public void guardarActividades(AccesoDatos ad) throws SQLException {
        String resp = "";
        String SQL = "";
        try {
            for (Actividad principal : this.getActividades()) {
                int idPadre = ingresarnuevaActividad(principal, ad, 0, "Adm");
                for (Actividad nivel2 : principal.getNivel2()) {
                    ingresarnuevaActividad(nivel2, ad, idPadre, "Adm");
                }
            }
        } catch (Exception e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public String deleteActividad(Integer id, AccesoDatos ad, int numActividad) throws SQLException {
        String resp = "", SQL = "";
        if (numActividad == 0) {
            SQL = "DELETE FROM t_actividades_aprendizaje WHERE (id_actividades_aprendizaje=?) ";
            resp = "ok";
        } else {
            SQL = "Update  t_actividades_aprendizaje SET estado='D' WHERE (id_actividades_aprendizaje=?)";
            resp = "ok";
        }
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return resp;
    }

    public String habilitarActividad(Integer id, AccesoDatos ad) throws SQLException {
        String resp = "", SQL = "";
        SQL = "Update  t_actividades_aprendizaje SET estado='H' WHERE (id_actividades_aprendizaje=?)";
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
        }
        return resp;
    }

    public Integer getNumActividad(AccesoDatos ad, Integer id) throws SQLException {
        int result = 0;
        String SQL = "Select id_act_aprend from t_subseccion_actividad_aprendizaje where(id_act_aprend=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rsActividades = ps.executeQuery();
            while (rsActividades.next()) {
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

    public int ingresarnuevaActividad(Actividad principal, AccesoDatos ad, int idPadre, String rol) {
        int result = 0;
        try {
            String SQL = "";
            Integer id = principal.getId_actividades_aprendizaje();
            String tipo = principal.getTipo_actividad();
            String desc = principal.getDescripcion();
            PreparedStatement ps = null;
            if (id == 0) {
                SQL = "INSERT INTO t_actividades_aprendizaje(descripcion,tipo_actividad,estado,idpadre,rol) VALUES (?,?,?,?,?) returning id_actividades_aprendizaje";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, desc);
                ps.setString(2, tipo);
                ps.setString(3, "H");
                ps.setInt(4, idPadre);
                ps.setString(5, rol);
                ResultSet rsActividad = ps.executeQuery();
                while (rsActividad.next()) {
                    result = rsActividad.getInt("id_actividades_aprendizaje");
                }
            } else {
                result = id;
                SQL = " UPDATE t_actividades_aprendizaje SET descripcion=? WHERE (id_actividades_aprendizaje=?)";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, desc);
                ps.setInt(2, id);
                ps.executeUpdate();
                ad.getCon().commit();
            }

            ps.close();
        } catch (SQLException e) {
        }
        return result;
    }
}
