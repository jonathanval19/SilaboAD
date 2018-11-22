/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.reportes.ln;

import dda.silabo.entidad.ln.EntidadLN;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CampoFormacionUnidos;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.clases.unidos.DocenteUnidos;
import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.db.Global;
import dda.silabo.entidad.ad.CampoFormacionAD;
import dda.silabo.login.ln.PeriodoLN;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.opciones.ad.Opciones2AD;
import dda.silabo.reportes.ad.EntidadAD;
import dda.silabo.reportes.ad.ReporteAD;
import dda.silabo.reportes.ad.ReporteGraficoAD;
import ec.edu.espoch.academico.Periodo;
import ec.edu.espoch.academico.Persona;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import redis.clients.jedis.Jedis;
import unidos.sw.CarreraAD;

/**
 *
 * @author Evelyn
 */
public class ReportesLN {

    Global global = new Global();
    Jedis jedis = global.conexion();
    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String ReporteUnidadesAcademicasUsuario(String jsonEntidad, String jsonLogin) throws SQLException {//reporte unidades academicas usuario
        String result = null;
        String peticion = "";
        try {
            if (ad.Connectar() != 0) {

                EntidadAD entidadAD = G.fromJson(jsonEntidad, EntidadAD.class);
                ReporteGraficoAD reporteGraficoAD = new ReporteGraficoAD();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
                EntidadLN entidadLN = new EntidadLN();
                if ((entidadAD.getAvance() == null || (entidadAD.getAvance() - 1) == -1)) {
                    reporteGraficoAD.setAvance(0);
                    jedis.set("EntidadReporte" + jsonLogin + 0, jsonEntidad);
                    jedis.expire("EntidadReporte" + jsonLogin + 0, 1440);
                } else {
                    String jsonEntidadAnterior = jedis.get("EntidadReporte" + jsonLogin + (entidadAD.getAvance() - 1));
                    EntidadAD entidadAD2 = G.fromJson(jsonEntidadAnterior, EntidadAD.class);
                    reporteGraficoAD.setFncClick("generarDetallePdfEntidadAnterior('" + entidadAD2.getCodigo() + "','" + entidadAD2.getNombre() + "','" + entidadAD2.getTipo() + "','reporte','" + entidadAD2.getFncClick() + "','" + (entidadAD.getAvance() - 1) + "');");
                    jedis.set("EntidadReporte" + jsonLogin + (entidadAD.getAvance()), jsonEntidad);
                    jedis.expire("EntidadReporte" + jsonLogin + (entidadAD.getAvance()), 1440);
                    reporteGraficoAD.setAvance((entidadAD.getAvance()));
                }
                if (result == null) {
                    List<EntidadUnidos> entidadesTrabajo = entidadLN.ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());
                    EntidadUnidos entidadesComunesUsuario = entidadAD.agregarEntidadesUsuarioRol(entidadesTrabajo);
                    reporteGraficoAD.agregarInformacionEntidad(entidadesComunesUsuario.getFacultades(), ad, loginAD, periodo.getCodigo(), jedis, entidadAD);
                    result = G.toJson(reporteGraficoAD);
                    jedis.set("Asignaturas_" + loginAD.getRolActivo() + loginAD.getCedula() + periodo.getCodigo(), result);
                }

            }
        } catch (JsonSyntaxException e) {
            return null;
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String ReporteCampoFormacionDocente(String jsonEntidad, String jsonlogin) {//reporte campo formacion docente
        String result = null;
        try {
            if (ad.Connectar() != 0) {

                EntidadAD entidadAD = G.fromJson(jsonEntidad, EntidadAD.class);
                ReporteAD reporteAD = new ReporteAD();
                LoginAD loginAD = G.fromJson(jsonlogin, LoginAD.class);
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                String jsonCarreraCampos = "", jsonCamposFormacion = "";
                CarreraUnidos carreraComun2 = null;
                CampoFormacionUnidos campoFormacion = null;
                if (entidadAD.getTipo().equals("campoformacion")) {
                    jsonCarreraCampos = "{'codCarrera':'" + entidadAD.getCodigo() + "'}";
                    unidos.sw.CarreraAD camposFormacionCarrera = asignaturasCamposFormacion(jsonCarreraCampos);
                    jsonCamposFormacion = G.toJson(camposFormacionCarrera);
                    carreraComun2 = G.fromJson(jsonCamposFormacion, CarreraUnidos.class);
                    campoFormacion = carreraComun2.getCamposFormacion().stream().filter(cp -> cp.getCodCampoF().equals(entidadAD.getNombre())).findFirst().orElse(null);
                    reporteAD.agregarEstadosCamposFormacionDocentes(campoFormacion.getAsignaturas(), ad, periodo.getCodigo(), entidadAD.getCodigo(), loginAD, jedis, campoFormacion.getDescCampoF(), entidadAD.getTipo());

                } else {
                    CampoFormacionAD campoFormacionAD = new CampoFormacionAD();
                    List< AsignaturaUnidos> materias = campoFormacionAD.obtenerMateriasPensumDocente(entidadAD.getNombre(), entidadAD.getFncClick(), periodo.getCodigo());
                    materias = materias.stream().filter(m -> m.getCodArea().equals(entidadAD.getCodigo())).collect(Collectors.toList());
                    Persona p = getDatosUsuarioCarrera(entidadAD.getFncClick(), entidadAD.getNombre());
                    loginAD.setCedula(entidadAD.getNombre());
                    loginAD.setRolActivo("Doc2");
                    reporteAD.agregarEstadosCamposFormacionDocentes(materias, ad, periodo.getCodigo(), entidadAD.getFncClick(), loginAD, jedis, "ING. " + p.getNombres() + p.getApellidos(), entidadAD.getTipo());
                }
                result = G.toJson(reporteAD);

            }
        } catch (JsonSyntaxException e) {
            return "";
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String ReporteCriteriosEntidadUsuario(String jsonReporte, String jsonlogin) throws SQLException {//reporte criterios de silabo
        String result = null;
        try {
            if (ad.Connectar() != 0) {

                EntidadAD entidadAD = G.fromJson(jsonReporte, EntidadAD.class);
                ReporteAD reporteAD = new ReporteAD();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                LoginAD loginAD = G.fromJson(jsonlogin, LoginAD.class);
                EntidadLN entidadLN = new EntidadLN();
                // result = jedis.get("Asignaturas_" + loginAD.getRolActivo() + loginAD.getCedula() + periodo.getCodigo());
                if (result == null) {
                    List<EntidadUnidos> entidadesTrabajo = entidadLN.ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());
                    EntidadUnidos entidadesComunesUsuario = entidadAD.agregarEntidadesUsuarioRol(entidadesTrabajo);
                    reporteAD.agregarCriteriosEntidadUsuario(ad, loginAD, periodo.getCodigo(), entidadesComunesUsuario.getFacultades(), entidadAD);
                    result = G.toJson(reporteAD);
                }

            }
        } catch (JsonSyntaxException e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String ReporteEstadoSilabos(String jsonlogin) throws SQLException {
        String result = null;
        try {
            if (ad.Connectar() != 0) {

                EntidadAD entidadAD = new EntidadAD();
                ReporteAD reporteAD = new ReporteAD();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                LoginAD loginAD = G.fromJson(jsonlogin, LoginAD.class);
                EntidadLN entidadLN = new EntidadLN();
                // result = jedis.get("Asignaturas_" + loginAD.getRolActivo() + loginAD.getCedula() + periodo.getCodigo());
                if (result == null) {
                    List<EntidadUnidos> entidadesTrabajo = entidadLN.ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());
                    EntidadUnidos entidadesComunesUsuario = entidadAD.agregarEntidadesUsuarioRol(entidadesTrabajo);
                    reporteAD.ReporteEstadoSilabos(ad, loginAD, periodo.getCodigo(), entidadesComunesUsuario.getFacultades());
                    result = G.toJson(reporteAD);
                }

            }
        } catch (JsonSyntaxException e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

    private static CarreraAD asignaturasCamposFormacion(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCamposFormacion(jsonCarrera);
    }

    public String ReporteOpcionesUsuario(String jsonlogin) throws SQLException {
        String result = null;
        try {
            if (ad.Connectar() != 0) {

                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                LoginAD loginAD = G.fromJson(jsonlogin, LoginAD.class);
                // result = jedis.get("Asignaturas_" + loginAD.getRolActivo() + loginAD.getCedula() + periodo.getCodigo());
                Opciones2AD opciones2AD = new Opciones2AD();
                if (result == null) {
                    opciones2AD.ReporteOpcionesUsuario(ad, loginAD, periodo);
                    result = G.toJson(opciones2AD);
                }

            }
        } catch (JsonSyntaxException e) {

        } finally {
            ad.Desconectar();
        }
        return result;

    }

    private static Persona getDatosUsuarioCarrera(java.lang.String arg0, java.lang.String arg1) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getDatosUsuarioCarrera(arg0, arg1);
    }

}
