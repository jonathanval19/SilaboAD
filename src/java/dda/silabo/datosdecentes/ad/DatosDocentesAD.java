/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.datosdecentes.ad;

import dda.silabo.ad.SilaboAD;
import dda.silabo.datosdocentes.comunes.DatoDocente;
import dda.silabo.datosdocentes.comunes.DatosDocentes;
import dda.silabo.db.AccesoDatos;
import ec.edu.espoch.academico.ArrayOfDictadoMateria;
import ec.edu.espoch.academico.DictadoMateria;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Jorge Zaruma
 */
public class DatosDocentesAD extends DatosDocentes {

    public void agregarDocente(String codMateria, String codCarrera, AccesoDatos ad) throws UnsupportedEncodingException, IOException, SQLException {
        ArrayOfDictadoMateria arrayOfDictadoMateria = lPADictadosMateria(codCarrera, codMateria);
        if (arrayOfDictadoMateria != null) {
            try {
                for (DictadoMateria dictadoMateria : arrayOfDictadoMateria.getDictadoMateria()) {
                    String SQL = getDocentesSQL();

                    PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                    ps.setString(1, dictadoMateria.getDocente().getCedula());
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        DatoDocenteAD datoDocenteAD = new DatoDocenteAD();
                        datoDocenteAD.agregarDocentesAsignatura(rs, "DOCENTE", ad);
                        this.getDatosdocentes().add(datoDocenteAD);
                    }

                    ad.getCon().commit();
                    ps.close();
                }

            } catch (SQLException e) {
                ad.getCon().rollback();
                ad.Desconectar();
            }
            List<DatoDocente> docentes = this.getDatosdocentes().stream().filter(distinctByKey(doc -> doc.getCedula())).collect(Collectors.toList());
            this.setDatosdocentes(docentes);
            this.setTitulo("Datos de Docentes");
            this.setAyuda("<div style='text-align:justify'>Se presenta la infromación de los Docentes que imparten la asignatura que se esta realizando el Sílabo</div>");
        }
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private String getDocentesSQL() {
        return "Select distinct on(td.cedula)tui.*,td.cedula from t_usuarios_informacion AS tui\n"
                + "               join t_docente AS td ON td.id_docente=tui.id_cedula and td.cedula=?";
    }

    public void AutoridadesSilaboCargar(SilaboAD silaboad, AccesoDatos ad) throws UnsupportedEncodingException, IOException, SQLException {
        String SQL = "Select tui.*, te.id_entidad,td.cedula,\n"
                + "(select nombre from t_entidad as teA\n"
                + "where (teA.id_entidad=(select id_padre from t_entidad as teA2 where (teA2.id_entidad=te.id_entidad))) )as nombrePadre \n"
                + "from t_usuario_entidad as tue \n"
                + "join t_usuarios_informacion as tui \n"
                + "on tui.id_cedula = tue.id_usuario\n"
                + "join t_entidad as te\n"
                + "on te.id_entidad = tue.id_entidad\n"
                + "join t_tipo_entidad as tte\n"
                + "on tte.id_tipo_entidad = te.id_tipo_entidad and tte.id_tipo_entidad='4'\n"
                + "JOIN t_roles as tr \n"
                + "on tr.id_rol = tue.id_rol and tr.rol_char = 'Dir'\n"
                + "join t_docente as td on td.id_docente = tue.id_usuario\n"
                + "where te.codigo_entidad = ?";
        Integer idPadre = 0;
        DatoDocenteAD datoDocenteAD1 = new DatoDocenteAD();
        DatoDocenteAD datoDocenteAD2 = new DatoDocenteAD();
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, silaboad.getCodCarrera());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                idPadre = rs.getInt("id_entidad");
                datoDocenteAD1.agregarDocente(rs, "FIRMA DEL DIRECTOR DE CARRERA ");

            }
            ps.close();
            SQL = "Select tui.*,td.cedula,te.nombre from t_usuario_entidad as tue \n"
                    + "join t_usuarios_informacion as tui \n"
                    + "on tui.id_cedula = tue.id_usuario \n"
                    + "join t_entidad as te\n"
                    + "on te.id_entidad = tue.id_entidad and te.id_padre=?\n"
                    + "join t_tipo_entidad as tte\n"
                    + "on tte.id_tipo_entidad = te.id_tipo_entidad and tte.id_tipo_entidad='5'\n"
                    + "JOIN t_roles as tr \n"
                    + "on tr.id_rol = tue.id_rol and tr.rol_char = 'Cor'\n"
                    + "join t_docente as td on td.id_docente = tue.id_usuario\n"
                    + "where te.codigo_entidad = ?";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idPadre);
            ps.setString(2, silaboad.getCampoFormacion());
            rs = ps.executeQuery();
            while (rs.next()) {
                datoDocenteAD2.agregarDocente(rs, "FIRMA DEL COORDINADOR DE CAMPO ");

            }
            ps.close();
            ad.getCon().commit();
            if (datoDocenteAD2.getApellidos() != null) {
                this.getDatosdocentes().add(datoDocenteAD2);
            }
            if (datoDocenteAD1.getApellidos() != null) {
                this.getDatosdocentes().add(datoDocenteAD1);
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void agregarDocenteInicio(List<DictadoMateria> dictadoMateria) {
        try {
            ConcurrentHashMap<String, DatoDocente> docentes = new ConcurrentHashMap<>();
            for (DictadoMateria materia : dictadoMateria) {
                DatoDocenteAD datoDocenteAD = new DatoDocenteAD();
                datoDocenteAD.agregarDocenteInicio(materia);
                docentes.putIfAbsent(datoDocenteAD.getCedula(), datoDocenteAD);

            }
            this.setDocentesHash(docentes);
        } catch (Exception e) {
        }
    }

    private static ArrayOfDictadoMateria lPADictadosMateria(java.lang.String arg0, java.lang.String arg1) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.lPADictadosMateria(arg0, arg1);
    }
}
