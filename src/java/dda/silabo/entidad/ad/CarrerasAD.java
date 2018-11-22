/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.entidad.ad;

import com.google.gson.Gson;
import dda.silabo.ad.SilaboAD;
import dda.silabo.bibliografias.comunes.BibliografiaLibro;
import dda.silabo.bibliografias.comunes.BibliografiaSitioWeb;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.clases.unidos.DocenteUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ln.EntidadLN;
import dda.silabo.reportes.ad.EstadosAD;
import dda.silabo.importacion.ad.PeriodoAD;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.login.ln.PeriodoLN;
import dda.silabo.reportes.ad.CriterioSilaboAD;
import dda.silabo.reportes.ad.EntidadAD;
import dda.silabo.reportes.ad.FacultadAD;
import dda.silabo.reportes.comunes.CriterioSilabo;
import ec.edu.espoch.academico.Periodo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import unidos.sw.CarreraAD;

/**
 *
 * @author Jorge Zaruma
 */
public class CarrerasAD extends CarreraUnidos {

    public void agregarAsignaturasEntidad(String nombreCarrera, LoginAD loginAD, AccesoDatos ad, SilaboAD silaboAD) throws SQLException {//unidades academicas de usuario
        this.setCodCarrera(silaboAD.getCodCarrera());
        this.setNombre(nombreCarrera);
        EstadosAD estadosAD = new EstadosAD();
        List<AsignaturaUnidos> materiasPensums = estadosAD.getMateriasPensum(loginAD, silaboAD.getPeriodo(), silaboAD.getCodCarrera());
        materiasPensums.stream().map((materiaPensum) -> {
            AsignaturasAD asignaturasAD = new AsignaturasAD();
            asignaturasAD.agregarAsignaturaEntidad(materiaPensum, silaboAD, ad);
            return asignaturasAD;
        }).forEachOrdered((asignaturasAD) -> {
            this.getAsignaturas().add(asignaturasAD);
        });
    }

    public void agregarCriterioReporteUsuario(CarreraUnidos carreraComun, AccesoDatos ad, String codPeriodo, LoginAD loginAD, EntidadAD entidadAD) throws SQLException {//reporte criterios de silabo
        EstadosAD estadosAD = new EstadosAD();
        List<AsignaturaUnidos> materiaPensums = estadosAD.getMateriasPensum(loginAD, codPeriodo, carreraComun.getCodCarrera());
        SilaboAD silabo = new SilaboAD();

        for (AsignaturaUnidos materia : materiaPensums) {
            Integer idSilabo = silabo.getIdSilaboBibliografias(materia.getCodMateria(), codPeriodo, ad);
            if (idSilabo == 0) {
            } else {
                switch (entidadAD.getTipo()) {
                    case "bibliografias":
                        agregarBibliografia(idSilabo, ad);
                        break;
                    case "criterios":
                        agregarCriteriosEvaluacion(idSilabo, ad);
                        break;
                    case "recursos":
                        agregarRecursos(idSilabo, ad);
                        break;
                    case "logros":
                        agregarLogros(idSilabo, ad);
                        break;
                    default:
                        break;
                }
            }
        }

    }

    public void agregarBibliografia(Integer idSilabo, AccesoDatos ad) throws SQLException {
        try {
            String SQL = "select * from t_bibliografia_libro\n"
                    + "where id_bibliografia = (select  id_bibliografia from t_seccion_bibliografia where tipo='BASICA' and id_silabo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsBibliografia = ps.executeQuery();
            while (rsBibliografia.next()) {
                String descripcion = bibliografiasAPAlibro(rsBibliografia.getString("autor"), rsBibliografia.getInt("anio"), rsBibliografia.getString("titulo"), rsBibliografia.getString("ciudad"), rsBibliografia.getString("editorial"));
                CriterioSilaboAD criterioSilaboAD = new CriterioSilaboAD();
                criterioSilaboAD.getcriterioSilabo(rsBibliografia.getInt("id_bibliografia"), descripcion, "", "");
                this.getCriteriosSilabo().add(criterioSilaboAD);
            }
            rsBibliografia.close();
            ad.getCon().commit();
            ps.close();
            SQL = "select * from t_bibliografia_sitio\n"
                    + "where id_bibliografia = (select  id_bibliografia from t_seccion_bibliografia where tipo='BASICA' and id_silabo=?)";
            ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            rsBibliografia = ps.executeQuery();
            while (rsBibliografia.next()) {
                String descripcion = bibliografiasAPASitio(rsBibliografia.getString("autor"), rsBibliografia.getInt("dia"), rsBibliografia.getInt("anio"), rsBibliografia.getString("mes"), rsBibliografia.getString("nombre_sitio"), rsBibliografia.getString("url"));
                CriterioSilaboAD criterioSilaboAD = new CriterioSilaboAD();
                criterioSilaboAD.getcriterioSilabo(rsBibliografia.getInt("id_bibliografia"), descripcion, "", "");
                this.getCriteriosSilabo().add(criterioSilaboAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
        }
    }

    public String bibliografiasAPASitio(String autor, Integer dia, Integer anio, String mes, String nombresitio, String url) {
        String result = "";
        String[] arrayAutor = autor.split(" ");
        result += arrayAutor[0] + " ";
        if (arrayAutor.length > 1) {
            result += arrayAutor[1].substring(0, 1);
        }
        result += ".(" + dia + " de " + mes + " de " + anio + ")." + nombresitio + "." + " Obtenido de " + url;
        return result;
    }

    public String bibliografiasAPAlibro(String autor, Integer anio, String titulo, String ciudad, String editorial) {
        String result = "";
        String[] arrayAutor = autor.split(" ");
        result += arrayAutor[0] + " ";
        if (arrayAutor.length > 1) {
            result += arrayAutor[1].substring(0, 1);
        }
        result += ".(" + anio + "). " + titulo + ". " + ciudad + ": " + editorial;
        return result;
    }

    private void agregarCriteriosEvaluacion(Integer idSilabo, AccesoDatos ad) throws SQLException {
        try {
            String SQL = "select distinct on(ta.id_actividades)ta.id_actividades, ta.descripcion\n"
                    + "from t_actividades AS ta\n"
                    + "JOIN t_actividades_aportes AS tap\n"
                    + "on tap.id_actividad = ta.id_actividades \n"
                    + "where tap.id_silabo= ?\n";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsCriterio = ps.executeQuery();
            while (rsCriterio.next()) {
                CriterioSilaboAD criterioSilaboAD = new CriterioSilaboAD();
                criterioSilaboAD.getcriterioSilabo(rsCriterio.getInt("id_actividades"), rsCriterio.getString("descripcion"), "", "");
                this.getCriteriosSilabo().add(criterioSilaboAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
        }
    }

    private void agregarRecursos(Integer idSilabo, AccesoDatos ad) throws SQLException {
        try {
            String SQL = "select tr.id_recursos,tr.descripcion \n"
                    + "from t_recursos AS tr  \n"
                    + "JOIN t_subseccion_recursos AS tsr\n"
                    + "on tsr.id_recurso = tr.id_recursos\n"
                    + "where id_silabo = ?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsCriterio = ps.executeQuery();
            while (rsCriterio.next()) {
                CriterioSilaboAD criterioSilaboAD = new CriterioSilaboAD();
                criterioSilaboAD.getcriterioSilabo(rsCriterio.getInt("id_recursos"), rsCriterio.getString("descripcion"), "", "");
                this.getCriteriosSilabo().add(criterioSilaboAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
        }
    }

    private void agregarLogros(Integer idSilabo, AccesoDatos ad) throws SQLException {
        try {
            String SQL = "select * from t_subseccion_logros \n"
                    + "where id_silabo = ?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idSilabo);
            ResultSet rsCriterio = ps.executeQuery();
            while (rsCriterio.next()) {
                CriterioSilaboAD criterioSilaboAD = new CriterioSilaboAD();
                criterioSilaboAD.getcriterioSilabo(rsCriterio.getInt("id_logro"), rsCriterio.getString("descripcion"), "", "");
                this.getCriteriosSilabo().add(criterioSilaboAD);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
        }
    }

    public void filtrarListaCriterios() {
        List<CriterioSilabo> respaldoFinal = new ArrayList<>();
        Map<String, CriterioSilabo> mapCriteriosSilabo = new HashMap<>(this.getCriteriosSilabo().size());
        this.getCriteriosSilabo().forEach((criterioSilabo) -> {
            mapCriteriosSilabo.put(criterioSilabo.getDescripcion(), criterioSilabo);
        });
        mapCriteriosSilabo.entrySet().stream().map((p) -> {
            Long numeroDeDescuentos = this.getCriteriosSilabo().stream()
                    .filter(crit -> crit.getDescripcion().equals(p.getValue().getDescripcion()))
                    .count();
            p.getValue().setCantidad(Integer.parseInt(numeroDeDescuentos.toString()));
            return p;
        }).forEachOrdered((p) -> {
            respaldoFinal.add(p.getValue());
        });
        this.getCriteriosSilabo().clear();
        this.setCriteriosSilabo(respaldoFinal);
    }

    void agregarCarreraCriteriosSilabo(CarreraUnidos carreraComun, AccesoDatos ad, LoginAD loginAD, String codPeriodo, EntidadAD entidadAD) throws SQLException {//reporte criterios de silabo
        this.setCodCarrera(carreraComun.getCodCarrera());
        this.setNombre(carreraComun.getNombre());
        agregarCriterioReporteUsuario(carreraComun, ad, codPeriodo, loginAD, entidadAD);
        filtrarListaCriterios();
    }

    public void asignarAsignaturasPeriodos(String codigoCarrera, String login, AccesoDatos ad, String periodoActual) throws SQLException {
        this.setCodCarrera(codigoCarrera);
        String SQL = "SELECT periodo from t_periodos_carreras where carrera ='EIS' AND periodo!=? and periodo!='null'\n"
                + "order by periodo desc ";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, periodoActual);
            ResultSet rsPeriodos = ps.executeQuery();
            while (rsPeriodos.next()) {
                PeriodoAD periodoad = new PeriodoAD();
                periodoad.agregarPeriodos(rsPeriodos.getString("periodo"));
                this.getPeriodos().add(periodoad);
            }
            if (!this.getPeriodos().isEmpty()) {
                Gson G = new Gson();
                CarreraAD carreraAD = asignaturasCarrera("{'codCarrera':'" + codigoCarrera + "'}");
                String jsonAsignaturas = G.toJson(carreraAD);
                CarreraUnidos carreraComun = G.fromJson(jsonAsignaturas, CarreraUnidos.class);
                if (carreraComun != null) {
                    this.setAsignaturas(carreraComun.getAsignaturas());
                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
        }
    }

    public void agregarFuncionSilabo(String codCarrera, String codPeriodo) {
        this.getCamposFormacion().forEach((campoFormacion) -> {
            campoFormacion.getAsignaturas().forEach((asignaturaComun) -> {
                asignaturaComun.setFncClick("descargarSilabo('" + codCarrera + "','" + asignaturaComun.getCodMateria() + "','" + codPeriodo + "');");
            });
        });
    }

    public void agregarAsignaturasReporte(AccesoDatos ad, LoginAD loginAD, String codPeriodo) {
        try {

            EstadosAD estadosAD = new EstadosAD();
            this.setEstadosSilabo(estadosAD.agregarAsignaturasReporteEstado(ad, loginAD, codPeriodo, this.getCodCarrera()));
        } catch (Exception e) {
        }
    }

    private static CarreraAD asignaturasCarrera(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCarrera(jsonCarrera);
    }

    public void RolUsuariosCargar(AccesoDatos ad, String codEntidad, Integer idEntidad, Integer idRol, String codPeriodo) {
        try {
            EntidadAD entidadAD = new EntidadAD();
            Gson G = new Gson();
            Integer idResult = 0;
            String json = "";
            if (null != idEntidad) {
                switch (idEntidad) {
                    case 2:

                        idResult = entidadAD.getIdEntidad(codEntidad, ad, 2);
                        unidos.sw.FacultadAD facultadAD1 = docentesFacultad("{'codFacultad':'" + codEntidad + "'}");
                        json = G.toJson(facultadAD1);
                        FacultadAD facultadAD = G.fromJson(json, FacultadAD.class);
                        if (!facultadAD.getDocentes().isEmpty()) {
                            this.setDocentes(facultadAD.getDocentes());
                        } else {
                            DocenteUnidos docenteUnidos = new DocenteUnidos();
                            docenteUnidos.setNombres("Nombre1");
                            docenteUnidos.setApellidos("Apellido1");
                            docenteUnidos.setCedula("000000");
                            this.getDocentes().add(docenteUnidos);
                        }
                        this.setIdCarrera(idResult);
                        break;
                    case 4:
                        idResult = entidadAD.getIdEntidad(codEntidad, ad, 4);
                        CarreraAD carreraAD1 = docentesCarrera("{'codCarrera':'" + codEntidad + "'}");
                        json = G.toJson(carreraAD1);
                        CarreraUnidos carreraUnidos = G.fromJson(json, CarreraUnidos.class);
                        this.setDocentes(carreraUnidos.getDocentes());
                        this.setIdCarrera(idResult);
                        break;
                    case 5:
                        String[] parts = codEntidad.split("\\."); // String array, each element is text between dots
                        codEntidad = parts[1];    // Text before the first dot
                        String codCampo = parts[0];
                        idResult = entidadAD.getIdEntidad(codEntidad, ad, 4);

                        CarreraAD carreraAD = docentesCamposFormacion("{'codCarrera':'" + codEntidad + "','codCampo':'" + codCampo + "'}");
                        json = G.toJson(carreraAD);
                        Integer idCampo = entidadAD.ingresarEntidad(codCampo, idResult, 5, ad, carreraAD.getCamposFormacion().get(0).getDescCampoF());
                        CarreraUnidos carreraUnidos2 = G.fromJson(json, CarreraUnidos.class);
                        this.setIdCarrera(idCampo);
                        this.setDocentes(carreraUnidos2.getCamposFormacion().get(0).getDocentes());
                        break;
                    default:
                        break;
                }
                asignarRolUsuarios(idRol, ad, codPeriodo);
            }
        } catch (Exception e) {
        }
    }

    private static CarreraAD docentesCamposFormacion(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.docentesCamposFormacion(jsonCarrera);
    }

    private void asignarRolUsuarios(Integer idRol, AccesoDatos ad, String codPeriodo) throws SQLException {
        String cedula2 = devolvercedulaUsuario(idRol, ad, codPeriodo);
        int cont = 0;
        if (!cedula2.equals("0")) {
            for (DocenteUnidos d : this.getDocentes()) {
                if (cedula2.equals(d.getCedula())) {
                    d.setSelected("selected");
                    cont++;
                    break;
                }
            }
            if (cont == 0) {
                DocenteUnidos d = obtenerInformacionUsuario(cedula2, ad);
                if (d != null) {
                    this.getDocentes().add(d);
                }
            }
        }

    }

    public String devolvercedulaUsuario(Integer idRol, AccesoDatos ad, String codPeriodo) {
        String result = "0";
        try {
            String SQL = "select td.cedula from  t_docente as td \n"
                    + "join t_usuario_entidad as tue\n"
                    + "on tue.id_usuario=td.id_docente\n"
                    + "where tue.id_rol=? and tue.id_entidad=? and id_periodo=(select id_periodo from t_periodo_academico WHERE codigo=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idRol);
            ps.setInt(2, this.getIdCarrera());
            ps.setString(3, codPeriodo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("cedula");
            }

            ad.getCon().commit();
            ps.close();
        } catch (Exception e) {
        }
        return result;
    }

    public boolean obtenerRolUsuario(Integer idRol, AccesoDatos ad, String cedula, Integer idEntidad) throws SQLException {
        boolean result = false;
        try {
            String SQL = "select id_usuario_entidad from t_usuario_entidad join t_docente\n"
                    + "on id_docente = id_usuario and cedula=?\n"
                    + "where id_entidad=? and id_rol=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, cedula);
            ps.setInt(2, idEntidad);
            ps.setInt(3, idRol);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = true;
            }

            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
        }
        return result;
    }

    private static CarreraAD docentesCarrera(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.docentesCarrera(jsonCarrera);
    }

    private static unidos.sw.FacultadAD docentesFacultad(java.lang.String jsonFacultad) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.docentesFacultad(jsonFacultad);
    }

    private DocenteUnidos obtenerInformacionUsuario(String cedula2, AccesoDatos ad) {
        DocenteUnidos docenteUnidos = null;
        try {
            String SQL = "select * from t_usuarios_informacion where id_cedula=(select id_docente from t_docente where cedula=? )";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, cedula2);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                docenteUnidos = new DocenteUnidos();
                docenteUnidos.setNombres(rs.getString("nombres"));
                docenteUnidos.setApellidos(rs.getString("apellidos"));
                docenteUnidos.setCedula(cedula2);
                docenteUnidos.setCorreo(rs.getString("email"));
                docenteUnidos.setSelected("selected");
            }

            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
        }
        return docenteUnidos;
    }

}
