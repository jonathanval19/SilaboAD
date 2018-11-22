/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.recursos.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.recursos.ad.RecursosAD;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class RecursosLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String RecursosCargar(String jsonSilabo) throws SQLException {
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        RecursosAD result = new RecursosAD();
        String SQL = "";
        try {
            if (silabo != null && ad.Connectar() != 0) {

                SQL = result.RecursosSQLSelect(silabo.getRol(), silabo.getIdSilabo(), silabo.getIdUnidad());
                result.RecursosCargar(ad, SQL, silabo);
                result.obtenerObservaciones("Recursos", silabo, ad);
                MenuLateralAD menuLateralAD = new MenuLateralAD();
                if ((silabo.getRol().equals("Cor") || silabo.getRol().equals("Dir")) && menuLateralAD.getEstadoSeccion(silabo.getIdSilabo(), silabo.getIdTipo(), ad, "subseccion").equals("Corregido")) {
                    menuLateralAD.updateSubseccionesMenuSilabo(silabo, silabo.getIdTipo(), ad, "Aprobado");
                }

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RecursosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return G.toJson(result);
    }

    public String GuardarRecursos(String jsonRecursos) throws SQLException {
        RecursosAD recursos = G.fromJson(jsonRecursos, RecursosAD.class);
        Silabo silabo = recursos.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        String result = "ko";
        try {
            if (ad.Connectar() != 0) {

                Integer idSilabo = silabo.getIdSilabo();
                Integer idUnidad = silabo.getIdUnidad();
                recursos.eliminarRecursosImportacion(ad, idSilabo, idUnidad);
                if (recursos.getRecursos().size() > 0) {
                    recursos.guardarRecursos(idSilabo, idUnidad);
                    if (silabo.getRol().equals("Doc")) {
                        observacion.updateSubseccionSilabo(ad, silabo, silabo.getIdTipo(), "Corregido");
                        observacion.updateObservacionSubseccion(ad, recursos.getObservaciones(), silabo, silabo.getIdTipo());
                    }
                }
                if (recursos.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSubseccion(ad, recursos.getObservaciones(), silabo, silabo.getIdTipo());
                }
                result = "ok";
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RecursosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        silabo.setRol("Doc");
        return result;
    }

}
