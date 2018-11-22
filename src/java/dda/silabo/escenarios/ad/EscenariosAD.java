/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.escenarios.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.observaciones.ad.ObservacionesAD;
import dda.silabo.escenarios.comunes.Escenarios;
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
public class EscenariosAD extends Escenarios {

    public String EscenariosSQLSelect(String tipo, String rol, Integer idSilabo) {
        String SQL = "";
        if (!rol.equals("Adm")) {
            SQL = "select tse.id_escenario,te.descripcion,te.estado,te.tipo,te.id_escenarios from t_escenarios_aprendizaje AS te\n"
                    + "left join \n"
                    + "t_seccion_escenarios AS tse \n"
                    + "on tse.id_escenario = te.id_escenarios and tse.id_silabo='" + idSilabo + "' \n"
                    + "where te.tipo='" + tipo + "' and te.estado='H'";
        } else {
            SQL = "SELECT e.id_escenarios, e.descripcion, e.tipo, e.estado\n"
                    + "                FROM\n"
                    + "                t_escenarios_aprendizaje AS e\n"
                    + "                where (e.tipo='" + tipo + "')";
        }
        return SQL;
    }

    public void EscenariosCargar(AccesoDatos ad, String SQL, Silabo silabo) throws SQLException {
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsEscenario = ps.executeQuery();
            while (rsEscenario.next()) {
                EscenarioAD escenarios = new EscenarioAD();
                escenarios.EscenariosCargar(rsEscenario, silabo.getRol());
                this.addEscenarios(escenarios);

            }  //ejcutar sql 
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        if (silabo.getTipo().equals("Real")) {
            this.setAyuda("<div style='text-align:justify'>Para el presente curso se desarrollará clases  y prácticas en el aula con hojas de problemas. Se utilizará la computadora con programas Office que permitan obtener mejores resultados y de igual manera se establecerá foros en el aula virtual.</div>");
            this.setTitulo("Escenarios de Aprendizajes Reales");
        }
        if (silabo.getTipo().equals("Virtual")) {
            this.setAyuda("<div style='text-align:justify'>Para el presente curso se desarrollará clases  y prácticas en el aula con hojas de problemas. Se utilizará la computadora con programas Office que permitan obtener mejores resultados y de igual manera se establecerá foros en el aula virtual.</div>");
            this.setTitulo("Escenarios de Aprendizajes Virtuales");
        }
        if (silabo.getTipo().equals("Aulico")) {
            this.setAyuda("<div style='text-align:justify'>Para el presente curso se desarrollará clases  y prácticas en el aula con hojas de problemas. Se utilizará la computadora con programas Office que permitan obtener mejores resultados y de igual manera se establecerá foros en el aula virtual.</div>");
            this.setTitulo("Escenarios de Aprendizajes Aúlicos");
        }
        this.setSilabos(silabo);
    }

    public void obtenerObservaciones(String tipo, Silabo silabo, AccesoDatos ad) {
        ObservacionesAD observacion = new ObservacionesAD();
        if (silabo.getTipo().equals("Real")) {
            observacion.getListaObservacionesSeccion(tipo, silabo, 1, ad);
            this.setObservacion(observacion);
        }
        if (silabo.getTipo().equals("Virtual")) {
            observacion.getListaObservacionesSeccion(tipo, silabo, 2, ad);
            this.setObservacion(observacion);
        } else if (silabo.getTipo().equals("Aulico")) {
            observacion.getListaObservacionesSeccion(tipo, silabo, 3, ad);
            this.setObservacion(observacion);
        }

    }

    public void EscenariosGuardar(AccesoDatos ad, Integer idSilabo, String tipo) throws SQLException {
        Integer id = 0;
        try {
            for (int i = 0; i < this.getEscenarios().size(); i++) {
                id = this.getEscenarios().get(i).getIdEsc();
                String SQL2 = "INSERT INTO t_seccion_escenarios (id_escenario,tipo,id_silabo) VALUES (?,?,?)";
                PreparedStatement ps = ad.getCon().prepareStatement(SQL2);
                ps.setInt(1, id);
                ps.setString(2, tipo);
                ps.setInt(3, idSilabo);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminarEscenarios(AccesoDatos ad, String tipo, Integer idSilabo) throws SQLException {
        try {
            String SQL = "DELETE FROM t_seccion_escenarios WHERE (tipo =? and id_silabo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, tipo);
            ps.setInt(2, idSilabo);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void importarEscenarios(Silabo silaboActual, Integer idSilaboAnterior, AccesoDatos ad) throws SQLException {
        ObservacionAD observacion = new ObservacionAD();
        eliminarEscenarios2(ad, silaboActual.getIdSilabo());
        String SQL = "insert into t_seccion_escenarios (id_escenario,tipo,id_silabo)\n"
                + "select id_escenario,tipo,'" + silaboActual.getIdSilabo() + "' from t_seccion_escenarios \n"
                + "where (id_silabo =?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilaboAnterior);
            ps.executeUpdate();
            observacion.updateSeccionesSilabo(silaboActual, ad, 1, "Corregido");
            observacion.updateSeccionesSilabo(silaboActual, ad, 2, "Corregido");
            observacion.updateSeccionesSilabo(silaboActual, ad, 3, "Corregido");
            ad.getCon().commit();
            ps.close();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void eliminarEscenarios2(AccesoDatos ad, Integer idSilabo) throws SQLException {
        try {
            String SQL = "DELETE FROM t_seccion_escenarios WHERE (id_silabo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("EscenariosAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
