/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.reportes.ad;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.ad.SilaboAD;
import dda.silabo.clases.unidos.CampoFormacionUnidos;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.clases.unidos.EntidadUnidos;
import dda.silabo.clases.unidos.EscuelaUnidos;
import dda.silabo.clases.unidos.FacultadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.CampoFormacionAD;
import dda.silabo.entidad.ad.CarrerasAD;
import dda.silabo.entidad.ad.EscuelasAD;
import dda.silabo.entidad.ln.EntidadLN;
import dda.silabo.login.ad.LoginAD;
import ec.edu.espoch.academico.ArrayOfEscuelaEntidad;
import ec.edu.espoch.academico.EscuelaEntidad;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import unidos.sw.CarreraAD;

/**
 *
 * @author Jorge Zaruma
 */
public class EntidadAD extends EntidadUnidos {

    public String getSQLSelect(String cedula, String codPeriodo, Integer idEntidad) {
        String SQL = " SELECT distinct(t_roles.id_rol),descripcion FROM t_docente INNER JOIN t_usuario_entidad ON t_docente.id_docente=t_usuario_entidad.id_usuario\n"
                + "                INNER JOIN t_periodo_academico ON t_periodo_academico.id_periodo=t_usuario_entidad.id_periodo\n"
                + "                INNER JOIN t_entidad ON t_usuario_entidad.id_entidad=t_entidad.id_entidad\n"
                + "                INNER JOIN t_roles ON t_usuario_entidad.id_rol=t_roles.id_rol\n"
                + "                WHERE cedula='" + cedula + "' AND t_periodo_academico.codigo='" + codPeriodo + "' AND t_entidad.id_entidad='" + idEntidad + "'";
        return SQL;
    }

    public Integer getIdEntidad(String espoch, AccesoDatos ad, Integer tipoEntidad) throws SQLException {
        Integer idResult = 0;
        try {
            String SQL = "select id_entidad from t_entidad where (codigo_entidad=? and id_tipo_entidad=?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, espoch);
            ps.setInt(2, tipoEntidad);
            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                idResult = rsEntidad.getInt("id_entidad");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return idResult;
    }

    public Integer ingresarEntidad(String codEntidad, Integer idPadre, Integer idTipoEntidad, AccesoDatos ad, String nombre) throws SQLException {
        Integer idResult = 0;
        String SQL = "select id_entidad from t_entidad where id_tipo_entidad=? and codigo_entidad=? and id_padre=?";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idTipoEntidad);
            ps.setString(2, codEntidad);
            ps.setInt(3, idPadre);
            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                idResult = rsEntidad.getInt("id_entidad");
            }
            ps.close();
            if (idResult == 0) {
                SQL = "insert into t_entidad (id_tipo_entidad,id_padre,codigo_entidad,nombre) values (?,?,?,?) returning id_entidad";
                ps = ad.getCon().prepareStatement(SQL);
                ps.setInt(1, idTipoEntidad);
                ps.setInt(2, idPadre);
                ps.setString(3, codEntidad);
                ps.setString(4, nombre);
                rsEntidad = ps.executeQuery();
                while (rsEntidad.next()) {
                    idResult = rsEntidad.getInt("id_entidad");
                }
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return idResult;
    }

    public void ingresarUsuarioInformacionEntidad(Integer idRol, Integer idPeriodo, Integer idEntidad, Integer idUsuario, AccesoDatos ad) throws SQLException {
        try {
            String SQL2 = "insert into t_usuario_entidad (id_usuario,id_rol,id_entidad,id_periodo) values (?,?,?,?)";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL2);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idRol);
            ps.setInt(3, idEntidad);
            ps.setInt(4, idPeriodo);
            ps.executeUpdate();
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public Integer verificarUsuarioEntidad(Integer idRol, Integer idEntidad, Integer idPeriodo, Integer idUsuario, AccesoDatos ad) throws SQLException {
        Integer idResult = 0;
        try {
            String SQL = "select id_usuario_entidad from t_usuario_entidad where id_rol=? and id_usuario =? and id_entidad=?  and id_periodo =? ";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idRol);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idEntidad);
            ps.setInt(4, idPeriodo);
            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                idResult = rsEntidad.getInt("id_usuario_entidad");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return idResult;
    }

    public String getEntidadesSQL(String cedula, String rolActivo, String codPeriodo) {
        return "select t_entidad.codigo_entidad,t_tipo_entidad.descripcion,t_tipo_entidad.id_tipo_entidad as id  from t_usuario_entidad\n"
                + "join t_entidad \n"
                + "on t_usuario_entidad.id_entidad = t_entidad.id_entidad\n"
                + "join t_tipo_entidad\n"
                + "on t_entidad.id_tipo_entidad = t_tipo_entidad.id_tipo_entidad\n"
                + "join t_roles\n"
                + "on t_roles.id_rol = t_usuario_entidad.id_rol\n"
                + "join \n"
                + "t_periodo_academico \n"
                + "on t_periodo_academico.id_periodo = t_usuario_entidad.id_periodo and t_periodo_academico.codigo='" + codPeriodo + "'\n"
                + "join\n"
                + "t_docente\n"
                + "on t_docente.id_docente = t_usuario_entidad.id_usuario and t_docente.cedula='" + cedula + "' \n"
                + "where t_roles.rol_char = '" + rolActivo + "'";
    }

    public void agregarEntidad(ResultSet rsEntidad, String rolActivo, AccesoDatos ad, String cedula) throws SQLException {
        if (!rolActivo.equals("Cor")) {
            this.setCodigo(rsEntidad.getString("codigo_entidad"));
            this.setNombre(rsEntidad.getString("descripcion"));
            this.setId(rsEntidad.getString("id"));
            this.setTipo("todos");
        } else {
            CampoFormacionAD campoFormacionAD = new CampoFormacionAD();
            String codCarrera = campoFormacionAD.getCarreraCampo(rsEntidad.getString("codigo_entidad"), ad, cedula);
            this.setCodigo(codCarrera);
            this.setTipo(rsEntidad.getString("codigo_entidad"));
            this.setNombre("Carreras");
        }
    }

    public EntidadUnidos agregarEntidadesUsuarioRol(List<EntidadUnidos> entidadesTrabajo) {//unidades academicas de usuario
        Gson G = new Gson();
        EntidadUnidos result = new EntidadUnidos();
        try {
            String jsonEntidad = unidadesAcademicasInstitucion(null);
            EntidadUnidos entidadesAcademicas = G.fromJson(jsonEntidad, EntidadUnidos.class);
            if (entidadesAcademicas != null) {
                OUTER:
                for (EntidadUnidos entidadComun : entidadesTrabajo) {
                    switch (entidadComun.getNombre()) {
                        case "Institucion":
                            result = entidadesAcademicas;
                            break OUTER;
                        case "Facultades":
                            FacultadUnidos facultadComun = entidadesAcademicas.getFacultades().stream().filter(facultad -> facultad.getCodFacultad().equals(entidadComun.getCodigo())).findFirst().orElse(null);
                            if (facultadComun != null) {
                                result.getFacultades().add(facultadComun);
                            }
                            break;
                        case "Escuelas": {
                            List<FacultadUnidos> nuevaFacultad = obtenerArbolEscuela(entidadesAcademicas.getFacultades(), entidadComun.getCodigo());
                            result.setFacultades(nuevaFacultad);
                            break OUTER;
                        }
                        case "Carreras": {
//                        List<FacultadUnidos> facultadFiltrada = obtenerFacultadFiltrada(entidadComun, entidadesAcademicas.getFacultades());
                            List<FacultadUnidos> nuevaFacultad = obtenerArbolCarreras(entidadesTrabajo, entidadesAcademicas.getFacultades());
                            result.setFacultades(nuevaFacultad);
                            break OUTER;
                        }
                        default:
                            break;
                    }
                }

            }
        } catch (JsonSyntaxException e) {
            return null;
        }
        return result;
    }

    public void agregarEntidadesUsuario(List<FacultadUnidos> facultades, LoginAD loginAD, String codPeriodo, AccesoDatos ad) {//unidades academicas de usuario
        SilaboAD silaboAD = new SilaboAD();
        silaboAD.setPeriodo(codPeriodo);
        facultades.stream().map((facultadComun) -> {
            facultadComun.getEscuelas().forEach((escuelaComun) -> {
                escuelaComun.getCarreras().forEach((carreraComun) -> {
                    CarrerasAD carrerasAD = new CarrerasAD();
                    silaboAD.setCodCarrera(carreraComun.getCodCarrera());
                    try {
                        carrerasAD.agregarAsignaturasEntidad(carreraComun.getNombre(), loginAD, ad, silaboAD);
                    } catch (SQLException ex) {
                        Logger.getLogger(EntidadAD.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (!carrerasAD.getAsignaturas().isEmpty()) {
                        carreraComun.getCamposFormacion().clear();
                        carreraComun.getCriteriosSilabo().clear();
                        carreraComun.getAsignaturas().clear();
                        carreraComun.setAsignaturas(carrerasAD.getAsignaturas());
                    }
                });
            });
            return facultadComun;
        }).forEachOrdered((_item) -> {
            this.setFacultades(facultades);
        });

    }

    private List<FacultadUnidos> obtenerArbolEscuela(List<FacultadUnidos> facultades, String codEntidad) {
        List<FacultadUnidos> result = new ArrayList<>();
        int cont = 0;
        for (FacultadUnidos facultadComun : facultades) {
            EscuelaUnidos escuelaComun = facultadComun.getEscuelas().stream().filter(escuela -> escuela.getCodEscuela().equals(codEntidad)).findFirst().orElse(null);
            if (escuelaComun != null) {
                FacultadAD facultadAD = new FacultadAD();
                facultadAD.setCodFacultad(facultadComun.getCodFacultad());
                facultadAD.setNombre(facultadComun.getNombre());
                facultadAD.getEscuelas().add(escuelaComun);
                result.add(facultadAD);
                cont++;
                break;
            }
            if (cont > 0) {
                break;
            }

        }
        return result;
    }

    public List<FacultadUnidos> obtenerArbolCarreras(List<EntidadUnidos> entidadesTrabajo, List<FacultadUnidos> facultades) {
        List<FacultadUnidos> resultFacultad = new ArrayList<>();

        int contC = entidadesTrabajo.size();
        for (FacultadUnidos facultadComun : facultades) {
            int contF = 0;
            if (contC == 0) {
                break;
            }
            List<EscuelaUnidos> resultEscuela = new ArrayList<>();
            for (EscuelaUnidos escuelaComun : facultadComun.getEscuelas()) {
                int contE = 0;
                if (contC == 0) {
                    break;
                }
                List<CarreraUnidos> resultCarrera = new ArrayList<>();
                for (EntidadUnidos entidadComun : entidadesTrabajo) {
                    CarreraUnidos carreraComun = escuelaComun.getCarreras().stream().filter(carrera -> carrera.getCodCarrera().equals(entidadComun.getCodigo())).findFirst().orElse(null);
                    if (carreraComun != null) {
                        carreraComun.getAsignaturas().clear();
                        carreraComun.getCamposFormacion().clear();
                        resultCarrera.add(carreraComun);
                        contE++;
                        contF++;
                        contC--;
                    }
                }
                if (contE > 0) {
                    EscuelasAD escuelasAD = new EscuelasAD();
                    escuelasAD.setCodEscuela(escuelaComun.getCodEscuela());
                    escuelasAD.setNombre(escuelaComun.getNombre());
                    escuelasAD.setCarreras(resultCarrera);
                    resultEscuela.add(escuelasAD);
                }
            }
            if (contF > 0) {
                FacultadAD facultadAD = new FacultadAD();
                facultadAD.setCodFacultad(facultadComun.getCodFacultad());
                facultadAD.setNombre(facultadComun.getNombre());
                facultadAD.setEscuelas(resultEscuela);
                resultFacultad.add(facultadAD);
            }
        }
        return resultFacultad;
    }

    private List<FacultadUnidos> obtenerFacultadFiltrada(EntidadUnidos entidadComun, List<FacultadUnidos> facultades) {
        List<FacultadUnidos> result = new ArrayList<>();
        Gson G = new Gson();
        ArrayOfEscuelaEntidad escuelasEntidad = arrayOfEscuelaEntidad();
        String jsoNFacultades = facultadesLista(null);
        EntidadUnidos entidadUnidos = G.fromJson(jsoNFacultades, EntidadUnidos.class);
        if (entidadComun.getNombre().equals("Carreras")) {
            EscuelaEntidad escuelaEntidad1 = escuelasEntidad.getEscuelaEntidad().stream().filter(escuela -> escuela.getCodCarrera().equals(entidadComun.getCodigo())).findFirst().orElse(null);
            if (escuelaEntidad1 != null) {

                FacultadUnidos facultad = entidadUnidos.getFacultades().stream().filter(fa -> fa.getNombre().equals(escuelaEntidad1.getFacultad())).findFirst().orElse(null);
                FacultadUnidos facultadComun = facultades.stream().filter(facultadAD -> facultadAD.getCodFacultad().equals(facultad.getCodFacultad())).findFirst().orElse(null);
                result.add(facultadComun);
            }

        } else {
            EscuelaEntidad escuelaEntidad1 = escuelasEntidad.getEscuelaEntidad().stream().filter(escuela -> escuela.getCodEscuela().equals(entidadComun.getCodigo())).findFirst().orElse(null);
            FacultadUnidos facultad = entidadUnidos.getFacultades().stream().filter(fa -> fa.getNombre().equals(escuelaEntidad1.getFacultad())).findFirst().orElse(null);
            FacultadUnidos facultadComun = facultades.stream().filter(facultadAD -> facultadAD.getCodFacultad().equals(facultad.getCodFacultad())).findFirst().orElse(null);
            result.add(facultadComun);
        }
        return result;
    }

    public void agregarCamposFormacionCarrera(String codCarrera, AccesoDatos ad, Integer idPadre) throws SQLException {
        CampoFormacionAD campoFormacionAD = new CampoFormacionAD();
        Integer numeroCammposFormacion = campoFormacionAD.getSQLCamposFormacion(ad, codCarrera, 4);
        if (numeroCammposFormacion == 0) {
            Gson G = new Gson();
            unidos.sw.CarreraAD camposFormacionCarrera = asignaturasCamposFormacion("{'codCarrera':'" + codCarrera + "'}");
            String jsonCamposFormacion = G.toJson(camposFormacionCarrera);
            CarreraUnidos carreraComun2 = G.fromJson(jsonCamposFormacion, CarreraUnidos.class);
            carreraComun2.getCamposFormacion().forEach((campoFormacion) -> {
                try {
                    ingresarEntidad(campoFormacion.getCodCampoF(), idPadre, 5, ad, campoFormacion.getDescCampoF());
                } catch (SQLException ex) {
                    Logger.getLogger(EntidadAD.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public void getEntidadTrabajo(String SQL, AccesoDatos ad) throws SQLException {
        if (ad.Connectar() != 0) {
            try {
                PreparedStatement ps = ad.getCon().prepareStatement(SQL);
                ResultSet rsEntidad = ps.executeQuery();
                while (rsEntidad.next()) {
                    this.setCodigo(rsEntidad.getString("codigo_entidad"));
                    this.setTipo(rsEntidad.getString("descripcion"));
                    this.setNombre(rsEntidad.getString("nombre"));

                }
                ad.getCon().commit();
                ps.close();
            } catch (SQLException e) {
                ad.getCon().rollback();
                ad.Desconectar();
            }
        }
    }

    public void agregarEntidadRol(ResultSet entidades) {
        try {
            Integer idEntidad = entidades.getInt("id_entidad");
            this.setId(idEntidad.toString());
            this.setCodigo(entidades.getString("codigo_entidad"));
            this.setNombre(entidades.getString("nombre"));
            this.setTipo(entidades.getString("codigopadre"));
            if (entidades.getInt("id_rol") != 0) {
                this.setFncClick("selected");
            }
        } catch (Exception e) {

        }
    }

    private static ArrayOfEscuelaEntidad arrayOfEscuelaEntidad() {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.arrayOfEscuelaEntidad();
    }

    private static String unidadesAcademicasInstitucion(java.lang.String jsonDatos) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.unidadesAcademicasInstitucion(jsonDatos);
    }

    private static String facultadesLista(java.lang.String jsonEntidad) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.facultadesLista(jsonEntidad);
    }

    private static CarreraAD asignaturasCamposFormacion(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCamposFormacion(jsonCarrera);
    }

    public Integer obteneridEntidad(String codCarrera, AccesoDatos ad) throws SQLException {
        Integer idResult = 0;
        try {
            String SQL = "SELECT id_entidad\n"
                    + "  FROM t_entidad where codigo_entidad=? AND id_tipo_entidad=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, codCarrera);
            ps.setInt(2, 4);
            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                idResult = rsEntidad.getInt("id_entidad");
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return idResult;
    }

    public void agregarEntidadesRol(List<EntidadUnidos> entidadesTrabajo, Integer idTipoEntidad, AccesoDatos ad) {
        try {
            Gson G = new Gson();
            String jsonFcultades = "";
            EntidadUnidos entidadUnidos = null;

            if (entidadesTrabajo.size() > 0) {
                switch (entidadesTrabajo.get(0).getNombre()) {
                    case "Institucion":
                        jsonFcultades = facultadesLista(null);
                        entidadUnidos = G.fromJson(jsonFcultades, EntidadUnidos.class);
                        this.setFacultades(entidadUnidos.getFacultades());
                        if (idTipoEntidad == 2) {
                            agregarClickUsuarios("mostrarDocentes(this," + idTipoEntidad + ");");
                        } else {
                            agregarClickUsuarios("mostrarSiguienteNivel(this," + idTipoEntidad + ",'Carreras');");
                        }
                        break;
                    case "Facultad":
                        jsonFcultades = facultadesLista(null);
                        entidadUnidos = G.fromJson(jsonFcultades, EntidadUnidos.class);
                        List<FacultadUnidos> facultadUnidos = entidadUnidos.getFacultades().stream().filter(fa -> fa.getCodFacultad().equals(entidadesTrabajo.get(0).getCodigo())).collect(Collectors.toList());
                        this.setFacultades(facultadUnidos);
                        if (idTipoEntidad == 2) {
                            agregarClickUsuarios("mostrarDocentes(this," + idTipoEntidad + ");");
                        } else {
                            agregarClickUsuarios("mostrarSiguienteNivel(this," + idTipoEntidad + ",'Carreras');");
                        }
                        break;
                    case "Carrera":
                        EntidadUnidos entidadesComunesUsuario = agregarEntidadesUsuarioRol(entidadesTrabajo);
                        this.setFacultades(entidadesComunesUsuario.getFacultades());
                        agregarClickUsuarios("mostrarSiguienteNivel(this," + idTipoEntidad + ",'Carreras');");
                        break;
                    default:
                        break;
                }
                agregarFacultadesID(ad);
            }
        } catch (Exception e) {
        }
    }

    private static CarreraAD arbolCarrera(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.arbolCarrera(jsonCarrera);
    }

    private void agregarClickUsuarios(String mostrarDocentesthis) {
        for (FacultadUnidos facultadUnidos : this.getFacultades()) {
            facultadUnidos.setFncClick(mostrarDocentesthis);
        }
    }

    public void agregarEntidadeCarreraRol(List<EntidadUnidos> entidadesTrabajo, String codEntidad, Integer idTipoEntidad, String nivel, AccesoDatos ad, Integer idRol, String cedula) {
        try {
            Gson G = new Gson();
            String jsonFcultades = "";
            if (entidadesTrabajo.size() > 0) {
                if (entidadesTrabajo.get(0).getNombre().equals("Institucion") || entidadesTrabajo.get(0).getNombre().equals("Facultad")) {
                    if (nivel.equals("Carreras")) {
                        jsonFcultades = arbolFacultad("{'codFacultad':'" + codEntidad + "'}");
                        FacultadUnidos facultadUnidos = G.fromJson(jsonFcultades, FacultadUnidos.class);
                        this.getFacultades().add(facultadUnidos);
                        if (idTipoEntidad == 4) {
                            agregarClickUsuariosCarrera("mostrarDocentes(this," + idTipoEntidad + ");");
                        } else {
                            agregarClickUsuariosCarrera("mostrarSiguienteNivel(this," + idTipoEntidad + ",'Campos');");
                        }
                    } else if (nivel.equals("Campos")) {
                        CarreraAD cad = arbolCarrera("{'codCarrera':'" + codEntidad + "'}");
                        String json = G.toJson(cad);
                        CarreraUnidos carreraUnidos = G.fromJson(json, CarreraUnidos.class);
                        agregarCamposEntidades(carreraUnidos, ad);
                    }
                } else if (entidadesTrabajo.get(0).getNombre().equals("Carrera")) {
                    if (nivel.equals("Carreras")) {
                        EntidadUnidos entidadesComunesUsuario = agregarEntidadesUsuarioRol(entidadesTrabajo);
                        this.setFacultades(entidadesComunesUsuario.getFacultades());
                        agregarClickUsuariosCarrera("mostrarSiguienteNivel(this," + idTipoEntidad + ",'Campos');");
                    } else if (nivel.equals("Campos")) {
                        CarreraAD cad = arbolCarrera("{'codCarrera':'" + codEntidad + "'}");
                        String json = G.toJson(cad);
                        CarreraUnidos carreraUnidos = G.fromJson(json, CarreraUnidos.class);
                        agregarCamposEntidades(carreraUnidos, ad);
                    }

                }
                if (!nivel.equals("Campos")) {
                    agregarCarrerasID(idRol, cedula, ad);
                }
            }
        } catch (Exception e) {
        }
    }

    private static String arbolFacultad(java.lang.String jsonFacultad) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.arbolFacultad(jsonFacultad);
    }

    private void agregarClickUsuariosCarrera(String mostrarDocentesthis) {
        for (FacultadUnidos facultadUnidos : this.getFacultades()) {
            for (EscuelaUnidos e : facultadUnidos.getEscuelas()) {
                for (CarreraUnidos c : e.getCarreras()) {
                    c.setFncClick(mostrarDocentesthis);
                }
            }

        }
    }

    private void agregarCamposEntidades(CarreraUnidos carreraUnidos, AccesoDatos ad) throws SQLException {
        FacultadAD facultadAD = new FacultadAD();
        EscuelasAD escuelasAD = new EscuelasAD();
        Integer idEntidadResult = getIdEntidad(carreraUnidos.getCodCarrera(), ad, 4);
        for (CampoFormacionUnidos cf : carreraUnidos.getCamposFormacion()) {
            CarreraUnidos carreraUnidos1 = new CarreraUnidos();
            carreraUnidos1.setNombre(cf.getDescCampoF());
            carreraUnidos1.setCodCarrera(cf.getCodCampoF());
            Integer idCampo = ingresarEntidad(cf.getCodCampoF(), idEntidadResult, 5, ad, cf.getDescCampoF());
            carreraUnidos1.setIdCarrera(idCampo);
            carreraUnidos1.setFncClick("mostrarDocentes(this," + 5 + ");");
            escuelasAD.getCarreras().add(carreraUnidos1);

        }

        facultadAD.getEscuelas().add(escuelasAD);
        this.getFacultades().add(facultadAD);

    }

    private void agregarFacultadesID(AccesoDatos ad) {
        EntidadLN entidadLN = new EntidadLN();
        Integer idEntidad = entidadLN.ingresarEntidad(ad);
        if (idEntidad != 0) {
            for (FacultadUnidos facultadComun : this.getFacultades()) {
                Integer idFacultad = entidadLN.ingresarIdFacultad(facultadComun.getCodFacultad(), facultadComun.getNombre(), idEntidad, ad);
                facultadComun.setIdFacultad(idFacultad);
            }
        }
    }

    private void agregarCarrerasID(Integer idRol, String cedula, AccesoDatos ad) throws SQLException {
        EntidadLN entidadLN = new EntidadLN();
        Integer idEntidad = entidadLN.ingresarEntidad(ad);
        if (idEntidad != 0) {
            for (FacultadUnidos facultadComun : this.getFacultades()) {
                Integer idFacultad = entidadLN.ingresarIdFacultad(facultadComun.getCodFacultad(), facultadComun.getNombre(), idEntidad, ad);
                for (EscuelaUnidos escuelaComun : facultadComun.getEscuelas()) {
                    Integer idEscuela = entidadLN.ingresarIdEscuela(idFacultad, escuelaComun.getCodEscuela(), escuelaComun.getNombre(), ad);
                    for (CarreraUnidos carreraComun : escuelaComun.getCarreras()) {
                        Integer idEntidadResult = entidadLN.ingresarIdCarrera(idEscuela, carreraComun.getCodCarrera(), carreraComun.getNombre(), ad);
                        carreraComun.setIdCarrera(idEntidadResult);
                        if (idRol != null && cedula != null) {
                            CarrerasAD carreraAD = new CarrerasAD();
                            if (carreraAD.obtenerRolUsuario(idRol, ad, cedula, idEntidadResult)) {
                                carreraComun.setSelected("selected");
                            }
                        }
                    }
                    escuelaComun.setIdEscuela(idEscuela);
                }
                facultadComun.setIdFacultad(idFacultad);
            }
        }
    }

}
