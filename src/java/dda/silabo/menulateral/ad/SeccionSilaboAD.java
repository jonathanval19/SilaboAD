/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.menulateral.ad;

import dda.silabo.menulateral.comunes.SeccionSilabo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class SeccionSilaboAD extends SeccionSilabo {

    public void asignarSecciones(ResultSet rsSecciones) {
        try {
            this.setTipoSeccion(rsSecciones.getString("descripcion"));
            this.setEstado(rsSecciones.getString("estado"));
        } catch (SQLException e) {
            Logger.getLogger("SeccionSilaboAD").log(java.util.logging.Level.SEVERE, "dda.silabo.menulateral.comunes", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void asignarSubsecciones(ResultSet rsSubsecciones, String rolActivo) {
        try {
            this.setTipoSeccion(rsSubsecciones.getString("codigo"));
            this.setEstado(rsSubsecciones.getString("estado"));
            if (this.getEstado().equals("Aprobado")) {
                this.setEstado("fa fa-check");
            } else if (this.getEstado().equals("Corregido") && !rolActivo.equals("Doc")) {
                this.setEstado("fa fa-question");
            } else if (this.getEstado().equals("Corregir") && rolActivo.equals("Doc")) {
                this.setEstado("fa fa-exclamation-triangle tag-warning");
            }
            this.setDescripcion(rsSubsecciones.getString("descripcion"));
            this.setIdUnidad(rsSubsecciones.getInt("id_unidad"));
            this.setIdDescripcion(rsSubsecciones.getInt("id_descripcion"));

        } catch (SQLException e) {
            Logger.getLogger("SeccionSilaboAD").log(java.util.logging.Level.SEVERE, "dda.silabo.menulateral.comunes", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void asignarSeccionesAsignatura(ResultSet rsSecciones) {
        try {
            this.setTipoSeccion(rsSecciones.getString("descripcion"));
            this.setEstado(null);
        } catch (SQLException e) {
            Logger.getLogger("SeccionSilaboAD").log(java.util.logging.Level.SEVERE, "dda.silabo.menulateral.comunes", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void asignarSubseccionesAsignatura(ResultSet rsSubsecciones) {
        try {
            this.setTipoSeccion(rsSubsecciones.getString("descripcion"));
            this.setEstado(null);
            this.setIdUnidad(0);
        } catch (SQLException e) {
            Logger.getLogger("SeccionSilaboAD").log(java.util.logging.Level.SEVERE, "dda.silabo.menulateral.comunes", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
