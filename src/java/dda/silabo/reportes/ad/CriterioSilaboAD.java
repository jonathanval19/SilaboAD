/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.reportes.ad;

import dda.silabo.reportes.comunes.CriterioSilabo;

/**
 *
 * @author Jorge Zaruma
 */
public class CriterioSilaboAD extends CriterioSilabo {

    public void getcriterioSilabo(int idCriterio, String descripcion, String tipo, String enlace) {
        this.setTipo(tipo);
        descripcion = descripcion.replaceAll("%20", " ").replaceAll("%0A", "\n");
        this.setDescripcion(descripcion);
        this.setIdCriterio(idCriterio);
        this.setEnlace(enlace);
    }
}
