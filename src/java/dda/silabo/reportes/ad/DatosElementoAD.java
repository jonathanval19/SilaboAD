/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.reportes.ad;

import com.google.gson.Gson;
import dda.silabo.ad.SilaboAD;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.clases.unidos.EscuelaUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.ln.SilaboLN;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.reportes.comunes.DatosElemento;
import ec.edu.espoch.academico.ArrayOfMateriaPensum;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class DatosElementoAD extends DatosElemento {

    public void agregarEstadosFacultad(List<EscuelaUnidos> escuelas, AccesoDatos ad, String codPeriodo, LoginAD loginAD) {
        Gson G = new Gson();
        SilaboAD silaboAD = new SilaboAD();
        silaboAD.setPeriodo(codPeriodo);
        SilaboLN silaboLN = new SilaboLN();
        silaboAD.setPeriodo(codPeriodo);
        Integer aux = 0;
        Integer contI = 0, contA = 0, contC = 0, contR = 0;
        EstadosAD estadosAD = new EstadosAD();
        try {

            for (EscuelaUnidos escuelaComun : escuelas) {
                for (CarreraUnidos carreraComun : escuelaComun.getCarreras()) {
                    try {
                        List<AsignaturaUnidos> materias = estadosAD.getMateriasPensum(loginAD, codPeriodo, carreraComun.getCodCarrera());
                        silaboAD.setCodCarrera(carreraComun.getCodCarrera());
                        for (AsignaturaUnidos pensum : materias) {
                            try {
                                if (silaboAD.idAsignaturaReporte(ad, pensum.getCodMateria()) != 0 && silaboAD.idPeriodoSelect(ad) != 0) {
                                    silaboAD.setCodMateria(pensum.getCodMateria());
                                    Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD), ad);
                                    String estado = silaboAD.getEstadoSilabo(idSilabo, ad);
                                    if (estado.equals("Inicio")) {
                                        contI++;
                                    } else if (estado.equals("Aprobado")) {
                                        contA++;
                                    } else if (estado.equals("Corregido")) {
                                        contR++;
                                    } else if (estado.equals("Corregir")) {
                                        contC++;
                                    } else {
                                        contI++;
                                    }
                                } else {
                                    contI++;
                                }
                            } catch (Exception e) {
                                contI++;
                            }
                        }
                    } catch (Exception e) {
                        aux++;
                    }
                }
            }
        } catch (Exception e) {
        }
        this.setAprobado(contA);
        this.setCorregir(contC);
        this.setInicio(contI);
        this.setRevision(contR);

    }

    public void agregarEstadoEscuela(EscuelaUnidos escuelaComun, AccesoDatos ad, String codPeriodo, LoginAD loginAD) {
        Gson G = new Gson();
        SilaboAD silaboAD = new SilaboAD();
        silaboAD.setPeriodo(codPeriodo);
        SilaboLN silaboLN = new SilaboLN();
        silaboAD.setPeriodo(codPeriodo);
        EstadosAD estadosAD = new EstadosAD();
        Integer aux = 0;
        Integer contI = 0, contA = 0, contC = 0, contR = 0;
        try {
            if (ad.Connectar() != 0) {
                for (CarreraUnidos carreraComun : escuelaComun.getCarreras()) {
                    try {
                        List<AsignaturaUnidos> materias = estadosAD.getMateriasPensum(loginAD, codPeriodo, carreraComun.getCodCarrera());
                        silaboAD.setCodCarrera(carreraComun.getCodCarrera());
                        for (AsignaturaUnidos pensum : materias) {
                            try {
                                if (silaboAD.idAsignaturaReporte(ad, pensum.getCodMateria()) != 0 && silaboAD.idPeriodoSelect(ad) != 0) {
                                    silaboAD.setCodMateria(pensum.getCodMateria());
                                    Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD), ad);
                                    String estado = silaboAD.getEstadoSilabo(idSilabo, ad);
                                    if (estado.equals("Inicio")) {
                                        contI++;
                                    } else if (estado.equals("Aprobado")) {
                                        contA++;
                                    } else if (estado.equals("Corregido")) {
                                        contR++;
                                    } else if (estado.equals("Corregir")) {
                                        contC++;
                                    } else {
                                        contI++;
                                    }
                                } else {
                                    contI++;
                                }
                            } catch (Exception e) {
                                contI++;
                            }
                        }
                    } catch (Exception e) {
                        aux++;
                    }
                }
            }
        } catch (Exception e) {
        }
        this.setAprobado(contA);
        this.setCorregir(contC);
        this.setInicio(contI);
        this.setRevision(contR);
    }

    public void agregarEstadosCarrera(CarreraUnidos carreraComun, AccesoDatos ad, String codPeriodo, LoginAD loginAD) throws SQLException {
        Gson G = new Gson();
        SilaboAD silaboAD = new SilaboAD();
        silaboAD.setPeriodo(codPeriodo);
        SilaboLN silaboLN = new SilaboLN();
        EstadosAD estadosAD = new EstadosAD();
        Integer contI = 0, contA = 0, contC = 0, contR = 0;
        String SQL = "";
        try {
            if (loginAD.getRolActivo().equals("Doc") || loginAD.getRolActivo().equals("Cor")) {
                List<AsignaturaUnidos> materias = estadosAD.getMateriasPensum(loginAD, codPeriodo, carreraComun.getCodCarrera());
                silaboAD.setCodCarrera(carreraComun.getCodCarrera());
                for (AsignaturaUnidos pensum : materias) {
                    try {
                        if (silaboAD.existeAsignaturaPeriodo(pensum.getCodMateria(), codPeriodo, ad) != 0) {
                            silaboAD.setCodMateria(pensum.getCodMateria());
                            Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD), ad);
                            String estado = silaboAD.getEstadoSilabo(idSilabo, ad);
                            if (estado.equals("Inicio")) {
                                contI++;
                            } else if (estado.equals("Aprobado")) {
                                contA++;
                            } else if (estado.equals("Corregido")) {
                                contR++;
                            } else if (estado.equals("Corregir")) {
                                contC++;
                            } else {
                                contI++;
                            }
                        } else {
                            contI++;
                        }
                    } catch (Exception e) {
                        contI++;
                    }
                }
                this.setAprobado(contA);
                this.setCorregir(contC);
                this.setInicio(contI);
                this.setRevision(contR);
            } else {
                selectEstadosCarrera(ad, carreraComun.getCodCarrera(), codPeriodo);
            }
        } catch (Exception e) {
        }
    }

    public void agregarEstadosMaterias(List<AsignaturaUnidos> materias, AccesoDatos ad, String codPeriodo, String codCarrera) {
        Gson G = new Gson();
        SilaboAD silaboAD = new SilaboAD();
        silaboAD.setPeriodo(codPeriodo);
        silaboAD.setCodCarrera(codCarrera);
        SilaboLN silaboLN = new SilaboLN();
        Integer contI = 0, contA = 0, contC = 0, contR = 0;
        try {
            for (AsignaturaUnidos asignaturaComun : materias) {
                try {
                    if (silaboAD.idAsignaturaReporte(ad, asignaturaComun.getCodMateria()) != 0 && silaboAD.idPeriodoSelect(ad) != 0) {
                        silaboAD.setCodMateria(asignaturaComun.getCodMateria());
                        Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD), ad);

                        String estado = silaboAD.getEstadoSilabo(idSilabo, ad);
                        if (estado.equals("Inicio")) {
                            contI++;
                        } else if (estado.equals("Aprobado")) {
                            contA++;
                        } else if (estado.equals("Corregido")) {
                            contR++;
                        } else if (estado.equals("Corregir")) {
                            contC++;
                        } else {
                            contI++;
                        }
                    } else {
                        contI++;
                    }
                } catch (Exception e) {
                    contI++;
                }
            }
        } catch (Exception e) {
        }
        this.setAprobado(contA);
        this.setCorregir(contC);
        this.setInicio(contI);
        this.setRevision(contR);
    }

    private void selectEstadosCarrera(AccesoDatos ad, String codCarrera, String codPeriodo) throws SQLException {
        String SQL = "select (	\n"
                + "SELECT  COUNT(tse.estado) from t_asignatura as ta \n"
                + "join t_entidad as te on te.id_entidad = ta.id_entidad\n"
                + "join t_docente_asig_periodo as tdap \n"
                + "on tdap.asignatura = ta.id_asignatura\n"
                + "join t_periodo_academico as tpa\n"
                + "on tpa.id_periodo = tdap.periodo and tpa.codigo=?\n"
                + "join t_silabo as ts\n"
                + "on ts.doc_asg_per = tdap.id_doc_asg_per\n"
                + "join t_silabo_estados as tse \n"
                + "on tse.id_silabo = ts.id_silabo\n"
                + "where te.codigo_entidad=? and tse.estado='Corregido' ) as corregido,(	\n"
                + "SELECT  COUNT(tse.estado) from t_asignatura as ta \n"
                + "join t_entidad as te on te.id_entidad = ta.id_entidad\n"
                + "join t_docente_asig_periodo as tdap \n"
                + "on tdap.asignatura = ta.id_asignatura\n"
                + "join t_periodo_academico as tpa\n"
                + "on tpa.id_periodo = tdap.periodo and tpa.codigo=?\n"
                + "join t_silabo as ts\n"
                + "on ts.doc_asg_per = tdap.id_doc_asg_per\n"
                + "join t_silabo_estados as tse \n"
                + "on tse.id_silabo = ts.id_silabo\n"
                + "where te.codigo_entidad=? and tse.estado='Aprobado'  ) as aprobado,(	\n"
                + "SELECT  COUNT(tse.estado) from t_asignatura as ta \n"
                + "join t_entidad as te on te.id_entidad = ta.id_entidad\n"
                + "join t_docente_asig_periodo as tdap \n"
                + "on tdap.asignatura = ta.id_asignatura\n"
                + "join t_periodo_academico as tpa\n"
                + "on tpa.id_periodo = tdap.periodo and tpa.codigo=?\n"
                + "join t_silabo as ts\n"
                + "on ts.doc_asg_per = tdap.id_doc_asg_per\n"
                + "join t_silabo_estados as tse \n"
                + "on tse.id_silabo = ts.id_silabo\n"
                + "where te.codigo_entidad=? and tse.estado='Corregir'  ) as corregir,(	\n"
                + "SELECT  COUNT(tse.estado) from t_asignatura as ta \n"
                + "join t_entidad as te on te.id_entidad = ta.id_entidad\n"
                + "join t_docente_asig_periodo as tdap \n"
                + "on tdap.asignatura = ta.id_asignatura\n"
                + "join t_periodo_academico as tpa\n"
                + "on tpa.id_periodo = tdap.periodo and tpa.codigo=?\n"
                + "join t_silabo as ts\n"
                + "on ts.doc_asg_per = tdap.id_doc_asg_per\n"
                + "join t_silabo_estados as tse \n"
                + "on tse.id_silabo = ts.id_silabo\n"
                + "where te.codigo_entidad=? and tse.estado!='Inicio') as total";
        try {
            Integer total = 0;
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, codPeriodo);
            ps.setString(2, codCarrera);
            ps.setString(3, codPeriodo);
            ps.setString(4, codCarrera);
            ps.setString(5, codPeriodo);
            ps.setString(6, codCarrera);
            ps.setString(7, codPeriodo);
            ps.setString(8, codCarrera);
            ResultSet rsSilabo = ps.executeQuery();
            while (rsSilabo.next()) {
                this.setAprobado(rsSilabo.getInt("aprobado"));
                this.setCorregir(rsSilabo.getInt("corregir"));
                this.setRevision(rsSilabo.getInt("corregido"));
                total = rsSilabo.getInt("total");

            }
            ad.getCon().commit();
            ps.close();
            ArrayOfMateriaPensum p = pensumVigente(codCarrera);
            this.setInicio(p.getMateriaPensum().size() - total);
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    private static ArrayOfMateriaPensum pensumVigente(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.pensumVigente(jsonCarrera);
    }
}
