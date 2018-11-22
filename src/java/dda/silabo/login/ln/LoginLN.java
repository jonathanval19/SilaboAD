/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.login.ln;

import com.google.gson.Gson;
import dda.silabo.ad.SilaboAD;
import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.ln.SilaboLN;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.entidad.ln.EntidadLN;
import dda.silabo.roles.ln.RolesLN;
import dda.silabo.silabo.comunes.Silabo;
import dda.silabo.usuarios.ln.UsuariosLN;
import ec.edu.espoch.academico.ArrayOfRolCarrera;
import ec.edu.espoch.academico.Periodo;
import ec.edu.espoch.academico.Persona;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class LoginLN {

    AccesoDatos ad = new AccesoDatos();
    Gson G = new Gson();

    public String AutenticarUsuario(String user) throws SQLException, UnsupportedEncodingException, ExecutionException {
        String result = null;
        PeriodoLN periodoLN = new PeriodoLN();
        Periodo periodo = periodoLN.getPeriodoValido();
        LoginAD loginAD = new LoginAD();
        SilaboAD silaboAD = new SilaboAD();
        silaboAD.setCedula(user);
        silaboAD.setPeriodo(periodo.getCodigo());
        Integer idUsuarioLocal = 0;
        String SQL = "";
        try {
            if (ad.Connectar() != 0) {
                Integer idUsuarioEntidad = silaboAD.getUsuarioEntidad(ad, user, periodo.getCodigo());
                if (idUsuarioEntidad != 0) {//Si existe el usuario en nuestra BD devolver sus roles
                    idUsuarioLocal = silaboAD.idDocenteSelect(ad, user);
                    SQL = loginAD.getSQLDatosUsuario(idUsuarioLocal);
                    loginAD.getDatos(SQL, ad, user);
                    loginAD.agregarRoles(user, periodo.getCodigo(), ad);
                    if (loginAD.getRoles().isEmpty()) {
                        ingresarInformacionUsuario(user, periodo.getCodigo(), ad);
                        loginAD.agregarRoles(user, periodo.getCodigo(), ad);
                    }
                    result = G.toJson(loginAD);
                } else {
                    ArrayOfRolCarrera rolCarrera = getRolUsuarioCarrera(user);//autenticarUsuarioCarrera(user, pass);
                    result = loginAD.getrolEstudiante(rolCarrera.getRolCarrera(), user, periodo.getCodigo());
                    if (!result.equals("Estudiante")) {
                        Persona persona = getDatosUsuarioCarrera(rolCarrera.getRolCarrera().get(0).getCodigoCarrera(), user);
                        idUsuarioLocal = loginAD.agregarInformacionUsuario(persona, ad, "OASIS");
                        SQL = loginAD.getSQLDatosUsuario(idUsuarioLocal);
                        loginAD.agregarRoles(user, periodo.getCodigo(), ad);
                        loginAD.getDatos(SQL, ad, user);
                        if (loginAD.getRoles().isEmpty()) {
                            ingresarInformacionUsuario(user, periodo.getCodigo(), ad);
                            loginAD.agregarRoles(user, periodo.getCodigo(), ad);
                        }
                        result = G.toJson(loginAD);
                    } else {
                        result = G.toJson(loginAD);
                    }
                }
            }
        } catch (Exception e) {
            result = "errores";
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    private void ingresarInformacionUsuario(String user, String codPeriodo, AccesoDatos ad) throws ExecutionException {
        Gson G = new Gson();
        RolesLN rol = new RolesLN();
        UsuariosLN usuarioLN = new UsuariosLN();
        SilaboLN silaboLN = new SilaboLN();
        EntidadLN entidadLN = new EntidadLN();
        Silabo silabo = new Silabo();
        silabo.setCedula(user);
        SilaboAD silaboAD = new SilaboAD();
        silabo.setPeriodo(codPeriodo);
        String jsonEntidades = entidadLN.loadEntidadesUsuario(G.toJson(silabo));
        try {
            if (jsonEntidades != null) {
                EntidadUnidos uAcademica = G.fromJson(jsonEntidades, EntidadUnidos.class);
                uAcademica.getFacultades().forEach((facultad) -> {
                    facultad.getEscuelas().forEach((escuela) -> {
                        escuela.getCarreras().forEach((carrera) -> {
                            Integer idRol = null;
                            try {
                                idRol = rol.GuardarRolInicio(carrera.getRoles().getDescRol(), "H", ad);
                            } catch (SQLException ex) {
                                Logger.getLogger(LoginLN.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            Integer idUsuario = null;
                            try {
                                idUsuario = usuarioLN.UsuarioGuardarInicio(user, "H", ad);
                            } catch (SQLException ex) {
                                Logger.getLogger(LoginLN.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            Integer idPeriodo = silaboLN.PeriodoGuardarInicio(codPeriodo, ad);
                            Integer idEntidad = entidadLN.EntidadGuardarInicio(carrera, escuela, facultad, ad);
                            entidadLN.ingresarUsuarioInformacionEntidad(idRol, idPeriodo, idEntidad, idUsuario, ad);
                            silaboAD.IngresarPeriodoCarrera(carrera.getCodCarrera(), codPeriodo, ad);
                        });
                    });
                });
            }
        } catch (Exception e) {
        }
    }

///CORRECCION
    private static ArrayOfRolCarrera getRolUsuarioCarrera(java.lang.String arg0) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getRolUsuarioCarrera(arg0);
    }

    private static Persona getDatosUsuarioCarrera(java.lang.String arg0, java.lang.String arg1) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getDatosUsuarioCarrera(arg0, arg1);
    }

}
