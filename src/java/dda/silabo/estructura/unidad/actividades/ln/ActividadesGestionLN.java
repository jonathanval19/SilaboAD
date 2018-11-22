/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.actividades.ln;

import com.google.gson.Gson;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.actividades.ad.ActividadesGestionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class ActividadesGestionLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    //-----------------------------------------------
    //      ACTIVIDADES DE APRENDIZAJE
    //-----------------------------------------------
    public String GuardarAdmiActividades(String jsonGActividades) {
        String resp = "", result = "";
        ActividadesGestionAD actividades = G.fromJson(jsonGActividades, ActividadesGestionAD.class);
        Silabo silabo = actividades.getSilabos();
        try {
            if (actividades.getActividades().size() > 0 && ad.Connectar() != 0) {

                actividades.guardarActividades(ad);

            }
            ActividadesLN actividadescargar = new ActividadesLN();
            result = actividadescargar.ActividadesAprendizajeCargar(G.toJson(silabo));
        } catch (Exception e) {
            Logger.getLogger("ActividadesGestionLN").log(Level.SEVERE, "dda.silabo.estructura.unidad.actividades.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String ActividadesAdmiEliminar(String jsonActividades) throws SQLException {
        String resp = "", result = "";
        ActividadesGestionAD gestion = G.fromJson(jsonActividades, ActividadesGestionAD.class);
        Silabo silabo = gestion.getSilabos();
        try {
            if (gestion.getActividades().size() > 0 && ad.Connectar() != 0) {
                Integer id = gestion.getActividades().get(0).getId_actividades_aprendizaje();
                Integer numActividad = existeActividad(id, ad);
                resp = gestion.deleteActividad(id, ad, numActividad);
            }
            if (resp.equals("ok")) {
                ActividadesLN actividadescargar = new ActividadesLN();
                result = actividadescargar.ActividadesAprendizajeCargar(G.toJson(silabo));
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String ActividadHabilitarAdm(String jsonActividades) {
        String resp = "", result = "";
        ActividadesGestionAD gestion = G.fromJson(jsonActividades, ActividadesGestionAD.class);
        Silabo silabo = gestion.getSilabos();
        try {
            if (gestion.getActividades().size() > 0 && ad.Connectar() != 0) {
                Integer id = gestion.getActividades().get(0).getId_actividades_aprendizaje();
                resp = gestion.habilitarActividad(id, ad);
            }
            if (resp.equals("ok")) {
                ActividadesLN actividadescargar = new ActividadesLN();
                result = actividadescargar.ActividadesAprendizajeCargar(G.toJson(silabo));
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public Integer existeActividad(Integer id, AccesoDatos ad) {
        Integer result = 0;
        try {
            ActividadesGestionAD actividades = new ActividadesGestionAD();
            result = actividades.getNumActividad(ad, id);
        } catch (Exception e) {
        }
        return result;
    }
}
