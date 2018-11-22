/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.importacion.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.CarrerasAD;
import dda.silabo.importacion.comunes.ImportarSilabo;
import dda.silabo.login.ln.PeriodoLN;
import ec.edu.espoch.academico.Periodo;

/**
 *
 * @author Jorge Zaruma
 */
public class ImportarSilaboAD extends ImportarSilabo {

    public void asignarAsignaturasPeriodos(String codCarrrera, String login, AccesoDatos ad) {
        PeriodoLN periodoLN = new PeriodoLN();
        Periodo periodo = periodoLN.getPeriodoValido();
//        List<Materia> materiasd = getMateriasDocente(codCarrrera, login, periodo.getCodigo()).getMateria();
//        if (materiasd.size() > 0) {
        try {
            CarrerasAD carreraad = new CarrerasAD();
            carreraad.asignarAsignaturasPeriodos(codCarrrera, login, ad, periodo.getCodigo());
            if (!carreraad.getAsignaturas().isEmpty()) {
                this.getCarreras().add(carreraad);
//            }
            }
        } catch (Exception e) {
        }
    }

}
