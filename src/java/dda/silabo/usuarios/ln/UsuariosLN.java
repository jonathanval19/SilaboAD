/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.usuarios.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.ad.SilaboAD;
import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ln.EntidadLN;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.login.ln.PeriodoLN;
import dda.silabo.reportes.ad.EntidadAD;
import dda.silabo.roles.ad.RolAD;
import dda.silabo.usuarios.ad.UsuarioAD;
import dda.silabo.usuarios.ad.UsuariosAD;
import ec.edu.espoch.academico.Periodo;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Evelyn
 */
public class UsuariosLN {

    Gson G = new Gson();
    AccesoDatos ad = new AccesoDatos();

    public String getUsuarios(String jsonLogin) throws SQLException {
        LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
        UsuariosAD usuario = new UsuariosAD();
        try {
            if (ad.Connectar() != 0) {

                EntidadLN entidadLN = new EntidadLN();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                List<EntidadUnidos> entidadesTrabajo = entidadLN.ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());
                usuario.agregarUsuariosAdministrador(entidadesTrabajo, ad, periodo.getCodigo(), loginAD.getCedula());
            }
        } catch (JsonSyntaxException e) {
            ad.getCon().rollback();
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return (G.toJson(usuario));
    }

    public String verificarUsuario(String jsonUsuario) throws SQLException {
        String result = "{}";
        try {
            if (ad.Connectar() != 0) {

                UsuarioAD usuario = G.fromJson(jsonUsuario, UsuarioAD.class);
                usuario.agregarUsuarioLocal(ad);
                if (usuario.getApellido() == null) {
                    usuario.verificarusuario(ad);
                }
                result = G.toJson(usuario);

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public Integer UsuarioGuardarInicio(String cedula, String estado, AccesoDatos ad) throws SQLException {
        Integer idCedula = 0;
        try {

            UsuarioAD usuariosad = new UsuarioAD();
            SilaboAD silaboAD = new SilaboAD();
            idCedula = silaboAD.idDocenteSelect(ad, cedula);
            if (idCedula == 0) {
                idCedula = usuariosad.guardarUsuarioIncio(ad, cedula, estado);
            }

        } catch (JsonSyntaxException e) {
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return idCedula;
    }

    public String UsuariosGuardar(String jsonUsuarios, String jsonLogin) throws SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                UsuarioAD usuariosad = G.fromJson(jsonUsuarios, UsuarioAD.class);
                usuariosad.guardarUsuarios(ad);
                result = getUsuarios(jsonLogin);

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String UsuariosEditar(String jsonUsuario) throws SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                UsuarioAD usuariosad = G.fromJson(jsonUsuario, UsuarioAD.class);
                usuariosad.getUsuario(ad);
                result = G.toJson(usuariosad);
            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public String UsuarioRolesCargar(String jsonUsuario, String jsonLogin) throws SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {
                UsuarioAD usuariosad = G.fromJson(jsonUsuario, UsuarioAD.class);
                LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
                EntidadLN entidadLN = new EntidadLN();
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                List<EntidadUnidos> entidadesTrabajo = entidadLN.ObtenerEntidadesUsuario(ad, loginAD.getCedula(), loginAD.getRolActivo(), periodo.getCodigo());;
                usuariosad.UsuarioRolesCargar(ad, periodo.getCodigo(), entidadesTrabajo, loginAD.getRolActivo());
                result = G.toJson(usuariosad);

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String UsuarioRolesGuardar(String jsonUsuario) throws SQLException {
        String result = "noingresado";
        try {
            if (ad.Connectar() != 0) {
                UsuarioAD usuariosad = G.fromJson(jsonUsuario, UsuarioAD.class);

                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                usuariosad.guardarRolesUsuario(ad, periodo.getCodigo());
                result = "ingresado";

            }
        } catch (JsonSyntaxException e) {
            Logger.getLogger("UsuariosLN").log(java.util.logging.Level.SEVERE, "dda.silabo.usuarios.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String UsuarioNuevoAgregar(String jsonUsuario, String jsonLogin) {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                LoginAD loginAD = G.fromJson(jsonLogin, LoginAD.class);
                UsuarioAD usuarioAD = G.fromJson(jsonUsuario, UsuarioAD.class);
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                usuarioAD.guardarUsuarioLocal(ad, loginAD, periodo.getCodigo());
                result = getUsuarios(jsonLogin);

            }
        } catch (Exception e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String UsuarioEstadoActualizar(String jsonUsuario, String jsonLogin) {
        String result = "{}";
        try {
            if (ad.Connectar() != 0) {

                UsuarioAD usuarioAD = G.fromJson(jsonUsuario, UsuarioAD.class);
                usuarioAD.actualizarEstado(ad);
                result = getUsuarios(jsonLogin);

            }
        } catch (Exception e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

    public String UsuarioEliminar(String jsonUsuario, String jsonLogin) throws SQLException {
        String result = "";
        try {
            if (ad.Connectar() != 0) {

                UsuarioAD usuarioAD = G.fromJson(jsonUsuario, UsuarioAD.class);
                PeriodoLN periodoLN = new PeriodoLN();
                Periodo periodo = periodoLN.getPeriodoValido();
                usuarioAD.UsuarioEliminar(ad, periodo.getCodigo());
                result = getUsuarios(jsonLogin);

            }
        } catch (JsonSyntaxException e) {

        } finally {
            ad.Desconectar();
        }
        return result;
    }

}
