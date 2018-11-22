/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.recursos.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.observaciones.ad.ObservacionesAD;
import dda.silabo.estructura.unidad.recursos.comunes.Recursos;
import dda.silabo.observaciones.ad.ObservacionAD;
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
public class RecursosAD extends Recursos {

    public String RecursosSQLSelect(String rol, Integer idSilabo, Integer idUnidad) {
        String SQL = "";
        if (!rol.equals("Adm")) {
            SQL = "select tsr.id_recurso,tr.descripcion,tr.estado,tr.id_recursos from t_recursos AS tr\n"
                    + "                   left join\n"
                    + "                   t_subseccion_recursos AS tsr\n"
                    + "                   on tsr.id_recurso= tr.id_recursos and tsr.id_silabo='" + idSilabo + "' and tsr.id_unidad='" + idUnidad + "'\n"
                    + "                   where tr.estado='H'";
        } else {
            SQL = "SELECT\n"
                    + "	r.id_recursos, \n"
                    + "	r.descripcion, \n"
                    + "	r.estado\n"
                    + " FROM\n"
                    + "	t_recursos AS r";
        }
        return SQL;
    }

    public void RecursosCargar(AccesoDatos ad, String SQL, Silabo silabo) throws SQLException {
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsRecursos = ps.executeQuery();
            while (rsRecursos.next()) {
                RecursoAD recurso = new RecursoAD();
                recurso.RecursosCargar(rsRecursos, silabo.getRol());
                this.addRecurso(recurso);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            Logger.getLogger("RecursosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        this.setSilabos(silabo);
        this.setAyuda("<div style='text-align:justify'>Son elementos que permiten ejecutar las estrategias metodológicas pueden ser materiales, técnicos o tecnológicos.</div>");
        this.setTitulo("Recursos");

    }

    public void obtenerObservaciones(String descripcion, Silabo silabo, AccesoDatos ad) throws SQLException {
        ObservacionesAD observacion = new ObservacionesAD();
        observacion.getListaObservacionesSubseccion(descripcion, silabo, 3, ad);
        this.setObservacion(observacion);
        this.getObservaciones().clear();
    }

    public void guardarRecursos(Integer idSilabo, Integer idUnidad) throws SQLException {
        AccesoDatos ad = new AccesoDatos();
        try {
            ad.Connectar();
            for (int i = 0; i < this.getRecursos().size(); i++) {
                Integer id = this.getRecursos().get(i).getIdRecurso();
                String SQL = "INSERT INTO t_subseccion_recursos (id_recurso,id_silabo,id_unidad) VALUES (?,?,?)";
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, id);
                ps.setInt(2, idSilabo);
                ps.setInt(3, idUnidad);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }

        } catch (SQLException e) {
            ad.getCon().rollback();
            Logger.getLogger("RecursosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminarRecursosImportacion(AccesoDatos ad, Integer idSilabo, Integer idUnidad) throws SQLException {
        try {
            String SQL = "DELETE FROM t_subseccion_recursos where (id_silabo=? and id_unidad=?)";
            ad.Connectar();
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.setInt(2, idUnidad);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            Logger.getLogger("RecursosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void importarRecursos(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad, Integer unidadAnterior) throws SQLException {
        ObservacionAD observacion = new ObservacionAD();
        eliminarRecursosImportacion(ad, silaboActual.getIdSilabo(), silaboActual.getIdUnidad());
        String SQL = "insert into t_subseccion_recursos (id_recurso,id_silabo,id_unidad)\n"
                + "select id_recurso,'" + silaboActual.getIdSilabo() + "','" + silaboActual.getIdUnidad() + "' from t_subseccion_recursos \n"
                + "where (id_silabo =? and id_unidad=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.setInt(2, unidadAnterior);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
            observacion.updateSubseccionSilabo(ad, silaboActual, 3, "Corregido");
        } catch (SQLException e) {
            ad.getCon().rollback();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }
}
