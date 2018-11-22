/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.login.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.login.comunes.LoginComunes;
import dda.silabo.roles.ad.RolAD;
import ec.edu.espoch.academico.ArrayOfMateria;
import ec.edu.espoch.academico.Persona;
import ec.edu.espoch.academico.RolCarrera;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import unidos.sw.DocenteAD;
import unidos.sw.DocenteTitulos;

/**
 *
 * @author Jorge Zaruma
 */
public class LoginAD extends LoginComunes {

    public String getSQLGetLogin(String cedula) {
        String SQL = "SELECT distinct (t_periodo_academico.codigo ) FROM t_docente \n"
                + "                INNER JOIN t_usuario_entidad ON t_docente.id_docente=t_usuario_entidad.id_usuario \n"
                + "                INNER JOIN t_periodo_academico ON t_periodo_academico.id_periodo = t_usuario_entidad.id_periodo\n"
                + "                WHERE cedula='" + cedula + "'";
        return SQL;
    }

    public String getSQLDatosUsuario(Integer idUsuarioLocal) {
        String SQL = "SELECT * FROM t_usuarios_informacion WHERE id_cedula='" + idUsuarioLocal + "';";
        return SQL;
    }

    public void getDatos(String SQL, AccesoDatos ad, String cedula) throws SQLException {
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsDatos = ps.executeQuery();
            while (rsDatos.next()) {
                String nombres = rsDatos.getString("nombres");
                String apellidos = rsDatos.getString("apellidos");
                String email = rsDatos.getString("email");
                this.setNombres(nombres);
                this.setApellidos(apellidos);
                this.setCedula(cedula);
                this.setEmail(email);
            }
            ad.getCon().commit();
            ps.close();
        } catch (Exception e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void agregarRoles(String user, String codigo, AccesoDatos ad) throws SQLException {
        if (user != null && codigo != null) {
            RolAD rolAD = new RolAD();
            String SQL = rolAD.getSQLRoles(user, codigo);
            try {
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ResultSet rsRoles = ps.executeQuery();
                while (rsRoles.next()) {
                    rolAD = new RolAD();
                    rolAD.agregarRolEntidad(rsRoles);
                    this.getRoles().add(rolAD);
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
    }

    public String getSQLTipoUsuario(String cedula) {
        String SQL = "SELECT tipo_usuario FROM t_docente WHERE cedula='" + cedula + "';";
        return SQL;
    }

    public Integer agregarInformacionUsuario(Persona docente, AccesoDatos ad, String tipoUsuario) throws SQLException {
        Integer idResult = 0;
        try {
            if (docente != null) {
                String SQL = "SELECT id_docente\n"
                        + "  FROM t_docente where cedula=?;";
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, docente.getCedula());
                ResultSet rsEntidad = ps.executeQuery();
                while (rsEntidad.next()) {
                    idResult = rsEntidad.getInt("id_docente");
                }
                ps.close();
                if (idResult == 0) {
                    SQL = "insert into t_docente (cedula,tipo_usuario,estado) values (?,?,?) returning id_docente";
                    ps = ad.getCon().prepareStatement(SQL);
                    ps.setString(1, docente.getCedula());
                    ps.setString(2, tipoUsuario);
                    ps.setString(3, "H");
                    rsEntidad = ps.executeQuery();
                    while (rsEntidad.next()) {
                        idResult = rsEntidad.getInt("id_docente");
                    }
                    ps.close();
                    String SQLInsert = "insert into t_usuarios_informacion (id_cedula,nombres,apellidos,email,telefono) values (?,?,?,?,?)";
                    ps = ad.getCon().prepareStatement(SQLInsert);
                    ps.setInt(1, idResult);
                    ps.setString(2, docente.getNombres());
                    ps.setString(3, docente.getApellidos());
                    ps.setString(4, docente.getEmail());
                    ps.setString(5, "0000000");
                    ps.executeUpdate();
                    ps.close();
                    DocenteAD docenteAD = docenteInformacion(docente.getCedula());
                    if (docenteAD != null) {
                        for (DocenteTitulos docenteTitulos : docenteAD.getCuartoNivel()) {
                            SQLInsert = "INSERT INTO t_usuario_titulos(\n"
                                    + "            id_usuario, descripcion, nivel,estado)\n"
                                    + "    VALUES (?,?,?,?);";
                            ps = ad.getCon().prepareStatement(SQLInsert);
                            ps.setInt(1, idResult);
                            ps.setString(2, docenteTitulos.getDescripcion());
                            ps.setInt(3, docenteTitulos.getNivel());
                            ps.setString(4, "ingreso");
                            ps.executeUpdate();
                            ps.close();
                        }
                        for (DocenteTitulos docenteTitulos : docenteAD.getTercerNivel()) {
                            SQLInsert = "INSERT INTO t_usuario_titulos(\n"
                                    + "            id_usuario, descripcion, nivel,estado)\n"
                                    + "    VALUES (?,?,?,?);";
                            ps = ad.getCon().prepareStatement(SQLInsert);
                            ps.setInt(1, idResult);
                            ps.setString(2, docenteTitulos.getDescripcion());
                            ps.setInt(3, docenteTitulos.getNivel());
                            ps.setString(4, "ingreso");
                            ps.executeUpdate();
                            ps.close();
                        }
                        String SQLUpdate = "UPDATE t_usuarios_informacion\n"
                                + "   SET telefono=?\n"
                                + " WHERE id_cedula=?";

                        ps = ad.getCon().prepareStatement(SQLUpdate);
                        ps.setString(1, docenteAD.getCelular());
                        ps.setInt(2, idResult);
                        ps.executeUpdate();
                        ps.close();
                    }
                }
            }
            ad.getCon().commit();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return idResult;
    }

    public String getrolEstudiante(List<RolCarrera> rolesCarreras, String cedula, String codPeriodo) {
        String result = "Docente";
        rolesCarreras = rolesCarreras.stream().filter(rc -> rc.getNombreRol().equals("EST")).collect(Collectors.toList());
        for (RolCarrera rolCarrera : rolesCarreras) {
            ArrayOfMateria materias = getMateriasEstudiante(rolCarrera.getCodigoCarrera(), cedula, codPeriodo);
            if (materias != null) {
                {
                    if (materias.getMateria().size() > 0) {
                        result = "Estudiante";
                        this.setIdRolActivo(0);
                        this.setRolActivo("Est");
                        this.setCedula(cedula);
                        this.setNombres("Estudiante");
                        this.setApellidos("Estudiante");
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static ArrayOfMateria getMateriasEstudiante(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getMateriasEstudiante(arg0, arg1, arg2);
    }

    private static DocenteAD docenteInformacion(java.lang.String cedula) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.docenteInformacion(cedula);
    }

}
