/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.estrategias.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.estrategias.comunes.Estrategias;
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
public class EstrategiasAD extends Estrategias {

    public String EstrategiasSQLSelect(String rol, Integer idSilabo, Integer idUnidad) {
        String SQL = "";
        if (!rol.equals("Adm")) {
            SQL = "select tse.id_est_met,te.descripcion,te.estado,te.id_estrategia from t_estrategias_metodologicas AS te\n"
                    + "                   left join\n"
                    + "                   t_subseccion_estrategias_met AS tse\n"
                    + "                   on tse.id_est_met= te.id_estrategia and tse.id_silabo='" + idSilabo + "' and tse.id_unidad='" + idUnidad + "'\n"
                    + "                   where te.estado='H'";
        } else {
            SQL = "select te.id_estrategia, te.descripcion, te.estado \n"
                    + "  from t_estrategias_metodologicas as te";
        }
        return SQL;
    }

    public void EstrategiasCargar(AccesoDatos ad, String SQL, Silabo silabo) throws SQLException {
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsEstrategia = ps.executeQuery();
            while (rsEstrategia.next()) {
                EstrategiaAD estrategia = new EstrategiaAD();
                estrategia.getEstrategia(rsEstrategia, silabo.getRol());
                this.getEstrategias().add(estrategia);
            }
            ad.getCon().commit();
            ps.close();
            this.setSilabos(silabo);
            this.setAyuda("<div style='text-align:justify'>Jones F. Palincsar A. define a  las estrategias de aprendizaje como el conjunto de actividades, técnicas y medios que se planifican de acuerdo con las necesidades de los estudiantes, los objetivos… a más de lo expuesto,  es de fundamental importancia orientar la selección de las estrategias metodológicas en función de la destrezas a desarrollarse, pudiendo utilizarse alternativas como: ERCA, pensamiento crítico, métodos de las aprendizaje significativo, didácticas específicas de cada área de estudio, etc.</div>");
            this.setTitulo("Estrategias Metodológicas");
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstrategiasAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void obtenerObservaciones(Silabo silabo, String descripcion, AccesoDatos ad) throws SQLException {
        ObservacionesAD observacion = new ObservacionesAD();
        observacion.getListaObservacionesSubseccion(descripcion, silabo, 2, ad);
        this.setObservacion(observacion);
    }

    public void EstrategiasGuardar(AccesoDatos ad, Integer idSilabo, Integer idUnidad, int idPadre) {
        try {
            for (int i = 0; i < this.getEstrategias().size(); i++) {
                Integer id = this.getEstrategias().get(i).getId_estrategia();
                EstrategiaAD estrategia = new EstrategiaAD();
                estrategia.insertarEstrategia(ad, idSilabo, idUnidad, id, idPadre);
            }
        } catch (Exception e) {
            Logger.getLogger("EstrategiasAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminarEstrategiasImportacion(AccesoDatos ad, Integer idSilabo, Integer idUnidad, int idPadre) throws SQLException {
        try {
            String SQL = "";
            PreparedStatement ps = null;

            SQL = "DELETE FROM t_subseccion_estrategias_met where (id_silabo=? and id_unidad=? and padre=?)";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.setInt(2, idUnidad);
            ps.setInt(3, idPadre);

            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstrategiasAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void importarEstrategias(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad, Integer unidadAnterior) throws SQLException {
        ObservacionAD observacion = new ObservacionAD();
        eliminarEstrategiasImportacion(ad, silaboActual.getIdSilabo(), silaboActual.getIdUnidad(), 0);
        String SQL = "insert into t_subseccion_estrategias_met (id_est_met,id_silabo,id_unidad)\n"
                + "select id_est_met,'" + silaboActual.getIdSilabo() + "','" + silaboActual.getIdUnidad() + "' from t_subseccion_estrategias_met \n"
                + "where (id_silabo =?and id_unidad=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.setInt(2, unidadAnterior);
            ps.executeUpdate();
            observacion.updateSubseccionSilabo(ad, silaboActual, 4, "Corregido");
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstrategiasAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }
}
