/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.roles.ad;

import com.google.gson.JsonSyntaxException;
import dda.silabo.ad.SilaboAD;
import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.login.ln.LoginLN;
import dda.silabo.opciones.ad.OpcionAD;
import dda.silabo.opciones.comunes.Opcion;
import dda.silabo.reportes.ad.EntidadAD;
import dda.silabo.roles.comunes.AsignacionRol;
import dda.silabo.roles.comunes.Rol;
import dda.silabo.usuarios.ad.UsuarioAD;
import dda.silabo.usuarios.ad.UsuariosAD;
import dda.silabo.usuarios.comunes.Usuario;
import dda.silabo.usuarios.ln.UsuariosLN;
import ec.edu.espoch.academico.Persona;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adry
 */
public class RolAD extends Rol {

    public void AgregarRol(ResultSet rsRoles, List<Rol> roles) {
        try {
            if (!roles.isEmpty()) {
                String desc = rsRoles.getString("descripcion");
                Rol rol2 = roles.stream().filter(r -> r.getDescRol().equals(desc)).findFirst().orElse(null);
                if (rol2 == null) {
                    agregarRol(rsRoles);
                }
            } else {
                agregarRol(rsRoles);
            }
        } catch (SQLException e) {
            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }

    }

    public int getNumeroRolesAsignado(AccesoDatos ad) throws SQLException {
        Integer result = 0;
        String SQL = "Select id_rol from t_usuario_entidad where(id_rol=?)";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getIdRol());
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                result++;
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public void deleteOpcion(AccesoDatos ad, int numRoles) throws SQLException {
        String SQL = "";
        try {
            if (numRoles == 0) {
                SQL = "DELETE FROM t_roles WHERE (id_rol=?)";
            } else {
                SQL = "Update  t_roles SET estado='D' WHERE (id_rol=?)";
            }

            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getIdRol());
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void guardarAsignacion(AccesoDatos ad) throws SQLException {
        try {

            if (this.getUsuarios().size() > 0) {
                PreparedStatement ps = null;
                String deleteUsuario = "delete from t_roles_usuarios where id_rol=?";
                ps = ad.getCon().prepareStatement(deleteUsuario);
                ps.setInt(1, this.getIdRol());
                ps.executeUpdate();
                ps.close();
                for (int u = 0; u < this.getUsuarios().size(); u++) {
                    String SQLAusuario = "insert into t_roles_usuarios (id_usuario,id_rol) values (?,?)";
                    ps.setInt(1, this.getUsuarios().get(u).getId());
                    ps.setInt(2, this.getIdRol());
                    ps = ad.getCon().prepareStatement(SQLAusuario);
                    ps.executeUpdate();
                    ps.close();
                }
                ad.getCon().commit();
            }
            if (this.getOpciones().size() > 0) {
                PreparedStatement ps = null;
                String deleteOpciones = "delete from t_roles_opciones where id_rol=?";
                ps = ad.getCon().prepareStatement(deleteOpciones);
                ps.setInt(1, this.getIdRol());
                ps.executeUpdate();
                ps.close();
                for (int o = 0; o < this.getOpciones().size(); o++) {
                    String SQLAOpcion = "insert into t_roles_opciones (id_rol,id_opcion) values (?,?)";
                    ps.setInt(1, this.getIdRol());
                    ps.setInt(2, this.getUsuarios().get(o).getId());
                    ps = ad.getCon().prepareStatement(SQLAOpcion);
                    ps.executeUpdate();
                    ps.close();
                }
                ad.getCon().commit();
            }
            ad.getCon().commit();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();

            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void RolOpcionesCargar(AccesoDatos ad) throws SQLException {
        try {

            String SQLRolOpciones = "select top.*,tro.id_rol from t_opciones as top\n"
                    + "left join t_roles_opciones as tro\n"
                    + "on top.id_opcion=tro.id_opcion and tro.id_rol=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQLRolOpciones);
            ps.setInt(1, this.getIdRol());
            ResultSet rsRolOpciones = ps.executeQuery();
            while (rsRolOpciones.next()) {
                OpcionAD opcionAD = new OpcionAD();
                opcionAD.getOpcionRol(rsRolOpciones);
                this.getOpciones().add(opcionAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();

            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void HabilitarRol(AccesoDatos ad) throws SQLException {
        String SQL = "";
        try {
            SQL = "Update  t_roles SET estado='H' WHERE (id_rol=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, this.getIdRol());
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();

            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void agregarRolesUsuario(ResultSet rsRolesUsuario, Integer idUsuario, String codPeriodo, AccesoDatos ad, List<Rol> roles) {
        try {
            if (!roles.isEmpty()) {
                String descRol = rsRolesUsuario.getString("descripcion");
                Rol rol = roles.stream().filter(ro -> ro.getDescRol().equals(descRol)).findFirst().orElse(null);
                if (rol == null) {
                    agregarRolesUs(rsRolesUsuario, idUsuario, codPeriodo, ad);
                }

            } else {
                agregarRolesUs(rsRolesUsuario, idUsuario, codPeriodo, ad);
            }
        } catch (SQLException e) {
            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public Integer guardarRolInicio(AccesoDatos ad, String nombreRol, String estado) throws SQLException {
        Integer idRol = 0;
        try {
            String rolChar = nombreRol.substring(0, 3);
            String SQL = "INSERT INTO t_roles (descripcion,estado, rol_char) VALUES (?,?,?) returning id_rol";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, nombreRol);
            ps.setString(2, estado);
            ps.setString(3, rolChar);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                idRol = rsRoles.getInt("id_rol");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();

            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return idRol;
    }

    public Integer existeRol(String nombreRol, AccesoDatos ad, String SQL) throws SQLException {
        Integer result = 0;
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                result = rsRoles.getInt("id_rol");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;

    }

    public String getSQL(String nombreRol) {
        return "select id_rol, descripcion, estado from t_roles where descripcion = '" + nombreRol + "'";
    }

    public String getSQLRoles(String user, String codPeriodo) {
        String SQL = "SELECT distinct(t_roles.descripcion),t_roles.id_rol, t_roles.rol_char, t_roles.id_tipo_entidad,t_roles.estado FROM t_periodo_academico as tp  \n"
                + "                INNER JOIN t_usuario_entidad ON tp.id_periodo = t_usuario_entidad.id_periodo and tp.codigo='" + codPeriodo + "'\n"
                + "                INNER JOIN t_docente ON t_docente.id_docente=t_usuario_entidad.id_usuario AND cedula='" + user + "'\n"
                + "                INNER JOIN t_roles ON t_roles.id_rol=t_usuario_entidad.id_rol where t_roles.rol_char!='Gen' ";
        return SQL;
    }

    public void agregarRolEntidad(ResultSet rsRoles) throws SQLException {
        this.setIdRol(rsRoles.getInt("id_rol"));
        this.setDescRol(rsRoles.getString("descripcion"));
        this.setEstado(rsRoles.getString("estado"));
        this.setIdTipoEntidad(rsRoles.getInt("id_tipo_entidad"));
        this.setRolChar(rsRoles.getString("rol_char"));
    }

    public String getSqlEntidad(LoginAD loginAD, String codPeriodo) {
        return "select te.id_entidad,te.codigo_entidad,tte.descripcion,te.nombre from t_usuario_entidad AS tue\n"
                + "JOIN t_entidad AS te\n"
                + "on te.id_entidad = tue.id_entidad\n"
                + "JOIN t_tipo_entidad AS tte\n"
                + "on tte.id_tipo_entidad = te.id_tipo_entidad\n"
                + "JOIN t_docente AS td \n"
                + "on td.id_docente=tue.id_usuario and td.cedula='" + loginAD.getCedula() + "'\n"
                + "JOIN t_periodo_academico AS tp \n"
                + "on tp.id_periodo=tue.id_periodo and tp.codigo='" + codPeriodo + "'\n"
                + "JOIN t_roles AS tr \n"
                + "on tr.id_rol = tue.id_rol\n"
                + "where tr.rol_char = '" + loginAD.getRolActivo() + "'";
    }

    private void agregarEntidadesRol(Integer idUsuario, String codPeriodo, int id_tipo_entidad, AccesoDatos ad) throws SQLException {
        try {
            String SQL = SQLentidadesRol(idUsuario, codPeriodo, id_tipo_entidad);
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet entidades = ps.executeQuery();
            while (entidades.next()) {
                EntidadAD entidadAD = new EntidadAD();
                entidadAD.agregarEntidadRol(entidades);
                this.getEntidades().add(entidadAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();

            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }

    }

    private String SQLentidadesRol(Integer idUsuario, String codPeriodo, int id_tipo_entidad) {
        return "select te.id_entidad, te.codigo_entidad, te.nombre, tue.id_rol from t_entidad AS te\n"
                + "left JOIN t_usuario_entidad AS tue\n"
                + "on te.id_entidad = tue.id_entidad and tue.id_usuario = '" + idUsuario + "'\n"
                + "LEFT JOIN t_periodo_academico AS tp  \n"
                + "on tp.id_periodo = tue.id_periodo and tp.codigo='" + codPeriodo + "'\n"
                + "where te.id_tipo_entidad='" + id_tipo_entidad + "' ";
    }

    public void agregarUsuariosRol(List<EntidadUnidos> entidadesTrabajo, AccesoDatos ad, String codPeriodo, String cedula) throws SQLException {
        try {
            UsuariosAD usuariosAD = new UsuariosAD();
            PreparedStatement ps = null;
            for (EntidadUnidos entidadComun : entidadesTrabajo) {
                String SQL = usuariosAD.getSQLEntidadesAdministrador(entidadComun);
                ps = ad.getCon().prepareStatement(SQL);
                ResultSet rsEntidades = ps.executeQuery();
                while (rsEntidades.next()) {
                    Integer id = rsEntidades.getInt("id");
                    SQL = usuariosEntidadRol(id, cedula);
                    ps = ad.getCon().prepareStatement(SQL);
                    ResultSet rsUsuarios = ps.executeQuery();
                    while (rsUsuarios.next()) {
                        UsuarioAD usuarioAD = new UsuarioAD();
                        usuarioAD.agregarUsuarios(rsUsuarios, codPeriodo, ad, this.getIdRol(), this.getIdTipoEntidad(), this.getUsuarios(), entidadComun);
                        if (usuarioAD.getId() != null) {
                            this.getUsuarios().add(usuarioAD);
                        }
                    }

                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();

            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private String usuariosEntidadRol(Integer id, String cedula) {
        return "select td.*, tui.nombres,tui.apellidos, tr.id_rol from t_usuario_entidad AS tue \n"
                + "JOIN t_docente as td \n"
                + "on td.id_docente = tue.id_usuario\n"
                + "JOIN t_usuarios_informacion as tui\n"
                + "on tui.id_cedula = td.id_docente\n"
                + "left join t_roles as tr \n"
                + "on tr.id_rol = tue.id_rol and tue.id_rol='" + this.getIdRol() + "'\n"
                + "where tue.id_entidad='" + id + "' and td.cedula!='" + cedula + "' ";
    }

    public void RolUsuariosGuardar(AccesoDatos ad, String codPeriodo, AsignacionRol asignacionRol) throws UnsupportedEncodingException, ExecutionException, SQLException {

        try {
            SilaboAD silaboAD = new SilaboAD();
            silaboAD.setPeriodo(codPeriodo);
            PreparedStatement ps = null;

            Integer idPeriodo = silaboAD.idPeriodoSelect(ad);
            Integer idCedula = 0;
            if (asignacionRol.getIdUsuario() == null) {
                String Informacion = asignacionRol.getInformacion();
                String[] datos = Informacion.split(",");
                String nombre = datos[0];
                String apellidos = datos[1];
                String correo = datos[2];
                String cedula = datos[3];
                LoginAD loginAD = new LoginAD();
                Persona docente = new Persona();
                docente.setApellidos(apellidos);
                docente.setNombres(nombre);
                docente.setCedula(cedula);
                docente.setEmail(correo);
                idCedula = loginAD.agregarInformacionUsuario(docente, ad, "OASIS");

                if (obtenerRolesUsuario(idCedula, idPeriodo, ad)) {
                    LoginLN lN = new LoginLN();
                    lN.AutenticarUsuario(cedula);
                }
            } else {
                idCedula = asignacionRol.getIdUsuario();
                String SQL = "DELETE FROM t_usuario_entidad\n"
                        + " WHERE id_rol='100' and id_usuario=?";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, idCedula);
                ps.executeUpdate();
                ps.close();
            }

            RolUsuariosAgregar(idCedula, ps, asignacionRol.getIdRol(), asignacionRol.getIdEntidad(), ad, idPeriodo);
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();

        }
    }

    private void RolUsuariosAgregar(Integer idUsuario, Statement ps2, Integer idRol, Integer idEntidad, AccesoDatos ad, Integer idPeriodo) throws SQLException {

        String SQL = "";//"delete from t_usuario_entidad where (id_usuario='" + idUsuario + "' and id_rol='" + idRol + "')";

        Integer idUsuarioEntidad = 0;
        PreparedStatement ps = null;
        try {
//            ps.executeUpdate(SQL);
            SQL = "select id_usuario_entidad from t_usuario_entidad where id_rol=? and id_entidad =?";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idRol);
            ps.setInt(2, idEntidad);
            ResultSet entidades = ps.executeQuery();
            while (entidades.next()) {
                idUsuarioEntidad = entidades.getInt("id_usuario_entidad");
            }
            ps.close();
            if (idUsuarioEntidad != 0) {
                SQL = "UPDATE t_usuario_entidad\n"
                        + "   SET id_usuario=?\n"
                        + " WHERE id_usuario_entidad=?";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, idUsuario);
                ps.setInt(2, idUsuarioEntidad);
                ps.executeUpdate();
                ps.close();
            } else {
                SQL = "insert into t_usuario_entidad (id_usuario,id_rol,id_entidad,id_periodo) values (?,?,?,?);";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, idUsuario);
                ps.setInt(2, idRol);
                ps.setInt(3, idEntidad);
                ps.setInt(4, idPeriodo);
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

    private void agregarRol(ResultSet rsRoles) {
        try {
            this.setIdRol(rsRoles.getInt("id_rol"));
            this.setDescRol(rsRoles.getString("descripcion"));
            this.setEstado(rsRoles.getString("estado"));
            this.setIdTipoEntidad(rsRoles.getInt("id_tipo_entidad"));
        } catch (SQLException e) {
            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private void agregarRolesUs(ResultSet rsRolesUsuario, Integer idUsuario, String codPeriodo, AccesoDatos ad) {
        try {
            this.setIdRol(rsRolesUsuario.getInt("id_rol"));
            this.setDescRol(rsRolesUsuario.getString("descripcion"));
            int id_tipo_entidad = rsRolesUsuario.getInt("id_tipo_entidad");
            this.setCheck("checked");
            agregarEntidadesRol(idUsuario, codPeriodo, id_tipo_entidad, ad);
        } catch (SQLException e) {

        }
    }

    public void RolOpcionesGuardar(AccesoDatos ad) throws SQLException {
        String SQL = "";
        try {

            String deleteOpciones = "delete from t_roles_opciones where id_rol=?";
            PreparedStatement ps = ad.getCon().prepareStatement(deleteOpciones);
            ps.setInt(1, this.getIdRol());
            ps.executeUpdate();
            ps.close();
            for (Opcion opcion : this.getOpciones()) {
                SQL = "insert into t_roles_opciones (id_rol,id_opcion) values (?,?)";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, this.getIdRol());
                ps.setInt(2, opcion.getIdOpcion());
                ps.executeUpdate();
                ps.close();
                ad.getCon().commit();

            }
            ad.getCon().commit();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void UsuarioRolesCargar(ResultSet rsRoles, EntidadUnidos entidadComun, Integer idUsuario, AccesoDatos ad) {
        try {
            this.setIdRol(rsRoles.getInt("id_rol"));
            this.setDescRol(rsRoles.getString("descripcion"));
            this.setEstado(rsRoles.getString("estado"));
            this.setIdTipoEntidad(rsRoles.getInt("id_tipo_entidad"));
            this.setCheck("checked");
            if (this.getIdTipoEntidad() == 2) {
                agregarClickRoles("mostrarEntidades(this," + this.getIdTipoEntidad() + ");");
            } else {
                agregarClickRoles("mostrarSiguienteNivelEntidades(this," + this.getIdTipoEntidad() + ",'Carreras');");
            }
//            RolEntidadesCargar(entidadComun, idUsuario, this.getIdTipoEntidad(), ad);

        } catch (SQLException e) {
            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void RolEntidadesCargar(EntidadUnidos entidadComun, Integer idUsuario, Integer idTipoEntidad, AccesoDatos ad) throws SQLException {
        try {

            String SQL = "WITH RECURSIVE subEntidad (id_entidad,tipo,padre,codigo_entidad)AS\n"
                    + "(\n"
                    + "    SELECT te.* FROM t_entidad AS te\n"
                    + "    JOIN t_tipo_entidad AS tte \n"
                    + "    on tte.id_tipo_entidad= te.id_tipo_entidad \n"
                    + "    WHERE codigo_entidad = ? and descripcion=?	\n"
                    + "\n"
                    + "    UNION ALL\n"
                    + "    SELECT d.*\n"
                    + "    FROM\n"
                    + "        t_entidad AS d\n"
                    + "        \n"
                    + "    JOIN\n"
                    + "        subEntidad AS sd\n"
                    + "        ON (d.id_padre = sd.id_entidad)\n"
                    + ")\n"
                    + "SELECT tr.*,(Select codigo_entidad from subEntidad where tr.padre=subEntidad.id_entidad) as codigoPadre, tue.id_rol\n"
                    + "FROM subEntidad AS tr\n"
                    + "left join t_usuario_entidad as tue on tr.id_entidad= tue.id_entidad and tue.id_usuario = ?\n"
                    + "where tipo=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, entidadComun.getCodigo());
            ps.setString(2, entidadComun.getNombre());
            ps.setInt(3, idUsuario);
            ps.setInt(4, idTipoEntidad);
            ResultSet entidades = ps.executeQuery();
            while (entidades.next()) {
                EntidadAD entidadAD = new EntidadAD();
                entidadAD.agregarEntidadRol(entidades);
                this.getEntidades().add(entidadAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("RolAD").log(Level.SEVERE, "dda.silabos.roles.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private void agregarClickRoles(String fncClick) {
        this.setFncClick(fncClick);
    }

    private boolean obtenerRolesUsuario(Integer idCedula, Integer idPeriodo, AccesoDatos ad) throws SQLException {
        boolean result = true;
        String SQL = "select id_usuario_entidad from t_usuario_entidad where id_usuario=? and id_periodo=? limit 1";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idCedula);
            ps.setInt(2, idPeriodo);
            ResultSet entidades = ps.executeQuery();
            while (entidades.next()) {
                result = false;
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }

        return result;
    }
}
