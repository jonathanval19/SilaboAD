/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.bibliografias.ad;

import dda.panalitico.ws.BibliografiaLibroComunes;
import dda.panalitico.ws.BibliografiaSitioWebComunes;
import dda.panalitico.ws.BibliografiasComunes;
import dda.silabo.bibliografias.comunes.BibliografiaLibro;
import dda.silabo.bibliografias.comunes.BibliografiaSitioWeb;
import dda.silabo.observaciones.ad.ObservacionesAD;
import dda.silabo.bibliografias.comunes.Bibliografias;
import dda.silabo.db.AccesoDatos;
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
public class BibliografiasAD extends Bibliografias {

    public String BibliografiasSQLSelect(Integer idSilabo) {
        String SQL = "SELECT tipo, id_bibliografia, id_silabo\n"
                + "  FROM t_seccion_bibliografia where id_silabo='" + idSilabo + "' order by tipo asc;";
        return SQL;
    }

    public void obtenerBibliografias(AccesoDatos ad, String SQL) throws SQLException {
        try {
            int cont = 0;
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsBibliografia = ps.executeQuery();
            while (rsBibliografia.next()) {
                BibliografiaAD bibliografia = new BibliografiaAD();
                bibliografia.getBibliografia(rsBibliografia, ad);
                if (cont == 0) {
                    this.setBasica(bibliografia);
                } else {
                    this.setComplementaria(bibliografia);
                }
                cont++;
            }  //ejcutar sql 
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void obtenerObservaciones(String descripcion, Silabo silabo, AccesoDatos ad) {
        ObservacionesAD observaciones = new ObservacionesAD();
        observaciones.getListaObservacionesSeccion(descripcion, silabo, 5, ad);
        this.setObservacion(observaciones);
        this.setTitulo("Bibliografía Básica y Complementaria");
        this.setAyuda("<div style='text-align:justify'>Al citar la bibliografía se deberá tomar en cuenta las Normas Internacionales APA.</div>");
        this.setSilabos(silabo);
    }

    public void guardarBibliografia(AccesoDatos ad, Integer idSilabo, String sistema) throws SQLException {
        try {
            Integer idBibliografiaB = 0;
            Integer idBibliografiaC = 0;
            String SQL = "";
            if (this.getBasica().getId_bibliografia() == 0 && (this.getBasica().getLibros().size() > 0 || this.getBasica().getSitios().size() > 0)) {
                SQL = "INSERT INTO t_seccion_bibliografia(\n"
                        + "            tipo,id_silabo)\n"
                        + "    VALUES (?, ?) returning id_bibliografia;";
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, this.getBasica().getDescripcion());
                ps.setInt(2, idSilabo);
                ResultSet rsRoles = ps.executeQuery();
                while (rsRoles.next()) {
                    idBibliografiaB = rsRoles.getInt("id_bibliografia");
                }
                ad.getCon().commit();
                ps.close();

            } else {
                idBibliografiaB = this.getBasica().getId_bibliografia();
            }
            if (this.getComplementaria().getId_bibliografia() == 0 && (this.getComplementaria().getLibros().size() > 0 || this.getComplementaria().getSitios().size() > 0)) {
                SQL = "INSERT INTO t_seccion_bibliografia(\n"
                        + "            tipo,id_silabo)\n"
                        + "    VALUES (?, ?) returning id_bibliografia;";
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, this.getComplementaria().getDescripcion());
                ps.setInt(2, idSilabo);
                ResultSet rsRoles = ps.executeQuery();
                while (rsRoles.next()) {
                    idBibliografiaC = rsRoles.getInt("id_bibliografia");
                }
                ad.getCon().commit();
                ps.close();
            } else {
                idBibliografiaC = this.getComplementaria().getId_bibliografia();
            }
            PreparedStatement ps = null;
            if (idBibliografiaB != 0) {
                for (BibliografiaLibro bl : this.getBasica().getLibros()) {

                    if (bl.getId() == 0) {
                        SQL = "INSERT INTO t_bibliografia_libro(\n"
                                + "            id_bibliografia, autor, titulo, anio, ciudad, editorial,sistema)\n"
                                + "    VALUES (?, ?, ?, ?, ?, ?,?);";
                        ps = ad.getCon().prepareStatement(SQL);
                        ps.setInt(1, idBibliografiaB);
                        ps.setString(2, bl.getAutor());
                        ps.setString(3, bl.getTitulo());
                        ps.setInt(4, bl.getAnio());
                        ps.setString(5, bl.getCiudad());
                        ps.setString(6, bl.getEditorial());
                        ps.setString(7, sistema);
                    } else {
                        SQL = "UPDATE t_bibliografia_libro\n"
                                + "   SET autor=?, titulo=?, anio=?, ciudad=?, \n"
                                + "       editorial=?\n"
                                + " WHERE id=?;";
                        ps = ad.getCon().prepareStatement(SQL);

                        ps.setString(1, bl.getAutor());
                        ps.setString(2, bl.getTitulo());
                        ps.setInt(3, bl.getAnio());
                        ps.setString(4, bl.getCiudad());
                        ps.setString(5, bl.getEditorial());
                        ps.setInt(6, bl.getId());
                    }
                    ps.executeUpdate();
                    ad.getCon().commit();
                    ps.close();
                }
                for (BibliografiaSitioWeb bs : this.getBasica().getSitios()) {
                    if (bs.getId() == 0) {
                        SQL = "INSERT INTO t_bibliografia_sitio(\n"
                                + "            id_bibliografia, autor, nombre_sitio, anio, mes, dia, url,sistema)\n"
                                + "    VALUES (?, ?, ?, ?, ?, ?, ?,?);";
                        ps = ad.getCon().prepareStatement(SQL);
                        ps.setInt(1, idBibliografiaB);
                        ps.setString(2, bs.getAutor());
                        ps.setString(3, bs.getNombreSitio());
                        ps.setInt(4, bs.getAnio());
                        ps.setString(5, bs.getMes());
                        ps.setInt(6, bs.getDia());
                        ps.setString(7, bs.getUrl());
                        ps.setString(8, sistema);
                    } else {
                        SQL = "UPDATE t_bibliografia_sitio\n"
                                + "   SET autor=?, nombre_sitio=?, anio=?, mes=?, \n"
                                + "       dia=?, url=?\n"
                                + " WHERE id=?;";
                        ps = ad.getCon().prepareStatement(SQL);

                        ps.setString(1, bs.getAutor());
                        ps.setString(2, bs.getNombreSitio());
                        ps.setInt(3, bs.getAnio());
                        ps.setString(4, bs.getMes());
                        ps.setInt(5, bs.getDia());
                        ps.setString(6, bs.getUrl());
                        ps.setInt(7, bs.getId());
                    }
                    ps.executeUpdate();
                    ad.getCon().commit();
                    ps.close();
                }

            }
            if (idBibliografiaC != 0) {
                for (BibliografiaLibro bl : this.getComplementaria().getLibros()) {
                    if (bl.getId() == 0) {
                        SQL = "INSERT INTO t_bibliografia_libro(\n"
                                + "            id_bibliografia, autor, titulo, anio, ciudad, editorial,sistema)\n"
                                + "    VALUES (?, ?, ?, ?, ?, ?,?);";
                        ps = ad.getCon().prepareStatement(SQL);
                        ps.setInt(1, idBibliografiaC);
                        ps.setString(2, bl.getAutor());
                        ps.setString(3, bl.getTitulo());
                        ps.setInt(4, bl.getAnio());
                        ps.setString(5, bl.getCiudad());
                        ps.setString(6, bl.getEditorial());
                        ps.setString(7, sistema);
                    } else {
                        SQL = "UPDATE t_bibliografia_libro\n"
                                + "   SET autor=?, titulo=?, anio=?, ciudad=?, \n"
                                + "       editorial=?\n"
                                + " WHERE id=?;";

                        ps = ad.getCon().prepareStatement(SQL);
                        ps.setString(1, bl.getAutor());
                        ps.setString(2, bl.getTitulo());
                        ps.setInt(3, bl.getAnio());
                        ps.setString(4, bl.getCiudad());
                        ps.setString(5, bl.getEditorial());
                        ps.setInt(6, bl.getId());
                    }
                    ps.executeUpdate();
                    ad.getCon().commit();
                    ps.close();
                }
                for (BibliografiaSitioWeb bs : this.getComplementaria().getSitios()) {
                    if (bs.getId() == 0) {
                        SQL = "INSERT INTO t_bibliografia_sitio(\n"
                                + "            id_bibliografia, autor, nombre_sitio, anio, mes, dia, url,sistema)\n"
                                + "    VALUES (?, ?, ?, ?, ?, ?, ?,?);";
                        ps = ad.getCon().prepareStatement(SQL);
                        ps.setInt(1, idBibliografiaC);
                        ps.setString(2, bs.getAutor());
                        ps.setString(3, bs.getNombreSitio());
                        ps.setInt(4, bs.getAnio());
                        ps.setString(5, bs.getMes());
                        ps.setInt(6, bs.getDia());
                        ps.setString(7, bs.getUrl());
                        ps.setString(8, sistema);
                    } else {
                        SQL = "UPDATE t_bibliografia_sitio\n"
                                + "   SET autor=?, nombre_sitio=?, anio=?, mes=?, \n"
                                + "       dia=?, url=?\n"
                                + " WHERE id=?;";
                        ps = ad.getCon().prepareStatement(SQL);

                        ps.setString(1, bs.getAutor());
                        ps.setString(2, bs.getNombreSitio());
                        ps.setInt(3, bs.getAnio());
                        ps.setString(4, bs.getMes());
                        ps.setInt(5, bs.getDia());
                        ps.setString(6, bs.getUrl());
                        ps.setInt(7, bs.getId());
                    }
                    ps.executeUpdate();
                    ad.getCon().commit();
                    ps.close();
                }

            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void EliminarBibliografia(AccesoDatos ad, Integer idSilabo) throws SQLException {
        try {
            String SQL = "delete from t_seccion_bibliografia where (id_silabo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public String deleteBibliografia(AccesoDatos ad) throws SQLException {
        String resp = "dl";
        String SQL = "";

        try {
            PreparedStatement ps = null;
            if (this.getBibliografias().get(0).getTipo().equals("libro")) {
                SQL = "DELETE FROM t_bibliografia_libro\n"
                        + " WHERE id=?;";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, this.getBibliografias().get(0).getId_bibliografia());
            } else {
                SQL = "DELETE FROM t_bibliografia_sitio\n"
                        + " WHERE id=?;";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, this.getBibliografias().get(0).getId_bibliografia());
            }

            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
            resp = "ok";
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("BibliografiaAD").log(Level.SEVERE, "dda.silabo.bibliografias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    public void importarBibliografias(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad) throws SQLException {

        EliminarBibliografia(ad, silaboActual.getIdSilabo());
        String SQL = "insert into t_seccion_bibliografia (tipo,id_silabo)\n"
                + " select tipo,? from t_seccion_bibliografia\n"
                + "  where (id_silabo =?) returning id_bibliografia,tipo";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, silaboActual.getIdSilabo());
            ps.setInt(2, idSilaboAnterior);
            ResultSet rsBibliografia = ps.executeQuery();
            while (rsBibliografia.next()) {
                BibliografiaLibroAD bl = new BibliografiaLibroAD();
                bl.importarBibliografias(rsBibliografia, ad, idSilaboAnterior);
                BibliografiaSitioAD bs = new BibliografiaSitioAD();
                bs.importarBibliografias(rsBibliografia, ad, idSilaboAnterior);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void guardarBibliografiaImportacion(AccesoDatos ad, Integer idSilabo, String pa, BibliografiasComunes bc) throws SQLException {
        try {
            String SQL = "";
            Integer idBibliografiaB = 0;
            Integer idBibliografiaC = 0;
            PreparedStatement ps = null;
            SQL = "INSERT INTO t_seccion_bibliografia(\n"
                    + "            tipo,id_silabo)\n"
                    + "    VALUES (?, ?) returning id_bibliografia;";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, "BASICA");
            ps.setInt(2, idSilabo);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                idBibliografiaB = rsRoles.getInt("id_bibliografia");
            }

            ps.close();

            SQL = "INSERT INTO t_seccion_bibliografia(\n"
                    + "            tipo,id_silabo)\n"
                    + "    VALUES (?,?) returning id_bibliografia;";
            ps = ad.getCon().prepareStatement(SQL);

            ps.setString(1, "COMPLEMENTARIA");
            ps.setInt(2, idSilabo);
            rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                idBibliografiaC = rsRoles.getInt("id_bibliografia");

            }
            ps.close();

            if (idBibliografiaB != 0) {
                eliminarTiposBibliografias(idBibliografiaB, ad);
                for (BibliografiaLibroComunes bl : bc.getBasica().getLibros()) {
                    SQL = "INSERT INTO t_bibliografia_libro(\n"
                            + "            id_bibliografia, autor, titulo, anio, ciudad, editorial,sistema)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?,?);";
                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setInt(1, idBibliografiaB);
                    ps.setString(2, bl.getAutor());
                    ps.setString(3, bl.getTitulo());
                    ps.setInt(4, bl.getAnio());
                    ps.setString(5, bl.getCiudad());
                    ps.setString(6, bl.getEditorial());
                    ps.setString(7, "pa");
                    ps.executeUpdate();
                    ps.close();
                }
                for (BibliografiaSitioWebComunes bs : bc.getBasica().getSitios()) {
                    SQL = "INSERT INTO t_bibliografia_sitio(\n"
                            + "            id_bibliografia, autor, nombre_sitio, anio, mes, dia, url,sistema)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?,?);";

                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setInt(1, idBibliografiaB);
                    ps.setString(2, bs.getAutor());
                    ps.setString(3, bs.getNombreSitio());
                    ps.setInt(4, bs.getAnio());
                    ps.setString(5, bs.getMes());
                    ps.setInt(6, bs.getDia());
                    ps.setString(7, bs.getUrl());
                    ps.setString(8, "pa");
                    ps.executeUpdate();
                    ps.close();
                }

            }
            if (idBibliografiaC != 0) {
                eliminarTiposBibliografias(idBibliografiaC, ad);
                for (BibliografiaLibroComunes bl : bc.getComplementaria().getLibros()) {
                    SQL = "INSERT INTO t_bibliografia_libro(\n"
                            + "            id_bibliografia, autor, titulo, anio, ciudad, editorial,sistema)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?,'pa');";

                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setInt(1, idBibliografiaC);
                    ps.setString(2, bl.getAutor());
                    ps.setString(3, bl.getTitulo());
                    ps.setInt(4, bl.getAnio());
                    ps.setString(5, bl.getCiudad());
                    ps.setString(6, bl.getEditorial());
                    ps.setString(7, "pa");
                    ps.executeUpdate();
                    ps.close();
                }
                for (BibliografiaSitioWebComunes bs : bc.getComplementaria().getSitios()) {
                    SQL = "INSERT INTO t_bibliografia_sitio(\n"
                            + "            id_bibliografia, autor, nombre_sitio, anio, mes, dia, url,sistema)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?,?);";
                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setInt(1, idBibliografiaC);
                    ps.setString(2, bs.getAutor());
                    ps.setString(3, bs.getNombreSitio());
                    ps.setInt(4, bs.getAnio());
                    ps.setString(5, bs.getMes());
                    ps.setInt(6, bs.getDia());
                    ps.setString(7, bs.getUrl());
                    ps.setString(8, "pa");
                    ps.executeUpdate();
                    ps.close();
                }

            }
            ad.getCon().commit();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private void eliminarTiposBibliografias(Integer idTipoBibliografia, AccesoDatos ad) throws SQLException {
        try {
            PreparedStatement ps = null;
            String SQL = "DELETE FROM t_bibliografia_libro\n"
                    + " WHERE id_bibliografia=? and sistema=?";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idTipoBibliografia);
            ps.setString(2, "pa");
            ps.executeUpdate();
            ps.close();
            SQL = "DELETE FROM t_bibliografia_sitio\n"
                    + " WHERE id_bibliografia=? and sistema=?";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idTipoBibliografia);
            ps.setString(2, "pa");
            ps.executeUpdate();
            ps.close();
            ad.getCon().commit();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }
}
