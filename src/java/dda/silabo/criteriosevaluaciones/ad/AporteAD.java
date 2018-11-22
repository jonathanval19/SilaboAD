/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.criteriosevaluaciones.ad;

import dda.silabo.criteriosevaluaciones.comunes.Aporte;
import dda.silabo.db.AccesoDatos;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class AporteAD extends Aporte {

    public List<Aporte> getActividadesAporte(Integer idActividad, AccesoDatos ad, Integer idSilabo) throws SQLException {
        List<Aporte> result = new ArrayList<>();
        try {
            String SQL = "select * from t_aportes order by id_aportes";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsAporte = ps.executeQuery();
            while (rsAporte.next()) {
                Aporte aporte = new Aporte();
                aporte.setId_aportes(rsAporte.getInt("id_aportes"));
                aporte.setDescripcion(rsAporte.getString("descripcion"));
                NotaAD nota = new NotaAD();
                nota.getActividadesAporteNota(rsAporte.getInt("id_aportes"), idActividad, ad, idSilabo);
                aporte.setNota(nota);
                aporte.setNotaTotal(nota.getNotaTotalAporte(rsAporte.getInt("id_aportes"), ad, idSilabo));
                result.add(aporte);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("AporteAD").log(java.util.logging.Level.SEVERE, "dda.silabo.criteriosevaluaciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } 
        return result;
    }

}
