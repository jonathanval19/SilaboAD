/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.logros.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.logros.comunes.Logro;
import dda.silabo.estructura.unidad.logros.comunes.Logros;
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
public class LogrosAD extends Logros {

    public String logroSQLSelect(Silabo silabo) {
        String SQL = "select * from t_subseccion_logros where id_silabo='" + silabo.getIdSilabo() + "' and id_unidad='" + silabo.getIdUnidad() + "'";
        return SQL;
    }

    public void obtenerLogros(AccesoDatos ad, String SQL) throws SQLException {
        try {
            try (PreparedStatement ps = ad.getCon().prepareStatement(SQL)) {
                ResultSet rsLogro = ps.executeQuery();
                while (rsLogro.next()) {
                    LogroAD logro = new LogroAD();
                    logro.getLogro(rsLogro);
                    this.getLogros().add(logro);
                }
                ad.getCon().commit();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void obtenerObservaciones(Silabo silabo, AccesoDatos ad) throws SQLException {
        this.setAyuda("<div style='text-align:justify'>Se deben ingresar los logros que se van a cumplir en el período vigente de la unidad</div>");
        this.setTitulo("Logros de Unidad");
        ObservacionesAD observacion = new ObservacionesAD();
        String descripcion = silabo.getTipo();
        observacion.getListaObservacionesSubseccion(descripcion, silabo, 6, ad);
        this.setObservacion(observacion);
        this.setSilabos(silabo);
    }

    public void LogroGuardar(AccesoDatos ad, Integer idSilabo, Integer idUnidad) throws SQLException {
        try {
            String SQL = "";
            PreparedStatement ps = null;
            for (Logro l : this.getLogros()) {

                if (l.getIdLogro() == 0) {
                    SQL = "insert into t_subseccion_logros (descripcion,id_silabo,id_unidad ) values (?,?,?)";
                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setString(1, l.getDescripcion());
                    ps.setInt(2, idSilabo);
                    ps.setInt(3, idUnidad);

                } else {
                    SQL = "UPDATE t_subseccion_logros\n"
                            + "   SET descripcion=?\n"
                            + " WHERE id_logro=? ";
                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setString(1, l.getDescripcion());
                    ps.setInt(2, l.getIdLogro());
                }
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();

            }

        } catch (SQLException e) {
            Logger.getLogger("LogrosAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.logros.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void eliminarLogro(AccesoDatos ad, Integer idlogro) throws SQLException {
        String SQL = "delete from t_subseccion_logros where (id_logro=?)";
        try {
            if (ad.Connectar() != 0) {
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, idlogro);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("LogrosAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.logros.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());

        }
    }

    public void eliminarLogroImportacion(AccesoDatos ad, Integer idSilabo, Integer numeroUnidad) throws SQLException {
        String SQL = "delete from t_subseccion_logros where (id_silabo=? and id_unidad=?)";
        try {
            if (ad.Connectar() != 0) {
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, idSilabo);
                ps.setInt(2, numeroUnidad);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("LogrosAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.logros.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());

        }
    }

    public void importarLogros(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad, Integer unidadAnterior) throws SQLException {
        ObservacionAD observacion = new ObservacionAD();
        eliminarLogroImportacion(ad, silaboActual.getIdSilabo(), silaboActual.getIdUnidad());
        String SQL = "insert into t_subseccion_logros (descripcion,id_silabo,id_unidad)\n"
                + "select descripcion,'" + silaboActual.getIdSilabo() + "','" + silaboActual.getIdUnidad() + "' from t_subseccion_logros \n"
                + "where (id_silabo =? and id_unidad=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.setInt(2, unidadAnterior);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
            observacion.updateSubseccionSilabo(ad, silaboActual, 7, "Corregido");
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
