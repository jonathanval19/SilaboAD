/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.importacion.ln;

import com.google.gson.Gson;
import dda.silabo.ad.SilaboAD;
import dda.silabo.bibliografias.ad.BibliografiasAD;
import dda.silabo.criteriosevaluaciones.ad.CalificacionesAD;
import dda.silabo.db.AccesoDatos;
import dda.silabo.escenarios.ad.EscenariosAD;
import dda.silabo.estructura.unidad.actividades.ad.ActividadesAD;
import dda.silabo.estructura.unidad.estrategias.ad.EstrategiasAD;
import dda.silabo.estructura.unidad.logros.ad.LogrosAD;
import dda.silabo.estructura.unidad.objetivos.ad.ObjetivosAD;
import dda.silabo.estructura.unidad.recursos.ad.RecursosAD;
import dda.silabo.estructura.unidadinformacion.ln.EstructuraDesarrolloLN;
import dda.silabo.importacion.ad.ImportarSilaboAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SilaboImportacionLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String importarSilabo(String tipo, Integer numeroUnidad, Integer idTipo, Integer idSilaboActual, Integer idSilaboAnterior, Integer unidadAnterior) {
        String resp = "error";
        Silabo silaboActual = new Silabo();
        ObservacionAD observacion = new ObservacionAD();
        try {
            if (ad.Connectar() != 0) {
                silaboActual.setIdSilabo(idSilaboActual);
                if (idSilaboActual != 0 && idSilaboAnterior != 0) {

                    if (numeroUnidad > 0 && tipo.equals("informacionUnidad")) {
                        silaboActual.setIdUnidad(numeroUnidad);
//                        EstructuraDesarrolloAD estructuraDesarrolloAD = new EstructuraDesarrolloAD();
//                        estructuraDesarrolloAD.importarInformacionUnidad(silaboActual, idSilaboAnterior, ad, unidadAnterior);
                        ObjetivosAD objetivosad = new ObjetivosAD();
                        objetivosad.importarObjetivos(silaboActual, idSilaboAnterior, ad, unidadAnterior);
                        EstrategiasAD estrategiasad = new EstrategiasAD();
                        estrategiasad.importarEstrategias(silaboActual, idSilaboAnterior, ad, unidadAnterior);
                        RecursosAD recursosad = new RecursosAD();
                        recursosad.importarRecursos(silaboActual, idSilaboAnterior, ad, unidadAnterior);
                        ActividadesAD actividadesad = new ActividadesAD();
                        actividadesad.importarActividades(silaboActual, idSilaboAnterior, ad, unidadAnterior);
                        LogrosAD logrosad = new LogrosAD();
                        logrosad.importarLogros(silaboActual, idSilaboAnterior, ad, unidadAnterior);
                        resp = "ok";
                    } else if (tipo.equals("Bibliografias")) {
                        BibliografiasAD bilbiografiasad = new BibliografiasAD();
                        bilbiografiasad.importarBibliografias(silaboActual, idSilaboAnterior, ad);

                        observacion.updateSeccionesSilabo(silaboActual, ad, idTipo, "Corregido");
                        resp = "ok";
                    } else if (tipo.equals("Escenarios")) {
                        EscenariosAD escenariosad = new EscenariosAD();
                        escenariosad.importarEscenarios(silaboActual, idSilaboAnterior, ad);
                        resp = "ok";
                    } else if (tipo.equals("Criterios")) {
                        CalificacionesAD calificacionesad = new CalificacionesAD();
                        calificacionesad.importarCalificaciones(silaboActual, idSilaboAnterior, ad);
                        observacion.updateSeccionesSilabo(silaboActual, ad, idTipo, "Corregido");
                        resp = "ok";
                    }
//                      
//                      
//
//                        SilaboAD silaboad = new SilaboAD();
//                        silaboad.asignarEstadoSilabo(idSilaboActual, ad);

                } else {
                    resp = "error";
                }

            }
        } catch (Exception e) {
            Logger.getLogger("SilaboImportacionLN").log(Level.SEVERE, "dda.silabo.importacion.ln;", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return resp;
    }

    private Integer getnumeroUnidades(String jsonSilaboActual, String jsonSilaboAnterior) {
        Integer numUnidades = 0;
        EstructuraDesarrolloLN estructuraln = new EstructuraDesarrolloLN();
        int numUnidadesActual = estructuraln.getNumeroUnidades(jsonSilaboActual);
        int numUnidadesAnterior = estructuraln.getNumeroUnidades(jsonSilaboAnterior);
        if (numUnidadesActual == numUnidadesAnterior) {
            numUnidades = numUnidadesActual;
        }
        if (numUnidadesActual < numUnidadesAnterior) {
            numUnidades = numUnidadesActual;
        }
        if (numUnidadesAnterior < numUnidadesActual) {
            numUnidades = numUnidadesAnterior;
        }
        return numUnidades;
    }

    public String getCarrerasPeriodos(String jsonDocenteSilabo) {
        String result = "";
        Silabo docenteSilabo = G.fromJson(jsonDocenteSilabo, Silabo.class);
        String login = docenteSilabo.getCedula();
        ImportarSilaboAD importarad = new ImportarSilaboAD();
        try {
            if (ad.Connectar() != 0) {
                importarad.asignarAsignaturasPeriodos(docenteSilabo.getCodCarrera(), login, ad);
                if (!importarad.getCarreras().isEmpty()) {
                    result = G.toJson(importarad);
                } else {
                    result = "vacio";
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public Integer SilaboImportacionIdSilabo(String jsonSilaboAnterior) {
        Integer result = 0;
        try {
            SilaboAD silaboAD = new SilaboAD();
            Silabo silaboAnterior = G.fromJson(jsonSilaboAnterior, Silabo.class);

            if (ad.Connectar() != 0) {
                result = silaboAD.existeAsignaturaPeriodo(silaboAnterior.getCodMateria(), silaboAnterior.getPeriodo(), ad);
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }
}
