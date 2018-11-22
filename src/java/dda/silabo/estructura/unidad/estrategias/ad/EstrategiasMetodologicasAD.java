/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.estrategias.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.estrategias.comunes.Estrategia;
import dda.silabo.estructura.unidad.estrategias.comunes.EstrategiasMetodologicas;
import dda.silabo.observaciones.ad.ObservacionesAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class EstrategiasMetodologicasAD extends EstrategiasMetodologicas {

    public String guardarEstrategias(AccesoDatos ad) throws SQLException {
        String resp = "";
        for (Estrategia nivel1 : this.getNivel1()) {
            Integer idpadre2 = accionesGuardarEstrategias(ad, nivel1, 0);
            for (Estrategia nivel2 : nivel1.getNivel2()) {
                Integer idpadre3 = accionesGuardarEstrategias(ad, nivel2, idpadre2);
                for (Estrategia nivel3 : nivel2.getNivel3()) {
                    accionesGuardarEstrategias(ad, nivel3, idpadre3);
                }
            }
        }
        return resp;
    }

    public String EstrategiasSQLSelect(String rol, Integer idSilabo, Integer idUnidad, String nivel, int idpadre) {
        String SQL = "";
        if (!rol.equals("Adm")) {
            SQL = "select tse.id_est_met,te.* from t_estrategias_metodologicas AS te\n"
                    + "                   left join\n"
                    + "                   t_subseccion_estrategias_met AS tse\n"
                    + "                   on tse.id_est_met= te.id_estrategia and tse.id_silabo='" + idSilabo + "' and tse.id_unidad='" + idUnidad + "'\n"
                    + "                   where te.estado='H' and nivel='" + nivel + "' and idpadre='" + idpadre + "'";
        } else {
            SQL = "SELECT id_estrategia, descripcion, estado, nombre, idpadre, nivel, 0 as id_est_met\n"
                    + "  FROM t_estrategias_metodologicas\n"
                    + "  where nivel='" + nivel + "' and idpadre='" + idpadre + "'";
        }
        return SQL;
    }

    public void EstrategiasCargar(AccesoDatos ad, Silabo silabo) throws SQLException {
        try {
            String SQL = EstrategiasSQLSelect(silabo.getRol(), silabo.getIdSilabo(), silabo.getIdUnidad(), "nivel1", 0);
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsEstrategia = ps.executeQuery();
            while (rsEstrategia.next()) {
                EstrategiaAD estrategia = new EstrategiaAD();
                estrategia.getEstrategia(rsEstrategia, "Adm");

                SQL = EstrategiasSQLSelect(silabo.getRol(), silabo.getIdSilabo(), silabo.getIdUnidad(), "nivel2", estrategia.getId_estrategia());
                ps = ad.getCon().prepareStatement(SQL);
                ResultSet rsEstrategia2 = ps.executeQuery();
                while (rsEstrategia2.next()) {
                    EstrategiaAD estrategia2 = new EstrategiaAD();
                    estrategia2.getEstrategia(rsEstrategia2, "Adm");
                    SQL = EstrategiasSQLSelect(silabo.getRol(), silabo.getIdSilabo(), silabo.getIdUnidad(), "nivel3", estrategia2.getId_estrategia());
                    ps = ad.getCon().prepareStatement(SQL);
                    ResultSet rsEstrategia3 = ps.executeQuery();
                    while (rsEstrategia3.next()) {
                        EstrategiaAD estrategia3 = new EstrategiaAD();
                        estrategia3.getEstrategia(rsEstrategia3, "Adm");
                        estrategia2.getNivel3().add(estrategia3);
                    }
                    estrategia.getNivel2().add(estrategia2);

                }
                this.getNivel1().add(estrategia);
            }
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

    private int accionesGuardarEstrategias(AccesoDatos ad, Estrategia nivel1, int idpadre) {
        int idPadre = 0;
        try {
            String SQL2 = "";

            String d = nivel1.getDescripcion();
            Integer id = nivel1.getId_estrategia();
            PreparedStatement ps = null;
            if (id == 0) {
                SQL2 = "INSERT INTO t_estrategias_metodologicas (descripcion, estado, nombre, idpadre, nivel) VALUES (?,?,?,?,?) returning id_estrategia";
                ps = ad.getCon().prepareStatement(SQL2);
                ps.setString(1, d);
                ps.setString(2, "H");
                ps.setString(3, nivel1.getNombre());
                ps.setInt(4, idpadre);
                ps.setString(5, nivel1.getNivel());
                ResultSet rsEstrategia = ps.executeQuery();
                while (rsEstrategia.next()) {
                    idPadre = rsEstrategia.getInt("id_estrategia");
                }

            } else if (nivel1.getChv_check() == null && id > 0) {
                idPadre = id;
                SQL2 = "UPDATE t_estrategias_metodologicas\n"
                        + "   SET descripcion=?, nombre=? \n"
                        + " WHERE id_estrategia=?;";
                ps = ad.getCon().prepareStatement(SQL2);
                ps.setString(1, d);
                ps.setString(2, nivel1.getNombre());
                ps.setInt(3, id);
                ps.executeUpdate();
            } else {
                EstrategiasGestionAD e = new EstrategiasGestionAD();
                Integer n = e.getNumEstrategia(ad, id);
                e.deleteEstrategia(id, ad, n);
            }

            ad.getCon().commit();
            ps.close();
        } catch (Exception e) {
        }
        return idPadre;
    }

}
