/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.opciones.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.login.ad.LoginAD;
import dda.silabo.opciones.comunes.Opciones2;
import ec.edu.espoch.academico.Periodo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class Opciones2AD extends Opciones2 {

    public void getOpcionesSilabo(AccesoDatos ad, int idRol) throws SQLException {
        try {
            String SQL = "select op.id_opcion,op.descripcion,op.estado,tro.id_rol\n"
                    + "               from  t_opciones as op left join\n"
                    + "             (select * from t_roles_opciones where id_rol=?) as tro\n"
                    + "on op.id_opcion=tro.id_opcion";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idRol);
            ResultSet rsOpciones = ps.executeQuery();
            while (rsOpciones.next()) {
                OpcionAD opcionad = new OpcionAD();
                opcionad.getOpcionRol(rsOpciones);
                this.getOpciones().add(opcionad);

            }  //ejcutar sql 
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("Opciones2AD").log(Level.SEVERE, "dda.silabo.opciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        this.setAyuda("Agregar opciones disponibles en el sistema");
        this.setTitulo("Opciones del Sistema");
    }

    public void saveOpcionesSilabo(AccesoDatos ad) throws SQLException {
        for (int i = 0; i < this.getOpciones().size(); i++) {
            try {
                String SQL2 = "";
                PreparedStatement ps = null;
                if (this.getOpciones().get(i).getIdOpcion() == 0) {
                    SQL2 = "insert into t_opciones (descripcion,estado) values(?,?)";
                    ps = ad.getCon().prepareStatement(SQL2);
                    ps.setString(1, this.getOpciones().get(i).getDescRol());
                    ps.setString(2, "H");

                } else {
                    SQL2 = "UPDATE t_opciones SET descripcion=? WHERE (id_opcion=?)";
                    ps = ad.getCon().prepareStatement(SQL2);
                    ps.setString(1, this.getOpciones().get(i).getDescRol());
                    ps.setInt(2, this.getOpciones().get(i).getIdOpcion());
                }

                ps.executeUpdate();
                ad.getCon().commit();
                ps.close();

            } catch (SQLException e) {
                ad.getCon().rollback();
                ad.Desconectar();
                Logger.getLogger("OpcionAD").log(Level.SEVERE, "dda.silabo.opciones.ad", e.getClass().getName() + "*****" + e.getMessage());
                System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            }
        }
    }

    public void ReporteOpcionesUsuario(AccesoDatos ad, LoginAD loginAD, Periodo periodo) throws SQLException {
        try {
            String SQL = "select * from t_tipo_entidad\n"
                    + "where id_tipo_entidad>=(select distinct on(tte.id_tipo_entidad)tte.id_tipo_entidad from t_tipo_entidad as tte\n"
                    + "join t_entidad as te on te.id_tipo_entidad = tte.id_tipo_entidad\n"
                    + "join t_usuario_entidad as tue \n"
                    + "on tue.id_entidad = te.id_entidad\n"
                    + "where (tue.id_rol=? and tue.id_usuario=\n"
                    + "(select id_docente from t_docente where cedula=?)))";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, loginAD.getIdRolActivo());
            ps.setString(2, loginAD.getCedula());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OpcionAD opcionAD = new OpcionAD();
                opcionAD.ReporteOpcionesUsuario(rs);
                this.getOpciones().add(opcionAD);

            }

            ad.getCon().commit();
            ps.close();

        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
            Logger.getLogger("OpcionAD").log(Level.SEVERE, "dda.silabo.opciones.ad", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

}
