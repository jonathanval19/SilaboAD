package pa.comunes;

import ec.edu.espoch.academico.Materia;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClaseEscuela implements Serializable {

    /**
     * @param carreras the carreras to set
     */
    public void setCarreras(List<ClaseCarrera> carreras) {
        this.carreras = carreras;
    }

    private List<ClaseCarrera> carreras = new ArrayList<>();
    String codEscuela;
    String nombre;

    public List<ClaseCarrera> getCarreras() {
        return carreras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodEscuela() {
        return codEscuela;
    }

    public void setCodEscuela(String codEscuela) {
        this.codEscuela = codEscuela;
    }

    public void addCarrera(ClaseCarrera carrera) {
        this.carreras.add(carrera);
    }

    public ClaseCarrera buscarCarrera(String codCarrera) {
        ClaseCarrera result = null;
        while (this.carreras.iterator().hasNext() && result == null) {
            ClaseCarrera carrera = this.carreras.iterator().next();
            if (carrera.getCodCarrera().equals(codCarrera)) {
                result = carrera;
            }
        }
        return result;
    }

    public void agregarAsignatura(String codCarrera, List<ClaseAsignatura> objAsignatura) {
        ClaseCarrera objCarrera = buscarCarrera(codCarrera);
        if (objCarrera != null) {
            objCarrera.agregarAsignatura(objAsignatura);
        }
    }

    public ClaseEscuela devolverCarreraConAsignaturas() {
        ClaseEscuela result = null;
        List<ClaseCarrera> carreras2 = new ArrayList<>();
        for (ClaseCarrera carrera : this.carreras) {
            if (carrera.haveAsignaturas()) {
                carreras2.add(carrera);
            }
        }
        if (carreras2.size() > 0) {
            result = new ClaseEscuela();
            result.setCodEscuela(this.getCodEscuela());
            result.setNombre(this.getNombre());

            result.setCarreras(carreras2);

        }
        return result;
    }

    public ClaseCarrera agregarAsignaturas(String codCarrera, List<Materia> objMaterias) {
        ClaseCarrera objCarrera = null;
        objCarrera = new ClaseCarrera();
        objCarrera.setCodCarrera(codCarrera);
        objCarrera.agregarAsignaturas(objMaterias, codCarrera);
        this.carreras.add(objCarrera);

        return objCarrera;
    }

}
