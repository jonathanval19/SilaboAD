/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.observaciones.ln;

import com.google.gson.Gson;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidadinformacion.ln.EstructuraDesarrolloLN;
import dda.silabo.ln.SilaboLN;
import dda.silabo.observaciones.ad.ObservacionesAD;

/**
 *
 * @author Jorge Zaruma
 */
public class ObservacionesLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String getObservacionesSeccion(String jsonAsignaturaInfo) {
        ObservacionesAD observaciones = new ObservacionesAD();
        SilaboLN silabo = new SilaboLN();

        try {
            if (ad.Connectar() != 0) {
                Integer idSilabo = silabo.getIdSilabo(jsonAsignaturaInfo, ad);
                observaciones.getObservacionesSeccion(idSilabo, ad);
                EstructuraDesarrolloLN estructura = new EstructuraDesarrolloLN();
                int numUnidades = estructura.getNumeroUnidades(jsonAsignaturaInfo);
                observaciones.getObservacionesSubseccion(idSilabo, ad, numUnidades);
            }
        } catch (Exception e) {

        } finally {
            ad.Desconectar();
        }
        return G.toJson(observaciones);
    }
}
