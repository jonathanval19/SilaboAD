/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.entidad.ad;

import com.google.gson.Gson;
import dda.silabo.ad.SilaboAD;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.DocenteUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.estructura.unidadinformacion.ln.EstructuraDesarrolloLN;
import dda.silabo.ln.SilaboLN;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.reportes.ad.EstadosAD;
import dda.silabo.silabo.comunes.Silabo;
import ec.edu.espoch.academico.ArrayOfDictadoMateria;
import ec.edu.espoch.academico.DictadoMateria;
import ec.edu.espoch.academico.Materia;

/**
 *
 * @author Jorge Zaruma
 */
public class AsignaturasAD extends AsignaturaUnidos {

    public void getAsignatura(Materia mat, String codCarrera) {
        this.setNombreMateria(mat.getNombre());
        this.setCodMateria(mat.getCodigo());
        this.setCodCarrera(codCarrera);
    }

    void agregarAsignaturaEntidad(AsignaturaUnidos asignaturaComun, SilaboAD silaboAD, AccesoDatos ad) {//unidades academicas de usuario
        this.setCodMateria(asignaturaComun.getCodMateria());
        this.setNombreMateria(asignaturaComun.getNombreMateria());
        this.setCodArea(asignaturaComun.getCodArea());
        Gson G = new Gson();
        SilaboLN silaboLN = new SilaboLN();
        String estado = "";
        try {
            if (silaboAD.existeAsignaturaPeriodo(asignaturaComun.getCodMateria(), silaboAD.getPeriodo(), ad) != 0) {
                silaboAD.setCodMateria(asignaturaComun.getCodMateria());
                Integer idSilabo = silaboLN.getIdSilabo(G.toJson(silaboAD), ad);
                estado = silaboAD.getEstadoSilabo(idSilabo, ad);
            }
        } catch (Exception e) {
            estado = "";
        }
        this.setEstadoSilabo(estado);
        this.setCodCarrera(silaboAD.getCodCarrera());
    }

    public void agregarAsignaturasBaseDatos(String jsonAsignaturaInfo, AccesoDatos ad) {
        Gson G = new Gson();
        SilaboAD silaboad = new SilaboAD();
        EstructuraDesarrolloLN estructuraln = new EstructuraDesarrolloLN();
        Silabo silabo = G.fromJson(jsonAsignaturaInfo, Silabo.class);
        if (silabo.getIdSilabo() > 0) {
            if (silaboad.existeSilaboSeccion(silabo.getIdSilabo(), ad) == 0) {
                silaboad.asignarSecciones(silabo.getIdSilabo(), ad);
            }
            int numUnidades = estructuraln.getNumeroUnidades(jsonAsignaturaInfo);
            if (numUnidades != 0 && silaboad.existeSilaboSubseccion(silabo.getIdSilabo(), ad) == 0) {
                silaboad.asignarSubsecciones(silabo.getIdSilabo(), numUnidades, ad);
            }
            if (silaboad.existeEstadoSilabo(silabo.getIdSilabo(), ad) == 0) {
                silaboad.agregarEstadoSilabo(silabo.getIdSilabo(), ad);
            }
        }
    }

    public void agregarDocentes(AsignaturaUnidos asignaturaComun, String codCarrera, String codPeriodo, AccesoDatos ad, LoginAD loginAD) {//funciona
        this.setCodMateria(asignaturaComun.getCodMateria());
        this.setNombreMateria(asignaturaComun.getNombreMateria());
        ArrayOfDictadoMateria arrayOfDictadoMateria = lPADictadosMateria(codCarrera, this.getCodMateria());
        if (arrayOfDictadoMateria != null) {

            for (DictadoMateria dm : arrayOfDictadoMateria.getDictadoMateria()) {
                DocenteUnidos d = new DocenteUnidos();
                d.setApellidos(dm.getDocente().getApellidos());
                d.setNombres(dm.getDocente().getNombres());
                d.setCedula(dm.getDocente().getCedula());
                this.getDocentes().add(d);
            }
        }
    }

    public void agregarAsignaturasDocente(Materia materia, String codCarrrera, String codPeriodo, AccesoDatos ad, LoginAD loginAD) {
        this.setCodMateria(materia.getCodigo());
        this.setNombreMateria(materia.getNombre());
        EstadosAD estadosAD = new EstadosAD();
        estadosAD.agregarEstadoAsignaturaCampoFormacion(this, codCarrrera, codPeriodo, ad, loginAD);
        this.setEstados(estadosAD);
    }

    private static ArrayOfDictadoMateria lPADictadosMateria(java.lang.String arg0, java.lang.String arg1) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.lPADictadosMateria(arg0, arg1);
    }

}
