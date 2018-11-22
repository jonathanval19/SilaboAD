/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.observaciones.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.observaciones.comunes.FechaObservacion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jorge Zaruma
 */
public class FechaObservacionAD extends FechaObservacion {

    void agregarObservacionesSubseccion(String SQL, AccesoDatos ad, String fecha) throws SQLException {
        this.setFecha(fecha);
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsObservacion = ps.executeQuery();
            while (rsObservacion.next()) {
                ObservacionAD observacion = new ObservacionAD();
                observacion.getObservacionSubseccion(rsObservacion);
                this.getObservaciones().add(observacion);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    void agregarObservacionesSeccion(String SQL, AccesoDatos ad, String fecha) throws SQLException {
        this.setFecha(fecha);
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ResultSet rsObservacion = ps.executeQuery();
            while (rsObservacion.next()) {
                ObservacionAD observacion = new ObservacionAD();
                observacion.getObservacionSeccion(rsObservacion);
                this.getObservaciones().add(observacion);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

}
