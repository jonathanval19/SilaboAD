/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.bibliografias.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.panalitico.ws.BibliografiasComunes;
import dda.silabo.bibliografias.ad.BibliografiasAD;
import dda.silabo.db.AccesoDatos;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class BibliografiasLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String guardarBibliografia(String jsonBibliografias) throws SQLException {
        BibliografiasAD bibliografias = G.fromJson(jsonBibliografias, BibliografiasAD.class);
        Silabo silabo = bibliografias.getSilabos();
        ObservacionAD observacion = new ObservacionAD();
        String result = "ko";
        try {
            if (ad.Connectar() != 0) {
                if (bibliografias.getBasica() != null || bibliografias.getComplementaria() != null) {
                    try {
                        if (silabo.getRol().equals("Doc")) {
                            Integer idSilabo = silabo.getIdSilabo();
                            //bibliografias.EliminarBibliografia(ad, idSilabo);
                            bibliografias.guardarBibliografia(ad, idSilabo, "si");
                            observacion.updateSeccionesSilabo(silabo, ad, silabo.getIdTipo(), "Corregido");
                            observacion.updateObservacionSeccion(ad, bibliografias.getObservaciones(), silabo, silabo.getIdTipo());
                        }
                    } catch (JsonSyntaxException e) {
                        Logger.getLogger("BibliografiasLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
                        System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
                    }
                }
                if (bibliografias.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSeccion(ad, bibliografias.getObservaciones(), silabo, silabo.getIdTipo());
                }
                result = "ok";
            }
        } catch (Exception e) {
            ad.getCon().rollback();

        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String getBibliografias(String jsonSilabo) {
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        BibliografiasAD result = new BibliografiasAD();
        if (ad.Connectar() != 0) {
            if (silabo != null) {
                Integer idSilabo = silabo.getIdSilabo();
                String tipo = silabo.getTipo();
                try {
                    MenuLateralAD menuLateralAD = new MenuLateralAD();
                    if ((silabo.getRol().equals("Cor") || silabo.getRol().equals("Dir")) && menuLateralAD.getEstadoSeccion(silabo.getIdSilabo(), silabo.getIdTipo(), ad, "seccion").equals("Corregido")) {
                        menuLateralAD.updateSeccionesMenuSilabo(silabo, silabo.getIdTipo(), ad, "Aprobado");
                    }
                    String SQL = result.BibliografiasSQLSelect(idSilabo);
                    result.obtenerBibliografias(ad, SQL);
                    result.obtenerObservaciones(tipo, silabo, ad);
                    if (result.getBasica().getLibros().isEmpty()) {
                        BibliografiasComunes bc = programaAnaliticoBibliografias(silabo.getCodMateria(), silabo.getCodCarrera());
                        if (!bc.getCodPrograma().equals("0")) {
                            BibliografiasAD bciBibliografiasAD = new BibliografiasAD();
                            bciBibliografiasAD.guardarBibliografiaImportacion(ad, idSilabo, "pa", bc);
                        }
                        result.obtenerBibliografias(ad, SQL);
                    }
                } catch (Exception e) {
                    Logger.getLogger("BibliografiasLN").log(Level.SEVERE, "dda.silabos.ln", e.getClass().getName() + "*****" + e.getMessage());
                    System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
                }
            }
        }
        return G.toJson(result);
    }

    public String deleteBibliografia(String jsonBibliografias) throws SQLException {
        String resp = "ad", result = "";
        if (ad.Connectar() != 0) {
            BibliografiasAD bibliografias = G.fromJson(jsonBibliografias, BibliografiasAD.class);
            Silabo silabo = bibliografias.getSilabos();
            if (bibliografias.getBibliografias().size() > 0 && silabo != null) {
                resp = bibliografias.deleteBibliografia(ad);
            }
            if (resp.equals("ok")) {
                result = getBibliografias(G.toJson(silabo));
            }
        }
        return result;
    }

    private static BibliografiasComunes programaAnaliticoBibliografias(java.lang.String codAsignatura, java.lang.String codCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.programaAnaliticoBibliografias(codAsignatura, codCarrera);
    }

}
