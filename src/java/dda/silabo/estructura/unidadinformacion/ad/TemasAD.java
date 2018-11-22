/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidadinformacion.ad;

import dda.panalitico.ws.SubtemaComunes;
import dda.panalitico.ws.TemaComunes;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidadinformacion.comunes.Subtemas;
import dda.silabo.estructura.unidadinformacion.comunes.Temas;
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
public class TemasAD extends Temas {

    public void agregarTema(ResultSet rsTemas, AccesoDatos ad) throws SQLException {
        try {
            this.setId_temas(rsTemas.getInt("id_tema"));
            this.setDescripcion(rsTemas.getString("tema"));
            this.setSistema(rsTemas.getString("sistema"));
            String SQL = "select id_subtema, subtema,sistema from t_subtemas where id_tema=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getId_temas());
            ResultSet rsSubtemas = ps.executeQuery();
            while (rsSubtemas.next()) {
                SubtemasAD subtemasAD = new SubtemasAD();
                subtemasAD.agregarSubtema(rsSubtemas, ad);
                this.getSubtemas().add(subtemasAD);
            }

            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("TemasAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void ingresarTemas(AccesoDatos ad, Integer idUnidad, Temas tema, String unidadSistema) throws SQLException {
        String SQL = "";

        try {
            Integer TemaID = 0;
            PreparedStatement ps = null;
            if (unidadSistema.equals("A") && tema.getSistema() == null) {
                SQL = "INSERT INTO t_temas (tema,id_unidad,sistema) VALUES (?,?,?)";

                ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, tema.getDescripcion());
                ps.setInt(2, idUnidad);
                ps.setString(3, unidadSistema);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
                TemaID = getIdTemaUnidad(idUnidad, ad);
            }
            if (tema.getSistema() != null) {
                unidadSistema = tema.getSistema();
            }
            if (tema.getId_temas() == 0 && unidadSistema.equals("S")) {
                SQL = "INSERT INTO t_temas (tema,id_unidad,sistema) VALUES (?,?,?)";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, tema.getDescripcion());
                ps.setInt(2, idUnidad);
                ps.setString(3, unidadSistema);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
                TemaID = getIdTemaUnidad(idUnidad, ad);
            }
            if (tema.getId_temas() > 0 && unidadSistema.equals("S")) {
                TemaID = tema.getId_temas();
                SQL = " UPDATE t_temas SET tema=? WHERE (id_tema=?)";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, tema.getDescripcion());
                ps.setInt(2, TemaID);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }
            if (tema.getId_temas() > 0 && unidadSistema.equals("A") && tema.getSistema() != null) {
                TemaID = tema.getId_temas();
            }
            if (tema.getSubtemas().size() > 0) {
                for (Subtemas subtema : tema.getSubtemas()) {
                    SubtemasAD subtemaAD = new SubtemasAD();
                    subtemaAD.ingresarSubtemas(ad, TemaID, subtema, unidadSistema);
                }
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RecursosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminiarTemasUnidadSilabo(Integer idTema, AccesoDatos ad) throws SQLException {
        try {
            String SQLDelete = "DELETE FROM t_temas WHERE (id_tema=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQLDelete);
            ps.setInt(1, idTema);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("TemasAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private Integer getIdTemaUnidad(Integer idUnidad, AccesoDatos ad) throws SQLException {
        Integer result = 0;
        try {
            String SQL = "SELECT id_tema FROM t_temas\n"
                    + "Where id_unidad =?\n"
                    + "ORDER BY id_tema DESC\n"
                    + "LIMIT 1;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idUnidad);
            ResultSet rsTemas = ps.executeQuery();
            while (rsTemas.next()) {
                result = rsTemas.getInt("id_tema");

            }

            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("TemasAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public void TemaEliminar(Integer idTema, AccesoDatos ad) {
        try {

            SubtemasAD subtemaAD = new SubtemasAD();
            subtemaAD.eliminarSubtemasTema(idTema, ad);
            eliminiarTemasUnidadSilabo(idTema, ad);

        } catch (Exception e) {
            Logger.getLogger("UnidadesAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidadinformacion.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    void IngresarContenidosImportados(AccesoDatos ad, Integer IdUnidad, TemaComunes tema, String unidadSistema) throws SQLException {
        try {
            Integer IdTema = 0;
            String SQL = "INSERT INTO t_temas (tema,id_unidad,sistema) VALUES (?,?,?) returning id_tema;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, tema.getDesc());
            ps.setInt(2, IdUnidad);
            ps.setString(3, unidadSistema);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                IdUnidad = rsRoles.getInt("id_tema");
            }

            if (tema.getSubtemas().size() > 0) {
                for (SubtemaComunes subtema : tema.getSubtemas()) {
                    SubtemasAD subtemasAD = new SubtemasAD();
                    subtemasAD.IngresarContenidosImportados(ad, IdUnidad, subtema, "A");
                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("TemasAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    void importarSubtemas(AccesoDatos ad) {
        try {
            String SQL = "";
            Statement st = ad.getCon().createStatement();
            st.executeUpdate(SQL);
        } catch (Exception e) {
        }
    }
}
