/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.usuarios.ad;

import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.usuarios.comunes.Usuario;
import dda.silabo.usuarios.comunes.Usuarios;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Evelyn
 */
public class UsuariosAD extends Usuarios {

    public void agregarUsuariosAdministrador(List<EntidadUnidos> entidadesTrabajo, AccesoDatos ad, String codPeriodo, String cedula) throws SQLException {
        ConcurrentHashMap<String, Usuario> usuarios = new ConcurrentHashMap<>();
        try {

            for (EntidadUnidos entidadComun : entidadesTrabajo) {
                String SQL = getSQLEntidadesAdministrador(entidadComun);
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ResultSet rsEntidades = ps.executeQuery();
                while (rsEntidades.next()) {
                    agregarUsuarios(rsEntidades, codPeriodo, ad, cedula, usuarios);
                }
                ps.close();
                ad.getCon().commit();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        this.setDocentes(usuarios);
        this.setTitulo("Usuarios");
    }

    public String getSQLEntidadesAdministrador(EntidadUnidos entidadComun) {
        return "WITH RECURSIVE subEntidad (id,tipo,padre,codigo)AS\n"
                + "(\n"
                + "    SELECT te.* FROM t_entidad AS te\n"
                + "    JOIN t_tipo_entidad AS tte \n"
                + "    on tte.id_tipo_entidad= te.id_tipo_entidad\n"
                + "    WHERE codigo_entidad = '" + entidadComun.getCodigo() + "' AND tte.descripcion='" + entidadComun.getNombre() + "'\n"
                + "\n"
                + "    UNION ALL\n"
                + "    SELECT d.*\n"
                + "    FROM\n"
                + "        t_entidad AS d\n"
                + "    JOIN\n"
                + "        subEntidad AS sd\n"
                + "        ON (d.id_padre = id)\n"
                + ")\n"
                + "SELECT *\n"
                + "FROM subEntidad";

    }

    private void agregarUsuarios(ResultSet rsEntidades, String codPeriodo, AccesoDatos ad, String cedula, ConcurrentHashMap<String, Usuario> usuarios) throws SQLException {
        try {
            String codigo = rsEntidades.getString("codigo");
            Integer tipo_entidad = rsEntidades.getInt("tipo");
            Integer id = rsEntidades.getInt("id");
            String SQL = SQLUsuarios(codigo, tipo_entidad, codPeriodo, cedula, id);
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsUsuarios = ps.executeQuery();
            while (rsUsuarios.next()) {
                UsuarioAD usuarioAD = new UsuarioAD();
                usuarioAD.agregarUsuario(rsUsuarios);
                usuarios.putIfAbsent(usuarioAD.getCedula(), usuarioAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
        }

    }

    private String SQLUsuarios(String codigo, Integer tipo_entidad, String codPeriodo, String cedula, Integer idEntidad) {
        return "Select tui.*,td.cedula,td.tipo_usuario,td.estado FROM t_usuario_entidad AS tue\n"
                + "JOIN t_docente AS td\n"
                + "on tue.id_usuario = td.id_docente\n"
                + "JOIN t_periodo_academico AS tpa \n"
                + "on tpa.id_periodo=tue.id_periodo and tpa.codigo='" + codPeriodo + "'\n"
                + "JOIN t_usuarios_informacion AS tui \n"
                + "ON tui.id_cedula = tue.id_usuario\n"
                + "JOIN t_entidad AS te \n"
                + "on te.id_entidad = tue.id_entidad\n"
                + "where td.cedula!='" + cedula + "' and tue.id_entidad='" + idEntidad + "' AND td.tipo_usuario!='OASIS'";
    }

}
