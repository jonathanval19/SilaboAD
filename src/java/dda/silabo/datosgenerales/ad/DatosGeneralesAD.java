/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.datosgenerales.ad;

import com.google.gson.Gson;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CarreraUnidos;
import dda.silabo.datosgenerales.comunes.DatosGenerales;
import dda.silabo.login.ln.PeriodoLN;
import dda.silabo.silabo.comunes.Silabo;
import ec.edu.espoch.academico.Periodo;
import ec.edu.espoch.academico.ArrayOfEscuelaEntidad;
import ec.edu.espoch.academico.EscuelaEntidad;
import ec.edu.espoch.academico.ParamCarrera;
import unidos.sw.CarreraAD;

/**
 *
 * @author Jorge Zaruma
 */
public class DatosGeneralesAD extends DatosGenerales {

    public void DatosGeneralesCargar(String codCarrera, String codMateria) {
        Gson G = new Gson();
        CarreraUnidos carreraComun = null;
        try {
            CarreraAD carreraAD = asignaturasCarrera("{'codCarrera':'" + codCarrera + "'}");
            String jsonAsignaturaCarrera = G.toJson(carreraAD);
            carreraComun = G.fromJson(jsonAsignaturaCarrera, CarreraUnidos.class);
        } catch (Exception e) {
            carreraComun = new CarreraUnidos();
        }
        PeriodoLN periodoLN = new PeriodoLN();
        Periodo periodo = periodoLN.getPeriodoValido();

        EscuelaEntidad escuelaEntidad = arrayOfEscuelaEntidad().getEscuelaEntidad().stream().filter(escE -> escE.getCodCarrera().equals(codCarrera)).findFirst().orElse(null);
        this.setNombre_escuela(escuelaEntidad.getEscuela());
        this.setNombre_facultad(escuelaEntidad.getFacultad());
        this.setNombre_carrera(escuelaEntidad.getCarrera());
        ParamCarrera pCarrera = getParametrosCarrera(codCarrera);
        if (pCarrera != null) {
            this.setModalidad(pCarrera.getModalidad());
            this.setNombre_sede(pCarrera.getResidencia());
        }
        AsignaturaUnidos asignaturaComun = carreraComun.getAsignaturas().stream().filter(as -> as.getCodMateria().equals(codMateria)).findFirst().orElse(null);
        if (asignaturaComun != null) {
            this.setSilabo_materia(asignaturaComun.getNombreMateria());
            this.setCodigo_asignatura(asignaturaComun.getCodMateria());
            this.setCampo(asignaturaComun.getArea());
            this.setCodigoCampo(asignaturaComun.getCodArea());
            this.setNivel(asignaturaComun.getNivel());
            String creditos = asignaturaComun.getCreditos().toString();
            creditos = creditos.substring(0, creditos.indexOf("."));
            this.setNumero_creditos(Double.parseDouble(creditos));
            this.setHoras_semanales(asignaturaComun.getHorasPracticas() + asignaturaComun.getHorasTeoricas());
            this.setPeriodo_academico(periodo.getDescripcion());
            String prerrequisitos = "";
//CORRECCION          
//  prerrequisitos = asignaturaComun.get.stream().map((asignaturaComun1) -> asignaturaComun1.getCodAsignatura() + "  ").reduce(prerrequisitos, String::concat);
            String correquisitos = "";
            // correquisitos = asignaturaComun.getCorrequisitos().stream().map((asignaturaComun2) -> asignaturaComun2.getCodAsignatura() + "  ").reduce(correquisitos, String::concat);
            this.setCorrequisitos(correquisitos);
            this.setPrerequisitos(prerrequisitos);

        }

        this.setTitulo("Datos Generales de Asignatura");
        this.setAyuda("<div style='text-align:justify'>Este contenido es referente a la informacion de la asignatura que se esta realizando el Sílabo, "
                + "así como a que Unidad Académica de la Institucion pertenece</div>");
    }

    private static ArrayOfEscuelaEntidad arrayOfEscuelaEntidad() {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.arrayOfEscuelaEntidad();
    }

    private static ParamCarrera getParametrosCarrera(java.lang.String arg0) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getParametrosCarrera(arg0);
    }

    private static CarreraAD asignaturasCarrera(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCarrera(jsonCarrera);
    }

}
