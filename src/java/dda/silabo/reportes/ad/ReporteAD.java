/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.reportes.ad;

import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.clases.unidos.EscuelaUnidos;
import dda.silabo.clases.unidos.FacultadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.CarrerasAD;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.reportes.comunes.Elemento;
import dda.silabo.reportes.comunes.ReporteComun;
import ec.edu.espoch.academico.ArrayOfMateria;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import redis.clients.jedis.Jedis;

/**
 *
 * @author Jorge Zaruma
 */
public class ReporteAD extends ReporteComun {

    public void agregarCriteriosEntidadUsuario(AccesoDatos ad, LoginAD loginAD, String codPeriodo, List<FacultadUnidos> facultades, EntidadAD entidadAD) throws SQLException {//reporte criterios de silabo
        facultades.forEach((facultadComun) -> {
            FacultadAD facultadAD = new FacultadAD();
            facultadAD.agregarFacultadCriteriosSilabo(facultadComun, ad, loginAD, codPeriodo, entidadAD);
            if (!facultadAD.getEscuelas().isEmpty()) {
                this.getFacultades().add(facultadAD);
            }
        });
        switch (entidadAD.getTipo()) {
            case "bibliografias":
                this.setSubtitulo("BIBLIOGRAFÍA BÁSICA");
                break;
            case "criterios":
                this.setSubtitulo("CRITERIOS DE EVALUACIÓN");
                break;
            case "recursos":
                this.setSubtitulo("RECURSOS");
                break;
            case "logros":
                this.setSubtitulo("LOGROS DE APRENDIZAJE");
                break;
            default:
                break;
        }
        this.setTitulo("");
        this.setAyuda("REPORTE DE " + obtenerDescripcionRol(loginAD, ad));
    }

    public void agregarEstadosCamposFormacionDocentes(List<AsignaturaUnidos> asignaturas, AccesoDatos ad, String codPeriodo, String codCarrera, LoginAD loginAD, Jedis jedis, String descripcion, String tipoEntidad) {
        this.setTitulo(descripcion);
        if (tipoEntidad.equals("campoformacion")) {
            this.setSubtitulo("ASIGNATURAS");
            this.setAyuda("ESTADOS DE SILABOS POR CAMPO DE FORMACIÓN");
        } else {
            this.setSubtitulo("DOCENTES");
            this.setAyuda("ESTADOS DE SILABOS POR DOCENTE");
        }
        Elemento e = new Elemento();
        e.setCodigo(codCarrera);
        this.getElementos().add(e);
        for (int i = 0; i < 4; i++) {
            String estado = "Inicio";
            if (i == 1) {
                estado = "Corregido";
            }
            if (i == 2) {
                estado = "Corregir";
            }
            if (i == 3) {
                estado = "Aprobado";
            }
            EstadosAD estadosAD = new EstadosAD();
            ArrayOfMateria materias = null;
            if (loginAD.getRolActivo().equals("Doc")) {
                materias = getMateriasDocente(codCarrera, loginAD.getCedula(), codPeriodo);
            }
            estadosAD.agregarAsignaturas(estado, asignaturas, ad, codPeriodo, codCarrera, loginAD, materias, jedis);

            this.getDatos().add(estadosAD);
        }
    }

    private String obtenerDescripcionRol(LoginAD loginAD, AccesoDatos ad) throws SQLException {
        String result = "";
        try {
            String SQL = "select descripcion from t_roles \n"
                    + "where rol_char=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, loginAD.getRolActivo());
            ResultSet rsRol = ps.executeQuery();
            while (rsRol.next()) {
                result = rsRol.getString("descripcion").toUpperCase();
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return result;
    }

    public void ReporteEstadoSilabos(AccesoDatos ad, LoginAD loginAD, String codPeriodo, List<FacultadUnidos> facultades) throws SQLException {
        try {
            String SQL = "SELECT descripcion\n"
                    + "  FROM t_roles where rol_char=?;";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, loginAD.getRolActivo());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                this.setAyuda(rs.getString("descripcion"));
            }

            this.setFacultades(facultades);
            for (FacultadUnidos fc : this.getFacultades()) {
                for (EscuelaUnidos ec : fc.getEscuelas()) {
                    for (CarreraUnidos cc : ec.getCarreras()) {
                        CarrerasAD ca = new CarrerasAD();
                        ca.setCodCarrera(cc.getCodCarrera());
                        ca.agregarAsignaturasReporte(ad, loginAD, codPeriodo);
                        if (!ca.getEstadosSilabo().isEmpty()) {
                            cc.setEstadosSilabo(ca.getEstadosSilabo());
                        }
                    }

                }

            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    private static ArrayOfMateria getMateriasDocente(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getMateriasDocente(arg0, arg1, arg2);
    }

}
