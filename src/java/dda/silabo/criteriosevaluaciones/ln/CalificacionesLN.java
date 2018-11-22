/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.criteriosevaluaciones.ln;

import com.google.gson.Gson;
import dda.silabo.criteriosevaluaciones.ad.CalificacionesAD;
import dda.silabo.db.AccesoDatos;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class CalificacionesLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String GuardarCalificacionesAD(String jsonCalificaciones) {
        CalificacionesAD calificaciones = G.fromJson(jsonCalificaciones, CalificacionesAD.class);
        Silabo silabo = calificaciones.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        try {
            if (ad.Connectar() != 0) {
                try {
                    calificaciones.eliminarCalificaciones(silabo.getIdSilabo(), ad);
                    if (calificaciones.getEvaluaciones().size() > 0) {
                        calificaciones.guardarCalificaciones(ad, silabo.getIdSilabo());
                        if (silabo.getRol().equals("Doc")) {
                            observacion.updateSeccionesSilabo(silabo, ad, silabo.getIdTipo(), "Corregido");
                            observacion.updateObservacionSeccion(ad, calificaciones.getObservaciones(), silabo, silabo.getIdTipo());
                        }
                    }
                    if (calificaciones.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                        observacion.updateObservacionSeccion(ad, calificaciones.getObservaciones(), silabo, silabo.getIdTipo());
                    }

                } catch (Exception e) {
                    Logger.getLogger("CalificacionesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.criteriosevaluaciones.ln", e.getClass().getName() + "*****" + e.getMessage());
                    System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        CriteriosEvaluacionesLN criteriosln = new CriteriosEvaluacionesLN();
        return criteriosln.getEvaluaciones(G.toJson(silabo));
    }
}
