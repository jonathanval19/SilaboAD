/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.reportes.ad;

import com.google.gson.Gson;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CampoFormacionUnidos;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.clases.unidos.DocenteUnidos;
import dda.silabo.clases.unidos.EscuelaUnidos;
import dda.silabo.clases.unidos.FacultadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.CampoFormacionAD;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.reportes.comunes.Elemento;
import dda.silabo.reportes.comunes.ReporteGrafico;
import ec.edu.espoch.academico.ArrayOfMateria;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import redis.clients.jedis.Jedis;
import unidos.sw.CarreraAD;
import unidos.sw.Docente;

/**
 *
 * @author Jorge Zaruma
 */
public class ReporteGraficoAD extends ReporteGrafico {

    public void agregarInformacionEntidad(List<FacultadUnidos> facultadesUsuario, AccesoDatos ad, LoginAD loginAD, String codPeriodo, Jedis jedis, EntidadAD entidadAD) throws SQLException {//reporte unidades academicas usuario
        switch (entidadAD.getTipo()) {
            case "todos":
                if (facultadesUsuario.size() > 1) {//facultades de la institucion
                    this.setTitulo("Escuela Superior Politécnica de Chimborazo");
                    this.setSubtitulo("FACULTADES");
                    this.setAyuda("ESTADOS DE SILABOS POR FACULTAD");
                    entidadInstitucion(facultadesUsuario, ad, loginAD, codPeriodo);
                } else {
                    entidadFacultad(facultadesUsuario, ad, codPeriodo, loginAD);//escuelas de la facultad
                }
                break;
            case "facultad":
                this.setSubtitulo("CARRERAS");
                this.setAyuda("ESTADOS DE SILABOS POR CARRERA");
                FacultadUnidos fu = facultadesUsuario.stream().filter(fa -> fa.getCodFacultad().equals(entidadAD.getCodigo())).findFirst().get();
                for (EscuelaUnidos escuelaComun2 : fu.getEscuelas()) {
                    this.setTitulo(escuelaComun2.getNombre());
                    entidadEscuela(escuelaComun2, ad, codPeriodo, loginAD);
                }
                break;
            case "escuela":
                this.setSubtitulo("CARRERAS");
                this.setAyuda("ESTADOS DE SILABOS POR CARRERA");
                facultadesUsuario.stream().map((facultadComun) -> facultadComun.getEscuelas().stream().filter(escuela -> escuela.getCodEscuela().equals(entidadAD.getCodigo()) && entidadAD.getTipo().equals("escuela")).findFirst().orElse(null)).filter((escuelaComun2) -> (escuelaComun2 != null)).forEachOrdered((escuelaComun2) -> {
                    this.setTitulo(escuelaComun2.getNombre());
                    entidadEscuela(escuelaComun2, ad, codPeriodo, loginAD);
                });
                break;
            case "carrera":
                this.setSubtitulo("CAMPOS DE FORMACIÓN");
                this.setAyuda("ESTADOS DE SILABOS POR CAMPO DE FORMACIÓN");
                entidadCarrera(loginAD, codPeriodo, jedis, ad, entidadAD);
                break;
            case "campoformacion":
                this.setAyuda("ESTADOS DE SILABOS POR DOCENTE");
                CampoFormacionAD campoFormacionAD = new CampoFormacionAD();
                String descripcionCampoFormacion = obtenerNombreCampoFormacionCarrera(entidadAD.getNombre(), entidadAD.getCodigo(), ad);
                this.setTitulo(descripcionCampoFormacion);
                if (loginAD.getRolActivo().equals("Doc")) {
                    DocenteUnidos datoDocente = obtenerInformacionDocente(loginAD);
                    List< AsignaturaUnidos> materias = campoFormacionAD.obtenerMateriasPensumDocente(datoDocente.getCedula(), entidadAD.getCodigo(), codPeriodo);
                    materias = materias.stream().filter(m -> m.getCodArea().equals(entidadAD.getNombre())).collect(Collectors.toList());
                    if (!materias.isEmpty()) {
                        entidadCampoFormacionUnidos2(datoDocente, ad, entidadAD.getCodigo(), codPeriodo, materias, entidadAD.getNombre());
                    }
                } else {
                    String jsonCarreraCampos = "{'codCarrera':'" + entidadAD.getCodigo() + "','codCampo':'" + entidadAD.getNombre() + "'}";
                    CarreraAD carreraAD = docentesCamposFormacion(jsonCarreraCampos);
                    carreraAD.getCamposFormacion().get(0).getDocentes().forEach((datoDocente) -> {
                        loginAD.setCedula(datoDocente.getCedula());
                        List<AsignaturaUnidos> materias = campoFormacionAD.obtenerMateriasPensumDocente(datoDocente.getCedula(), entidadAD.getCodigo(), codPeriodo);
                        materias = materias.stream().filter(m -> m.getCodArea().equals(entidadAD.getNombre())).collect(Collectors.toList());
                        if (!materias.isEmpty()) {
                            entidadCampoFormacionUnidos(datoDocente, ad, entidadAD.getCodigo(), codPeriodo, materias, entidadAD.getNombre());
                        }
                    });
                }
                break;
            default:
                entidadFacultad(facultadesUsuario, ad, codPeriodo, loginAD);//escuelas de la facultad
                break;
        }
    }

    private void entidadInstitucion(List<FacultadUnidos> facultadesUsuario, AccesoDatos ad, LoginAD loginAD, String codPeriodo) {//reporte unidades academicas usuario
        facultadesUsuario.forEach((facultadComun) -> {
            try {
                Elemento elementoAD = new Elemento();
                elementoAD.setCodigo(facultadComun.getCodFacultad());
                elementoAD.setNombre(facultadComun.getNombre());
                elementoAD.setFncClick("generarDetallePdfEntidad('" + facultadComun.getCodFacultad() + "','','facultad',this.id,'','" + this.getAvance() + "')");
                DatosElementoAD datosElemento = new DatosElementoAD();
                datosElemento.agregarEstadosFacultad(facultadComun.getEscuelas(), ad, codPeriodo, loginAD);
                elementoAD.setDatos(datosElemento);
                this.getElementos().add(elementoAD);
            } catch (Exception e) {

            }
        });
    }

    private void entidadFacultad(List<FacultadUnidos> facultadesUsuario, AccesoDatos ad, String codPeriodo, LoginAD loginAD) {
        facultadesUsuario.forEach((facultadComun) -> {
            if (facultadComun.getEscuelas().size() > 1) {
                this.setTitulo(facultadComun.getNombre());
                this.setAyuda("ESTADOS DE SILABOS POR ESCUELA");
                this.setSubtitulo("ESCUELAS");
                facultadComun.getEscuelas().forEach((escuelaComun) -> {
                    Elemento elementoAD = new Elemento();
                    elementoAD.setCodigo(escuelaComun.getCodEscuela());
                    elementoAD.setNombre(escuelaComun.getNombre());
                    elementoAD.setFncClick("generarDetallePdfEntidad('" + escuelaComun.getCodEscuela() + "','','escuela',this.id,'','" + this.getAvance() + "')");
                    DatosElementoAD datosElemento = new DatosElementoAD();
                    datosElemento.agregarEstadoEscuela(escuelaComun, ad, codPeriodo, loginAD);
                    elementoAD.setDatos(datosElemento);
                    this.getElementos().add(elementoAD);
                });
            } else {
                this.setSubtitulo("CARRERAS");
                this.setAyuda("ESTADOS DE SILABOS POR CARRERA");
                entidadEscuela(facultadComun.getEscuelas().get(0), ad, codPeriodo, loginAD);
            }
        });

    }

    private void entidadEscuela(EscuelaUnidos escuelaComun2, AccesoDatos ad, String codPeriodo, LoginAD loginAD) {//reporte unidades academicas usuario
        this.setTitulo(escuelaComun2.getNombre());
        Gson G = new Gson();
        escuelaComun2.getCarreras().stream().map((carreraComun) -> {
            try {
                agregarCamposFormacionCarrera(carreraComun.getCodCarrera(), ad);
            } catch (SQLException ex) {
                Logger.getLogger(ReporteGraficoAD.class.getName()).log(Level.SEVERE, null, ex);
            }
            return carreraComun;
        }).map((carreraComun) -> {
            Elemento elementoAD = new Elemento();
            elementoAD.setCodigo(carreraComun.getCodCarrera());
            elementoAD.setNombre(carreraComun.getNombre());
            elementoAD.setFncClick("generarDetallePdfEntidad('" + carreraComun.getCodCarrera() + "','','carrera',this.id,'','" + this.getAvance() + "')");
            DatosElementoAD datosElemento = new DatosElementoAD();
            try {
                datosElemento.agregarEstadosCarrera(carreraComun, ad, codPeriodo, loginAD);
            } catch (SQLException ex) {
                Logger.getLogger(ReporteGraficoAD.class.getName()).log(Level.SEVERE, null, ex);
            }
            elementoAD.setDatos(datosElemento);
            return elementoAD;
        }).forEachOrdered((entidadAD) -> {
            this.getElementos().add(entidadAD);
        });
    }

    private void entidadCarrera(LoginAD loginAD, String codPeriodo, Jedis jedis, AccesoDatos ad, EntidadAD entidadAD2) throws SQLException {//reporte unidades academicas usuario
        Integer cantidadM = -1;
        Gson G = new Gson();
        if (loginAD.getRolActivo().equals("Doc")) {
            ArrayOfMateria materias = getMateriasDocente(entidadAD2.getCodigo(), loginAD.getCedula(), codPeriodo);
            cantidadM = materias.getMateria().size();
            jedis.set(loginAD.getCedula() + loginAD.getRolActivo(), cantidadM.toString());
            jedis.set("Materias" + loginAD.getCedula() + loginAD.getRolActivo(), G.toJson(materias));
        } else {
            jedis.set(loginAD.getCedula() + loginAD.getRolActivo(), cantidadM.toString());
        }
        String codCarrera = entidadAD2.getCodigo();
        this.setTitulo(obtenerNombreCarrera(entidadAD2.getCodigo(), ad));

        CampoFormacionAD campoFormacionAD = new CampoFormacionAD();
        String codCampoFormacionUnidos = campoFormacionAD.getCampoCarreraActual(ad, loginAD.getCedula(), codCarrera, codPeriodo);
        if (codCampoFormacionUnidos.equals("todos") && !loginAD.getRolActivo().equals("Doc")) {
            String jsonCarreraCampos = "{'codCarrera':'" + codCarrera + "'}";
            unidos.sw.CarreraAD camposFormacionCarrera = asignaturasCamposFormacion(jsonCarreraCampos);
            String jsonCamposFormacion = G.toJson(camposFormacionCarrera);
            CarreraUnidos carreraComun2 = G.fromJson(jsonCamposFormacion, CarreraUnidos.class);
            agregarInformacionCampos(carreraComun2.getCamposFormacion(), codCarrera, codPeriodo, ad);
        } else if (!loginAD.getRolActivo().equals("Doc") && !codCampoFormacionUnidos.equals("todos")) {
            Elemento elementoAD = new Elemento();
            String descripcionCampoFormacion = obtenerNombreCampoFormacionCarrera(codCampoFormacionUnidos, codCarrera, ad);
            elementoAD.setCodigo(codCampoFormacionUnidos);
            elementoAD.setNombre(descripcionCampoFormacion);
            elementoAD.setFncClick("generarDetallePdfEntidad('" + codCarrera + "','" + codCampoFormacionUnidos + "','campoformacion',this.id,'','" + this.getAvance() + "')");
            DatosElementoAD datosElemento = new DatosElementoAD();
            String jsonCarreraCampos = "{'codCarrera':'" + codCarrera + "'}";
            unidos.sw.CarreraAD camposFormacionCarrera = asignaturasCamposFormacion(jsonCarreraCampos);
            String jsonCamposFormacion = G.toJson(camposFormacionCarrera);
            CarreraUnidos carreraComun2 = G.fromJson(jsonCamposFormacion, CarreraUnidos.class);
            CampoFormacionUnidos campoFormacion = carreraComun2.getCamposFormacion().stream().filter(cf -> cf.getCodCampoF().equals(codCampoFormacionUnidos)).findFirst().orElse(null);
            datosElemento.agregarEstadosMaterias(campoFormacion.getAsignaturas(), ad, codPeriodo, codCarrera);
            elementoAD.setDatos(datosElemento);
            this.getElementos().add(elementoAD);
        } else if (loginAD.getRolActivo().equals("Doc")) {
            List< AsignaturaUnidos> materias = campoFormacionAD.obtenerMateriasPensumDocente(loginAD.getCedula(), codCarrera, codPeriodo);
            List<CampoFormacionUnidos> campoFormacionUnidoses = campoFormacionAD.obtenerCamposFormacionDocente(materias, codCarrera);
            agregarInformacionCampos(campoFormacionUnidoses, codCarrera, codPeriodo, ad);
        }

    }

    public void agregarInformacionCampos(List<CampoFormacionUnidos> campos, String codCarrera, String codPeriodo, AccesoDatos ad) throws SQLException {
        for (CampoFormacionUnidos campoFormacion : campos) {
            Elemento entidadAD = new Elemento();
            entidadAD.setCodigo(campoFormacion.getCodCampoF());
            String descripcionCampoFormacion = obtenerNombreCampoFormacionCarrera(campoFormacion.getCodCampoF(), codCarrera, ad);
            entidadAD.setNombre(descripcionCampoFormacion);
            entidadAD.setFncClick("generarDetallePdfEntidad('" + codCarrera + "','" + campoFormacion.getCodCampoF() + "','campoformacion',this.id,'','" + this.getAvance() + "')");
            DatosElementoAD datosElemento = new DatosElementoAD();
            datosElemento.agregarEstadosMaterias(campoFormacion.getAsignaturas(), ad, codPeriodo, codCarrera);
            entidadAD.setDatos(datosElemento);
            this.getElementos().add(entidadAD);
        }
    }

    private void entidadCampoFormacionUnidos(Docente datoDocente, AccesoDatos ad, String codCarrera, String codPeriodo, List<AsignaturaUnidos> materias, String codCampo) {//reporte unidades academicas usuario
        this.setSubtitulo("DOCENTES");
        Elemento entidadAD = new Elemento();
        entidadAD.setCodigo(datoDocente.getCedula());
        entidadAD.setNombre(datoDocente.getNombres() + " " + datoDocente.getApellidos());
        entidadAD.setFncClick("generarDetallePdfEntidad('" + codCampo + "','" + datoDocente.getCedula() + "','docente',this.id,'" + codCarrera + "','" + this.getAvance() + "')");
        DatosElementoAD datosElemento = new DatosElementoAD();
        datosElemento.agregarEstadosMaterias(materias, ad, codPeriodo, codCarrera);
        entidadAD.setDatos(datosElemento);
        this.getElementos().add(entidadAD);
    }

    private void entidadCampoFormacionUnidos2(DocenteUnidos datoDocente, AccesoDatos ad, String codCarrera, String codPeriodo, List<AsignaturaUnidos> materias, String codCampo) {//reporte unidades academicas usuario
        this.setSubtitulo("DOCENTES");
        Elemento entidadAD = new Elemento();
        entidadAD.setCodigo(datoDocente.getCedula());
        entidadAD.setNombre(datoDocente.getNombres() + " " + datoDocente.getApellidos());
        entidadAD.setFncClick("generarDetallePdfEntidad('" + codCampo + "','" + datoDocente.getCedula() + "','docente',this.id,'" + codCarrera + "','" + this.getAvance() + "')");
        DatosElementoAD datosElemento = new DatosElementoAD();
        datosElemento.agregarEstadosMaterias(materias, ad, codPeriodo, codCarrera);
        entidadAD.setDatos(datosElemento);
        this.getElementos().add(entidadAD);
    }

    private void agregarCamposFormacionCarrera(String codCarrera, AccesoDatos ad) throws SQLException {
        CampoFormacionAD campoFormacionAD = new CampoFormacionAD();
        Integer numeroCammposFormacion = campoFormacionAD.getSQLCamposFormacion(ad, codCarrera, 4);
        EntidadAD entidadAD = new EntidadAD();
        if (numeroCammposFormacion == 1) {
            Gson G = new Gson();
            Integer idPadre = campoFormacionAD.getSQLEntidad(ad, codCarrera);
            String jsonCarreraCampos = "{'codCarrera':'" + codCarrera + "'}";
            unidos.sw.CarreraAD camposFormacionCarrera = asignaturasCamposFormacion(jsonCarreraCampos);
            String jsonCamposFormacion = G.toJson(camposFormacionCarrera);
            CarreraUnidos carreraComun2 = G.fromJson(jsonCamposFormacion, CarreraUnidos.class);
            if (carreraComun2 != null) {
                carreraComun2.getCamposFormacion().forEach((campoFormacion) -> {
                    try {
                        entidadAD.ingresarEntidad(campoFormacion.getCodCampoF(), idPadre, 5, ad, campoFormacion.getDescCampoF());
                    } catch (SQLException ex) {
                        Logger.getLogger(ReporteGraficoAD.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        }
    }

    private static ArrayOfMateria getMateriasDocente(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getMateriasDocente(arg0, arg1, arg2);
    }

    private String obtenerNombreCarrera(String codigo, AccesoDatos ad) throws SQLException {
        String result = "No Definido";
        try {
            String SQL = "SELECT nombre\n"
                    + "  FROM t_entidad where codigo_entidad=? and id_tipo_entidad=?";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, codigo);
            ps.setInt(2, 4);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("nombre");
            }
            ad.getCon().commit();
            ps.close();
        } catch (Exception e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return result;
    }

    private static CarreraAD asignaturasCamposFormacion(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCamposFormacion(jsonCarrera);
    }

    private static CarreraAD docentesCamposFormacion(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.docentesCamposFormacion(jsonCarrera);
    }

    private String obtenerNombreCampoFormacionCarrera(String codCampo, String codCarrera, AccesoDatos ad) throws SQLException {
        String result = "";
        try {
            String SQL = "select te.nombre from t_entidad as te \n"
                    + "where te.codigo_entidad=? and te.id_padre=(select id_entidad from t_entidad where codigo_entidad=? and id_tipo_entidad='4')";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, codCampo);
            ps.setString(2, codCarrera);
            ResultSet rsSilabo = ps.executeQuery();
            while (rsSilabo.next()) {
                result = rsSilabo.getString("nombre");

            }
            ad.getCon().commit();
            ps.close();
        } catch (Exception e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return result;
    }

    private DocenteUnidos obtenerInformacionDocente(LoginAD loginAD) {
        DocenteUnidos docenteUnidos = new DocenteUnidos();
        try {
            docenteUnidos.setApellidos(loginAD.getApellidos());
            docenteUnidos.setCedula(loginAD.getCedula());
            docenteUnidos.setNombres(loginAD.getNombres());
        } catch (Exception e) {
        }
        return docenteUnidos;
    }

}
