/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.objetivos.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.objetivos.comunes.Objetivo;
import dda.silabo.estructura.unidad.objetivos.comunes.Objetivos;
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
public class ObjetivosAD extends Objetivos {

    public String ObjetivoSQLSelect(Silabo silabo) {
        String SQL = "select * from t_subseccion_objetivos where id_silabo='" + silabo.getIdSilabo() + "' and id_unidad='" + silabo.getIdUnidad() + "'";
        return SQL;
    }

    public void ObjetivoCargar(AccesoDatos ad, String SQL) throws SQLException {
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);

            ResultSet rsObjetivo = ps.executeQuery();
            while (rsObjetivo.next()) {
                ObjetivoAD objetivo = new ObjetivoAD();
                objetivo.ObjetivoCargar(rsObjetivo);
                this.getObjetivos().add(objetivo);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger("ObjetivosAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.objetivos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            ad.getCon().rollback();
            ad.Desconectar();
            ad.Desconectar();
        }
    }

    public void obtenerObservaciones(Silabo silabo, AccesoDatos ad) throws SQLException {
        this.setAyuda("<div style='text-align:justify'>Se debe  ingresar los objetivos que se quiere conseguir  en la unidad</div>");
        this.setTitulo("Objetivos de Unidad");
        String descripcion = silabo.getTipo();
        ObservacionesAD observacionesAD = new ObservacionesAD();
        observacionesAD.getListaObservacionesSubseccion(descripcion, silabo, 1, ad);
        this.setObservacion(observacionesAD);
        this.setSilabos(silabo);
    }

    public void eliminarObjetivoImportacion(AccesoDatos ad, Integer idSilabo, Integer nuemroUnidad) throws SQLException {
        String SQL = "delete from t_subseccion_objetivos where (id_silabo=? and id_unidad=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.setInt(2, nuemroUnidad);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger("ObjetivosAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.objetivos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void eliminarObjetivo(AccesoDatos ad, Integer idObjetivo) throws SQLException {
        String SQL = "delete from t_subseccion_objetivos where (id_objetivos=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idObjetivo);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger("ObjetivosAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.objetivos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void ObjetivoGuardar(AccesoDatos ad, Integer idSilabo, Integer idUnidad) throws SQLException {
        try {
            String SQL = "";
            PreparedStatement ps = null;
            for (Objetivo o : this.getObjetivos()) {
                if (o.getIdObjetivo() == 0) {
                    SQL = "insert into t_subseccion_objetivos (descripcion,id_silabo,id_unidad )values (?,?,?)";
                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setString(1, o.getDescripcion());
                    ps.setInt(2, idSilabo);
                    ps.setInt(3, idUnidad);
                } else {
                    SQL = "UPDATE t_subseccion_objetivos\n"
                            + "   SET descripcion='" + o.getDescripcion() + "'\n"
                            + " WHERE id_objetivos='" + o.getIdObjetivo() + "'";
                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setString(1, o.getDescripcion());
                    ps.setInt(2, o.getIdObjetivo());
                }
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("ObjetivosAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.objetivos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void importarObjetivos(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad, Integer unidadAnterior) throws SQLException {
        ObservacionAD observacion = new ObservacionAD();
        eliminarObjetivoImportacion(ad, silaboActual.getIdSilabo(), silaboActual.getIdUnidad());
        String SQL = "insert into t_subseccion_objetivos (descripcion,id_silabo,id_unidad)\n"
                + "select descripcion,'" + silaboActual.getIdSilabo() + "','" + silaboActual.getIdUnidad() + "' from t_subseccion_objetivos \n"
                + "where (id_silabo =? and id_unidad=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.setInt(2, unidadAnterior);
            ps.executeUpdate();
            observacion.updateSubseccionSilabo(ad, silaboActual, 2, "Corregido");
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger("ObjetivosAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.objetivos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

}
