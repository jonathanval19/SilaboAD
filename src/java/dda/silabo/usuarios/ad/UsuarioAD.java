/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.usuarios.ad;

import dda.silabo.ad.SilaboAD;
import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ln.EntidadLN;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.reportes.ad.EntidadAD;
import dda.silabo.roles.ad.RolAD;
import dda.silabo.roles.comunes.Rol;
import dda.silabo.usuarios.comunes.Usuario;
import ec.edu.espoch.academico.ArrayOfRolCarrera;
import ec.edu.espoch.academico.Persona;
import ec.edu.espoch.academico.RolCarrera;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import unidos.sw.DocenteAD;

/**
 * @author Evelyn
 */
public class UsuarioAD extends Usuario {

    public void agregarUsuario(ResultSet rsDocentes) {
        try {
            this.setId(rsDocentes.getInt("id_cedula"));
            this.setCedula(rsDocentes.getString("cedula"));
            this.setTipo(rsDocentes.getString("tipo_usuario"));
            this.setNombre(rsDocentes.getString("nombres"));
            this.setApellido(rsDocentes.getString("apellidos"));
            this.setEmail(rsDocentes.getString("email"));
            this.setTipo(rsDocentes.getString("tipo_usuario"));
            this.setEstado(rsDocentes.getString("estado"));
        } catch (SQLException e) {
            this.setNombre("---------------");
            this.setApellido("---------------");
        }
    }

    public void agregarUsuarioCargar(ResultSet rsDocentes) {
        try {
            this.setId(rsDocentes.getInt("id_usuario"));
            this.setCedula(rsDocentes.getString("cedula"));
            if (rsDocentes.getString("nombres") != null) {
                this.setNombre(rsDocentes.getString("nombres"));
                this.setApellido(rsDocentes.getString("apellidos"));
            } else {
                ArrayOfRolCarrera u = getRolUsuarioCarrera(rsDocentes.getString("cedula"));
                Persona d = getDatosUsuarioCarrera(u.getRolCarrera().get(0).getCodigoCarrera(), rsDocentes.getString("cedula"));
                this.setNombre(d.getNombres());
                this.setApellido(d.getApellidos());
            }
        } catch (Exception e) {
        }
    }

    public void verificarusuario(AccesoDatos ad) {
        try {
            String usrP = this.getCedula().substring(0, 9);
            String usrS = this.getCedula().substring(9, 10);
            String usr = usrP + "-" + usrS;
//            ArrayOfRolCarrera rol = getRolUsuarioCarrera(usr);
//            if (rol != null) {
//                List<RolCarrera> rolcarrera = rol.getRolCarrera();
//                if (rolcarrera.size() > 0) {
//                    Persona persona = getDatosUsuarioCarrera(rolcarrera.get(0).getCodigoCarrera(), this.getCedula());
//                    this.setNombre(persona.getNombres());
//                    this.setApellido(persona.getApellidos());
//                    this.setEmail(persona.getEmail());
//                    this.setId(0);
//                    this.setTipo("Usuario OASIS");
//                    this.setEstado("O");
//                }
//            }
            if (this.getApellido() == null) {
                DocenteAD docenteAD = null;
                try {
                    docenteAD = docenteInformacion(this.getCedula());
                    this.setNombre(docenteAD.getNombres());
                    this.setApellido(docenteAD.getApellidos());
                    this.setEmail(docenteAD.getCorreo());
                    this.setId(0);
                    this.setTipo("Usuario Externo");
                    this.setEstado("E");
                } catch (Exception e) {
                    this.setTipo("Usuario Externo");
                    this.setEstado("F");
                }

            }
        } catch (Exception e) {
            this.setTipo("Usuario Externo");
            this.setEstado("E");
        }
    }

    public void ingresarusuario(AccesoDatos ad) {
        SilaboAD silabo = new SilaboAD();
        if (silabo.idDocenteSelect(ad, this.getCedula()) == 0) {
            silabo.idDocenteInsert(ad, this.getCedula(), "OASIS");
        }
    }

    public void guardarUsuarios(AccesoDatos ad) throws SQLException {
        try {
            Integer idCedula = this.getId();
            if (idCedula != 0) {
                String update = "UPDATE t_usuarios_informacion SET nombres= ?,apellidos=?,email=?  WHERE (id_cedula=?) ";
                PreparedStatement ps = ad.getCon().prepareStatement(update);
                ps.setString(1, this.getNombre());
                ps.setString(2, this.getApellido());
                ps.setString(3, this.getEmail());
                ps.setInt(4, idCedula);
                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void getUsuario(AccesoDatos ad) throws SQLException {
        try {
            String SQL = "select tu.id_cedula,td.cedula,td.tipo_usuario,tu.nombres,tu.apellidos,tu.email,td.estado from \n"
                    + "t_docente as td left join t_usuarios_informacion as tu\n"
                    + "on td.id_docente = tu.id_cedula where tu.id_cedula=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getId());
            ResultSet rsDocentes = ps.executeQuery();
            while (rsDocentes.next()) {
                agregarUsuario(rsDocentes);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void UsuarioRolesCargar(AccesoDatos ad, String codPeriodo, List<EntidadUnidos> entidades, String rolActivo) throws SQLException {
        try {
            String SQL = "";
            SQL = "SELECT tr.* from t_roles as tr\n"
                    + "where  (tr.rol_char='Adm' or tr.rol_char='Sec')and tr.id_tipo_entidad>?\n"
                    + "order by tr.id_tipo_entidad,tr.descripcion";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, Integer.parseInt(entidades.get(0).getId()));
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                RolAD rolAD = new RolAD();
                rolAD.UsuarioRolesCargar(rsRoles, entidades.get(0), this.getId(), ad);
                this.getRoles().add(rolAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void guardarRolesUsuario(AccesoDatos ad, String codPeriodo) throws SQLException {
        try {
            SilaboAD silaboAD = new SilaboAD();
            silaboAD.setPeriodo(codPeriodo);
            Integer idPeriodo = silaboAD.idPeriodoSelect(ad);
            PreparedStatement ps = null;
            for (Rol rol : this.getRoles()) {
                String deleteRolesUsuario = "delete from t_usuario_entidad where id_usuario=? and id_rol=?;";
                ps = ad.getCon().prepareStatement(deleteRolesUsuario);
                ps.setInt(1, this.getId());
                ps.setInt(2, rol.getIdRol());
                ps.executeUpdate();
                ps.close();
                ad.getCon().commit();
                for (EntidadUnidos entidadComun : rol.getEntidades()) {
                    String SQL2 = "insert into t_usuario_entidad (id_usuario,id_rol,id_entidad,id_periodo) values (?,?,?,?);";
                    ps = ad.getCon().prepareStatement(SQL2);
                    ps.setInt(1, this.getId());
                    ps.setInt(2, rol.getIdRol());
                    ps.setInt(3, Integer.parseInt(entidadComun.getId()));
                    ps.setInt(4, idPeriodo);
                    ps.executeUpdate();
                    ps.close();
                    ad.getCon().commit();

                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public Integer guardarUsuarioIncio(AccesoDatos ad, String cedula, String estado) throws SQLException {
        Integer idCedula = 0;
        try {
            String SQL = "insert into t_docente (cedula,tipo_usuario,estado)VALUES (?,?,?) returning id_docente";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, cedula);
            ps.setString(2, "OASIS");
            ps.setString(3, estado);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                idCedula = rsRoles.getInt("id_docente");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return idCedula;
    }

    public void agregarUsuarioLocal(AccesoDatos ad) throws SQLException {
        String SQL = usuarioLocalSQL();
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsDocente = ps.executeQuery();
            while (rsDocente.next()) {
                this.setApellido(rsDocente.getString("apellidos"));
                this.setNombre(rsDocente.getString("nombres"));
                this.setEmail(rsDocente.getString("email"));
                this.setId(rsDocente.getInt("id_cedula"));
                this.setTipo("Usuario Registrado");
                this.setEstado("R");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    private String usuarioLocalSQL() {
        String usrP = this.getCedula().substring(0, 9);
        String usrS = this.getCedula().substring(9, 10);
        String usr = usrP + "-" + usrS;
        return "select tui.id_cedula,tui.nombres,tui.apellidos,tui.email from t_usuarios_informacion AS tui \n"
                + "JOIN t_docente AS td on td.id_docente = tui.id_cedula\n"
                + "where td.cedula='" + usr + "'";
    }

    public void guardarUsuarioLocal(AccesoDatos ad, LoginAD loginAD, String codPeriodo) throws SQLException {
        int idUsuario = this.getId();
        if (idUsuario == 0) {
            Persona persona = new Persona();
            persona.setApellidos(this.getApellido());
            persona.setCedula(this.getCedula());
            persona.setNombres(this.getNombre());
            persona.setEmail(this.getEmail());
            idUsuario = loginAD.agregarInformacionUsuario(persona, ad, this.getTipo());
        }
        RolAD rolAD = new RolAD();
        String SQL = rolAD.getSqlEntidad(loginAD, codPeriodo);
        int idEntidad = 0;
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                idEntidad = rsEntidad.getInt("id_entidad");
            }
            EntidadLN entidadLN = new EntidadLN();
            SilaboAD silaboAD = new SilaboAD();
            silaboAD.setPeriodo(codPeriodo);
            int idPerido = silaboAD.idPeriodoSelect(ad);
            entidadLN.ingresarUsuarioInformacionEntidad(100, idPerido, idEntidad, idUsuario, ad);
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void actualizarEstado(AccesoDatos ad) throws SQLException {
        try {
            String SQL = actualizarEstadoUsuarioSQL();
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    private String actualizarEstadoUsuarioSQL() {
        return "update t_docente set estado = '" + this.getEstado() + "' where id_docente='" + this.getId() + "'";
    }

    public void agregarUsuarios(ResultSet rsUsuarios, String codPeriodo, AccesoDatos ad, Integer idRol, Integer idTipoEntidad, List<Usuario> usuarios, EntidadUnidos entidadComun) {
        try {
            Integer idUsuario = rsUsuarios.getInt("id_docente");
            if (!usuarios.isEmpty()) {
                Usuario usuario = usuarios.stream().filter(us -> Objects.equals(us.getId(), idUsuario)).findFirst().orElse(null);
                if (usuario == null) {
                    agregarUsuarioRol(rsUsuarios, codPeriodo, ad, idRol, idTipoEntidad, entidadComun);
                }
            } else {
                agregarUsuarioRol(rsUsuarios, codPeriodo, ad, idRol, idTipoEntidad, entidadComun);
            }
        } catch (SQLException e) {

        }
    }

    private void agregarUsuarioRol(ResultSet rsUsuarios, String codPeriodo, AccesoDatos ad, Integer idRol, Integer idTipoEntidad, EntidadUnidos entidadComun) {
        try {
            Integer idUsuario = rsUsuarios.getInt("id_docente");
            this.setNombre(rsUsuarios.getString("nombres"));
            this.setApellido(rsUsuarios.getString("apellidos"));
            this.setId(idUsuario);
            this.setEstado(rsUsuarios.getString("estado"));
            this.setTipo(rsUsuarios.getString("tipo_usuario"));
            this.setEmail("checked");
            RolAD rolAD = new RolAD();
            rolAD.RolEntidadesCargar(entidadComun, idUsuario, idTipoEntidad, ad);
            this.setEntidades(rolAD.getEntidades());

        } catch (SQLException e) {

        }
    }

    public void UsuarioEliminar(AccesoDatos ad, String codPeriodo) throws SQLException {
        try {
            int cont = 0;
            String SQL = "select id_usuario_entidad from t_usuario_entidad as tue\n"
                    + "JOIN t_periodo_academico AS tp\n"
                    + "ON tp.id_periodo = tue.id_periodo and tp.codigo=?\n"
                    + "where id_usuario = ? AND id_rol!=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, codPeriodo);
            ps.setInt(2, this.getId());
            ps.setInt(3, 100);
            ResultSet rsUsuario = ps.executeQuery();
            while (rsUsuario.next()) {
                cont++;
            }
            ps.close();
            if (cont > 0) {
                this.setEstado("D");
                actualizarEstado(ad);

            } else {
                SQL = "delete from t_docente where id_docente= ?";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, this.getId());
                ps.executeUpdate();
                ps.close();
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

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

    private static DocenteAD docenteInformacion(java.lang.String cedula) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.docenteInformacion(cedula);
    }

}
