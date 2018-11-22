/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.entidad.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.ad.SilaboAD;
import dda.silabo.areadocentes.ad.DocentesInformacionAD;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.clases.unidos.EscuelaUnidos;
import dda.silabo.clases.unidos.FacultadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.login.ln.PeriodoLN;
import dda.silabo.reportes.ad.EntidadAD;
import dda.silabo.silabo.comunes.Silabo;
import dda.silabo.entidad.ad.CarrerasAD;
import ec.edu.espoch.academico.ArrayOfMateria;
import ec.edu.espoch.academico.ArrayOfMateriaPensum;
import ec.edu.espoch.academico.ArrayOfRolCarrera;
import ec.edu.espoch.academico.Materia;
import ec.edu.espoch.academico.Periodo;
import ec.edu.espoch.academico.RolCarrera;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import unidos.sw.CarreraAD;

/**
 *
 * @author Jorge Zaruma
 */
public class EntidadLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();
    // Jedis jedis = new Jedis(dda.silabo.db.Global.ipRedis, dda.silabo.db.Global.portRedis);

    public Integer EntidadGuardarInicio(CarreraUnidos carreraComun, EscuelaUnidos escuelaComun, FacultadUnidos facultadComun, AccesoDatos ad) {
        Integer idEntidadResult = 0;
        try {

            Integer idEntidad = ingresarEntidad(ad);
            Integer idFacultad = ingresarIdFacultad(facultadComun.getCodFacultad(), facultadComun.getNombre(), idEntidad, ad);
            Integer idEscuela = ingresarIdEscuela(idFacultad, escuelaComun.getCodEscuela(), escuelaComun.getNombre(), ad);
            idEntidadResult = ingresarIdCarrera(idEscuela, carreraComun.getCodCarrera(), carreraComun.getNombre(), ad);
            ingresarIdCamposFormacion(carreraComun, idEntidadResult, ad);

        } catch (Exception e) {

        }
        return idEntidadResult;

    }

    public Integer ingresarEntidad(AccesoDatos ad) {
        Integer idResult = 0;
        try {
            try {
                EntidadAD entidadAD = new EntidadAD();
                idResult = entidadAD.getIdEntidad("ESPOCH", ad, 1);
                if (idResult == 0) {
                    idResult = entidadAD.ingresarEntidad("ESPOCH", 0, 1, ad, "ESCUELA SUPERIOR POLITÉCNICA DE CHIMBORAZO");
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return idResult;
    }

    public Integer ingresarIdFacultad(String codfacultad, String nombre, Integer idPadre, AccesoDatos ad) {
        Integer idResult = 0;
        try {

            EntidadAD entidadAD = new EntidadAD();
            idResult = entidadAD.getIdEntidad(codfacultad, ad, 2);
            if (idResult == 0) {
                idResult = entidadAD.ingresarEntidad(codfacultad, idPadre, 2, ad, nombre);
            }
        } catch (Exception e) {
        }
        return idResult;
    }

    public Integer ingresarIdEscuela(Integer idPadre, String codEscuela, String nombre, AccesoDatos ad) {
        Integer idResult = 0;
        try {

            EntidadAD entidadAD = new EntidadAD();
            idResult = entidadAD.getIdEntidad(codEscuela, ad, 3);
            if (idResult == 0) {
                idResult = entidadAD.ingresarEntidad(codEscuela, idPadre, 3, ad, nombre);
            }

        } catch (Exception e) {
        }
        return idResult;
    }

    public Integer ingresarIdCarrera(Integer idPadre, String codCarrera, String nombre, AccesoDatos ad) {
        Integer idResult = 0;
        try {

            EntidadAD entidadAD = new EntidadAD();
            idResult = entidadAD.getIdEntidad(codCarrera, ad, 4);
            if (idResult == 0) {
                idResult = entidadAD.ingresarEntidad(codCarrera, idPadre, 4, ad, nombre);
            }
        } catch (Exception e) {
        }
        return idResult;
    }

    private void ingresarIdCamposFormacion(CarreraUnidos carreraComun, Integer idPadre, AccesoDatos ad) {
        try {

            EntidadAD entidadAD = new EntidadAD();
            entidadAD.agregarCamposFormacionCarrera(carreraComun.getCodCarrera(), ad, idPadre);

        } catch (Exception e) {
        }
    }

    public void ingresarUsuarioInformacionEntidad(Integer idRol, Integer idPeriodo, Integer idEntidad, Integer idUsuario, AccesoDatos ad) {
        try {

            EntidadAD entidadAD = new EntidadAD();
            if (entidadAD.verificarUsuarioEntidad(idRol, idEntidad, idPeriodo, idUsuario, ad) == 0) {
                entidadAD.ingresarUsuarioInformacionEntidad(idRol, idPeriodo, idEntidad, idUsuario, ad);
            }
        } catch (Exception e) {
        }
    }

    public String loadUnidadesAcademicasUsuario(String jsonLogin) throws SQLException {//unidades academicas de usuario
        String result = null;
        try {
            PeriodoLN periodoLN = new PeriodoLN();
            Periodo periodo = periodoLN.getPeriodoValido();
            LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
            if (loginAD != null && ad.Connectar() != 0) {
                // result = jedis.get("Asignaturas_" + loginAD.getRolActivo() + loginAD.getCedula() + periodo.getCodigo());
                if (result == null) {
                    EntidadAD entidadAD = new EntidadAD();
                    List<EntidadUnidos> entidadesTrabajo = ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());
                    EntidadUnidos entidadComun = entidadAD.agregarEntidadesUsuarioRol(entidadesTrabajo);
                    result = G.toJson(entidadComun);
                    //  jedis.set("Asignaturas_" + loginAD.getRolActivo() + loginAD.getCedula() + periodo.getCodigo(), result);
                }
            }

        } catch (JsonSyntaxException e) {
            return null;
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public List<EntidadUnidos> ObtenerEntidadesUsuario(AccesoDatos ad, String cedula, String rolActivo, String codPeriodo) throws SQLException {//unidades academicas de usuario
        List<EntidadUnidos> result = new ArrayList<>();
        try {

            EntidadAD entidadAD = new EntidadAD();
            String SQL = entidadAD.getEntidadesSQL(cedula, rolActivo, codPeriodo);
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                entidadAD = new EntidadAD();
                entidadAD.agregarEntidad(rsEntidad, rolActivo, ad, cedula);
                result.add(entidadAD);

            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return result;
    }

    public String loadEntidadesUsuario(String jsonSilabo) throws ExecutionException {
        String result = null;
        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
        // result = jedis.get("UA" + silabo.getCedula());
        if (result == null) {
            EntidadUnidos uAcademicas = new EntidadUnidos();
            DocentesInformacionAD objCarrerasDocente = getAsignacionRolEntidad(silabo.getCedula());
            List<CarreraUnidos> carreras = objCarrerasDocente.getCarreras();
            if (!carreras.isEmpty()) {
                List<FacultadUnidos> objFacultad = new ArrayList();
                String jsonEntidad = unidadesAcademicasInstitucion(null);
                EntidadUnidos entidadesComun = G.fromJson(jsonEntidad, EntidadUnidos.class);
                entidadesComun.getFacultades().forEach((facultad) -> {
                    List<EscuelaUnidos> objEscuela = new ArrayList<>();
                    objEscuela = ExisteEscuela(carreras, facultad.getEscuelas());
                    if (objEscuela.size() > 0) {
                        FacultadUnidos facultadDocente = new FacultadUnidos();
                        facultadDocente.setCodFacultad(facultad.getCodFacultad());
                        facultadDocente.setNombre(facultad.getNombre());
                        facultadDocente.setEscuelas(objEscuela);
                        objFacultad.add(facultadDocente);
                    }
                });
                if (objFacultad.size() > 0) {
                    uAcademicas.setFacultades(objFacultad);
                }
                result = G.toJson(uAcademicas);
                //jedis.set("UA" + silabo.getCedula(), result);
            }
        }
        return result;
    }

    public DocentesInformacionAD getAsignacionRolEntidad(String login) {
        DocentesInformacionAD result = new DocentesInformacionAD();
        PeriodoLN periodoLN = new PeriodoLN();
        Periodo objPeriodo = periodoLN.getPeriodoValido();
        List<RolCarrera> objCarreras = getRolUsuarioCarrera(login).getRolCarrera();
        if (objCarreras.size() > 0) {
            for (RolCarrera carrera : objCarreras) {
                if (carrera.getNombreRol().equals("DOC")) {
                    List<Materia> objMaterias = getMateriasDocente(carrera.getCodigoCarrera(), login, objPeriodo.getCodigo()).getMateria();
                    if (objMaterias.size() > 0) {
                        result.agregarRolCarrera(carrera.getCodigoCarrera(), carrera.getNombreRol());
                    }
                } else {
                    if (carrera.getNombreRol().equals("DIRCAR")) {
                        ArrayOfMateriaPensum materias = pensumVigente(carrera.getCodigoCarrera());
                        if (materias != null) {
                            result.agregarRolCarrera(carrera.getCodigoCarrera(), carrera.getNombreRol());
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<EscuelaUnidos> ExisteEscuela(List<CarreraUnidos> carreras, List<EscuelaUnidos> escuelas) {
        List<CarreraUnidos> objCarrera = new ArrayList<>();
        List<EscuelaUnidos> result = new ArrayList<>();
        EscuelaUnidos objEscuela = null;
        for (EscuelaUnidos escuela : escuelas) {
            objCarrera = ExisteCarrera(carreras, escuela.getCarreras());
            if (objCarrera.size() > 0) {
                objEscuela = new EscuelaUnidos();
                objEscuela.setCodEscuela(escuela.getCodEscuela());
                objEscuela.setNombre(escuela.getNombre());
                objEscuela.setCarreras(objCarrera);
                result.add(objEscuela);
            }
        }
        return result;
    }

    private List<CarreraUnidos> ExisteCarrera(List<CarreraUnidos> carreras1, List<CarreraUnidos> carreras2) {
        List<CarreraUnidos> result = new ArrayList();
        carreras1.forEach((carrera1) -> {
            carreras2.stream().filter((carrera2) -> (carrera1.getCodCarrera().equals(carrera2.getCodCarrera()))).filter((carrera2) -> (carrera1.getRoles().getDescRol() != null)).map((carrera2) -> {
                carrera1.setNombre(carrera2.getNombre());
                return carrera2;
            }).forEachOrdered((_item) -> {
                result.add(carrera1);
            });
        });
        return result;
    }

    public String AsignaturasCarreraUsuario(String jsonCarrera, String jsonLogin) throws SQLException {
        String result = null;
        try {
            if (ad.Connectar() != 0) {

                CarreraUnidos carreraComun = G.fromJson(jsonCarrera, CarreraUnidos.class);
                LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                CarrerasAD carrerasAD = new CarrerasAD();
                SilaboAD silaboAD = new SilaboAD();
                silaboAD.setCodCarrera(carreraComun.getCodCarrera());
                silaboAD.setPeriodo(periodo.getCodigo());
                carrerasAD.agregarAsignaturasEntidad("indefinido", loginAD, ad, silaboAD);
                result = G.toJson(carrerasAD);

            }
        } catch (JsonSyntaxException e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String UnidadesAcademicasInstitucion() {
        return unidadesAcademicasInstitucion(null);
    }

    public String UnidadesAcademicasAsignaturas(String jsonCarrera) {
        String result = "";
        try {
            CarreraUnidos carreraComun = G.fromJson(jsonCarrera, CarreraUnidos.class);
            String jsonCarreraCampos = "{'codCarrera':'" + carreraComun.getCodCarrera() + "'}";
            unidos.sw.CarreraAD camposFormacionCarrera = asignaturasCamposFormacion(jsonCarreraCampos);
            result = G.toJson(camposFormacionCarrera);
            if (result != null) {
                CarrerasAD carreraComun2 = G.fromJson(result, CarrerasAD.class);
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                if (!carreraComun2.getCamposFormacion().isEmpty()) {
                    carreraComun2.agregarFuncionSilabo(carreraComun.getCodCarrera(), periodo.getCodigo());
                    carreraComun.setCamposFormacion(carreraComun2.getCamposFormacion());
                }
                result = G.toJson(carreraComun);
            }
        } catch (JsonSyntaxException e) {
        }
        return result;
    }

    private static ArrayOfMateria getMateriasDocente(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getMateriasDocente(arg0, arg1, arg2);
    }

    private static ArrayOfRolCarrera getRolUsuarioCarrera(java.lang.String arg0) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getRolUsuarioCarrera(arg0);
    }

    private static ArrayOfMateriaPensum pensumVigente(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.pensumVigente(jsonCarrera);
    }

    private static String unidadesAcademicasInstitucion(java.lang.String jsonDatos) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.unidadesAcademicasInstitucion(jsonDatos);
    }

    private static CarreraAD asignaturasCamposFormacion(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCamposFormacion(jsonCarrera);
    }

}
