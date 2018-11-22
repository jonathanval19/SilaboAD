/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.areadocentes.ln;

import com.google.gson.Gson;
import dda.silabo.ad.SilaboAD;
import ec.edu.espoch.academico.ArrayOfRolCarrera;
import ec.edu.espoch.academico.Persona;
import dda.silabo.areadocentes.comunes.DocentesInformacion;

/**
 *
 * @author Jorge Zaruma
 */
public class DocenteInformacionLN {

    Gson G = new Gson();

    public DocentesInformacion getDocenteInformacion(String jsonSilabo) {
        DocentesInformacion result = null;
        SilaboAD silabo = G.fromJson(jsonSilabo, SilaboAD.class);
        String login = silabo.getCedula();
        ArrayOfRolCarrera rolCarrera = getRolUsuarioCarrera(login);
        if (rolCarrera.getRolCarrera().size() > 0) {
            Persona objPersona = getDatosUsuarioCarrera(rolCarrera.getRolCarrera().get(0).getCodigoCarrera(), login);

            if (objPersona != null) {
                result = new DocentesInformacion();
                result.setNombres(objPersona.getNombres());
                result.setApellidos(objPersona.getApellidos());
                result.setCedula(objPersona.getCedula());
                result.setEmail(objPersona.getEmail());
            }
        }
        return result;
    }

    private static Persona getDatosUsuarioCarrera(java.lang.String arg0, java.lang.String arg1) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getDatosUsuarioCarrera(arg0, arg1);
    }

    private static ArrayOfRolCarrera getRolUsuarioCarrera(java.lang.String arg0) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getRolUsuarioCarrera(arg0);
    }

}
