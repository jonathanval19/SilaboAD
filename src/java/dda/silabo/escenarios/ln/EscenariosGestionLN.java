/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.escenarios.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.db.AccesoDatos;
import dda.silabo.escenarios.ad.EscenariosGestionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class EscenariosGestionLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String GuardarAdmiEscenarios(String jsonEscenarios) {
        String resp = "", result = "";
        try {
            if (ad.Connectar() != 0) {
                EscenariosGestionAD escenarios = G.fromJson(jsonEscenarios, EscenariosGestionAD.class);
                Silabo silabo = escenarios.getSilabos();
                if (escenarios.getEscenarios().size() > 0) {
                    resp = escenarios.GuardarAdmiEscenarios(ad);
                }
                EscenariosLN escenarioscargar = new EscenariosLN();
                result = escenarioscargar.EscenariosCargar(G.toJson(silabo));
            }
        } catch (Exception e) {
            Logger.getLogger("EscenariosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String EscenariosAdmiEliminar(String jsonEscenarios) {
        String resp = "", result = "";
        try {
            if (ad.Connectar() != 0) {
                EscenariosGestionAD eliminar = G.fromJson(jsonEscenarios, EscenariosGestionAD.class);
                Silabo silabo = eliminar.getSilabos();
                if (eliminar.getEscenarios().size() > 0) {

                    Integer id = eliminar.getEscenarios().get(0).getIdEsc();
                    Integer numEscenario = existeEscenario(id, ad);
                    resp = eliminar.EscenariosAdmiEliminar(id, ad, numEscenario);

                }
                    EscenariosLN escenarioscargar = new EscenariosLN();
                    result = escenarioscargar.EscenariosCargar(G.toJson(silabo));
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("EscenariosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;

    }

    public String EscenarioHabilitarAdm(String jsonEscenarios) {
        String resp = "", result = "";
        try {
            if (ad.Connectar() != 0) {
                EscenariosGestionAD eliminar = G.fromJson(jsonEscenarios, EscenariosGestionAD.class);
                Silabo silabo = eliminar.getSilabos();
                if (eliminar.getEscenarios().size() > 0) {

                    Integer id = eliminar.getEscenarios().get(0).getIdEsc();
                    resp = eliminar.HabilitarEscenario(id, ad);

                }
                if (resp.equals("ok")) {
                    EscenariosLN escenarioscargar = new EscenariosLN();
                    result = escenarioscargar.EscenariosCargar(G.toJson(silabo));
                }
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("EscenariosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;

    }

    private Integer existeEscenario(Integer id, AccesoDatos ad) {
        Integer result = 0;
        try {
            EscenariosGestionAD actividades = new EscenariosGestionAD();
            result = actividades.getNumEscenario(ad, id);
        } catch (Exception e) {
        }
        return result;
    }

}
