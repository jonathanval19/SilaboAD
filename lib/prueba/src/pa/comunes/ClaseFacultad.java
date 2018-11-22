package pa.comunes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClaseFacultad implements Serializable {

    private List<ClaseEscuela> escuelas = new ArrayList();
    String codFacultad, nombre;

    public List<ClaseEscuela> getEscuelas() {
        return escuelas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodFacultad() {
        return codFacultad;
    }

    public void setCodFacultad(String codFacultad) {
        this.codFacultad = codFacultad;
    }

    public void addEscuela(ClaseEscuela escuela) {
        this.escuelas.add(escuela);
    }

    public ClaseEscuela buscarEscuelaXCarrera(String codCarrera) {
        ClaseEscuela result = null;
        while (this.escuelas.iterator().hasNext() && result == null) {
            ClaseEscuela escuela = this.escuelas.iterator().next();
            if (escuela.buscarCarrera(codCarrera) != null) {
                result = escuela;
            }
        }
        return result;
    }

    public void agregarAsignatura(String codCarrera, List<ClaseAsignatura> objAsignatura) {
        ClaseEscuela objEscuela = buscarEscuelaXCarrera(codCarrera);
        if (objEscuela != null) {
            objEscuela.agregarAsignatura(codCarrera, objAsignatura);
        }
    }

    public ClaseFacultad buscarEscuelaConAsignaturas() {
        ClaseFacultad result = null;
        List<ClaseEscuela> escuelas2 = new ArrayList<>();
        for (ClaseEscuela escuela : this.escuelas) {
            if (escuela.devolverCarreraConAsignaturas() != null) {
                escuelas2.add(escuela);
            }
            if (escuelas2.size() > 0) {
                result = new ClaseFacultad();
                result.setCodFacultad(this.getCodFacultad());
                result.setNombre(this.getNombre());
                result.setEscuelas(escuelas2);
            }
        }
        return result;
    }

    /**
     * @param escuelas the escuelas to set
     */
    public void setEscuelas(List<ClaseEscuela> escuelas) {
        this.escuelas = escuelas;
    }

}
