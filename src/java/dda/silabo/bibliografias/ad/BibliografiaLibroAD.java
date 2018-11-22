/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.bibliografias.ad;

import dda.silabo.bibliografias.comunes.BibliografiaLibro;
import dda.silabo.db.AccesoDatos;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jorge Zaruma
 */
public class BibliografiaLibroAD extends BibliografiaLibro {

    void getBibliografiaLibro(ResultSet rs) {
        try {
            this.setAnio(rs.getInt("anio"));
            this.setAutor(rs.getString("autor"));
            this.setCiudad(rs.getString("ciudad"));
            this.setEditorial(rs.getString("editorial"));
            this.setId(rs.getInt("id"));
            this.setTitulo(rs.getString("titulo"));
            this.setDisabled(rs.getString("sistema"));
        } catch (Exception e) {
        }
    }

    void importarBibliografias(ResultSet rsBibliografia, AccesoDatos ad, Integer idSilaboAnterior) throws SQLException {
        try {
            String SQL = "insert into t_bibliografia_libro(id_bibliografia,autor,titulo,anio,ciudad,editorial,sistema)\n"
                    + "select '" + rsBibliografia.getInt("id_bibliografia") + "',autor,titulo,anio,ciudad,editorial,sistema from t_bibliografia_libro as bl \n"
                    + "join t_seccion_bibliografia as sb \n"
                    + "on sb.id_bibliografia = bl.id_bibliografia\n"
                    + "where id_silabo=? and tipo=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.setString(2, rsBibliografia.getString("tipo"));
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

}
