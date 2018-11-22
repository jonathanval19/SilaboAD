/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.estrategias.ln;

import com.google.gson.Gson;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.estrategias.ad.EstrategiasAD;
import dda.silabo.estructura.unidad.estrategias.ad.EstrategiasGestionAD;
import dda.silabo.estructura.unidad.estrategias.ad.EstrategiasMetodologicasAD;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class EstrategiasGestionLN {
    
    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();
//ADMINISTRADOTR

    public String EstrategiasCargar(String jsonSilabo) {
        EstrategiasMetodologicasAD result = new EstrategiasMetodologicasAD();
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        try {
            if (silabo != null && ad.Connectar() != 0) {
                
                result.EstrategiasCargar(ad, silabo);
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
    
    public String EstrategiasGuardarAdm(String jsonEstrategias) {
        String resp = "", result = "";
        EstrategiasMetodologicasAD estrategias = G.fromJson(jsonEstrategias, EstrategiasMetodologicasAD.class);
        Silabo silabo = estrategias.getSilabos();
        try {
            if (estrategias.getNivel1().size() > 0 && ad.Connectar() != 0) {
                
                resp = estrategias.guardarEstrategias(ad);
                
            }
            result = EstrategiasCargar(G.toJson(silabo));
        } catch (Exception e) {
            Logger.getLogger("EstrategiasGestionLN").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }
    
    public String EstrategiaEliminarAdm(String jsonEstrategias) throws SQLException {
        String resp = "", result = "";
        EstrategiasGestionAD gestion = G.fromJson(jsonEstrategias, EstrategiasGestionAD.class);
        Silabo silabo = gestion.getSilabos();
        try {
            if (ad.Connectar() != 0) {
                Integer id = gestion.getEstrategias().get(0).getId_estrategia();
                Integer numEstrategia = existeEstrategia(id, ad);
                resp = gestion.deleteEstrategia(id, ad, numEstrategia);
            }
            EstrategiasLN estrategiascargar = new EstrategiasLN();
            result = estrategiascargar.EstrategiasCargar(G.toJson(silabo));
        } catch (Exception e) {
            Logger.getLogger("EstrategiasGestionLN").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }
    
    public String EstrategiaHabilitarAdm(String jsonEstrategias) {
        String resp = "", result = "";
        EstrategiasGestionAD gestion = G.fromJson(jsonEstrategias, EstrategiasGestionAD.class);
        Silabo silabo = gestion.getSilabos();
        try {
            if (gestion.getEstrategias().size() > 0 && ad.Connectar() != 0) {
                Integer id = gestion.getEstrategias().get(0).getId_estrategia();
                resp = gestion.habilitarEstrategia(id, ad);
            }
            if (resp.equals("ok")) {
                EstrategiasLN estrategiascargar = new EstrategiasLN();
                result = estrategiascargar.EstrategiasCargar(G.toJson(silabo));
            }
        } catch (Exception e) {
            Logger.getLogger("EstrategiasGestionLN").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }
    
    private Integer existeEstrategia(Integer id, AccesoDatos ad) {
        Integer result = 0;
        try {
            EstrategiasGestionAD estrategia = new EstrategiasGestionAD();
            result = estrategia.getNumEstrategia(ad, id);
        } catch (Exception e) {
            Logger.getLogger("EstrategiasGestionLN").log(Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }
}
