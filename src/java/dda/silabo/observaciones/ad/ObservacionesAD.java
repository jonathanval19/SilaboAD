/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.observaciones.ad;

import dda.silabo.ad.SilaboAD;
import dda.silabo.db.AccesoDatos;
import dda.silabo.observaciones.comunes.Observacion;
import dda.silabo.observaciones.comunes.Observaciones;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class ObservacionesAD extends Observaciones {

    public List<Observacion> getListaObservacionesSeccion(String tipo, Silabo silabo, Integer idSeccion, AccesoDatos ad) {
        try {

            String SQL = observacionSeccionEstadoSQL(tipo, silabo, "Pendiente", null);
            ObtenerObserbacionPendienteSeccion(ad, SQL);
            ObteberObservacionesCorregidasSeccion(ad, tipo, silabo);
            SilaboAD silaboln = new SilaboAD();
            if (!silabo.getRol().equals("Doc") && silaboln.getEstadoSeccion(silabo, ad, idSeccion).equals("Corregido")) {
                MenuLateralAD menulateralad = new MenuLateralAD();
                menulateralad.updateSeccionesMenuSilabo(silabo, idSeccion, ad, "Aprobado");
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return this.getSecciones();
    }

    public void getObservacionesSeccion(Integer idSilabo, AccesoDatos ad) throws SQLException {
        String SQL = "select * from t_observaciones_secciones where(id_silabo=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsObservacion = ps.executeQuery();

            while (rsObservacion.next()) {
                ObservacionAD observacion = new ObservacionAD();
                observacion.getObservacionSeccion(rsObservacion);
                this.getSecciones().add(observacion);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void getListaObservacionesSubseccion(String tipo, Silabo silabo, Integer idSubseccion, AccesoDatos ad) throws SQLException {
        String SQL = "";
        try {
            SQL = observacionSubseccionEstadoSQL(tipo, silabo, "Pendiente", null);
            ObtenerObserbacionPendienteSubseccion(ad, SQL);
            ObteberObservacionesCorregidasSubseccion(ad, tipo, silabo);
            SilaboAD silaboad = new SilaboAD();
            if (!silabo.getRol().equals("Doc") && silaboad.getEstadoSubseccion(silabo, ad, idSubseccion).equals("Corregido")) {
                MenuLateralAD menulateralad = new MenuLateralAD();
                menulateralad.updateSubseccionesMenuSilabo(silabo, idSubseccion, ad, "Aprobado");
//            menulateralad.updateEstadoSilabo(silabo, ad, "Aprobado");
            }
            this.getSecciones().clear();
            this.getSubsecciones().clear();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void getObservacionesSubseccion(Integer idSilabo, AccesoDatos ad, int numUnidades) throws SQLException {
        for (int i = 0; i < numUnidades; i++) {
            String SQL = "select * from t_observaciones_unidades where (id_silabo=? and id_unidad=?)";
            try {
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, idSilabo);
                ps.setInt(2, (i + 1));
                ResultSet rsObservacion = ps.executeQuery();
                while (rsObservacion.next()) {
                    ObservacionAD observacion = new ObservacionAD();
                    observacion.getObservacionSubseccion(rsObservacion);
                    this.getSubsecciones().add(observacion);
                }
                ad.getCon().commit();
                ps.close();
            } catch (SQLException e) {
                ad.getCon().rollback();
                ad.Desconectar();
                Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
                System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            }
        }
        this.setNumUnidades(numUnidades);
    }

    private String observacionSubseccionEstadoSQL(String tipo, Silabo silabo, String estado, String fecha) {
        String result = "";
        if (estado != null && fecha == null) {
            result = "select t.* \n"
                    + "from t_observaciones_unidades as t  \n"
                    + "where (t.tipo='" + tipo + "' and t.id_silabo='" + silabo.getIdSilabo() + "' and t.id_unidad='" + silabo.getIdUnidad() + "' and estado='" + estado + "') \n"
                    + "order by t.fecha desc";
        } else if (estado == null && fecha == null) {
            result = "select Distinct t.fecha \n"
                    + "from t_observaciones_unidades as t  \n"
                    + "where (t.tipo='" + tipo + "' and t.id_silabo='" + silabo.getIdSilabo() + "' and t.id_unidad='" + silabo.getIdUnidad() + "' and estado !='Pendiente') \n"
                    + "order by t.fecha desc";
        } else if (estado == null && fecha != null) {
            result = "select t.* \n"
                    + "from t_observaciones_unidades as t  \n"
                    + "where (t.tipo='" + tipo + "' and t.id_silabo='" + silabo.getIdSilabo() + "' and t.id_unidad='" + silabo.getIdUnidad() + "' and fecha='" + fecha + "') \n";
        }
        return result;
    }

    private String observacionSeccionEstadoSQL(String tipo, Silabo silabo, String estado, String fecha) {
        String result = "";
        if (estado != null && fecha == null) {
            result = "select t.* \n"
                    + "from t_observaciones_secciones as t  \n"
                    + "where (t.tipo='" + tipo + "' and t.id_silabo='" + silabo.getIdSilabo() + "' and estado='" + estado + "') \n"
                    + "order by t.fecha desc";
        } else if (estado == null && fecha == null) {
            result = "select Distinct t.fecha \n"
                    + "from t_observaciones_secciones as t  \n"
                    + "where (t.tipo='" + tipo + "' and t.id_silabo='" + silabo.getIdSilabo() + "' and estado !='Pendiente') \n"
                    + "order by t.fecha desc";
        } else if (estado == null && fecha != null) {
            result = "select t.* \n"
                    + "from t_observaciones_secciones as t  \n"
                    + "where (t.tipo='" + tipo + "' and t.id_silabo='" + silabo.getIdSilabo() + "' and fecha='" + fecha + "' and estado !='Pendiente') \n";
        }
        return result;
    }

    private void ObtenerObserbacionPendienteSubseccion(AccesoDatos ad, String SQL) throws SQLException {

        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsObservacion = ps.executeQuery();
            while (rsObservacion.next()) {
                ObservacionAD observacion = new ObservacionAD();
                observacion.getObservacionSubseccion(rsObservacion);
                this.setObservacion(observacion);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private void ObteberObservacionesCorregidasSubseccion(AccesoDatos ad, String tipo, Silabo silabo) throws SQLException {
        String SQL = observacionSubseccionEstadoSQL(tipo, silabo, null, null);
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsObservacionFecha = ps.executeQuery();
            while (rsObservacionFecha.next()) {
                String fecha = rsObservacionFecha.getString("fecha");
                FechaObservacionAD fechaObservacionAD = new FechaObservacionAD();
                SQL = observacionSubseccionEstadoSQL(tipo, silabo, null, fecha);
                fechaObservacionAD.agregarObservacionesSubseccion(SQL, ad, fecha);
                if (!fechaObservacionAD.getObservaciones().isEmpty()) {
                    this.getFechas().add(fechaObservacionAD);
                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private void ObtenerObserbacionPendienteSeccion(AccesoDatos ad, String SQL) throws SQLException {

        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsObservacion = ps.executeQuery();
            while (rsObservacion.next()) {
                ObservacionAD observacion = new ObservacionAD();
                observacion.getObservacionSeccion(rsObservacion);
                this.setObservacion(observacion);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private void ObteberObservacionesCorregidasSeccion(AccesoDatos ad, String tipo, Silabo silabo) throws SQLException {
        String SQL = observacionSeccionEstadoSQL(tipo, silabo, null, null);
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsObservacionFecha = ps.executeQuery();
            while (rsObservacionFecha.next()) {
                String fecha = rsObservacionFecha.getString("fecha");
                FechaObservacionAD fechaObservacionAD = new FechaObservacionAD();
                SQL = observacionSeccionEstadoSQL(tipo, silabo, null, fecha);
                fechaObservacionAD.agregarObservacionesSeccion(SQL, ad, fecha);
                if (!fechaObservacionAD.getObservaciones().isEmpty()) {
                    this.getFechas().add(fechaObservacionAD);
                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }
}
