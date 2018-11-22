package dda.silabo.archivos.ad;

import dda.silabo.archivos.comunes.ArchivoPublicacion;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Peter
 */
public class ArchivoPublicacionAD extends ArchivoPublicacion {

    //Sentencia para registrar un archivo de la publicación
    public String getSQLToInsert(Integer idSilabo, String fecha) {
        String result = "";
        result = "update t_silabo_estados set archivo=?, estado='Aprobado', fecha='" + fecha + "'"
                + "where id_silabo='" + idSilabo + "'";
        return result;
    }

}
