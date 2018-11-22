/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.login.ln;

import dda.silabo.db.AccesoDatos;
import ec.edu.espoch.academico.Periodo;

/**
 *
 * @author Jorge Zaruma
 */
public class PeriodoLN {

    AccesoDatos ad = new AccesoDatos();

    public Periodo getPeriodoValido() {
        Periodo result = periodoActual();

        return result;
    }

    private static Periodo periodoActual() {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.periodoActual();
    }

 

}
