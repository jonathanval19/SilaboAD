/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.actividades.ln;

import com.google.gson.Gson;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.actividades.ad.ActividadesAD;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class ActividadesLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String ActividadesAprendizajeCargar(String jsonSilabo) {
        ActividadesAD result = new ActividadesAD();
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        String SQL = "";
        try {
            if (silabo != null && ad.Connectar() != 0) {
                {
                    String tipo = silabo.getTipo();
                    Integer idSubseccion = silabo.getIdTipo();

//                    SQL = result.ActividadesSQLSelect(tipo, silabo.getRol(), silabo.getIdSilabo(), silabo.getIdUnidad());
                    result.ActividadesAprendizajeCargar(ad, silabo);
                    MenuLateralAD menuLateralAD = new MenuLateralAD();
                    if ((silabo.getRol().equals("Cor") || silabo.getRol().equals("Dir")) && menuLateralAD.getEstadoSeccion(silabo.getIdSilabo(), idSubseccion, ad, "subseccion").equals("Corregido")) {
                        menuLateralAD.updateSubseccionesMenuSilabo(silabo, idSubseccion, ad, "Aprobado");
                    }
                    if (!silabo.getRol().equals("Adm")) {
                        result.obtenerObservaciones(tipo, silabo, ad);
                    }

                }
            }
        } catch (Exception e) {
            Logger.getLogger("ActividadesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return G.toJson(result);
    }

    public String ActividadesAprendizajeGuardar(String jsonActividades) {
        ActividadesAD actividades = G.fromJson(jsonActividades, ActividadesAD.class);
        Silabo silabo = actividades.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        Integer idSubseccion = silabo.getIdTipo();
        String result = "ko";
        try {
            if (ad.Connectar() != 0) {

                String tipoA = silabo.getTipo();
                Integer idSilabo = silabo.getIdSilabo();
                Integer idUnidad = silabo.getIdUnidad();
                actividades.eliminarActividades(ad, tipoA, idSilabo, idUnidad);
                if (actividades.getActividades().size() > 0) {
                    actividades.ActividadesAprendizajeGuardar(ad, idSilabo, idUnidad, tipoA);
                    if (silabo.getRol().equals("Doc")) {
                        observacion.updateSubseccionSilabo(ad, silabo, idSubseccion, "Corregido");
                        observacion.updateObservacionSubseccion(ad, actividades.getObservaciones(), silabo, idSubseccion);
                    }
                }
                if (actividades.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSubseccion(ad, actividades.getObservaciones(), silabo, idSubseccion);
                }
                result = "ok";

            }
        } catch (Exception e) {
            Logger.getLogger("ActividadesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String ActividadesEliminar(String jsonActividades) {
        ActividadesAD actividades = G.fromJson(jsonActividades, ActividadesAD.class);
        Silabo silabo = actividades.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        Integer idSubseccion = silabo.getIdTipo();
        String result = "ko";
        try {
            if (ad.Connectar() != 0) {
                int id = 0;
                if (!actividades.getActividades().isEmpty()) {
                    id = actividades.getActividades().get(0).getId_actividades_aprendizaje();
                }
                actividades.eliminarActividadesUsuario(ad, id);
                if (silabo.getRol().equals("Doc")) {
                    observacion.updateSubseccionSilabo(ad, silabo, idSubseccion, "Corregido");
                    observacion.updateObservacionSubseccion(ad, actividades.getObservaciones(), silabo, idSubseccion);
                }
                if (actividades.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSubseccion(ad, actividades.getObservaciones(), silabo, idSubseccion);
                }
                result = ActividadesAprendizajeCargar(new Gson().toJson(silabo));

            }
        } catch (Exception e) {
            Logger.getLogger("ActividadesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

}
