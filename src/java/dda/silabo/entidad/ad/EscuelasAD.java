/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.entidad.ad;

import com.google.gson.Gson;
import dda.silabo.clases.unidos.EscuelaUnidos;
import dda.silabo.db.AccesoDatos;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.reportes.ad.EntidadAD;
import ec.edu.espoch.academico.EscuelaEntidad;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EscuelasAD extends EscuelaUnidos {

    public void asignarescuela(AccesoDatos ad, EscuelaEntidad escuela) {
        try {
            this.setCodEscuela(escuela.getCodEscuela());
            this.setNombre(escuela.getEscuela());
        } catch (Exception e) {
        }
    }

    public Boolean asignarescuela2(AccesoDatos ad, EscuelaEntidad escuelaEntidad, List<EscuelaUnidos> escuelas) {
        Boolean r = false;
        try {
            int cE = 0;
            for (EscuelaUnidos escuela : escuelas) {
                if (escuela.getCodEscuela().equals(escuelaEntidad.getCodEscuela())) {
                    cE++;
                }
            }
            if (cE == 0) {
                this.setCodEscuela(escuelaEntidad.getCodEscuela());
                this.setNombre(escuelaEntidad.getEscuela());
                r = true;
            }
        } catch (Exception e) {
        }
        return r;
    }

    public void agregarEscuelaCriteriosSilabo(EscuelaUnidos escuelaComun, AccesoDatos ad, LoginAD loginAD, String codPeriodo, EntidadAD entidadAD) {//reporte criterios de silabo
        this.setCodEscuela(escuelaComun.getCodEscuela());
        this.setNombre(escuelaComun.getNombre());
        escuelaComun.getCarreras().forEach((carreraComun) -> {
            CarrerasAD carrerasAD = new CarrerasAD();
            try {
                carrerasAD.agregarCarreraCriteriosSilabo(carreraComun, ad, loginAD, codPeriodo, entidadAD);
            } catch (SQLException ex) {
                Logger.getLogger(EscuelasAD.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!carrerasAD.getCriteriosSilabo().isEmpty()) {
                this.getCarreras().add(carrerasAD);
            }
        });
    }

}
