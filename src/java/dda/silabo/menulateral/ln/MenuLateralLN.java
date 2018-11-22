/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.menulateral.ln;

import com.google.gson.Gson;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.AsignaturasAD;
import dda.silabo.ln.SilaboLN;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.silabo.comunes.Silabo;

/**
 *
 * @author Jorge Zaruma
 */
public class MenuLateralLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String getMenuLateral(String jsonAsignaturaInfo) {
        Silabo silaboMenuLateral = G.fromJson(jsonAsignaturaInfo, Silabo.class);
        SilaboLN silaboln = new SilaboLN();

        MenuLateralAD menulateralad = new MenuLateralAD();
        try {
            if (ad.Connectar() != 0) {
                Integer idSilabo = silaboln.getIdSilabo(jsonAsignaturaInfo, ad);
                silaboMenuLateral.setIdSilabo(idSilabo);
                AsignaturasAD asignaturasAD = new AsignaturasAD();
                asignaturasAD.agregarAsignaturasBaseDatos(G.toJson(silaboMenuLateral), ad);
                Silabo silabo = G.fromJson(jsonAsignaturaInfo, Silabo.class);
                silabo.setIdSilabo(idSilabo);
                menulateralad.getNumeroUnidades(G.toJson(silabo), idSilabo, ad);
                menulateralad.getSeccionesSilabo(idSilabo, ad);
                if (menulateralad.getNumUnidades() > 0) {
                    menulateralad.getSubseccionesSilabo(idSilabo, ad, silaboMenuLateral.getRol());

                }
                menulateralad.updateEstadoSilabo(silabo.getIdSilabo(), ad);
                menulateralad.getRolMenu(silaboMenuLateral.getRol());
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return G.toJson(menulateralad);

    }

}
