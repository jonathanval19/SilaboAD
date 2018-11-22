/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.roles.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.CarrerasAD;
import dda.silabo.entidad.ln.EntidadLN;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.login.ln.PeriodoLN;
import dda.silabo.reportes.ad.EntidadAD;
import dda.silabo.roles.ad.RolAD;
import dda.silabo.roles.ad.RolesAD;
import dda.silabo.roles.comunes.AsignacionRol;
import ec.edu.espoch.academico.Periodo;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adry
 */
public class RolesLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String getRoles(String jsonLogin) {
        RolesAD rolad = new RolesAD();
        if (ad.Connectar() != 0) {
            try {
                LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
                EntidadLN entidadLN = new EntidadLN();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                List<EntidadUnidos> entidadesTrabajo = entidadLN.ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());
                rolad.getRolesUsuarios(ad, entidadesTrabajo, loginAD.getRolActivo());
            } catch (Exception e) {
                Logger.getLogger("RolesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.roles.ln", e.getClass().getName() + "*****" + e.getMessage());
                System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            }
        }
        return G.toJson(rolad);
    }

    public String GuardarRoles(String jsonRoles) throws SQLException {
        if (ad.Connectar() != 0) {
            try {
                RolesAD rolesad = G.fromJson(jsonRoles, RolesAD.class);
                rolesad.GuardarRoles(ad);
            } catch (JsonSyntaxException e) {
                Logger.getLogger("RolesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.roles.ln", e.getClass().getName() + "*****" + e.getMessage());
                System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            }
        }
        return getRoles("");
    }

    public String deleteRol(String jsonRol, String jsonLogin) throws SQLException {
        try {
            if (ad.Connectar() != 0) {
                RolAD rolad = G.fromJson(jsonRol, RolAD.class);
                int numRoles = rolad.getNumeroRolesAsignado(ad);
                rolad.deleteOpcion(ad, numRoles);
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RolesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.roles.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return getRoles(jsonLogin);
    }

    public String HabilitarRol(String jsonRol, String jsonLogin) throws SQLException {
        try {
            if (ad.Connectar() != 0) {

                RolAD rolad = G.fromJson(jsonRol, RolAD.class);
                rolad.HabilitarRol(ad);
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RolesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.roles.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return getRoles(jsonLogin);
    }

    public String RolesAsignacionGuardar(String jsonRol) throws SQLException {
        try {
            if (ad.Connectar() != 0) {
                RolAD rolad = G.fromJson(jsonRol, RolAD.class);
                rolad.guardarAsignacion(ad);
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RolesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.roles.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return RolOpcionesCargar(jsonRol);
    }

    public String RolOpcionesCargar(String jsonRol) throws SQLException {
        RolAD rolad = G.fromJson(jsonRol, RolAD.class);
        try {
            if (ad.Connectar() != 0) {
                rolad.RolOpcionesCargar(ad);
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RolesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.roles.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return G.toJson(rolad);
    }

    public Integer GuardarRolInicio(String nombreRol, String estado, AccesoDatos ad) throws SQLException {
        RolAD rolad = new RolAD();
        Integer idRol = 0;
        try {

            String SQL = rolad.getSQL(nombreRol);
            idRol = rolad.existeRol(nombreRol, ad, SQL);
            if (idRol == 0) {
                idRol = rolad.guardarRolInicio(ad, nombreRol, estado);
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("RolesLN").log(java.util.logging.Level.SEVERE, "dda.silabo.roles.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return idRol;
    }

    public String getEntidadTrabajoRol(String jsonlogin) throws SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                LoginAD loginAD = G.fromJson(jsonlogin, LoginAD.class);
                RolAD rolAD = new RolAD();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                EntidadAD entidadAD = new EntidadAD();
                String SQL = rolAD.getSqlEntidad(loginAD, periodo.getCodigo());
                entidadAD.getEntidadTrabajo(SQL, ad);
                result = G.toJson(entidadAD);
            }
        } catch (JsonSyntaxException e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String RolEntidadesCargar(String jsonRol, String jsonLogin) throws SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                RolAD rolAD = G.fromJson(jsonRol, RolAD.class);
                LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
                EntidadLN entidadLN = new EntidadLN();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                List<EntidadUnidos> entidadesTrabajo = entidadLN.ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());
                EntidadAD entidadAD = new EntidadAD();
                entidadAD.agregarEntidadesRol(entidadesTrabajo, rolAD.getIdTipoEntidad(), ad);
                //rolAD.agregarUsuariosRol(entidadesTrabajo, ad, periodo.getCodigo(), loginAD.getCedula());
                result = G.toJson(entidadAD);

            }
        } catch (JsonSyntaxException e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String RolUsuariosGuardar(String jsonRol) throws UnsupportedEncodingException, UnsupportedEncodingException, ExecutionException, SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                AsignacionRol asignacionRol = G.fromJson(jsonRol, AsignacionRol.class);
                RolAD rolAD = new RolAD();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                rolAD.RolUsuariosGuardar(ad, periodo.getCodigo(), asignacionRol);
                result = "ingresado";

            }
        } catch (JsonSyntaxException e) {
            result = "no";
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String RolOpcionesGuardar(String jsonORolOpciones, String jsonLogueo) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                RolAD rolAD = G.fromJson(jsonORolOpciones, RolAD.class);
                rolAD.RolOpcionesGuardar(ad);
                result = "ingresado";
            }
        } catch (JsonSyntaxException e) {
            result = "no";
        } catch (SQLException ex) {
            Logger.getLogger(RolesLN.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String RolUsuariosCargar(String codEntidad, Integer idEntidad, Integer idRol) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                CarrerasAD carreraAD = new CarrerasAD();
                carreraAD.RolUsuariosCargar(ad, codEntidad, idEntidad, idRol, periodo.getCodigo());
                result = G.toJson(carreraAD);

            }
        } catch (JsonSyntaxException e) {
            result = "no";
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String RolUsuariosNivelInferiorCargar(String codEntidad, Integer idEntidad, String jsonLogin, String nivel, Integer idRol, String cedula) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
                EntidadLN entidadLN = new EntidadLN();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                List<EntidadUnidos> entidadesTrabajo = entidadLN.ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());
                EntidadAD entidadAD = new EntidadAD();
                entidadAD.agregarEntidadeCarreraRol(entidadesTrabajo, codEntidad, idEntidad, nivel, ad, idRol, cedula);
                //rolAD.agregarUsuariosRol(entidadesTrabajo, ad, periodo.getCodigo(), loginAD.getCedula());
                result = G.toJson(entidadAD);

            }
        } catch (JsonSyntaxException e) {

        } catch (SQLException ex) {
            Logger.getLogger(RolesLN.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ad.Desconectar();
        }
        return result;
    }

}
