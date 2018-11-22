/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.menulateral.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidadinformacion.ln.EstructuraDesarrolloLN;
import dda.silabo.menulateral.comunes.MenuLateral;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class MenuLateralAD extends MenuLateral {

    public void getSeccionesSilabo(Integer idSilabo, AccesoDatos ad) throws SQLException {
        String SQL = "select ds.descripcion, s.estado \n"
                + "from t_descripcion_seccion as ds join\n"
                + "t_secciones as s\n"
                + "on ds.id_descripcion = s.id_descripcion\n"
                + "where (s.id_silabo =? )";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);

            ResultSet rsSecciones = ps.executeQuery();
            while (rsSecciones.next()) {
                SeccionSilaboAD seccionsilabo = new SeccionSilaboAD();
                seccionsilabo.asignarSecciones(rsSecciones);
                this.getSecciones().add(seccionsilabo);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void getSeccionesAsignatura(AccesoDatos ad) throws SQLException {
        String SQL = "select descripcion from t_descripcion_seccion";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsSecciones = ps.executeQuery();
            while (rsSecciones.next()) {
                SeccionSilaboAD seccionsilabo = new SeccionSilaboAD();
                seccionsilabo.asignarSeccionesAsignatura(rsSecciones);
                this.getSecciones().add(seccionsilabo);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void getSubseccionesSilabo(Integer idSilabo, AccesoDatos ad, String rolActivo) {

        try {
            for (int i = 1; i <= this.getNumUnidades(); i++) {
                UnidadAD unidadAD = new UnidadAD();
                unidadAD.agregarUnidad(i, ad, idSilabo, rolActivo);
                this.getUnidades().add(unidadAD);
            }
            long countCorregir = this.getUnidades().stream()
                    .filter(t -> t.getEstado().equals("fa fa-exclamation-triangle tag-warning"))
                    .count();
            long countCorregido = this.getUnidades().stream()
                    .filter(t -> t.getEstado().equals("fa fa-question"))
                    .count();
            long countAprobado = this.getUnidades().stream()
                    .filter(t -> t.getEstado().equals("fa fa-check"))
                    .count();
            if (countAprobado == this.getNumUnidades()) {
                this.setEstado("fa fa-check");
            } else if (countCorregido > 0 && !rolActivo.equals("Doc")) {
                this.setEstado("fa fa-question");
            } else if (countCorregir > 0 && rolActivo.equals("Doc")) {
                this.setEstado("fa fa-exclamation-triangle tag-warning");
            } else {
                this.setEstado("Inicio");
            }
        } catch (Exception e) {
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void getSubseccionesAsignatura(AccesoDatos ad) throws SQLException {
        String SQL = "select descripcion from t_descripcion_subseccion";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsSubsecciones = ps.executeQuery();
            while (rsSubsecciones.next()) {
                SeccionSilaboAD seccionsilabo = new SeccionSilaboAD();
                seccionsilabo.asignarSubseccionesAsignatura(rsSubsecciones);
                this.getSubsecciones().add(seccionsilabo);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void getNumeroUnidades(String jsonAsignaturaInfo, Integer idSilabo, AccesoDatos ad) throws SQLException {
        int numUnidades = 0;
        try {
            String SQL = "select id_unidad from t_unidades where id_silabo=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsUnidades = ps.executeQuery();
            while (rsUnidades.next()) {
                numUnidades++;
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        if (numUnidades == 0) {
            EstructuraDesarrolloLN estructura = new EstructuraDesarrolloLN();
            numUnidades = estructura.getNumeroUnidades(jsonAsignaturaInfo);
        }
        this.setNumUnidades(numUnidades);
    }

    public void getRolMenu(String rol) {
        this.setRol(rol);
    }

    public void updateSubseccionesMenuSilabo(Silabo silabo, int idSubseccion, AccesoDatos ad, String estado) throws SQLException {
        String SQL = "UPDATE t_unidad_subsecciones SET estado=? WHERE (id_descripcion=? and id_silabo=? and id_unidad=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, estado);
            ps.setInt(2, idSubseccion);
            ps.setInt(3, silabo.getIdSilabo());
            ps.setInt(4, silabo.getIdUnidad());
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void updateSeccionesMenuSilabo(Silabo silabo, Integer idSeccion, AccesoDatos ad, String estado) throws SQLException {
        String SQL = "UPDATE t_secciones SET estado=? WHERE (id_descripcion=? and id_silabo=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, estado);
            ps.setInt(2, idSeccion);
            ps.setInt(3, silabo.getIdSilabo());
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public String getEstadoSeccion(Integer idSilabo, Integer idSeccion, AccesoDatos ad, String tipoAccion) {
        String result = "desconocido";
        try {
            String SQL = "select estado from t_secciones  AS ts\n"
                    + "JOIN t_descripcion_seccion AS td\n"
                    + "on ts.id_descripcion = td.id_descripcion and ts.id_descripcion=?\n"
                    + "where id_silabo=?";
            if (tipoAccion.equals("subseccion")) {
                SQL = "select estado from t_unidad_subsecciones  AS ts\n"
                        + "JOIN t_descripcion_subseccion AS td\n"
                        + "on ts.id_descripcion = td.id_descripcion and ts.id_descripcion=?\n"
                        + "where id_silabo=?";
            }
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSeccion);
            ps.setInt(2, idSilabo);
            ResultSet rsUnidades = ps.executeQuery();
            while (rsUnidades.next()) {
                result = rsUnidades.getString("estado");
            }
        } catch (SQLException e) {
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public void updateEstadoSilabo(Integer idSilabo, AccesoDatos ad) throws SQLException {
        String estado = "Inicio";
        long countCorregir = this.getSecciones().stream()
                .filter(t -> t.getEstado().equals("Corregir"))
                .count();
        long countCorregido = this.getSecciones().stream()
                .filter(t -> t.getEstado().equals("Corregido"))
                .count();
        long countAprobado = this.getSecciones().stream()
                .filter(t -> t.getEstado().equals("Aprobado"))
                .count();

        long countInicio = this.getSecciones().stream()
                .filter(t -> t.getEstado().equals("Inicio"))
                .count();
        if (this.getEstado() != null) {
            if (this.getEstado().equals("fa fa-check")) {
                countAprobado++;
            } else if (this.getEstado().equals("fa fa-question")) {
                countCorregido++;
            } else if (this.getEstado().equals("fa fa-exclamation-triangle tag-warning")) {
                countCorregir++;
            } else {
                countInicio++;
            }
        }
        if (countCorregir > 0) {
            estado = "Corregir";
        } else if (countCorregido > 0 && countCorregir == 0) {
            estado = "Corregido";
        } else if (countAprobado > 0 && countCorregir == 0 && countCorregido == 0 && countInicio == 0) {
            estado = "Aprobado";
        }

        String SQL = "UPDATE t_silabo_estados SET estado=? WHERE (id_silabo=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, estado);
            ps.setInt(2, idSilabo);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("MenuLateralAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }

    }

}
