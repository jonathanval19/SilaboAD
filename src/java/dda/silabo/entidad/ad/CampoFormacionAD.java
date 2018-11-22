/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.entidad.ad;

import com.google.gson.Gson;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CampoFormacionUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.reportes.ad.EstadosAD;
import ec.edu.espoch.academico.ArrayOfDictadoMateria;
import ec.edu.espoch.academico.ArrayOfMateria;
import ec.edu.espoch.academico.ArrayOfMateriaPensum;
import ec.edu.espoch.academico.Materia;
import ec.edu.espoch.academico.MateriaPensum;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import redis.clients.jedis.Jedis;

/**
 *
 * @author Jorge Zaruma
 */
public class CampoFormacionAD extends CampoFormacionUnidos {

    public String getCampoCarreraActual(AccesoDatos ad, String user, String codCarrera, String codPeriodo) throws SQLException {
        String result = "todos";
        try {
            String SQL = "select te.codigo_entidad from t_entidad AS te\n"
                    + "                    JOIN t_usuario_entidad AS tue\n"
                    + "                   on tue.id_entidad=te.id_entidad and\n"
                    + "                    te.id_padre = (select id_entidad from t_entidad where codigo_entidad=? and id_tipo_entidad =4)\n"
                    + "                    JOIN t_docente AS td \n"
                    + "                    on td.id_docente=tue.id_usuario\n"
                    + "                    JOIN t_periodo_academico AS tpa \n"
                    + "                    on tpa.id_periodo = tue.id_periodo and tpa.codigo=?\n"
                    + "                   where td.cedula=?";
            if (ad.Connectar() != 0) {
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ps.setString(1, codCarrera);
                ps.setString(2, codPeriodo);
                ps.setString(3, user);
                ResultSet rsEntidad = ps.executeQuery();
                while (rsEntidad.next()) {
                    result = rsEntidad.getString("codigo_entidad");

                }
                ad.getCon().commit();
                ps.close();
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        } 
        return result;
    }

    public void agregarCampoFormacion(MateriaPensum materiaPensum, List<MateriaPensum> materias, AccesoDatos ad, String codPeriodo, String codCarrera) {
        try {
            this.setCodCampoF(materiaPensum.getCodArea());
            this.setDescCampoF(materiaPensum.getArea());
            EstadosAD estadoAD = new EstadosAD();
            estadoAD.getEstadosSilabos(materiaPensum, materias, ad, codPeriodo, codCarrera);
            this.setEstados(estadoAD);
        } catch (Exception e) {

        }
    }

    public boolean agregarCampoFormacion2(MateriaPensum materiaPensum, List<MateriaPensum> materias, AccesoDatos ad, String codPeriodo, String codCarrera, List<CampoFormacionUnidos> campos) {
        boolean result = false;
        try {
            int cont = 0;
            for (int i = 0; i < campos.size(); i++) {
                if (campos.get(i).getCodCampoF().equals(materiaPensum.getCodArea())) {
                    cont++;
                }
            }
            if (cont == 0) {
                this.setCodCampoF(materiaPensum.getCodArea());
                this.setDescCampoF(materiaPensum.getArea());
                EstadosAD estadoAD = new EstadosAD();
                estadoAD.getEstadosSilabos(materiaPensum, materias, ad, codPeriodo, codCarrera);
                this.setEstados(estadoAD);
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public List<AsignaturaUnidos> obtenerMateriasPensum(List<AsignaturaUnidos> asignaturas, LoginAD loginAD, Jedis jedis) {
        String cantidadMaterias = jedis.get(loginAD.getCedula() + loginAD.getRolActivo());
        Integer numeroMaterias = Integer.parseInt(cantidadMaterias);
        List<AsignaturaUnidos> result = new ArrayList<>();
        Gson G = new Gson();
        if (loginAD.getRolActivo().equals("Doc")) {
            ArrayOfMateria materiasDocente = G.fromJson(jedis.get("Materias" + loginAD.getCedula() + loginAD.getRolActivo()), ArrayOfMateria.class);
            for (AsignaturaUnidos asignaturaComun : asignaturas) {
                Materia materias = materiasDocente.getMateria().stream().filter(mat -> mat.getCodigo().equals(asignaturaComun.getCodMateria())).findFirst().orElse(null);
                if (materias != null) {
                    AsignaturaUnidos materiaPensum = new AsignaturaUnidos();
                    materiaPensum.setCodMateria(asignaturaComun.getCodMateria());
                    materiaPensum.setNombreMateria(asignaturaComun.getNombreMateria());
                    result.add(materiaPensum);
                    numeroMaterias--;
                    jedis.set(loginAD.getCedula() + loginAD.getRolActivo(), numeroMaterias.toString());
                }
            }
        } else {
            result = asignaturas;
        }
        return result;
    }

    public String getCarreraCampo(String codCampoF, AccesoDatos ad, String cedula) throws SQLException {
        String result = null;
        try {
            String SQL = "WITH RECURSIVE subEntidad (codigo,entidad,idpadre) AS \n"
                    + "                    (\n"
                    + "                        SELECT t_entidad.codigo_entidad,te.descripcion,t_entidad.id_padre FROM t_entidad \n"
                    + "                    JOIN\n"
                    + "                    	t_tipo_entidad AS te\n"
                    + "                    	on te.id_tipo_entidad = t_entidad.id_tipo_entidad\n"
                    + "                    	JOIN t_usuario_entidad AS tue \n"
                    + "                    on tue.id_entidad = t_entidad.id_entidad\n"
                    + "                    JOIN t_docente AS td\n"
                    + "                    on td.id_docente = tue.id_usuario and td.cedula=?\n"
                    + "                        WHERE codigo_entidad = ? AND te.id_tipo_entidad=5\n"
                    + "                         \n"
                    + "                       UNION ALL\n"
                    + "                        SELECT d.codigo_entidad,te.descripcion,d.id_padre\n"
                    + "                        FROM\n"
                    + "                            t_entidad AS d\n"
                    + "                       JOIN\n"
                    + "                          subEntidad AS sd\n"
                    + "                           ON (d.id_entidad = idpadre) and d.id_tipo_entidad=4\n"
                    + "                           JOIN\n"
                    + "                    	t_tipo_entidad AS te\n"
                    + "                    on te.id_tipo_entidad = d.id_tipo_entidad\n"
                    + "                                        \n"
                    + "                    )\n"
                    + "                    SELECT *\n"
                    + "                    FROM subEntidad\n"
                    + "                    ORDER BY idpadre LIMIT 1;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, cedula);
            ps.setString(2, codCampoF);
            ResultSet rsCarrera = ps.executeQuery();
            while (rsCarrera.next()) {
                result = rsCarrera.getString("codigo");

            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return result;
    }

    public List<AsignaturaUnidos> obtenermateriasPensumDocente(List<AsignaturaUnidos> asignaturas, LoginAD loginAD, Jedis jedis, String codCarrera, String codPeriodo, String cedula) {
        List<AsignaturaUnidos> result = new ArrayList<>();
        Gson G = new Gson();
        ArrayOfMateria materiasDocente = G.fromJson(jedis.get("Materias" + loginAD.getCedula() + loginAD.getRolActivo()), ArrayOfMateria.class);
        if (materiasDocente == null) {
            materiasDocente = getMateriasDocente(codCarrera, loginAD.getCedula(), codPeriodo);
        }
        for (Materia materia : materiasDocente.getMateria()) {
            loginAD.setCedula(codCarrera);
            AsignaturaUnidos asignaturaComun = asignaturas.stream().filter(as -> as.getCodMateria().equals(materia.getCodigo())).findFirst().orElse(null);
            if (asignaturaComun != null) {
                result.add(asignaturaComun);
            }
        }
        return result;
    }

    public Integer getSQLEntidad(AccesoDatos ad, String codCarrera) throws SQLException {
        Integer result = 0;
        try {
            String SQL = "select te.id_entidad from t_entidad AS te\n"
                    + "where codigo_entidad = ? AND id_tipo_entidad= 4";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, codCarrera);
            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                result = rsEntidad.getInt("id_entidad");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        } 
        return result;
    }

    public Integer getSQLCamposFormacion(AccesoDatos ad, String codCarrera, int idTipoEntidad) throws SQLException {
        Integer result = 0;
        String SQL = "WITH RECURSIVE subEntidad AS\n"
                + "(\n"
                + "    SELECT * FROM t_entidad WHERE codigo_entidad = ? AND id_tipo_entidad=?\n"
                + "\n"
                + "    UNION ALL\n"
                + "    SELECT d.*\n"
                + "    FROM\n"
                + "        t_entidad AS d\n"
                + "    JOIN\n"
                + "        subEntidad AS sd\n"
                + "        ON (d.id_padre = sd.id_entidad)\n"
                + ")\n"
                + "SELECT *\n"
                + "FROM subEntidad\n"
                + "ORDER BY id_tipo_entidad;";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, codCarrera);
            ps.setInt(2, idTipoEntidad);
            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                result++;
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        } 
        return result;
    }

    private static ArrayOfMateria getMateriasDocente(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getMateriasDocente(arg0, arg1, arg2);
    }

    public List<AsignaturaUnidos> obtenerMateriasPensumDocente(String cedula, String codCarrera, String codPeriodo) {
        List<AsignaturaUnidos> asignaturaUnidoses = new ArrayList<>();
        try {
            ArrayOfMateria arrayOfMateria = getMateriasDocente(codCarrera, cedula, codPeriodo);
            ArrayOfMateriaPensum aomp = pensumVigente(codCarrera);
            if (arrayOfMateria != null) {
                for (Materia materiaPensum : arrayOfMateria.getMateria()) {
                    MateriaPensum m = aomp.getMateriaPensum().stream().filter(mp -> mp.getCodMateria().equals(materiaPensum.getCodigo())).findFirst().orElse(null);
                    if (m != null) {
                        AsignaturaUnidos asignaturaUnidos = new AsignaturaUnidos();
                        asignaturaUnidos.setCodMateria(materiaPensum.getCodigo());
                        asignaturaUnidos.setNombreMateria(materiaPensum.getNombre());
                        asignaturaUnidos.setCodArea(m.getCodArea());
                        asignaturaUnidos.setArea(m.getArea());
                        asignaturaUnidoses.add(asignaturaUnidos);
                    }
                }
            }
        } catch (Exception e) {
        }
        return asignaturaUnidoses;
    }

    public List<CampoFormacionUnidos> obtenerCamposFormacionDocente(List<AsignaturaUnidos> materias, String codCarrera) {
        List<CampoFormacionUnidos> result = new ArrayList<>();
        materias.stream().collect(Collectors.groupingBy(mr -> mr.getCodArea(), Collectors.toList())).forEach((id, lista)
                -> result.add(verificar(id, lista)));
        return result;
    }

    private static ArrayOfMateriaPensum pensumVigente(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.pensumVigente(jsonCarrera);
    }

    private CampoFormacionUnidos verificar(String id, List<AsignaturaUnidos> lista) {
        CampoFormacionUnidos result = new CampoFormacionUnidos();
        result.setCodCampoF(id);
        result.setAsignaturas(lista);
        return result;
    }

}
