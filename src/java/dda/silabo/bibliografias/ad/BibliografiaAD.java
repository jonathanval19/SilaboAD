/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.bibliografias.ad;

import dda.silabo.bibliografias.comunes.Bibliografia;
import dda.silabo.db.AccesoDatos;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class BibliografiaAD extends Bibliografia {

    public void getBibliografia(ResultSet rsBibliografia, AccesoDatos ad) throws SQLException {
        try {
            this.setTipo(rsBibliografia.getString("tipo"));
            this.setId_bibliografia(rsBibliografia.getInt("id_bibliografia"));
            String SQL = "SELECT id, id_bibliografia, autor, titulo, anio, ciudad, editorial,sistema\n"
                    + "  FROM t_bibliografia_libro where id_bibliografia=?;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getId_bibliografia());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BibliografiaLibroAD bl = new BibliografiaLibroAD();
                bl.getBibliografiaLibro(rs);
                this.getLibros().add(bl);
            }
            ps.close();
            SQL = "SELECT id, id_bibliografia, autor, nombre_sitio, anio, mes, dia, url,sistema\n"
                    + "  FROM t_bibliografia_sitio where id_bibliografia=?;";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getId_bibliografia());

            rs = ps.executeQuery();
            while (rs.next()) {
                BibliografiaSitioAD bs = new BibliografiaSitioAD();
                bs.getBibliografiaSitio(rs);
                this.getSitios().add(bs);

            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("BibliografiaAD").log(Level.SEVERE, "dda.silabo.bibliografias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
