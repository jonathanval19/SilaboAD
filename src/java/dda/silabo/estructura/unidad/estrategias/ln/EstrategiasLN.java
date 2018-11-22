/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.estrategias.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.estrategias.ad.EstrategiasAD;
import dda.silabo.estructura.unidad.estrategias.ad.EstrategiasMetodologicasAD;
import dda.silabo.estructura.unidad.estrategias.comunes.EstrategiasMetodologicas;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class EstrategiasLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String EstrategiasCargar(String jsonSilabo) {
        EstrategiasAD result = new EstrategiasAD();
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        try {
            if (silabo != null && ad.Connectar() != 0) {

                String SQL = result.EstrategiasSQLSelect(silabo.getRol(), silabo.getIdSilabo(), silabo.getIdUnidad());
                result.EstrategiasCargar(ad, SQL, silabo);
                if (!silabo.getRol().equals("Adm")) {
                    result.obtenerObservaciones(silabo, "Estrategias", ad);
                }
                MenuLateralAD menuLateralAD = new MenuLateralAD();
                if ((silabo.getRol().equals("Cor") || silabo.getRol().equals("Dir")) && menuLateralAD.getEstadoSeccion(silabo.getIdSilabo(), silabo.getIdTipo(), ad, "subseccion").equals("Corregido")) {
                    menuLateralAD.updateSubseccionesMenuSilabo(silabo, silabo.getIdTipo(), ad, "Aprobado");
                }

            }
        } catch (Exception e) {
            Logger.getLogger("EstrategiasLN").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return G.toJson(result);
    }

    public String EstrategiasGuardar(String jsonEstrategias) {
        EstrategiasAD estrategias = G.fromJson(jsonEstrategias, EstrategiasAD.class);
        Silabo silabo = estrategias.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        String result = "ko";
        try {
            if (ad.Connectar() != 0) {
                Integer idSilabo = silabo.getIdSilabo();
                Integer idUnidad = silabo.getIdUnidad();
                Integer idPadre = Integer.parseInt(silabo.getPeriodo());
                estrategias.eliminarEstrategiasImportacion(ad, idSilabo, idUnidad, idPadre);
                if (estrategias.getEstrategias().size() > 0) {
                    estrategias.EstrategiasGuardar(ad, idSilabo, idUnidad, idPadre);
                    if (silabo.getRol().equals("Doc")) {
                        observacion.updateSubseccionSilabo(ad, silabo, silabo.getIdTipo(), "Corregido");
                        observacion.updateObservacionSubseccion(ad, estrategias.getObservaciones(), silabo, silabo.getIdTipo());
                    }
                }
                if (estrategias.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSubseccion(ad, estrategias.getObservaciones(), silabo, silabo.getIdTipo());
                }
                result = "ok";
                EstrategiasGestionLN egln = new EstrategiasGestionLN();
                result = egln.EstrategiasCargar(G.toJson(estrategias.getSilabos()));

            }
        } catch (Exception e) {
            Logger.getLogger("EstrategiasLN").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

}
