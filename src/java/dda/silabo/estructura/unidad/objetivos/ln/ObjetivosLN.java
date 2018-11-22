/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.objetivos.ln;

import com.google.gson.Gson;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.objetivos.ad.ObjetivosAD;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;

/**
 *
 * @author Jorge Zaruma
 */
public class ObjetivosLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String ObjetivoCargar(String jsonSilabo) {
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        ObjetivosAD objetivo = new ObjetivosAD();
        try {
            if (silabo != null && ad.Connectar() != 0) {

                String SQL = objetivo.ObjetivoSQLSelect(silabo);
                objetivo.ObjetivoCargar(ad, SQL);
                objetivo.obtenerObservaciones(silabo, ad);
                MenuLateralAD menuLateralAD = new MenuLateralAD();
                if ((silabo.getRol().equals("Cor") || silabo.getRol().equals("Dir")) && menuLateralAD.getEstadoSeccion(silabo.getIdSilabo(), silabo.getIdTipo(), ad, "subseccion").equals("Corregido")) {
                    menuLateralAD.updateSubseccionesMenuSilabo(silabo, silabo.getIdTipo(), ad, "Aprobado");
                }

            }
        } catch (Exception e) {

        } finally {
            ad.Desconectar();
        }
        return G.toJson(objetivo);
    }

    public String ObjetivoGuardar(String jsonObjetivos) {
        ObjetivosAD objetivos = G.fromJson(jsonObjetivos, ObjetivosAD.class);
        Silabo silabo = objetivos.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        String result = "ko";
        Integer idSilabo = silabo.getIdSilabo();
        Integer idUnidad = silabo.getIdUnidad();
        try {
            if (ad.Connectar() != 0) {

                if (!objetivos.getObjetivos().isEmpty()) {
                    if (objetivos.getAccion().equals("guardar")) {
                        objetivos.ObjetivoGuardar(ad, idSilabo, idUnidad);
                    } else {
                        objetivos.eliminarObjetivo(ad, objetivos.getObjetivos().get(0).getIdObjetivo());
                    }
                    if (silabo.getRol().equals("Doc")) {
                        observacion.updateSubseccionSilabo(ad, silabo, silabo.getIdTipo(), "Corregido");
                        observacion.updateObservacionSubseccion(ad, objetivos.getObservaciones(), silabo, silabo.getIdTipo());
                    }
                }
                if (objetivos.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSubseccion(ad, objetivos.getObservaciones(), silabo, silabo.getIdTipo());
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
