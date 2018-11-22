/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.estrategias.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.estrategias.comunes.Estrategia;
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
public class EstrategiaAD extends Estrategia {

    public void getEstrategia(ResultSet rsEstrategia, String rol) {
        try {
            this.setId_estrategia(rsEstrategia.getInt("id_estrategia"));
            this.setDescripcion(rsEstrategia.getString("descripcion"));
            this.setEstado(rsEstrategia.getString("estado"));
            this.setNombre(rsEstrategia.getString("nombre"));
            this.setIdpadre(rsEstrategia.getInt("idpadre"));
            this.setNivel(rsEstrategia.getString("nivel"));
            if (rsEstrategia.getInt("id_est_met") != 0) {
                this.setChv_check("checked");
            }
        } catch (SQLException e) {
            Logger.getLogger("EstrategiaAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void insertarEstrategia(AccesoDatos ad, int idSilabo, int idUnidad, int idEstrategia, int idpadre) throws SQLException {
        try {
            String SQL = "insert into t_subseccion_estrategias_met (id_est_met,id_silabo,id_unidad,padre) VALUES (?,?,?,?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idEstrategia);
            ps.setInt(2, idSilabo);
            ps.setInt(3, idUnidad);
            ps.setInt(4, idpadre);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EstrategiaAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void asignarEstrategiaSeleccionada(ResultSet rsEstrategia) {
        try {
            this.setId_estrategia(rsEstrategia.getInt("id_est_met"));
        } catch (SQLException e) {
            Logger.getLogger("EstrategiaAD").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }
}
