/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.escenarios.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.db.AccesoDatos;
import dda.silabo.escenarios.ad.EscenariosAD;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class EscenariosLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String EscenariosCargar(String jsonSilabo) {
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        EscenariosAD result = new EscenariosAD();
        try {
            if (silabo != null && ad.Connectar() != 0) {
                Integer idSeccion = silabo.getIdTipo();
                String tipo = silabo.getTipo();

                String SQL = result.EscenariosSQLSelect(tipo, silabo.getRol(), silabo.getIdSilabo());
                result.EscenariosCargar(ad, SQL, silabo);
                if (!silabo.getRol().equals("Adm")) {
                    result.obtenerObservaciones(tipo, silabo, ad);
                }
                MenuLateralAD menuLateralAD = new MenuLateralAD();
                if ((silabo.getRol().equals("Cor") || silabo.getRol().equals("Dir")) && menuLateralAD.getEstadoSeccion(silabo.getIdSilabo(), idSeccion, ad, "seccion").equals("Corregido")) {
                    menuLateralAD.updateSeccionesMenuSilabo(silabo, idSeccion, ad, "Aprobado");
                }

            }
        } catch (Exception e) {
            Logger.getLogger("EscenariosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return G.toJson(result);
    }

    public String EscenariosGuardar(String jsonEscenarios) throws SQLException {
        EscenariosAD escenarios = G.fromJson(jsonEscenarios, EscenariosAD.class);
        Silabo silabo = escenarios.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        Integer idSeccion = silabo.getIdTipo();
        String result = "ko";
        try {
            if (ad.Connectar() != 0) {

                Integer idSilabo = silabo.getIdSilabo();
                String tipoE = silabo.getTipo();
                escenarios.eliminarEscenarios(ad, tipoE, idSilabo);
                if (escenarios.getEscenarios().size() > 0) {
                    escenarios.EscenariosGuardar(ad, idSilabo, tipoE);
                    if (silabo.getRol().equals("Doc")) {
                        observacion.updateSeccionesSilabo(silabo, ad, idSeccion, "Corregido");
                        observacion.updateObservacionSeccion(ad, escenarios.getObservaciones(), silabo, idSeccion);
                    }
                }
                if (escenarios.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSeccion(ad, escenarios.getObservaciones(), silabo, idSeccion);
                }
                result = "ok";
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("EscenariosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

}
