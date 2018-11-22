/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.escenarios.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.escenarios.comunes.Escenarios;
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
public class EscenariosGestionAD extends Escenarios {

    public String GuardarAdmiEscenarios(AccesoDatos ad) throws SQLException {
        String resp = "Datos no Guardados";
        String SQL2 = "";
        try {
            for (int i = 0; i < this.getEscenarios().size(); i++) {
                String d = this.getEscenarios().get(i).getDescripcion();
                Integer id = this.getEscenarios().get(i).getIdEsc();
                String tipo = this.getEscenarios().get(i).getTipoE();
                if (id == 0) {
                    SQL2 = "INSERT INTO t_escenarios_aprendizaje (descripcion, tipo, estado) VALUES ('" + d + "','" + tipo + "','" + 'H' + "')";
                    resp = "ok";
                } else {
                    SQL2 = " UPDATE t_escenarios_aprendizaje SET descripcion='" + d + "' WHERE (id_escenarios='" + id + "')";
                    resp = "ok";
                }
                Statement ps = ad.getCon().createStatement();
                ps.executeUpdate(SQL2);
                ad.getCon().commit();
            }

        } catch (SQLException e) {
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            ad.getCon().rollback();
        }
        return resp;
    }

    public String EscenariosAdmiEliminar(Integer id, AccesoDatos ad, Integer numEscenario) {
        String resp = "", SQL = "";
        if (numEscenario == 0) {
            SQL = "DELETE FROM t_escenarios_aprendizaje WHERE (id_escenarios='" + id + "') ";
        } else {
            SQL = "UPDATE t_escenarios_aprendizaje SET estado= 'D'  WHERE (id_escenarios='" + id + "') ";
        }
        try {
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
            ad.getCon().commit();
            ps.close();
            resp = "ok";
        } catch (SQLException e) {
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    public String HabilitarEscenario(Integer id, AccesoDatos ad) {
        String resp = "", SQL = "";
        SQL = "UPDATE t_escenarios_aprendizaje SET estado= 'H'  WHERE (id_escenarios='" + id + "') ";
        try {
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
            resp = "ok";
        } catch (SQLException e) {
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    public Integer getNumEscenario(AccesoDatos ad, Integer id) throws SQLException {
        int result = 0;
        String SQL = "Select id_escenario from t_seccion_escenarios where(id_escenario='" + id + "')";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsEscenarios = ps.executeQuery();
            while (rsEscenarios.next()) {
                result++;
            }
            ps.close();
            ad.getCon().commit();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

}
