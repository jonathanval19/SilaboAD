/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.archivos.ln;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dda.silabo.ad.SilaboAD;
import dda.silabo.archivos.ad.ArchivoPublicacionAD;
import dda.silabo.db.AccesoDatos;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 *
 * @author Peter
 */
public class ArchivoPublicacionLN {

    private AccesoDatos ad;
    private ArchivoPublicacionAD apAD;
    private Gson gson;
    private String strSQL;

    public ArchivoPublicacionLN() {
        ad = new AccesoDatos();
        apAD = new ArchivoPublicacionAD();
        gson = new Gson();
        strSQL = "";
    }

    public Integer subirArchivos(String objApJSON, String jsonSilabo) throws SQLException {
        Integer result = -1;
        try {
            SilaboAD silaboAD = gson.fromJson(jsonSilabo, SilaboAD.class);
            apAD = gson.fromJson(objApJSON, ArchivoPublicacionAD.class);
            java.util.Date myDate = new java.util.Date();
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(myDate);
            strSQL = apAD.getSQLToInsert(silaboAD.getIdSilabo(), fecha);
            if (ad.Connectar() != 0) {
                PreparedStatement consulta = ad.getCon().prepareStatement(strSQL);
                consulta.setBytes(1, apAD.getBtyArchivo());
                result = consulta.executeUpdate();
                ad.getCon().commit();
            }
        } catch (JsonSyntaxException | SQLException e) {
            ad.getCon().rollback();
            Logger log = Logger.getLogger(this.getClass().getName());
            log.severe(e.getMessage());
        } finally {
            ad.Desconectar();
        }
        return (result);
    }
}
