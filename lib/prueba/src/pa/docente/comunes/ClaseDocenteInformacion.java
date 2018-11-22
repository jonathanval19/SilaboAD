/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pa.docente.comunes;

import pa.comunes.ClaseCarrera;
import ec.edu.espoch.academico.Materia;
import ec.edu.espoch.academico.Persona;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge Zaruma
 */
public class ClaseDocenteInformacion implements Serializable {

    private String nombres;
    private String apellidos;
    private String cedula;
    private String email;
    private List<ClaseCarrera> carreras = new ArrayList();

    /**
     * @return the nombres
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * @param nombres the nombres to set
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the carreras
     */
    public List<ClaseCarrera> getCarreras() {
        return carreras;
    }

    /**
     * @param carreras the carreras to set
     */
    public void setCarreras(List<ClaseCarrera> carreras) {
        this.carreras = carreras;
    }

    public void agregarDatosDocente(Persona objPersona) {
        this.nombres = objPersona.getNombres();
        this.apellidos = objPersona.getApellidos();
        this.cedula = objPersona.getCedula();
        this.email = objPersona.getEmail();
    }

    public void agregarAsignaturas(String codCarrera, List<Materia> objMaterias, String nombreRol) {
        ClaseCarrera objCarrera = null;
        objCarrera = new ClaseCarrera();
        objCarrera.setCodCarrera(codCarrera);
        objCarrera.agregarAsignaturas(objMaterias, codCarrera);
        objCarrera.agregarRolesDocente(nombreRol);
        this.carreras.add(objCarrera);
    }

}
