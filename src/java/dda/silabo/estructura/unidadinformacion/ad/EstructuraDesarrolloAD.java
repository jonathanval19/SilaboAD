/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidadinformacion.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidadinformacion.comunes.EstructuraDesarrollo;
import dda.silabo.estructura.unidadinformacion.comunes.Unidades;
import dda.silabo.observaciones.ad.ObservacionesAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class EstructuraDesarrolloAD extends EstructuraDesarrollo {

    public void getUnidadSilabo(AccesoDatos ad, Silabo silabo) throws SQLException {
        try {
            String SQL = "select id_unidad,titulo,numero_unidad from t_unidades where (id_silabo=?  and numero_unidad=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, silabo.getIdSilabo());
            ps.setInt(2, silabo.getIdUnidad());
            ResultSet rsUnidades = ps.executeQuery();
            while (rsUnidades.next()) {
                UnidadesAD unidadesAD = new UnidadesAD();
                unidadesAD.agregarUnidad(rsUnidades, ad);
                this.getUnidad().add(unidadesAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        this.setUnidadSistema("SILABO");
    }

    public void obtenerObservaciones(String unidades, Silabo silabo, AccesoDatos ad) throws SQLException {
        this.setAyuda("<div style='text-align:justify'>Spresenta la informacion de Unidad importada del Programa Analítico ademas se podrá agregar mas temas y subtemas si lo considera pertinente</div>");
        ObservacionesAD observacion = new ObservacionesAD();
        observacion.getListaObservacionesSubseccion(unidades, silabo, 3, ad);
        this.setObservacion(observacion);
        this.setSilabos(silabo);
    }

    public void unidadesGuardar(AccesoDatos ad, int idSilabo) throws SQLException {
        if (this.getUnidad().size() > 0) {
            if (this.getSilabos().getIdSilabo() != null) {
                idSilabo = this.getSilabos().getIdSilabo();
            }
            for (Unidades unidad : this.getUnidad()) {
                UnidadesAD unidadAD = new UnidadesAD();
                unidadAD.IngresarUnidad(ad, idSilabo, unidad);
            }
        }
    }

    public int getunidadesSilabo(int idSilabo, AccesoDatos ad) throws SQLException {
        int result = 0;
        try {
            String SQL = "select id_unidad from t_unidades where (id_silabo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsUnidades = ps.executeQuery();
            while (rsUnidades.next()) {
                result++;
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        this.setUnidadSistema("SILABO");
        return result;
    }

    public void getUnidadesSilabo(AccesoDatos ad, Integer idSilabo) throws SQLException {

        try {
            String SQL = "select id_unidad,titulo,numero_unidad from t_unidades where (id_silabo=?) order by numero_unidad";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsUnidades = ps.executeQuery();
            while (rsUnidades.next()) {
                UnidadesAD unidadesAD = new UnidadesAD();
                unidadesAD.agregarUnidadSilabo(rsUnidades, ad);
                this.getUnidad().add(unidadesAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        this.setUnidadSistema("SILABO");
    }

    ///servicios externos 
    public void getUnidadesAsignturaPeriodo(AccesoDatos ad, Integer idSilabo) throws SQLException {
        try {
            String SQL = "select id_unidad,titulo,numero_unidad from t_unidades where (id_silabo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsUnidades = ps.executeQuery();
            while (rsUnidades.next()) {
                UnidadesAD unidadesAD = new UnidadesAD();
                unidadesAD.agregarUnidad(rsUnidades, ad);
                this.getUnidad().add(unidadesAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        this.setUnidadSistema("SILABO");
    }

    public void importarInformacionUnidad(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad, Integer unidadAnterior) throws SQLException {
        try {
            String SQL = "insert into t_temas (tema,id_unidad,sistema) select tt.tema,'" + silaboActual.getIdUnidad() + "', tt.sistema from t_temas as tt \n"
                    + "where (id_unidad=(select id_unidad from t_unidades where id_silabo=? and numero_unidad=?)\n"
                    + "and sistema='S') returning id_tema ";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.setInt(2, unidadAnterior);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer idTema = rs.getInt("id_tema");
                TemasAD temasAD = new TemasAD();
                temasAD.setId_temas(idTema);
                temasAD.importarSubtemas(ad);

            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstructuraDesarrolloAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void UnidadInformacionGuardarReestablecer(AccesoDatos ad) {
        try {
            String SQL = "DELETE FROM t_unidades\n"
                    + " WHERE id_silabo=?;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getSilabos().getIdSilabo());
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (Exception e) {
        }
    }

}
