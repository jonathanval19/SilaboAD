/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.datosdocentes.ln;

import com.google.gson.Gson;
import dda.silabo.datosdecentes.ad.DatoDocenteAD;
import dda.silabo.datosdecentes.ad.DatosDocentesAD;
import dda.silabo.db.AccesoDatos;
import dda.silabo.silabo.comunes.Silabo;
import ec.edu.espoch.academico.ArrayOfDictadoMateria;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author Jorge
 */
public class DatosDocentesLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String getDatosdocentes(String jsonSilabo) throws IOException, SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
                DatosDocentesAD datosdocentes = new DatosDocentesAD();
                if (silabo != null) {
                    String codMateria = silabo.getCodMateria();
                    datosdocentes.agregarDocente(codMateria, silabo.getCodCarrera(), ad);
                    if (datosdocentes.getDatosdocentes().size() > 0) {
                        result = G.toJson(datosdocentes);
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String getDatosdocentesInicio(String jsonSilabo) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
                DatosDocentesAD datosdocentes = new DatosDocentesAD();
                if (silabo != null) {
                    String codMateria = silabo.getCodMateria();
                    ArrayOfDictadoMateria arrayOfDictadoMateria = lPADictadosMateria(silabo.getCodCarrera(), codMateria);
                    if (!arrayOfDictadoMateria.getDictadoMateria().isEmpty()) {
                        datosdocentes.agregarDocenteInicio(arrayOfDictadoMateria.getDictadoMateria());
                        result = G.toJson(datosdocentes);
                    }

                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String updateDatosdocentes(String jsonDocente, String jsonSilabo) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                DatoDocenteAD datoDocenteAD = G.fromJson(jsonDocente, DatoDocenteAD.class);
                datoDocenteAD.updateDocente(ad);
                result = getDatosdocentes(jsonSilabo);

            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    private static ArrayOfDictadoMateria lPADictadosMateria(java.lang.String arg0, java.lang.String arg1) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.lPADictadosMateria(arg0, arg1);
    }

}
