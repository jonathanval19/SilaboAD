/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pa.docente.comunes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge Zaruma
 */
public class ClaseDocenteRol implements Serializable {

    private String idRol;
    private String nombreRol;
    private List<ClaseDocenteRolOpcion> docenteRolOpciones = new ArrayList();

    /**
     * @return the idRol
     */
    public String getIdRol() {
        return idRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }

    /**
     * @return the nombreRol
     */
    public String getNombreRol() {
        return nombreRol;
    }

    /**
     * @param nombreRol the nombreRol to set
     */
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    /**
     * @return the docenteRolOpciones
     */
    public List<ClaseDocenteRolOpcion> getDocenteRolOpciones() {
        return docenteRolOpciones;
    }

    /**
     * @param docenteRolOpciones the docenteRolOpciones to set
     */
    public void setDocenteRolOpciones(List<ClaseDocenteRolOpcion> docenteRolOpciones) {
        this.docenteRolOpciones = docenteRolOpciones;
    }
}
