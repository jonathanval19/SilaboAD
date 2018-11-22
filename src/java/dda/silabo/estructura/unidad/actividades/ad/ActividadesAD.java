/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.actividades.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.actividades.comunes.Actividad;
import dda.silabo.estructura.unidad.actividades.comunes.Actividades;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.observaciones.ad.ObservacionesAD;
import dda.silabo.silabo.comunes.Silabo;
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
public class ActividadesAD extends Actividades {

    public String ActividadesSQLSelect(String tipo, String rol, Integer idSilabo, Integer idUnidad, int idpadre) {
        String SQL = "";
        if (!rol.equals("Adm")) {
            if (!tipo.equals("Aula")) {
                SQL = "select tsaa.id_act_aprend,taa.descripcion,taa.estado,taa.tipo_actividad,taa.id_actividades_aprendizaje,taa.rol from t_actividades_aprendizaje AS taa\n"
                        + "                   left join\n"
                        + "                   t_subseccion_actividad_aprendizaje AS tsaa\n"
                        + "                   on tsaa.id_act_aprend = taa.id_actividades_aprendizaje and tsaa.id_silabo='" + idSilabo + "' \n"
                        + "                   where taa.tipo_actividad='" + tipo + "' and taa.estado='H' and idpadre='" + idpadre + "' and tsaa.id_unidad='" + idUnidad + "' order by taa.id_actividades_aprendizaje desc";
            } else if (tipo.equals("Aula") && idpadre == 0) {
                SQL = "select tsaa.id_act_aprend,taa.descripcion,taa.estado,taa.tipo_actividad,taa.id_actividades_aprendizaje,taa.rol from t_actividades_aprendizaje AS taa\n"
                        + "                   left join\n"
                        + "                   t_subseccion_actividad_aprendizaje AS tsaa\n"
                        + "                   on tsaa.id_act_aprend = taa.id_actividades_aprendizaje and tsaa.id_silabo='" + idSilabo + "' \n"
                        + "                   where taa.tipo_actividad='" + tipo + "' and taa.estado='H' and idpadre='" + idpadre + "'  order by taa.id_actividades_aprendizaje desc";
            } else if (tipo.equals("Aula") && idpadre > 0) {
                SQL = "select tsaa.id_act_aprend,taa.descripcion,taa.estado,taa.tipo_actividad,taa.id_actividades_aprendizaje,taa.rol from t_actividades_aprendizaje AS taa\n"
                        + "                   left join\n"
                        + "                   t_subseccion_actividad_aprendizaje AS tsaa\n"
                        + "                   on tsaa.id_act_aprend = taa.id_actividades_aprendizaje and tsaa.id_silabo='" + idSilabo + "' \n"
                        + "                   where taa.tipo_actividad='" + tipo + "' and taa.estado='H' and idpadre='" + idpadre + "' and tsaa.id_unidad='" + idUnidad + "' order by taa.id_actividades_aprendizaje desc";
            }
        } else {
            SQL = "SELECT\n"
                    + "	a.id_actividades_aprendizaje,\n"
                    + "	a.descripcion,\n"
                    + "	a.tipo_actividad, a.estado,\n"
                    + "	a.rol\n"
                    + "	FROM\n"
                    + "	    t_actividades_aprendizaje AS a\n"
                    + "where (a.tipo_actividad = '" + tipo + "' and idpadre='" + idpadre + "' and rol!='Doc')";
        }
        return SQL;
    }

    public void ActividadesAprendizajeCargar(AccesoDatos ad, Silabo silabo) throws SQLException {
        String SQL = ActividadesSQLSelect(silabo.getTipo(), silabo.getRol(), silabo.getIdSilabo(), silabo.getIdUnidad(), 0);
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsActividades = ps.executeQuery();
            while (rsActividades.next()) {
                ActividadAD actividad = new ActividadAD();
                actividad.getActividad(rsActividades, silabo.getRol());
                SQL = ActividadesSQLSelect(silabo.getTipo(), silabo.getRol(), silabo.getIdSilabo(), silabo.getIdUnidad(), actividad.getId_actividades_aprendizaje());
                ps = ad.getCon().prepareStatement(SQL);
                ResultSet rsActividades2 = ps.executeQuery();
                while (rsActividades2.next()) {
                    ActividadAD actividad2 = new ActividadAD();
                    actividad2.getActividad(rsActividades2, silabo.getRol());
                    actividad.getNivel2().add(actividad2);
                }
                this.addActividad(actividad);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        if (silabo.getTipo().equals("Aula")) {
            this.setAyuda("<div style='text-align:justify'>Corresponde a actividades de aprendizaje asistidas por el profesor. Podrán incorporar actividades pedagógicas orientadas a la contextualización, organización, explicación y sistematización del conocimiento científico, técnico. Profesional y humanístico, desarrolladas en diferentes ambientes de aprendizaje.  Y pueden ser: - Actividades de aprendizaje asistido por el profesor - Actividades de aprendizaje colaborativo</div>");
            this.setTitulo("Actividades de Aprendizaje en el Aula");
        } else {

            this.setAyuda("<div style='text-align:justify'>Comprende e l trabajo realizado por el estudiante, orientado al desarrollo de capacidades para e l aprendizaje independiente e individual. Este trabaja será diseñado, planificado y orientado por el profesor, para alcanzar los objetivos y el perfil de egreso de la carrera o programa. Son actividades de aprendizaje autónomo, entre otras: la lectura; el análisis y comprensión de materiales bibliográficos y documentales, tanto analógicos como digitales; la generación de datos y búsqueda de información; la elaboración individual de ensayos, trabajos y exposiciones.</div>");
            this.setTitulo("Actividades de Aprendizaje Autónomas");
        }
        this.setSilabos(silabo);
    }

    public void obtenerObservaciones(String tipo, Silabo silabo, AccesoDatos ad) throws SQLException {
        ObservacionesAD observacion = new ObservacionesAD();
        if (silabo.getTipo().equals("Aula")) {
            observacion.getListaObservacionesSubseccion(tipo, silabo, 4, ad);
            this.setObservacion(observacion);

        } else {
            observacion.getListaObservacionesSubseccion(tipo, silabo, 5, ad);
            this.setObservacion(observacion);
        }
    }

    public void eliminarActividades(AccesoDatos ad, String tipoA, Integer idSilabo, Integer idUnidad) throws SQLException {
        try {
            String SQL = "DELETE FROM t_subseccion_actividad_aprendizaje WHERE (tipo=? and id_silabo=? and id_unidad=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, tipoA);
            ps.setInt(2, idSilabo);
            ps.setInt(3, idUnidad);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminarActividadesUsuario(AccesoDatos ad, Integer id) throws SQLException {
        try {
            String SQL = "DELETE FROM t_actividades_aprendizaje WHERE (id_actividades_aprendizaje=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void ActividadesAprendizajeGuardar(AccesoDatos ad, Integer idSilabo, Integer idUnidad, String tipo) throws SQLException {
        Integer id = 0;
        try {
            PreparedStatement ps = null;
            ActividadesGestionAD aD = new ActividadesGestionAD();
            for (Actividad principal : this.getActividades()) {
                principal.setTipo_actividad(tipo);
                id = principal.getId_actividades_aprendizaje();
                if (id == 0) {
                    id = aD.ingresarnuevaActividad(principal, ad, principal.getIdpadre(), principal.getChv_check());
                }
                String SQL = "INSERT INTO t_subseccion_actividad_aprendizaje (tipo,id_act_aprend,id_silabo,id_unidad) VALUES (?,?,?,?)";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, tipo);
                ps.setInt(2, id);
                ps.setInt(3, idSilabo);
                ps.setInt(4, idUnidad);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }

    }

    public void importarActividades(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad, Integer unidadAnterior) throws SQLException {
        ObservacionAD observacion = new ObservacionAD();
        eliminarActividadesImportacion(ad, silaboActual.getIdSilabo(), silaboActual.getIdUnidad());
        String SQL = "insert into t_subseccion_actividad_aprendizaje (tipo,id_act_aprend,id_silabo,id_unidad)\n"
                + "select tipo,id_act_aprend,'" + silaboActual.getIdSilabo() + "','" + silaboActual.getIdUnidad() + "' from t_subseccion_actividad_aprendizaje   \n"
                + "where (id_silabo =?and id_unidad=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.setInt(2, unidadAnterior);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
            observacion.updateSubseccionSilabo(ad, silaboActual, 5, "Corregido");
            observacion.updateSubseccionSilabo(ad, silaboActual, 6, "Corregido");
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminarActividadesImportacion(AccesoDatos ad, Integer idSilabo, Integer idUnidad) throws SQLException {
        try {
            String SQL = "DELETE FROM t_subseccion_actividad_aprendizaje WHERE (id_silabo=? and id_unidad=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.setInt(2, idUnidad);
            ps.executeUpdate();

            ad.getCon().commit();
            ps.close();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
