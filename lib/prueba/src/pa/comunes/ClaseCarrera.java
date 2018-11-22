/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pa.comunes;

import ec.edu.espoch.academico.Materia;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pa.docente.comunes.ClaseDocenteRol;

/**
 *
 * @author Jorge Zaruma
 */
public class ClaseCarrera implements Serializable {

    private String codCarrera;
    private String nombreCarrera;
    private List<ClaseAsignatura> asignaturas = new ArrayList();
    private List<ClaseDocenteRol> docenteroles = new ArrayList<>();

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

    /**
     * @return the nombreCarrera
     */
    public String getNombreCarrera() {
        return nombreCarrera;
    }

    /**
     * @param nombreCarrera the nombreCarrera to set
     */
    public void setNombreCarrera(String nombreCarrera) {
        this.nombreCarrera = nombreCarrera;
    }

    /**
     * @return the asignaturas
     */
    public List<ClaseAsignatura> getAsignaturas() {
        return asignaturas;
    }

    /**
     * @param asignaturas the asignaturas to set
     */
    public void setAsignaturas(List<ClaseAsignatura> asignaturas) {
        this.asignaturas = asignaturas;
    }

    public boolean haveAsignaturas() {
        return (this.asignaturas.size() > 0);
    }

    public void agregarAsignatura(List<ClaseAsignatura> objAsignatura) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void agregarAsignaturas(List<Materia> objMateria, String codCarrera) {
        ClaseAsignatura objAsignatura = null;
        for (Materia materia : objMateria) {
            objAsignatura = new ClaseAsignatura();
            objAsignatura.setCodAsignatura(materia.getCodigo());
            objAsignatura.setNombreAsignatura(materia.getNombre());
            objAsignatura.setCodCarrera(codCarrera);
            this.asignaturas.add(objAsignatura);
        }
    }

    public void agregarRolesDocente(String nombreRol) {
        ClaseDocenteRol objRol = new ClaseDocenteRol();
        objRol.setNombreRol("Docente");
        this.docenteroles.add(objRol);
    }

    /**
     * @return the docenteroles
     */
    public List<ClaseDocenteRol> getDocenteroles() {
        return docenteroles;
    }

    /**
     * @param docenteroles the docenteroles to set
     */
    public void setDocenteroles(List<ClaseDocenteRol> docenteroles) {
        this.docenteroles = docenteroles;
    }

}
