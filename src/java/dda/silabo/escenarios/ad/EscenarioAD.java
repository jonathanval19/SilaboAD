/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.escenarios.ad;

import dda.silabo.escenarios.comunes.Escenario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class EscenarioAD extends Escenario {

    public void EscenariosCargar(ResultSet rsEscenario, String rol) {
        try {
//            if (!rol.equals("Coordinador")) {
            this.setIdEsc(rsEscenario.getInt("id_escenarios"));
            this.setDescripcion(rsEscenario.getString("descripcion"));
            this.setTipoE(rsEscenario.getString("tipo"));
            this.setEstado(rsEscenario.getString("estado"));
            Integer idEscenario = rsEscenario.getInt("id_escenario");
            if (idEscenario != 0) {
                this.setCheck("checked");
            }
//            } else {
//                for (Escenario escenario : escenariosSeleccionados) {
//                    if (escenario.getIdEsc() == rsEscenario.getInt("id_escenarios")) {
//                        this.setCheck("checked");
//                        this.setIdEsc(rsEscenario.getInt("id_escenarios"));
//                        this.setDescripcion(rsEscenario.getString("descripcion"));
//                        this.setTipoE(rsEscenario.getString("tipo"));
//                    }
//                }
//            }

        } catch (SQLException e) {
            Logger.getLogger("EscenarioAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void asignarEscenarioSeleccionada(ResultSet rsEscenarios) {
        try {
            this.setIdEsc(rsEscenarios.getInt("id_escenario"));
        } catch (SQLException e) {
            Logger.getLogger("EscenarioAD").log(Level.SEVERE, "dda.silabos.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
