/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.entidad.ln;

import com.google.gson.Gson;
import dda.silabo.silabo.comunes.Silabo;
import dda.silabo.entidad.ad.AsignaturasAD;
import ec.edu.espoch.academico.Materia;

/**
 *
 * @author Jorge
 */
public class AsignaturasLN {

    Gson G = new Gson();

    public String getDatosAsignatura(String jsonSilabo) {
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        AsignaturasAD asignatura = new AsignaturasAD();
        if (silabo != null) {
            String codCarrera = silabo.getCodCarrera();
            String codMateria = silabo.getCodMateria();
            try {
                Materia mat = getDatosMateria(codCarrera, codMateria);
                asignatura.getAsignatura(mat, codCarrera);
            } catch (Exception e) {
            }
        }
        return G.toJson(asignatura);
    }

    private static Materia getDatosMateria(java.lang.String arg0, java.lang.String arg1) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getDatosMateria(arg0, arg1);
    }

}
