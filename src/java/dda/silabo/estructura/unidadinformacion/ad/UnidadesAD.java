/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidadinformacion.ad;

import dda.panalitico.ws.TemaComunes;
import dda.panalitico.ws.UnidadComunes;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidadinformacion.comunes.Temas;
import dda.silabo.estructura.unidadinformacion.comunes.Unidades;
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
public class UnidadesAD extends Unidades {

    public void agregarUnidad(ResultSet rsUnidades, AccesoDatos ad) throws SQLException {
        try {
            Integer id_Unidad = rsUnidades.getInt("id_unidad");
            this.setIdUnidad(id_Unidad.toString());
            this.setNumUnidad(rsUnidades.getInt("numero_unidad"));
            this.setTitulo(rsUnidades.getString("titulo"));
            String SQL = "select id_tema, tema, sistema from t_temas where id_unidad=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, id_Unidad);
            ResultSet rsTemas = ps.executeQuery();
            while (rsTemas.next()) {
                TemasAD temasAD = new TemasAD();
                temasAD.agregarTema(rsTemas, ad);
                this.getTemas().add(temasAD);
            }

            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("UnidadesAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void IngresarUnidad(AccesoDatos ad, Integer idSilabo, Unidades unidad) throws SQLException {
        String SQL2 = "";
        try {
            Integer unidadID = 0;
            int numUnidad = unidad.getNumUnidad();
            String unidadSistema = "S";

            if (unidad.getIdUnidad().equals("0") && unidad.getAccion() == null) {
                SQL2 = "insert into t_unidades (titulo,id_silabo,numero_unidad,sistema) values (?,?,?,?)";
                PreparedStatement ps = ad.getCon().prepareStatement(SQL2);
                ps.setString(1, unidad.getTitulo());
                ps.setInt(2, idSilabo);
                ps.setInt(3, numUnidad);
                ps.setString(4, unidadSistema);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
                unidadID = getIdUnidadSilabo(idSilabo, ad);
                asignarSubsecciones(idSilabo, unidad.getNumUnidad(), ad);
            } else if (!unidad.getIdUnidad().equals("0") && unidad.getAccion() == null) {
                unidadID = Integer.parseInt(unidad.getIdUnidad());
                SQL2 = "UPDATE t_unidades\n"
                        + "   SET titulo=?, numero_unidad=?\n"
                        + " WHERE id_unidad=?;";
                PreparedStatement ps = ad.getCon().prepareStatement(SQL2);
                ps.setString(1, unidad.getTitulo());
                ps.setInt(2, numUnidad);
                ps.setInt(3, unidadID);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            } else {
                unidadID = Integer.parseInt(unidad.getIdUnidad());
                SQL2 = "DELETE FROM t_unidades\n"
                        + " WHERE id_unidad=?";
                PreparedStatement ps = ad.getCon().prepareStatement(SQL2);
                ps.setInt(1, unidadID);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }
            if (unidad.getTemas().size() > 0) {
                for (Temas tema : unidad.getTemas()) {
                    TemasAD temaAD = new TemasAD();
                    temaAD.ingresarTemas(ad, unidadID, tema, unidadSistema);
                }
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("UnidadesAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void asignarSubsecciones(Integer idSilabo, int numUnidad, AccesoDatos ad) {
        for (int sub = 1; sub < 8; sub++) {
            try {
                String SQL = "insert into t_unidad_subsecciones (id_unidad,id_silabo,id_descripcion,estado) values('" + numUnidad + "','" + idSilabo + "','" + sub + "','Inicio')";
                Statement ps = ad.getCon().createStatement();
                ps.executeUpdate(SQL);
            } catch (SQLException e) {
                Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
                System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            }
        }
    }

    private Integer getIdUnidadSilabo(Integer idSilabo, AccesoDatos ad) throws SQLException {
        Integer idUnidad = 0;
        try {
            String SQL = "select id_unidad from t_unidades where (id_silabo=?) \n"
                    + "ORDER BY id_unidad DESC LIMIT 1";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsUnidades = ps.executeQuery();
            while (rsUnidades.next()) {
                idUnidad = rsUnidades.getInt("id_unidad");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("UnidadesAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return idUnidad;
    }

    void agregarUnidadSilabo(ResultSet rsUnidades, AccesoDatos ad) throws SQLException {
        try {
            Integer id_Unidad = rsUnidades.getInt("id_unidad");
            this.setIdUnidad(id_Unidad.toString());
            this.setNumUnidad(rsUnidades.getInt("numero_unidad"));
            this.setTitulo(rsUnidades.getString("titulo"));
            String SQL = "select id_tema, tema, sistema from t_temas where id_unidad=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, rsUnidades.getInt("id_unidad"));
            ResultSet rsTemas = ps.executeQuery();
            while (rsTemas.next()) {
                TemasAD temasAD = new TemasAD();
                temasAD.agregarTema(rsTemas, ad);
                this.getTemas().add(temasAD);
            }

            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("UnidadesAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void IngresarContenidosImportados(AccesoDatos ad, Integer idSilabo, UnidadComunes unidad, Integer numeroUnidad) throws SQLException {
        try {
            Integer IdUnidad = 0;
            String SQL = "insert into t_unidades (titulo,id_silabo,numero_unidad,sistema) values (?,?,?,?) returning id_unidad;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, unidad.getDesc());
            ps.setInt(2, idSilabo);
            ps.setInt(3, numeroUnidad);
            ps.setString(4, "P");
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                IdUnidad = rsRoles.getInt("id_unidad");
            }

            if (unidad.getTemas().size() > 0) {
                for (TemaComunes tema : unidad.getTemas()) {
                    TemasAD temaAD = new TemasAD();
                    temaAD.IngresarContenidosImportados(ad, IdUnidad, tema, "A");
                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("UnidadesAD").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
