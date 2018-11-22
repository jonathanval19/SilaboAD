/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.parametros.ln;

import com.google.gson.Gson;
import dda.silabo.ad.SilaboAD;
import dda.silabo.db.AccesoDatos;
import dda.silabo.parametros.ad.ParametrosAD;

/**
 *
 * @author Jorge Zaruma
 */
public class ParametrosLN {

    AccesoDatos ad = new AccesoDatos();
    Gson g = new Gson();

    public String ParametrosCargar(String jsonSilabo) {
        String result = "";
        ParametrosAD parametrosAD = new ParametrosAD();
        try {
            if (ad.Connectar() != 0) {

                SilaboAD silaboAD = g.fromJson(jsonSilabo, SilaboAD.class);
                parametrosAD.ParametrosCargar(ad);
                result = g.toJson(parametrosAD);

            }
        } catch (Exception e) {
            result = null;
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String ParametrosGuardar(String jsonParametros, String jsonSilabo) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                ParametrosAD parametrosAD = g.fromJson(jsonParametros, ParametrosAD.class);
                parametrosAD.ParametrosGuardar(ad);
                result = ParametrosCargar(jsonSilabo);

            }
        } catch (Exception e) {
            result = null;
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String ParametroSilaboCargar(Integer idParametro) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                dda.silabo.parametros.ad.ParametroAD parametroAD = new dda.silabo.parametros.ad.ParametroAD();
                parametroAD.ParametroSilaboCargar(ad, idParametro);
                result = g.toJson(parametroAD);

            }
        } catch (Exception e) {
            result = null;
        } finally {
            ad.Desconectar();
        }
        return result;
    }

}
