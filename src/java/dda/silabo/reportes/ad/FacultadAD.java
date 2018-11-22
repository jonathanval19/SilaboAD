/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.reportes.ad;

import dda.silabo.clases.unidos.FacultadUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.EscuelasAD;
import dda.silabo.login.ad.LoginAD;
import java.util.List;

/**
 *
 * @author Evelyn
 */
public class FacultadAD extends FacultadUnidos {

    void agregarFacultadCriteriosSilabo(FacultadUnidos facultadComun, AccesoDatos ad, LoginAD loginAD, String codPeriodo, EntidadAD entidadAD) {//reporte criterios de silabo
        this.setCodFacultad(facultadComun.getCodFacultad());
        this.setNombre(facultadComun.getNombre());
        facultadComun.getEscuelas().forEach((escuelaComun) -> {
            EscuelasAD escuelasAD = new EscuelasAD();
            escuelasAD.agregarEscuelaCriteriosSilabo(escuelaComun, ad, loginAD, codPeriodo, entidadAD);
            if (!escuelasAD.getCarreras().isEmpty()) {
                this.getEscuelas().add(escuelasAD);
            }
        });
    }

}
