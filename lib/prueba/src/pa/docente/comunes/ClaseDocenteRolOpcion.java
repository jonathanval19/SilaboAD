/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pa.docente.comunes;

import java.io.Serializable;

/**
 *
 * @author Jorge Zaruma
 */
public class ClaseDocenteRolOpcion implements Serializable {

    private String idOpcion;
    private String nombreOpcion;

    /**
     * @return the idOpcion
     */
    public String getIdOpcion() {
        return idOpcion;
    }

    /**
     * @param idOpcion the idOpcion to set
     */
    public void setIdOpcion(String idOpcion) {
        this.idOpcion = idOpcion;
    }

    /**
     * @return the nombreOpcion
     */
    public String getNombreOpcion() {
        return nombreOpcion;
    }

    /**
     * @param nombreOpcion the nombreOpcion to set
     */
    public void setNombreOpcion(String nombreOpcion) {
        this.nombreOpcion = nombreOpcion;
    }

}
