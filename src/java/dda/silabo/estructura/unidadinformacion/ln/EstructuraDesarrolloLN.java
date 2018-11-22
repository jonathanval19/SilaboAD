/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.estructura.unidadinformacion.ln;

import com.google.gson.Gson;
import dda.panalitico.ws.ContenidosAD;
import dda.panalitico.ws.UnidadComunes;
import dda.silabo.ad.SilaboAD;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidadinformacion.ad.EstructuraDesarrolloAD;
import dda.silabo.estructura.unidadinformacion.ad.SubtemasAD;
import dda.silabo.estructura.unidadinformacion.ad.TemasAD;
import dda.silabo.estructura.unidadinformacion.ad.UnidadesAD;
import dda.silabo.estructura.unidadinformacion.comunes.EstructuraDesarrollo;
import dda.silabo.importacion.ln.SilaboImportacionLN;
import dda.silabo.ln.SilaboLN;
import dda.silabo.menulateral.ad.MenuLateralAD;
import dda.silabo.observaciones.ad.ObservacionAD;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.SQLException;

/**
 *
 * @author Jorge
 */
public class EstructuraDesarrolloLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String UnidadInformacionCargar(String jsonAsignaturaInfo) throws SQLException {
        Silabo silabo = G.fromJson(jsonAsignaturaInfo, Silabo.class);
        EstructuraDesarrolloAD estructuraAD = new EstructuraDesarrolloAD();
        try {
            if (ad.Connectar() != 0) {
                if (estructuraAD.getunidadesSilabo(silabo.getIdSilabo(), ad) != 0) {
                    estructuraAD.getUnidadSilabo(ad, silabo);
                }
                if (!silabo.getRol().equals("Adm")) {
                    estructuraAD.obtenerObservaciones("Unidades", silabo, ad);
                }
                MenuLateralAD menuLateralAD = new MenuLateralAD();
                if ((silabo.getRol().equals("Cor") || silabo.getRol().equals("Dir")) && menuLateralAD.getEstadoSeccion(silabo.getIdSilabo(), silabo.getIdTipo(), ad, "subseccion").equals("Corregido")) {
                    menuLateralAD.updateSubseccionesMenuSilabo(silabo, silabo.getIdTipo(), ad, "Aprobado");
                }

                estructuraAD.setSilabos(silabo);
                estructuraAD.setTitulo("Informacion de la Unidad la misma que ontiene la descriopción de los temas y los subtemas que la conforman");
                estructuraAD.setAyuda("Muestra el contenido de cada Unidad");
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return G.toJson(estructuraAD);
    }

    public Integer getNumeroUnidades(String jsonAsignaturaInfo) {
        Gson G = new Gson();
        int numUnidades = 0;
        try {
            if (ad.Connectar() != 0) {
                Silabo silabo = G.fromJson(jsonAsignaturaInfo, Silabo.class);
                EstructuraDesarrolloAD estructuraAD = new EstructuraDesarrolloAD();
                numUnidades = estructuraAD.getunidadesSilabo(silabo.getIdSilabo(), ad);
                if (numUnidades == 0) {
                    ContenidosAD contenidosAD = programaAnaliticoContenidosAsignatura(silabo.getCodMateria(), silabo.getCodCarrera());
                    if (!contenidosAD.getCodPrograma().equals("0")) {
                        IngresarContenidosImportados(contenidosAD, silabo, ad);
                        if (!contenidosAD.getUnidades().isEmpty()) {
                            numUnidades = contenidosAD.getUnidades().size();
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return numUnidades;
    }

    public Integer getNumeroUnidadesSilabo(String jsonAsignaturaInfo) throws SQLException {
        Integer result = 0;
        Silabo silabo = G.fromJson(jsonAsignaturaInfo, Silabo.class);
        EstructuraDesarrolloAD aD = new EstructuraDesarrolloAD();
        try {
            if (ad.Connectar() != 0) {
                result = aD.getunidadesSilabo(silabo.getIdSilabo(), ad);
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String UnidadInformacionGuardar(String jsonEstructura, int idSilabo) throws SQLException {
        EstructuraDesarrolloAD estructuraAD = G.fromJson(jsonEstructura, EstructuraDesarrolloAD.class);
        ObservacionAD observacion = new ObservacionAD();
        Silabo silabo = estructuraAD.getSilabos();
        try {
            if (ad.Connectar() != 0) {
                if (estructuraAD.getUnidad().size() > 0) {
                    estructuraAD.unidadesGuardar(ad, idSilabo);
                    if (silabo.getRol().equals("Doc")) {
                        observacion.updateSubseccionSilabo(ad, silabo, silabo.getIdTipo(), "Corregido");
                        observacion.updateObservacionSubseccion(ad, estructuraAD.getObservaciones(), silabo, silabo.getIdTipo());
                    }
                }
                if (estructuraAD.getObservaciones().size() > 0 && !silabo.getRol().equals("Doc")) {
                    observacion.updateObservacionSubseccion(ad, estructuraAD.getObservaciones(), silabo, silabo.getIdTipo());
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return UnidadInformacionCargar(G.toJson(estructuraAD.getSilabos()));
    }

    public String TemaEliminar(String jsonEstructura) throws SQLException {
        String result = "";
        try {
            EstructuraDesarrolloAD estructuraDesarrolloAD = G.fromJson(jsonEstructura, EstructuraDesarrolloAD.class);
            if (estructuraDesarrolloAD.getUnidad().size() > 0 && ad.Connectar() != 0) {
                TemasAD temasAD = new TemasAD();
                temasAD.TemaEliminar(estructuraDesarrolloAD.getUnidad().get(0).getTemas().get(0).getId_temas(), ad);
            }
            result = UnidadInformacionCargar(G.toJson(estructuraDesarrolloAD.getSilabos()));
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String SubtemaEliminar(String jsonEstructura) throws SQLException {
        String result = "";
        try {
            EstructuraDesarrolloAD estructuraDesarrolloAD = G.fromJson(jsonEstructura, EstructuraDesarrolloAD.class);
            if (estructuraDesarrolloAD.getUnidad().size() > 0 && ad.Connectar() != 0) {
                SubtemasAD subtemasAD = new SubtemasAD();
                subtemasAD.SubtemaEliminar(estructuraDesarrolloAD.getUnidad().get(0).getTemas().get(0).getSubtemas().get(0).getId_temas_subtemas(), ad);
            }
            result = UnidadInformacionCargar(G.toJson(estructuraDesarrolloAD.getSilabos()));
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String getUnidadesInformacionSilabo(String codCarrera, String codMateria, String codPeriodo) {
        String result = "";
        Silabo silabo = new Silabo();
        silabo.setCodCarrera(codCarrera);
        silabo.setCodMateria(codMateria);
        silabo.setPeriodo(codPeriodo);
        SilaboImportacionLN siln = new SilaboImportacionLN();
        EstructuraDesarrolloAD estructuraAD = new EstructuraDesarrolloAD();
        try {
            if (silabo != null && ad.Connectar() != 0) {
                Integer idSilabo = siln.SilaboImportacionIdSilabo(G.toJson(silabo));
                if (idSilabo != 0) {
                    estructuraAD.getUnidadesSilabo(ad, idSilabo);
                    result = G.toJson(estructuraAD);
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    ///servicios extrenos 
    public EstructuraDesarrolloAD SilaboContenidoAsignatura(String codCarrera, String codPeriodo, String codMateria) {
//        String result = null;
        EstructuraDesarrolloAD estructuraAD = new EstructuraDesarrolloAD();
        try {
            if (ad.Connectar() != 0) {
                try {
                    SilaboAD silaboAD = new SilaboAD();
                    SilaboLN silaboLN = new SilaboLN();
                    Integer unidad = silaboAD.existeAsignaturaPeriodo(codMateria, codPeriodo, ad);
                    if (unidad != 0) {
                        silaboAD.setCodCarrera(codCarrera);
                        silaboAD.setPeriodo(codPeriodo);
                        silaboAD.setCodMateria(codMateria);
                        Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD), ad);
                        estructuraAD.getUnidadesAsignturaPeriodo(ad, idSilabo);
//                    result = G.toJson(estructuraAD);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return estructuraAD;
    }

    private static ContenidosAD programaAnaliticoContenidosAsignatura(java.lang.String codAsginatura, java.lang.String codCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.programaAnaliticoContenidosAsignatura(codAsginatura, codCarrera);
    }

    private void IngresarContenidosImportados(ContenidosAD contenidosAD, Silabo silabo, AccesoDatos ad) {
        try {
            int i = 1;
            for (UnidadComunes unidadComunes : contenidosAD.getUnidades()) {
                UnidadesAD unidadAD = new UnidadesAD();
                unidadAD.IngresarContenidosImportados(ad, silabo.getIdSilabo(), unidadComunes, i);
                i++;
            }
        } catch (Exception e) {
        }
    }

    public String UnidadInformacionGuardarReestablecer(String json) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                EstructuraDesarrolloAD estructuraDesarrolloAD = G.fromJson(json, EstructuraDesarrolloAD.class);
                estructuraDesarrolloAD.UnidadInformacionGuardarReestablecer(ad);
//            result = UnidadInformacionCargar(G.toJson(estructuraDesarrolloAD.getSilabos()));
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;
    }

}
