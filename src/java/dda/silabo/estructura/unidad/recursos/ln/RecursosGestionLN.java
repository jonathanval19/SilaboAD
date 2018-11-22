/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidad.recursos.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidad.recursos.ad.RecursosGestionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class RecursosGestionLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    //-----------------------------ADMINISTRADOR
    public String GuardarAdmiRecursos(String jsonRecursos) throws SQLException {
        RecursosGestionAD recursos = G.fromJson(jsonRecursos, RecursosGestionAD.class);
        Silabo silabo = recursos.getSilabos();
        try {
            if (recursos.getRecursos().size() > 0 && ad.Connectar() != 0) {

                recursos.GuardarAdmiRecursos(ad);

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RecursosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        RecursosLN recursoscargar = new RecursosLN();
        return recursoscargar.RecursosCargar(G.toJson(silabo));
    }

    public String RecursosEliminarAdm(String jsonRecursos) throws SQLException {
        RecursosGestionAD eliminar = G.fromJson(jsonRecursos, RecursosGestionAD.class);
        Silabo silabo = eliminar.getSilabos();
        String resp = "", result = "";
        Integer id;
        try {
            if (eliminar.getRecursos().size() > 0 && ad.Connectar() != 0) {
                id = eliminar.getRecursos().get(0).getIdRecurso();
                Integer numRecurso = existeRecurso(id, ad);
                try {
                    resp = eliminar.RecursosEliminarAdm(id, numRecurso, ad);
                } catch (JsonSyntaxException e) {
                    Logger.getLogger("RecursosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
                    System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
                }
            }
            RecursosLN recursoscargar = new RecursosLN();
            result = recursoscargar.RecursosCargar(G.toJson(silabo));
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RecursosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String RecursoHabilitarAdm(String jsonRecursos) throws SQLException {
        RecursosGestionAD eliminar = G.fromJson(jsonRecursos, RecursosGestionAD.class);
        Silabo silabo = eliminar.getSilabos();
        String resp = "", result = "";
        Integer id;
        try {
            if (eliminar.getRecursos().size() > 0 && ad.Connectar() != 0) {
                id = eliminar.getRecursos().get(0).getIdRecurso();

                resp = eliminar.RecursoHabilitarAdm(id, ad);

            }
            if (resp.equals("ok")) {
                RecursosLN recursoscargar = new RecursosLN();
                result = recursoscargar.RecursosCargar(G.toJson(silabo));
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RecursosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public Integer existeRecurso(int id, AccesoDatos ad) throws SQLException {
        Integer result = 0;
        try {
            RecursosGestionAD recursos = new RecursosGestionAD();
            result = recursos.getNumRecurso(ad, id);
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RecursosLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

}
