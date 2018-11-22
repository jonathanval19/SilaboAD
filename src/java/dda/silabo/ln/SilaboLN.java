/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.ad.SilaboAD;
import dda.silabo.archivos.ad.ArchivoAD;
import dda.silabo.areadocentes.ad.DocentesInformacionAD;
import dda.silabo.datosdecentes.ad.DatosDocentesAD;
import dda.silabo.db.AccesoDatos;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.reportes.ad.EntidadAD;
import dda.silabo.silabo.comunes.Silabo;
import ec.edu.espoch.academico.ArrayOfDictadoMateria;
import ec.edu.espoch.academico.DictadoMateria;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class SilaboLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public Integer getIdSilabo(String jsonAsignaturaInfo, AccesoDatos ad) {
        SilaboAD silabo = G.fromJson(jsonAsignaturaInfo, SilaboAD.class);
        Integer idSilabo = 0;
        try {
            LoginAD loginAD = new LoginAD();
            ArrayOfDictadoMateria docentesMateria = lPADictadosMateria(silabo.getCodCarrera(), silabo.getCodMateria());
            for (DictadoMateria docente : docentesMateria.getDictadoMateria()) {
                if (docente.getDocente().getCedula() != null && silabo.getPeriodo() != null && silabo.getCodMateria() != null) {
                    int idDocente = silabo.getIdDocente(ad, docente.getDocente().getCedula());
                    if (idDocente == 0) {
                        idDocente = loginAD.agregarInformacionUsuario(docente.getDocente(), ad, "OASIS");
                    }
                    EntidadAD entidadAD2 = new EntidadAD();

                    Integer idEntidad = entidadAD2.obteneridEntidad(silabo.getCodCarrera(), ad);
                    int idAsignatura = silabo.getIdAsignatura(ad, idEntidad);
                    int idPeriodo = silabo.getIdPeriodo(ad);
                    int idDocAsgPrd = silabo.getIdAsignaturaDocentePerido(idDocente, idAsignatura, idPeriodo, ad);
                    idSilabo = silabo.getIdsilabo(idDocAsgPrd, ad);

                }
            }
        } catch (Exception e) {
        }
        return idSilabo;
    }

//*************************************************************OBTENER ID SILABO**********************************************************
    public Integer getidSilaboCoordinador(String codMateria, String codigoPeriodo, String rol) {
        SilaboAD silabo = new SilaboAD();
        silabo.setCodMateria(codMateria);
        silabo.setPeriodo(codigoPeriodo);
        Integer idSilabo = 0;
        try {
            if (ad.Connectar() != 0) {
                Integer idAsignatura = silabo.getIdAsignatura(ad, 0);
                Integer idPeriodo = silabo.getIdPeriodo(ad);
                Integer idAsigPeriodo = silabo.idAsignaturaDocentePeridoSelect(idAsignatura, idPeriodo, ad);
                idSilabo = silabo.getIdsilabo(idAsigPeriodo, ad);
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return idSilabo;
    }

    public String estadoSilabo(String jsonSilabo) {
        String estado = "";
        SilaboAD silaboad = G.fromJson(jsonSilabo, SilaboAD.class);
        try {
            if (ad.Connectar() != 0) {
                if (silaboad.idAsignaturaSelect(ad) != 0 && silaboad.idPeriodoSelect(ad) != 0) {
                    Integer idSilabo = getIdSilabo(jsonSilabo, ad);
                    estado = silaboad.getEstadoSilabo(idSilabo, ad);
                }

            }
        } catch (Exception e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return estado;
    }

    public String getEstadoSilabo(String materia, String login, String codPeriodo) {
        String estado = "";
        Silabo silaboDocente = new Silabo();
        silaboDocente.setCedula(login);
        silaboDocente.setCodMateria(materia);
        silaboDocente.setPeriodo(codPeriodo);
        Integer idSilabo = getIdSilaboAnterior(G.toJson(silaboDocente));
        try {
            if (idSilabo > 0 && ad.Connectar() != 0) {
                SilaboAD silaboad = new SilaboAD();
                estado = silaboad.getEstadoSilabo(idSilabo, ad);
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return estado;
    }

    public Integer getIdSilaboAnterior(String jsonAsignaturaInfo) {
        SilaboAD silabo = G.fromJson(jsonAsignaturaInfo, SilaboAD.class);
        Integer idSilabo = -1;
        try {
            if (silabo.getCedula() != null && silabo.getPeriodo() != null && silabo.getCodMateria() != null && ad.Connectar() != 0) {
                int idDocente = silabo.idDocenteSelect(ad, silabo.getCedula());
                int idAsignatura = silabo.idAsignaturaSelect(ad);
                int idPeriodo = silabo.idPeriodoSelect(ad);
                if (idDocente != 0 && idAsignatura != 0 && idPeriodo != 0) {
                    int idDocAsgPrd = silabo.idAsignaturaDocentePeridoSelect(idAsignatura, idPeriodo, ad);
                    if (idDocAsgPrd != 0) {
                        idSilabo = silabo.idSilaboSelect(idDocAsgPrd, ad);
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return idSilabo;
    }

    public Integer PeriodoGuardarInicio(String codigo, AccesoDatos ad) {
        Integer idPeriodo = 0;
        try {

            SilaboAD silaboAD = new SilaboAD();
            silaboAD.setPeriodo(codigo);
            idPeriodo = silaboAD.getIdPeriodo(ad);

        } catch (JsonSyntaxException e) {
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return idPeriodo;
    }

    public String SilaboGenerarPDF(Integer idSilabo, String jsonSilabo) throws SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                Silabo silabo = new Gson().fromJson(jsonSilabo, Silabo.class);
                ArchivoAD archivoAD = new ArchivoAD();
                SilaboAD silaboAD = new SilaboAD();
                String SQL = silaboAD.SilaboGenerarSelect(idSilabo);
                archivoAD.SilaboGenerarPDF(SQL, ad);
                archivoAD.obtenerVigenciaCarrera(silabo);
                result = G.toJson(archivoAD);

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public Integer EstructuraCurricularSilaboID(String jsonSilabo) {
        SilaboAD silaboAD = G.fromJson(jsonSilabo, SilaboAD.class);
        try {
            if (ad.Connectar() != 0) {
                silaboAD.EstructuraCurricularSilaboID(ad);
            }
        } catch (Exception e) {

        } finally {
            ad.Desconectar();
        }
        return silaboAD.getIdSilabo();
    }

    public String AutoridadesSilaboCargar(String jsonSilabo) {
        String result = "";
        SilaboAD silaboad = G.fromJson(jsonSilabo, SilaboAD.class);
        try {
            if (ad.Connectar() != 0) {

                DatosDocentesAD datosDocentesAD = new DatosDocentesAD();
                datosDocentesAD.AutoridadesSilaboCargar(silaboad, ad);
                result = G.toJson(datosDocentesAD);

            }
        } catch (Exception e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
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
