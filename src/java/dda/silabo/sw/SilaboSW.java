/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.sw;

import com.google.gson.Gson;
import dda.silabo.ad.NewMain;
import dda.silabo.archivos.ln.ArchivoLN;
import dda.silabo.archivos.ln.ArchivoPublicacionLN;
import dda.silabo.areadocentes.comunes.DocentesInformacion;
import dda.silabo.criteriosevaluaciones.ln.CalificacionesLN;
import dda.silabo.estructura.unidad.actividades.ln.ActividadesLN;
import dda.silabo.bibliografias.ln.BibliografiasLN;
import dda.silabo.datosgenerales.ln.DatosGeneralesLN;
import dda.silabo.escenarios.ln.EscenariosLN;
import dda.silabo.estructura.unidad.estrategias.ln.EstrategiasLN;
import dda.silabo.criteriosevaluaciones.ln.CriteriosEvaluacionesLN;
import dda.silabo.entidad.ln.AsignaturasLN;
import dda.silabo.estructura.unidad.recursos.ln.RecursosLN;
import dda.silabo.ln.SilaboLN;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import dda.silabo.areadocentes.ln.DocenteInformacionLN;
import dda.silabo.escenarios.ln.EscenariosGestionLN;
import dda.silabo.estructura.unidad.actividades.ln.ActividadesGestionLN;
import dda.silabo.estructura.unidad.estrategias.ln.EstrategiasGestionLN;
import dda.silabo.estructura.unidad.logros.ln.LogrosLN;
import dda.silabo.estructura.unidad.objetivos.ln.ObjetivosLN;
import dda.silabo.estructura.unidad.recursos.ln.RecursosGestionLN;
import dda.silabo.estructura.unidadinformacion.ln.EstructuraDesarrolloLN;
import dda.silabo.menulateral.ln.MenuLateralLN;
import dda.silabo.observaciones.ln.ObservacionesLN;
import dda.silabo.datosdocentes.ln.DatosDocentesLN;
import dda.silabo.db.AccesoDatos;
import dda.silabo.importacion.ln.SilaboImportacionLN;
import dda.silabo.login.ln.LoginLN;
import dda.silabo.login.ln.PeriodoLN;
import dda.silabo.opciones.ln.OpcionesLN;
import dda.silabo.entidad.ln.EntidadLN;
import dda.silabo.parametros.ln.ParametrosLN;
import dda.silabo.reportes.ln.ReportesLN;
import dda.silabo.roles.ln.RolesLN;
import dda.silabo.usuarios.ln.UsuariosLN;
import ec.edu.espoch.academico.Periodo;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import javax.jws.WebParam;

/**
 *
 * @author user
 */
@WebService(serviceName = "SilaboSW")
public class SilaboSW {

    //*************************************************************************************************************
    @WebMethod(operationName = "SilaboIdCargar")
    public Integer getIdSilabo(String jsonAsignaturaInfo) {
        Integer result = 0;
        SilaboLN silabo = new SilaboLN();
        AccesoDatos ad = new AccesoDatos();
        try {

            ad.Connectar();
            result = silabo.getIdSilabo(jsonAsignaturaInfo, ad);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }

        return result;
    }

    //*************************************************************************************************************
    //***********INICIO DE DOCUMENTO***************//
    @WebMethod(operationName = "AsignaturaCargar")
    public String AsignaturaCargar(String jsonSilabo) {
        String result = "";
        AsignaturasLN materias = new AsignaturasLN();
        try {
            result = materias.getDatosAsignatura(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }

        return result;
    }
//******************************************//
//***********1DATOS GENERALES***************//

    @WebMethod(operationName = "DatosGeneralesCargar")
    public String DatosGeneralesCargar(String jsonSilabo) {

        DatosGeneralesLN objLN = new DatosGeneralesLN(jsonSilabo);
        try {
            jsonSilabo = objLN.getDatosGenerales();
        } catch (Exception e) {
            jsonSilabo = null;
        }
        return jsonSilabo;
    }
//******************************************//
//*********2ESTRUCTURA Y DESARROLLO*********// 
//UNIDAD//

    @WebMethod(operationName = "UnidadInformacionCargar")//revisado
    public String UnidadInformacionCargar(String jsonAsignaturaInfo) {
        String resp = "";
        EstructuraDesarrolloLN estructura = new EstructuraDesarrolloLN();
        try {
            resp = estructura.UnidadInformacionCargar(jsonAsignaturaInfo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    @WebMethod(operationName = "UnidadInformacionSilabo")//revisado
    public String UnidadInformacionSilabo(String codCarrera, String codMateria, String codPeriodo) {
        String resp = "";
        EstructuraDesarrolloLN estructura = new EstructuraDesarrolloLN();
        try {
            resp = estructura.getUnidadesInformacionSilabo(codCarrera, codMateria, codPeriodo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    @WebMethod(operationName = "UnidadInformacionGuardar")//revisado
    public String UnidadInformacionGuardar(String jsonEstructura) {
        String resp = "";
        EstructuraDesarrolloLN estructura = new EstructuraDesarrolloLN();
        try {
            resp = estructura.UnidadInformacionGuardar(jsonEstructura, 0);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    @WebMethod(operationName = "UnidadInformacionGuardarReestablecer")//revisado
    public String UnidadInformacionGuardarReestablecer(String json) {
        String resp = "";
        EstructuraDesarrolloLN estructura = new EstructuraDesarrolloLN();
        try {
            resp = estructura.UnidadInformacionGuardarReestablecer(json);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    @WebMethod(operationName = "TemaEliminar")
    public String TemaEliminar(String jsonEstructura) {
        String resp = "";
        EstructuraDesarrolloLN estructura = new EstructuraDesarrolloLN();
        try {
            resp = estructura.TemaEliminar(jsonEstructura);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    @WebMethod(operationName = "SubtemaEliminar")
    public String SubtemaEliminar(String jsonEstructura) {
        String resp = "";
        EstructuraDesarrolloLN estructura = new EstructuraDesarrolloLN();
        try {
            resp = estructura.SubtemaEliminar(jsonEstructura);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    @WebMethod(operationName = "ObjetivoCargar")//revisado
    public String ObjetivoCargar(String jsonSilabo) {
        String resp = "";
        ObjetivosLN objetivo = new ObjetivosLN();
        try {
            resp = objetivo.ObjetivoCargar(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    @WebMethod(operationName = "ObjetivoGuardar")
    public String ObjetivoGuardar(String jsonObjetivos) {
        String resp = "";
        ObjetivosLN objetivo = new ObjetivosLN();
        try {
            resp = objetivo.ObjetivoGuardar(jsonObjetivos);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "LogroCargar")
    public String LogroCargar(String jsonSilabo) {
        String resp = "";
        LogrosLN logro = new LogrosLN();
        try {
            resp = logro.LogroCargar(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    @WebMethod(operationName = "LogroGuardar")
    public String LogroGuardar(String jsonLogros) {
        String resp = "";
        LogrosLN logro = new LogrosLN();
        try {
            resp = logro.LogroGuardar(jsonLogros);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;

    }

    //-------
//ESTRATEGIAS METODOLOGICAS//
    @WebMethod(operationName = "EstrategiasCargar")
    public String EstrategiasCargar(String jsonSilabo) {
        //crear iuna lista para devolver
        String result = "";
        try {
            EstrategiasGestionLN estrategia = new EstrategiasGestionLN();
            result = estrategia.EstrategiasCargar(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    //??????????????????????????????????????????????????JSON
    @WebMethod(operationName = "EstrategiasGuardar")
    public String EstrategiasGuardar(String jsonEstrategias) {
        EstrategiasLN estrategia = new EstrategiasLN();
        String resp = "";
        try {
            resp = estrategia.EstrategiasGuardar(jsonEstrategias);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }
//---------------------------
//RECURSOS//

    @WebMethod(operationName = "RecursosCargar")
    public String RecursosCargar(String jsonSilabo) {
        //crear iuna lista para devolver
        String result = "";
        try {
            RecursosLN eRecursos = new RecursosLN();
            result = eRecursos.RecursosCargar(jsonSilabo);

            //result.setObservaciones(...);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    //??????????????????????????????????????????????????JSON
    @WebMethod(operationName = "RecursosGuardar")
    public String GuardarRecursosAD(String strRecursos) {
        RecursosLN recursos = new RecursosLN();
        String result = "";
        try {
            result = recursos.GuardarRecursos(strRecursos);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }
//----------
//ACTIVIDADES DE APRENDIZAJE//

    @WebMethod(operationName = "ActividadesAprendizajeCargar")
    public String ActividadesAprendizajeCargar(String jsonSilabo) {
        String result = "";
        try {
            ActividadesLN eActividades = new ActividadesLN();
            result = eActividades.ActividadesAprendizajeCargar(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ActividadesAprendizajeGuardar")
    public String ActividadesAprendizajeGuardar(String jsonActividades) {
        ActividadesLN actividades = new ActividadesLN();
        String resp = "";
        try {
            resp = actividades.ActividadesAprendizajeGuardar(jsonActividades);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "ActividadesEliminar")
    public String ActividadesEliminar(String jsonActividades) {
        ActividadesLN actividades = new ActividadesLN();
        String resp = "";
        try {
            resp = actividades.ActividadesEliminar(jsonActividades);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }
//----------------------------
//******************************************//
//********3ESCENARIOS DE APRENDIZAJE********//

    @WebMethod(operationName = "EscenariosCargar")
    public String EscenariosCargar(String jsonSilabo) {
        //crear iuna lista para devolver
        String result = "";
        try {
            EscenariosLN eEscenarios = new EscenariosLN();
            result = eEscenarios.EscenariosCargar(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

//??????????????????????????????????????????????????JSON
    @WebMethod(operationName = "EscenariosGuardar")
    public String EscenariosGuardar(String jsonEscenarios) {
        //crear el sql 
        EscenariosLN escenario = new EscenariosLN();
        String resp = "";
        try {
            resp = escenario.EscenariosGuardar(jsonEscenarios);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }
//******************************************//
//**********4CRITERIOS DE EVALUACION********//

    @WebMethod(operationName = "EvaluacionesCargar")
    public String EvaluacionesCargar(String jsonSilabo) {
        String result = null;
        CriteriosEvaluacionesLN evaluaciones = new CriteriosEvaluacionesLN();
        try {
            result = evaluaciones.getEvaluaciones(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("SilaboSW").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }
//??????????????????????????????????????????????????JSON

    @WebMethod(operationName = "CalificacionesGuardar")
    public String CalificacionesGuardar(String jsonEvaluaciones) {
        CalificacionesLN calificaciones = new CalificacionesLN();
        String resp = "";
        try {
            resp = calificaciones.GuardarCalificacionesAD(jsonEvaluaciones);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }
//******************************************//
//****************5BIBLIOGRAFIA*************//

    @WebMethod(operationName = "BibliografiasCargar")
    public String BibliografiasCargar(String jsonSilabo) {
        String result = "";
        BibliografiasLN bibliografia = new BibliografiasLN();
        try {
            result = bibliografia.getBibliografias(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "BibliografiasGuardar")
    public String BibliografiasGuardar(String jsonBibliografias) {
        BibliografiasLN bibliografia = new BibliografiasLN();
        String resp = "sw";
        try {
            resp = bibliografia.guardarBibliografia(jsonBibliografias);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "BibliografiasEliminar")
    public String BibliografiasEliminar(String jsonBibliografias) {
        String result = "sa";
        BibliografiasLN bibliografia = new BibliografiasLN();
        try {
            result = bibliografia.deleteBibliografia(jsonBibliografias);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }
//******************************************//
//****************6DATOS DOCENTE*************//

    @WebMethod(operationName = "DatosDocentesCargar")
    public String DatosDocentesCargar(String jsonSilabo) {
        //crear iuna lista para devolver
        String result = "";
        DatosDocentesLN docente = new DatosDocentesLN();
        try {
            result = docente.getDatosdocentes(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "DatosDocentesInicioCargar")
    public String DatosDocentesInicioCargar(String jsonSilabo) {
        //crear iuna lista para devolver
        String result = "";
        DatosDocentesLN docente = new DatosDocentesLN();
        try {
            result = docente.getDatosdocentesInicio(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "DatosDocenteActualizar")
    public String DatosDocenteActualizar(String jsonDocente, String jsonSilabo) {
        //crear iuna lista para devolver
        String result = "";
        DatosDocentesLN docente = new DatosDocentesLN();
        try {
            result = docente.updateDatosdocentes(jsonDocente, jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }
//******************************************//

//******************************************//
//---------------------------------ADMINISTRADOR------------------------------------------------------------!
    @WebMethod(operationName = "RecursosGuardarAdm")
    public String RecursosGuardarAdm(String jsonRecursos) {
        RecursosGestionLN recursos = new RecursosGestionLN();
        String resp = "";
        try {
            //se creara el metodo que me permite guardar el objeto obtenido
            resp = recursos.GuardarAdmiRecursos(jsonRecursos);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "RecursosEliminarAdm")
    public String RecursosEliminarAdm(String jsonRecurso) {
        RecursosGestionLN recursos = new RecursosGestionLN();
        String resp = "";
        try {
            resp = recursos.RecursosEliminarAdm(jsonRecurso);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "RecursoHabilitarAdm")
    public String RecursoHabilitarAdm(String jsonRecursos) {
        RecursosGestionLN recursos = new RecursosGestionLN();
        String resp = "";
        try {
            resp = recursos.RecursoHabilitarAdm(jsonRecursos);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    //---------------------------------ESTRATEGIAS------------------------------------------------------------!
    @WebMethod(operationName = "EstrategiasGuardarAdm")
    public String EstrategiasGuardarAdm(String jsonEstrategias) {
        EstrategiasGestionLN estrategia = new EstrategiasGestionLN();
        String resp = "";
        try {
            //se creara el metodo que me permite guardar el objeto obtenido
            resp = estrategia.EstrategiasGuardarAdm(jsonEstrategias);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "EstrategiasEliminarAdm")
    public String EstrategiasEliminarAdm(String jsonEstrategia) {
        EstrategiasGestionLN estrategia = new EstrategiasGestionLN();
        String resp = "ad";
        try {
            //se creara el metodo que me permite guardar el objeto obtenido
            resp = estrategia.EstrategiaEliminarAdm(jsonEstrategia);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "EstrategiaHabilitarAdm")
    public String EstrategiaHabilitarAdm(String jsonEstrategia) {
        EstrategiasGestionLN estrategia = new EstrategiasGestionLN();
        String resp = "ad";
        try {
            //se creara el metodo que me permite guardar el objeto obtenido
            resp = estrategia.EstrategiaHabilitarAdm(jsonEstrategia);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    //**************************************************************************//
    //                          ACTIVIDADES DE APRENDIZAJE                      //
    //**************************************************************************//
    @WebMethod(operationName = "ActividadesGuardarAdm")
    public String ActividadesGuardarAdm(String jsonActividades) {
        ActividadesGestionLN actividades = new ActividadesGestionLN();
        String resp = "";
        try {
            //se creara el metodo que me permite guardar el objeto obtenido
            resp = actividades.GuardarAdmiActividades(jsonActividades);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "ActividadesEliminarAdm")
    public String ActividadesEliminarAdm(String jsonActividad) {

        ActividadesGestionLN actividades = new ActividadesGestionLN();
        String resp = "ad";
        try {
            //se creara el metodo que me permite guardar el objeto obtenido
            resp = actividades.ActividadesAdmiEliminar(jsonActividad);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "ActividadHabilitarAdm")
    public String ActividadHabilitarAdm(String jsonActividad) {

        ActividadesGestionLN actividades = new ActividadesGestionLN();
        String resp = "ad";
        try {
            //se creara el metodo que me permite guardar el objeto obtenido
            resp = actividades.ActividadHabilitarAdm(jsonActividad);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    //**************************************************************************//
    //                          ESCENARIOS DE APRENDIZAJE                       //
    //**************************************************************************//
    @WebMethod(operationName = "EscenariosGuardarAdm")
    public String EscenariosGuardarAdm(String jsonEscenarios) {
        EscenariosGestionLN escenarios = new EscenariosGestionLN();
        String resp = "";
        try {
            resp = escenarios.GuardarAdmiEscenarios(jsonEscenarios);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "EscenariosEliminarAdm")
    public String EscenariosEliminarAdm(String jsonEscenario) {
        EscenariosGestionLN escenarios = new EscenariosGestionLN();
        String resp = "";
        try {
            //se creara el metodo que me permite guardar el objeto obtenido
            resp = escenarios.EscenariosAdmiEliminar(jsonEscenario);

        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "EscenarioHabilitarAdm")
    public String EscenarioHabilitarAdm(String jsonEscenario) {
        EscenariosGestionLN escenarios = new EscenariosGestionLN();
        String resp = "";
        try {
            resp = escenarios.EscenarioHabilitarAdm(jsonEscenario);

        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return resp;
    }
/////////////////////////////////////////////PDF////////////////////////////////////////////
    ////////////////////////////UNIDADES  ACADEMICAS //////////////////////////////////////////////////////////////////////////////////

    @WebMethod(operationName = "MenuSilaboCargar")
    public String MenuSilaboCargar(String jsonAsignaturaInfo) {
        String result = "";
        try {
            MenuLateralLN menulateralln = new MenuLateralLN();
            result = menulateralln.getMenuLateral(jsonAsignaturaInfo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "SilaboImportacionGenerar")
    public String SilaboImportacionGenerar(String tipo, Integer numeroUnidad, Integer idTipo, Integer idSilaboActual, Integer idSilaboAnterior, Integer unidadAnterior) {
        SilaboImportacionLN importacionln = new SilaboImportacionLN();
        String result = "error";
        try {
            result = importacionln.importarSilabo(tipo, numeroUnidad, idTipo, idSilaboActual, idSilaboAnterior, unidadAnterior);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "SilaboImportacionIdSilabo")
    public Integer SilaboImportacionIdSilabo(String jsonSilaboAnterior) {
        SilaboImportacionLN importacionln = new SilaboImportacionLN();
        Integer result = 0;
        try {
            result = importacionln.SilaboImportacionIdSilabo(jsonSilaboAnterior);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "SilaboEstadoCargar")
    public String SilaboEstadoCargar(String jsonSilabo) {
        SilaboLN silaboln = new SilaboLN();
        String result = "";
        try {
            result = silaboln.estadoSilabo(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "AsignaturasPeriodos")
    public String AsignaturasPeriodos(String jsonSilaboDocente) {
        SilaboImportacionLN importacionln = new SilaboImportacionLN();
        String result = "x";
        try {
            result = importacionln.getCarrerasPeriodos(jsonSilaboDocente);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "DocenteInformacionCargar")
    public String DocenteInformacionCargar(String jsonSilabo) {
        DocenteInformacionLN lLoadDocenteInformacion = new DocenteInformacionLN();
        DocentesInformacion result = new DocentesInformacion();
        try {
            result = lLoadDocenteInformacion.getDocenteInformacion(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        Gson G = new Gson();
        return G.toJson(result);
    }

    ////////////////////////////////////////////////////OBSERVACIONES ////////////////////////////////////////////////////////////////////////////
    @WebMethod(operationName = "ObservacionesSilaboCargar")
    public String ObservacionesSilaboCargar(String jsonSilabo) {
        String result = "";
        try {
            ObservacionesLN observaciones = new ObservacionesLN();
            result = observaciones.getObservacionesSeccion(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "PeriodoActual")
    public Periodo PeriodoActual() {
        Periodo result = null;
        try {
            PeriodoLN periodoLN = new PeriodoLN();
            result = periodoLN.getPeriodoValido();
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }

        return result;
    }

    ///////////////////////////////////////OPCIONES USUARIO////////////////////////////////////////////////////
    @WebMethod(operationName = "OpcionesCargar")
    public String OpcionesCargar(String jsonRol) {
        String result = "";
        try {
            OpcionesLN opciones = new OpcionesLN();
            result = opciones.getOpciones(jsonRol);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "OpcionesGuardar")
    public String OpcionesGuardar(String jsonOpciones) {
        String result = "";
        try {
            OpcionesLN opciones = new OpcionesLN();
            result = opciones.saveOpciones(jsonOpciones);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "OpcionEliminar")
    public String OpcionEliminar(String jsonOpcion) {
        String result = "";
        try {
            OpcionesLN opciones = new OpcionesLN();
            result = opciones.deleteOpciones(jsonOpcion);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "OpcionHabilitar")
    public String OpcionHabilitar(String jsonOpcion) {
        String result = "";
        try {
            OpcionesLN opciones = new OpcionesLN();
            result = opciones.habilitarOpcion(jsonOpcion);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    ///////////////////////////////////ROLES/////////////////////////////////////////////////////
    @WebMethod(operationName = "RolesGuardar")
    public String RolesGuardar(String jsonRoles) {
        String result = "";
        try {
            RolesLN rolln = new RolesLN();
            result = rolln.GuardarRoles(jsonRoles);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolesCargar")
    public String RolesCargar(String jsonLogin) {
        String result = "";
        try {
            RolesLN rolesln = new RolesLN();
            result = rolesln.getRoles(jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolEliminar")
    public String RolEliminar(String jsonRol, String jsonLogin) {
        String result = "";
        try {
            RolesLN rolesln = new RolesLN();
            result = rolesln.deleteRol(jsonRol, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolHabilitar")
    public String RolHabilitar(String jsonRol, String jsonLogin) {
        String result = "";
        try {
            RolesLN rolesln = new RolesLN();
            result = rolesln.HabilitarRol(jsonRol, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolesOpcionesCargar")
    public String RolesOpcionesCargar(String jsonOpcion) {
        String result = "";
        try {
            OpcionesLN opciones = new OpcionesLN();
            result = opciones.deleteOpciones(jsonOpcion);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolOpcionesGuardar")
    public String RolOpcionesGuardar(String jsonORolOpciones, String jsonLogueo) {
        String result = "";
        try {
            RolesLN rolesLN = new RolesLN();
            result = rolesLN.RolOpcionesGuardar(jsonORolOpciones, jsonLogueo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolEntidadesCargar")
    public String RolEntidadesCargar(String jsonRol, String jsonLogin) {
        String result = "";
        try {
            RolesLN rolesLN = new RolesLN();
            result = rolesLN.RolEntidadesCargar(jsonRol, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolUsuariosNivelInferiorCargar")
    public String RolUsuariosNivelInferiorCargar(String codEntidad, Integer idEntidad, String jsonLogin, String nivel, Integer idRol, String cedula) {
        String result = "";
        try {
            RolesLN rolesLN = new RolesLN();
            result = rolesLN.RolUsuariosNivelInferiorCargar(codEntidad, idEntidad, jsonLogin, nivel, idRol, cedula);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolUsuariosGuardar")
    public String RolUsuariosGuardar(String json) {
        String result = "";
        try {
            RolesLN rolesLN = new RolesLN();
            result = rolesLN.RolUsuariosGuardar(json);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "RolUsuariosCargar")
    public String RolUsuariosCargar(String codEntidad, Integer idEntidad, Integer idRol) {
        String result = "";
        try {
            RolesLN rolesLN = new RolesLN();
            result = rolesLN.RolUsuariosCargar(codEntidad, idEntidad, idRol);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    //////////////////////////////////////////////////USUARIOS////////////////////////////////////////////////////////////////
    @WebMethod(operationName = "UsuariosCargar")
    public String UsuariosCargar(String jsonLogin) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.getUsuarios(jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UsuarioVerificar")
    public String UsuarioVerificar(String jsonUsuario) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.verificarUsuario(jsonUsuario);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UsuariosGuardar")
    public String UsuariosGuardar(String jsonUsuario, String jsonLogin) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.UsuariosGuardar(jsonUsuario, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UsuariosEditar")
    public String UsuariosEditar(String jsonUsuario) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.UsuariosEditar(jsonUsuario);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UsuarioEliminar")
    public String UsuarioEliminar(String jsonUsuario, String jsonLogin) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.UsuarioEliminar(jsonUsuario, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UsuarioNuevoAgregar")
    public String UsuarioNuevoAgregar(String jsonUsuario, String jsonLogin) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.UsuarioNuevoAgregar(jsonUsuario, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UsuarioEstadoActualizar")
    public String UsuarioEstadoActualizar(String jsonUsuario, String jsonLogin) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.UsuarioEstadoActualizar(jsonUsuario, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UsuarioRolesCargar")
    public String UsuarioRolesCargar(String jsonUsuario, String jsonLogin) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.UsuarioRolesCargar(jsonUsuario, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UsuarioRolesGuardar")
    public String UsuarioRolesGuardar(String jsonUsuario) {
        String result = "";
        try {
            UsuariosLN usuariosln = new UsuariosLN();
            result = usuariosln.UsuarioRolesGuardar(jsonUsuario);
        } catch (Exception e) {
            result = "noingresado";
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    /////////////////////////////////////////////ASIGNACION DE ROLES /////////////////////////////////////////////////
    @WebMethod(operationName = "RolOpcionesCargar")
    public String RolOpcionesCargar(String jsonRol) {
        String result = "";
        try {
            RolesLN rolln = new RolesLN();
            result = rolln.RolOpcionesCargar(jsonRol);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    /////////////////////////////////////////// INICIO USUARIO //////////////////////////////////////////////////////////
    @WebMethod(operationName = "AutenticarUsuario")
    public String AutenticarUsuario(@WebParam(name = "cedula") String user) throws ExecutionException, UnsupportedEncodingException {
        String result = "";
        LoginLN loginLN = new LoginLN();
        try {
            result = loginLN.AutenticarUsuario(user);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "unidadesAcademicasUsuarioCargar")
    public String unidadesAcademicasUsuarioCargar(@WebParam(name = "jsonlogin") String jsonLogin) {
        EntidadLN entidadLN = new EntidadLN();
        String result = "";
        try {
            result = entidadLN.loadUnidadesAcademicasUsuario(jsonLogin);
        } catch (Exception e) {
            result = "servicio";
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "AsignaturasCarreraUsuario")
    public String AsignaturasCarreraUsuario(@WebParam(name = "codCarrera") String jsonCarrera, @WebParam(name = "jsonlogin") String jsonLogin) {
        EntidadLN entidadLN = new EntidadLN();
        String result = "";
        try {
            result = entidadLN.AsignaturasCarreraUsuario(jsonCarrera, jsonLogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ReporteUnidadesAcademicasUsuario")//reporte unidades academicas usuario
    public String ReporteUnidadesAcademicasUsuario(@WebParam(name = "jsonEntidad") String jsonEntidad, @WebParam(name = "jsonlogin") String jsonlogin) {
        ReportesLN reportesLN = new ReportesLN();
        String result = "";
        try {
            result = reportesLN.ReporteUnidadesAcademicasUsuario(jsonEntidad, jsonlogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ReporteCampoFormacionDocente")//reporte campo formacion docente
    public String ReporteCampoFormacionDocente(@WebParam(name = "jsonEntidad") String jsonEntidad, @WebParam(name = "jsonlogin") String jsonlogin) {
        ReportesLN reportesLN = new ReportesLN();
        String result = "";
        try {
            result = reportesLN.ReporteCampoFormacionDocente(jsonEntidad, jsonlogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ReporteCriteriosEntidadUsuario")//reporte criterios de silabo
    public String ReporteCriteriosEntidadUsuario(@WebParam(name = "jsonReporte") String jsonReporte, @WebParam(name = "jsonlogin") String jsonlogin) {
        ReportesLN reportesLN = new ReportesLN();
        String result = "";
        try {
            result = reportesLN.ReporteCriteriosEntidadUsuario(jsonReporte, jsonlogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ReporteEstadoSilabos")//reporte criterios de silabo
    public String ReporteEstadoSilabos(@WebParam(name = "jsonlogin") String jsonlogin) {
        ReportesLN reportesLN = new ReportesLN();
        String result = "";
        try {
            result = reportesLN.ReporteEstadoSilabos(jsonlogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ReporteOpcionesUsuario")//reporte criterios de silabo
    public String ReporteOpcionesUsuario(@WebParam(name = "jsonlogin") String jsonlogin) {
        ReportesLN reportesLN = new ReportesLN();
        String result = "";
        try {
            result = reportesLN.ReporteOpcionesUsuario(jsonlogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    //Entidades de Usuarios 
    @WebMethod(operationName = "EntidadTrabajoRol")//reporte criterios de silabo
    public String EntidadTrabajoRol(@WebParam(name = "jsonlogin") String jsonlogin) {
        RolesLN rolesLN = new RolesLN();
        String result = "";
        try {
            result = rolesLN.getEntidadTrabajoRol(jsonlogin);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.sw", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "LogoEntidad")
    public String getLogoEntidad(String codEntidad, String tipoEntidad) {
        String result = null;
        try {
            ArchivoLN unidades = new ArchivoLN();
            result = unidades.getLogoEntidad(codEntidad, tipoEntidad);
        } catch (Exception e) {

        }
        return result;
    }

    @WebMethod(operationName = "NumUnidadesCargar")
    public Integer getNumUnidades(String jsonAsignaturaInfo) {
        Integer resp = 0;
        try {
            EstructuraDesarrolloLN estructuradesarrolloLN = new EstructuraDesarrolloLN();
            resp = estructuradesarrolloLN.getNumeroUnidades(jsonAsignaturaInfo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "NumUnidadesSilaboCargar")
    public Integer NumUnidadesSilaboCargar(String jsonAsignaturaInfo) {
        Integer resp = 0;
        try {
            EstructuraDesarrolloLN estructuradesarrolloLN = new EstructuraDesarrolloLN();
            resp = estructuradesarrolloLN.getNumeroUnidadesSilabo(jsonAsignaturaInfo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "UnidadesAcademicasInstitucion")
    public String UnidadesAcademicasInstitucion() {
        String result = "";
        try {
            EntidadLN entidadLN = new EntidadLN();
            result = entidadLN.UnidadesAcademicasInstitucion();
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "UnidadesAcademicasAsignaturas")
    public String UnidadesAcademicasAsignaturas(String jsonCarrera) {
        String result = "";
        try {
            EntidadLN entidadLN = new EntidadLN();
            result = entidadLN.UnidadesAcademicasAsignaturas(jsonCarrera);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ParametrosCargar")
    public String ParametrosCargar(String jsonSilabo) {
        String result = "";
        try {
            ParametrosLN parametrosLN = new ParametrosLN();
            result = parametrosLN.ParametrosCargar(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ParametrosGuardar")
    public String ParametrosGuardar(String jsonParametros, String jsonSilabo) {
        String result = "";
        try {
            ParametrosLN parametrosLN = new ParametrosLN();
            result = parametrosLN.ParametrosGuardar(jsonParametros, jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    //archivos pdf 
    @WebMethod(operationName = "subirArchivos")
    public Integer subirArchivos(@WebParam(name = "objApJSON") String objApJSON, @WebParam(name = "jsonSilabo") String jsonSilabo) {
        Integer result = 0;
        try {
            ArchivoPublicacionLN apLN = new ArchivoPublicacionLN();
            result = apLN.subirArchivos(objApJSON, jsonSilabo);
        } catch (Exception e) {
        }
        return result;
    }

    @WebMethod(operationName = "SilaboGenerarPDF")
    public String SilaboGenerarPDF(Integer idSilabo, String jsonSilabo) {
        String result = "";
        try {
            SilaboLN silaboLN = new SilaboLN();
            result = silaboLN.SilaboGenerarPDF(idSilabo, jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "EstructuraCurricularSilaboID")
    public Integer EstructuraCurricularSilaboID(String jsonSilabo) {
        Integer result = 0;
        try {
            SilaboLN silaboLN = new SilaboLN();
            result = silaboLN.EstructuraCurricularSilaboID(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "ParametroSilaboCargar")
    public String ParametroSilaboCargar(Integer idParametro) {
        String result = "";
        try {
            ParametrosLN parametrosLN = new ParametrosLN();
            result = parametrosLN.ParametroSilaboCargar(idParametro);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "AutoridadesSilaboCargar")
    public String AutoridadesSilaboCargar(String jsonSilabo) {
        String result = "";
        try {
            SilaboLN silaboLN = new SilaboLN();
            result = silaboLN.AutoridadesSilaboCargar(jsonSilabo);
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "conexionBase")
    public String conexionBase() {
        String result = "";
        try {
            NewMain silaboLN = new NewMain();
            result = silaboLN.conexionBase();
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "conexionRedis")
    public String conexionRedis() {
        String result = "";
        try {
            NewMain silaboLN = new NewMain();
            result = silaboLN.conexionRedis();
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }

    @WebMethod(operationName = "servicio")
    public String servicio() {
        String result = "";
        try {
            NewMain silaboLN = new NewMain();
            result = silaboLN.servicio();
        } catch (Exception e) {
            Logger.getLogger("silaboWSAcdatos").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "***" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "*" + e.getMessage());
        }
        return result;
    }
}
