/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.logros.ln;

import com.google.gson.Gson;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.logros.ad.LogrosAD;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;

/**
 *
 * @author Jorge Zaruma
 */
public class LogrosLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String LogroCargar(String jsonSilabo) {
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        LogrosAD logros = new LogrosAD();
        try {
            if (silabo != null && ad.Connectar() != 0) {

                String SQL = logros.logroSQLSelect(silabo);
                logros.obtenerLogros(ad, SQL);
                logros.obtenerObservaciones(silabo, ad);
                MenuLateralAD menuLateralAD = new MenuLateralAD();
                if ((silabo.getRol().equals("Cor") || silabo.getRol().equals("Dir")) && menuLateralAD.getEstadoSeccion(silabo.getIdSilabo(), silabo.getIdTipo(), ad, "subseccion").equals("Corregido")) {
                    menuLateralAD.updateSubseccionesMenuSilabo(silabo, silabo.getIdTipo(), ad, "Aprobado");
                }

            }
        } catch (Exception e) {

        } finally {
            ad.Desconectar();
        }
        return G.toJson(logros);
    }

    public String LogroGuardar(String jsonLogros) {
        LogrosAD logros = G.fromJson(jsonLogros, LogrosAD.class);
        Silabo silabo = logros.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        String result = "ko";
        Integer idSilabo = silabo.getIdSilabo();
        Integer idUnidad = silabo.getIdUnidad();
        try {
            if (ad.Connectar() != 0) {
                if (!logros.getLogros().isEmpty()) {
                    if (logros.getAccion().equals("guardar")) {
                        logros.LogroGuardar(ad, idSilabo, idUnidad);
                    } else {
                        logros.eliminarLogro(ad, logros.getLogros().get(0).getIdLogro());
                    }
                    if (silabo.getRol().equals("Doc")) {
                        observacion.updateSubseccionSilabo(ad, silabo, silabo.getIdTipo(), "Corregido");
                        observacion.updateObservacionSubseccion(ad, logros.getObservaciones(), silabo, silabo.getIdTipo());
                    }
                }
                if (logros.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSubseccion(ad, logros.getObservaciones(), silabo, silabo.getIdTipo());
                }
                result = "ok";
            }
        } catch (Exception e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

}
