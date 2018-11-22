/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.reportes.ad;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.ad.SilaboAD;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CampoFormacionUnidos;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.clases.unidos.DocenteUnidos;
import dda.silabo.datosdocentes.comunes.DatoDocente;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.AsignaturasAD;
import dda.silabo.entidad.ad.CampoFormacionAD;
import dda.silabo.ln.SilaboLN;
import dda.silabo.reportes.comunes.Estados;
import dda.silabo.login.ad.LoginAD;
import ec.edu.espoch.academico.ArrayOfDictadoMateria;
import ec.edu.espoch.academico.ArrayOfMateria;
import ec.edu.espoch.academico.DictadoMateria;
import ec.edu.espoch.academico.Materia;
import ec.edu.espoch.academico.MateriaPensum;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.Jedis;
import unidos.sw.CarreraAD;

/**
 *
 * @author Jorge Zaruma
 */
public class EstadosAD extends Estados {

    public void getEstadosSilabos(MateriaPensum materiaPensum, List<MateriaPensum> materias, AccesoDatos ad, String codPeriodo, String codCarrera) {
        SilaboLN silaboLN = new SilaboLN();
        SilaboAD silaboAD = new SilaboAD();
        Gson G = new Gson();
        int contIinicio = 0, contA = 0, contC = 0, contR = 0;
        MateriaPensum pensumMateriaPensum = materias.stream().filter((pensum) -> (pensum.getCodArea().equals(materiaPensum.getCodArea()))).findFirst().orElse(null);
        if (silaboAD.idAsignaturaReporte(ad, pensumMateriaPensum.getCodMateria()) != 0 && silaboAD.idPeriodoSelect(ad) != 0) {
            silaboAD.setCodMateria(pensumMateriaPensum.getCodMateria());
            Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD),ad);
            String estado = silaboAD.getEstadoSilabo(idSilabo, ad);
            if (estado.equals("Inicio")) {
                contIinicio++;
            } else if (estado.equals("Aprobado")) {
                contA++;
            } else if (estado.equals("Corregido")) {
                contR++;
            } else if (estado.equals("Corregir")) {
                contC++;
            } else {
                contIinicio++;
            }
        } else {
            contIinicio++;
        }
        this.setAprobado(contA);
        this.setCorregir(contC);
        this.setInicio(contIinicio);
        this.setRevision(contR);
    }

    public Estados agregarEstadosEscuelas(List<CarreraUnidos> carreras) {
        Estados result = null;
        if (carreras.size() > 0) {
            Integer numInicio = 0, numAprobado = 0, numRevision = 0, numCorregir = 0;
            for (CarreraUnidos carrera : carreras) {
                if (carrera.getEstados() != null) {
                    numAprobado += carrera.getEstados().getAprobado();
                    numInicio += carrera.getEstados().getInicio();
                    numRevision += carrera.getEstados().getRevision();
                    numCorregir += carrera.getEstados().getCorregir();
                }
            }
            result = new Estados();
            result.setAprobado(numAprobado);
            result.setInicio(numInicio);
            result.setRevision(numRevision);
            result.setCorregir(numCorregir);
        }
        return result;
    }

    public List<AsignaturaUnidos> getMateriasPensum(LoginAD loginAD, String codPeriodo, String codCarrera) throws SQLException {//unidades academicas de usuario
        List<AsignaturaUnidos> result = new ArrayList<>();
        Gson G = new Gson();
        if (loginAD.getRolActivo().equals("Doc")) {
            ArrayOfMateria materias = getMateriasDocente(codCarrera, loginAD.getCedula(), codPeriodo);
            for (Materia materia : materias.getMateria()) {
                AsignaturaUnidos asignaturaComun = new AsignaturaUnidos();
                asignaturaComun.setNombreMateria(materia.getNombre());
                asignaturaComun.setCodMateria(materia.getCodigo());
                result.add(asignaturaComun);
            }
        } else if (loginAD.getRolActivo().equals("Cor")) {
            CampoFormacionAD campoFormacionAD = new CampoFormacionAD();
            AccesoDatos ad = new AccesoDatos();
            String codCampo = campoFormacionAD.getCampoCarreraActual(ad, loginAD.getCedula(), codCarrera, codPeriodo);
            try {
                unidos.sw.CarreraAD camposFormacionCarrera = asignaturasCamposFormacion("{'codCarrera':'" + codCarrera + "'}");
                String jsonAsignaturasCarrera = G.toJson(camposFormacionCarrera);
                CarreraUnidos carreraComun = G.fromJson(jsonAsignaturasCarrera, CarreraUnidos.class);
                if (carreraComun != null) {
                    CampoFormacionUnidos campoFormacion = carreraComun.getCamposFormacion().stream().filter(ca -> ca.getCodCampoF().equals(codCampo)).findFirst().orElse(null);
                    result = campoFormacion.getAsignaturas();
                } else {
                    result.add(errorAsignatura(codCarrera));
                }
            } catch (JsonSyntaxException e) {
                result.add(errorAsignatura(codCarrera));

            }

        } else {
            String jsonAsignaturasCarrera = "";
            try {
                CarreraAD carreraAD = asignaturasCarrera("{'codCarrera':'" + codCarrera + "'}");
                jsonAsignaturasCarrera = G.toJson(carreraAD);
            } catch (JsonSyntaxException e) {
                return result;
            }
            CarreraUnidos carreraComun = G.fromJson(jsonAsignaturasCarrera, CarreraUnidos.class);
            if (!carreraComun.getAsignaturas().isEmpty()) {
                result = carreraComun.getAsignaturas();
            }
        }

        return result;
    }

    public void agregarEstadoAsignaturaCampoFormacion(AsignaturaUnidos asignaturaComun, String codCarrera, String codPeriodo, AccesoDatos ad, LoginAD loginAD) {
        SilaboLN silaboLN = new SilaboLN();
        SilaboAD silaboAD = new SilaboAD();
        Gson G = new Gson();
        silaboAD.setPeriodo(codPeriodo);
        Integer contI = 0, contA = 0, contC = 0, contR = 0;
        if (silaboAD.idAsignaturaReporte(ad, asignaturaComun.getCodMateria()) != 0 && silaboAD.idPeriodoSelect(ad) != 0) {
            silaboAD.setCodMateria(asignaturaComun.getCodMateria());
            Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD),ad);
            String estado = silaboAD.getEstadoSilabo(idSilabo, ad);
            if (estado.equals("Inicio")) {
                contI++;
            }
            if (estado.equals("Aprobado")) {
                contA++;
            }
            if (estado.equals("Corregido")) {
                contR++;
            }
            if (estado.equals("Corregir")) {
                contC++;
            }
        } else {
            contI++;
        }
        this.setAprobado(contA);
        this.setCorregir(contC);
        this.setInicio(contI);
        this.setRevision(contR);
    }

    public void agregarAsignaturas(String estado, List<AsignaturaUnidos> asignaturas, AccesoDatos ad, String codPeriodo, String codCarrera, LoginAD loginAD, ArrayOfMateria materias, Jedis jedis) {
        Integer cont = 0;
        this.setDescripcion(estado);
        SilaboAD silaboAD = new SilaboAD();
        silaboAD.setPeriodo(codPeriodo);
        silaboAD.setCodCarrera(codCarrera);
        for (AsignaturaUnidos pensum : asignaturas) {
            if (materias != null) {
                Materia materia = materias.getMateria().stream().filter(ma -> ma.getCodigo().equals(pensum.getCodMateria())).findFirst().orElse(null);
                if (materia != null) {
                    cont += agregarAsignaturasCampo(silaboAD, loginAD, pensum, codPeriodo, codCarrera, ad, estado);
                }
            } else {
                cont += agregarAsignaturasCampo(silaboAD, loginAD, pensum, codPeriodo, codCarrera, ad, estado);
            }
        }
        this.setInicio(cont);
    }

    private Integer agregarAsignaturasCampo(SilaboAD silaboAD, LoginAD loginAD, AsignaturaUnidos pensum, String codPeriodo, String codCarrera, AccesoDatos ad, String estado) {
        SilaboLN silaboLN = new SilaboLN();
        Integer cont = 0;
        Gson G = new Gson();
        try {
            if (silaboAD.idAsignaturaReporte(ad, pensum.getCodMateria()) != 0 && silaboAD.idPeriodoSelect(ad) != 0) {
                silaboAD.setCodMateria(pensum.getCodMateria());
                Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD),ad);
                String estadoSilabo = silaboAD.getEstadoSilabo(idSilabo, ad);
                if (estadoSilabo.equals(estado)) {
                    cont++;
                    AsignaturasAD asignatura = new AsignaturasAD();
                    asignatura.agregarDocentes(pensum, codCarrera, codPeriodo, ad, loginAD);
                    this.getAsignatura().add(asignatura);
                }

            } else {
                if (estado.equals("Inicio")) {
                    cont++;
                    AsignaturasAD asignatura = new AsignaturasAD();
                    asignatura.agregarDocentes(pensum, codCarrera, codPeriodo, ad, loginAD);
                    this.getAsignatura().add(asignatura);

                }
            }
        } catch (Exception e) {
            cont++;
            AsignaturasAD asignatura = new AsignaturasAD();
            asignatura.agregarDocentes(pensum, codCarrera, codPeriodo, ad, loginAD);
            this.getAsignatura().add(asignatura);
        }

        return cont;
    }

    public List<Estados> agregarAsignaturasReporteEstado(AccesoDatos ad, LoginAD loginAD, String codPeriodo, String codCarrera) {
        List<Estados> estados = new ArrayList<>();
        try {
            EstadosAD estadosADInicio = new EstadosAD();
            estadosADInicio.setDescripcion("INICIO");
            EstadosAD estadosADCorregir = new EstadosAD();
            estadosADCorregir.setDescripcion("CORREGIR");
            EstadosAD estadosADAprobado = new EstadosAD();
            estadosADAprobado.setDescripcion("APROBADO");
            EstadosAD estadosADRevision = new EstadosAD();
            estadosADRevision.setDescripcion("REVISIÓN");
            Gson G = new Gson();
            SilaboLN silaboLN = new SilaboLN();
            List<AsignaturaUnidos> asignaturaComuns = getMateriasPensum(loginAD, codPeriodo, codCarrera);
            SilaboAD silaboAD = new SilaboAD();
            for (AsignaturaUnidos as : asignaturaComuns) {
                if (as.getDocentes().isEmpty()) {
                    as.setDocentes(obtenerDocentesMateria(codPeriodo, as.getCodMateria(), codCarrera));
                }
                if (silaboAD.existeAsignaturaPeriodo(as.getCodMateria(), codPeriodo, ad) != 0) {
                    silaboAD.setCodCarrera(codCarrera);
                    silaboAD.setPeriodo(codPeriodo);
                    Integer idSilabo = silaboLN.getidSilaboCoordinador(as.getCodMateria(), codPeriodo, loginAD.getRolActivo());
                    String estado = silaboAD.getEstadoSilabo(idSilabo, ad);
                    if (estado.equals("Inicio")) {
                        estadosADInicio.getAsignatura().add(as);
                    } else if (estado.equals("Aprobado")) {
                        estadosADAprobado.getAsignatura().add(as);
                    } else if (estado.equals("Corregido")) {
                        estadosADRevision.getAsignatura().add(as);
                    } else if (estado.equals("Corregir")) {
                        estadosADCorregir.getAsignatura().add(as);
                    } else {
                        estadosADInicio.getAsignatura().add(as);
                    }
                } else {
                    estadosADInicio.getAsignatura().add(as);
                }
            }
            estados.add(estadosADInicio);
            estados.add(estadosADRevision);
            estados.add(estadosADCorregir);
            estados.add(estadosADAprobado);

        } catch (Exception e) {
        }
        return estados;
    }

    private List<DocenteUnidos> obtenerDocentesMateria(String codPeriodo, String codMateria, String codCarrera) {
        List<DocenteUnidos> result = new ArrayList<>();
        try {
            ArrayOfDictadoMateria array = lPADictadosMateria(codCarrera, codMateria);
            for (DictadoMateria dm : array.getDictadoMateria()) {
                DocenteUnidos dc = new DocenteUnidos();
                dc.setNombres(dm.getDocente().getNombres());
                dc.setApellidos(dm.getDocente().getApellidos());
                result.add(dc);
            }
        } catch (Exception e) {
        }
        return result;
    }

    private static ArrayOfMateria getMateriasDocente(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getMateriasDocente(arg0, arg1, arg2);
    }

    private static ArrayOfDictadoMateria lPADictadosMateria(java.lang.String arg0, java.lang.String arg1) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.lPADictadosMateria(arg0, arg1);
    }

    private AsignaturaUnidos errorAsignatura(String codCarrera) {
        AsignaturaUnidos asignaturaUnidos = new AsignaturaUnidos();
        asignaturaUnidos.setCodCarrera(codCarrera);
        asignaturaUnidos.setCodMateria("0");
        asignaturaUnidos.setNombreMateria("Error al consultar asignaturas");
        return asignaturaUnidos;
    }

    private static CarreraAD asignaturasCarrera(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCarrera(jsonCarrera);
    }

    private static CarreraAD asignaturasCamposFormacion(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCamposFormacion(jsonCarrera);
    }

}
