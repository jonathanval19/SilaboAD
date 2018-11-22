/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.opciones.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.db.AccesoDatos;
import dda.silabo.opciones.ad.OpcionAD;
import dda.silabo.opciones.ad.Opciones2AD;
import dda.silabo.roles.ad.RolAD;
import dda.silabo.roles.comunes.Rol;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class OpcionesLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String getOpciones(String jsonRol) {
        Opciones2AD opcionesad = new Opciones2AD();
        try {
            if (ad.Connectar() != 0) {
                try {
                    Rol rol = G.fromJson(jsonRol, RolAD.class);
                    opcionesad.getOpcionesSilabo(ad, rol.getIdRol());
                } catch (JsonSyntaxException e) {
                    Logger.getLogger("OpcionesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.opciones.ln", e.getClass().getName() + "*****" + e.getMessage());
                    System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return G.toJson(opcionesad);
    }

    public String saveOpciones(String jsonOpciones) throws SQLException {
        try {
            if (ad.Connectar() != 0) {
                Opciones2AD opcionesad = G.fromJson(jsonOpciones, Opciones2AD.class);
                opcionesad.saveOpcionesSilabo(ad);
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("OpcionesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.opciones.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return getOpciones("{'idRol':'0'}");
    }

    public String deleteOpciones(String jsonRol) throws SQLException {
        try {
            if (ad.Connectar() != 0) {

                RolAD rolAD = G.fromJson(jsonRol, RolAD.class);
                OpcionAD opcionAD = new OpcionAD();
                if (rolAD.getOpciones().size() > 0) {
                    Integer numOpcion = opcionAD.existeOpcionAsignada(ad, rolAD.getOpciones().get(0).getIdOpcion());
                    opcionAD.deleteOpcion(ad, numOpcion, rolAD.getOpciones().get(0).getIdOpcion());
                }

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("OpcionesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.opciones.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return getOpciones(jsonRol);
    }

    public String habilitarOpcion(String jsonRol) throws SQLException {
        try {
            if (ad.Connectar() != 0) {

                RolAD rolAD = G.fromJson(jsonRol, RolAD.class);
                OpcionAD opcionAD = new OpcionAD();
                if (rolAD.getOpciones().size() > 0) {
                    opcionAD.habilitarOpcion(ad, rolAD.getOpciones().get(0).getIdOpcion());
                }

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("OpcionesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.opciones.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return getOpciones(jsonRol);
    }

}
