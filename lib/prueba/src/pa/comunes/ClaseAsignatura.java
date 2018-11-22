/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pa.comunes;

import java.io.Serializable;

/**
 *
 * @author Jorge Zaruma
 */
public class ClaseAsignatura implements Serializable {

    /**
     * @return the codCarrera
     */
    public String getCodCarrera() {
        return codCarrera;
    }

    /**
     * @param codCarrera the codCarrera to set
     */
    public void setCodCarrera(String codCarrera) {
        this.codCarrera = codCarrera;
    }

    private String codAsignatura;
    private String nombreAsignatura;
    private String codCarrera;

    /**
     * @return the codAsignatura
     */
    public String getCodAsignatura() {
        return codAsignatura;
    }

    /**
     * @param codAsignatura the codAsignatura to set
     */
    public void setCodAsignatura(String codAsignatura) {
        this.codAsignatura = codAsignatura;
    }

    /**
     * @return the nombreAsignatura
     */
    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    /**
     * @param nombreAsignatura the nombreAsignatura to set
     */
    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

}
