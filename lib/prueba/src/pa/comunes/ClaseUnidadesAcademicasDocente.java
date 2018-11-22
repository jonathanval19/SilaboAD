/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pa.comunes;

import java.util.List;

/**
 *
 * @author Jorge Zaruma
 */
public class ClaseUnidadesAcademicasDocente extends ClaseUnidadesAcademicas {

    public void AgregarAsignatura(String codCarrera, List<ClaseAsignatura> asignaturas) {
        ClaseFacultad objFacultad = buscarFacultadXCarrera(codCarrera);
        if (objFacultad != null) {
            objFacultad.agregarAsignatura(codCarrera, asignaturas);
        }
    }

    public ClaseUnidadesAcademicas getUnidadesAcademicasXDocente(List<ClaseCarrera> carreras) {
        ClaseUnidadesAcademicas result = new ClaseUnidadesAcademicas();
//        for (ClaseCarrera carrera : carreras) {
//            String codCarrera = carrera.getCodCarrera();
//            List<ClaseAsignatura> asignaturas = carrera.getAsignaturas();
//            AgregarAsignatura(codCarrera, asignaturas);
//        }
        if (carreras.size() > 0) {
            result.setDesc("eeeeeeEEEEEE");
            result.setId("11111111111111");
        }
//        
//        for (ClaseFacultad facultad : this.getFacultades()) {
//            if (facultad.buscarEscuelaConAsignaturas() != null) {
//                result = new ClaseUnidadesAcademicas();
//                result.addFacultad(facultad);
//            }
//        }
        return result;
    }
}
