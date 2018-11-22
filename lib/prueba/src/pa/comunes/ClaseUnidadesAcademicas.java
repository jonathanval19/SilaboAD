package pa.comunes;

import ec.edu.espoch.academico.Materia;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClaseUnidadesAcademicas implements Serializable {

    List<ClaseFacultad> facultades = new ArrayList();
    private String id, desc;

    public void addFacultad(ClaseFacultad facultad) {
        getFacultades().add(facultad);
    }

    public ClaseFacultad buscarFacultadXCarrera(String codCarrera) {
        ClaseFacultad result = null;
        while (this.getFacultades().iterator().hasNext() && result == null) {
            ClaseFacultad facultad = this.getFacultades().iterator().next();
            if (facultad.buscarEscuelaXCarrera(codCarrera) != null) {
                result = facultad;
            }
        }
        return result;
    }

    public ClaseFacultad buscarFacultadConAsignaturas() {
        ClaseFacultad result = null;
        while (this.getFacultades().iterator().hasNext() && result == null) {
            ClaseFacultad facultad = this.getFacultades().iterator().next();
            if (facultad.buscarEscuelaConAsignaturas() != null) {
                result = facultad;
            }
        }
        return result;

    }

    /**
     * @return the facultades
     */
    public List<ClaseFacultad> getFacultades() {
        return facultades;
    }

    /**
     * @param facultades the facultades to set
     */
    public void setFacultades(List<ClaseFacultad> facultades) {
        this.facultades = facultades;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
