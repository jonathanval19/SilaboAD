/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.menulateral.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.menulateral.comunes.Unidad;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jorge Zaruma
 */
public class UnidadAD extends Unidad {

    void agregarUnidad(int numeroUnidad, AccesoDatos ad, Integer idSilabo, String rolActivo) throws SQLException {
        this.setDescripcion("Unidad " + numeroUnidad);
        this.setIdUnidad(numeroUnidad);
        String SQL = "select ds.descripcion,ds.codigo,ds.id_descripcion, us.id_unidad, us.estado \n"
                + "                from t_descripcion_subseccion as ds join\n"
                + "                t_unidad_subsecciones as us \n"
                + "                on ds.id_descripcion = us.id_descripcion  and us.id_unidad=?\n"
                + "                where (us.id_silabo =?)\n"
                + "                order by ds.id_descripcion asc";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, numeroUnidad);
            ps.setInt(2, idSilabo);
            ResultSet rsSubsecciones = ps.executeQuery();
            while (rsSubsecciones.next()) {
                SeccionSilaboAD seccionsilabo = new SeccionSilaboAD();
                seccionsilabo.asignarSubsecciones(rsSubsecciones, rolActivo);
                this.getSubsecciones().add(seccionsilabo);
            }
            ps.close();
            ad.getCon().commit();
            long countCorregir = this.getSubsecciones().stream()
                    .filter(t -> t.getEstado().equals("fa fa-exclamation-triangle tag-warning"))
                    .count();
            long countCorregido = this.getSubsecciones().stream()
                    .filter(t -> t.getEstado().equals("fa fa-question"))
                    .count();
            long countAprobado = this.getSubsecciones().stream()
                    .filter(t -> t.getEstado().equals("fa fa-check"))
                    .count();
            if (countAprobado == 7) {
                this.setEstado("fa fa-check");
            } else if (countCorregido > 0 && !rolActivo.equals("Doc")) {
                this.setEstado("fa fa-question");
            } else if (countCorregir > 0 && rolActivo.equals("Doc")) {
                this.setEstado("fa fa-exclamation-triangle tag-warning");
            } else {
                this.setEstado("Inicio");
            }
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

}
